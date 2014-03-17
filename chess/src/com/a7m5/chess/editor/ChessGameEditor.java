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

import org.lwjgl.opengl.GL11;

import com.a7m5.chess.ChessBoard;
import com.a7m5.chess.ResourceGrabber;
import com.a7m5.chess.ResourceThrower;
import com.a7m5.chess.Tile;
import com.a7m5.chess.Vector2;
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
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldListener;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.gdx.extension.ui.color.SlideColorPicker;
import com.gdx.extension.ui.grid.GridSelection;
import com.gdx.extension.ui.grid.GridSelectionItem;
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
	private TextField boardNameTextField;
	private int selectedChessPieceIndex = -1;
	private ChessOwner selectedChessOwner = ChessOwner.WHITE;
	private AdvancedList<ListRow> movementVectorsList;
	private AdvancedList<ListRow> attackVectorsList;
	private ArrayList<Vector2> attackVectors = null;
	private ArrayList<Vector2> attackDirectionVectors = null;
	private ArrayList<Vector2> movementVectors = null;
	private ArrayList<Vector2> movementDirectionVectors = null;
	private Skin skin;

	public ChessGameEditor() {
		self = this;
		// Grab the set of chess pieces before starting the editor
		ResourceGrabber myGrab;
		myGrab = new ResourceGrabber();
		ChessBoard.gamePieceSet = myGrab.getChessPieceSet();
		boards = myGrab.getBoards();
	}

	@Override
	public void create() {
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
		skin = new Skin(skinFile, atlas);

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
		final AdvancedList<ListRow> boardList = new AdvancedList<ListRow>();

		for(int i = 0; i < boards.size(); i++) {
			final int index = i;
			ListRow row = new ListRow(skin);
			Label label = new Label(boards.get(i).getName(), skin);
			label.setAlignment(Align.center);
			row.add(label).width(200).fill().expand();
			row.addListener(new ClickListener() {

				@Override
				public void clicked(InputEvent event, float x, float y) {
					editingBoardIndex = index;
					ChessBoard board = getEditingBoard();
					int width = board.getBoardWidth();
					String name = board.getName();
					boardWidthTextField.setText(String.valueOf(width));
					boardNameTextField.setText(name);
				}
			});
			boardList.addItem(row);
		}

		Label boardNameLabel = new Label("Name", skin);
		boardNameTextField = new TextField("", skin);

		boardWidthTextField = new TextField("", skin);
		Label boardWidthLabel = new Label("Size", skin);
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

		TextButton saveBoardButton = new TextButton("Save", skin);
		saveBoardButton.addListener(new ClickListener() {

			@Override
			public void clicked(InputEvent event, float x, float y) {
				ChessBoard board = getEditingBoard();
				if(board != null) {
					board.setName(boardNameTextField.getText());
					ResourceThrower thrower = new ResourceThrower();
					thrower.saveBoard(board);
				}


			}

		});

		wrapper.add(boardList).colspan(3).fillX().padBottom(16).left();
		wrapper.row();
		wrapper.add(boardNameLabel).width(60).padRight(8);
		wrapper.add(boardNameTextField);
		wrapper.row();
		wrapper.add(boardWidthLabel).width(60).padRight(8);
		wrapper.add(boardWidthTextField);
		wrapper.add(updateBoardWidthButton).padLeft(8);
		wrapper.row();
		wrapper.add(saveBoardButton);
		wrapper.align(Align.top);

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
		tilesContainer.align(Align.top);

		//Piece Container
		GridSelection<GridSelectionItem> piecesGrid = new GridSelection<GridSelectionItem>();

		ChessPiece[] set = getChessPieceSet().getPieces();
		for(int i = 0; i < set.length; i++) {
			final int index = i;
			final ChessPiece piece = set[i];
			GridSelectionItem item = new GridSelectionItem(skin);
			TextureRegion textureRegion = piece.getWhiteTextureRegion();
			if(textureRegion != null) {
				TextureRegionDrawable drawable = new TextureRegionDrawable(textureRegion);
				item.add(new Image(drawable)).fill().expand();
				item.addListener(new ClickListener() {

					@Override
					public void clicked(InputEvent event, float x, float y) {
						editorMode = EditingMode.PIECE_SET;
						selectedChessPieceIndex = index;
						attackVectors = new ArrayList<Vector2>(Arrays.asList(piece.getAttackVectors()));
						attackDirectionVectors = new ArrayList<Vector2>(Arrays.asList(piece.getAttackDirectionVectors()));
						movementVectors = new ArrayList<Vector2>(Arrays.asList(piece.getMovementVectors()));
						movementDirectionVectors = new ArrayList<Vector2>(Arrays.asList(piece.getMovementDirectionVectors()));
						updateMovementVectorList();
						updateAttackVectorList();
					}

				});
				piecesGrid.addItem(item);
			} else {
				System.out.println("texture is null");
			}
		}
		TextButton whiteChessOwnerButton = new TextButton("White", skin);
		TextButton blackChessOwnerButton = new TextButton("Black", skin);
		TextButton chessPieceDeleteButton = new TextButton("Delete", skin);

		whiteChessOwnerButton.addListener(new ClickListener() {

			@Override
			public void clicked(InputEvent event, float x, float y) {
				editorMode = EditingMode.PIECE_SET;
				selectedChessOwner = ChessOwner.WHITE;
			}

		});

		blackChessOwnerButton.addListener(new ClickListener() {

			@Override
			public void clicked(InputEvent event, float x, float y) {
				editorMode = EditingMode.PIECE_SET;
				selectedChessOwner = ChessOwner.BLACK;
			}

		});
		
		chessPieceDeleteButton.addListener(new ClickListener() {

			@Override
			public void clicked(InputEvent event, float x, float y) {
				editorMode = EditingMode.PIECE_REMOVE;
			}

		});
		
		Label movementVectorsLabel = new Label("Movement Vectors", skin);
		Label attackVectorsLabel = new Label("Attack Vectors", skin);
		TextButton newMovementVectorButton = new TextButton("Add", skin);
		TextButton newAttackVectorButton = new TextButton("Add", skin);
		TextButton savePieceButton = new TextButton("Save", skin);
		
		savePieceButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				ResourceThrower thrower = new ResourceThrower();
				ChessPiece piece = getSelectedChessPiece();
				piece.setMovementVectors(movementVectors.toArray(new Vector2[movementVectors.size()]));
				piece.setMovementDirectionVectors(movementDirectionVectors.toArray(new Vector2[movementDirectionVectors.size()]));
				piece.setAttackVectors(attackVectors.toArray(new Vector2[attackVectors.size()]));
				piece.setAttackDirectionVectors(attackDirectionVectors.toArray(new Vector2[attackDirectionVectors.size()]));
				thrower.savePiece(piece);
			}
		});
		
		newMovementVectorButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				ChessPiece piece = getSelectedChessPiece();
				if(piece != null) {
					movementVectors.add(new Vector2(0, 0));
					ListRow row = createMovementVectorListRow(movementVectors.size()-1);
					movementVectorsList.addItem(row);
				}
			}
		});
		
		newAttackVectorButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				ChessPiece piece = getSelectedChessPiece();
				if(piece != null) {
					attackVectors.add(new Vector2(0, 0));
					ListRow row = createMovementVectorListRow(attackVectors.size()-1);
					attackVectorsList.addItem(row);
				}
			}
		});
		movementVectorsList = new AdvancedList<ListRow>();
		attackVectorsList = new AdvancedList<ListRow>();
		
		Table piecesContainerWrapper = new Table();

		piecesContainerWrapper.add(piecesGrid).colspan(3);
		piecesContainerWrapper.row();
		piecesContainerWrapper.add(whiteChessOwnerButton);
		piecesContainerWrapper.add(blackChessOwnerButton);
		piecesContainerWrapper.add(chessPieceDeleteButton);
		piecesContainerWrapper.row().padTop(8);
		piecesContainerWrapper.add(movementVectorsLabel).colspan(2);
		piecesContainerWrapper.add(newMovementVectorButton);
		piecesContainerWrapper.row().padTop(4);
		piecesContainerWrapper.add(movementVectorsList).colspan(3);
		piecesContainerWrapper.row().padTop(8);
		piecesContainerWrapper.add(attackVectorsLabel).colspan(2);
		piecesContainerWrapper.add(newAttackVectorButton);
		piecesContainerWrapper.row().padTop(4);
		piecesContainerWrapper.add(attackVectorsList).colspan(3);
		piecesContainerWrapper.row().padTop(8);
		piecesContainerWrapper.add(savePieceButton);
		piecesContainerWrapper.align(Align.top);
		
		ScrollPane piecesContainerScrollPane = new ScrollPane(piecesContainerWrapper);
		piecesContainerScrollPane.setScrollingDisabled(true, false);
		piecesContainer.add(piecesContainerScrollPane).fill().expand();
		
		boardsTab.addListener(new ClickListener() {

			@Override
			public void clicked(InputEvent event, float x, float y) {
				System.out.println("Boards tab clicked.");
			}


		});

		piecesTab.addListener(new ClickListener() {

			@Override
			public void clicked(InputEvent event, float x, float y) {
				System.out.println("Pieces tab clicked.");
				editorMode = EditingMode.PIECE_SET;
			}


		});

		tilesTab.addListener(new ClickListener() {

			@Override
			public void clicked(InputEvent event, float x, float y) {
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

		ChessBoard board = getEditingBoard();
		if(board == null) {
			return;
		}

		int width = board.getBoardWidth();
		int tileX = board.getTileFromCoordinate(x);
		int tileY = board.getTileFromCoordinate(ChessBoard.actualBoardWidth - y);
		if(tileX >= width || tileY >= width) {
			return;
		}
		Tile[][] tiles = board.getTileArray();
		ChessPiece[][] pieces = board.getChessPieces();

		if(editorMode == EditingMode.TILE_DELETE) {
			tiles[tileX][tileY] = null;
			pieces[tileX][tileY] = null;
		}

		if(editorMode == EditingMode.TILE_PAINT && tilePaintColor != null) {
			if(tiles[tileX][tileY] == null) {
				tiles[tileX][tileY] = new Tile();
			}
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


		if(editorMode == EditingMode.PIECE_SET) {
			ChessPiece piece = getSelectedChessPiece();
			if(tiles[tileX][tileY] != null && piece != null) {
				ChessPiece newPiece = piece.getClone(selectedChessOwner);
				newPiece.setPosition(new Vector2(tileX, tileY));
				pieces[tileX][tileY] = newPiece;
			}
		}

		if(editorMode == EditingMode.PIECE_REMOVE) {
			pieces[tileX][tileY] = null;
		}

	}
	
	private void updateMovementVectorList() {
		movementVectorsList.removeItems(movementVectorsList.getItems());
		for(int i = 0; i < movementVectors.size(); i++) {
			ListRow row = createMovementVectorListRow(i);
			movementVectorsList.addItem(row);
		}
		for(int i = 0; i < movementDirectionVectors.size(); i++) {
			ListRow row = createMovementDirectionVectorListRow(i);
			movementVectorsList.addItem(row);
		}
		
	}
	
	private void updateAttackVectorList() {
				attackVectorsList.clear();
				for(int i = 0; i < attackVectors.size(); i++) {
					ListRow row = createAttackVectorListRow(i);
					attackVectorsList.addItem(row);
				}
				for(int i = 0; i < attackDirectionVectors.size(); i++) {
					ListRow row = createAttackMovementVectorListRow(i);
					attackVectorsList.addItem(row);
				}
	}
	
	public ListRow createMovementVectorListRow(int i) {
		final Vector2 movementVector = movementVectors.get(i);
		ListRow row = new ListRow(skin);
		final TextField xComponentField = new TextField(String.valueOf(movementVector.getX()), skin);
		final TextField yComponentField = new TextField(String.valueOf(movementVector.getY()), skin);
		final CheckBox directionVectorCheckBox = new CheckBox("Dir", skin);
		directionVectorCheckBox.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if(directionVectorCheckBox.isChecked()) {
					movementVectors.remove(movementVector);
					movementDirectionVectors.add(movementVector);
				} else {
					movementDirectionVectors.remove(movementVector);
					movementVectors.add(movementVector);
				}
			}
			
		});
		xComponentField.setTextFieldListener(new TextFieldListener() {
			public void keyTyped (TextField textField, char key) {
				try {
					double xComponent = Double.parseDouble(xComponentField.getText());
					System.out.println("Changed value: " + xComponent);
					movementVector.setX(xComponent);
				} catch(NumberFormatException e) {}
				
			}
			
		});
		yComponentField.setTextFieldListener(new TextFieldListener() {
			
			public void keyTyped (TextField textField, char key) {
				try {
					double yComponent = Double.parseDouble(yComponentField.getText());
					movementVector.setY(yComponent);
				} catch(NumberFormatException e) {}
				
			}
			
		});
		row.add(xComponentField).width(50);
		row.add(yComponentField).width(50);
		row.add(directionVectorCheckBox).width(100);
		return row;
	}
	
	public ListRow createMovementDirectionVectorListRow(int i) {
		final Vector2 movementDirectionVector = movementDirectionVectors.get(i);
		ListRow row = new ListRow(skin);
		final TextField xComponentField = new TextField(String.valueOf(movementDirectionVector.getX()), skin);
		final TextField yComponentField = new TextField(String.valueOf(movementDirectionVector.getY()), skin);
		final CheckBox directionVectorCheckBox = new CheckBox("Dir", skin);
		directionVectorCheckBox.setChecked(true);
		directionVectorCheckBox.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if(!directionVectorCheckBox.isChecked()) {
					movementDirectionVectors.remove(movementDirectionVector);
					movementVectors.add(movementDirectionVector);
				} else {
					movementVectors.remove(movementDirectionVector);
					movementDirectionVectors.add(movementDirectionVector);
				}
			}
			
		});
		xComponentField.setTextFieldListener(new TextFieldListener() {
			public void keyTyped (TextField textField, char key) {
				try {
					double xComponent = Double.parseDouble(xComponentField.getText());
					movementDirectionVector.setX(xComponent);
				} catch(NumberFormatException e) {}
				
			}
			
		});
		yComponentField.setTextFieldListener(new TextFieldListener() {
			public void keyTyped (TextField textField, char key) {
				try {
					double yComponent = Double.parseDouble(yComponentField.getText());
					movementDirectionVector.setY(yComponent);
				} catch(NumberFormatException e) {}
				
			}
			
		});
		row.add(xComponentField).width(50);
		row.add(yComponentField).width(50);
		row.add(directionVectorCheckBox).width(100);
		return row;
	}
	
	public ListRow createAttackVectorListRow(int i) {
		final Vector2 attackVector = attackVectors.get(i);
		ListRow row = new ListRow(skin);
		final TextField xComponentField = new TextField(String.valueOf(attackVector.getX()), skin);
		final TextField yComponentField = new TextField(String.valueOf(attackVector.getY()), skin);
		final CheckBox directionVectorCheckBox = new CheckBox("Dir", skin);
		directionVectorCheckBox.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if(directionVectorCheckBox.isChecked()) {
					attackVectors.remove(attackVector);
					attackDirectionVectors.add(attackVector);
				} else {
					attackDirectionVectors.remove(attackVector);
					attackVectors.add(attackVector);
				}
			}
			
		});
		xComponentField.setTextFieldListener(new TextFieldListener() {
			public void keyTyped (TextField textField, char key) {
				try {
					double xComponent = Double.parseDouble(xComponentField.getText());
					attackVector.setX(xComponent);
				} catch(NumberFormatException e) {}
				
			}
			
		});
		yComponentField.setTextFieldListener(new TextFieldListener() {
			public void keyTyped (TextField textField, char key) {
				try {
					double yComponent = Double.parseDouble(yComponentField.getText());
					attackVector.setY(yComponent);
				} catch(NumberFormatException e) {}
				
			}
			
		});
		row.add(xComponentField).width(50);
		row.add(yComponentField).width(50);
		row.add(directionVectorCheckBox).width(100);
		return row;
	}
	
	public ListRow createAttackMovementVectorListRow(int i) {
		final Vector2 attackDirectionVector = attackDirectionVectors.get(i);
		ListRow row = new ListRow(skin);
		final TextField xComponentField = new TextField(String.valueOf(attackDirectionVector.getX()), skin);
		final TextField yComponentField = new TextField(String.valueOf(attackDirectionVector.getY()), skin);
		final CheckBox directionVectorCheckBox = new CheckBox("Dir", skin);
		directionVectorCheckBox.setChecked(true);
		directionVectorCheckBox.addListener(new ClickListener() {
			
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if(!directionVectorCheckBox.isChecked()) {
					attackDirectionVectors.remove(attackDirectionVector);
					attackVectors.add(attackDirectionVector);
				} else {
					attackVectors.remove(attackDirectionVector);
					attackDirectionVectors.add(attackDirectionVector);
				}
			}
			
		});
		xComponentField.setTextFieldListener(new TextFieldListener() {
			public void keyTyped (TextField textField, char key) {
				try {
					double xComponent = Double.parseDouble(xComponentField.getText());
					attackDirectionVector.setX(xComponent);
				} catch(NumberFormatException e) {}
				
			}
			
		});
		yComponentField.setTextFieldListener(new TextFieldListener() {
			public void keyTyped (TextField textField, char key) {
				try {
					double yComponent = Double.parseDouble(yComponentField.getText());
					attackDirectionVector.setY(yComponent);
				} catch(NumberFormatException e) {}
				
			}
			
		});
		row.add(xComponentField).width(50);
		row.add(yComponentField).width(50);
		row.add(directionVectorCheckBox).width(100);
		return row;
	}

	public ChessBoard getEditingBoard() {
		if(editingBoardIndex > -1) {
			return boards.get(editingBoardIndex);
		} else {
			return null;
		}

	}

	public ChessPieceSet getChessPieceSet() {
		return ChessBoard.gamePieceSet;
	}
	
	private ChessPiece getSelectedChessPiece() {
		if(selectedChessPieceIndex > -1) {
			return getChessPieceSet().getPieceByIndex(selectedChessPieceIndex);
		} else {
			return null;
		}
	}

	public static ChessGameEditor getInstance() {
		return self;
	}
}
