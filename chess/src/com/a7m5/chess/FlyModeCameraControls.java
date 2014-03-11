package com.a7m5.chess;

import java.nio.IntBuffer;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Cursor;
import org.lwjgl.input.Mouse;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.BufferUtils;

public class FlyModeCameraControls implements InputProcessor {

	private static Cursor emptyCursor;
	private int previousMousePositionX;
	private int previousMousePositionY;
	
	protected boolean forward = false;
	protected boolean backward = false;
	protected boolean left = false;
	protected boolean right = false;
	protected boolean up = false;
	protected boolean down = false;
	
	protected Camera camera;
	protected int width;
	protected int height;
	
	public FlyModeCameraControls(PerspectiveCamera camera) {
		this.camera = camera;
		this.width = Gdx.graphics.getWidth();
		this.height = Gdx.graphics.getHeight();
		Gdx.input.setInputProcessor(this);
	}
	
	@Override
	public boolean keyDown (int keycode) {
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
				Gdx.input.setCursorPosition(width/2, height/2);
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
			
			camera.rotateAround(camera.position, Vector3.Y, (float) -deltaX/ 10f);
			camera.update();
			
			Vector3 axis = camera.direction.cpy().crs(camera.up).nor();
			camera.rotate(axis, (float) -deltaY / 10f);
			
			previousMousePositionX = screenX;
			previousMousePositionY = screenY;
			
		}
		
		return false;

	}
	
	public void update(float seconds) {

		float movementFactor = 64f * seconds;

		Vector3 rightDirection = camera.direction.cpy().crs(camera.up).nor().scl(movementFactor);

		if(forward) {
			camera.translate(camera.direction.cpy().scl(movementFactor));
		}
		if(left) {
			camera.translate(rightDirection.scl(-1));
		}
		if(backward) {
			camera.translate(camera.direction.cpy().scl(-movementFactor));
		}
		if(right) {
			camera.translate(rightDirection);
		}
		if(up) {
			camera.translate(0, movementFactor, 0);
		}
		if(down) {
			camera.translate(0, -movementFactor, 0);
		}
		camera.update();
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

	@Override
	public boolean keyTyped(char character) {
		// TODO Auto-generated method stub
		return false;
	}
}