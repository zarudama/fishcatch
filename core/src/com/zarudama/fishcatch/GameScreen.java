package com.zarudama.fishcatch;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureWrap;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class GameScreen extends MyScreenAdapter {
    public static final String LOG_TAG = GameScreen.class.getSimpleName();

    // 16:9
    public static int LOGICAL_WIDTH = 256;
    public static int LOGICAL_HEIGHT = 144;

    // 1秒間に16x3ドット進む
    private static final float MOVE_SPEED = 16*3.0f/60;

    // 移動状態
    enum NekoState {
        IDLE,
        MOVE,
    }

    // 向き状態
    enum NekoDir {
        RIGHT,
        LEFT,
    }

    // ゲームの状態
    enum GameState {
        PLAY,
        PAUSE,
        GAMEOVER,
    }

    // ゲームに使用するテクスチャ
    private Texture img;

    private SpriteBatch batch;

    // BGM
    private Music music;

    // 魚取得音
    private Sound seGet;
    private Sound seMiss;

    // 画面フォント用
    private BitmapFont font;

    // ゲームカメラとビュポート
    private OrthographicCamera camera;
    private Viewport viewport;

    // UIカメラとビュポート
    private OrthographicCamera uiCamera;
    private Viewport uiViewport;

    // タッチ座標変換用ウケザラ
    private Vector3 touchPoint;

    // 左右のボタン
    private Sprite leftButton;
    private Sprite rightButton;

    // 背景用
    private Texture bgImg;
    private Sprite bg;
    private Sprite ground;

    // デバッグ用ワールド座標軸
    private ShapeRenderer shapeRenderer;
    private boolean isDebug;

    // ねこアニメーション
    private Animation animLeft;
    private Animation animRight;
    private Animation animIdleLeft;
    private Animation animIdleRight;
    private float animTime = 0;

    // ねこ座標
    private Vector2 pos;
    private Rectangle nekoBounds;

    // ねこ状態
    private NekoState nekoState;
    private NekoDir nekoDir;

    private Vector2 fishpos;
    private Rectangle fishBounds;
	private Animation fish;

    // UVスクロール用
    private float scrollCounter;

    private static final int MISS_COUNT_MAX = 3;
    private int missCount;
    private int score;
    private GameState gameSate;

    private Sprite pauseButton;
    private Sprite okButton;
    private Sprite quitButton;
    private Sprite gameButton;
    private Sprite panel;
    private Sprite gameover;
    private Sprite pause;

    public GameScreen(FishcatchGame game) {
        super(game);
        touchPoint = new Vector3();
        batch = new SpriteBatch();
        font = new BitmapFont();
        img = new Texture(Gdx.files.internal("neko.png"));
        pos = new Vector2();

        music = Gdx.audio.newMusic(Gdx.files.internal("mixdown.mp3"));
        music.setLooping(true);
        music.setVolume(0.3f);
        music.play();

        seGet = Gdx.audio.newSound(Gdx.files.internal("get.wav"));
        seMiss = Gdx.audio.newSound(Gdx.files.internal("miss.wav"));

        // 背景
        bgImg = new Texture("bg.png");
        bgImg.setWrap(TextureWrap.Repeat, TextureWrap.Repeat);
        bg = new Sprite(bgImg, 0, 0, LOGICAL_WIDTH, LOGICAL_HEIGHT);
        bg.setU(0);
        bg.setU2(5);
        bg.setV(0);
        bg.setV2(5);
        scrollCounter = 0.0f;

        // 地面
        ground = new Sprite(img, 0, 0, LOGICAL_WIDTH, 16*2);
        ground.setU(16*6/256.0f);
        ground.setV(0);
        ground.setU2(16*7/256.0f);
        ground.setV2(16*2/256.0f);

        camera = new OrthographicCamera();
        camera.position.x = 0;
        camera.position.y = LOGICAL_HEIGHT/2 - 16*2;
        viewport = new FitViewport(LOGICAL_WIDTH, LOGICAL_HEIGHT, camera);

        uiCamera = new OrthographicCamera();
        uiCamera.setToOrtho(false, LOGICAL_WIDTH, LOGICAL_HEIGHT);
        uiViewport = new FitViewport(LOGICAL_WIDTH, LOGICAL_HEIGHT, uiCamera);

        shapeRenderer = new ShapeRenderer();

        // アニメーション情報構築
        TextureRegion[] split = new TextureRegion(img).split(16, 16)[0];
        TextureRegion[] mirror = new TextureRegion(img).split(16, 16)[0];
        for (TextureRegion region : mirror)
            region.flip(true, false);
        animRight = new Animation(0.1f, split[2], split[3], split[4]);
        animLeft = new Animation(0.1f,  mirror[2], mirror[3], mirror[4]);
        animIdleRight = new Animation(0.5f, split[0], split[1]);
        animIdleLeft = new Animation(0.5f, mirror[0], mirror[1]);
        animTime = 0;

        // 移動ボタン
        leftButton = new Sprite(img, 0, 16*2, 16*3, 16*2);
        rightButton = new Sprite(img, 16*3, 16*2, 16*3, 16*2);
        leftButton.setPosition(8, 0);
        rightButton.setPosition(8 + 16*3, 0);

        // 魚
        split = new TextureRegion(img).split(16, 16)[1];
        fishpos = new Vector2();
        fishBounds = new Rectangle(0, 0, 16, 16);
		fish = new Animation(0.5f, split[0], split[1]);
        nekoBounds = new Rectangle(0, 0, 16, 16);

        // パネル
        panel = new Sprite(img, 0, 16*6, 16*10, 16*4);
        panel.setPosition((LOGICAL_WIDTH-16*10)/2, (LOGICAL_HEIGHT-16*4)/2);

        // gameover文字
        gameover = new Sprite(img, 0, 16*11, 16*8, 16*1);
        gameover.setPosition(panel.getX() + (16*10 - 16*8)/2,
                             panel.getY() + 40);

        // okボタン
        okButton = new Sprite(img, 0, 16*10, 16*3, 16*1);
        okButton.setPosition(panel.getX() + (16*10 - 16*3)/2,
                             panel.getY() + 8);

        // pause文字
        pause = new Sprite(img, 0, 16*12, 16*5, 16*1);
        pause.setPosition(panel.getX() + (16*10 - 16*5)/2,
                          panel.getY() + 40);

        // gameボタン
        gameButton = new Sprite(img, 16*3, 16*10, 16*3, 16*1);
        gameButton.setPosition(panel.getX() + 16*1,
                               panel.getY() + 8);
        // quitボタン
        quitButton = new Sprite(img, 16*3+16*3, 16*10, 16*3, 16*1);
        quitButton.setPosition(panel.getX() + (16*1 + 16*3 + 16*2),
                               panel.getY() + 8);


        // pauseボタン
        pauseButton = new Sprite(img, 16*9, 16*10, 16, 16);
        pauseButton.setPosition(LOGICAL_WIDTH-16-4, LOGICAL_HEIGHT-16-4);

        // ログ情報取得
        Gdx.app.setLogLevel(Application.LOG_DEBUG);

        reset();
    }

    @Override
    public void resize(int width, int height) {
        Gdx.app.log(LOG_TAG, "resize");
        viewport.update(width, height);
        uiViewport.update(width, height);
    }

    private void reset() {
        pos.set(0, 0);
        nekoBounds.x = pos.x;
        nekoBounds.y = pos.y;
        resetFish();
        missCount = MISS_COUNT_MAX;
        gameSate = GameState.PLAY;
        score = 0;
    }

    private void resetFish() {
        fishpos.y = LOGICAL_HEIGHT;
        fishpos.x = MathUtils.random(-LOGICAL_WIDTH/2+16, LOGICAL_WIDTH/2-16);
        fishBounds.x = fishpos.x;
        fishBounds.y = fishpos.y;
    }

    private void left() {
        nekoDir = NekoDir.LEFT;
        nekoState = NekoState.MOVE;
        pos.x -= MOVE_SPEED;
    }

    private void right() {
        nekoDir = NekoDir.RIGHT;
        nekoState = NekoState.MOVE;
        pos.x += MOVE_SPEED;
    }

    private Animation currentAnim() {
        Animation anim = null;
        if (nekoState == NekoState.MOVE) {
            if (nekoDir == NekoDir.LEFT) {
                anim = animLeft;
            } else {
                anim = animRight;
            }
        } else {
            if (nekoDir == NekoDir.LEFT) {
                anim = animIdleLeft;
            } else {
                anim = animIdleRight;
            }
        }
        return anim;
    }

    private void gamePlay(float deltaTime) {
        if (Gdx.input.isKeyPressed(Input.Keys.R)) {
            reset();
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            isDebug = !isDebug;
        }
        nekoState = NekoState.IDLE;
        leftButton.setColor(Color.WHITE);
        rightButton.setColor(Color.WHITE);
        if (Gdx.input.isTouched()) {
            float x = Gdx.input.getX();
            float y = Gdx.input.getY();
            uiViewport.unproject(touchPoint.set(x, y, 0));
            Rectangle leftBounds = leftButton.getBoundingRectangle();
            Rectangle rightBounds = rightButton.getBoundingRectangle();
            Rectangle pauseBounds = pauseButton.getBoundingRectangle();
            if (leftBounds.contains(touchPoint.x, touchPoint.y)) {
                left();
                leftButton.setColor(Color.GRAY);
            } else if (rightBounds.contains(touchPoint.x, touchPoint.y)) {
                right();
                rightButton.setColor(Color.GRAY);
            } else if (pauseBounds.contains(touchPoint.x, touchPoint.y)) {
                gameSate = GameState.PAUSE;
			}
        }

        animTime += deltaTime;

        if (pos.x < -LOGICAL_WIDTH/2-16)
            pos.x = LOGICAL_WIDTH/2;
        else if (pos.x > LOGICAL_WIDTH/2)
            pos.x = -LOGICAL_WIDTH/2-16;

        nekoBounds.x = pos.x;
        nekoBounds.y = pos.y;
        if (fishpos.y < 0) {
           resetFish();
           missCount -= 1;
           seMiss.play();
           if (missCount <= 0) {
               gameSate = GameState.GAMEOVER;
               if (score > game.hiScore) {
                   game.hiScore = score;
               }
           }
        }
        fishpos.y -= MOVE_SPEED;
        fishBounds.y = fishpos.y;

        if (nekoBounds.overlaps(fishBounds)) {
            resetFish();
            seGet.play();
            score += 1;
        }

        // コメントを外すと、UVスクロールが見れる。
        bg.setU(scrollCounter);
        //bg.setV(scrollCounter);
        bg.setU2(scrollCounter + 5.0f);
        //bg.setV2(scrollCounter + 5.0f);
        
        // 酔うようなスクロールエフェクト
        // bg.setU(scrollCounter);
        // bg.setV(scrollCounter + 5.0f);

        scrollCounter += 0.01f;
        if (scrollCounter > 5.0f)
            scrollCounter = 0.0f;
    }

    private void drawNumeric(final float x,
                             final float y,
                             final int aNum) {
        float u,v,u2,v2;
        v = 16*5/256.0f;
        v2 = 16*6/256.0f;
        float xx = x;
        int num = aNum;
        for (;;) {
            int digit = num % 10;
            u = 16*digit/256.0f;
            u2 = 16*(digit+1)/256.0f;
            batch.draw(img,
                       xx, y,
                       16,16,
                       u, v2,  // テクスチャ座標系の左下
                       u2,v);  // テクスチャ座標系の右上
            num /= 10;
            if (num == 0) break;
            xx -= 16;
        }
    }

    private void gamePause(float deltaTime) {
        if (Gdx.input.isTouched()) {
            float x = Gdx.input.getX();
            float y = Gdx.input.getY();
            uiViewport.unproject(touchPoint.set(x, y, 0));
            Rectangle gameBounds = gameButton.getBoundingRectangle();
            Rectangle quitBounds = quitButton.getBoundingRectangle();
            if (gameBounds.contains(touchPoint.x, touchPoint.y)) {
                gameSate = GameState.PLAY;
            } else if (quitBounds.contains(touchPoint.x, touchPoint.y)) {
                music.stop();
			 	game.setScreen(new MainMenuScreen(game));
                Gdx.app.log(LOG_TAG, "game quit!");
			}
        }
    }

    private void gameOver(float deltaTime) {
        if (Gdx.input.isTouched()) {
            float x = Gdx.input.getX();
            float y = Gdx.input.getY();
            uiViewport.unproject(touchPoint.set(x, y, 0));
            Rectangle okBounds = okButton.getBoundingRectangle();
            if (okBounds.contains(touchPoint.x, touchPoint.y)) {
                reset();
            }
        }
    }

    private void update(float deltaTime) {
        if (gameSate == GameState.PLAY) {
            gamePlay(deltaTime);
        } else if (gameSate == GameState.PAUSE) {
            gamePause(deltaTime);
        } else {
            gameOver(deltaTime);
        }
    }

    private void drawPause() {
        panel.draw(batch);
        pause.draw(batch);
        gameButton.draw(batch);
        quitButton.draw(batch);
    }

    private void drawGameover() {
        panel.draw(batch);
        gameover.draw(batch);
        okButton.draw(batch);
    }

    private void draw() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // BGカメラセットアップ
        uiCamera.update();
        batch.setProjectionMatrix(uiCamera.combined);
        batch.begin();
        bg.draw(batch);
        ground.draw(batch);
        batch.end();

        // ゲームカメラセットアップ
        camera.update();
        batch.setProjectionMatrix(camera.combined);

        batch.begin();

        // ネコの描画
        boolean loop = true;
        float width = 16;
        float height = 16;
        Animation nekoAnim = currentAnim();
        batch.draw(nekoAnim.getKeyFrame(animTime, loop),
                   pos.x, pos.y,
                   width, height);
        // 魚の描画
		batch.draw(fish.getKeyFrame(animTime, loop),
                   fishpos.x, fishpos.y,
                   width, height);

        batch.end();

        // ワールド座標軸を描画する。
        if (isDebug) {
            shapeRenderer.setProjectionMatrix(camera.combined);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            shapeRenderer.setColor(1, 0, 0, 1);
            shapeRenderer.line(-1024, 0, 1024, 0);
            shapeRenderer.setColor(0, 1, 0, 1);
            shapeRenderer.line(0, -1024, 0, 1024);
            shapeRenderer.end();
        }

        // UIカメラセットアップ
        uiCamera.update();
        batch.setProjectionMatrix(uiCamera.combined);

        // UIの描画
        String info = String.format("pos(%f,%f)", pos.x, pos.y);
        batch.begin();
        leftButton.draw(batch);
        rightButton.draw(batch);
        pauseButton.draw(batch);
        drawNumeric(LOGICAL_WIDTH/2, LOGICAL_HEIGHT-16, score);

        batch.draw(img,
                   0, LOGICAL_HEIGHT-16,
                   16,16,
                   16*2/256.0f, 16*2/256.0f,  // テクスチャ座標系の左下
                   16*3/256.0f, 16*1/256.0f);  // テクスチャ座標系の右上
        drawNumeric(16, LOGICAL_HEIGHT-16, missCount);

        if (gameSate == GameState.PAUSE) {
            drawPause();
        } else if (gameSate == GameState.GAMEOVER) {
            drawGameover();
        }

        batch.end();
    }

    @Override
    public void render (float deltaTime) {
        //Gdx.app.log(LOG_TAG, "render()");
        update(deltaTime);
        draw();
    }

    @Override
    public void hide() {
        Gdx.app.log(LOG_TAG, "hide");
        dispose();
    }

    @Override
    public void dispose() {
        Gdx.app.log(LOG_TAG, "GameScreen#dispose()");
        music.dispose();
        seGet.dispose();
        seMiss.dispose();
        batch.dispose();
        font.dispose();
        img.dispose();
        bgImg.dispose();
        shapeRenderer.dispose();
    }
}
