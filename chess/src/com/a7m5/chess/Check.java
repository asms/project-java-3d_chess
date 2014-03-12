// Test Comment.

package com.a7m5.chess;

import com.a7m5.chess.chesspieces.ChessOwner;
import com.a7m5.chess.chesspieces.ChessPiece;

public class Check {
	private ChessBoard board;
	public ChessOwner player;
	public ChessOwner opponent;
	public ChessPiece playerKing;
	public ChessPiece opponentKing;
	public void check(ChessBoard board, ChessOwner player, ChessOwner opponent, Vector2 moveFrom, Vector2 moveTo) {
		this.board = board;
		this.player = player;
		this.opponent = opponent;
		findKings();
		//board.moveChessPiece(moveFrom, moveTo);
		
	}
	private void findKings() {
		for(ChessPiece[] rows : board.getChessPieces()) {
			for(ChessPiece piece : rows) {
				if(piece != null) {
					if(piece.getPieceName() == "King") {
						if(piece.getOwner() == player) {
							playerKing = piece;
						} else {
							opponentKing = piece;
						}
					}
				}
				
			}
		}
	}
}
