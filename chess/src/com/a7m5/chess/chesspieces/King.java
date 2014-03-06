package com.a7m5.chess.chesspieces;

import com.a7m5.chess.GdxChessGame;
import com.a7m5.chess.Vector2;


public class King extends ChessPiece {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8239258843260705743L;

	public King(ChessOwner owner) {
		super(owner);
		Vector2[] kingMovementVectors = {
				new Vector2(1, -1),
				new Vector2(1, 0),
				new Vector2(1, 1),
				
				new Vector2(0, 1),
				new Vector2(0, -1),
				
				new Vector2(-1, -1),
				new Vector2(-1, 0),
				new Vector2(-1, 1)
			};
		
		movementVectors = kingMovementVectors;
		attackVectors = movementVectors;
	}
	
	public void onClick() {
		ChessPiece selectedChessPiece= board.getSelectedChessPiece();
		if(selectedChessPiece == null) {
			System.out.println("Chess piece selected.");
			board.setSelectedChessPiece(this);
		} else {
			boolean attacked = selectedChessPiece.tryAttack(this);
			if(attacked) {
				GdxChessGame.getClient().sendGameOver();
			} else {
				board.setSelectedChessPiece(this);
			}
		}
	}

}
