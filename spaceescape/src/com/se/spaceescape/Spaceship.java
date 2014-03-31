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
	World world;
	
	public Spaceship(SpaceEscapeGame g, Sprite s) {
		super(g, s);
	}
	
	public void initBody(World w, Vector2 pos) {
		setPosition(pos);
		world = w;
		BodyDef bd = new BodyDef();
		bd.position.set(pos);
		bd.type = BodyType.DynamicBody;

		body = w.createBody(bd);
		CircleShape circle = new CircleShape();
		circle.setRadius(sprite.getWidth() * 0.45f);

		FixtureDef fd = new FixtureDef();
		fd.shape = circle;
		fd.density = 0.05f; 
		fd.friction = 0.05f;

		body.createFixture(fd);
		body.setUserData(this);
		body.setAngularDamping(0.2f);
		
		circle.dispose();
	}
	
	@Override
	public void rotate(float dr) {
		body.applyAngularImpulse(1000 * dr, true);
	}
	
	public void toss(Vector2 dir, ResourceItem ri) {
		Vector2 offset = dir.nor().cpy().scl(sprite.getWidth() * 0.6f);
		Vector2 pos = body.getPosition().cpy().add(offset);
		
		ri.initBody(world, pos);
		
		Vector2 force = offset.cpy().nor().scl(100000);
		ri.body.applyForce(force, pos.cpy().sub(offset.cpy().scl(3f)), true);
		body.applyForce(Vector2.Zero.cpy().sub(force.cpy()), pos.cpy().sub(offset.cpy().scl(3f)), true);
	}
}
