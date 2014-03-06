package com.a7m5.chess.chesspieces;

import java.io.Serializable;
import java.util.ArrayList;

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
	public Vector2[] attackDirectionVectors = null;
	public Vector2[] movementDirectionVectors = null;
	public Vector2[] specialMovementVectors = null;
	public Vector2[] movementVectors = null;
	public Vector2[] attackVectors = null;

	public ChessPiece(ChessOwner owner) {
		this.owner = owner;
	}

	public void register(ChessBoard chessBoard, Vector2 position) {
		this.board = chessBoard;
		this.position = position;
	}

	public void onClick() {
		ChessPiece selectedChessPiece= board.getSelectedChessPiece();
		if(selectedChessPiece == null) {
			if(getOwner() == GdxChessGame.getOwner()) {
				board.setSelectedChessPiece(this);
			}
		} else {
			boolean attacked = selectedChessPiece.tryAttack(this);
			if(attacked) {
				GdxChessGame.getClient().sendAttack(selectedChessPiece.getPosition(), getPosition());
			} else {
				if(getOwner() == GdxChessGame.getOwner()) {
					board.setSelectedChessPiece(this);
				}
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

	public ArrayList<Vector2> getPossibleMoves() {
		ArrayList<Vector2> possibleMoves = new ArrayList<Vector2>();
		if(movementDirectionVectors != null) {
			for(Vector2 movementVector : movementDirectionVectors) {
				if(owner == ChessOwner.WHITE) {
					movementVector = movementVector.multiplyY(-1);
				}
				for(int i = 1; i < board.getBoardWidth() - 1; i++) {
					Vector2 testVector = getPosition().add(movementVector.multiply(i));
					try {
						if(board.getChessPieceByVector(testVector) == null) {
							possibleMoves.add(testVector);
						} else {
							break;
						}
					} catch(Exception e) {
						break;
					}
				}

			}
		}
		if(movementVectors != null) {
			for(Vector2 movementVector : movementVectors) {
				System.out.println("asdf");
				if(owner == ChessOwner.WHITE) {
					movementVector = movementVector.multiplyY(-1);
				}
				Vector2 testVector = getPosition().add(movementVector);
				try {
					if(board.getChessPieceByVector(testVector) == null) {
						possibleMoves.add(testVector);
					}
				} catch(Exception e) {}
			}
		}
		return possibleMoves;
	}

	public boolean tryMove(Vector2 newPosition) {
		ArrayList<Vector2> possibleMoves = getPossibleMoves();
		for(Vector2 possibleMove : possibleMoves) {
			System.out.println("try" + newPosition.getX() + newPosition.getY());
			System.out.println("can do" + possibleMove.getX() + possibleMove.getY());
			if(newPosition.equals(possibleMove)) {
				return true;
			}
		}
		return false;
	}

	public boolean tryAttack(ChessPiece targetChessPiece) {
		if(owner != targetChessPiece.owner) {
			if(attackDirectionVectors != null) {
				for(Vector2 attackVector : attackDirectionVectors) {
					if(owner == ChessOwner.WHITE) {
						attackVector = attackVector.multiplyY(-1);
					}
					for(int i = 1; i < board.getBoardWidth() - 1; i++) {
						Vector2 testVector = getPosition().add(attackVector.multiply(i));
						try {
							if(board.getChessPieceByVector(testVector) != null) {
								if(testVector.equals(targetChessPiece.getPosition())) {
									return true;
								} else {
									break;
								}
							}
						} catch(Exception e) {
							break;
						}
					}

				}
			}
			if(attackVectors != null) {
				for(Vector2 attackVector : attackVectors) {
					if(owner == ChessOwner.WHITE) {
						attackVector = attackVector.multiplyY(-1);
					}
					if(getPosition().add(attackVector).equals(targetChessPiece.getPosition())) {
						return true;
					}
				}
			}
		}
		return false;
	}
}
