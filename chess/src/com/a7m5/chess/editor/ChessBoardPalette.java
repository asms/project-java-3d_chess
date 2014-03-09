package com.a7m5.chess.editor;

import java.io.Serializable;
import java.util.ArrayList;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class ChessBoardPalette implements Serializable{
	private static ArrayList<ClickableComponent> clickableComponents = new ArrayList<ClickableComponent>();
	private static ClickableComponent tabWhite;
	private static ClickableComponent tabBlack;
	private static ClickableComponent tabNPC;
	private static ClickableComponent tabTiles;
	private static ClickableComponent tabSelected;

	private static TabPane paneWhite;
	private static TabPane paneBlack;
	private static TabPane paneNPC;
	private static TabPane paneTiles;
	private static TabPane paneSelected;

	private static int windowHeight;
	private static int windowWidth;

	private final static int TILE_SIZE = 62;
	private static int paletteWidth = 6;
	private static int actualPaletteWidth = TILE_SIZE*paletteWidth;
	private static int paletteHeight = 7;
	private static int actualPaletteHeight = TILE_SIZE*paletteHeight;
	private static int tileWidth = actualPaletteWidth / paletteWidth;
	private static int tileHeight = actualPaletteHeight / paletteHeight;
	private static int paletteBottomLeftX;
	private static int paletteBottomLeftY;

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
		// Load all the resources from file (Pieces, NPC's, and tiles), this
		//includes the pieces themselves and their associated artwork.
		
		// TODO: Load all GamePiece files from a folder.
		// TODO: Create an array for white, black, NPC, and tile pieces respectively. Fill arrays.
		// TODO: Create panes to hold pieces and pass in arrays of the pieces.
		
		// Panel textures.
		paneWhite.loadTextures();
		paneBlack.loadTextures();
		paneNPC.loadTextures();
		paneTiles.loadTextures();
	}

	public void drawBackground(ShapeRenderer shapeRenderer) {
		// Background
		shapeRenderer.setColor(Color.RED);
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
		// Draw the current pane.
//		paneSelected.drawPane(batch);	


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
		} else if(tabNPC.compClicked(x, y, windowHeight, windowWidth)){
			tabSelected = tabNPC;
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

		// TODO: Make each tab render independently
		//  - The selected tab should render it's palette segment
		//	- The selected tab should setup it's mouse/piece selection interface.
		//	- TODO: How to deal with tabs that might be overloaded with pieces from the file load or standard set????
	}

	public void resize(int width, int height) {
		windowHeight = height;
		windowWidth = width;
	}
}