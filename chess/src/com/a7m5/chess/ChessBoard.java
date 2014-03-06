package com.a7m5.chess;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

import com.a7m5.chess.chesspieces.Bishop;
import com.a7m5.chess.chesspieces.ChessOwner;
import com.a7m5.chess.chesspieces.ChessPiece;
import com.a7m5.chess.chesspieces.King;
import com.a7m5.chess.chesspieces.Knight;
import com.a7m5.chess.chesspieces.Pawn;
import com.a7m5.chess.chesspieces.Queen;
import com.a7m5.chess.chesspieces.Rook;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class ChessBoard implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7954652516619094585L;
	private static TextureRegion kingTextureRegion;
	private static TextureRegion pawnWhiteTextureRegion;
	private static TextureRegion pawnBlackTextureRegion;
	private static TextureRegion queenTextureRegion;
	private static TextureRegion knightTextureRegion;
	private static TextureRegion rookWhiteTextureRegion;
	private static TextureRegion rookBlackTextureRegion;
	private static TextureRegion bishopTextureRegion;

	private ChessPiece[][] chessPieces;
	private ChessPiece selectedChessPiece = null;
	private ChessOwner turnOwner;
	private ChessOwner checkedPlayer = null;
	private Vector2 whiteCursor = null;
	private Vector2 blackCursor = null;
	public static int actualBoardWidth = 512;
	public static int boardWidth = 8; //8 (traditional), 16 (large), 32 (extra large)
	public static int tileWidth = actualBoardWidth / boardWidth;
	
	public ChessBoard() {
		chessPieces = new ChessPiece[boardWidth][boardWidth];
	}
	
	public static void loadTextures() {
		Texture pawnWhiteTexture = new Texture(Gdx.files.internal("data/pawn-white.png"));
		pawnWhiteTexture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		pawnWhiteTextureRegion = new TextureRegion(pawnWhiteTexture, 0, 0, 64, 64);

		Texture pawnBlackTexture = new Texture(Gdx.files.internal("data/pawn-black.png"));
		pawnBlackTexture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		pawnBlackTextureRegion = new TextureRegion(pawnBlackTexture, 0, 0, 64, 64);


		Texture kingTexture = new Texture(Gdx.files.internal("data/king.png"));
		kingTexture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		kingTextureRegion = new TextureRegion(kingTexture, 0, 0, 64, 64);

		Texture queenTexture = new Texture(Gdx.files.internal("data/queen.png"));
		queenTexture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		queenTextureRegion = new TextureRegion(queenTexture, 0, 0, 64, 64);

		Texture knightTexture = new Texture(Gdx.files.internal("data/knight.png"));
		knightTexture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		knightTextureRegion = new TextureRegion(knightTexture, 0, 0, 64, 64);

		Texture rookWhiteTexture = new Texture(Gdx.files.internal("data/rook-white.png"));
		rookWhiteTexture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		rookWhiteTextureRegion = new TextureRegion(rookWhiteTexture, 0, 0, 64, 64);

		Texture rookBlackTexture = new Texture(Gdx.files.internal("data/rook-black.png"));
		rookBlackTexture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		rookBlackTextureRegion = new TextureRegion(rookBlackTexture, 0, 0, 64, 64);

		Texture bishopTexture = new Texture(Gdx.files.internal("data/bishop.png"));
		bishopTexture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		bishopTextureRegion = new TextureRegion(bishopTexture, 0, 0, 64, 64);
	}


	public void drawBoard(ShapeRenderer shapeRenderer) {
		boolean alt = true;
		shapeRenderer.setColor(Color.BLACK);
		shapeRenderer.rect(0,
				0,
				actualBoardWidth,
				actualBoardWidth,
				actualBoardWidth/2,
				actualBoardWidth/2,
				0);
		for(int y = 0; y < boardWidth; y++) {
			for(int x = 0; x < boardWidth/2; x++) {
				shapeRenderer.setColor(new Color(0.9f, 0.9f, 0.9f, 1));
				shapeRenderer.rect((2*x+(alt?1:0)) * tileWidth + 1,
						y*tileWidth + 1,
						tileWidth-2,
						tileWidth-2
						);

				shapeRenderer.setColor(new Color(0, 0.84f, 0.18f, 1));
				shapeRenderer.rect((2*x+(alt?0:1)) * tileWidth + 1,
						y*tileWidth + 1,
						tileWidth-2,
						tileWidth-2
						);
			}
			alt = !alt;
		}

		if(selectedChessPiece != null) {
			ArrayList<Vector2> possibleMoves = selectedChessPiece.getPossibleMoves();
			for(Vector2 possibleMove : possibleMoves) {
				shapeRenderer.setColor(Color.BLUE);
				shapeRenderer.rect((float) (possibleMove.getX() * tileWidth + 1),
						(float) (possibleMove.getY()*tileWidth + 1),
						tileWidth-2,
						tileWidth-2
						);
			}
			ArrayList<Vector2> possibleAttacks = selectedChessPiece.getPossibleAttacks();
			for(Vector2 possibleAttack : possibleAttacks) {
				shapeRenderer.setColor(Color.RED);
				shapeRenderer.rect((float) (possibleAttack.getX() * tileWidth + 1),
						(float) (possibleAttack.getY()*tileWidth + 1),
						tileWidth-2,
						tileWidth-2
						);
			}
		}

		shapeRenderer.rect(512,
				0,
				400,
				512,
				Color.LIGHT_GRAY,
				Color.LIGHT_GRAY,
				Color.WHITE,
				Color.WHITE
				);
	}

	public void drawPieces(SpriteBatch batch) {
		for(int y = 0; y < boardWidth; y++) {
			for(int x = 0; x < boardWidth; x++) {
				ChessPiece chessPiece = chessPieces[x][y];
				if(chessPiece != null) {
					TextureRegion textureRegion;
					if(chessPiece instanceof Pawn) {
						if(chessPiece.getOwner() == ChessOwner.WHITE) {
							textureRegion = pawnWhiteTextureRegion;
						} else {
							textureRegion = pawnBlackTextureRegion;
						}
					} else if(chessPiece instanceof King) {
						textureRegion = kingTextureRegion;
					} else if(chessPiece instanceof Queen) {
						textureRegion = queenTextureRegion;
					} else if(chessPiece instanceof Knight) {
						textureRegion = knightTextureRegion;
					} else if(chessPiece instanceof Rook) {
						if(chessPiece.getOwner() == ChessOwner.WHITE) {
							textureRegion = rookWhiteTextureRegion;
						} else {
							textureRegion = rookBlackTextureRegion;
						}
					} else if(chessPiece instanceof Bishop) {
						textureRegion = bishopTextureRegion;
					} else {
						break;
					}
					int positionX;
					int positionY;
					if(chessPiece.isAnimating()) {
						Vector2 animationPosition = chessPiece.getAnimationPosition();					Vector2 displacementVector = new Vector2(
								x*tileWidth - animationPosition.getX(),
								y*tileWidth - animationPosition.getY());
						if(displacementVector.getMagnitude() <= 3) {
							chessPiece.stopAnimation();
						} else {
							Vector2 unitVector = displacementVector.getUnitVector().multiply(chessPiece.getSpeed());
							animationPosition = animationPosition.add(unitVector);
							chessPiece.setAnimationPosition(animationPosition);
						}
						positionX = (int) Math.floor(animationPosition.getX());
						positionY = (int) Math.floor(animationPosition.getY());
					} else {
						positionX = x*tileWidth;
						positionY = y*tileWidth;
					}
					batch.draw(textureRegion, positionX, positionY, tileWidth, tileWidth);
				}
			}
		}
	}
	
	public void drawCursors(ShapeRenderer shapeRenderer) {
		if(whiteCursor != null) {
			shapeRenderer.setColor(Color.WHITE);
			shapeRenderer.circle((float) whiteCursor.getX(), (float) whiteCursor.getY(), 6);
		}
		if(blackCursor != null) {
			shapeRenderer.setColor(Color.BLACK);
			shapeRenderer.circle((float) blackCursor.getX(), (float) blackCursor.getY(), 6);
		}
	}


	public void addPiece(int x, int y, ChessPiece chessPiece) {
		chessPiece.register(this, new Vector2(x, y));
		chessPieces[x][y] = chessPiece;
	}

	public void start() {
		clear();
	}

	public void restart() {
		start();
	}

	public void clear() {
		chessPieces = new ChessPiece[boardWidth][boardWidth];
	}
	public static int getTileXFromXCoordinate(int x) {
		return (int) Math.floor((float) x / (float) tileWidth);
	}
	public static int getTileYFromYCoordinate(int y) {
		return (int) Math.floor((float) (actualBoardWidth - y )/ (float) tileWidth);
	}
	public ChessPiece getChessPieceByXY(int x, int y) throws IndexOutOfBoundsException {
		int tileX = getTileXFromXCoordinate(x);
		int tileY = getTileYFromYCoordinate(y);
		return chessPieces[tileX][tileY];
	}
	public ChessPiece getChessPieceByVector(Vector2 vector) {
		return chessPieces[(int) vector.getX()][(int) vector.getY()];
	}
	public void moveChessPiece(int tileX0, int tileY0, int tileX1, int tileY1) {
		chessPieces[tileX1][tileY1] = chessPieces[tileX0][tileY0];
		chessPieces[tileX0][tileY0] = null;
	}
	public ChessPiece getSelectedChessPiece() {
		return selectedChessPiece ;
	}
	public void setSelectedChessPiece(ChessPiece chessPiece) {
		if(chessPiece == null) {
			selectedChessPiece = null;
		} else if(chessPiece.getOwner() == GdxChessGame.getOwner()) {
			this.selectedChessPiece = chessPiece;
		}
	}
	public void moveChessPiece(Vector2 oldPosition, Vector2 newPosition) {
		ChessPiece chessPiece = chessPieces[(int) oldPosition.getX()][(int) oldPosition.getY()];
		if(chessPiece.getOwner() == ChessOwner.WHITE) {
			turnOwner  = ChessOwner.BLACK;
		} else {
			turnOwner = ChessOwner.WHITE;
		}
		chessPieces[(int) oldPosition.getX()][(int) oldPosition.getY()] = null;
		chessPiece.setPosition(newPosition);

		//Animation
		chessPiece.animate(oldPosition.multiply(tileWidth));

		chessPieces[(int) newPosition.getX()][(int) newPosition.getY()] = chessPiece;
		selectedChessPiece = null;
	}
	public void setChessPieces(ChessPiece[][] chessPieces) {
		this.chessPieces = chessPieces;
	}
	public void attackChessPiece(Vector2 vector1, Vector2 vector2) {
		ChessPiece attacker = chessPieces[(int) vector1.getX()][(int) vector1.getY()];
		if(attacker.getOwner() == ChessOwner.WHITE) {
			turnOwner  = ChessOwner.BLACK;
		} else {
			turnOwner = ChessOwner.WHITE;
		}
		chessPieces[(int) vector1.getX()][(int) vector1.getY()] = null;
		attacker.setPosition(vector2);
		chessPieces[(int) vector2.getX()][(int) vector2.getY()] = attacker;
		selectedChessPiece = null;
	}
	public int getBoardWidth() {
		return actualBoardWidth / tileWidth;
	}
	public void gameOver(int winner) {
		// TODO Auto-generated method stub

	}
	public void setTurnOwner(ChessOwner owner) {
		this.turnOwner = owner;
	}
	public ChessOwner getTurnOwner() {
		return turnOwner;
	}
	public ChessPiece[][] getChessPieces() {
		return chessPieces;
	}
	public void setCheck(ChessOwner opponent) {
		checkedPlayer = opponent;
	}
	public ChessOwner getCheckedPlayer() {
		return checkedPlayer;
	}
	public void setCursor(ChessOwner owner, Vector2 vector) {
		if(owner == ChessOwner.WHITE) {
			whiteCursor = vector;
		} else if(owner == ChessOwner.BLACK) {
			blackCursor = vector;
		}
	}
	
	public static void setBoardWidth(int boardWidth) {
		ChessBoard.boardWidth = boardWidth;
		tileWidth = actualBoardWidth / boardWidth;
	}
}
