package com.se.spaceescape;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;

public class ResourceGenerator extends Entity {
	float delay;
	public int type;
	
	public ResourceGenerator(SpaceEscapeGame g, Sprite s, int type) {
		super(g, s);
		sprite = s;
		delay = 0.0f;
		this.type = type;
	}
	
	public void tap() {
		if(delay > 0 || !active)
			return;
		Utils.addItem(game, type, getPosition());
		delay = Constants.GENERATOR_DELAY;
	}
	
	public void render() {
		delay -= Gdx.graphics.getDeltaTime();
		if(delay < 0)
			delay = 0;
		
		if(active) {
			elapsedTime += Gdx.graphics.getDeltaTime();
		} else {
			sprite.setColor(game.gameScreen.tint(Color.RED));
			sprite.draw(game.hudBatch);
			return;
		}
		
		sprite.setColor(game.gameScreen.tint(Color.GRAY));
		sprite.draw(game.hudBatch);
		sprite.setColor(game.gameScreen.tint(Color.WHITE));
		int h = (int)sprite.getHeight();
		int w2 = sprite.getTexture().getWidth();
		int h2 = sprite.getTexture().getHeight();
		float s = 1.0f * h / h2;
		float offset = 1 - delay / Constants.GENERATOR_DELAY;
		game.hudBatch.draw(sprite.getTexture(), sprite.getX(), sprite.getY(), 0, 0, 
				w2, (int)(h2 * offset), s, s, 0, 
				0, h2 - (int)(h2 * offset), w2, (int)(h2 * offset), false, false);
	}
}
