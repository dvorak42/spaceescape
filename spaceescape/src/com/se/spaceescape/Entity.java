package com.se.spaceescape;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;

public class Entity {
	SpaceEscapeGame game;
	
	public Sprite sprite;
	boolean active = true;
	boolean visible = true;
	float elapsedTime;
	
	public Entity(SpaceEscapeGame g, Sprite s) {
		game = g;
		sprite = s;
		elapsedTime = 0.0f;
	}
	
	public boolean active() {
		return active;
	}
	
	public void setActive(boolean a) {
		active = a;
	}
	
	public boolean visible() {
		return visible;
	}
	
	public void setVisible(boolean v) {
		visible = v;
	}
	
	public void render() {
		if(active) {
			elapsedTime += Gdx.graphics.getDeltaTime();
		}
		
		sprite.draw(game.batch);
	}
	
	public void setPosition(Vector2 pos) {
		sprite.setPosition(pos.x, pos.y);
	}

	public Vector2 getPosition() {
		return new Vector2(sprite.getX(), sprite.getY());
	}
	        
	public void setSize(Vector2 size) {
		sprite.setSize(size.x, size.y);
	}

	public Vector2 getSize() {
		return new Vector2(sprite.getWidth(), sprite.getHeight());
	}
	
	public void setRotation(float r) {
		sprite.setRotation(r);
	}

	public float getRotation() {
		return sprite.getRotation();
	}
	        
	public void kill()
	{
		setActive(false);
	}
}
