package com.a7m5.chess.chesspieces;

import com.a7m5.chess.Vector2;


public class Rook extends ChessPiece {

	public Rook(ChessOwner owner) {
		super(owner);
	}

	public static final Vector2[] specialMovementVectors = {};
	
	public static final Vector2[] movementVectors = {
		new Vector2(0, 1),
		new Vector2(0, 2),
		new Vector2(0, 3),
		new Vector2(0, 4),
		new Vector2(0, 5),
		new Vector2(0, 6),
		new Vector2(0, 7),
		
		new Vector2(0, -1),
		new Vector2(0, -2),
		new Vector2(0, -3),
		new Vector2(0, -4),
		new Vector2(0, -5),
		new Vector2(0, -6),
		new Vector2(0, -7),
		
		new Vector2(1, 0),
		new Vector2(2, 0),
		new Vector2(3, 0),
		new Vector2(4, 0),
		new Vector2(5, 0),
		new Vector2(6, 0),
		new Vector2(7, 0),
		
		new Vector2(-1, 0),
		new Vector2(-2, 0),
		new Vector2(-3, 0),
		new Vector2(-4, 0),
		new Vector2(-5, 0),
		new Vector2(-6, 0),
		new Vector2(-7, 0)
	};
	
	public static final Vector2[] attackVectors = movementVectors;
	
	public boolean tryMove(Vector2 newPosition) {
		for(Vector2 movementVector : movementVectors) {
			if(owner == ChessOwner.TOP) {
				movementVector = movementVector.multiplyY(-1);
			}
			if(getPosition().add(movementVector).equals(newPosition)) {
				return true;
			}
		}
		return false;
	}

}
