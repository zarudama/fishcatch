package com.zarudama.fishcatch;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.FPSLogger;
import java.util.Set;
import java.util.HashSet;

public class LibGdxSample extends Game {
    public static final String LOG_TAG = LibGdxSample.class.getSimpleName();
    // 4:3
    public static int LOGICAL_WIDTH = 256;
    public static int LOGICAL_HEIGHT = 192;
    private Screen nextScreen;

    @Override
    public void create() {
        Gdx.app.log(LOG_TAG, "create");
        setScreen(new MainMenuScreen(this));
    }
    @Override
    public void render() {
        super.render();
        if (nextScreen != null) {
            super.setScreen(nextScreen);
            nextScreen = null;
        }
    }

    @Override
    public void setScreen (Screen screen) {
        Gdx.app.log(LOG_TAG, "setScreen");
        nextScreen = screen;
    }

    @Override
    public void dispose() {
        super.dispose();
        Gdx.app.log(LOG_TAG, "dispose");
    }
}
