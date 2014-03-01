package com.a7m5.networking;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import com.a7m5.chess.ChessBoard;
import com.a7m5.chess.Vector2;
import com.a7m5.chess.chesspieces.ChessPiece;

public class Client implements Runnable {

	private static String address;
	private static int port;

	public Socket socket;
	public ChessBoard board;
	private boolean running;
	private OutputStream os;
	private ObjectOutputStream oos;

	public Client(String address, int port) {
		this.address = address;
		this.port = port;
	}

	@Override
	public void run() {
		board = new ChessBoard();
		board.start();
		try {
			socket = new Socket(address, port);
			running = true;
			os = socket.getOutputStream();
			oos = new ObjectOutputStream(os);
			listen();
			oos.close();
			os.close();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void listen() {
		InputStream inputStream;
		try {
			inputStream = socket.getInputStream();
			ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);  

			while(running ) {
				try {
					NetworkCommand command = (NetworkCommand) objectInputStream.readObject();
					if(command != null) {
						switch(command.getCommand()) {
						case 0: //DEBUG
							
							break;
						case 1: //Move
							Vector2[] vectors = command.getVectorArray();
							board.moveChessPiece(vectors[0], vectors[1]);
							break;
						}
					} else {
						running = false;
					}
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public void onClickListener(int x, int y, int pointer, int button) {
		System.out.println(x + ":" + y);
		ChessPiece clickedChessPiece = board.getChessPieceByXY(x, y);
		if(clickedChessPiece != null) {
			clickedChessPiece.onClick();
		} else {
			ChessPiece selectedChessPiece = board.getSelectedChessPiece();
			if(selectedChessPiece != null) {
				selectedChessPiece.onNullTileClicked(x, y);
			}
		}
	}
	
	public void send(NetworkCommand command) {
		try {
			oos.writeObject(command);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void sendMove(ChessPiece chessPiece, Vector2 newTile) {
		NetworkCommand command = new NetworkCommand(NetworkCommand.MOVE);
		command.move(chessPiece.getPosition(), newTile);
		send(command);
	}
}