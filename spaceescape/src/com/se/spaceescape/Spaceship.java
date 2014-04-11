package com.se.spaceescape;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.se.spaceescape.screens.SpaceScreen;

public class Spaceship extends PhysicalEntity {
	SpaceScreen screen;

	public Spaceship(SpaceEscapeGame g, SpaceScreen screen, Sprite s) {
		super(g, s);
		this.screen = screen;
	}
	
	@Override
	public void initBody(World w, Vector2 pos) {
		super.initBody(w, pos);

		BodyDef bd = new BodyDef();
		bd.position.set(pos);
		bd.type = BodyType.DynamicBody;

		body = w.createBody(bd);

		FixtureDef fd = new FixtureDef();
	    fd.density = 0.1f;
	    fd.friction = 0.5f;
	    fd.restitution = 0.3f;
	    Utils.mainBodies.attachFixture(body, "spaceshuttle", fd, sprite.getWidth());
	    modelOrigin = Utils.mainBodies.getOrigin("spaceshuttle", sprite.getWidth());
	    
	    body.setUserData(this);
		body.setAngularDamping(0.2f);
	}
	
	public void rotate(float dr) {
		body.applyAngularImpulse(1000 * dr, true);
	}
	
	public void toss(Vector2 dir, ResourceItem ri) {
		Vector2 offset = dir.nor().cpy().scl(sprite.getWidth() * 0.8f);
		Vector2 pos = body.getWorldCenter().cpy().add(offset);
		
		ri.initBody(world, pos);
		
		Vector2 force = offset.cpy().nor().scl(100000);
		ri.body.applyForce(force, pos.cpy().sub(offset.cpy().scl(3f)), true);
		body.applyForce(Vector2.Zero.cpy().sub(force.cpy()), pos.cpy().sub(offset.cpy().scl(3f)), true);
	}

	public void acquire(ResourceItem ri) {
		screen.toDestroy.add(ri.body);
		screen.resources.add(ri);
		screen.entities.removeValue(ri, false);
	}
}
