package com.a7m5.chess;

import java.io.Serializable;

public class Vector2 implements Serializable {
	
	private int x;
	private int y;
	
	public Vector2(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public void setX(int x) {
		this.x = x;
	}
	
	public void setY(int y) {
		this.y = y;
	}
}
