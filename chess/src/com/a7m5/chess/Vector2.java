package com.a7m5.chess;

import java.io.Serializable;

public class Vector2 implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7695473131801712959L;
	private double x;
	private double y;
	
	public Vector2(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public void setX(int x) {
		this.x = x;
	}
	
	public void setY(int y) {
		this.y = y;
	}
	
	public Vector2(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	public double getX() {
		return x;
	}
	
	public double getY() {
		return y;
	}
	
	public void setX(double x) {
		this.x = x;
	}
	
	public void setY(double y) {
		this.y = y;
	}

	public Vector2 add(Vector2 vector) {
		return new Vector2(getX() + vector.getX(), getY() + vector.getY());
	}
	
	public boolean equals(Vector2 vector) {
		return (getX() == vector.getX() && getY() == vector.getY());
	}
	
	public Vector2 multiplyX(double scalar) {
		return new Vector2(getX() * scalar, getY());
	}
	
	public Vector2 multiplyY(double scalar) {
		return new Vector2(getX(), getY() * scalar);
	}
	
	public Vector2 multiply(double scalar) {
		return new Vector2(getX() * scalar, getY() * scalar);
	}

	public Vector2 getUnitVector() {
		return new Vector2(getX()/getMagnitude(), getY()/getMagnitude());
	}

	public double getMagnitude() {
		return Math.sqrt(getX()*getX() + getY()*getY());
	}
}
