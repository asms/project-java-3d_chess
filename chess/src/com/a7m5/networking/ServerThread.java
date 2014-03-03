package com.a7m5.networking;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;

import com.a7m5.chess.Vector2;

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
						switch(command.getCommand()) {
						case 0: //DEBUG

							break;
						case 1: //MOVE
							doMove(command);
							syncClient();
							break;

						case 3: //ATTACK
							doAttack(command);
							syncClient();
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

	private void doAttack(NetworkCommand command) {
		Vector2[] vectors = command.getVectorArray();
		server.getChessBoard().attackChessPiece(vectors[0], vectors[1]);
		server.sendAll(command);
	}

	private void doMove(NetworkCommand command) {
		Vector2[] vectors = command.getVectorArray();
		server.getChessBoard().moveChessPiece(vectors[0], vectors[1]);
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
