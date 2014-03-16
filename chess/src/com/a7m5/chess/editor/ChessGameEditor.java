// This class will be the main editor GUI window. The alternate view from the game play window.
// TODO: Make this interface similar to the game but
// with a side bar with things either for:
// 						- Editing game pieces (Black/White, or NPC)
// 						- Editing game boards. Set size and give palette of pieces.
// with save/open buttons for files and resources.
// Way to organise things into game packages containing files for a collection of pieces and a board.

package com.a7m5.chess.editor;

import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.JFileChooser;

import org.lwjgl.opengl.GL11;

import com.a7m5.chess.ChessBoard;
import com.a7m5.chess.ChessGame3D;
import com.a7m5.chess.ResourceGrabber;
import com.a7m5.chess.Tile;
import com.a7m5.chess.chesspieces.ChessOwner;
import com.a7m5.chess.chesspieces.ChessPiece;
import com.a7m5.chess.chesspieces.ChessPieceSet;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.gdx.extension.ui.color.SlideColorPicker;
import com.gdx.extension.ui.list.AdvancedList;
import com.gdx.extension.ui.list.ListRow;
import com.gdx.extension.ui.tab.Tab;
import com.gdx.extension.ui.tab.TabContainer;
import com.gdx.extension.ui.tab.TabPane;

public class ChessGameEditor implements ApplicationListener {
	private static ChessGameEditor self = null;
	private OrthographicCamera camera;
	private SpriteBatch spriteBatch;
	private TabPane tabPane;
	private Stage stage;
	private ShapeRenderer shapeRenderer;
	private ArrayList<ChessBoard> boards;
	private int editingBoardIndex = -1;
	private TextField boardWidthTextField;
	
	private EditingMode editorMode;
	private enum EditingMode {
		Board,
		TILE_PAINT,
		TILE_PICK,
		TILE_DELETE,
		PIECE_SET,
		PIECE_REMOVE
	};
	private Color tilePaintColor = null;
	private SlideColorPicker tileColorPicker;
	private Slider tileSaturationSlider;
	private Slider tileBrightnessSlider;

	private static ChessBoardPalette editingPalette;
	private static ChessPieceSet editorPieceSet;

	public ChessGameEditor() {
		self = this;
		// Grab the set of chess pieces before starting the editor
		ResourceGrabber myGrab;
		myGrab = new ResourceGrabber();
		editorPieceSet = myGrab.getChessPieceSet();
		boards = myGrab.getBoards();
		editingPalette = new ChessBoardPalette(522,10, editorPieceSet);
	}

