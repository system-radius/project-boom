package com.radius.system.screens.game_ui;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.radius.system.assets.GlobalAssets;
import com.radius.system.events.OverTimeListener;
import com.radius.system.events.TimerEventListener;

import java.util.ArrayList;
import java.util.List;

public class TimerDisplay extends Actor implements TimerEventListener {

    private static int tensMinutesValue, onesMinutesValue, tensSecondsValue, onesSecondsValue;

    private static final TextureRegion[][] SYMBOLS = GlobalAssets.LoadTextureRegion(GlobalAssets.SYMBOLS_TEXTURE_PATH, GlobalAssets.SYMBOLS_TEXTURE_REGION_SIZE, GlobalAssets.SYMBOLS_TEXTURE_REGION_SIZE);

    private final List<OverTimeListener> overtimeListeners = new ArrayList<>();

    private final TextureRegion colon;

    private TextureRegion tensMinutes, onesMinutes, tensSeconds, onesSeconds;

    private float totalTime;

    private boolean paused;

    public TimerDisplay(float x, float y, float width, float height) {
        this.colon = SYMBOLS[5][0];
        setX(x);
        setY(y);
        setWidth(width);
        setHeight(height);
        SetTime(299);
        PauseTimer();
    }

    private void DeriveTime(float time) {
        int minutes = (int)time / 60;
        int seconds = (int)time % 60;

        tensMinutesValue = minutes / 10;
        onesMinutesValue = minutes % 10;

        tensMinutes = SYMBOLS[0][tensMinutesValue];
        onesMinutes = SYMBOLS[0][onesMinutesValue];

        tensSecondsValue = seconds / 10;
        onesSecondsValue = seconds % 10;

        tensSeconds = SYMBOLS[0][tensSecondsValue];
        onesSeconds = SYMBOLS[0][onesSecondsValue];
    }

    public void SetTime(float time) {
        DeriveTime(time);
    }

    public void AddOverTimeListener(OverTimeListener listener) {
        if (overtimeListeners.contains(listener)) return;
        overtimeListeners.add(listener);
    }

    public void FireOverTimeEvent() {
        for (OverTimeListener listener : overtimeListeners) {
            listener.OverTime();
        }
    }

    @Override
    public void act(float delta) {
        if (paused) {
            return;
        }

        totalTime -= delta;
        if (totalTime < 0) {
            FireOverTimeEvent();
            PauseTimer();
        }

        DeriveTime(totalTime);
    }

    @Override
    public void draw(Batch batch, float alpha) {
        DrawItem(batch, tensMinutes, 0);
        DrawItem(batch, onesMinutes, 1);
        DrawItem(batch, colon, 2);
        DrawItem(batch, tensSeconds, 3);
        DrawItem(batch, onesSeconds, 4);
    }

    private void DrawItem(Batch batch, TextureRegion texture, int index) {
        batch.draw(texture, getX() + (getWidth() * index) / 1.5f, getY(), getWidth(), getHeight());
    }

    @Override
    public void StartTimer(float totalTime) {
        this.totalTime = totalTime;
        SetTime(totalTime);
        ResumeTimer();
    }

    public void PauseTimer() {
        paused = true;
    }

    public void ResumeTimer() {
        paused = false;
    }

    public static void LogTimeStamped(String message) {
        System.out.println("[" + tensMinutesValue + onesMinutesValue + ":" + tensSecondsValue + onesSecondsValue + "] " + message);
    }
}
