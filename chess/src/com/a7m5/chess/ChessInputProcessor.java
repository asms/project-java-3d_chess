package com.a7m5.chess;

import com.a7m5.chess.chesspieces.ChessOwner;
import com.a7m5.networking.NetworkCommand;
import com.badlogic.gdx.InputProcessor;

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
		   GdxChessGame.setOwner(ChessOwner.WHITE);
	   } else if(character == 'b') {
		   GdxChessGame.setOwner(ChessOwner.BLACK);
	   }
      return false;
   }

   @Override
   public boolean touchDown (int x, int y, int pointer, int button) {
	   GdxChessGame.onClickListener(x, y, pointer, button);
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
		NetworkCommand command = new NetworkCommand();
		command.setCommand(NetworkCommand.MOUSE_MOVE);
		Vector2[] vector2Array = { new Vector2(screenX, ChessBoard.actualBoardWidth-screenY) };
		command.setVectorArray(vector2Array);
		GdxChessGame.getClient().send(command);
		return false;
	}
}