// This class will be the main editor GUI window. The alternate view from the game play window.
// TODO: Make this interface similar to the game but
// with a side bar with things either for:
// 						- Editing game pieces (Black/White, or NPC)
// 						- Editing game boards. Set size and give palette of pieces.
// with save/open buttons for files and resources.
// Way to organise things into game packages containing files for a collection of pieces and a board.

package com.a7m5.chess.editor;

import java.util.ArrayList;

import javax.swing.JFileChooser;

import org.lwjgl.opengl.GL11;

import com.a7m5.chess.ChessBoard;
import com.a7m5.chess.ResourceGrabber;
import com.a7m5.chess.chesspieces.ChessOwner;
import com.a7m5.chess.chesspieces.ChessPieceSet;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.gdx.extension.ui.list.AdvancedList;
import com.gdx.extension.ui.list.ListRow;
import com.gdx.extension.ui.tab.Tab;
import com.gdx.extension.ui.tab.TabContainer;
import com.gdx.extension.ui.tab.TabPane;

public class ChessGameEditor implements ApplicationListener {
	private OrthographicCamera camera;
	private SpriteBatch spriteBatch;
	private TabPane tabPane;
	private TabContainer tabContainer;
	private Stage stage;
	private ShapeRenderer shapeRenderer;
	private ArrayList<ChessBoard> boards;
	private static ChessBoard editingBoard;
	private int editingBoardIndex = -1;

	private static ChessBoardPalette editingPalette;
	private static ChessPieceSet editorPieceSet;

	public ChessGameEditor() {
		// Grab the set of chess pieces before starting the editor
		ResourceGrabber myGrab;
		JFileChooser directoryChooser = new JFileChooser();
		directoryChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		/*
		String fileAddress = null;

		if(JFileChooser.APPROVE_OPTION == directoryChooser.showOpenDialog(null)) {
			File myFile = directoryChooser.getSelectedFile();
			System.out.println("Opening Directory: " + myFile.getAbsolutePath());
			fileAddress = myFile.getAbsolutePath();
		}

		if(fileAddress != null){
		 */
		myGrab = new ResourceGrabber();
		editorPieceSet = myGrab.getChessPieceSet();
		boards = myGrab.getBoards();
		new Object();
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
		
		camera = new OrthographicCamera(512, 512+330);
		camera.setToOrtho(false);
		
		shapeRenderer =  new ShapeRenderer();
		spriteBatch = new SpriteBatch();

		FileHandle skinFile = Gdx.files.internal("skins/default.json");
		TextureAtlas atlas = new TextureAtlas(Gdx.files.internal("skins/skin.atlas"));
		Skin skin = new Skin(skinFile, atlas);

		tabPane = new TabPane(skin);
		tabContainer = new TabContainer(skin);
		TabContainer boardsContainer = new TabContainer(skin);
		Tab boardsTab = new Tab("Boards", boardsContainer, skin);
		Tab tilesTab = new Tab("Tiles", tabContainer, skin);
		Tab piecesTab = new Tab("Pieces", tabContainer, skin);
		
		AdvancedList<ListRow> list = new AdvancedList<ListRow>();
		for(int i = 0; i < boards.size(); i++) {
			final int index = i;
			ListRow row = new ListRow(skin);
			row.add(new Label(boards.get(i).getName(), skin));
			row.addListener(new ClickListener() {

				@Override
			    public void clicked(InputEvent event, float x, float y) {
					editingBoardIndex = index;
					System.out.println(index);
				}
			});
			list.addItem(row);
		}
		boardsContainer.add(list);
		
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
			}
			
			
		});
		
		tabPane.addTab(boardsTab);
		tabPane.addTab(tilesTab);
		tabPane.addTab(piecesTab);
		tabPane.setCurrentTab(0);
		tabPane.setPosition(512, 0);
		tabPane.setWidth(256);
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


	public static void onClickListener(int x, int y, int pointer, int button) {
		System.out.println(x + ":" + y);
		// Palette Clicks.
		ChessBoardPalette.onClickListener(x, y, pointer, button);
		// Board Clicks.
		// Adding pieces.
		if(ChessBoardPalette.getTabSelected() == ChessBoardPalette.tabWhite || ChessBoardPalette.getTabSelected() == ChessBoardPalette.tabBlack){
			if((ChessBoardPalette.getSelectedPiece() != null)&&(x < editingBoard.getBoardWidth()*editingBoard.getTileWidth())){
				// Adding the selected peice.
				ChessOwner tempOwner =  ChessBoardPalette.getSelectedPiece().getOwner();
				editingBoard.addPiece(editingBoard.getTileFromCoordinate(x), editingBoard.boardWidth - editingBoard.getTileFromCoordinate(y) - 1, ChessBoardPalette.getSelectedPiece().getClone(tempOwner));
			}
			// TODO: some way to remove chess peices.
		} else if((ChessBoardPalette.getTabSelected() == ChessBoardPalette.tabTiles)&&(x < editingBoard.getBoardWidth()*editingBoard.getTileWidth())){
			// Toggling tiles.
			/*
			editingBoard.setTile(ChessBoard.getTile(
				ChessBoard.getTileFromCoordinate(x),
				ChessBoard.getTileFromCoordinate(y)
			);
			 */
		}

	}

	public static ChessBoard getEditingBoard() {
		return editingBoard;
	}
}
