package com.se.spaceescape;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;

public class Constants {

	public static final int RESOURCE_FOOD = 1;
	public static final int NUM_RESOURCES = 8;
	public static final ArrayList<ArrayList<Texture>> RESOURCE_IMGS = new ArrayList<ArrayList<Texture>>(NUM_RESOURCES);
	public static final int TOTAL_RESOURCE_FOOD = 160;
	
	static {
		for(int i = 0; i < NUM_RESOURCES; i++)
			RESOURCE_IMGS.add(new ArrayList<Texture>());
		Texture t = new Texture(Gdx.files.internal("art/bread.png"));
		t.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		RESOURCE_IMGS.get(RESOURCE_FOOD).add(t);
	}
}
