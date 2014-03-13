package com.a7m5.chess.editor;

import com.a7m5.chess.ChessGame3D;
import com.a7m5.chess.ResourceThrower;
import com.a7m5.chess.chesspieces.ChessOwner;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Input.Keys;

public class EditorInputProcessor implements InputProcessor {
	@Override
	public boolean keyDown (int keycode) {
		if(keycode == Keys.TAB){
			ResourceThrower boardEditorThrow = new ResourceThrower("C:\\Users\\Peter\\git\\weird-chess\\chess\\assets\\data");
			boardEditorThrow.createBoardFile(ChessGameEditor.getEditingBoard());
		}
		return false;
	}

	@Override
	public boolean keyUp (int keycode) {
		return false;
	}

	@Override
	public boolean keyTyped (char character) {
		System.out.print("" + character);
		return false;
	}

	@Override
	public boolean touchDown (int x, int y, int pointer, int button) {
		ChessGameEditor.onClickListener(x, y, pointer, button);
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
		return false;
	}
}