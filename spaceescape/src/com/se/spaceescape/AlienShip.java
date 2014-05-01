package com.se.spaceescape;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.se.spaceescape.screens.SpaceScreen;

public class AlienShip extends PhysicalEntity {
	SpaceScreen screen;
	public int health;
	float blinkTime;
	float stealTime;
	public boolean stealFunnel = false;
	Vector2 offset;
	
	public AlienShip(SpaceEscapeGame g, SpaceScreen screen, Sprite s, Vector2 os) {
		super(g, s);
		this.screen = screen;
		health = 100;
		blinkTime = 0;
		stealTime = 0;
		offset = os;
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
	    body.setLinearDamping(1);
		body.setAngularDamping(0.2f);
	}
	
	public void rotate(float dr) {
		body.applyAngularImpulse(1000 * dr, true);
	}

	@Override
	public void render() {
		blinkTime -= Gdx.graphics.getDeltaTime();
		if((blinkTime > 0 && blinkTime % 0.3 < 0.15) || blinkTime <= 0)
			super.render();
		
		if(Constants.ATTACK_MODE == 1) {
			Vector2 pPos = game.gameScreen.spaceship.body.getWorldCenter();
			
			if(pPos.dst(body.getWorldCenter()) < Constants.ATTACK_DIST)
				body.applyForce(pPos.cpy().sub(body.getWorldCenter()).nor().scl(-10000), body.getWorldCenter(), true);
			if(pPos.dst(body.getWorldCenter()) > Constants.ATTACK_DIST * 1.3)
				body.applyForce(pPos.cpy().sub(body.getWorldCenter()).nor().scl(10000), body.getWorldCenter(), true);
			
		} else if(Constants.ATTACK_MODE == 2) {
			Vector2 pPos = game.gameScreen.spaceship.body.getWorldCenter().add(offset);
			
			if(pPos.dst(body.getWorldCenter()) > 10)
				body.applyForce(pPos.cpy().sub(body.getWorldCenter()).nor().scl(10000), body.getWorldCenter(), true);
		}
		
		
		if(game.gameScreen.spaceship.body.getWorldCenter().dst(body.getWorldCenter()) < Constants.ATTACK_DIST * 1.3) {
			stealTime -= Gdx.graphics.getDeltaTime();
			if(stealTime < Constants.STEAL_DELAY / 2)
				stealFunnel = true;
			else
				stealFunnel = false;
			if(stealTime < 0) {
				boolean steal = false;
				for(int i = game.gameScreen.resources.get(Constants.RESOURCE_WEAPONS).size - 1; i < Constants.TOTAL_RESOURCE[Constants.RESOURCE_WEAPONS]; i++)
					steal = steal || (MathUtils.random() < Constants.STEAL_PROB);

				if(steal) {
					game.gameScreen.spaceship.steal();
					if(MathUtils.random() < Constants.DESTROY_PROB) {
						ResourceGenerator rg = game.gameScreen.generators.get(MathUtils.random(game.gameScreen.generators.size - 1));
						rg.setActive(false);
					}
				}
				stealTime = Constants.STEAL_DELAY;
			}
		} else 
			stealFunnel = false;
	}
	
	public void hit(ResourceItem ri) {
		if(screen.toDestroy.contains(ri, true))
			return;
		screen.toDestroy.add(ri);
		if(ri.type <= Constants.NUM_RESOURCES) {
			health -= 50;
			blinkTime = 2;
			if(health <= 0) {
				this.kill();
				screen.toDestroy.add(this);
			}
		}
	}

}
