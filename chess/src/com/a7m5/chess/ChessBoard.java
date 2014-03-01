package com.a7m5.chess;

import com.a7m5.chess.chesspieces.Bishop;
import com.a7m5.chess.chesspieces.ChessPiece;
import com.a7m5.chess.chesspieces.King;
import com.a7m5.chess.chesspieces.Knight;
import com.a7m5.chess.chesspieces.Pawn;
import com.a7m5.chess.chesspieces.Queen;
import com.a7m5.chess.chesspieces.Rook;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class ChessBoard {
	
	private static TextureRegion kingTextureRegion;
	private static TextureRegion pawnTextureRegion;
	private static TextureRegion queenTextureRegion;
	private static TextureRegion knightTextureRegion;
	private static TextureRegion rookTextureRegion;
	private static TextureRegion bishopTextureRegion;
	
	private ChessPiece[][] chessPieces;
	private ChessPiece selectedChessPiece = null;
	public static int tileWidth = 64;
	private static int boardWidth = tileWidth * 8;
	
	
	public ChessBoard() {
		chessPieces = new ChessPiece[8][8];
	}
	public static void loadTextures() {
		Texture pawnTexture = new Texture(Gdx.files.internal("data/pawn.png"));
		pawnTexture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		pawnTextureRegion = new TextureRegion(pawnTexture, 0, 0, 64, 64);
		
		Texture kingTexture = new Texture(Gdx.files.internal("data/king.png"));
		kingTexture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		kingTextureRegion = new TextureRegion(kingTexture, 0, 0, 64, 64);
		
		Texture queenTexture = new Texture(Gdx.files.internal("data/queen.png"));
		queenTexture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		queenTextureRegion = new TextureRegion(queenTexture, 0, 0, 64, 64);
		
		Texture knightTexture = new Texture(Gdx.files.internal("data/knight.png"));
		knightTexture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		knightTextureRegion = new TextureRegion(knightTexture, 0, 0, 64, 64);
		
		Texture rookTexture = new Texture(Gdx.files.internal("data/rook.png"));
		rookTexture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		rookTextureRegion = new TextureRegion(rookTexture, 0, 0, 64, 64);
		
		Texture bishopTexture = new Texture(Gdx.files.internal("data/bishop.png"));
		bishopTexture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		bishopTextureRegion = new TextureRegion(bishopTexture, 0, 0, 64, 64);
	}
	
	
	public void drawBoard(ShapeRenderer shapeRenderer) {
		boolean alt = true;
		shapeRenderer.setColor(0, 0, 0, 1);
		shapeRenderer.rect(0,
				0,
				boardWidth,
				boardWidth,
				boardWidth/2,
				boardWidth/2,
				0);
		shapeRenderer.setColor(1, 1, 1, 1);
		for(int y = 0; y < 8; y++) {
			for(int x = 0; x < 4; x++) {
				shapeRenderer.rect((2*x+(alt?1:0)) * 64,
						y*64,
						tileWidth,
						tileWidth
						);
			}
			alt = !alt;
		}
		
	}
	
	public void drawPieces(SpriteBatch batch) {
		for(int y = 0; y < 8; y++) {
			for(int x = 0; x < 8; x++) {
				ChessPiece chessPiece = chessPieces[x][y];
				if(chessPiece != null) {
					if(chessPiece instanceof Pawn) {
						batch.draw(pawnTextureRegion, x*64, y*64);
					} else if(chessPiece instanceof King) {
						batch.draw(kingTextureRegion, x*64, y*64);
					} else if(chessPiece instanceof Queen) {
						batch.draw(queenTextureRegion, x*64, y*64);
					} else if(chessPiece instanceof Knight) {
						batch.draw(knightTextureRegion, x*64, y*64);
					} else if(chessPiece instanceof Rook) {
						batch.draw(rookTextureRegion, x*64, y*64);
					} else if(chessPiece instanceof Bishop) {
						batch.draw(bishopTextureRegion, x*64, y*64);
					}
				}
				
			}
		}
	}


	public void addPiece(int x, int y, ChessPiece chessPiece) {
		chessPiece.register(this, x, y);
		chessPieces[x][y] = chessPiece;
	}
	
	public void start() {
		clear();
		for(int x = 0; x < 2; x++) {
			addPiece(0, (x == 0 ? 0 : 7), new Rook(x));
			addPiece(1, (x == 0 ? 0 : 7), new Knight(x));
			addPiece(2, (x == 0 ? 0 : 7), new Bishop(x));
			addPiece(3, (x == 0 ? 0 : 7), new Queen(x));
			addPiece(4, (x == 0 ? 0 : 7), new King(x));
			addPiece(5, (x == 0 ? 0 : 7), new Bishop(x));
			addPiece(6, (x == 0 ? 0 : 7), new Knight(x));
			addPiece(7, (x == 0 ? 0 : 7), new Rook(x));
		}
		
		for(int x = 0; x < 8; x++) {
			addPiece(x, 1, new Pawn(0));
			addPiece(x, 6, new Pawn(1));
		}
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
		return (int) Math.floor((float) (boardWidth - y )/ (float) tileWidth);
	}
	public ChessPiece getChessPieceByXY(int x, int y) {
		int tileX = getTileXFromXCoordinate(x);
		int tileY = getTileYFromYCoordinate(y);
		return chessPieces[tileX][tileY];
	}
	public void moveChessPiece(int tileX0, int tileY0, int tileX1, int tileY1) {
		chessPieces[tileX1][tileY1] = chessPieces[tileX0][tileY0];
		chessPieces[tileX0][tileY0] = null;
	}
	public ChessPiece getSelectedChessPiece() {
		return selectedChessPiece ;
	}
	public void setSelectedChessPiece(ChessPiece chessPiece) {
		selectedChessPiece = chessPiece;
	}
	public void moveChessPiece(Vector2 oldPosition, Vector2 newPosition) {
		ChessPiece chessPiece = chessPieces[oldPosition.getX()][oldPosition.getY()];
		chessPieces[oldPosition.getX()][oldPosition.getY()] = null;
		chessPiece.setPosition(newPosition);
		chessPieces[newPosition.getX()][newPosition.getY()] = chessPiece;
	}
}
