package com.se.spaceescape;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.input.GestureDetector.GestureListener;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.QueryCallback;
import com.se.spaceescape.screens.SpaceScreen;

public class SpaceGestureListener implements GestureListener {
	SpaceScreen screen;
	Vector2 lastPress;
	
	public SpaceGestureListener(SpaceScreen s) {
		screen = s;
	}
	
	@Override
	public boolean touchDown(float x, float y, int pointer, int button) {
		lastPress = new Vector2(x, y);
		return false;
	}

	@Override
	public boolean tap(float x, float y, int count, int button) {
		if(x < Gdx.graphics.getWidth() / 2)
			screen.spaceship.rotate(10);
		else
			screen.spaceship.rotate(-10);
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean longPress(float x, float y) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean fling(float velocityX, float velocityY, int button) {
		Vector2 worldPress = Utils.worldSpace(lastPress, screen.spaceship.body.getWorldCenter(), screen.camera.zoom / 1.5f);
		boolean hit = false;
		for(Fixture f : screen.spaceship.body.getFixtureList()) {
			if(f.testPoint(worldPress))
				hit = true;
		}
		if(hit) {
			ResourceItem ri = null;
			if(screen.resources.size > 0)
				ri = screen.resources.pop();
			if(ri != null) {
				screen.entities.add(ri);
				screen.spaceship.toss(new Vector2(velocityX, -velocityY), ri);
			}
			return true;
		}
		return false;
	}

	@Override
	public boolean pan(float x, float y, float deltaX, float deltaY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean panStop(float x, float y, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean zoom(float initialDistance, float distance) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2,
			Vector2 pointer1, Vector2 pointer2) {
		// TODO Auto-generated method stub
		return false;
	}

}
