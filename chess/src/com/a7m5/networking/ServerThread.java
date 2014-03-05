package com.a7m5.networking;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;

import com.a7m5.chess.ChessBoard;
import com.a7m5.chess.Vector2;
import com.a7m5.chess.chesspieces.ChessOwner;
import com.a7m5.chess.chesspieces.ChessPiece;
import com.a7m5.chess.chesspieces.King;

public class ServerThread implements Runnable {

	private boolean running = true;
	private Socket socket;
	private Server server;
	private OutputStream os;
	private ObjectOutputStream oos;

	public ServerThread(Server server, Socket socket) {
		this.server = server;
		this.socket = socket;
	}

	@Override
	public void run() {

		try {
			InputStream inputStream = socket.getInputStream();
			ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);  
			os = socket.getOutputStream();
			oos = new ObjectOutputStream(os);
			syncClient();
			while(running) {
				try {
					NetworkCommand command = (NetworkCommand) objectInputStream.readObject();
					if(command != null) {
						boolean[] check;
						ChessOwner owner = command.getOwner();
						ChessBoard board = server.getChessBoard();
						Vector2[] vectors = command.getVectorArray();
						
						switch(command.getCommand()) {
						case 0: //DEBUG

							break;
						case 1: //MOVE
							ChessOwner opponent;
							boolean canMove;
							boolean checkedOtherPlayer;
							
							board.moveChessPiece(vectors[0], vectors[1]);
							check = checkForCheck(board, owner);
							if(owner == ChessOwner.BLACK) {
								canMove = !check[0];
								checkedOtherPlayer = check[1];
								opponent = ChessOwner.WHITE;
							} else {
								canMove = !check[1];
								checkedOtherPlayer = check[0];
								opponent = ChessOwner.BLACK;
							}
							if(canMove) {
								if(checkedOtherPlayer) {
									board.setCheck(opponent);
								}
								doMove(command);
							} else {
								board.moveChessPiece(vectors[1], vectors[0]);
							}
							server.setChessBoard(board);
							//syncClient();
							break;

						case 3: //ATTACK
							doAttack(command);
							//check = checkForCheck(board, owner);
							//syncClient();
							break;
						}
						System.out.println("Command received: " + command.getCommand());
					} else {
						System.out.println("Command was null.");
						running = false;
					}
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			close();
		}
	}

	private boolean[] checkForCheck(ChessBoard board, ChessOwner owner) {
		boolean[] check = { false, false };
		King topKing = null;
		King bottomKing = null;
		for(ChessPiece[] chessPieces : board.getChessPieces()) {
			for(ChessPiece chessPiece : chessPieces) {
				if((chessPiece instanceof King)) {
					if(chessPiece.getOwner() == ChessOwner.WHITE) {
						topKing = (King) chessPiece;
					} else {
						bottomKing = (King) chessPiece;
					}
				}
			}
		}
		for(ChessPiece[] chessPieces : board.getChessPieces()) {
			for(ChessPiece chessPiece : chessPieces) {
				if(!(chessPiece instanceof King) && chessPiece != null) {
					if(chessPiece.getOwner() == ChessOwner.WHITE) {
						if(chessPiece.tryAttack(bottomKing)) {
							check[0] = true;
						}
						
					} else {
						if(chessPiece.tryAttack(topKing)) {
							check[1] = true;
						}
					}
				}
			}
		}
		return check;
	}

	private void doAttack(NetworkCommand command) {
		Vector2[] vectors = command.getVectorArray();
		server.getChessBoard().attackChessPiece(vectors[0], vectors[1]);
		server.sendAll(command);
	}

	private void doMove(NetworkCommand command) {
		server.sendAll(command);
	}

	private void syncClient() {
		NetworkCommand command = new NetworkCommand();
		command.setCommand(NetworkCommand.SYNC);
		command.setChessBoard(server.getChessBoard());
		send(command);
	}

	public void close() {
		System.out.println("Client thread is closing.");
		running = false;
		if(socket != null) {
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
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

}
