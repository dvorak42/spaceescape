package com.se.spaceescape;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.se.spaceescape.screens.PauseScreen;
import com.se.spaceescape.screens.SpaceScreen;

public class SpaceEscapeGame extends Game {
	public PauseScreen pauseScreen;
	public SpaceScreen gameScreen;
	
    public SpriteBatch batch;
    public SpriteBatch hudBatch;
    public SpriteBatch menuBatch;

    public SpriteBatch backgroundBatch;
    public BitmapFont font;
		
	@Override
	public void create() {
		Texture.setEnforcePotImages(false);
		hudBatch = new SpriteBatch();
		batch = new SpriteBatch();
		backgroundBatch = new SpriteBatch();
		menuBatch = new SpriteBatch();
		font = new BitmapFont();
		
		gameScreen = new SpaceScreen(this);
		pauseScreen = new PauseScreen(this, gameScreen);
		setScreen(gameScreen);
	}
	
	@Override
	public void render() {
		super.render();
	}
	
	@Override
	public void dispose() {
		batch.dispose();
		hudBatch.dispose();
		backgroundBatch.dispose();
		font.dispose();
	}
}