	@Override
	public void create() {
		ChessBoardPalette.loadTextures();
		ChessBoard.loadTextures();

		EditorInputProcessor inputProcessor = new EditorInputProcessor();
		stage = new Stage();
		Gdx.input.setInputProcessor(stage);
		InputMultiplexer multiplexer = new InputMultiplexer();
		multiplexer.addProcessor(stage);
		multiplexer.addProcessor(inputProcessor);
		Gdx.input.setInputProcessor(multiplexer);
		
		camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		camera.setToOrtho(false);
		
		shapeRenderer =  new ShapeRenderer();
		spriteBatch = new SpriteBatch();

		FileHandle skinFile = Gdx.files.internal("skins/default.json");
		TextureAtlas atlas = new TextureAtlas(Gdx.files.internal("skins/skin.atlas"));
		Skin skin = new Skin(skinFile, atlas);

		tabPane = new TabPane(skin);
		TabContainer boardsContainer = new TabContainer(skin);
		TabContainer tilesContainer = new TabContainer(skin);
		TabContainer piecesContainer = new TabContainer(skin);
		Tab boardsTab = new Tab("Boards", boardsContainer, skin);
		Tab tilesTab = new Tab("Tiles", tilesContainer, skin);
		Tab piecesTab = new Tab("Pieces", piecesContainer, skin);
		
		//Boards Container
		Table wrapper = new Table();
		ScrollPane scrollPane = new ScrollPane(wrapper);
		AdvancedList<ListRow> boardList = new AdvancedList<ListRow>();
		
		for(int i = 0; i < boards.size(); i++) {
			final int index = i;
			ListRow row = new ListRow(skin);
			Label label = new Label(boards.get(i).getName(), skin);
			label.setAlignment(Align.center);
			row.add(label).width(100).fill().expand();
			row.addListener(new ClickListener() {

				@Override
			    public void clicked(InputEvent event, float x, float y) {
					editingBoardIndex = index;
					int width = boards.get(index).getBoardWidth();
					boardWidthTextField.setText(String.valueOf(width));
				}
			});
			boardList.addItem(row);
		}
		
		boardWidthTextField = new TextField("", skin);
		Label textLabel = new Label("Size", skin);
		TextButton updateBoardWidthButton = new TextButton("Update", skin);
		updateBoardWidthButton.addListener(new ClickListener() {
			@Override
		    public void clicked(InputEvent event, float x, float y) {
				String widthString = boardWidthTextField.getText();
				try {
					ChessBoard board = boards.get(editingBoardIndex);
					int width = Integer.parseInt(widthString);
					board.setBoardWidth(width);
					//Resize Tile Array
					Tile[][] newTileArray = new Tile[width][width];
					Tile[][] oldTileArray = board.getTileArray();
					for(int i = 0; i < oldTileArray.length && i < width; i++) {
						newTileArray[i] = Arrays.copyOf(oldTileArray[i], width);
					}
					board.setTileArray(newTileArray);
					
					//Resize ChessPiece Array
					ChessPiece[][] newPieceArray = new ChessPiece[width][width];
					ChessPiece[][] oldPieceArray = board.getChessPieces();
					for(int i = 0; i < oldPieceArray.length && i < width; i++) {
						newPieceArray[i] = Arrays.copyOf(oldPieceArray[i], width);
					}
					board.setChessPieces(newPieceArray);
				} catch(NumberFormatException e) {
					e.printStackTrace();
				}
			}
		});
		
		wrapper.add(boardList).fillX().padBottom(16).left();
		wrapper.row();
		wrapper.add(textLabel).padRight(16);
		wrapper.add(boardWidthTextField);
		wrapper.add(updateBoardWidthButton);
		wrapper.row();
		
		boardsContainer.add(scrollPane).fill().expand();
		
		//Tiles Container
		tileColorPicker = new SlideColorPicker(false, skin);
		tileColorPicker.addListener(new ChangeListener() {

			@Override
			public void changed(ChangeEvent event, Actor actor) {
				if(!tileColorPicker.isDragging()) {
					java.awt.Color color = java.awt.Color.getHSBColor(tileColorPicker.getValue() / 360f, tileColorPicker.getSaturation(), tileColorPicker.getBrightness());
					
					tilePaintColor = new Color((float) color.getRed() / 256f, (float) color.getGreen() / 256f, (float) color.getBlue() / 256f, 1);
				}
			}

			
		});
		tileColorPicker.fire(new ChangeEvent());
		
		tileSaturationSlider = new Slider(0f, 1f, 1f / 100f, false, skin);
		tileSaturationSlider.setValue(tileColorPicker.getSaturation());
		tileSaturationSlider.addListener(new ChangeListener() {
		    
		    @Override
		    public void changed(ChangeEvent event, Actor actor)
		    {
		    	tileColorPicker.setSaturation(tileSaturationSlider.getValue());
		    	tileColorPicker.fire(new ChangeEvent());
		    }
		    
		});
		tileBrightnessSlider = new Slider(0f, 1f, 1f / 100f, false, skin);
		tileBrightnessSlider.setValue(tileColorPicker.getBrightness());
		tileBrightnessSlider.addListener(new ChangeListener() {
		    
		    @Override
		    public void changed(ChangeEvent event, Actor actor)
		    {
		    	tileColorPicker.setBrightness(tileBrightnessSlider.getValue());
		    	tileColorPicker.fire(new ChangeEvent());
		    }
		    
		});
		
		TextButton tilePaintButton = new TextButton("Paint", skin);
		TextButton tilePickButton = new TextButton("Pick", skin);
		TextButton tileDeleteButton = new TextButton("Delete", skin);
		Label colorLabel = new Label("Color", skin);
		Label saturationLabel = new Label("Saturation", skin);
		Label brightnessLabel = new Label("Brightness", skin);
		
		tilePaintButton.addListener(new ClickListener() {

			@Override
		    public void clicked(InputEvent event, float x, float y) {
				editorMode = EditingMode.TILE_PAINT;
			}
			
			
		});
		tilePickButton.addListener(new ClickListener() {

			@Override
		    public void clicked(InputEvent event, float x, float y) {
				editorMode = EditingMode.TILE_PICK;
			}
			
			
		});
		tileDeleteButton.addListener(new ClickListener() {

			@Override
		    public void clicked(InputEvent event, float x, float y) {
				editorMode = EditingMode.TILE_DELETE;
			}
			
			
		});
		
		tilesContainer.add(colorLabel).pad(8);
		tilesContainer.add(tileColorPicker).pad(8);
		tilesContainer.row();
		tilesContainer.add(saturationLabel).pad(8);
		tilesContainer.add(tileSaturationSlider).pad(8);
		tilesContainer.row();
		tilesContainer.add(brightnessLabel).pad(8);
		tilesContainer.add(tileBrightnessSlider).pad(8);
		tilesContainer.row();
		tilesContainer.add(tilePaintButton).pad(8);
		tilesContainer.add(tilePickButton).pad(8);
		tilesContainer.add(tileDeleteButton).pad(8);
		
		boardsTab.addListener(new ClickListener() {

			@Override
		    public void clicked(InputEvent event, float x, float y) {
				// TODO Auto-generated method stub
				System.out.println("Boards tab clicked.");
			}
			
			
		});
		
		piecesTab.addListener(new ClickListener() {

			@Override
		    public void clicked(InputEvent event, float x, float y) {
				// TODO Auto-generated method stub
				System.out.println("Pieces tab clicked.");
			}
			
			
		});
		
		tilesTab.addListener(new ClickListener() {

			@Override
		    public void clicked(InputEvent event, float x, float y) {
				// TODO Auto-generated method stub
				System.out.println("Tiles tab clicked.");
				editorMode = EditingMode.TILE_PAINT;
			}
			
			
		});
		
		tabPane.addTab(boardsTab);
		tabPane.addTab(tilesTab);
		tabPane.addTab(piecesTab);
		tabPane.setCurrentTab(0);
		tabPane.setPosition(512, 0);
		tabPane.setWidth(300);
		tabPane.setHeight(512);
		stage.addActor(tabPane);
	}

