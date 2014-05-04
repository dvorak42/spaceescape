package com.se.spaceescape.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.se.spaceescape.AlertEntity;
import com.se.spaceescape.AlienShip;
import com.se.spaceescape.Constants;
import com.se.spaceescape.Entity;
import com.se.spaceescape.PhysicalEntity;
import com.se.spaceescape.Planet;
import com.se.spaceescape.ResourceGenerator;
import com.se.spaceescape.ResourceItem;
import com.se.spaceescape.SpaceContactListener;
import com.se.spaceescape.SpaceEscapeGame;
import com.se.spaceescape.SpaceGestureListener;
import com.se.spaceescape.Spaceship;
import com.se.spaceescape.Utils;

public class SpaceScreen implements Screen {
	public SpaceEscapeGame game;
	public Color oC = Color.WHITE;

	public Color tint(Color c) {
		return oC.cpy().mul(c);
	}
	
	public OrthographicCamera camera;
	
	public Spaceship spaceship;
	
	public boolean paused = false;
	
	World world;
	Box2DDebugRenderer debugRenderer;
	
	float totalTime;
	
	public int selectedResource = Constants.RESOURCE_FOOD;
	public float maximumOxygenSteps = Constants.TOTAL_RESOURCE[Constants.RESOURCE_OXYGEN];
	
	float worldTime = 0;
	float MAX_TIME_LIMIT = 60f; // LEVEL TIME IN SECONDS
	float stepTime = MAX_TIME_LIMIT / maximumOxygenSteps;

	public Array<Array<ResourceItem>> resources;
	public float oxygenRemaining;
	public Array<Entity> entities;
	public Array<AlertEntity> hovering;
	public Array<ResourceItem> tossedResources;
	public Array<Planet> planets;
	public Array<Vector2> clouds;
	public Array<AlienShip> enemies;
	
	public Array<PhysicalEntity> toDestroy;
	
	// TEMP VARIABLES FOR CHOOSING UI
	// TODO: REMOVE THIS AND CHOOSE
	private boolean SEGMENTED_UI = true;
	private boolean SHADOWED = true;
	private boolean RANDOM_LEVEL = false;
	
	ShapeRenderer sr;
	
	public Array<ResourceGenerator> generators;
	
	float timeToAttack;
	
	public int stealingResource = -1;
	
	public Sprite zoomButton;
	
	// Sounds
	public Sound suctionAudio = Gdx.audio.newSound(Gdx.files.internal("music/suction.wav"));
	public Sound popAudio = Gdx.audio.newSound(Gdx.files.internal("music/pop.mp3"));
	public Sound itemGetAudio = Gdx.audio.newSound(Gdx.files.internal("music/itemget.wav"));
	public Sound explosionAudio = Gdx.audio.newSound(Gdx.files.internal("music/explosion.mp3"));
	
	// Music
	public Music bgmusicAudio = Gdx.audio.newMusic(Gdx.files.internal("music/bgmusic.mp3"));
	
	public SpaceScreen(SpaceEscapeGame g) {
		game = g;
		
		world = new World(new Vector2(0, 0), true);
		debugRenderer = new Box2DDebugRenderer();
		
		world.setContactListener(new SpaceContactListener());
	}
	
	public void runPhysics(float delta) {
		if(!paused) {
			Vector2 shipPos = spaceship.body.getPosition();
			for (ResourceItem ri : tossedResources) {
				// The camera size / 3 seemed to work on desktop and tablet builds
				// as a good indicator of "off the screen" but still giving some
				// time before removing.
				if (Math.abs(ri.body.getPosition().x - shipPos.x)*3 > camera.viewportWidth ||
				    Math.abs(ri.body.getPosition().y - shipPos.y)*3 > camera.viewportHeight) {
					toDestroy.add(ri);
				}
			}
			while(toDestroy.size > 0) {
				PhysicalEntity ri = toDestroy.pop();
				world.destroyBody(ri.body);
				ri.body.setUserData(null);
				ri.body = null;
				entities.removeValue(ri, true);
				if(ri instanceof ResourceItem) {
					tossedResources.removeValue((ResourceItem)ri, true);
				}
				if(ri instanceof AlienShip) {
					enemies.removeValue((AlienShip) ri, true);
				}
				for(Planet p : planets) {
					p.getOrbitters().removeValue(ri, true);
				}
			}


			for(Planet p : planets) {
				p.runOrbit();
			}
		
			Vector2 v = spaceship.body.getLinearVelocity().cpy();
			spaceship.body.applyForce(v.scl(-0.05f * v.len2()), spaceship.body.getWorldCenter(), true);

			float a1 = (MathUtils.radDeg * spaceship.getRotation() + 360) % 360;
			float a2 = (spaceship.targetAngle + 360) % 360;
			float da = a2 - a1;
			if(da > 180) da -= 360;
			if(da < -180) da += 360;
			if(da > 10 && spaceship.body.getAngularVelocity() < 0.1)
				spaceship.rotate(10);
			else if(da < -10 && spaceship.body.getAngularVelocity() > -0.1)
				spaceship.rotate(-10);

			world.step(1/60f, 6, 2);
			worldTime += 1/60f;
		    if (worldTime >= stepTime) {
		    	oxygenRemaining--;
		        worldTime -= stepTime;
		    }
		
			if(oxygenRemaining <= 0)
				game.setScreen(new LoseScreen(game, this));
		}
	}
	
