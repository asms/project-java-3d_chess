package com.a7m5.chess.editor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class ClickableComponent {
	private int xLocation, yLocation, width, height;	// Location fields.
	Texture enTexture, dnTexture;	// Textures.
	TextureRegion enTextureRegion, dnTextureRegion;	// Texture Reigons.
	Boolean componentSelected = false;

	public ClickableComponent(int x, int y, int width, int height, String enabledTexturePath, String disabledTexturePath) {
		// Load in the textures.
		Texture enTexture = new Texture(Gdx.files.internal(enabledTexturePath));
		enTexture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		enTextureRegion = new TextureRegion(enTexture, 0, 0, enTexture.getWidth(), enTexture.getHeight());
		
		Texture dnTexture = new Texture(Gdx.files.internal(disabledTexturePath));
		dnTexture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		dnTextureRegion = new TextureRegion(dnTexture, 0, 0, dnTexture.getWidth(), dnTexture.getHeight());
		// Set startup fields.
		xLocation = x;
		yLocation = y;
		this.width = width;
		this.height = height;
	}
	
	public void drawComponent(SpriteBatch batch){
		if(componentSelected){
			batch.draw(enTextureRegion, xLocation, yLocation, width, height);
		} else {
			batch.draw(dnTextureRegion, xLocation, yLocation, width, height);
		}
		
	}

	// Is this point a point in the component location.
	public boolean compClicked(int x, int y, int windowHeight, int windowWidth){
		y = 512*(windowHeight - y)/windowHeight;	// Y scaling for window resizes
		x = (int) ((double) x*((((double) 512+400)/((double) windowWidth))));	// X scaling for window resizes
		return (x > xLocation)&&(x < xLocation + width)&&(y > yLocation)&&(y < yLocation + height);	
	}

	public Boolean getComponentSelected() {
		return componentSelected;
	}

	public void setComponentSelected(Boolean componentSelected) {
		this.componentSelected = componentSelected;
	}
}