package com.a7m5.chess.chesspieces;

import com.a7m5.chess.Vector2;

public abstract interface ChessPieceInterface {
	public boolean tryMove(Vector2 newPosition);
	public boolean tryAttack(ChessPiece chessPiece);
}
