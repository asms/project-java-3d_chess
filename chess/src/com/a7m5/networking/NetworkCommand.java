package com.a7m5.networking;

import java.io.Serializable;

import com.a7m5.chess.Vector2;
import com.a7m5.chess.chesspieces.ChessPiece;

public class NetworkCommand implements Serializable {
	
	private static final long serialVersionUID = -6248678209942083091L;
	
	public static int DEBUG = 0;
	public static int MOVE = 1;
	
	public int command;
	private Vector2[] vector;
	
	public NetworkCommand(int command) {
		this.command = command;
	}

	public void move(Vector2 oldPosition, Vector2 newPosition) {
		this.vector = new Vector2[2];
		this.vector[0] = oldPosition;
		this.vector[1] = newPosition;
	}

	public int getCommand() {
		return command;
	}

	public Vector2[] getVectorArray() {
		return vector;
	}

}
