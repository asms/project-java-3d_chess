package com.a7m5.chess.chesspieces;

import com.a7m5.chess.Vector2;


public class Rook extends ChessPiece {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8281050694944104353L;

	public Rook(ChessOwner owner) {
		super(owner);
		Vector2[] rookMovementDirectionVectors = {
				new Vector2(0, 1),
				new Vector2(0, -1),
				new Vector2(1, 0),
				new Vector2(-1, 0)
			};
		
		movementDirectionVectors = rookMovementDirectionVectors;
		attackDirectionVectors = movementDirectionVectors;
	}
}
