package com.se.spaceescape;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.input.GestureDetector.GestureListener;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.se.spaceescape.screens.SpaceScreen;

public class SpaceGestureListener implements GestureListener {
	SpaceScreen screen;
	Vector2 lastPress;
	
	int testX = 125;
	int testY = 150;
	int testOffset = 225;
	
	public SpaceGestureListener(SpaceScreen s) {
		screen = s;
	}
	
	@Override
	public boolean touchDown(float x, float y, int pointer, int button) {
		if(screen.paused)
			return false;
		
		lastPress = new Vector2(x, y);
		return false;
	}

	@Override
	public boolean tap(float x, float y, int count, int button) {
		if(screen.paused)
			return false;

		y = Gdx.graphics.getHeight() - y;
		
		if (Math.pow(x - (screen.zoomButton.getX() + screen.zoomButton.getWidth() / 2), 2) + 
				Math.pow(y - (screen.zoomButton.getY() + screen.zoomButton.getHeight() / 2), 2) < Math.pow(screen.zoomButton.getWidth() / 2, 2)) {
			if(screen.camera.zoom > Constants.DEFAULT_ZOOM)
				screen.camera.zoom = Constants.DEFAULT_ZOOM;
			else
				screen.camera.zoom = Constants.MAX_ZOOM;
			screen.camera.update();
		}
		
		if(screen.camera.zoom > Constants.DEFAULT_ZOOM)
			return false;
		
		if (Math.pow(x-testX,2) + Math.pow(y-testY, 2) < 10000) {
			screen.selectedResource = Constants.RESOURCE_SANITY;
		} else if (Math.pow(x-testX,2) + Math.pow(y-(testY+testOffset), 2) < 10000) {
			screen.selectedResource = Constants.RESOURCE_WEAPONS;
		} else if (Math.pow(x-testX,2) + Math.pow(y-(testY+2*testOffset), 2) < 10000) {
			screen.selectedResource = Constants.RESOURCE_FOOD;
		} else {
			for(ResourceGenerator rg : screen.generators) {
				Vector2 p1 = rg.getPosition();
				Vector2 p2 = rg.getPosition().cpy().add(rg.getSize());
				if(x > p1.x && x < p2.x && y > p1.y && y < p2.y) {
					rg.tap();
					screen.itemGetAudio.play(0.2f);
				}
			}
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
		if(screen.paused)
			return false;

		if(screen.camera.zoom > Constants.DEFAULT_ZOOM)
			return false;

		Vector2 worldPress = Utils.worldSpace(lastPress, screen.spaceship.body.getWorldCenter(), screen.camera.zoom / 1.5f);
		boolean hit = false;
		for(Fixture f : screen.spaceship.body.getFixtureList()) {
			if(f.testPoint(worldPress))
				hit = true;
		}
		
		ResourceItem ri = null;
		if(hit) {
			if (screen.selectedResource > 0 && screen.selectedResource <= Constants.NUM_RESOURCES) {
				if(screen.resources.get(screen.selectedResource).size > 0)
					ri = screen.resources.get(screen.selectedResource).pop();
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
		return false;
	}

	@Override
	public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2,
			Vector2 pointer1, Vector2 pointer2) {
		// TODO Auto-generated method stub
		return false;
	}

}
