package com.se.spaceescape;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;

public class SpaceContactListener implements ContactListener {

	@Override
	public void beginContact(Contact contact) {
		Body a = contact.getFixtureA().getBody();
		Body b = contact.getFixtureB().getBody();
		if(a.getUserData() instanceof Spaceship && b.getUserData() instanceof ResourceItem) {
			((Spaceship)a.getUserData()).acquire((ResourceItem)b.getUserData());
		} else if(b.getUserData() instanceof Spaceship && a.getUserData() instanceof ResourceItem) {
			((Spaceship)b.getUserData()).acquire((ResourceItem)a.getUserData());			
		}

		if(a.getUserData() instanceof Spaceship && b.getUserData() instanceof Bullet) {
			((Spaceship)a.getUserData()).steal((Bullet)b.getUserData());
		} else if(b.getUserData() instanceof Spaceship && a.getUserData() instanceof Bullet) {
			((Spaceship)b.getUserData()).steal((Bullet)a.getUserData());
		}

		if(a.getUserData() instanceof Spaceship && b.getUserData() instanceof Planet) {
			((Planet)b.getUserData()).visited();
		} else if(b.getUserData() instanceof Spaceship && a.getUserData() instanceof Planet) {
			((Planet)a.getUserData()).visited();
		}

		if(a.getUserData() instanceof ResourceItem && b.getUserData() instanceof AlienShip) {
			((AlienShip)b.getUserData()).hit((ResourceItem)a.getUserData());
		} else if(b.getUserData() instanceof ResourceItem && a.getUserData() instanceof AlienShip) {
			((AlienShip)a.getUserData()).hit((ResourceItem)b.getUserData());
		}
}

	@Override
	public void endContact(Contact contact) {
	}

	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {
	}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {
	}
}
