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
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.se.spaceescape.Constants;
import com.se.spaceescape.Entity;
import com.se.spaceescape.ResourceItem;
import com.se.spaceescape.SpaceEscapeGame;
import com.se.spaceescape.SpaceGestureListener;
import com.se.spaceescape.Spaceship;
import com.se.spaceescape.Utils;

public class SpaceScreen implements Screen {
	public SpaceEscapeGame game;

	OrthographicCamera camera;

	Texture spaceshipTexture;
	Texture backgroundTexture;
	
	public Spaceship spaceship;
	
	World world;
	Box2DDebugRenderer debugRenderer;
	
	public int selectedResource = Constants.RESOURCE_FOOD;

	public Array<ResourceItem> foodResources;
	public Array<ResourceItem> oxygenResources;
	public Array<ResourceItem> sanityResources;
	public Array<ResourceItem> powerResources;
	public Array<Entity> entities;
	
	// TEMP VARIABLES FOR CHOOSING UI
	// TODO: REMOVE THIS AND CHOOSE
	private boolean SEGMENTED_UI = true;
	private boolean SHADOWED = true;
	
	public SpaceScreen(SpaceEscapeGame g) {
		game = g;
		
		world = new World(new Vector2(0, 0), true);
		debugRenderer = new Box2DDebugRenderer();

		float w = Gdx.graphics.getWidth();
		float h = Gdx.graphics.getHeight();
		
		camera = new OrthographicCamera(w, h);
		camera.zoom = 0.1f*5;
		
		spaceshipTexture = new Texture(Gdx.files.internal("art/spaceshuttle.png"));
		spaceshipTexture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		
		backgroundTexture = new Texture(Gdx.files.internal("art/space.png"));
		backgroundTexture.setWrap(TextureWrap.Repeat, TextureWrap.Repeat);
		
		Sprite spaceshipSprite = new Sprite(spaceshipTexture);
		spaceship = new Spaceship(g, spaceshipSprite);
		spaceship.setSize(new Vector2(80, 80));
		Utils.createBounds(world, 1000, 1000);
		spaceship.initBody(world, new Vector2(500, 500));
		entities = new Array<Entity>();
		
		foodResources = new Array<ResourceItem>();
		for(int i = 0; i < Constants.TOTAL_RESOURCE_FOOD; i++) {
			foodResources.add(Utils.createResource(game, Constants.RESOURCE_FOOD));
		}
		oxygenResources = new Array<ResourceItem>();
		for(int i = 0; i < Constants.TOTAL_RESOURCE_OXYGEN; i++) {
			oxygenResources.add(Utils.createResource(game, Constants.RESOURCE_OXYGEN));
		}
		sanityResources = new Array<ResourceItem>();
		for(int i = 0; i < Constants.TOTAL_RESOURCE_SANITY; i++) {
			sanityResources.add(Utils.createResource(game, Constants.RESOURCE_SANITY));
		}
		powerResources = new Array<ResourceItem>();
		for(int i = 0; i < Constants.TOTAL_RESOURCE_POWER; i++) {
			powerResources.add(Utils.createResource(game, Constants.RESOURCE_POWER));
		}
		
		Gdx.input.setInputProcessor(new GestureDetector(new SpaceGestureListener(this)));
	}
	
	public void runPhysics(float delta) {
		debugRenderer.render(world, camera.combined);
		world.step(1/60f, 6, 2);
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
		game.batch.end();

		runPhysics(delta);

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
			sr.setColor(Color.GREEN);
			float arclength = 360 / Constants.TOTAL_RESOURCE_FOOD;
			for (int i = 0; i < foodResources.size; i++) {
				sr.arc(testX,testY,34, 90 + (i * arclength), arclength - 5, 3);
			}
			sr.setColor(Color.BLUE);
			arclength = 360 / Constants.TOTAL_RESOURCE_OXYGEN;
			for (int i = 0; i < oxygenResources.size; i++) {
				sr.arc(testX,testY+testOffset,34, 90 + (i * arclength), arclength - 5, 3);
			}
			sr.setColor(Color.PINK);
			arclength = 360 / Constants.TOTAL_RESOURCE_SANITY;
			for (int i = 0; i < sanityResources.size; i++) {
				sr.arc(testX,testY+2*testOffset,34, 90 + (i * arclength), arclength - 5, 3);
			}
			sr.setColor(Color.RED);
			arclength = 360 / Constants.TOTAL_RESOURCE_POWER;
			for (int i = 0; i < powerResources.size; i++) {
				sr.arc(testX,testY+3*testOffset,34, 90 + (i * arclength), arclength - 5, 3);
			}
			if (SHADOWED) {
				sr.setColor(Color.GRAY);
				arclength = 360 / Constants.TOTAL_RESOURCE_FOOD;
				for (int i = foodResources.size; i < Constants.TOTAL_RESOURCE_FOOD; i++) {
					sr.arc(testX,testY,34, 90 + (i * arclength), arclength - 5, 3);
				}
				arclength = 360 / Constants.TOTAL_RESOURCE_OXYGEN;
				for (int i = oxygenResources.size; i < Constants.TOTAL_RESOURCE_OXYGEN; i++) {
					sr.arc(testX,testY+testOffset,34, 90 + (i * arclength), arclength - 5, 3);
				}
				arclength = 360 / Constants.TOTAL_RESOURCE_SANITY;
				for (int i = sanityResources.size; i < Constants.TOTAL_RESOURCE_SANITY; i++) {
					sr.arc(testX,testY+2*testOffset,34, 90 + (i * arclength), arclength - 5, 3);
				}
				arclength = 360 / Constants.TOTAL_RESOURCE_POWER;
				for (int i = powerResources.size; i < Constants.TOTAL_RESOURCE_POWER; i++) {
					sr.arc(testX,testY+3*testOffset,34, 90 + (i * arclength), arclength - 5, 3);
				}
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
		
		game.hudBatch.begin();
		testX -= 28;
		testY -= 28;
		game.hudBatch.draw(Constants.RESOURCE_IMGS[Constants.RESOURCE_FOOD], testX, testY, 54, 54);
		game.hudBatch.draw(Constants.RESOURCE_IMGS[Constants.RESOURCE_OXYGEN], testX-1, testY+testOffset+3, 54, 54);
		game.hudBatch.draw(Constants.RESOURCE_IMGS[Constants.RESOURCE_SANITY], testX+3, testY+2*testOffset+6, 54, 54);
		game.hudBatch.draw(Constants.RESOURCE_IMGS[Constants.RESOURCE_POWER], testX+4, testY+3*testOffset-2, 54, 54);
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
