package com.se.spaceescape.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.se.spaceescape.SpaceEscapeGame;

public class WinScreen implements Screen {
	SpaceEscapeGame game;
	SpaceScreen parent;
	
	public WinScreen(SpaceEscapeGame g, SpaceScreen parent) {
		game = g;
		this.parent = parent;
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		parent.paused = true;
		parent.render(0);
		parent.paused = false;
		
		game.menuBatch.begin();
		game.font.setColor(Color.BLACK);
		game.font.draw(game.menuBatch, "Congragulations you win!", 10, 60);
		game.font.draw(game.menuBatch, "Press SPACE to quit.", 10, 30);
		game.menuBatch.end();
		
		if(Gdx.input.isKeyPressed(Input.Keys.SPACE))
			Gdx.app.exit();
	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub

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
		// TODO Auto-generated method stub

	}

}
