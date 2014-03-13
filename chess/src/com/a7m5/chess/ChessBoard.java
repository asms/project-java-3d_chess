package com.a7m5.chess;

import java.io.Serializable;
import java.util.ArrayList;

import com.a7m5.chess.chesspieces.ChessOwner;
import com.a7m5.chess.chesspieces.ChessPiece;
import com.a7m5.chess.chesspieces.ChessPieceSet;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Plane;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.Array;

public class ChessBoard implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7954652516619094585L;


	private static ChessPieceSet gamePieceSet;
	private ChessPiece[][] chessPieces;
	private ChessPiece selectedChessPiece = null;
	private ChessOwner turnOwner;
	private ChessOwner checkedPlayer = null;
	private Vector2 whiteCursor = null;
	private Vector2 blackCursor = null;

	private Model tileModel;
	private Array<ModelInstance> tileInstances;
	private Array<ModelInstance> specialTileInstances;

	public static int actualBoardWidth = 512;
	public static int boardWidth = 8; //8 (traditional), 16 (large), 32 (extra large)
	public static int tileWidth = actualBoardWidth / boardWidth;

	public ChessBoard(){
		System.out.println("NEW CHESSBOARD INSTANCE!!! empty const, " + toString());
	}

	public ChessBoard(ChessPieceSet gamePieceSet) {
		chessPieces = new ChessPiece[boardWidth][boardWidth];
		this.gamePieceSet = gamePieceSet;
		System.out.println("NEW CHESSBOARD INSTANCE!!! gamePieceSet const, " + toString());
		
	}

	public static void loadTextures() {
		ResourceGrabber myGrab;
		myGrab = new ResourceGrabber();
		gamePieceSet = new ChessPieceSet(myGrab.getGrabbedPieces());

		// Load from xml defined paths.
		for(int i = 0; i < gamePieceSet.getLength()-1; i++){
			gamePieceSet.getPieceByIndex(i).loadTextures();
		} 
	}


	public void drawBoard(ModelBatch modelBatch, Environment environment) {
	//	System.out.println("Draw board, new.");
		boolean alt = true;
		ModelBuilder modelBuilder = new ModelBuilder();
		if(tileInstances == null) {
			tileInstances = new Array<ModelInstance>();
			tileModel = modelBuilder.createRect(
					0, 0, 0,
					tileWidth, 0, 0,
					tileWidth, 0, -tileWidth,
					0, 0, -tileWidth,
					0, 1, 0,
					new Material(),
					Usage.Position | Usage.Normal);


			for(int y = 0; y < boardWidth; y++) {
				alt =  (1 == y % 2);
				for(int x = 0; x < boardWidth; x++) {
					ModelInstance tileInstance = new ModelInstance(tileModel);
					tileInstance.transform.setToTranslation(x*tileWidth, 0, -y*tileWidth);
					if(alt){
						tileInstance.materials.get(0).set(ColorAttribute.createDiffuse(new Color(0.84f, 0.84f, 0.84f, 1)));
					} else {
						tileInstance.materials.get(0).set(ColorAttribute.createDiffuse(new Color(0, 0.84f, 0.18f, 1)));
					}
					tileInstances.add(tileInstance);

					alt = !alt;	// Alternate colors on rows.
				}
			}
		}
		modelBatch.render(tileInstances, environment);

		if(selectedChessPiece != null) {
			specialTileInstances = new Array<ModelInstance>();
			ArrayList<Vector2> possibleMoves = selectedChessPiece.getPossibleMoves();
			for(Vector2 possibleMove : possibleMoves) {
				ModelInstance tileInstance = new ModelInstance(tileModel);
				tileInstance.transform.setToTranslation((float) (possibleMove.getX()*tileWidth), 0, (float) (-possibleMove.getY()*tileWidth));
				tileInstance.materials.get(0).set(ColorAttribute.createDiffuse(new Color(0, 0.5f, 1, 1)));
				specialTileInstances.add(tileInstance);
			}
			ArrayList<Vector2> possibleAttacks = selectedChessPiece.getPossibleAttacks();
			for(Vector2 possibleAttack : possibleAttacks) {
				ModelInstance tileInstance = new ModelInstance(tileModel);
				tileInstance.transform.setToTranslation((float) (possibleAttack.getX()*tileWidth), 0, (float) (-possibleAttack.getY()*tileWidth));
				tileInstance.materials.get(0).set(ColorAttribute.createDiffuse(new Color(1f, 0.5f, 0, 1)));
				specialTileInstances.add(tileInstance);

			}
			modelBatch.render(specialTileInstances, environment);
		} else {
			specialTileInstances = null;
		}
	}

	@Deprecated
	public void drawBoard(ShapeRenderer shapeRenderer) {
	//	System.out.println("Draw board, old");
		boolean alt = true;
		shapeRenderer.setColor(Color.BLACK);
		shapeRenderer.rect(0,
				0,
				actualBoardWidth,
				actualBoardWidth,
				actualBoardWidth/2,
				actualBoardWidth/2,
				0);

		for(int y = 0; y < boardWidth; y++) {
			alt =  (1 == y % 2);
			for(int x = 0; x < boardWidth; x++) {
				if(alt){
					shapeRenderer.setColor(Color.GREEN);
				} else {
					shapeRenderer.setColor(Color.GRAY);
				}
				shapeRenderer.rect(x*tileWidth + 1,
						y*tileWidth + 1,
						tileWidth-2,
						tileWidth-2
						);

				alt = !alt;	// Alternate colors on rows.
			}
		}

		if(selectedChessPiece != null) {
			ArrayList<Vector2> possibleMoves = selectedChessPiece.getPossibleMoves();
			for(Vector2 possibleMove : possibleMoves) {
				shapeRenderer.setColor(Color.BLUE);
				shapeRenderer.rect((float) (possibleMove.getX() * tileWidth + 1),
						(float) (possibleMove.getY()*tileWidth + 1),
						tileWidth-2,
						tileWidth-2
						);
			}
			ArrayList<Vector2> possibleAttacks = selectedChessPiece.getPossibleAttacks();
			for(Vector2 possibleAttack : possibleAttacks) {
				shapeRenderer.setColor(Color.RED);
				shapeRenderer.rect((float) (possibleAttack.getX() * tileWidth + 1),
						(float) (possibleAttack.getY()*tileWidth + 1),
						tileWidth-2,
						tileWidth-2
						);
			}
		}

		shapeRenderer.rect(512,
				0,
				400,
				512,
				Color.LIGHT_GRAY,
				Color.LIGHT_GRAY,
				Color.WHITE,
				Color.WHITE
				);
	}


	public void drawPieces(SpriteBatch batch) {
		for(int y = 0; y < boardWidth; y++) {
			for(int x = 0; x < boardWidth; x++) {
				ChessPiece chessPiece = chessPieces[x][y];
				TextureRegion textureRegion = new TextureRegion();
				if(chessPiece != null){
					if(chessPiece.getPieceName() != null){

						if(chessPiece.getPieceName().compareTo("Pawn") == 0) {
							if(chessPiece.getOwner() == ChessOwner.WHITE) {
								textureRegion = gamePieceSet.getPieceByName("Pawn").getWhiteTextureRegion();;
							} else {
								textureRegion = gamePieceSet.getPieceByName("Pawn").getBlackTextureRegion();;
							}
						} else if(chessPiece.getPieceName().compareTo("King") == 0) {
							if(chessPiece.getOwner() == ChessOwner.WHITE) {
								textureRegion = gamePieceSet.getPieceByName("King").getWhiteTextureRegion();;
							} else {
								textureRegion = gamePieceSet.getPieceByName("King").getBlackTextureRegion();;
							}
						} else if(chessPiece.getPieceName().compareTo("Queen") == 0) {
							if(chessPiece.getOwner() == ChessOwner.WHITE) {
								textureRegion = gamePieceSet.getPieceByName("Queen").getWhiteTextureRegion();;
							} else {
								textureRegion = gamePieceSet.getPieceByName("Queen").getBlackTextureRegion();;
							}
						} else if(chessPiece.getPieceName().compareTo("Knight") == 0) {
							if(chessPiece.getOwner() == ChessOwner.WHITE) {
								textureRegion = gamePieceSet.getPieceByName("Knight").getWhiteTextureRegion();;
							} else {
								textureRegion = gamePieceSet.getPieceByName("Knight").getBlackTextureRegion();;
							}
						} else if(chessPiece.getPieceName().compareTo("Rook") == 0) {
							if(chessPiece.getOwner() == ChessOwner.WHITE) {
								textureRegion = gamePieceSet.getPieceByName("Rook").getWhiteTextureRegion();;
							} else {
								textureRegion = gamePieceSet.getPieceByName("Rook").getBlackTextureRegion();;
							}
						} else if(chessPiece.getPieceName().compareTo("Bishop") == 0) {
							if(chessPiece.getOwner() == ChessOwner.WHITE) {
								textureRegion = gamePieceSet.getPieceByName("Bishop").getWhiteTextureRegion();;
							} else {
								textureRegion = gamePieceSet.getPieceByName("Bishop").getBlackTextureRegion();;
							}
						} else {
							break;
						}
					}	


					int positionX;
					int positionY;

					if(chessPiece.isAnimating()) {
						double speed = chessPiece.getSpeed();
						Vector2 animationPosition = chessPiece.getAnimationPosition();
						Vector2 differenceVector = new Vector2(
								x*tileWidth - animationPosition.getX(),
								y*tileWidth - animationPosition.getY());
						if(differenceVector.getMagnitude() <= speed) {
							chessPiece.stopAnimation();
						} else {
							Vector2 movementVector = differenceVector.getUnitVector().multiply(speed);
							animationPosition = animationPosition.add(movementVector);
							chessPiece.setAnimationPosition(animationPosition);
						}
						positionX = (int) Math.floor(animationPosition.getX());
						positionY = (int) Math.floor(animationPosition.getY());
					} else {
						positionX = x*tileWidth;
						positionY = y*tileWidth;
					}
					batch.draw(textureRegion, positionX, positionY, tileWidth, tileWidth);
				}
			}
		}
	}

	@Deprecated
	public void drawCursors(ShapeRenderer shapeRenderer) {
		if(whiteCursor != null) {
			shapeRenderer.setColor(Color.WHITE);
			shapeRenderer.circle((float) whiteCursor.getX(), (float) whiteCursor.getY(), 6);
		}
		if(blackCursor != null) {
			shapeRenderer.setColor(Color.BLACK);
			shapeRenderer.circle((float) blackCursor.getX(), (float) blackCursor.getY(), 6);
		}
	}

	public void drawCursors(ModelBatch modelBatch, Environment environment,
			PerspectiveCamera camera) {
		ModelBuilder modelBuilder = new ModelBuilder();
		Model cursorModel = modelBuilder.createSphere(
				16f, 16f, 16f,
				20, 20,
				new Material(ColorAttribute.createDiffuse(Color.ORANGE)),
				Usage.Position | Usage.Normal
				);
		ModelInstance cursor = new ModelInstance(cursorModel);

		try {
			Ray mouseRay = camera.getPickRay(ChessGame3D.width/2, ChessGame3D.height/2);
			com.badlogic.gdx.math.Vector3 intersectionVector = new com.badlogic.gdx.math.Vector3();
			Intersector.intersectRayPlane(mouseRay, new Plane(new com.badlogic.gdx.math.Vector3(0, 1, 0), new com.badlogic.gdx.math.Vector3(0, 0, 0)), intersectionVector);
			//intersectionVector.scl(1, 0, -1);
			cursor.transform.setToTranslation(intersectionVector);
		} catch(Exception e) {
			e.printStackTrace();
		}


		modelBatch.render(cursor, environment);
	}


	public void addPiece(int x, int y, ChessPiece chessPiece) {
		System.out.println("Add piece.");
		chessPiece.register(this, new Vector2(x, y));
		chessPieces[x][y] = chessPiece;
	}

	public void start() {
		System.out.println("Start");
		clear();
	}

	public void restart() {
		System.out.println("restart");
		start();
	}

	public void clear() {
		System.out.println("Clear");
		chessPieces = new ChessPiece[boardWidth][boardWidth];
	}
	public static int getTileFromCoordinate(int x) {
		return (int) Math.floor((float) x / (float) tileWidth);
	}
	public ChessPiece getChessPieceByXY(int x, int y) throws IndexOutOfBoundsException {
		int tileX = getTileFromCoordinate(x);
		int tileY = getTileFromCoordinate(y);
		return chessPieces[tileX][tileY];
	}
	public ChessPiece getChessPieceByXYTile(int x, int y) throws IndexOutOfBoundsException {
		return chessPieces[x][y];
	}
		public ChessPiece getChessPieceByVector(Vector2 vector) {
		return chessPieces[(int) vector.getX()][(int) vector.getY()];
	}
	public void moveChessPiece(int tileX0, int tileY0, int tileX1, int tileY1) {
		chessPieces[tileX1][tileY1] = chessPieces[tileX0][tileY0];
		chessPieces[tileX0][tileY0] = null;
	}
	public ChessPiece getSelectedChessPiece() {
		return selectedChessPiece ;
	}
	public void setSelectedChessPiece(ChessPiece chessPiece) {
		if(chessPiece == null) {
			selectedChessPiece = null;
		} else if(chessPiece.getOwner() == ChessGame3D.getOwner()) {
			this.selectedChessPiece = chessPiece;
		}
	}
	public void moveChessPiece(Vector2 oldPosition, Vector2 newPosition) {
		ChessPiece chessPiece = chessPieces[(int) oldPosition.getX()][(int) oldPosition.getY()];
		if(chessPiece.getOwner() == ChessOwner.WHITE) {
			turnOwner  = ChessOwner.BLACK;
		} else {
			turnOwner = ChessOwner.WHITE;
		}
		chessPieces[(int) oldPosition.getX()][(int) oldPosition.getY()] = null;
		chessPiece.setPosition(newPosition);

		//Animation
		chessPiece.animate(oldPosition.multiply(tileWidth));

		chessPieces[(int) newPosition.getX()][(int) newPosition.getY()] = chessPiece;
		selectedChessPiece = null;
	}
	public void setChessPieces(ChessPiece[][] chessPieces) {
		this.chessPieces = chessPieces;
	}
	public void attackChessPiece(Vector2 vector1, Vector2 vector2) {
		ChessPiece attacker = chessPieces[(int) vector1.getX()][(int) vector1.getY()];
		if(attacker.getOwner() == ChessOwner.WHITE) {
			turnOwner  = ChessOwner.BLACK;
		} else {
			turnOwner = ChessOwner.WHITE;
		}
		chessPieces[(int) vector1.getX()][(int) vector1.getY()] = null;
		attacker.setPosition(vector2);
		chessPieces[(int) vector2.getX()][(int) vector2.getY()] = attacker;
		selectedChessPiece = null;
	}
	public int getBoardWidth() {
		return actualBoardWidth / tileWidth;
	}
	public void gameOver(int winner) {
		// TODO Auto-generated method stub

	}
	public void setTurnOwner(ChessOwner owner) {
		this.turnOwner = owner;
	}
	public ChessOwner getTurnOwner() {
		return turnOwner;
	}
	public ChessPiece[][] getChessPieces() {
		return chessPieces;
	}
	public void setCheck(ChessOwner opponent) {
		checkedPlayer = opponent;
	}
	public ChessOwner getCheckedPlayer() {
		return checkedPlayer;
	}
	public void setCursor(ChessOwner owner, Vector2 vector) {
		if(owner == ChessOwner.WHITE) {
			whiteCursor = vector;
		} else if(owner == ChessOwner.BLACK) {
			blackCursor = vector;
		}
	}

	public static void setBoardWidth(int boardWidth) {
		ChessBoard.boardWidth = boardWidth;
		tileWidth = actualBoardWidth / boardWidth;
	}
}
