package com.se.spaceescape;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.Texture.TextureWrap;
import com.badlogic.gdx.graphics.g2d.Sprite;

public class Constants {
	public static int ATTACK_MODE = 2; //1 = Hover, 2 = Lock
	public static final float ATTACK_START_DIST = 500;
	public static final float ATTACK_DIST = 150;
	public static final float ATTACK_DELAY = 30.0f;
	public static final float ATTACK_PROB = 0.3f;
	public static final int ATTACK_SIZE = 3;
	public static final float STEAL_DELAY = 6.0f;
	public static final float STEAL_PROB = 0.3f;
	public static final float DESTROY_PROB = 0.03f;

	public static final float DEFAULT_ZOOM = 0.5f;
	public static final float MAX_ZOOM = 4.0f;
	
	public static final int RESOURCE_SANITY   = 1;
	public static final int RESOURCE_WEAPONS = 2;
	public static final int RESOURCE_FOOD = 3;
	public static final int RESOURCE_OXYGEN = 4;
	public static final int NUM_RESOURCES = 4;
	
	public static final Color FOOD_COLOR = new Color(233/255.f, 202/255.f, 116/255.f, 1.f);
	public static final Color WEAPON_COLOR = new Color(136/255.f, 213/255.f, 211/255.f, 1.f);
	public static final Color HEART_COLOR = new Color(227/255.f, 144/255.f, 123/255.f, 1.f);
	
	public static final Color HIGHLIGHT_COLOR = new Color(233/255.f, 202/255.f, 116/255.f, 1.f);
	
	public static final int[] RESOURCE_TYPES = {RESOURCE_FOOD, RESOURCE_WEAPONS, RESOURCE_SANITY}; // Treat OXYGEN as a separate resource.
	public static final Color[] RESOURCE_COLORS = {Color.BLACK, FOOD_COLOR, WEAPON_COLOR, HEART_COLOR, Color.GREEN};
	public static final int[] TOTAL_RESOURCE = {0, 7, 7, 7, 200};
	public static final Texture[] RESOURCE_GENERATOR_TEXTURES = new Texture[NUM_RESOURCES + 1];
	public static final Texture[] RESOURCE_ICONS = new Texture[NUM_RESOURCES + 1];
	
	
	public static final ArrayList<ArrayList<Texture>> RESOURCE_IMGS = new ArrayList<ArrayList<Texture>>(NUM_RESOURCES);
	public static final ArrayList<ArrayList<String>> RESOURCE_NAMES = new ArrayList<ArrayList<String>>(NUM_RESOURCES);
	public static final float GENERATOR_DELAY = 10.0f;
	public static final Texture SPACESHIP_TEXTURE = new Texture(Gdx.files.internal("art/spaceshuttle.png"));
	public static final Texture SPACE_TEXTURE = new Texture(Gdx.files.internal("art/space.png"));
	public static final Sprite PLUS_SPRITE = new Sprite(new Texture(Gdx.files.internal("art/plus.png")));
	public static final Texture BUTTON_TEXTURE = new Texture(Gdx.files.internal("art/button.png"));
	
	static {
		for(int i = 0; i < NUM_RESOURCES + 1; i++) {
			RESOURCE_IMGS.add(new ArrayList<Texture>());
			RESOURCE_NAMES.add(new ArrayList<String>());
		}
		RESOURCE_GENERATOR_TEXTURES[RESOURCE_FOOD] = new Texture(Gdx.files.internal("art/chef.png"));
		RESOURCE_ICONS[RESOURCE_FOOD] = new Texture(Gdx.files.internal("art/food.png"));
		for(int i = 1; i < 7; i++) {
			RESOURCE_IMGS.get(RESOURCE_FOOD).add(new Texture(Gdx.files.internal("art/food" + i + ".png")));
			RESOURCE_NAMES.get(RESOURCE_FOOD).add("food" + i);
		}

		RESOURCE_GENERATOR_TEXTURES[RESOURCE_OXYGEN] = new Texture(Gdx.files.internal("art/chef.png"));
		RESOURCE_ICONS[RESOURCE_OXYGEN] = new Texture(Gdx.files.internal("art/oxygen.png"));
		for(int i = 1; i < 7; i++) {
			RESOURCE_IMGS.get(RESOURCE_OXYGEN).add(new Texture(Gdx.files.internal("art/air" + i + ".png")));
			RESOURCE_NAMES.get(RESOURCE_OXYGEN).add("air" + i);
		}

		RESOURCE_GENERATOR_TEXTURES[RESOURCE_SANITY] = new Texture(Gdx.files.internal("art/gf.png"));
		RESOURCE_ICONS[RESOURCE_SANITY] = new Texture(Gdx.files.internal("art/sentiment.png"));
		for(int i = 1; i < 7; i++) {
			RESOURCE_IMGS.get(RESOURCE_SANITY).add(new Texture(Gdx.files.internal("art/love" + i + ".png")));
			RESOURCE_NAMES.get(RESOURCE_SANITY).add("love" + i);
		}

		RESOURCE_GENERATOR_TEXTURES[RESOURCE_WEAPONS] = new Texture(Gdx.files.internal("art/gunner.png"));
		RESOURCE_ICONS[RESOURCE_WEAPONS] = new Texture(Gdx.files.internal("art/weapons.png"));
		for(int i = 1; i < 7; i++) {
			RESOURCE_IMGS.get(RESOURCE_WEAPONS).add(new Texture(Gdx.files.internal("art/weapon" + i + ".png")));
			RESOURCE_NAMES.get(RESOURCE_WEAPONS).add("weapon" + i);
		}

		for(ArrayList<Texture> tl : RESOURCE_IMGS) {
			for(Texture t : tl) {
				t.setFilter(TextureFilter.Linear, TextureFilter.Linear);
			}
		}
		
		SPACESHIP_TEXTURE.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		SPACE_TEXTURE.setWrap(TextureWrap.Repeat, TextureWrap.Repeat);
	}
}