	@Override
	public void resize(int width, int height) {
		editingPalette.resize(width, height);
		stage.setViewport(width, height, true);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void render() {
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL11.GL_COLOR_BUFFER_BIT);
		
		stage.act(Gdx.graphics.getDeltaTime());
		
		if(editingBoardIndex > -1) {
			shapeRenderer.setProjectionMatrix(camera.combined);
			shapeRenderer.begin(ShapeType.Filled);
			boards.get(editingBoardIndex).drawBoard(shapeRenderer);
			//editingPalette.drawBackground(shapeRenderer);
			shapeRenderer.end();
			
			spriteBatch.setProjectionMatrix(camera.combined);
			spriteBatch.begin();
			//editingPalette.drawElements(spriteBatch);
			boards.get(editingBoardIndex).drawPieces(spriteBatch);
			spriteBatch.end();
		}
		
		
		stage.act(Gdx.graphics.getDeltaTime());
	    stage.draw();
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub

	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose() {
		spriteBatch.dispose();
		stage.dispose();
	}


	public void onClickListener(int x, int y, int pointer, int button) {
		System.out.println(x + ":" + y);
		// Palette Clicks.
		ChessBoardPalette.onClickListener(x, y, pointer, button);
		// Board Clicks.
		// Adding pieces.
		ChessBoard board = getEditingBoard();
		if(board == null) {
			return;
		}
		
		int tileX = board.getTileFromCoordinate(x);
		int tileY = board.getTileFromCoordinate(ChessBoard.actualBoardWidth - y);
		Tile[][] tiles = board.getTileArray();
		
		if(editorMode == EditingMode.TILE_DELETE) {
			tiles[tileX][tileY] = null;
			ChessPiece[][] pieces = board.getChessPieces();
			pieces[tileX][tileY] = null;
		}
		
		if(editorMode == EditingMode.TILE_PAINT && tilePaintColor != null) {
			tiles[tileX][tileY].setColor(tilePaintColor);
		}
		
		if(editorMode == EditingMode.TILE_PICK) {
			Color pickedColor = tiles[tileX][tileY].getColor();
			float[] hsb = new float[3];
			java.awt.Color.RGBtoHSB((int)(pickedColor.r * 256f), (int)(pickedColor.g * 256f), (int)(pickedColor.b * 256f), hsb);
			tileColorPicker.setValue(hsb[0] * 360f);
			tileColorPicker.setSaturation(hsb[1]);
			tileColorPicker.setBrightness(hsb[2]);
			tileSaturationSlider.setValue(hsb[1]);
			tileBrightnessSlider.setValue(hsb[2]);
			tileColorPicker.fire(new ChangeEvent());
		}

	}

	public ChessBoard getEditingBoard() {
		if(editingBoardIndex > -1) {
			return boards.get(editingBoardIndex);
		} else {
			return null;
		}
		
	}

	public static ChessGameEditor getInstance() {
		return self;
	}
}
