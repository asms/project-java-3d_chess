package com.a7m5.chess.chesspieces;

import com.a7m5.chess.Vector2;


public class Queen extends ChessPiece {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7808494174957885644L;

	public Queen(ChessOwner owner) {
		super(owner);
		Vector2[] queenMovementDirectionVectors = {
				new Vector2(0, 1),
				new Vector2(0, -1),
				new Vector2(1, 0),
				new Vector2(-1, 0),

				// Diagonals

				new Vector2(1, 1),
				new Vector2(-1, -1),
				new Vector2(-1, 1),
				new Vector2(1, -1)
			};
		
		movementDirectionVectors = queenMovementDirectionVectors;
		attackDirectionVectors = movementDirectionVectors;
		
	}

}
