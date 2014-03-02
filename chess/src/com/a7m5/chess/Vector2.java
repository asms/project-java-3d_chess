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

	public Vector2 add(Vector2 vector) {
		return new Vector2(getX() + vector.getX(), getY() + vector.getY());
	}
	
	public boolean equals(Vector2 vector) {
		return (getX() == vector.getX() && getY() == vector.getY());
	}
	
	public Vector2 multiplyX(int scalar) {
		return new Vector2(getX() * scalar, getY());
	}
	
	public Vector2 multiplyY(int scalar) {
		return new Vector2(getX(), getY() * scalar);
	}
	
	public Vector2 multiply(int scalar) {
		return new Vector2(getX() * scalar, getY() * scalar);
	}
}
