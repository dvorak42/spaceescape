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
	
	int testX = 50;
	int testY = 100;
	int testOffset = 100;
	
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
		y = Gdx.graphics.getHeight() - y;
		if (Math.pow(x-testX,2) + Math.pow(y-testY, 2) < 1296) {
			screen.selectedResource = Constants.RESOURCE_FOOD;
		} else if (Math.pow(x-testX,2) + Math.pow(y-(testY+testOffset), 2) < 1296) {
			screen.selectedResource = Constants.RESOURCE_OXYGEN;
		} else if (Math.pow(x-testX,2) + Math.pow(y-(testY+2*testOffset), 2) < 1296) {
			screen.selectedResource = Constants.RESOURCE_SANITY;
		} else if (Math.pow(x-testX,2) + Math.pow(y-(testY+3*testOffset), 2) < 1296) {
			screen.selectedResource = Constants.RESOURCE_POWER;
		} else {
//			if(x < Gdx.graphics.getWidth() / 2)
//				screen.spaceship.rotate(10);
//			else
//				screen.spaceship.rotate(-10);
		}
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
		
		ResourceItem ri = null;
		if(hit) {
			if (screen.selectedResource == Constants.RESOURCE_FOOD) {
				if(screen.foodResources.size > 0)
					ri = screen.foodResources.pop();
			} else if (screen.selectedResource == Constants.RESOURCE_OXYGEN) {
				if(screen.oxygenResources.size > 0)
					ri = screen.oxygenResources.pop();
			} else if (screen.selectedResource == Constants.RESOURCE_SANITY) {
				if(screen.sanityResources.size > 0)
					ri = screen.sanityResources.pop();
			} else if (screen.selectedResource == Constants.RESOURCE_POWER) {
				if(screen.powerResources.size > 0)
					ri = screen.powerResources.pop();
			}
		}
		if(ri != null) {
			screen.spaceship.toss(new Vector2(velocityX, -velocityY), ri);
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
