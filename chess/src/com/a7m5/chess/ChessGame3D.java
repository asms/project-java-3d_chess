package com.a7m5.chess;


import java.awt.Graphics;
import java.nio.IntBuffer;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Cursor;
import org.lwjgl.input.Mouse;

import com.a7m5.chess.chesspieces.Bishop;
import com.a7m5.chess.chesspieces.ChessOwner;
import com.a7m5.chess.chesspieces.King;
import com.a7m5.chess.chesspieces.Knight;
import com.a7m5.chess.chesspieces.Pawn;
import com.a7m5.chess.chesspieces.Queen;
import com.a7m5.chess.chesspieces.Rook;
import com.a7m5.networking.Client;
import com.a7m5.networking.Server;
import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.GL11;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.utils.FirstPersonCameraController;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.utils.BufferUtils;

public class ChessGame3D implements ApplicationListener {
	
	public static int width;
	public static int height;
	
	//3D stuff
	public static PerspectiveCamera cam;
	public ModelBatch modelBatch;
	public Model model;
	public ModelInstance instance;


	private SpriteBatch batch;
	private Sprite yourTurnSprite;
	private Sprite waitSprite;
	private int port;
	private String address;
	private Texture yourTurnTexture;
	private Texture waitTexture;
	private ChessInputProcessor inputProcessor;

	private static Server server = null;
	private static Client client = null;
	private static Thread clientThread = null;
	private static Thread serverThread = null;
	private static ChessOwner owner;

	public ChessGame3D(ChessOwner chessOwner, String address, int port) {
		setOwner(chessOwner);
		setAddress(address);
		setPort(port);
	}

	@Override
	public void create() {
		//3D
		modelBatch = new ModelBatch();

		cam = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		cam.position.set(256f, 256f, 128f);
		cam.lookAt(256f,0, -256f);
		cam.near = 0.1f;
		cam.far = 1024f;
		cam.update();

		/*
		 * coordinate system
		 * x: left (negative) to right (positive)
		 * y: up (positive) and down (negative)
		 * z: forward (negative) and backward (positive)
		 */

		ModelBuilder modelBuilder = new ModelBuilder();
		model = modelBuilder.createSphere(612f, 612f, 612f, 16, 16,  new Material(ColorAttribute.createDiffuse(Color.GREEN)),
				Usage.Position | Usage.Normal);
		instance = new ModelInstance(model);




		ChessBoard.loadTextures();

		inputProcessor = new ChessInputProcessor();
		Gdx.input.setInputProcessor(inputProcessor);

		width = Gdx.graphics.getWidth();
		height = Gdx.graphics.getHeight();

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

		modelBatch.dispose();
		model.dispose();
	}

	@Override
	public void render() {		
		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		Gdx.gl.glClearColor(0.5f, 0.75f, 1f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT | (Gdx.graphics.getBufferFormat().coverageSampling?GL20.GL_COVERAGE_BUFFER_BIT_NV:0));
		
		modelBatch.begin(cam);
		//modelBatch.render(instance);
		modelBatch.end();



		if(client != null && clientThread.isAlive()) {
			inputProcessor.move(cam, Gdx.graphics.getRawDeltaTime());
			
			
			
			
			ShapeRenderer shapeRenderer = new ShapeRenderer();
			shapeRenderer.setProjectionMatrix(cam.combined);
			shapeRenderer.rotate(1, 0, 0, -90);

			shapeRenderer.begin(ShapeType.Filled);
			client.board.drawBoard(shapeRenderer);
			client.board.drawCursors(shapeRenderer);
			shapeRenderer.end();

			batch.setProjectionMatrix(shapeRenderer.getProjectionMatrix());
			batch.setTransformMatrix(shapeRenderer.getTransformMatrix());
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
			board.setTurnOwner(ChessOwner.WHITE);
			for(int x = 0; x < 2; x++) {
				ChessOwner owner = (x == 0 ? ChessOwner.BLACK : ChessOwner.WHITE);
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
				board.addPiece(x, 1, new Pawn(ChessOwner.BLACK));
				board.addPiece(x, 6, new Pawn(ChessOwner.WHITE));
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

	private void setPort(int port) {
		this.port = port;
	}

	private void setAddress(String address) {
		this.address = address;
	}

	public static PerspectiveCamera getCamera() {
		return cam;
	}
}