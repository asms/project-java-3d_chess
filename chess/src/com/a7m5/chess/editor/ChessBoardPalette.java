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

public class ChessBoardPalette implements Serializable{
	private static TextureRegion kingTextureRegion;
	private static TextureRegion pawnWhiteTextureRegion;
	private static TextureRegion pawnBlackTextureRegion;
	private static TextureRegion queenTextureRegion;
	private static TextureRegion knightTextureRegion;
	private static TextureRegion rookWhiteTextureRegion;
	private static TextureRegion rookBlackTextureRegion;
	private static TextureRegion bishopTextureRegion;
	// Tabs
	private static TextureRegion en_tabWhiteTeamTextureRegion;
	private static TextureRegion en_tabBlackTeamTextureRegion;
	private static TextureRegion en_tabNPCTextureRegion;
	private static TextureRegion en_tabTilesTextureRegion;
	private static TextureRegion dn_tabWhiteTeamTextureRegion;
	private static TextureRegion dn_tabBlackTeamTextureRegion;
	private static TextureRegion dn_tabNPCTextureRegion;
	private static TextureRegion dn_tabTilesTextureRegion;

	private ChessPiece[][] chessPieces;
	private ChessPiece selectedChessPiece = null;
	public static int paletteWidth = 6;
	public static int actualPaletteWidth = 62*paletteWidth;
	public static int paletteHeight = 7;
	public static int actualPaletteHeight = 62*paletteHeight;
	public static int tileWidth = actualPaletteWidth / paletteWidth;
	public static int tileHeight = actualPaletteHeight / paletteHeight;
	public static int paletteBottomLeftX;
	public static int paletteBottomLeftY;

	// Definition of the palette modes.
	public final static int MODE_WHITE = 0;
	public final static int MODE_BLACK = 1;
	public final static int MODE_NPC = 2;
	public final static int MODE_TILE = 3;
	public static int paletteMode = MODE_WHITE;


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

		// Tabs (Enabled:Disabled => selected:unselected)
		Texture en_tabWhiteTeamTexture = new Texture(Gdx.files.internal("data/en_tabWhiteTeam.png"));
		en_tabWhiteTeamTexture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		en_tabWhiteTeamTextureRegion = new TextureRegion(en_tabWhiteTeamTexture, 0, 0, 128, 32);

		Texture en_tabBlackTeamTexture = new Texture(Gdx.files.internal("data/en_tabBlackTeam.png"));
		en_tabBlackTeamTexture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		en_tabBlackTeamTextureRegion = new TextureRegion(en_tabBlackTeamTexture, 0, 0, 128, 32);

		Texture en_tabNPCTexture = new Texture(Gdx.files.internal("data/en_tabNPC.png"));
		en_tabNPCTexture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		en_tabNPCTextureRegion = new TextureRegion(en_tabNPCTexture, 0, 0, 128, 32);

		Texture en_tabTilesTexture = new Texture(Gdx.files.internal("data/en_tabTiles.png"));
		en_tabTilesTexture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		en_tabTilesTextureRegion = new TextureRegion(en_tabTilesTexture, 0, 0, 128, 32);

		Texture dn_tabWhiteTeamTexture = new Texture(Gdx.files.internal("data/dn_tabWhiteTeam.png"));
		dn_tabWhiteTeamTexture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		dn_tabWhiteTeamTextureRegion = new TextureRegion(dn_tabWhiteTeamTexture, 0, 0, 128, 32);

		Texture dn_tabBlackTeamTexture = new Texture(Gdx.files.internal("data/dn_tabBlackTeam.png"));
		dn_tabBlackTeamTexture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		dn_tabBlackTeamTextureRegion = new TextureRegion(dn_tabBlackTeamTexture, 0, 0, 128, 32);

		Texture dn_tabNPCTexture = new Texture(Gdx.files.internal("data/dn_tabNPC.png"));
		dn_tabNPCTexture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		dn_tabNPCTextureRegion = new TextureRegion(dn_tabNPCTexture, 0, 0, 128, 32);

		Texture dn_tabTilesTexture = new Texture(Gdx.files.internal("data/dn_tabTiles.png"));
		dn_tabTilesTexture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		dn_tabTilesTextureRegion = new TextureRegion(dn_tabTilesTexture, 0, 0, 128, 32);
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
		TextureRegion tabWhite	= dn_tabWhiteTeamTextureRegion;
		TextureRegion tabBlack	= dn_tabBlackTeamTextureRegion;
		TextureRegion tabNPC	= dn_tabNPCTextureRegion;
		TextureRegion tabTiles	= dn_tabTilesTextureRegion;
		
		switch(paletteMode){
		case MODE_WHITE:
			tabWhite = en_tabWhiteTeamTextureRegion;
			break;
		case MODE_BLACK:
			tabBlack = en_tabBlackTeamTextureRegion;
			break;
		case MODE_NPC:
			tabNPC = en_tabNPCTextureRegion;
			break;
		case MODE_TILE:
			tabTiles = en_tabTilesTextureRegion;
			break;
		}

		int tabWidth = 85;
		int tabHeight = 20;
		int positionX = paletteBottomLeftX + 4;
		int positionY = paletteBottomLeftY + actualPaletteHeight;

		batch.draw(tabWhite, positionX, positionY, tabWidth, tabHeight);
		positionX += tabWidth + tabWidth/15;
		batch.draw(tabBlack, positionX, positionY, tabWidth, tabHeight);
		positionX += tabWidth + tabWidth/15;
		batch.draw(tabNPC, positionX, positionY, tabWidth, tabHeight);
		positionX += tabWidth + tabWidth/15;
		batch.draw(tabTiles, positionX, positionY, tabWidth, tabHeight);
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

}