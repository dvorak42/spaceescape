package com.se.spaceescape.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.Texture.TextureWrap;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.se.spaceescape.Constants;
import com.se.spaceescape.Entity;
import com.se.spaceescape.Planet;
import com.se.spaceescape.ResourceItem;
import com.se.spaceescape.SpaceContactListener;
import com.se.spaceescape.SpaceEscapeGame;
import com.se.spaceescape.SpaceGestureListener;
import com.se.spaceescape.Spaceship;
import com.se.spaceescape.Utils;

public class SpaceScreen implements Screen {
	public SpaceEscapeGame game;

	public OrthographicCamera camera;

	Texture spaceshipTexture;
	Texture backgroundTexture;
	
	public Spaceship spaceship;
	
	World world;
	Box2DDebugRenderer debugRenderer;

	public Array<ResourceItem> resources;
	public Array<Entity> entities;
	public Array<Planet> planets;

	public Array<Body> toDestroy;
	
	public SpaceScreen(SpaceEscapeGame g) {
		game = g;
		
		world = new World(new Vector2(0, 0), true);
		debugRenderer = new Box2DDebugRenderer();
		
		world.setContactListener(new SpaceContactListener());

		float w = Gdx.graphics.getWidth();
		float h = Gdx.graphics.getHeight();
		
		camera = new OrthographicCamera(w, h);
		camera.zoom = 1;//0.1f*5;
		
		spaceshipTexture = new Texture(Gdx.files.internal("art/spaceshuttle.png"));
		spaceshipTexture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		
		backgroundTexture = new Texture(Gdx.files.internal("art/space.png"));
		backgroundTexture.setWrap(TextureWrap.Repeat, TextureWrap.Repeat);
		
		Sprite spaceshipSprite = new Sprite(spaceshipTexture);
		spaceship = new Spaceship(g, this, spaceshipSprite);
		spaceship.setSize(new Vector2(80, 80));
		Utils.createBounds(world, 1000, 1000);
		spaceship.initBody(world, new Vector2(500, 500));
		resources = new Array<ResourceItem>();
		entities = new Array<Entity>();
		planets = new Array<Planet>();
		toDestroy = new Array<Body>();
		for(int i = 0; i < Constants.TOTAL_RESOURCE_FOOD; i++) {
			resources.add(Utils.createResource(game, Constants.RESOURCE_FOOD));
		}
		
		planets.add(Utils.createPlanet(game, world, "planet1", 150, new Vector2(300, 300)));
		planets.add(Utils.createPlanet(game, world, "planet2", 100, new Vector2(600, 600)));
		Gdx.input.setInputProcessor(new GestureDetector(new SpaceGestureListener(this)));
	}
	
	public void runPhysics(float delta) {
		for(Body b : toDestroy)
			world.destroyBody(b);
		toDestroy = new Array<Body>();
		
		Array<Body> bodies = new Array<Body>();
		world.getBodies(bodies);
		for(Body b : bodies) {
			if(b.getUserData() instanceof Spaceship || b.getUserData() instanceof Planet)
				continue;
			Vector2 p1 = b.getWorldCenter();
			for(Planet p : planets) {
				Vector2 p2 = p.body.getWorldCenter();
				Vector2 dp = p2.cpy().sub(p1);
				float force = 10000000 * b.getMass() / dp.len2();
				if(dp.len() > p.altitude)
					b.applyForce(dp.nor().scl(force), p1, true);
				else
					b.applyForce(dp.nor().scl(0.25f * force).rotate(115), p1, true);

			}			
		}
		world.step(1/60f, 6, 2);
		debugRenderer.render(world, camera.combined);
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		camera.position.set(spaceship.body.getWorldCenter(), 0);
		camera.update();
		
		game.backgroundBatch.begin();
		int tilecount = 6;
		game.backgroundBatch.draw(backgroundTexture, -spaceship.getPosition().x, -spaceship.getPosition().y,
				backgroundTexture.getWidth() * tilecount, backgroundTexture.getHeight() * tilecount,
				0, tilecount, tilecount, 0);
		game.backgroundBatch.end();

		game.batch.setProjectionMatrix(camera.combined);
		game.batch.begin();
		spaceship.render();
		for(Entity r : entities)
			r.render();
		for(Entity r : planets)
			r.render();
		game.batch.end();

		runPhysics(delta);

		game.hudBatch.begin();
		game.font.setColor(Color.BLACK);
		game.font.setScale(1.1f);
		game.font.draw(game.hudBatch, "Food Available:", 14, 52);
		ShapeRenderer sr = new ShapeRenderer();
		sr.begin(ShapeType.Filled);
		sr.setColor(Color.BLACK);
		sr.rect(10, 10, 200, 20);
		sr.setColor(Color.BLUE);
		sr.rect(14, 14, 192, 12);
		sr.setColor(Color.GREEN);
		sr.rect(14, 14, (int)(192f * resources.size / Constants.TOTAL_RESOURCE_FOOD), 12);
		sr.end();
		game.hudBatch.end();
	}

	@Override
	public void resize(int width, int height) {
		camera.viewportHeight = height;
		camera.viewportWidth = width;
		camera.update();
	}

	@Override
	public void show() {
		// TODO Auto-generated method stub

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
		spaceshipTexture.dispose();
	}

}
