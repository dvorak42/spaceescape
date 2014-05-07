package com.se.spaceescape;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
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
	Vector2 offset;
	public float fleeTimer = -1;
	boolean flee = false;
	
	public AlienShip(SpaceEscapeGame g, SpaceScreen screen, Sprite s, Vector2 os) {
		super(g, s);
		this.screen = screen;
		health = 100;
		blinkTime = 0;
		stealTime = Constants.STEAL_DELAY;
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
	    Utils.mainBodies.attachFixture(body, "attacker", fd, sprite.getWidth());
	    modelOrigin = Utils.mainBodies.getOrigin("attacker", sprite.getWidth());
	    
	    body.setUserData(this);
	    body.setLinearDamping(1);
		body.setAngularDamping(0.2f);
	}
	
	public void rotate(float dr) {
		body.applyAngularImpulse(1000 * dr, true);
	}

	public void shoot() {
		Sprite bs = new Sprite(new Texture(Gdx.files.internal("art/projectile.png")));
		bs.setSize(12.5f, 16.375f);
		Vector2 pos = body.getWorldCenter().cpy().sub(new Vector2(8, 8));
		Vector2 offset = game.gameScreen.spaceship.body.getWorldCenter().cpy().sub(pos);
		Bullet b = new Bullet(game, bs);
		b.initBody(world, pos);
		b.body.setTransform(b.body.getPosition(), (offset.angle() - 90) * MathUtils.degRad);
		Vector2 force = offset.cpy().nor().scl(10000000);
		b.body.applyForce(force, b.body.getWorldCenter(), true);
		screen.entities.add(b);
		screen.attackAudio.play(0.3f);
	}
	
	@Override
	public void render() {
		float dt = Gdx.graphics.getDeltaTime();
		if(fleeTimer >= 0) {
			fleeTimer -= dt;
			if(fleeTimer <= 0) {
				flee = true;
			}
		}

		sprite.setColor(game.gameScreen.oC);
		blinkTime -= dt;
		if((blinkTime > 0 && blinkTime % 0.3 < 0.15) || blinkTime <= 0)
			super.render();
		
		if(Constants.ATTACK_MODE == 1) {
			Vector2 pPos = game.gameScreen.spaceship.body.getWorldCenter();
			
			if(pPos.dst(body.getWorldCenter()) < Constants.ATTACK_DIST)
				body.applyForce(pPos.cpy().sub(body.getWorldCenter()).nor().scl(-10000), body.getWorldCenter(), true);
			if(pPos.dst(body.getWorldCenter()) > Constants.ATTACK_DIST * 1.3)
				body.applyForce(pPos.cpy().sub(body.getWorldCenter()).nor().scl(flee ? -100000 : 10000), body.getWorldCenter(), true);
			
		} else if(Constants.ATTACK_MODE == 2) {
			Vector2 pPos = game.gameScreen.spaceship.body.getWorldCenter().add(offset);
			
			if(pPos.dst(body.getWorldCenter()) > 10)
				body.applyForce(pPos.cpy().sub(body.getWorldCenter()).nor().scl(flee ? -100000 : 10000), body.getWorldCenter(), true);
		}
		
		if(flee && game.gameScreen.spaceship.body.getWorldCenter().dst(body.getWorldCenter()) > Constants.ATTACK_START_DIST)
			game.gameScreen.toDestroy.add(this);
		
		if(game.gameScreen.spaceship.body.getWorldCenter().dst(body.getWorldCenter()) < Constants.ATTACK_DIST * 1.3) {
			stealTime -= Gdx.graphics.getDeltaTime();
			if(stealTime < 0) {
				boolean shoot = false;
				for(int i = game.gameScreen.resources.get(Constants.RESOURCE_WEAPONS).size - 1; i < Constants.TOTAL_RESOURCE[Constants.RESOURCE_WEAPONS]; i++)
					shoot = shoot || (MathUtils.random() < Constants.STEAL_PROB);

				if(shoot) {
					shoot();
				}
				stealTime = Constants.STEAL_DELAY;
			}
		}
	}
	
	public void hit(ResourceItem ri) {
		if(screen.toDestroy.contains(ri, true))
			return;
		screen.toDestroy.add(ri);
		if(ri.type <= Constants.NUM_RESOURCES) {
			health -= 50;
			blinkTime = 2;
			if(health <= 0) {
				game.gameScreen.explosionAudio.play(0.3f);
				this.kill();
				screen.toDestroy.add(this);
			}
		}
	}

}
