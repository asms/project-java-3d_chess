package com.a7m5.chess;

import java.nio.IntBuffer;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Cursor;
import org.lwjgl.input.Mouse;

import com.a7m5.chess.chesspieces.ChessOwner;
import com.a7m5.networking.NetworkCommand;
import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.utils.FirstPersonCameraController;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Plane;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.BufferUtils;

public class ChessInputProcessor implements InputProcessor {

	private static Cursor emptyCursor;
	private int previousMousePositionX = ChessGame3D.width / 2;
	private int previousMousePositionY = ChessGame3D.height / 2;
	
	private boolean forward = false;
	private boolean backward = false;
	private boolean left = false;
	private boolean right = false;
	private boolean up = false;
	private boolean down = false;
	
	@Override
	public boolean keyDown (int keycode) {
		if(keycode == Keys.T){
			if(ChessGame3D.getOwner() == ChessOwner.BLACK){
				ChessGame3D.setOwner(ChessOwner.WHITE);
			} else if (ChessGame3D.getOwner() == ChessOwner.WHITE){
				ChessGame3D.setOwner(ChessOwner.BLACK);
			}
		}
		if(keycode == Keys.W) {
			forward = true;
		}
		if(keycode == Keys.A) {
			left = true;
		}
		if(keycode == Keys.S) {
		backward = true;
		}
		if(keycode == Keys.D) {
			right = true;
		}
		if(keycode == Keys.SPACE) {
			up = true;
		}
		if(keycode == Keys.SHIFT_LEFT) {
			down = true;
		}
		if(keycode == Keys.ESCAPE) {
			try {
				setHWCursorVisible(true);
				Gdx.input.setCursorPosition(ChessGame3D.width/2, ChessGame3D.height/2);
				Gdx.input.setCursorCatched(false);
			
			} catch (LWJGLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return false;
	}

	@Override
	public boolean keyUp (int keycode) {
		if(keycode == Keys.W) {
			forward = false;
		}
		if(keycode == Keys.A) {
			left = false;
		}
		if(keycode == Keys.S) {
		backward = false;
		}
		if(keycode == Keys.D) {
			right = false;
		}
		if(keycode == Keys.SPACE) {
			up = false;
		}
		if(keycode == Keys.SHIFT_LEFT) {
			down = false;
		}
		return false;
	}

	@Override
	public boolean keyTyped (char character) {
		if(character == '1') {
			ChessGame3D.setOwner(ChessOwner.WHITE);
		} else if(character == '2') {
			ChessGame3D.setOwner(ChessOwner.BLACK);
		}
		return false;
	}

	@Override
	public boolean touchDown (int x, int y, int pointer, int button) {
		if(!Gdx.input.isCursorCatched()) {
			try {
				setHWCursorVisible(false);
				Gdx.input.setCursorCatched(true);
				previousMousePositionX = x;
				previousMousePositionY = y;
			} catch (LWJGLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
		Ray mouseRay = ChessGame3D.getCamera().getPickRay(ChessGame3D.width/2, ChessGame3D.height/2);
		Vector3 intersectionVector = new Vector3();
		Intersector.intersectRayPlane(mouseRay, new Plane(new Vector3(0, 1, 0), new Vector3(0, 0, 0)), intersectionVector);
		intersectionVector.scl(1, 0, -1);
		ChessGame3D.onClickListener((int) intersectionVector.x, (int) intersectionVector.z, pointer, button);
		System.out.println("x: " + x + ", y:" + y);
		System.out.println("x: " + (int) intersectionVector.x + ", y:" + (int) intersectionVector.z);
		return false;
	}

	@Override
	public boolean touchUp (int x, int y, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchDragged (int x, int y, int pointer) {
		return false;
	}

	@Override
	public boolean scrolled (int amount) {
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		if(Gdx.input.isCursorCatched()) {
			int deltaY = (screenY-previousMousePositionY);
			int deltaX = (screenX-previousMousePositionX);
			PerspectiveCamera cam = ChessGame3D.getCamera();
			
			cam.rotateAround(cam.position, Vector3.Y, (float) -deltaX/ 10f);
			
			cam.update();
			
			Vector3 axis = cam.direction.cpy().crs(cam.up).nor();
			
			cam.rotate(axis, (float) -deltaY / 10f);
			
			
			
			previousMousePositionX = screenX;
			previousMousePositionY = screenY;
	
			ChessGame3D.getCamera().update();
	
			Ray mouseRay = ChessGame3D.getCamera().getPickRay(ChessGame3D.width/2, ChessGame3D.height/2);
			Vector3 intersectionVector = new Vector3();
			Intersector.intersectRayPlane(mouseRay, new Plane(new Vector3(0, 1, 0), new Vector3(0, 0, 0)), intersectionVector);
			intersectionVector.scl(1, 0, -1);
	
			NetworkCommand command = new NetworkCommand();
			command.setCommand(NetworkCommand.MOUSE_MOVE);
			com.a7m5.chess.Vector2[] vector2Array = { new com.a7m5.chess.Vector2(intersectionVector.x, intersectionVector.z) };
			command.setVectorArray(vector2Array);
			ChessGame3D.getClient().send(command);
		}
		
		return false;

	}
	
	public void move(PerspectiveCamera cam, float seconds) {

		float movementFactor = 128f * seconds;
		
		Vector3 rightDirection = cam.direction.cpy().crs(cam.up).nor().scl(movementFactor);

		if(left) {
			ChessGame3D.getCamera().translate(rightDirection.scl(-1));
		} else if(right) {
			ChessGame3D.getCamera().translate(rightDirection);
		}
		
		ChessGame3D.getCamera().update();
		
		if(forward) {
			ChessGame3D.getCamera().translate(cam.direction.cpy().scl(movementFactor));
		} else if(backward) {
			ChessGame3D.getCamera().translate(cam.direction.cpy().scl(-movementFactor));
		}
		
		if(up) {
			ChessGame3D.getCamera().translate(0, movementFactor, 0);
		} else if(down) {
			ChessGame3D.getCamera().translate(0, -movementFactor, 0);
		}
		
		ChessGame3D.getCamera().update();
	}
	
	public static void setHWCursorVisible(boolean visible) throws LWJGLException {
		if (Gdx.app.getType() != ApplicationType.Desktop && Gdx.app instanceof LwjglApplication)
			return;
		if (emptyCursor == null) {
			if (Mouse.isCreated()) {
				int min = org.lwjgl.input.Cursor.getMinCursorSize();
				IntBuffer tmp = BufferUtils.newIntBuffer(min * min);
				emptyCursor = new org.lwjgl.input.Cursor(min, min, min / 2, min / 2, 1, tmp, null);
			} else {
				throw new LWJGLException(
						"Could not create empty cursor before Mouse object is created");
			}
		}
		if (Mouse.isInsideWindow())
			Mouse.setNativeCursor(visible ? null : emptyCursor);
	}
}