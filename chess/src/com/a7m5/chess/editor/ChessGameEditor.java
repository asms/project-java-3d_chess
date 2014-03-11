// This class will be the main editor GUI window. The alternate view from the game play window.
// TODO: Make this interface similar to the game but
// with a side bar with things either for:
// 						- Editing game pieces (Black/White, or NPC)
// 						- Editing game boards. Set size and give palette of pieces.
// with save/open buttons for files and resources.
// Way to organise things into game packages containing files for a collection of pieces and a board.

package com.a7m5.chess.editor;

import com.a7m5.chess.ChessBoard;
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
	private ChessBoard editingBoard;
	private ChessBoardPalette editingPalette;
	private ChessPieceSet pieceSet;

	public ChessGameEditor(int requestedBoardSize, ChessPieceSet editorSet) {
		// Make the editing board of the correct size.
		pieceSet = editorSet;
		editingBoard = new ChessBoard(editorSet);
		editingBoard.setBoardWidth(requestedBoardSize);
		// Creates the palette in the correct position.
		editingPalette = new ChessBoardPalette(522,10);
	}

	@Override
	public void create() {
		ChessBoardPalette.loadTextures();
		ChessBoard.loadTextures();
		

		EditorInputProcessor inputProcessor = new EditorInputProcessor();
		Gdx.input.setInputProcessor(inputProcessor);

		camera = new OrthographicCamera(512, 512);
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

}
