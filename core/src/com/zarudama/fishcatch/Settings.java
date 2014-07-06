package com.zarudama.fishcatch;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
public class Settings {
    private static final String NAME = "com.zarudama.fishcatch";
    private static final String KEY_SE_ON = "seOn";
    private static final String KEY_BGM_ON = "bgmOn";
    private static final String KEY_HISCORE = "hiscore";
    private static final boolean DEFAULT_SE_ON = true;
    private static final boolean DEFAULT_BGM_ON = true;
    private static final int DEFAULT_HISCORE = 0;
    private Preferences mPrefs;
    public Settings() {
        mPrefs = Gdx.app.getPreferences(NAME);
    }
    public boolean seOn() {
        return mPrefs.getBoolean(KEY_SE_ON, DEFAULT_SE_ON);
    }
    public boolean bgmOn() {
        return mPrefs.getBoolean(KEY_BGM_ON, DEFAULT_BGM_ON);
    }
    public int hiscore() {
        return mPrefs.getInteger(KEY_HISCORE, DEFAULT_HISCORE);
    }
    public void toggleSeOn() {
        mPrefs.putBoolean(KEY_SE_ON, !seOn());
        mPrefs.flush();
    }
    public void toggleBgmOn() {
        mPrefs.putBoolean(KEY_BGM_ON, !bgmOn());
        mPrefs.flush();
    }
    public void hiscore(final int aScore) {
        mPrefs.putInteger(KEY_HISCORE, aScore);
        mPrefs.flush();
    }
}
