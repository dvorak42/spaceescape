package com.se.spaceescape;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;

public class Constants {

	public static final int RESOURCE_FOOD = 1;
	public static final int NUM_RESOURCES = 8;
	public static final Texture[] RESOURCE_IMGS = new Texture[NUM_RESOURCES];
	public static final int TOTAL_RESOURCE_FOOD = 16;
	
	static {
		RESOURCE_IMGS[RESOURCE_FOOD] = new Texture(Gdx.files.internal("art/bread.png"));
		RESOURCE_IMGS[RESOURCE_FOOD].setFilter(TextureFilter.Linear, TextureFilter.Linear);
	}
}
