package com.se.spaceescape;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

public class ResourceItem extends PhysicalEntity {
	int type;
	float mass;
	public String name;
	public boolean leaving;
	
	public ResourceItem(SpaceEscapeGame g, Sprite s, int rType, String n) {
		super(g, s);
		type = rType;
		mass = 100;
		name = n;
		leaving = false;
	}
	
	@Override
	public void initBody(World w, Vector2 pos) {
		super.initBody(w, pos);

		BodyDef bd = new BodyDef();
		bd.position.set(pos);
		bd.type = BodyType.DynamicBody;

		body = w.createBody(bd);

		FixtureDef fd = new FixtureDef();
		fd.density = 1.0f; 
		fd.friction = 0.05f;
		// TODO: Decide whether to use.
		fd.isSensor = true;
	    Utils.mainBodies.attachFixture(body, name, fd, sprite.getWidth());
	    modelOrigin = Utils.mainBodies.getOrigin(name, sprite.getWidth());

	    body.setAngularDamping(2.0f);
	    body.createFixture(fd);
		body.setUserData(this);
		sprite.setSize(60, 60);
	}
	
	@Override
	public void render() {
		sprite.setColor(game.gameScreen.oC);
		super.render();
		
		if(body.getWorldCenter().dst(game.gameScreen.spaceship.body.getWorldCenter()) > game.gameScreen.spaceship.getSize().x)
			leaving = false;
	}
}
