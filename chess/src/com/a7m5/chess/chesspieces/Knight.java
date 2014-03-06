package com.a7m5.chess.chesspieces;

import com.a7m5.chess.Vector2;


public class Knight extends ChessPiece {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7813442088651475283L;

	public Knight(ChessOwner owner) {
		super(owner);
		Vector2[] knightMovementVectors = {
				new Vector2(2, -1),
				new Vector2(2, 1),
				
				new Vector2(-2, -1),
				new Vector2(-2, 1),
				
				new Vector2(1, -2),
				new Vector2(1, 2),
				
				new Vector2(-1, -2),
				new Vector2(-1, 2)
				
			};
		
		movementVectors = knightMovementVectors;
		attackVectors = movementVectors;
	}

}
