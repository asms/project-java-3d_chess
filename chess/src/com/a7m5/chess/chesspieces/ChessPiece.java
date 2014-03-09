package com.a7m5.chess.chesspieces;

import java.io.Serializable;
import java.util.ArrayList;

import com.a7m5.chess.ChessBoard;
import com.a7m5.chess.ChessGame3D;
import com.a7m5.chess.Vector2;
import com.a7m5.chess.editor.ChessBoardPalette;

public class ChessPiece implements Serializable, ChessPieceInterface {

	private static final long serialVersionUID = 6131164911961928291L;
	protected String pieceName;
	protected int uniquePieceID;
	protected ChessOwner owner;

	protected ChessBoard board;
	protected ChessBoardPalette palette;
	private Vector2 position;
	private boolean animating = false;
	private Vector2 animationPosition;
	protected Vector2[] attackDirectionVectors = null;
	protected Vector2[] movementDirectionVectors = null;
	protected Vector2[] specialMovementVectors = null;
	protected Vector2[] movementVectors = null;
	protected Vector2[] attackVectors = null;
	protected String blackArtFile = "";
	protected String whiteArtFile = "";
	protected String NPCArtFile = "";
	
	public ChessPiece(ChessOwner owner) {
		this.owner = owner;
	}

	public ChessPiece(String pieceName, int uniquePieceID,
			Vector2[] attackDirectionVectors,
			Vector2[] movementDirectionVectors,
			Vector2[] specialMovementVectors, Vector2[] movementVectors,
			Vector2[] attackVectors,
			String blackArtFile,
			String whiteArtFile,
			String NPCArtFile) {
		super();
		this.pieceName = pieceName;
		this.uniquePieceID = uniquePieceID;
		this.attackDirectionVectors = attackDirectionVectors;
		this.movementDirectionVectors = movementDirectionVectors;
		this.specialMovementVectors = specialMovementVectors;
		this.movementVectors = movementVectors;
		this.attackVectors = attackVectors;
		this.blackArtFile = blackArtFile;
		this.whiteArtFile = whiteArtFile;
		this.NPCArtFile = NPCArtFile;
	}

	public String getPieceName() {
		return pieceName;
	}

	public int getUniquePieceID() {
		return uniquePieceID;
	}

	public void register(ChessBoard chessBoard, Vector2 position) {
		this.board = chessBoard;
		this.position = position;
	}

	public void register(ChessBoardPalette chessPalette, Vector2 position) {
		this.palette = chessPalette;
		this.position = position;
	}

	public boolean isAnimating() {
		return animating;
	}

	public void animate(Vector2 animationPosition) {
		animating = true;
		this.animationPosition = animationPosition;
	}

	public void setAnimationPosition(Vector2 animationPosition) {
		this.animationPosition = animationPosition;
	}

	public Vector2 getAnimationPosition() {
		return animationPosition;
	}

	public void stopAnimation() {
		this.animating = false;
	}

	public void onClick() {
		ChessPiece selectedChessPiece= board.getSelectedChessPiece();
		if(selectedChessPiece == null) {
			if(getOwner() == ChessGame3D.getOwner()) {
				board.setSelectedChessPiece(this);
			}
		} else {
			boolean attacked = selectedChessPiece.tryAttack(this);
			if(attacked) {
				ChessGame3D.getClient().sendAttack(selectedChessPiece.getPosition(), getPosition());
			} else {
				if(getOwner() == ChessGame3D.getOwner()) {
					board.setSelectedChessPiece(this);
				}
			}
		}
	}

	public void onNullTileClicked(int x, int y) {
		int tileX = ChessBoard.getTileFromCoordinate(x);
		int tileY = ChessBoard.getTileFromCoordinate(y);
		Vector2 tileClicked = new Vector2(tileX, tileY);
		board.setSelectedChessPiece(null);
		boolean moved = tryMove(tileClicked);
		if(moved) {
			ChessGame3D.getClient().sendMove(getPosition(), tileClicked);
		}
	}

	public Vector2 getPosition() {
		return position;
	}

	public void setX(int x) {
		position.setX(x);
	}

	public int getX() {
		return (int) position.getX();
	}

	public void setY(int y) {
		position.setY(y);
	}

	public int getY() {
		return (int) position.getY();
	}

	public void setPosition(Vector2 newPosition) {
		this.position = newPosition;
	}
	
	public void setOwner(ChessOwner owner) {
		this.owner = owner;
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

	public ArrayList<Vector2> getPossibleAttacks() {
		ArrayList<Vector2> possibleAttacks = new ArrayList<Vector2>();
		if(attackDirectionVectors != null) {
			for(Vector2 attackVector : attackDirectionVectors) {
				if(owner == ChessOwner.WHITE) {
					attackVector = attackVector.multiplyY(-1);
				}
				for(int i = 1; i < board.getBoardWidth() - 1; i++) {
					Vector2 testVector = getPosition().add(attackVector.multiply(i));
					try {
						ChessPiece testPiece = board.getChessPieceByVector(testVector);
						if(testPiece != null) {
							if(testPiece.getOwner() != ChessGame3D.getOwner()) {
								possibleAttacks.add(testVector);
							}
							break;
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
				Vector2 testVector = getPosition().add(attackVector);
				try {
					ChessPiece testPiece = board.getChessPieceByVector(testVector);
					if(testPiece != null) {
						if(testPiece.getOwner() != ChessGame3D.getOwner()) {
							possibleAttacks.add(testVector);
						}

					}
				} catch(Exception e) {}
			}
		}
		return possibleAttacks;
	}

	public double getSpeed() {
		return ChessBoard.tileWidth/4;
	}

	@Override
	public String toString() {
		return getX() + ", " + getY();
	}
	

	public String getBlackArtFile() {
		return blackArtFile;
	}

	public String getWhiteArtFile() {
		return whiteArtFile;
	}

	public String getNPCArtFile() {
		return NPCArtFile;
	}

	public Vector2[] getAttackDirectionVectors() {
		return attackDirectionVectors;
	}

	public Vector2[] getMovementDirectionVectors() {
		return movementDirectionVectors;
	}

	public Vector2[] getSpecialMovementVectors() {
		return specialMovementVectors;
	}

	public Vector2[] getMovementVectors() {
		return movementVectors;
	}

	public Vector2[] getAttackVectors() {
		return attackVectors;
	}
	// Copies all the info in this to create an independent clone that is not a pointer.
	public ChessPiece getClone(ChessOwner owner){
		ChessPiece temp = new ChessPiece(pieceName, uniquePieceID, attackDirectionVectors,
				movementDirectionVectors, specialMovementVectors, movementVectors,
				attackVectors, blackArtFile, whiteArtFile, NPCArtFile);
		temp.setOwner(owner);
		return temp;
	}

}
