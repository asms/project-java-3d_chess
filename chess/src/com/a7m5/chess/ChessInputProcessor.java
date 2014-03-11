package com.a7m5.chess;

import com.a7m5.chess.chesspieces.ChessOwner;
import com.a7m5.networking.NetworkCommand;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Plane;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.math.Vector3;

public class ChessInputProcessor extends FlyModeCameraControls {

	public ChessInputProcessor(PerspectiveCamera camera) {
		super(camera);
	}
	
	@Override
	public boolean keyDown (int keycode) {
		super.keyDown(keycode);
		if(keycode == Keys.T){
			if(ChessGame3D.getOwner() == ChessOwner.BLACK){
				ChessGame3D.setOwner(ChessOwner.WHITE);
			} else if (ChessGame3D.getOwner() == ChessOwner.WHITE){
				ChessGame3D.setOwner(ChessOwner.BLACK);
			}
		}
		return false;
	}

	@Override
	public boolean keyUp (int keycode) {
		super.keyUp(keycode);
		return false;
	}

	@Override
	public boolean keyTyped (char character) {
		super.keyTyped(character);
		return false;
	}

	@Override
	public boolean touchDown (int x, int y, int pointer, int button) {
		super.touchDown(x, y, pointer, button);
		
		Ray mouseRay = camera.getPickRay(ChessGame3D.width/2, ChessGame3D.height/2);
		Vector3 intersectionVector = new Vector3();
		Intersector.intersectRayPlane(mouseRay, new Plane(new Vector3(0, 1, 0), new Vector3(0, 0, 0)), intersectionVector);
		intersectionVector.scl(1, 0, -1);
		ChessGame3D.onClickListener((int) intersectionVector.x, (int) intersectionVector.z, pointer, button);
		return false;
	}

	@Override
	public boolean touchUp (int x, int y, int pointer, int button) {
		super.touchUp(x, y, pointer, button);
		return false;
	}

	@Override
	public boolean touchDragged (int x, int y, int pointer) {
		super.touchDragged(x, y, pointer);
		return false;
	}

	@Override
	public boolean scrolled (int amount) {
		super.scrolled(amount);
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		super.mouseMoved(screenX, screenY);
		if(Gdx.input.isCursorCatched()) {
			onCameraMove();
		}
		
		return false;

	}
	
	@Override
	public void update(float seconds) {
		super.update(seconds);
		if(forward ||
				backward ||
				left ||
				right ||
				up ||
				down)
			{
				onCameraMove();
			}
	}
	
	public void onCameraMove() {
		
		NetworkCommand command = new NetworkCommand(NetworkCommand.CAMERA_MOVED);
		float[][] args = new float[4][3];
		// Camera Position
		args[0][0] = camera.position.x;
		args[0][1] = camera.position.y;
		args[0][2] = camera.position.z;
		
		// Camera Direction
		args[1][0] = camera.direction.x;
		args[1][1] = camera.direction.y;
		args[1][2] = camera.direction.z;
		
		// Camera Up
		args[2][0] = camera.up.x;
		args[2][1] = camera.up.y;
		args[2][2] = camera.up.z;
		command.setFloat2DArray(args);
		ChessGame3D.getClient().send(command);
	}
}