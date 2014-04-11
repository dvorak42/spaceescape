package com.se.spaceescape;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.utils.Array;
import com.se.spaceescape.screens.WinScreen;

public class Planet extends PhysicalEntity {
	public float altitude;
	public String fname;
	Array<PhysicalEntity> orbitters;
	public boolean endPlanet;
	
	public Planet(SpaceEscapeGame g, Sprite s, float alt, String fn) {
		super(g, s);
		altitude = alt;
		fname = fn;
		orbitters = new Array<PhysicalEntity>();
	}
	
	@Override
	public void initBody(World w, Vector2 pos) {
		super.initBody(w, pos);

		BodyDef bd = new BodyDef();
		bd.position.set(pos);
		bd.type = BodyType.DynamicBody;

		body = w.createBody(bd);

		FixtureDef fd = new FixtureDef();
		fd.density = 10000.0f; 
		fd.friction = 10000.00f;
	    Utils.mainBodies.attachFixture(body, fname, fd, sprite.getWidth());

		body.createFixture(fd);
		body.setUserData(this);
	}
	
	public Array<PhysicalEntity> getOrbitters() {
		return orbitters;
	}

	public void addOrbitter(ResourceItem ri) {
		ri.initBody(world, body.getWorldCenter().cpy().add(new Vector2(altitude, 0).rotate(MathUtils.random(360f))));
		orbitters.add(ri);
	}
	
	public void runOrbit() {
		for(PhysicalEntity p : orbitters) {
			Body b = p.body;
			Vector2 p1 = b.getWorldCenter();
			Vector2 p2 = body.getWorldCenter();
			Vector2 dp = p2.cpy().sub(p1);
			float force = 10000000 * b.getMass() / dp.len2();
			if(dp.len() > altitude)
				b.applyForce(dp.nor().scl(force), p1, true);
			else
				b.applyForce(dp.nor().scl(0.25f * force).rotate(115), p1, true);
		}
	}
	
	public void visited() {
		if(endPlanet)
			game.setScreen(new WinScreen(game, game.gameScreen));
	}
}
