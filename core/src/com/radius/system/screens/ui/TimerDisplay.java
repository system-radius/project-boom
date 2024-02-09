package com.radius.system.screens.ui;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.radius.system.events.OverTimeListener;
import com.radius.system.events.TimerEventListener;

import java.util.ArrayList;
import java.util.List;

public class TimerDisplay extends Actor implements TimerEventListener {

    public static final TextureRegion[][] SYMBOLS = HeadsUpDisplayItem.SYMBOLS;

    private final List<OverTimeListener> overtimeListeners = new ArrayList<>();

    private TextureRegion tensMinutes, onesMinutes, colon, tensSeconds, onesSeconds;

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

        int tensMinutesValue = minutes / 10;
        int onesMinutesValue = minutes % 10;

        tensMinutes = SYMBOLS[0][tensMinutesValue];
        onesMinutes = SYMBOLS[0][onesMinutesValue];

        int tensSecondsValue = seconds / 10;
        int onesSecondsValue = seconds % 10;

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

    @Override
    public void PauseTimer() {
        paused = true;
    }

    @Override
    public void ResumeTimer() {
        paused = false;
    }
}
