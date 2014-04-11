package com.se.spaceescape;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;

public class Constants {

	public static final int RESOURCE_FOOD   = 1;
	public static final int RESOURCE_OXYGEN = 2;
	public static final int RESOURCE_SANITY = 3;
	public static final int RESOURCE_POWER  = 4;
	public static final int NUM_RESOURCES = 8;

	public static final ArrayList<ArrayList<Texture>> RESOURCE_IMGS = new ArrayList<ArrayList<Texture>>(NUM_RESOURCES);
	public static final ArrayList<ArrayList<String>> RESOURCE_NAMES = new ArrayList<ArrayList<String>>(NUM_RESOURCES);
	public static final int TOTAL_RESOURCE_FOOD   = 12;
	public static final int TOTAL_RESOURCE_OXYGEN = 12;
	public static final int TOTAL_RESOURCE_SANITY = 12;
	public static final int TOTAL_RESOURCE_POWER  = 12;
	
	static {
		for(int i = 0; i < NUM_RESOURCES; i++) {
			RESOURCE_IMGS.add(new ArrayList<Texture>());
			RESOURCE_NAMES.add(new ArrayList<String>());
		}
		RESOURCE_IMGS.get(RESOURCE_FOOD).add(new Texture(Gdx.files.internal("art/bread.png")));
		RESOURCE_NAMES.get(RESOURCE_FOOD).add("bread");
		RESOURCE_IMGS.get(RESOURCE_OXYGEN).add(new Texture(Gdx.files.internal("art/food3.png")));
		RESOURCE_NAMES.get(RESOURCE_OXYGEN).add("food3");
		RESOURCE_IMGS.get(RESOURCE_SANITY).add(new Texture(Gdx.files.internal("art/food5.png")));
		RESOURCE_NAMES.get(RESOURCE_SANITY).add("food5");
		RESOURCE_IMGS.get(RESOURCE_POWER).add(new Texture(Gdx.files.internal("art/food7.png")));
		RESOURCE_NAMES.get(RESOURCE_POWER).add("food7");
		
		for(ArrayList<Texture> tl : RESOURCE_IMGS) {
			for(Texture t : tl) {
				t.setFilter(TextureFilter.Linear, TextureFilter.Linear);
			}
		}
	}
}
