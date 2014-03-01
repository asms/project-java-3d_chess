package com.a7m5.networking;

import java.io.Serializable;

import com.a7m5.chess.Vector2;
import com.a7m5.chess.chesspieces.ChessPiece;

public class ClientCommand implements Serializable {
	
	private static final long serialVersionUID = -6248678209942083091L;
	
	public static int DEBUG = 0;
	public static int MOVE = 1;
	
	public int command;
	private Vector2[] vector;
	
	public ClientCommand(int command) {
		this.command = command;
	}

	public void move(ChessPiece chessPiece, Vector2 newTile) {
		this.vector = new Vector2[2];
		this.vector[0] = chessPiece.getPosition();
		this.vector[1] = newTile;
	}

	public int getCommand() {
		return command;
	}

	public Vector2[] getVectorArray() {
		return vector;
	}

}
