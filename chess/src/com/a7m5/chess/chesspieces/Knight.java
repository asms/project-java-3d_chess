package com.a7m5.chess.chesspieces;

import com.a7m5.chess.Vector2;


public class Knight extends ChessPiece {

	public Knight(ChessOwner owner) {
		super(owner);
	}

	public static final Vector2[] specialMovementVectors = {};
	
	public static final Vector2[] movementVectors = {
		new Vector2(2, -1),
		new Vector2(2, 1),
		
		new Vector2(-2, -1),
		new Vector2(-2, 1),
		
		new Vector2(1, -2),
		new Vector2(1, 2),
		
		new Vector2(-1, -2),
		new Vector2(-1, 2)
		
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
