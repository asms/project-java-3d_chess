package com.a7m5.networking;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;

import com.a7m5.chess.Check;
import com.a7m5.chess.ChessBoard;
import com.a7m5.chess.Vector2;
import com.a7m5.chess.chesspieces.ChessOwner;

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
							ChessOwner player = command.getOwner();
							ChessOwner opponent = command.getOwner() == ChessOwner.WHITE ? ChessOwner.BLACK : ChessOwner.WHITE;
							boolean canMove;
							boolean checkedOtherPlayer;
							Vector2 moveFrom = vectors[0];
							Vector2 moveTo = vectors[1];

							board.moveChessPiece(moveFrom, moveTo);
							Check checker = new Check();
							checker.check(board, player, opponent, moveFrom, moveTo);
							/*
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
							 */
							doMove(command);
							//syncClient();
							break;

						case 3: //ATTACK
							doAttack(command);
							//check = checkForCheck(board, owner);
							//syncClient();
							break;
						case 5: //MOUSE_MOVED
							server.sendAll(command);
							break;
						case 6: //CAMERA_MOVED
							server.sendAll(command);
							break;
						}

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
	// TODO, now with more abstract gamepeices this will need to be modified.
	/*
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
	 */
	private void doAttack(NetworkCommand command) {
		Vector2[] vectors = command.getVectorArray();
		server.getChessBoard().attackChessPiece(vectors[0], vectors[1]);
		server.sendAll(command);
	}

	private void doMove(NetworkCommand command) {
		server.sendAll(command);
	}

	private void syncClient() {
		NetworkCommand command = new NetworkCommand(NetworkCommand.SYNC);
		command.setChessBoard(server.getChessBoard());
		send(command);
	}

	public void close() {
		System.out.println("Client thread is closing.");
		running = false;
		if(socket != null) {
			if(!socket.isClosed()) {
				try {
					socket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void send(NetworkCommand command) {
		if(oos != null) {
			try {
				oos.writeObject(command);
			} catch(Exception e){
				System.out.println("Client output stream failed. Nullifying stream.");
				oos = null;
			}
		}
		
	}

}