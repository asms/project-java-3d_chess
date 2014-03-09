package com.a7m5.chess;

import com.a7m5.chess.chesspieces.ChessOwner;
import com.a7m5.networking.NetworkCommand;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Plane;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;

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
	
	public void update(float seconds) {
		super.update(seconds);
	}
}