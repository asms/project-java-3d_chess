// This class will be the main editor GUI window. The alternate view from the game play window.
// TODO: Make this interface similar to the game but
// with a side bar with things either for:
// 						- Editing game pieces (Black/White, or NPC)
// 						- Editing game boards. Set size and give palette of pieces.
// with save/open buttons for files and resources.
// Way to organise things into game packages containing files for a collection of pieces and a board.

package com.a7m5.chess.editor;

import java.io.File;

import javax.swing.JFileChooser;

import com.a7m5.chess.ChessBoard;
import com.a7m5.chess.ChessGame3D;
import com.a7m5.chess.ResourceGrabber;
import com.a7m5.chess.chesspieces.ChessOwner;
import com.a7m5.chess.chesspieces.ChessPiece;
import com.a7m5.chess.chesspieces.ChessPieceSet;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

public class ChessGameEditor implements ApplicationListener {
	private OrthographicCamera camera;
	private SpriteBatch batch;
	private static ChessBoard editingBoard;

	private static ChessBoardPalette editingPalette;
	private static ChessPieceSet editorPieceSet;

	public ChessGameEditor(int requestedBoardSize) {
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
			editorPieceSet = new ChessPieceSet(myGrab.getGrabbedPieces());
			// Make the editing board of the correct size.
			editingBoard = new ChessBoard(editorPieceSet);
			ChessBoard.setBoardWidth(requestedBoardSize);
			// Creates the palette in the correct position.
			editingPalette = new ChessBoardPalette(522,10,editorPieceSet);
//		}
	}

	@Override
	public void create() {
		ChessBoardPalette.loadTextures();
		ChessBoard.loadTextures();

		EditorInputProcessor inputProcessor = new EditorInputProcessor();
		Gdx.input.setInputProcessor(inputProcessor);

		camera = new OrthographicCamera(512, 512+330);
		camera.setToOrtho(false);

		batch = new SpriteBatch();
	}

	@Override
	public void resize(int width, int height) {
		editingPalette.resize(width, height);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void render() {
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

		ShapeRenderer shapeRenderer = new ShapeRenderer();
		shapeRenderer.setProjectionMatrix(camera.combined);

		shapeRenderer.begin(ShapeType.Filled);
		editingBoard.drawBoard(shapeRenderer);
		editingPalette.drawBackground(shapeRenderer);
		shapeRenderer.end();

		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		editingPalette.drawElements(batch);
		editingBoard.drawPieces(batch);
		batch.end();
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
		batch.dispose();
	}


	public static void onClickListener(int x, int y, int pointer, int button) {
		System.out.println(x + ":" + y);
		// Palette Clicks.
		ChessBoardPalette.onClickListener(x, y, pointer, button);
		// Board Clicks.
		// Adding pieces.
		if(ChessBoardPalette.getTabSelected() == ChessBoardPalette.tabWhite || ChessBoardPalette.getTabSelected() == ChessBoardPalette.tabBlack){
			if((ChessBoardPalette.getSelectedPiece() != null)&&(x < editingBoard.getBoardWidth()*ChessBoard.getTileWidth())){
				// Adding the selected peice.
				ChessOwner tempOwner =  ChessBoardPalette.getSelectedPiece().getOwner();
				editingBoard.addPiece(ChessBoard.getTileFromCoordinate(x), ChessBoard.boardWidth - ChessBoard.getTileFromCoordinate(y) - 1, ChessBoardPalette.getSelectedPiece().getClone(tempOwner));
			}
			// TODO: some way to remove chess peices.
		} else if((ChessBoardPalette.getTabSelected() == ChessBoardPalette.tabTiles)&&(x < editingBoard.getBoardWidth()*ChessBoard.getTileWidth())){
			// Toggling tiles.
			editingBoard.toggleTile(ChessBoard.getTileFromCoordinate(x),  ChessBoard.boardWidth - ChessBoard.getTileFromCoordinate(y) - 1);
		}

	}

	public static ChessBoard getEditingBoard() {
		return editingBoard;
	}
}
