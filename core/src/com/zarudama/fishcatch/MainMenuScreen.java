package com.zarudama.fishcatch;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureWrap;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class MainMenuScreen extends MyScreenAdapter {
    private static final String LOG_TAG = MainMenuScreen.class.getSimpleName();
	OrthographicCamera uiCamera;
    Viewport viewport;
	SpriteBatch batch;
	Vector3 touchPoint;
    Texture img;
    Sprite start;
    Sprite title;
    float alpha;
    // 画面フォント用
    private BitmapFont font;

	public MainMenuScreen(FishcatchGame game) {
        super(game);
        Gdx.app.log(LOG_TAG, "constractor");
        img = new Texture(Gdx.files.internal("neko.png"));
        start = new Sprite(img, 0, 16*4, 16*5, 16);
        title = new Sprite(img, 0, 16*13, 16*14, 16*2);
		uiCamera = new OrthographicCamera();
		uiCamera.setToOrtho(false, FishcatchGame.LOGICAL_WIDTH, FishcatchGame.LOGICAL_HEIGHT);
        viewport = new FitViewport(FishcatchGame.LOGICAL_WIDTH, FishcatchGame.LOGICAL_HEIGHT, uiCamera);
		batch = new SpriteBatch();
		touchPoint = new Vector3();

        font = new BitmapFont();
        title.setPosition((FishcatchGame.LOGICAL_WIDTH - 16*14)/2,
                          FishcatchGame.LOGICAL_HEIGHT/2);

        start.setPosition((FishcatchGame.LOGICAL_WIDTH - 16*4)/2,
                          16*3);
        alpha = 0;
        Gdx.app.log(LOG_TAG, "constractor exit");
	}
	public void update(float delta) {
        alpha += 0.05f;
        start.setAlpha(0.5f - 0.3f * (float) Math.sin(alpha));
		if (Gdx.input.justTouched()) {
			viewport.unproject(touchPoint.set(Gdx.input.getX(), Gdx.input.getY(), 0));
            Rectangle playBounds = start.getBoundingRectangle();
			if (playBounds.contains(touchPoint.x, touchPoint.y)) {
			 	game.setScreen(new GameScreen(game));
                Gdx.app.log(LOG_TAG, "game start!");
			 	return;
			}
		}
	}
	public void draw () {
		GL20 gl = Gdx.gl;
		gl.glClearColor(0, 0, 0, 1);
		gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		uiCamera.update();
		batch.setProjectionMatrix(uiCamera.combined);
		batch.begin();
        title.draw(batch);
        start.draw(batch);
        font.draw(batch, "HiScore:" + game.hiScore, 10,10);
		batch.end();
	}

    @Override
    public void resize(int width, int height) {
        Gdx.app.log(LOG_TAG, "resize");
        viewport.update(width, height);
    }

	@Override
	public void render(float delta) {
		update(delta);
		draw();
	}

    @Override
    public void hide() {
        Gdx.app.log(LOG_TAG, "hide");
        dispose();
    }

    @Override
    public void dispose() {
        Gdx.app.log(LOG_TAG, "dispose");
        img.dispose();
        font.dispose();
        batch.dispose();
    }
}
