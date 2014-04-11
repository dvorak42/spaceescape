package com.se.spaceescape;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

public class Planet extends PhysicalEntity {
	public float altitude;
	public String fname;
	
	public Planet(SpaceEscapeGame g, Sprite s, float alt, String fn) {
		super(g, s);
		altitude = alt;
		fname = fn;
	}
	
	@Override
	public void initBody(World w, Vector2 pos) {
		super.initBody(w, pos);

		BodyDef bd = new BodyDef();
		bd.position.set(pos);
		bd.type = BodyType.DynamicBody;

		body = w.createBody(bd);

		FixtureDef fd = new FixtureDef();
		fd.density = 10000.0f; 
		fd.friction = 10000.00f;
	    Utils.mainBodies.attachFixture(body, fname, fd, sprite.getWidth());

		body.createFixture(fd);
		body.setUserData(this);
	}

}