	public void attackPlayer() {
		if(enemies.size > 0)
			return;
		float angle = MathUtils.random(360);

		for(int i = 0; i < Constants.ATTACK_SIZE; i++) {
			AlienShip a = new AlienShip(game, this, new Sprite(Constants.SPACESHIP_TEXTURE), new Vector2(0, Constants.ATTACK_DIST).rotate(angle + 120 * i));
			a.setSize(new Vector2(64, 64));
			a.initBody(world, spaceship.getPosition().cpy().add(new Vector2(0, Constants.ATTACK_START_DIST).rotate(angle + 120 * i)));
			entities.add(a);
			enemies.add(a);
		}
	}

	@Override
	public void render(float delta) {
		game.batch.setColor(oC);
		game.hudBatch.setColor(oC);
		
		totalTime += delta;
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		camera.position.set(spaceship.body.getWorldCenter(), 0);
		camera.update();
		game.backgroundBatch.setProjectionMatrix(camera.combined);
		game.hudBatch.begin();
		float x = -spaceship.getPosition().x;
		float y = -spaceship.getPosition().y;
		while(x < -Gdx.graphics.getWidth())
			x += Gdx.graphics.getWidth();
		while(x > 0)
			x -= Gdx.graphics.getWidth();
		while(y < -Gdx.graphics.getHeight())
			y += Gdx.graphics.getHeight();
		while(y > 0)
			y -= Gdx.graphics.getHeight();
		game.hudBatch.draw(Constants.SPACE_TEXTURE, x, y, 2 * Gdx.graphics.getWidth(), 2 * Gdx.graphics.getHeight(),
				0, 0, 4 * Constants.SPACE_TEXTURE.getWidth(), 4 * Constants.SPACE_TEXTURE.getHeight(), false, false);
		game.hudBatch.end();
		
		if(Gdx.input.isKeyPressed(Input.Keys.NUM_1))
			Constants.ATTACK_MODE = 1;
		else if(Gdx.input.isKeyPressed(Input.Keys.NUM_2))
			Constants.ATTACK_MODE = 2;
		runPhysics(delta);
		
		int midX = Gdx.graphics.getWidth() / 2;
		int midY = Gdx.graphics.getHeight() / 2;
		float scl = Constants.DEFAULT_ZOOM / camera.zoom;

		Gdx.gl.glEnable(GL20.GL_BLEND);
	    Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		sr = new ShapeRenderer();
		sr.begin(ShapeType.Line);
		Gdx.gl.glLineWidth(20 * scl);
		sr.setColor(tint(Color.valueOf("00853A")));
		sr.circle(midX, midY, 125 * scl, 100);
		if(camera.zoom > Constants.DEFAULT_ZOOM) {
			Gdx.gl.glLineWidth(80 * scl);
			sr.setColor(tint(Color.GREEN));
			sr.circle(midX,  midY, 200 * scl, 100);
		}
		sr.end();
		
		// Grab the two closest planets.
		float[] closestPlanets  = {99999999f, 99999999f};
		int[] closestPlanetsIdx = new int[2];
		float planetDistance;
		for (int i = 0; i < planets.size; i++) {
			planetDistance = planets.get(i).body.getWorldCenter().dst2(spaceship.body.getWorldCenter());
			if (planetDistance < 700000 && planets.get(i).fname != "goldplanet") {
				if (planetDistance < closestPlanets[0]) {
					closestPlanets[1] = closestPlanets[0];
					closestPlanetsIdx[1] = closestPlanetsIdx[0];
					closestPlanets[0] = planetDistance;
					closestPlanetsIdx[0] = i;
				} else if (planetDistance < closestPlanets[1]) {
					closestPlanets[1] = planetDistance;
					closestPlanetsIdx[1] = i;
				}
			}
		}
		// Draw the triangles
		
		sr.begin(ShapeType.Filled);
		Gdx.gl.glLineWidth(1);
		sr.setColor(tint(Color.valueOf("FFD700")));
		int navCount = (int) Math.ceil(2 * ((float) resources.get(Constants.RESOURCE_SANITY).size / (float) Constants.TOTAL_RESOURCE[Constants.RESOURCE_SANITY]));
		if (navCount > closestPlanets.length)
			navCount = closestPlanets.length;
		for (int i = 0; i < navCount; i++) {
			if (closestPlanets[i] < 700000) {
				Vector2 direction = planets.get(closestPlanetsIdx[i])
						.body.getWorldCenter().sub(spaceship.body.getWorldCenter()).nor();
				Vector2 ang1 = direction.cpy().rotate(10);
				Vector2 ang2 = direction.cpy().rotate(-10);
				sr.triangle(midX + 150 * direction.x * scl,  midY + 150 * direction.y * scl,
						    midX + 120 * ang1.x * scl,       midY + 120 * ang1.y * scl,
						    midX + 120 * ang2.x * scl,       midY + 120 * ang2.y * scl);
			}
		}
		sr.setColor(tint(Color.valueOf("a8ff00")));
		Vector2 direction = planets.peek()
				.body.getWorldCenter().sub(spaceship.body.getWorldCenter()).nor();
		Vector2 ang1 = direction.cpy().rotate(10);
		Vector2 ang2 = direction.cpy().rotate(-10);			
		sr.triangle(midX + 170 * direction.x * scl,  midY + 170 * direction.y * scl,
				    midX + 120 * ang1.x * scl,       midY + 120 * ang1.y * scl,
				    midX + 120 * ang2.x * scl,       midY + 120 * ang2.y * scl);
		sr.end();
		Gdx.gl.glDisable(GL20.GL_BLEND);

		
		timeToAttack -= Gdx.graphics.getDeltaTime();
		if(timeToAttack < 0) {
			if(MathUtils.random() < Constants.ATTACK_PROB)
				attackPlayer();
			timeToAttack = Constants.ATTACK_DELAY;
		}
		
		game.batch.setProjectionMatrix(camera.combined);
		game.batch.begin();
		spaceship.render();
		for(Entity r : entities)
			r.render();
		for(Entity r : planets) {
			Planet p = (Planet)r;
			if(p.endPlanet && camera.zoom > Constants.DEFAULT_ZOOM)
				p.renderEnd();
			else
				r.render();
		}
		game.batch.end();
		
		sr = new ShapeRenderer();

		game.hudBatch.begin();
		zoomButton.setColor(oC);
		zoomButton.setPosition(Gdx.graphics.getWidth() - 150, 50);
		zoomButton.draw(game.hudBatch);
		game.hudBatch.end();
		
		if(Gdx.input.isKeyPressed(Input.Keys.P))
			game.setScreen(game.pauseScreen);

		if(camera.zoom != Constants.DEFAULT_ZOOM)
			return;
		
		Gdx.gl.glEnable(GL20.GL_BLEND);
	    Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		sr.begin(ShapeType.Filled);
		int initX = 75;
		int initY = 100;
		if (SEGMENTED_UI) {
			int offset = 0;
			for(int rType : Constants.RESOURCE_TYPES) {
				if(rType == selectedResource) {
					sr.setColor(tint(Color.valueOf("00FF0070")));
					sr.circle(initX, initY + offset, 70);
				}
				sr.setColor(tint(Color.BLACK));
//				switch(rType) {
//				case Constants.RESOURCE_FOOD:
//					sr.setColor(tint(Color.valueOf("7493e9")));
//					break;
//				case Constants.RESOURCE_SANITY:
//					sr.setColor(tint(Color.valueOf("7bcee3")));
//					break;
//				case Constants.RESOURCE_WEAPONS:
//					sr.setColor(tint(Color.valueOf("d5888a")));
//					break;
//				}
//				sr.circle(initX, initY + offset, 60);
				int total = Math.max(Constants.TOTAL_RESOURCE[rType], resources.get(rType).size);
				float arclength = 360 / total;
				for (int i = 0; i < total; i++) {
					if(i < resources.get(rType).size) {
						sr.setColor(tint(Constants.RESOURCE_COLORS[rType]));
					} else if(SHADOWED) {
						sr.setColor(tint(Color.GRAY));
					} else
						continue;
					
					sr.arc(initX,initY + offset, 58, 90 + (i * arclength), arclength - 5, 3);					
				}
				switch(rType) {
				case Constants.RESOURCE_FOOD:
					sr.setColor(tint(Color.valueOf("BD65CB")));
					break;
				case Constants.RESOURCE_SANITY:
					sr.setColor(tint(Color.valueOf("6DA65F")));
					break;
				case Constants.RESOURCE_WEAPONS:
					sr.setColor(tint(Color.valueOf("AE594E")));
					break;
				}
				sr.circle(initX, initY + offset, 40);
				offset += 150;
			}
			sr.setColor(tint(Color.DARK_GRAY));
			sr.rect(midX - 254, Gdx.graphics.getHeight() - 74, 508, 58);
			sr.setColor(tint(Color.valueOf("ac2b1b80")));
			float percentO2Remaining = ((float)oxygenRemaining / maximumOxygenSteps);
			if (percentO2Remaining*MAX_TIME_LIMIT < 15f && (int) (percentO2Remaining*200) % 2 == 0) {
				sr.rect(midX - 254, Gdx.graphics.getHeight() - 74, 508, 58);
			}
			sr.setColor(tint(Color.valueOf("ac2b1b")));
			sr.rect(midX - 250, Gdx.graphics.getHeight() - 70,
					500f * percentO2Remaining, 50);
		}
		sr.end();
		
		game.hudBatch.setProjectionMatrix(sr.getProjectionMatrix());
		game.hudBatch.begin();
		initX = 39;
		initY = 64;
		int offset = 0;
		for(int rType : Constants.RESOURCE_TYPES) {
			Sprite s = new Sprite(Constants.RESOURCE_ICONS[rType]);
			s.setPosition(initX, initY + offset);
			s.setSize(72, 72);
			s.setColor(oC);
			s.draw(game.hudBatch);
			offset += 150;
		}
		Sprite s = new Sprite(Constants.RESOURCE_ICONS[Constants.RESOURCE_OXYGEN]);
		s.setPosition(midX - 305 , Gdx.graphics.getHeight() - 68);
		s.setSize(48, 48);
		s.setColor(oC);
		s.draw(game.hudBatch);

		int yPos = 200;
		for(ResourceGenerator r : generators) {
			r.setSize(new Vector2(147, 147));
			r.setPosition(new Vector2(Gdx.graphics.getWidth() - r.getSize().x - 50, yPos));
			r.render();
			for(AlertEntity a : hovering) {
				if(a.type == r.type) {
					a.setPosition(new Vector2(r.getPosition().x + 16, r.getPosition().y + 32));
					a.setSize(new Vector2(64, 64));
					a.render();
				}
			}
			yPos += 160;
		}
		game.hudBatch.end();
		
		Gdx.gl.glEnable(GL20.GL_BLEND);
	    Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
	    sr.begin(ShapeType.Filled);
		initX = 75;
		initY = 100;
		offset = 0;
		for(int rType : Constants.RESOURCE_TYPES) {
			if(rType == stealingResource) {
				sr.setColor(tint(Color.valueOf("FF0000B0")));
				sr.circle(initX, initY + offset, 60);
			}
			offset += 150;
		}
		sr.end();
		
		if(enemies.size > 0) {
			game.hudBatch.begin();
			game.font.setScale(2);
			game.font.setColor(tint(Color.RED));
			game.font.getRegion().getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
			String str = "Throw things to defend!";
			AlienShip closestEnemy = null;
			Vector2 sp = spaceship.body.getWorldCenter();
			for(AlienShip e : enemies)
				if(closestEnemy == null || e.body.getWorldCenter().dst(sp) < closestEnemy.body.getWorldCenter().dst(sp))
					closestEnemy = e;
			if(closestEnemy.body.getWorldCenter().dst(sp) > 250)
				str = "Warning: Enemies incoming!";
			float strx = Gdx.graphics.getWidth() - game.font.getBounds(str).width;
			if(totalTime % 0.5 < 0.25)
				game.font.draw(game.hudBatch, str, strx / 2, 60);
			game.font.setScale(1);
			game.hudBatch.end();
		}
		//debugRenderer.render(world, camera.combined);
	}

