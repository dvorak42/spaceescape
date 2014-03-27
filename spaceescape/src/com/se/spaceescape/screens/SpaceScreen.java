package com.se.spaceescape.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;
import com.se.spaceescape.Entity;
import com.se.spaceescape.SpaceEscapeGame;
import com.se.spaceescape.SpaceGestureListener;

public class SpaceScreen implements Screen {
	SpaceEscapeGame game;

	OrthographicCamera camera;

	Texture spaceshipTexture;
	
	public Entity spaceship;
	
	
	
	public SpaceScreen(SpaceEscapeGame g) {
		game = g;
		
		float w = Gdx.graphics.getWidth();
		float h = Gdx.graphics.getHeight();
		camera = new OrthographicCamera(w, h);

		spaceshipTexture = new Texture(Gdx.files.internal("art/spaceship.png"));
		spaceshipTexture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		
		Sprite spaceshipSprite = new Sprite(spaceshipTexture);
		spaceship = new Entity(g, spaceshipSprite);
		spaceship.setSize(new Vector2(0.8f * w, 0.8f * h));
		spaceship.setPosition(new Vector2(0, 0));

		Gdx.input.setInputProcessor(new GestureDetector(new SpaceGestureListener(this)));
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		game.batch.setProjectionMatrix(camera.combined);

		game.batch.begin();
		game.font.draw(game.batch, "Hello World!", 0, 0);
		game.batch.end();

		game.batch.begin();
		spaceship.render();
		game.batch.end();
	}

	@Override
	public void resize(int width, int height) {
		camera.viewportHeight = height;
		camera.viewportWidth = width;
		camera.update();
	}

	@Override
	public void show() {
		// TODO Auto-generated method stub

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
		spaceshipTexture.dispose();
	}

}
