package com.a7m5.chess;

import com.a7m5.chess.chesspieces.ChessOwner;
import com.a7m5.networking.NetworkCommand;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Plane;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;

public class ChessInputProcessor implements InputProcessor {
	@Override
	public boolean keyDown (int keycode) {
		return false;
	}

	@Override
	public boolean keyUp (int keycode) {
		return false;
	}

	@Override
	public boolean keyTyped (char character) {
		if(character == 'w') {
			ChessGame3D.setOwner(ChessOwner.WHITE);
		} else if(character == 'b') {
			ChessGame3D.setOwner(ChessOwner.BLACK);
		}
		return false;
	}

	@Override
	public boolean touchDown (int x, int y, int pointer, int button) {
		Ray mouseRay = ChessGame3D.getCamera().getPickRay(x, y);
		Vector3 intersectionVector = new Vector3();
		Intersector.intersectRayPlane(mouseRay, new Plane(new Vector3(0, 1, 0), new Vector3(0, 0, 0)), intersectionVector);
		intersectionVector = new Vector3(0, 0, -ChessBoard.actualBoardWidth).sub(intersectionVector).scl(-1);
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
		/*
		NetworkCommand command = new NetworkCommand();
		command.setCommand(NetworkCommand.MOUSE_MOVE);
		com.a7m5.chess.Vector2[] vector2Array = { new com.a7m5.chess.Vector2(screenX, ChessBoard.actualBoardWidth-screenY) };
		command.setVectorArray(vector2Array);
		ChessGame3D.getClient().send(command);
		*/
		return false;

	}
}