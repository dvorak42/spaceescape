package com.se.spaceescape.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.Texture.TextureWrap;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
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
		while(toDestroy.size > 0) {
			ResourceItem ri = toDestroy.pop();
			world.destroyBody(ri.body);
			ri.body.setUserData(null);
			ri.body = null;
			entities.removeValue(ri, true);
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

		//TODO: Code for attraction of items.
//		Array<Body> bodies = new Array<Body>();
//		world.getBodies(bodies);
//		for(Body b : bodies) {
//			if(b.getUserData() instanceof Spaceship || b.getUserData() instanceof Planet)
//				continue;
//			Vector2 p1 = b.getWorldCenter();
//			Vector2 tf = new Vector2();
//			for(Planet p : planets) {
//				Vector2 p2 = p.body.getWorldCenter();
//				Vector2 dp = p2.cpy().sub(p1);
//				float force = 10000000 * b.getMass() / dp.len2();
//				if(dp.len() > p.altitude)
//					tf.add(dp.nor().scl(force));
//				else
//					tf.add(dp.nor().scl(0.25f * force).rotate(115));
//			}
//			b.applyForce(tf, p1, true);
//		}
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
		int tilecount = 8;
		Constants.SPACE_TEXTURE.setWrap(TextureWrap.Repeat, TextureWrap.Repeat);
		game.hudBatch.draw(Constants.SPACE_TEXTURE,  - Gdx.graphics.getWidth(),  - Gdx.graphics.getHeight(),
				Constants.SPACE_TEXTURE.getWidth() * tilecount, Constants.SPACE_TEXTURE.getHeight() * tilecount,
				0, tilecount, tilecount, 0);
		game.hudBatch.end();

		runPhysics(delta);

		game.batch.setProjectionMatrix(camera.combined);
		game.batch.begin();
		spaceship.render();
		for(Entity r : entities)
			r.render();
		for(Entity r : planets)
			r.render();
		game.batch.end();
		

		// We have to end all SpriteBatches before we start using the ShapeRenderer
		// or we will get side effects when choosing the colors. As per:
		// https://stackoverflow.com/questions/16381106/libgdx-shaperenderer-in-group-draw-renders-in-wrong-colour
		ShapeRenderer sr = new ShapeRenderer();
		sr.begin(ShapeType.Filled);
		int testX = 50;
		int testY = 100;
		int testOffset = 100;
		if (SEGMENTED_UI) {
			sr.setColor(Color.WHITE);
			sr.circle(testX, testY, 36);
			sr.circle(testX, testY+testOffset, 36);
			sr.circle(testX, testY+2*testOffset, 36);
			sr.circle(testX, testY+3*testOffset, 36);
			sr.setColor(Color.YELLOW);
			sr.circle(testX, testY+(selectedResource-1)*testOffset, 36);
			int offset = 0;
			for(int rType : Constants.RESOURCE_TYPES) {
				int total = Math.max(Constants.TOTAL_RESOURCE[rType], resources.get(rType).size);
				float arclength = 360 / total;
				for (int i = 0; i < total; i++) {
					if(i < resources.get(rType).size) {
						sr.setColor(Constants.RESOURCE_COLORS[rType]);
					} else if(SHADOWED) {
						sr.setColor(Color.GRAY);
					} else
						continue;
					
					sr.arc(testX,testY + offset,34, 90 + (i * arclength), arclength - 5, 3);					
				}
				offset += 100;
			}

			sr.setColor(Color.WHITE);
			sr.circle(testX, testY, 24);
			sr.circle(testX, testY+testOffset, 24);
			sr.circle(testX, testY+2*testOffset, 24);
			sr.circle(testX, testY+3*testOffset, 24);
			sr.setColor(Color.YELLOW);
			sr.circle(testX, testY+(selectedResource-1)*testOffset, 24);
		}
		sr.end();
		
		game.hudBatch.setProjectionMatrix(sr.getProjectionMatrix());
		game.hudBatch.begin();
		testX -= 28;
		testY -= 28;
		game.hudBatch.draw(Constants.RESOURCE_IMGS.get(Constants.RESOURCE_FOOD).get(0), testX, testY, 54, 54);
		game.hudBatch.draw(Constants.RESOURCE_IMGS.get(Constants.RESOURCE_OXYGEN).get(0), testX-1, testY+testOffset+3, 54, 54);
		game.hudBatch.draw(Constants.RESOURCE_IMGS.get(Constants.RESOURCE_SANITY).get(0), testX+3, testY+2*testOffset+6, 54, 54);
		game.hudBatch.draw(Constants.RESOURCE_IMGS.get(Constants.RESOURCE_WEAPONS).get(0), testX+4, testY+3*testOffset-2, 54, 54);
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
