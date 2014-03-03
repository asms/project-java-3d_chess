package com.a7m5.chess.chesspieces;

import com.a7m5.chess.Vector2;


public class Pawn extends ChessPiece {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2328484679658320116L;

	public Pawn(ChessOwner owner) {
		super(owner);
		
	}

	public static final Vector2[] specialMovementVectors = {
		new Vector2(0, 2)
	};
	
	public static final Vector2[] movementVectors = {
		new Vector2(0, 1)
	};
	
	public static final Vector2[] attackVectors = {
		new Vector2(-1, 1),
		new Vector2(1, 1)
	};
	
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
