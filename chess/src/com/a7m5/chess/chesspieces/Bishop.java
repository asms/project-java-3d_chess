package com.a7m5.chess.chesspieces;

import com.a7m5.chess.Vector2;


public class Bishop extends ChessPiece {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8899602072817271337L;

	public Bishop(ChessOwner owner) {
		super(owner);
		Vector2[] bishopMovementDirectionVectors = {
				new Vector2(1, 1),
				new Vector2(-1, -1),
				new Vector2(-1, 1),
				new Vector2(1, -1)
			};
		movementDirectionVectors = bishopMovementDirectionVectors;
		attackDirectionVectors = movementDirectionVectors;
		
	}

}
