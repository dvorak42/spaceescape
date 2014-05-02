package com.se.spaceescape;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.utils.Array;
import com.se.spaceescape.screens.SpaceScreen;

public class Spaceship extends PhysicalEntity {
	SpaceScreen screen;
	public float targetAngle;
	
	float stealing;
	int stealType;

	public Spaceship(SpaceEscapeGame g, SpaceScreen screen, Sprite s) {
		super(g, s);
		this.screen = screen;
		targetAngle = 0.0f;
		stealType = -1;
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
	    fd.friction = 0.2f;
	    fd.restitution = 0.3f;
	    Utils.mainBodies.attachFixture(body, "spaceshuttle", fd, sprite.getWidth());
	    modelOrigin = Utils.mainBodies.getOrigin("spaceshuttle", sprite.getWidth());
	    
	    body.setUserData(this);
		body.setAngularDamping(0.2f);
	}
	
	public void rotate(float dr) {
		body.applyAngularImpulse(1000 * dr, true);
	}
	
	@Override
	public void render() {
		stealing -= Gdx.graphics.getDeltaTime();
		
		if(game.gameScreen.enemies.size > 0) {
			body.setLinearVelocity(Vector2.Zero);
			body.setAngularVelocity(0);
		}
		
		if(stealType >= 0) {
			if(stealing < 0) {
				Array<ResourceItem> r = game.gameScreen.resources.get(stealType);
				if(r != null && r.size > 0) {
					r.pop();
				}
				stealType = -1;
				game.gameScreen.stealingResource = -1;
			}
			if(stealing % 0.3 < 0.15)
				game.gameScreen.stealingResource = stealType;
			else
				game.gameScreen.stealingResource = 0;
		}
		
		super.render();
	}
	
	public void steal() {
		stealing = 1.0f;
		stealType = MathUtils.random(1, Constants.NUM_RESOURCES - 1);
	}
	
	public void toss(Vector2 dir, ResourceItem ri) {
		Vector2 offset = dir.nor().cpy().scl(sprite.getHeight() * 0.2f + ri.sprite.getHeight() * 1.5f);
		Vector2 pos = body.getWorldCenter().cpy().add(offset);
		targetAngle = dir.angle() + 90;
		ri.initBody(world, pos);
		
		Vector2 force = offset.cpy().nor().scl(10000000);
		//ri.body.applyForce(force, pos.cpy().sub(offset.cpy().scl(3f)), true);
		ri.body.applyForce(force, ri.body.getWorldCenter(), true);
		body.applyForce(Vector2.Zero.cpy().sub(force.cpy()), pos.cpy(), true);
		screen.entities.add(ri);
		screen.tossedResources.add(ri);
	}

	public void acquire(ResourceItem ri) {
		if(screen.toDestroy.contains(ri, true))
			return;
		Utils.addItem(game, ri.type, getPosition());
		screen.toDestroy.add(ri);
	}
}
