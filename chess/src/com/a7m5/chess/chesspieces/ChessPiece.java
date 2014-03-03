package com.a7m5.chess.chesspieces;

import java.io.Serializable;

import com.a7m5.chess.ChessBoard;
import com.a7m5.chess.GdxChessGame;
import com.a7m5.chess.Vector2;

public abstract class ChessPiece implements Serializable, ChessPieceInterface {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6131164911961928291L;
	protected ChessOwner owner;
	protected ChessBoard board;
	private Vector2 position;
	
	public ChessPiece(ChessOwner owner) {
		this.owner = owner;
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
		} else {
			boolean attacked = selectedChessPiece.tryAttack(this);
			if(attacked) {
				GdxChessGame.getClient().sendAttack(selectedChessPiece.getPosition(), getPosition());
			} else {
				board.setSelectedChessPiece(this);
			}
		}
	}

	public void onNullTileClicked(int x, int y) {
		int tileX = ChessBoard.getTileXFromXCoordinate(x);
		int tileY = ChessBoard.getTileYFromYCoordinate(y);
		Vector2 tileClicked = new Vector2(tileX, tileY);
		board.setSelectedChessPiece(null);
		boolean moved = tryMove(tileClicked);
		if(moved) {
			GdxChessGame.getClient().sendMove(getPosition(), tileClicked);
		}
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

	public ChessOwner getOwner() {
		return owner;
	}
}
