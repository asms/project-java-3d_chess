package com.a7m5.chess.chesspieces;

import com.a7m5.chess.Vector2;

public abstract interface ChessPieceInterface {
	public Vector2[] specialMovementVectors = null;
	public Vector2[] movementVectors = null;
	public Vector2[] attackVectors = null;
	
	public boolean tryMove(Vector2 newPosition);
	
}
