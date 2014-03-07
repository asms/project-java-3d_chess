package com.a7m5.chess.editor;

import java.io.Serializable;
import java.util.ArrayList;

import com.a7m5.chess.Vector2;
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
import com.badlogic.gdx.Input.Buttons;

public class ChessBoardPalette implements Serializable{
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
	private static int paletteWidth = 6;
	private static int actualPaletteWidth = 62*paletteWidth;
	private static int paletteHeight = 7;
	private static int actualPaletteHeight = 62*paletteHeight;
	private static int tileWidth = actualPaletteWidth / paletteWidth;
	private static int tileHeight = actualPaletteHeight / paletteHeight;
	private static int paletteBottomLeftX;
	private static int paletteBottomLeftY;

	private static ArrayList<ClickableComponent> clickableComponents = new ArrayList<ClickableComponent>();
	private static ClickableComponent tabWhite;
	private static ClickableComponent tabBlack;
	private static ClickableComponent tabNPC;
	private static ClickableComponent tabTiles;


	public ChessBoardPalette(int bottomLeftX, int bottomLeftY) {
		chessPieces = new ChessPiece[tileWidth][tileHeight];
		paletteBottomLeftX = bottomLeftX;
		paletteBottomLeftY = bottomLeftY;
	}

	public static void loadTextures() {
		// Standard Chess pieces
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

		int tabWidth = 85;
		int tabHeight = 20;
		// Clickables
		tabWhite = new ClickableComponent(paletteBottomLeftX,
				paletteBottomLeftY + actualPaletteHeight,
				tabWidth,
				tabHeight,
				"data/en_tabWhiteTeam.png",
				"data/dn_tabWhiteTeam.png");
		clickableComponents.add(tabWhite);
		
		tabBlack = new ClickableComponent(paletteBottomLeftX + tabWidth,
				paletteBottomLeftY + actualPaletteHeight,
				tabWidth,
				tabHeight,
				"data/en_tabBlackTeam.png",
				"data/dn_tabBlackTeam.png");
		clickableComponents.add(tabBlack);
		
		tabNPC = new ClickableComponent(paletteBottomLeftX + tabWidth*2,
				paletteBottomLeftY + actualPaletteHeight,
				tabWidth,
				tabHeight,
				"data/en_tabNPC.png",
				"data/dn_tabNPC.png");
		clickableComponents.add(tabNPC);
		
		tabTiles = new ClickableComponent(paletteBottomLeftX + tabWidth*3,
				paletteBottomLeftY + actualPaletteHeight,
				tabWidth,
				tabHeight,
				"data/en_tabTiles.png",
				"data/dn_tabTiles.png");
		clickableComponents.add(tabTiles);

	}

	public void drawPalette(ShapeRenderer shapeRenderer) {

		shapeRenderer.setColor(Color.RED);
		shapeRenderer.rect(paletteBottomLeftX, paletteBottomLeftY, actualPaletteWidth, actualPaletteHeight);

		// Draws the Palette backround.
		for(int y = 0; y < paletteHeight; y++) {
			for(int x = 0; x < paletteWidth; x++) {
				shapeRenderer.setColor(new Color(0, 0.84f, 0.18f, 1));
				shapeRenderer.rect(paletteBottomLeftX + x*tileWidth + 2, paletteBottomLeftY + y*tileHeight + 2, tileWidth-4, tileHeight-4);
			}
		}

		// Highlights the selected Chess Piece.

		if(selectedChessPiece != null) {
			Vector2 selectedPiece = selectedChessPiece.getPosition();
			shapeRenderer.setColor(Color.BLUE);
			shapeRenderer.rect((float) (selectedPiece.getX()*tileWidth + 2 + paletteBottomLeftX),
					(float) (selectedPiece.getY()*tileWidth + 2 + paletteBottomLeftY),
					tileWidth-4,
					tileWidth-4
					);
		}
	}

	public void drawPieces(SpriteBatch batch) {
		for(int y = 0; y < paletteHeight; y++) {
			for(int x = 0; x < paletteWidth; x++) {
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
					int positionX = x*tileWidth + 2 + paletteBottomLeftX;
					int positionY = y*tileHeight+ 2 + paletteBottomLeftY;

					batch.draw(textureRegion, positionX, positionY, tileWidth - 4, tileWidth - 4);
				}
			}
		}
	}

	public void drawTabs(SpriteBatch batch) {
		for(int i = 0; i < clickableComponents.size(); i++){
			clickableComponents.get(i).drawComponent(batch);
		}
	}

	public void start() {
		clear();
	}

	public void restart() {
		start();
	}

	public void clear() {
		chessPieces = new ChessPiece[paletteWidth][paletteHeight];
	}

	public void addPiece(int x, int y, ChessPiece chessPiece) {
		chessPiece.register(this, new Vector2(x, y));
		chessPieces[x][y] = chessPiece;
	}

	public static void onClickListener(int x, int y, int pointer, int button) {
		System.out.println("(" + x + ", " + y + ")");
		for(int i = 0; i < clickableComponents.size(); i++){
			//System.out.print("*");
			if(clickableComponents.get(i).compClicked(x, y)){
				clickableComponents.get(i).setComponentSelected(true);
			}
		}
	}

}