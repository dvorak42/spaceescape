package com.se.spaceescape;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;

public class AlertEntity extends Entity {
	float lifespan;
	public int type;
	Sprite plus;
	
	public AlertEntity(SpaceEscapeGame g, Sprite s, int type) {
		super(g, s);
		plus = Constants.PLUS_SPRITE;
		this.type = type;
		lifespan = 1.0f;
	}
	
	@Override
	public void render() {
		lifespan -= Gdx.graphics.getDeltaTime();
		if(lifespan < 0) {
			setActive(false);
			setVisible(false);
			game.gameScreen.hovering.removeValue(this, true);
			return;
		}
		
		if(active) {
			elapsedTime += Gdx.graphics.getDeltaTime();
		}
		
		plus.setSize(getSize().x, getSize().y);
		plus.setPosition(sprite.getX(), sprite.getY() + ((1 - lifespan) * 32));
		sprite.setPosition(sprite.getX() + getSize().x - 4, sprite.getY() + ((1 - lifespan) * 32));
		plus.draw(game.hudBatch, 1 - (float)Math.sqrt(1 - lifespan));
		sprite.draw(game.hudBatch, 1 - (float)Math.sqrt(1 - lifespan));
	}
}
