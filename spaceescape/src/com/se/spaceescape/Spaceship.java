package com.se.spaceescape;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
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
	    fd.friction = 0.1f;
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
		
		if(game.gameScreen.enemies.size > 0 && Constants.STOP_MOVE) {
			body.setLinearVelocity(Vector2.Zero);
			body.setAngularVelocity(0);
		}
		
		if(stealType >= 0) {
			if(stealing < 0) {
				if(MathUtils.random() < Constants.DESTROY_PROB && stealType < game.gameScreen.generators.size) {
					ResourceGenerator rg = game.gameScreen.generators.get(stealType);
					rg.setActive(false);
				} else {
					Array<ResourceItem> r = game.gameScreen.resources.get(stealType);
					if(r != null && r.size > 0) {
						r.pop();
					}
				}
				stealType = -1;
				game.gameScreen.stealingResource = -1;
			}
			if(stealing % 0.3 < 0.15)
				game.gameScreen.stealingResource = stealType;
			else
				game.gameScreen.stealingResource = 0;
		}
		
		if(stealType < 0 || stealing % 0.3 > 0.15) {
			if(stealType >= 0)
				sprite.setColor(game.gameScreen.tint(Color.RED));
			super.render();
			sprite.setColor(game.gameScreen.tint(Color.WHITE));
		}
	}
	
	public void steal(Bullet b) {
		if(screen.toDestroy.contains(b, true))
			return;
		screen.toDestroy.add(b);
		stealing = 1.0f;
		stealType = MathUtils.random(1, Constants.NUM_RESOURCES - 1);
	}
	
	public void toss(Vector2 dir, ResourceItem ri) {
		Vector2 offset = dir.nor().cpy().scl(sprite.getHeight() * 0.2f + ri.sprite.getHeight() * 1.5f);
		Vector2 pos = body.getWorldCenter().cpy().sub(ri.getSize().cpy().scl(0.5f));
		targetAngle = dir.angle() + 90;
		ri.initBody(world, pos);
		ri.leaving = true;
		
		Vector2 force = offset.cpy().nor().scl(1000000000);
		//ri.body.applyForce(force, pos.cpy().sub(offset.cpy().scl(3f)), true);
		ri.body.applyForce(force, ri.body.getWorldCenter(), true);
		body.applyForce(Vector2.Zero.cpy().sub(force.cpy()), body.getWorldCenter(), true);
		screen.entities.add(ri);
		screen.tossedResources.add(ri);
		screen.popAudio.play(0.35f);
	}

	public void acquire(ResourceItem ri) {
		if(ri.leaving || screen.toDestroy.contains(ri, true))
			return;
		screen.itemGetAudio.play(0.2f);
		Utils.addItem(game, ri.type, getPosition());
		screen.pickedUp++;
		screen.toDestroy.add(ri);
	}
}
