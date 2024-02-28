package com.radius.system;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.ScreenUtils;
import com.radius.system.configs.GameConfig;
import com.radius.system.events.listeners.ExitGameListener;
import com.radius.system.events.listeners.StartGameListener;
import com.radius.system.screens.ConfigScreen;
import com.radius.system.screens.GameScreen;

public class ProjectBoom extends Game implements StartGameListener, ExitGameListener {

	private ConfigScreen configScreen;

	private GameScreen gameScreen;
	
	@Override
	public void create () {
		// Music bgm = Gdx.audio.newMusic(Gdx.files.internal("")); bgm.play(); // Can be set to loop, played right away.
		// Sound sfx = Gdx.audio.newSound(Gdx.files.internal("")); // Played on trigger.

		configScreen = new ConfigScreen();
		gameScreen = new GameScreen();

		configScreen.AddStartGameListener(this);
		configScreen.AddStartGameListener(gameScreen);

		gameScreen.AddExitGameListener(this);
		gameScreen.AddExitGameListener(configScreen);

		setScreen(configScreen);
	}

	@Override
	public void OnGameStart(GameConfig gameConfig) {
		setScreen(gameScreen);
	}

	@Override
	public void OnExitGame() {
		setScreen(configScreen);
	}
}
