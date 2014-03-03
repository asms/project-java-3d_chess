package com.a7m5.chess.chesspieces;

import com.a7m5.chess.Vector2;


public class King extends ChessPiece {

	public King(ChessOwner owner) {
		super(owner);
	}

	public static final Vector2[] specialMovementVectors = {};
	
	public static final Vector2[] movementVectors = {
		new Vector2(1, -1),
		new Vector2(1, 0),
		new Vector2(1, 1),
		
		new Vector2(0, 1),
		new Vector2(0, -1),
		
		new Vector2(-1, -1),
		new Vector2(-1, 0),
		new Vector2(-1, 1)
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
	
	public boolean tryAttack(ChessPiece targetChessPiece) {
		if(owner != targetChessPiece.owner) {
			for(Vector2 attackVector : attackVectors) {
				if(owner == ChessOwner.TOP) {
					attackVector = attackVector.multiplyY(-1);
				}
				if(getPosition().add(attackVector).equals(targetChessPiece.getPosition())) {
					return true;
				}
			}
		}
		return false;
	}

}
