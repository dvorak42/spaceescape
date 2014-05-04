package com.se.spaceescape;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

public class Bullet extends PhysicalEntity {
	public Bullet(SpaceEscapeGame g, Sprite s) {
		super(g, s);
	}
	
	@Override
	public void initBody(World w, Vector2 pos) {
		super.initBody(w, pos);

		BodyDef bd = new BodyDef();
		bd.position.set(pos);
		bd.type = BodyType.DynamicBody;
		bd.bullet = true;
		
		body = w.createBody(bd);

		FixtureDef fd = new FixtureDef();
		fd.density = 1.0f; 
		fd.isSensor = true;
	    Utils.mainBodies.attachFixture(body, "projectile", fd, sprite.getWidth());
	    modelOrigin = Utils.mainBodies.getOrigin("projectile", sprite.getWidth());
	    body.createFixture(fd);
		body.setUserData(this);
	}
	
	@Override
	public void render() {
		sprite.setColor(game.gameScreen.oC);
		super.render();
	}
}
