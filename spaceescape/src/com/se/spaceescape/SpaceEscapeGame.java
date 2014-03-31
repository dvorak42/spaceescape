package com.se.spaceescape;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.se.spaceescape.screens.PauseScreen;
import com.se.spaceescape.screens.SpaceScreen;

public class SpaceEscapeGame extends Game {
	Screen pauseScreen;
	Screen gameScreen;
	
    public SpriteBatch batch;
    public SpriteBatch hudBatch;
    public BitmapFont font;
		
	@Override
	public void create() {
		hudBatch = new SpriteBatch();
		batch = new SpriteBatch();
		font = new BitmapFont();
		
		pauseScreen = new PauseScreen(this);
		gameScreen = new SpaceScreen(this);
		setScreen(gameScreen);
	}
	
	@Override
	public void render() {
		super.render();
	}
	
	@Override
	public void dispose() {
		batch.dispose();
		font.dispose();
	}
}
