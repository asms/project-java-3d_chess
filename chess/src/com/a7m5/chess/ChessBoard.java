package com.a7m5.chess;

import java.io.Serializable;
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
	public static int tileWidth = 64;
	private static int actualBoardWidth = tileWidth * 8;


	public ChessBoard() {
		chessPieces = new ChessPiece[8][8];
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
		for(int y = 0; y < 8; y++) {
			for(int x = 0; x < 4; x++) {
				
				shapeRenderer.setColor(new Color(0.8f, 1, 0.8f, 1));
				shapeRenderer.rect((2*x+(alt?1:0)) * 64 + 1,
						y*64 + 1,
						tileWidth-2,
						tileWidth-2
						);
				
				shapeRenderer.setColor(new Color(0, 0.84f, 0.18f, 1));
				shapeRenderer.rect((2*x+(alt?0:1)) * 64 + 1,
						y*64 + 1,
						tileWidth-2,
						tileWidth-2
						);
			}
			alt = !alt;
		}

		shapeRenderer.rect(512,
						0,
						200,
						512,
						Color.LIGHT_GRAY,
						Color.LIGHT_GRAY,
						Color.WHITE,
						Color.WHITE
						);
	}

	public void drawPieces(SpriteBatch batch) {
		for(int y = 0; y < 8; y++) {
			for(int x = 0; x < 8; x++) {
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
					batch.draw(textureRegion, x*64, y*64);
				}

			}
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
		chessPieces = new ChessPiece[8][8];
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
		return chessPieces[vector.getX()][vector.getY()];
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
		ChessPiece chessPiece = chessPieces[oldPosition.getX()][oldPosition.getY()];
		if(chessPiece.getOwner() == ChessOwner.WHITE) {
			turnOwner  = ChessOwner.BLACK;
		} else {
			turnOwner = ChessOwner.WHITE;
		}
		chessPieces[oldPosition.getX()][oldPosition.getY()] = null;
		chessPiece.setPosition(newPosition);
		chessPieces[newPosition.getX()][newPosition.getY()] = chessPiece;
		selectedChessPiece = null;
	}
	public void setChessPieces(ChessPiece[][] chessPieces) {
		this.chessPieces = chessPieces;
	}
	public void attackChessPiece(Vector2 vector1, Vector2 vector2) {
		ChessPiece attacker = chessPieces[vector1.getX()][vector1.getY()];
		if(attacker.getOwner() == ChessOwner.WHITE) {
			turnOwner  = ChessOwner.BLACK;
		} else {
			turnOwner = ChessOwner.WHITE;
		}
		chessPieces[vector1.getX()][vector1.getY()] = null;
		attacker.setPosition(vector2);
		chessPieces[vector2.getX()][vector2.getY()] = attacker;
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
}
