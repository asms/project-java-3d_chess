package com.a7m5.chess;

import com.a7m5.chess.chesspieces.Bishop;
import com.a7m5.chess.chesspieces.ChessOwner;
import com.a7m5.chess.chesspieces.King;
import com.a7m5.chess.chesspieces.Knight;
import com.a7m5.chess.chesspieces.Pawn;
import com.a7m5.chess.chesspieces.Queen;
import com.a7m5.chess.chesspieces.Rook;
import com.a7m5.networking.Client;
import com.a7m5.networking.Server;
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

public class GdxChessGame implements ApplicationListener {
	private OrthographicCamera camera;
	private SpriteBatch batch;
	private Sprite yourTurnSprite;
	private Sprite waitSprite;
	private int port;
	private String address;
	private Texture yourTurnTexture;
	private Texture waitTexture;

	private static Server server = null;
	private static Client client = null;
	private static Thread clientThread = null;
	private static Thread serverThread = null;
	private static ChessOwner owner;

	public GdxChessGame(String address, int port) {
		this.address = address;
		this.port = port;
	}

	@Override
	public void create() {
		ChessBoard.loadTextures();

		ChessInputProcessor inputProcessor = new ChessInputProcessor();
		Gdx.input.setInputProcessor(inputProcessor);

		//float w = Gdx.graphics.getWidth();
		//float h = Gdx.graphics.getHeight();

		camera = new OrthographicCamera(512, 512);
		camera.setToOrtho(false);

		batch = new SpriteBatch();
		
		//Your Turn
		yourTurnTexture = new Texture(Gdx.files.internal("data/your_turn.png"));
		yourTurnTexture.setFilter(TextureFilter.Linear, TextureFilter.Linear);

		TextureRegion yourTurnRegion = new TextureRegion(yourTurnTexture, 0, 0, 128, 128);

		yourTurnSprite = new Sprite(yourTurnRegion);
		yourTurnSprite.setOrigin(yourTurnSprite.getWidth()/2, yourTurnSprite.getHeight()/2);
		yourTurnSprite.setPosition(612-64, 412-64-128-32);
		
		//Wait
		waitTexture = new Texture(Gdx.files.internal("data/wait.png"));
		waitTexture.setFilter(TextureFilter.Linear, TextureFilter.Linear);


		TextureRegion waitRegion = new TextureRegion(waitTexture, 0, 0, 128, 128);

		waitSprite = new Sprite(waitRegion);
		waitSprite.setOrigin(waitSprite.getWidth()/2, waitSprite.getHeight()/2);
		waitSprite.setPosition(612-64, 412-64-128-32);
		
		client = new Client(address, port);
		clientThread = new Thread(client);
		clientThread.start();
	}

	@Override
	public void dispose() {
		batch.dispose();
		yourTurnTexture.dispose();
		waitTexture.dispose();
	}

	@Override
	public void render() {		
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

		if(client != null && clientThread.isAlive()) {
			ShapeRenderer shapeRenderer = new ShapeRenderer();
			shapeRenderer.setProjectionMatrix(camera.combined);

			shapeRenderer.begin(ShapeType.Filled);
			client.board.drawBoard(shapeRenderer);
			shapeRenderer.end();

			batch.setProjectionMatrix(camera.combined);
			batch.begin();
			if(getOwner() == getClient().board.getTurnOwner()) {
				yourTurnSprite.draw(batch);
			} else {
				waitSprite.draw(batch);
			}
			client.board.drawPieces(batch);
			batch.end();
		}
	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	public static void startServer(int port) {
		if(server == null && serverThread == null) {
			ChessBoard board = new ChessBoard();
			board.setTurnOwner(ChessOwner.TOP);
			for(int x = 0; x < 2; x++) {
				ChessOwner owner = (x == 0 ? ChessOwner.BOTTOM : ChessOwner.TOP);
				board.addPiece(0, (x == 0 ? 0 : 7), new Rook(owner));
				board.addPiece(1, (x == 0 ? 0 : 7), new Knight(owner));
				board.addPiece(2, (x == 0 ? 0 : 7), new Bishop(owner));
				board.addPiece(3, (x == 0 ? 0 : 7), new Queen(owner));
				board.addPiece(4, (x == 0 ? 0 : 7), new King(owner));
				board.addPiece(5, (x == 0 ? 0 : 7), new Bishop(owner));
				board.addPiece(6, (x == 0 ? 0 : 7), new Knight(owner));
				board.addPiece(7, (x == 0 ? 0 : 7), new Rook(owner));
			}
			
			for(int x = 0; x < 8; x++) {
				board.addPiece(x, 1, new Pawn(ChessOwner.BOTTOM));
				board.addPiece(x, 6, new Pawn(ChessOwner.TOP));
			}
			server = new Server(port, board);
			serverThread = new Thread(server);
			serverThread.start();
		}

	}
	
	public static void killServer() throws InterruptedException {
		if(server != null && serverThread != null) {
			if(serverThread.isAlive()) {
				server.kill();
			}
		}
		server = null;
		serverThread = null;
	}


	public static void onClickListener(int x, int y, int pointer, int button) {
		if(client != null) {
			client.onClickListener(x, y, pointer, button);
		}
	}

	public static void clearBoard() {
		if(client != null) {
			client.board.clear();
		}
	}

	public static void restartBoard() {
		client.board.restart();
	}

	public static Client getClient() {
		return client;
	}

	public static ChessOwner getOwner() {
		return owner;
	}

	public static void setOwner(ChessOwner arg) {
		owner = arg;
	}
}
