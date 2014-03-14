package com.a7m5.chess;

import java.io.Serializable;

import com.badlogic.gdx.graphics.Color;

public class Tile implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2052093311755067581L;
	Color color;
	Vector2 position;
	
	public Color getColor() {
		return color;
	}
	
	public void setColor(Color color) {
		this.color = color;
	}
	
	public Vector2 getPosition() {
		return position;
	}
	
	public void setPosition(Vector2 position) {
		this.position = position;
	}
}
