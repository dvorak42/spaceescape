package com.se.spaceescape;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;

public class Constants {

	public static final int RESOURCE_FOOD = 1;
	public static final int NUM_RESOURCES = 8;
	public static final Texture[] RESOURCE_IMGS = new Texture[NUM_RESOURCES];
	
	static {
		RESOURCE_IMGS[RESOURCE_FOOD] = new Texture(Gdx.files.internal("art/food.png"));
		RESOURCE_IMGS[RESOURCE_FOOD].setFilter(TextureFilter.Linear, TextureFilter.Linear);
	}
}
