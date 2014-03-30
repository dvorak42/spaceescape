package com.se.spaceescape;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

public class Spaceship extends Entity {
	Body body;
	
	public Spaceship(SpaceEscapeGame g, Sprite s) {
		super(g, s);
	}
	
	public void initBody(World w, Vector2 pos) {
		setPosition(pos);
		BodyDef bd = new BodyDef();
		bd.position.set(pos);
		bd.type = BodyType.DynamicBody;

		body = w.createBody(bd);
		CircleShape circle = new CircleShape();
		circle.setRadius(sprite.getWidth() * 0.45f);

		FixtureDef fd = new FixtureDef();
		fd.shape = circle;
		fd.density = 0.05f; 
		fd.friction = 0.04f;

		body.createFixture(fd);
		body.setUserData(this);
		body.setAngularDamping(0.2f);
		
		circle.dispose();
	}
	
	@Override
	public void rotate(float dr) {
		body.applyAngularImpulse(1000 * dr, true);
	}
}
