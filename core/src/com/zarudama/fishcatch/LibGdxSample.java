package com.zarudama.fishcatch;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.FPSLogger;

public class LibGdxSample extends Game {
    public static final String LOG_TAG = LibGdxSample.class.getSimpleName();
    // 4:3
    public static int LOGICAL_WIDTH = 256;
    public static int LOGICAL_HEIGHT = 192;
	@Override
	public void create() {
        Gdx.app.log(LOG_TAG, "create");
		setScreen(new MainMenuScreen(this));
	}
	@Override
	public void render() {
		super.render();
	}
	@Override
	public void dispose() {
        Gdx.app.log(LOG_TAG, "dispose");
		super.dispose();
		getScreen().dispose();
	}
}
