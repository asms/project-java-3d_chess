package com.a7m5.chess.chesspieces;

import com.a7m5.chess.ChessBoard;
import com.a7m5.chess.GdxChessGame;
import com.a7m5.chess.Vector2;
import com.badlogic.gdx.graphics.g2d.Sprite;

public abstract class ChessPiece {
	
	private int ownerID;
	private ChessBoard board;
	private Vector2 position;
	
	public ChessPiece(int ownerID) {
		this.ownerID = ownerID;
	}

	public void register(ChessBoard chessBoard, int tileX, int tileY) {
		this.board = chessBoard;
		this.position = new Vector2(tileX, tileY);
	}
	
	public void onClick() {
		ChessPiece selectedChessPiece= board.getSelectedChessPiece();
		if(selectedChessPiece == null) {
			System.out.println("Chess piece selected.");
			board.setSelectedChessPiece(this);
		}
	}

	public void onNullTileClicked(int x, int y) {
		int tileX = ChessBoard.getTileXFromXCoordinate(x);
		int tileY = ChessBoard.getTileYFromYCoordinate(y);
		Vector2 tileClicked = new Vector2(tileX, tileY);
		board.setSelectedChessPiece(null);
		GdxChessGame.getClient().sendMove(this, tileClicked);
	}
	
	public Vector2 getPosition() {
		return position;
	}
	
	public void setX(int x) {
		position.setX(x);
	}
	
	public int getX() {
		return position.getX();
	}
	
	public void setY(int y) {
		position.setY(y);
	}
	
	public int getY() {
		return position.getY();
	}

	public void setPosition(Vector2 newPosition) {
		this.position = newPosition;
	}
}
