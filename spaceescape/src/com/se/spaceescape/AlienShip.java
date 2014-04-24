package com.se.spaceescape;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
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

	public AlienShip(SpaceEscapeGame g, SpaceScreen screen, Sprite s) {
		super(g, s);
		this.screen = screen;
		health = 100;
		blinkTime = 0;
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

	@Override
	public void render() {
		blinkTime -= Gdx.graphics.getDeltaTime();
		if((blinkTime > 0 && blinkTime % 0.3 < 0.15) || blinkTime <= 0)
			super.render();
	}
	
	public void hit(ResourceItem ri) {
		if(screen.toDestroy.contains(ri, true))
			return;
		screen.toDestroy.add(ri);
		if(ri.type == Constants.RESOURCE_WEAPONS) {
			health -= 50;
			blinkTime = 2;
			if(health <= 0) {
				this.kill();
				screen.toDestroy.add(this);
			}
		}
	}

}
