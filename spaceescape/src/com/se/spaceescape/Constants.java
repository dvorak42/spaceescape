package com.se.spaceescape;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;

public class Constants {

	public static final int RESOURCE_FOOD   = 1;
	// FIXME: @Steven,
	// I added these just to keep the pattern of RESOURCE_FOOD.
	// Do we want these in an enum or will leaving them like this be fine?
	// I have no problem leaving them like this. If you don't either, just
	// delete this comment.
	public static final int RESOURCE_OXYGEN = 2;
	public static final int RESOURCE_SANITY = 3;
	public static final int RESOURCE_POWER  = 4;
	// -- Carlo
	public static final int NUM_RESOURCES = 8;
	public static final Texture[] RESOURCE_IMGS = new Texture[NUM_RESOURCES];
	public static final int TOTAL_RESOURCE_FOOD   = 12;
	public static final int TOTAL_RESOURCE_OXYGEN = 12;
	public static final int TOTAL_RESOURCE_SANITY = 12;
	public static final int TOTAL_RESOURCE_POWER  = 12;
	
	static {
		RESOURCE_IMGS[RESOURCE_FOOD] = new Texture(Gdx.files.internal("art/bread.png"));
		RESOURCE_IMGS[RESOURCE_FOOD].setFilter(TextureFilter.Linear, TextureFilter.Linear);
		RESOURCE_IMGS[RESOURCE_OXYGEN] = new Texture(Gdx.files.internal("art/food3.png"));
		RESOURCE_IMGS[RESOURCE_OXYGEN].setFilter(TextureFilter.Linear, TextureFilter.Linear);
		RESOURCE_IMGS[RESOURCE_SANITY] = new Texture(Gdx.files.internal("art/food5.png"));
		RESOURCE_IMGS[RESOURCE_SANITY].setFilter(TextureFilter.Linear, TextureFilter.Linear);
		RESOURCE_IMGS[RESOURCE_POWER] = new Texture(Gdx.files.internal("art/food7.png"));
		RESOURCE_IMGS[RESOURCE_POWER].setFilter(TextureFilter.Linear, TextureFilter.Linear);
	}
}
