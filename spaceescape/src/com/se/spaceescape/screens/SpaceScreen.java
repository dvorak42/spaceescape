package com.se.spaceescape.screens;

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
import com.se.spaceescape.Constants;
import com.se.spaceescape.Entity;
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
	public Array<ResourceItem> tossedResources;
	public Array<Planet> planets;

	public Array<ResourceItem> toDestroy;
	
	// TEMP VARIABLES FOR CHOOSING UI
	// TODO: REMOVE THIS AND CHOOSE
	private boolean SEGMENTED_UI = true;
	private boolean SHADOWED = true;
	
	ShapeRenderer sr;
	
	public Array<ResourceGenerator> generators;
	
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
				ResourceItem ri = toDestroy.pop();
				world.destroyBody(ri.body);
				ri.body.setUserData(null);
				ri.body = null;
				entities.removeValue(ri, true);
				tossedResources.removeValue(ri, true);
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
		sr.begin(ShapeType.Filled);
		sr.setColor(new Color(1, 0, 0, 0.25f));
		sr.circle(midX, midY, 125);
		sr.setColor(new Color(1, 0, 0, 0.75f));
		
		// Grab the two closest planets.
		float[] closestPlanets  = {99999999f, 99999999f};
		int[] closestPlanetsIdx = new int[2];
		float planetDistance;
		for (int i = 0; i < planets.size; i++) {
			planetDistance = planets.get(i).getPosition().dst2(spaceship.getPosition());
			if (planetDistance < 250000 && planets.get(i).fname != "goldplanet") {
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
		for (int i = 0; i < closestPlanets.length; i++) {
			if (closestPlanets[i] < 250000) {
				Vector2 direction = planets.get(closestPlanetsIdx[i])
						.getPosition().sub(spaceship.getPosition()).nor();
				Vector2 ang1 = direction.cpy().rotate(10);
				Vector2 ang2 = direction.cpy().rotate(-10);			
				sr.triangle(midX + 150 * direction.x,  midY + 150 * direction.y,
						    midX + 120 * ang1.x,       midY + 120 * ang1.y,
						    midX + 120 * ang2.x,       midY + 120 * ang2.y);
			}
		}
		sr.setColor(Color.valueOf("FFD700CD"));
		Vector2 direction = planets.peek()
				.getPosition().sub(spaceship.getPosition()).nor();
		Vector2 ang1 = direction.cpy().rotate(10);
		Vector2 ang2 = direction.cpy().rotate(-10);			
		sr.triangle(midX + 160 * direction.x,  midY + 160 * direction.y,
				    midX + 110 * ang1.x,       midY + 110 * ang1.y,
				    midX + 110 * ang2.x,       midY + 110 * ang2.y);

		
		sr.end();
		Gdx.gl.glDisable(GL20.GL_BLEND);

		game.batch.setProjectionMatrix(camera.combined);
		game.batch.begin();
		spaceship.render();
		for(Entity r : entities)
			r.render();
		for(Entity r : planets)
			r.render();
		game.batch.end();
		
		sr = new ShapeRenderer();
		sr.begin(ShapeType.Filled);
		int initX = 50;
		int initY = 100;
		if (SEGMENTED_UI) {
			int offset = 0;
			for(int rType : Constants.RESOURCE_TYPES) {
				if(rType == selectedResource)
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
		initX = 28;
		initY = 75;
		int offset = 0;
		for(int rType : Constants.RESOURCE_TYPES) {
			Sprite s = new Sprite(Constants.RESOURCE_IMGS.get(rType).get(0));
			if(resources.get(rType).size > 0) {
				s = new Sprite(resources.get(rType).get(resources.get(rType).size - 1).sprite.getTexture());
			}
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
		spaceship.initBody(world, new Vector2(500, 500));
		entities = new Array<Entity>();
		tossedResources = new Array<ResourceItem>();
		planets = new Array<Planet>();
		toDestroy = new Array<ResourceItem>();

		resources = new Array<Array<ResourceItem>>();
		resources.add(null);

		for(int rType : Constants.RESOURCE_TYPES) {
			resources.add(new Array<ResourceItem>());
			for(int i = 0; i < Constants.TOTAL_RESOURCE[rType]; i++)
				resources.get(rType).add(Utils.createResource(game, rType));
		}
		
		
		Planet p = Utils.createPlanet(game, world, "planet1", 150, new Vector2(200, 200));
		for(int i = 0; i < 8; i++)
			p.addOrbitter(Utils.createResource(game, Constants.RESOURCE_FOOD));
		planets.add(p);
		entities.addAll(p.getOrbitters());

		p = Utils.createPlanet(game, world, "planet2", 100, new Vector2(700, 700));
		for(int i = 0; i < 8; i++)
			p.addOrbitter(Utils.createResource(game, Constants.RESOURCE_OXYGEN));
		planets.add(p);
		entities.addAll(p.getOrbitters());

		p = Utils.createPlanet(game, world, "goldplanet", 50, new Vector2(50, 800));
		p.endPlanet = true;
		planets.add(p);
		
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
