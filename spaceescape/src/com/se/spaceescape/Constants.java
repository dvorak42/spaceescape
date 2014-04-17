package com.se.spaceescape;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.Texture.TextureWrap;

public class Constants {

	public static final int RESOURCE_FOOD   = 1;
	public static final int RESOURCE_OXYGEN = 2;
	public static final int RESOURCE_SANITY = 3;
	public static final int RESOURCE_WEAPONS  = 4;
	public static final int NUM_RESOURCES = 4;
	
	public static final int[] RESOURCE_TYPES = {RESOURCE_FOOD, RESOURCE_OXYGEN, RESOURCE_SANITY, RESOURCE_WEAPONS};
	public static final Color[] RESOURCE_COLORS = {Color.BLACK, Color.GREEN, Color.BLUE, Color.PINK, Color.RED};
	public static final int[] TOTAL_RESOURCE = {0, 12, 12, 12, 12};
	public static final Texture[] RESOURCE_GENERATOR_TEXTURES = new Texture[NUM_RESOURCES + 1];
	
	public static final ArrayList<ArrayList<Texture>> RESOURCE_IMGS = new ArrayList<ArrayList<Texture>>(NUM_RESOURCES);
	public static final ArrayList<ArrayList<String>> RESOURCE_NAMES = new ArrayList<ArrayList<String>>(NUM_RESOURCES);
	public static final float GENERATOR_DELAY = 10.0f;
	public static final Texture SPACESHIP_TEXTURE = new Texture(Gdx.files.internal("art/spaceshuttle.png"));
	public static final Texture SPACE_TEXTURE = new Texture(Gdx.files.internal("art/space.png"));
	static {
		for(int i = 0; i < NUM_RESOURCES + 1; i++) {
			RESOURCE_IMGS.add(new ArrayList<Texture>());
			RESOURCE_NAMES.add(new ArrayList<String>());
		}
		RESOURCE_IMGS.get(RESOURCE_FOOD).add(new Texture(Gdx.files.internal("art/bread.png")));
		RESOURCE_NAMES.get(RESOURCE_FOOD).add("bread");
		RESOURCE_GENERATOR_TEXTURES[RESOURCE_FOOD] = new Texture(Gdx.files.internal("art/food_icon.png"));
		RESOURCE_IMGS.get(RESOURCE_OXYGEN).add(new Texture(Gdx.files.internal("art/food3.png")));
		RESOURCE_NAMES.get(RESOURCE_OXYGEN).add("food3");
		RESOURCE_GENERATOR_TEXTURES[RESOURCE_OXYGEN] = new Texture(Gdx.files.internal("art/food_icon.png"));
		RESOURCE_IMGS.get(RESOURCE_SANITY).add(new Texture(Gdx.files.internal("art/food5.png")));
		RESOURCE_NAMES.get(RESOURCE_SANITY).add("food5");
		RESOURCE_GENERATOR_TEXTURES[RESOURCE_SANITY] = new Texture(Gdx.files.internal("art/food_icon.png"));
		RESOURCE_IMGS.get(RESOURCE_WEAPONS).add(new Texture(Gdx.files.internal("art/food7.png")));
		RESOURCE_NAMES.get(RESOURCE_WEAPONS).add("food7");
		RESOURCE_GENERATOR_TEXTURES[RESOURCE_WEAPONS] = new Texture(Gdx.files.internal("art/food_icon.png"));
		
		for(ArrayList<Texture> tl : RESOURCE_IMGS) {
			for(Texture t : tl) {
				t.setFilter(TextureFilter.Linear, TextureFilter.Linear);
			}
		}
		
		SPACESHIP_TEXTURE.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		SPACE_TEXTURE.setWrap(TextureWrap.Repeat, TextureWrap.Repeat);
	}
}
