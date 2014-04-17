package com.se.spaceescape;

import java.util.ArrayList;

import com.se.spaceescape.external.BodyEditorLoader;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.World;

public class Utils {
    public static BodyEditorLoader mainBodies = new BodyEditorLoader(Gdx.files.internal("data/main_bodies.json"));
    
	public static Body createWall(World w, Vector2 start, Vector2 end) {
		BodyDef bd = new BodyDef();
		bd.position.set(start);
		bd.type = BodyType.StaticBody;

		Body body = w.createBody(bd);
		EdgeShape edge = new EdgeShape();
		edge.set(Vector2.Zero, end.sub(start));

		FixtureDef fd = new FixtureDef();
		fd.shape = edge;

		body.createFixture(fd);
		
		return body;
	}
	
	public static void createBounds(World w, int width, int height) {
		createWall(w, Vector2.Zero, new Vector2(width, 0));
		createWall(w, Vector2.Zero, new Vector2(0, height));
		createWall(w, new Vector2(width, 0), new Vector2(width, height));
		createWall(w, new Vector2(0, height), new Vector2(width, height));
	}

	public static ResourceItem createResource(SpaceEscapeGame g, int rType) {
		ArrayList<Texture> rTextures = Constants.RESOURCE_IMGS.get(rType);
		int variety = MathUtils.random(rTextures.size() - 1);
		Sprite sprite = new Sprite(rTextures.get(variety));
		ResourceItem ri = new ResourceItem(g, sprite, rType, Constants.RESOURCE_NAMES.get(rType).get(variety));
		ri.setSize(new Vector2(16, 16));
		return ri;
	}
	
	public static Vector2 worldSpace(Vector2 sPoint, Vector2 sCenter, float scale) {
		return sPoint.cpy().sub(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2).scl(scale).add(sCenter);
	}

	public static Planet createPlanet(SpaceEscapeGame game, World world, String fname, int alt, Vector2 pos) {
		Texture fPlanetTexture = new Texture(Gdx.files.internal("art/" + fname + ".png"));
		Planet p = new Planet(game, new Sprite(fPlanetTexture), alt, fname);
		p.initBody(world, pos);
		return p;
	}
	
	public static void addItem(SpaceEscapeGame game, int type) {
		Gdx.input.vibrate(200);
		if(type <= Constants.NUM_RESOURCES && type > 0)
			game.gameScreen.resources.get(type).add(Utils.createResource(game, type));
	}
}
