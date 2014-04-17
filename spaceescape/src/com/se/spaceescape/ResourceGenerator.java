package com.se.spaceescape;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class ResourceGenerator extends Entity {
	float delay;
	int type;
	
	public ResourceGenerator(SpaceEscapeGame g, Sprite s, int type) {
		super(g, s);
		sprite = s;
		delay = 0.0f;
		this.type = type;
	}
	
	public void tap() {
		if(delay > 0)
			return;
		Utils.addItem(game, type);
		delay = Constants.GENERATOR_DELAY;
	}
	
	public void render() {
		delay -= Gdx.graphics.getDeltaTime();
		if(delay < 0)
			delay = 0;
		
		if(active) {
			elapsedTime += Gdx.graphics.getDeltaTime();
		}
		
		sprite.setColor(Color.GRAY);
		sprite.draw(game.hudBatch);
		sprite.setColor(Color.WHITE);
		int w = (int)sprite.getWidth();
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
