package com.se.spaceescape;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

public class ResourceItem extends Entity {
	int type;
	float mass;
	World world;
	Body body;
	
	public ResourceItem(SpaceEscapeGame g, Sprite s, int rType) {
		super(g, s);
		type = rType;
		mass = 100;
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
		fd.density = mass / (2 * MathUtils.PI * circle.getRadius() * circle.getRadius()); 
		fd.friction = 0.05f;

		body.createFixture(fd);
		body.setUserData(this);
		
		circle.dispose();
	}
}