	@Override
	public void resize(int width, int height) {
		camera.viewportHeight = height;
		camera.viewportWidth = width;
		camera.update();
		game.hudBatch.setProjectionMatrix(camera.combined);
	}

	@Override
	public void show() {
		float w = Gdx.graphics.getWidth();
		float h = Gdx.graphics.getHeight();
		
		camera = new OrthographicCamera(w, h);
		camera.zoom = Constants.DEFAULT_ZOOM;
		
		sr = new ShapeRenderer();
		
		Sprite spaceshipSprite = new Sprite(Constants.SPACESHIP_TEXTURE);
		spaceship = new Spaceship(game, this, spaceshipSprite);
		spaceship.setSize(new Vector2(80, 80));
		//Utils.createBounds(world, 1000, 1000);
		spaceship.initBody(world, new Vector2(0, 0));
		entities = new Array<Entity>();
		tossedResources = new Array<ResourceItem>();
		planets = new Array<Planet>();
		clouds  = new Array<Vector2>();
		toDestroy = new Array<PhysicalEntity>();
		hovering = new Array<AlertEntity>();
		resources = new Array<Array<ResourceItem>>();
		resources.add(null);

		for(int rType : Constants.RESOURCE_TYPES) {
			resources.add(new Array<ResourceItem>());
			for(int i = 0; i < Constants.TOTAL_RESOURCE[rType]; i++)
				resources.get(rType).add(Utils.createResource(game, rType));
		}
		oxygenRemaining = maximumOxygenSteps;
		
		if (RANDOM_LEVEL) {
			placePlanetsRandomly();
		} else {
			// (60 * Oxygen Time Limit) is usually larger than
			// the distance a player can get from flinging all
			// 36 items steadily in an attempt to get to the
			// home planet.
			int[] endplanet = {250, 3440};
			int[][] newPlanets = { { 400,   2500, 250, 1},
					               { 1667,  1400, 150, 2},
					               { -200, -1508, 150, 2},
					               {-1109,   600, 250, 1},
					               { 1480,  -237, 250, 1},
					               { 2480, -1237, 150, 2},
					               {-1980,  -837, 250, 1},
					               {-3200,  2000, 150, 2},
					               { 3200,  3400, 250, 1},
					               {-3200, -3400, 150, 2},
					               };
			int[][] newClouds  = { {-1724,   968, 100},
					               { 1001,   597, 100},
					               {  200, -3000, 100},
					               {-1600,  2400, 100},
					               {-2000, -1500, 100},
			                       };
			placePlanets(endplanet, newPlanets, newClouds);
		}

		zoomButton = new Sprite(new Texture(Gdx.files.internal("art/button.png")));
		zoomButton.setPosition(Gdx.graphics.getWidth() - 150, Gdx.graphics.getHeight() - 150);
		
		enemies = new Array<AlienShip>();
		
		generators = new Array<ResourceGenerator>();
		Sprite gSprite = new Sprite(Constants.RESOURCE_GENERATOR_TEXTURES[Constants.RESOURCE_SANITY]);
		ResourceGenerator sanityGenerator = new ResourceGenerator(game, gSprite, Constants.RESOURCE_SANITY);
		generators.add(sanityGenerator);

		gSprite = new Sprite(Constants.RESOURCE_GENERATOR_TEXTURES[Constants.RESOURCE_WEAPONS]);
		ResourceGenerator weaponsGenerator = new ResourceGenerator(game, gSprite, Constants.RESOURCE_WEAPONS);
		generators.add(weaponsGenerator);

		gSprite = new Sprite(Constants.RESOURCE_GENERATOR_TEXTURES[Constants.RESOURCE_FOOD]);
		ResourceGenerator foodGenerator = new ResourceGenerator(game, gSprite, Constants.RESOURCE_FOOD);
		generators.add(foodGenerator);
		
		timeToAttack = Constants.ATTACK_DELAY;
		
		Gdx.input.setInputProcessor(new GestureDetector(new SpaceGestureListener(this)));
		
		Gdx.gl.glEnable(GL10.GL_LINE_SMOOTH);
		Gdx.gl.glEnable(GL10.GL_POINT_SMOOTH);
		Gdx.gl.glHint(GL10.GL_POLYGON_SMOOTH_HINT, GL10.GL_NICEST);
		Gdx.gl.glHint(GL10.GL_POINT_SMOOTH_HINT, GL10.GL_NICEST);
		
		bgmusicAudio.play();
	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub

	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub

	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose() {
		suctionAudio.dispose();
		popAudio.dispose();
		itemGetAudio.dispose();
		explosionAudio.dispose();
		bgmusicAudio.dispose();
	}
	
	/*
	 * endPlanet  = location of end planet. [x, y]
	 * newPlanets = list of planets.        [x, y, size, filename]
	 * newClouds  = list of clouds.         [x, y, size]
	 */
	private void placePlanets(int[] endPlanet, int[][] newPlanets, int[][] newClouds) {
		Planet p = Utils.createPlanet(game, world, "goldplanet", 50,  new Vector2(endPlanet[0], endPlanet[1]));
		p.endPlanet = true;
		planets.add(p);
		
		for (int i = 0; i < newPlanets.length; i++) {
			p = Utils.createPlanet(game, world, "planet" + newPlanets[i][3], newPlanets[i][2], new Vector2(newPlanets[i][0], newPlanets[i][1]));
			for(int j = 0; j < 8; j++)
				p.addOrbitter(Utils.createResource(game, Constants.RESOURCE_FOOD));
			planets.add(p);
			entities.addAll(p.getOrbitters());
		}
		
		ResourceItem cloudFood = null;
		Vector2 cloudPos = null;
		for (int i = 0; i < newClouds.length; i++) {
			cloudPos = new Vector2(newClouds[i][0], newClouds[i][1]);
			for(int j = 0; j < 4; j++) {
				cloudFood = Utils.createResource(game, Constants.RESOURCE_OXYGEN);
				cloudFood.initBody(world, cloudPos.cpy().add(new Vector2(newClouds[i][2], 0).rotate(MathUtils.random(360f))));
				entities.add(cloudFood);
			}
			clouds.add(cloudPos);
		}
	}
	
	private void placePlanetsRandomly() {
		// Random map generation values for testing.
		// A bunch of EOL comments just for completeness. They can be removed.
		int MIN_DIST_GOAL = 1000; // Minimum distance from start -> goal.
		int MAX_DIST_GOAL = 3000; // Maximum distance from start -> goal.
		int MIN_START_DIST = 500; // Minimum distance from start to the closest planet.
		int MAX_START_DIST = 700; // Maximum distance from start to the closest planet.
		int MAX_PLANET_DIST = 2000; // Maximum distance from start to a planet.
		int MAX_CLOUD_DIST = 2500; // Maximum distance from start to a cloud.
		int MIN_PLANETS = 4; // The minimum number of planets.
		int MAX_PLANETS = 8; // The maximum number of planets.
		int MIN_OXYGEN_CLOUDS = 1; // The minimum number of oxygen clouds.
		int MAX_OXYGEN_CLOUDS = 4; // The maximum number of oxygen clouds.
		int MIN_SEPARATION_PLANETS = 250000; // The minimum squared distance between planets.
		int MIN_SEPARATION_CLOUDS = 500000; // The minimum distance between oxygen clouds.
		int MIN_PLANET_SIZE = 50;
		int MAX_PLANET_SIZE = 125;
		
		// Weird way of doing it. Randomization like this never feels right, but considering we don't
		// know exactly what types of layouts are enjoyable.
		// It just chooses random spots that fit the restrictions above. 
		int numberOfPlanets = 1;
		int totalPlanets = (int) (MIN_PLANETS + Math.random() * (MAX_PLANETS - MIN_PLANETS));
		Vector2[] planetLocations = new Vector2[MAX_PLANETS];
		int placementDist = (int) (MIN_START_DIST + Math.random() * (MAX_START_DIST - MIN_START_DIST));
		double placementAngle = 2*Math.PI*Math.random();
		Vector2 randomPos = new Vector2((int) (placementDist*Math.cos(placementAngle)), (int) (placementDist*Math.sin(placementAngle)));
		
		// Place the regular food planets.
		planetLocations[0] = randomPos;
		Planet p = Utils.createPlanet(game, world, "planet1", (int) (MIN_PLANET_SIZE + Math.round(Math.random()*(MAX_PLANET_SIZE-MIN_PLANET_SIZE))), randomPos);
		for(int i = 0; i < 8; i++)
			p.addOrbitter(Utils.createResource(game, Constants.RESOURCE_FOOD));
		planets.add(p);
		entities.addAll(p.getOrbitters());
		System.out.println("Number of Planets: " + totalPlanets);
		int placementX, placementY;
		newPlanet: while (numberOfPlanets < totalPlanets) {
			placementX = (int) ((2*Math.random() - 1) * MAX_PLANET_DIST);
			placementY = (int) ((2*Math.random() - 1) * MAX_PLANET_DIST);
			randomPos = new Vector2(placementX, placementY);
			// Make sure the planet is far from start.
			if (randomPos.dst2(spaceship.getPosition()) < MAX_START_DIST*MAX_START_DIST)
				continue;
			// And far enough from other planets.
			for (int i = 0; i < numberOfPlanets; i++) {
				if (randomPos.dst2(planetLocations[i]) < MIN_SEPARATION_PLANETS) {
					continue newPlanet;
				}
			}
			p = Utils.createPlanet(game, world, Math.random() < 0.5 ? "planet1" : "planet2", (int) (MIN_PLANET_SIZE + Math.round(Math.random()*(MAX_PLANET_SIZE-MIN_PLANET_SIZE))), randomPos);
			for(int i = 0; i < 8; i++)
				p.addOrbitter(Utils.createResource(game, Constants.RESOURCE_FOOD));
			planets.add(p);
			entities.addAll(p.getOrbitters());
			
			System.out.println("  Planet[" + numberOfPlanets + "]: " + placementX + ", " + placementY);
			planetLocations[numberOfPlanets] = randomPos;
			numberOfPlanets++;
		}

		int numberOfClouds = 0;
		int totalClouds = (int) (MIN_OXYGEN_CLOUDS + Math.random() * (MAX_OXYGEN_CLOUDS - MIN_OXYGEN_CLOUDS));
		Vector2[] cloudLocations = new Vector2[MAX_OXYGEN_CLOUDS];
		System.out.println("Number of Clouds: " + totalClouds);
		ResourceItem cloudFood = null;
		newCloud: while (numberOfClouds < totalClouds) {
			placementX = (int) ((2*Math.random() - 1) * MAX_CLOUD_DIST);
			placementY = (int) ((2*Math.random() - 1) * MAX_CLOUD_DIST);
			randomPos = new Vector2(placementX, placementY);
			// Make sure the cloud is far from start.
			if (randomPos.dst2(spaceship.getPosition()) < MAX_START_DIST*MAX_START_DIST)
				continue;
			// And far enough from other clouds.
			for (int i = 0; i < numberOfClouds; i++) {
				if (randomPos.dst2(cloudLocations[i]) < MIN_SEPARATION_CLOUDS) {
					continue newCloud;
				}
			}
			for(int i = 0; i < 4; i++) {
				cloudFood = Utils.createResource(game, Constants.RESOURCE_OXYGEN);
				cloudFood.initBody(world, randomPos.cpy().add(new Vector2(100, 0).rotate(MathUtils.random(360f))));
				entities.add(cloudFood);
			}
			
			System.out.println("  Cloud[" + numberOfClouds + "]: " + placementX + ", " + placementY);
			cloudLocations[numberOfClouds] = randomPos;
			clouds.add(randomPos);
			numberOfClouds++;
		}
		
		
		// Place the end planet.
		goldPlanet: while (true) {
			placementDist = (int) (MIN_DIST_GOAL + Math.random() * (MAX_DIST_GOAL - MIN_DIST_GOAL));
			placementAngle = 2*Math.PI*Math.random();
			randomPos = new Vector2((int) (placementDist*Math.cos(placementAngle)), (int) (placementDist*Math.sin(placementAngle)));

			for (int i = 0; i < numberOfPlanets; i++) {
				if (randomPos.dst2(planetLocations[i]) < MIN_SEPARATION_PLANETS) {
					continue goldPlanet;
				}
			}
			break;
		}
		System.out.println("Gold Planet: " + randomPos.x + ", " + randomPos.y);
		
		p = Utils.createPlanet(game, world, "goldplanet", 50, randomPos);
		p.endPlanet = true;
		planets.add(p);
	}
}
