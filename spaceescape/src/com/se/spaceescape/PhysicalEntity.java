package com.se.spaceescape;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;

public class PhysicalEntity extends Entity {
	public World world;
	public Body body;
	Vector2 modelOrigin = Vector2.Zero;

	public PhysicalEntity(SpaceEscapeGame g, Sprite s) {
		super(g, s);
	}

	public void initBody(World w, Vector2 pos) {
		world = w;
	}
	
	@Override
	public void setPosition(Vector2 pos) {
		body.setTransform(pos, body.getAngle());
	}

	@Override
	public Vector2 getPosition() {
		return body.getPosition();
	}
	        
	@Override
	public void setSize(Vector2 size) {
		sprite.setSize(size.x, size.y);
	}

	@Override
	public Vector2 getSize() {
		return new Vector2(sprite.getWidth(), sprite.getHeight());
	}
	
	@Override
	public void setRotation(float r) {
		body.setTransform(body.getPosition(), r);
	}

	@Override
	public float getRotation() {
		return body.getAngle();
	}

	@Override
	public void render() {
		Vector2 spritePos = body.getPosition().sub(modelOrigin);
		
		sprite.setPosition(spritePos.x, spritePos.y);
		sprite.setOrigin(modelOrigin.x, modelOrigin.y);
		sprite.setRotation(body.getAngle() * MathUtils.radiansToDegrees);

		super.render();
	}
}
