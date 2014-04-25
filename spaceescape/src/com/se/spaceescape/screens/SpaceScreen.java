package com.se.spaceescape.screens;

import sun.net.www.http.Hurryable;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
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

	public OrthographicCamera camera;
	
	public Spaceship spaceship;
	
	public boolean paused = false;
	
	World world;
	Box2DDebugRenderer debugRenderer;
	
	public int selectedResource = Constants.RESOURCE_FOOD;

	public Array<Array<ResourceItem>> resources;
	public Array<Entity> entities;
	public Array<AlertEntity> hovering;
	public Array<ResourceItem> tossedResources;
	public Array<Planet> planets;
	public Array<AlienShip> enemies;
	
	public Array<PhysicalEntity> toDestroy;
	
	// TEMP VARIABLES FOR CHOOSING UI
	// TODO: REMOVE THIS AND CHOOSE
	private boolean SEGMENTED_UI = true;
	private boolean SHADOWED = true;
	
	ShapeRenderer sr;
	
	public Array<ResourceGenerator> generators;
	
	float timeToAttack;
	
	public int stealingResource = -1;
	
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
		
			if(resources.get(Constants.RESOURCE_OXYGEN).size == 0)
				game.setScreen(new LoseScreen(game, this));
		}
		//debugRenderer.render(world, camera.combined);
	}
	
	public void attackPlayer() {
		float angle = MathUtils.random(360);

		for(int i = enemies.size; i < Constants.ATTACK_SIZE; i++) {
			AlienShip a = new AlienShip(game, this, new Sprite(Constants.SPACESHIP_TEXTURE));
			a.setSize(new Vector2(64, 64));
			a.initBody(world, spaceship.getPosition().cpy().add(new Vector2(0, 500).rotate(angle + 120 * i)));
			entities.add(a);
			enemies.add(a);
		}
	}

	@Override
	public void render(float delta) {
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

		runPhysics(delta);
		
		int midX = Gdx.graphics.getWidth() / 2;
		int midY = Gdx.graphics.getHeight() / 2;
		
		Gdx.gl.glEnable(GL20.GL_BLEND);
	    Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		ShapeRenderer sr = new ShapeRenderer();
		sr.begin(ShapeType.Line);
		Gdx.gl.glLineWidth(20);
		sr.setColor(Color.valueOf("00853A"));
		sr.circle(midX, midY, 125, 100);
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
		sr.setColor(Color.valueOf("FFD700"));
		for (int i = 0; i < closestPlanets.length; i++) {
			if (closestPlanets[i] < 700000) {
				Vector2 direction = planets.get(closestPlanetsIdx[i])
						.body.getWorldCenter().sub(spaceship.body.getWorldCenter()).nor();
				Vector2 ang1 = direction.cpy().rotate(10);
				Vector2 ang2 = direction.cpy().rotate(-10);			
				sr.triangle(midX + 150 * direction.x,  midY + 150 * direction.y,
						    midX + 120 * ang1.x,       midY + 120 * ang1.y,
						    midX + 120 * ang2.x,       midY + 120 * ang2.y);
			}
		}
		sr.setColor(Color.valueOf("a8ff00"));
		Vector2 direction = planets.peek()
				.body.getWorldCenter().sub(spaceship.body.getWorldCenter()).nor();
		Vector2 ang1 = direction.cpy().rotate(10);
		Vector2 ang2 = direction.cpy().rotate(-10);			
		sr.triangle(midX + 170 * direction.x,  midY + 170 * direction.y,
				    midX + 120 * ang1.x,       midY + 120 * ang1.y,
				    midX + 120 * ang2.x,       midY + 120 * ang2.y);
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
		for(Entity r : planets)
			r.render();
		game.batch.end();
		
		sr = new ShapeRenderer();
		sr.setProjectionMatrix(camera.combined);
		sr.begin(ShapeType.Line);
		sr.setColor(Color.BLACK);

		for(AlienShip a : enemies) {
			if(a.stealFunnel)
				sr.line(a.body.getWorldCenter().x, a.body.getWorldCenter().y, spaceship.body.getWorldCenter().x, spaceship.body.getWorldCenter().y);
		}
		sr.end();
		
		sr = new ShapeRenderer();
		sr.begin(ShapeType.Filled);
		int initX = 50;
		int initY = 100;
		if (SEGMENTED_UI) {
			int offset = 0;
			for(int rType : Constants.RESOURCE_TYPES) {
				if(rType == stealingResource)
					sr.setColor(Color.RED);
				else if(rType == selectedResource)
					sr.setColor(Color.YELLOW);
				else
					sr.setColor(Color.WHITE);
				sr.circle(initX, initY + offset, 36);
				int total = Math.max(Constants.TOTAL_RESOURCE[rType], resources.get(rType).size);
				float arclength = 360 / total;
				for (int i = 0; i < total; i++) {
					if(i < resources.get(rType).size) {
						sr.setColor(Constants.RESOURCE_COLORS[rType]);
					} else if(SHADOWED) {
						sr.setColor(Color.GRAY);
					} else
						continue;
					
					sr.arc(initX,initY + offset, 34, 90 + (i * arclength), arclength - 5, 3);					
				}
				if(rType == selectedResource)
					sr.setColor(Color.YELLOW);
				else
					sr.setColor(Color.WHITE);
				sr.circle(initX, initY + offset, 24);
				offset += 100;
			}
		}
		sr.end();
		
		game.hudBatch.setProjectionMatrix(sr.getProjectionMatrix());
		game.hudBatch.begin();
		initX = 26;
		initY = 76;
		int offset = 0;
		for(int rType : Constants.RESOURCE_TYPES) {
			Sprite s = new Sprite(Constants.RESOURCE_ICONS[rType]);
			
			s.setPosition(initX, initY + offset);
			s.setSize(48, 48);
			s.draw(game.hudBatch);
			offset += 100;
		}

		int yPos = 50;
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
		
		if(Gdx.input.isKeyPressed(Input.Keys.P))
			game.setScreen(game.pauseScreen);
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
		camera.zoom = 0.1f*5;
		
		sr = new ShapeRenderer();
		
		Sprite spaceshipSprite = new Sprite(Constants.SPACESHIP_TEXTURE);
		spaceship = new Spaceship(game, this, spaceshipSprite);
		spaceship.setSize(new Vector2(80, 80));
		//Utils.createBounds(world, 1000, 1000);
		spaceship.initBody(world, new Vector2(0, 0));
		entities = new Array<Entity>();
		tossedResources = new Array<ResourceItem>();
		planets = new Array<Planet>();
		toDestroy = new Array<PhysicalEntity>();
		hovering = new Array<AlertEntity>();
		resources = new Array<Array<ResourceItem>>();
		resources.add(null);

		for(int rType : Constants.RESOURCE_TYPES) {
			resources.add(new Array<ResourceItem>());
			for(int i = 0; i < Constants.TOTAL_RESOURCE[rType]; i++)
				resources.get(rType).add(Utils.createResource(game, rType));
		}
		resources.add(new Array<ResourceItem>());
		for(int i = 0; i < Constants.TOTAL_RESOURCE[Constants.RESOURCE_OXYGEN]; i++)
			resources.get(Constants.RESOURCE_OXYGEN).add(Utils.createResource(game, Constants.RESOURCE_OXYGEN));
		
		// Random map generation values for testing.
		// A bunch of EOL comments just for completeness. They can be removed.
		int MIN_DIST_GOAL = 1000; // Minimum distance from start -> goal.
		int MAX_DIST_GOAL = 3000; // Maximum distance from start -> goal.
		int MIN_START_DIST = 500; // Minimum distance from start to the closest planet.
		int MAX_START_DIST = 700; // Maximum distance from start to the closest planet.
		int MAX_PLANET_DIST = 2000; // Maximum distance from start to a planet.
		int MIN_PLANETS = 4; // The minimum number of planets.
		int MAX_PLANETS = 8; // The maximum number of planets.
		int MIN_OXYGEN_CLOUDS; // The minimum number of oxygen clouds.
		int MAX_OXYGEN_CLOUDS; // The maximum number of oxygen clouds.
		int MIN_SEPARATION_PLANETS = 250000; // The minimum squared distance between planets.
		int MAX_SEPARATION_PLANETS; // The maximum distance between planets.
		int MIN_SEPARATION_CLOUDS; // The minimum distance between oxygen clouds.
		int MAX_SEPARATION_CLOUDS; // The maximum distance between oxygen clouds.
		int MIN_PLANET_SIZE = 50;
		int MAX_PLANET_SIZE = 150;
		
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


//		p = Utils.createPlanet(game, world, "planet2", 100, new Vector2(700, 700));
//		for(int i = 0; i < 8; i++)
//			p.addOrbitter(Utils.createResource(game, Constants.RESOURCE_OXYGEN));
//		planets.add(p);
//		entities.addAll(p.getOrbitters());
		
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

		enemies = new Array<AlienShip>();
		
		generators = new Array<ResourceGenerator>();
		Sprite gSprite = new Sprite(Constants.RESOURCE_GENERATOR_TEXTURES[Constants.RESOURCE_FOOD]);
		ResourceGenerator foodGenerator = new ResourceGenerator(game, gSprite, Constants.RESOURCE_FOOD);
		generators.add(foodGenerator);

		gSprite = new Sprite(Constants.RESOURCE_GENERATOR_TEXTURES[Constants.RESOURCE_SANITY]);
		ResourceGenerator sanityGenerator = new ResourceGenerator(game, gSprite, Constants.RESOURCE_SANITY);
		generators.add(sanityGenerator);

		gSprite = new Sprite(Constants.RESOURCE_GENERATOR_TEXTURES[Constants.RESOURCE_WEAPONS]);
		ResourceGenerator weaponsGenerator = new ResourceGenerator(game, gSprite, Constants.RESOURCE_WEAPONS);
		generators.add(weaponsGenerator);

		timeToAttack = Constants.ATTACK_DELAY;
		
		Gdx.input.setInputProcessor(new GestureDetector(new SpaceGestureListener(this)));
		
		Gdx.gl.glEnable(GL10.GL_LINE_SMOOTH);
		Gdx.gl.glEnable(GL10.GL_POINT_SMOOTH);
		Gdx.gl.glHint(GL10.GL_POLYGON_SMOOTH_HINT, GL10.GL_NICEST);
		Gdx.gl.glHint(GL10.GL_POINT_SMOOTH_HINT, GL10.GL_NICEST);
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
	}

}
