// This class will be the main editor GUI window. The alternate view from the game play window.
// TODO: Make this interface similar to the game but
// with a side bar with things either for:
// 						- Editing game pieces (Black/White, or NPC)
// 						- Editing game boards. Set size and give palette of pieces.
// with save/open buttons for files and resources.
// Way to organise things into game packages containing files for a collection of pieces and a board.

package com.a7m5.chess.editor;

import com.a7m5.chess.ChessBoard;
import com.a7m5.chess.chesspieces.Bishop;
import com.a7m5.chess.chesspieces.ChessOwner;
import com.a7m5.chess.chesspieces.King;
import com.a7m5.chess.chesspieces.Knight;
import com.a7m5.chess.chesspieces.Pawn;
import com.a7m5.chess.chesspieces.Queen;
import com.a7m5.chess.chesspieces.Rook;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

public class ChessGameEditor implements ApplicationListener {
	private OrthographicCamera camera;
	private SpriteBatch batch;
	private ChessBoard editingBoard;
	private ChessBoardPalette editingPalette;

	public ChessGameEditor(int requestedBoardSize) {
		editingBoard = new ChessBoard();
		editingBoard.setBoardWidth(requestedBoardSize);
		
		editingPalette = new ChessBoardPalette(522,10);
		editingPalette.addPiece(0, 0, new Pawn(ChessOwner.WHITE));
		editingPalette.addPiece(1, 0, new Bishop(ChessOwner.WHITE));
		editingPalette.addPiece(2, 0, new King(ChessOwner.WHITE));
		editingPalette.addPiece(0, 1, new Queen(ChessOwner.WHITE));
		editingPalette.addPiece(1, 1, new Rook(ChessOwner.WHITE));
		editingPalette.addPiece(2, 1, new Knight(ChessOwner.WHITE));
		editingPalette.addPiece(0, 2, new Pawn(ChessOwner.BLACK));
		
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
		// TODO Auto-generated method stub
	}

	@Override
	public void render() {
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);


		ShapeRenderer shapeRenderer = new ShapeRenderer();
		shapeRenderer.setProjectionMatrix(camera.combined);

		shapeRenderer.begin(ShapeType.Filled);
		editingBoard.drawBoard(shapeRenderer);
		editingPalette.drawPalette(shapeRenderer);
		shapeRenderer.end();

		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		editingPalette.drawPieces(batch);
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
