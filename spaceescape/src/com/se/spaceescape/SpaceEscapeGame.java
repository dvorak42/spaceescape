package com.se.spaceescape;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.se.spaceescape.screens.LoseScreen;
import com.se.spaceescape.screens.PauseScreen;
import com.se.spaceescape.screens.SpaceScreen;
import com.se.spaceescape.screens.SplashScreen;
import com.se.spaceescape.screens.WinScreen;

public class SpaceEscapeGame extends Game {
	public PauseScreen pauseScreen;
	public SpaceScreen gameScreen;
	
    public SpriteBatch batch;
    public SpriteBatch hudBatch;
    public SpriteBatch menuBatch;

    public SpriteBatch backgroundBatch;
    public BitmapFont smallFont;
    public BitmapFont bigFont;
		
	@Override
	public void create() {
		Texture.setEnforcePotImages(false);
		hudBatch = new SpriteBatch();
		batch = new SpriteBatch();
		backgroundBatch = new SpriteBatch();
		menuBatch = new SpriteBatch();
		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/mizufalp.ttf"));
		smallFont = generator.generateFont(24);
		bigFont = generator.generateFont(42);
		generator.dispose();
		
		gameScreen = new SpaceScreen(this);
		pauseScreen = new PauseScreen(this, gameScreen);
		setScreen(new SplashScreen(this, gameScreen));
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
		smallFont.dispose();
		bigFont.dispose();
	}
}
