package com.se.spaceescape.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.se.spaceescape.Constants;
import com.se.spaceescape.SpaceEscapeGame;

public class SplashScreen implements Screen {
	SpaceEscapeGame game;
	SpaceScreen next;
	OrthographicCamera camera;
	Texture background;
	float fadeDelay = -1;
	
	public SplashScreen(SpaceEscapeGame g, SpaceScreen next) {
		game = g;
		this.next = next;
	}

	@Override
	public void render(float delta) {
		if(fadeDelay > 0)
			fadeDelay -= delta;
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		float d = fadeDelay / Constants.FADE_DELAY;
		if(d < 0)
			d = 1;
		game.gameScreen.oC = new Color(1, 1, 1, 1-d);

		next.paused = true;
		next.render(0);
		next.paused = false;
		game.gameScreen.oC = Color.WHITE;
		
		camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		game.menuBatch.setProjectionMatrix(camera.combined);
		game.menuBatch.begin();
		game.menuBatch.setColor(Color.WHITE.cpy().mul(d));
		game.menuBatch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		game.menuBatch.end();
		
		if(Gdx.input.isTouched() && fadeDelay == -1)
			fadeDelay = Constants.FADE_DELAY;
		if(fadeDelay < 0 && fadeDelay != -1)
			game.setScreen(next);
	}

	@Override
	public void resize(int width, int height) {
		next.resize(width, height);
		camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
	}

	@Override
	public void show() {
		next.show();
		background = new Texture(Gdx.files.internal("art/splash.png"));
		camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub

	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub

	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

}
