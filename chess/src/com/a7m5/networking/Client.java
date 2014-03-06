package com.a7m5.networking;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import com.a7m5.chess.ChessBoard;
import com.a7m5.chess.GdxChessGame;
import com.a7m5.chess.Vector2;
import com.a7m5.chess.chesspieces.ChessPiece;

public class Client implements Runnable {

	private String address;
	private int port;

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
						case 2: //Sync
							board = command.getChessBoard();
							if(board.getCheckedPlayer() != null) {
								System.out.println("Someone is checked.");
							}
							break;
						case 3: //ATTACK
							Vector2[] positions = command.getVectorArray();
							board.attackChessPiece(positions[0], positions[1]);
							break;
						case 4: //GAME OVER
							int winner = (int) command.getVectorArray()[0].getX();
							board.gameOver(winner);
						case 5: //MOUSE_MOVED
							board.setCursor(command.getOwner(), command.getVectorArray()[0]);
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
		if(GdxChessGame.getOwner() == board.getTurnOwner())
		try {
			ChessPiece clickedChessPiece = board.getChessPieceByXY(x, y);
			if(clickedChessPiece != null) {
				clickedChessPiece.onClick();
			} else {
				ChessPiece selectedChessPiece = board.getSelectedChessPiece();
				if(selectedChessPiece != null) {
					selectedChessPiece.onNullTileClicked(x, y);
				}
			}
			return;
		} catch(IndexOutOfBoundsException e) {}
		
		//side menu interaction
	}
	
	public void send(NetworkCommand command) {
		command.setOwner(GdxChessGame.getOwner());
		if(oos != null) {
			try {
				oos.writeObject(command);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void sendMove(Vector2 oldPosition, Vector2 newPosition) {
		NetworkCommand command = new NetworkCommand();
		command.setCommand(NetworkCommand.MOVE);
		command.setVectorArray(new Vector2[] { oldPosition, newPosition});
		
		send(command);
	}

	public void sendAttack(Vector2 piece1, Vector2 piece2) {
		NetworkCommand command = new NetworkCommand();
		command.setCommand(NetworkCommand.ATTACK);
		command.setVectorArray(new Vector2[] { piece1, piece2});
		
		send(command);
	}

	public void sendGameOver() {
		NetworkCommand command = new NetworkCommand();
		command.setCommand(NetworkCommand.GAME_OVER);
		Vector2[] vectorArray = {new Vector2(GdxChessGame.getOwner().ordinal(), 0)};
		command.setVectorArray(vectorArray);
		
		send(command);
	}
}