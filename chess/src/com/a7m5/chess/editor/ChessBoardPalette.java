package com.a7m5.chess.editor;

import java.io.Serializable;
import java.util.ArrayList;

import com.a7m5.chess.ResourceGrabber;
import com.a7m5.chess.Vector2;
import com.a7m5.chess.chesspieces.ChessOwner;
import com.a7m5.chess.chesspieces.ChessPiece;
import com.a7m5.chess.chesspieces.ChessPieceSet;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class ChessBoardPalette implements Serializable{
	private static ArrayList<ClickableComponent> clickableComponents = new ArrayList<ClickableComponent>();
	private static ClickableComponent tabWhite;
	private static ClickableComponent tabBlack;
	private static ClickableComponent tabTiles;
	private static ClickableComponent tabSelected;

	private static int windowHeight;
	private static int windowWidth;

	private final static int TILE_SIZE = 62;
	private static int paletteWidth = 5;
	private static int actualPaletteWidth = TILE_SIZE*paletteWidth;
	private static int paletteHeight = 7;
	private static int actualPaletteHeight = TILE_SIZE*paletteHeight;
	private static int tileWidth = actualPaletteWidth / paletteWidth;
	private static int tileHeight = actualPaletteHeight / paletteHeight;
	private static int paletteBottomLeftX;
	private static int paletteBottomLeftY;

	private static ChessPieceSet editorPieceSet;
	private static ChessPiece[][] whiteTabPieces = new ChessPiece[paletteWidth][paletteHeight];
	private static ChessPiece[][] blackTabPieces = new ChessPiece[paletteWidth][paletteHeight];

	public ChessBoardPalette(int bottomLeftX, int bottomLeftY) {
		paletteBottomLeftX = bottomLeftX;
		paletteBottomLeftY = bottomLeftY;
	}

	public static void loadTextures() {
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

		tabTiles = new ClickableComponent(paletteBottomLeftX + tabWidth*2,
				paletteBottomLeftY + actualPaletteHeight,
				tabWidth,
				tabHeight,
				"data/en_tabTiles.png",
				"data/dn_tabTiles.png");
		clickableComponents.add(tabTiles);

		// Grab the piece set. 
		ResourceGrabber myGrab;
		myGrab = new ResourceGrabber();
		editorPieceSet = new ChessPieceSet(myGrab.getGrabbedPieces());

		// Load textures from xml defined paths.
		for(int i = 0; i < editorPieceSet.getLength()-1; i++){
			editorPieceSet.getPieceByIndex(i).loadTextures();
		}

		// Fill up the chess piece arrays with the loaded pieces.
		int deltaX = 0;
		int deltaY = 0;
		for(int k = 0; k < editorPieceSet.getLength()-1; k++){
			whiteTabPieces[deltaX][deltaY] = editorPieceSet.getPieceByIndex(k).getClone(ChessOwner.WHITE);
			System.out.println("WHITE: " + deltaX + ":" + deltaY + " " + editorPieceSet.getPieceByIndex(k).getPieceName());
			blackTabPieces[deltaX][deltaY] = editorPieceSet.getPieceByIndex(k).getClone(ChessOwner.BLACK);

			if(deltaX == paletteWidth - 1){
				deltaX = 0;
				deltaY++;
			} else {
				deltaX++;
			}
		}

		// Set the starting tab.
		tabSelected = tabWhite;
		tabWhite.setComponentSelected(true);
	}

	public void drawBackground(ShapeRenderer shapeRenderer) {
		// Background
		shapeRenderer.setColor(new Color(0.84f, 0.84f, 0.84f, 1));
		shapeRenderer.rect(paletteBottomLeftX, paletteBottomLeftY, actualPaletteWidth, actualPaletteHeight);

		// Tiles
		for(int y = 0; y < paletteHeight; y++) {
			for(int x = 0; x < paletteWidth; x++) {
				shapeRenderer.setColor(new Color(0, 0.84f, 0.18f, 1));
				shapeRenderer.rect(paletteBottomLeftX + x*tileWidth + 2, paletteBottomLeftY + y*tileHeight + 2, tileWidth-4, tileHeight-4);
			}
		}

	}

	public void drawElements(SpriteBatch batch) {
		for(int i = 0; i < clickableComponents.size(); i++){
			clickableComponents.get(i).drawComponent(batch);
		}

		for(int y = 0; y < paletteHeight; y++) {
			for(int x = 0; x < paletteWidth; x++) {
				ChessPiece chessPiece = whiteTabPieces[x][y];

				TextureRegion textureRegion = new TextureRegion();
				if(chessPiece != null){
					if(chessPiece.getPieceName() != null){

						if(chessPiece.getPieceName().compareTo("Pawn") == 0) {
							if(tabSelected == tabWhite){
								textureRegion = editorPieceSet.getPieceByName("Pawn").getWhiteTextureRegion();
							} else if(tabSelected == tabBlack) {
								textureRegion = editorPieceSet.getPieceByName("Pawn").getBlackTextureRegion();
							}
						} else if(chessPiece.getPieceName().compareTo("King") == 0) {
							if(tabSelected == tabWhite) {
								textureRegion = editorPieceSet.getPieceByName("King").getWhiteTextureRegion();
							} else if(tabSelected == tabBlack){
								textureRegion = editorPieceSet.getPieceByName("King").getBlackTextureRegion();
							}
						} else if(chessPiece.getPieceName().compareTo("Queen") == 0) {
							if(tabSelected == tabWhite) {
								textureRegion = editorPieceSet.getPieceByName("Queen").getWhiteTextureRegion();
							} else if(tabSelected == tabBlack){
								textureRegion = editorPieceSet.getPieceByName("Queen").getBlackTextureRegion();
							}
						} else if(chessPiece.getPieceName().compareTo("Knight") == 0) {
							if(tabSelected == tabWhite) {
								textureRegion = editorPieceSet.getPieceByName("Knight").getWhiteTextureRegion();
							} else if(tabSelected == tabBlack){
								textureRegion = editorPieceSet.getPieceByName("Knight").getBlackTextureRegion();
							}
						} else if(chessPiece.getPieceName().compareTo("Rook") == 0) {
							if(tabSelected == tabWhite) {
								textureRegion = editorPieceSet.getPieceByName("Rook").getWhiteTextureRegion();
							} else if(tabSelected == tabBlack){
								textureRegion = editorPieceSet.getPieceByName("Rook").getBlackTextureRegion();
							}
						} else if(chessPiece.getPieceName().compareTo("Bishop") == 0) {
							if(tabSelected == tabWhite) {
								textureRegion = editorPieceSet.getPieceByName("Bishop").getWhiteTextureRegion();
							} else if(tabSelected == tabBlack){
								textureRegion = editorPieceSet.getPieceByName("Bishop").getBlackTextureRegion();
							}
						} else {
							break;
						}
					}	
					// Check that we are in the correct tab before rendering the pieces.
					if(tabSelected == tabWhite || tabSelected == tabBlack){
						batch.draw(textureRegion, paletteBottomLeftX + x*tileWidth, paletteBottomLeftY + y*tileHeight, tileWidth, tileHeight);
					}
				}
			}
		}





	}

	public void start() {
		clear();
	}

	public void restart() {
		start();
	}

	public void clear() {
		//	chessPieces = new ChessPiece[paletteWidth][paletteHeight];
	}

	public static void onClickListener(int x, int y, int pointer, int button) {
		// Get the selected tab (check for change and change if click on a tab).
		if(tabWhite.compClicked(x, y, windowHeight, windowWidth)){
			tabSelected = tabWhite;
		} else if(tabBlack.compClicked(x, y, windowHeight, windowWidth)) {
			tabSelected = tabBlack;
		} else if (tabTiles.compClicked(x, y, windowHeight, windowWidth)){
			tabSelected = tabTiles;
		}
		// Apply the tab change
		for(int i = 0; i < clickableComponents.size(); i++){
			if(clickableComponents.get(i) == tabSelected){	
				clickableComponents.get(i).setComponentSelected(true);
			} else {
				clickableComponents.get(i).setComponentSelected(false);
			}
		}
	}

	public void resize(int width, int height) {
		windowHeight = height;
		windowWidth = width;
	}
}