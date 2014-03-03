package com.a7m5.networking;

import java.io.Serializable;

import com.a7m5.chess.ChessBoard;
import com.a7m5.chess.Vector2;
import com.a7m5.chess.chesspieces.ChessPiece;

public class NetworkCommand implements Serializable {
	
	private static final long serialVersionUID = -6248678209942083091L;

	
	
	public static final int DEBUG = 0;
	public static final int MOVE = 1;
	public static final int SYNC = 2;
	public static final int ATTACK = 3;
	
	public int command;
	private Vector2[] vectors;

	private ChessBoard chessBoard;
	
	public void setCommand(int command) {
		this.command = command;
	}

	public int getCommand() {
		return command;
	}
	
	public void setVectorArray(Vector2[] vectorArray) {
		this.vectors = vectorArray;
	}

	public Vector2[] getVectorArray() {
		return vectors;
	}

	public ChessBoard getChessBoard() {
		return chessBoard;
	}

	public void setChessBoard(ChessBoard chessBoard) {
		this.chessBoard = chessBoard;
	}

}
