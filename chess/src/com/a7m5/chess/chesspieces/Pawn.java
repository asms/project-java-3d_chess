package com.a7m5.chess.chesspieces;

import com.a7m5.chess.Vector2;


public class Pawn extends ChessPiece {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2328484679658320116L;

	public Pawn(ChessOwner owner) {
		super(owner);
		Vector2[] pawnSpecialMovementVectors = {
				new Vector2(0, 2)
			};
		Vector2[] pawnMovementVectors = {
				new Vector2(0, 1)
			};
		Vector2[] pawnAttackVectors = {
				new Vector2(-1, 1),
				new Vector2(1, 1)
			};
		movementVectors = pawnMovementVectors;
		attackVectors = pawnAttackVectors;
		specialMovementVectors = pawnSpecialMovementVectors;
		
	}
}
