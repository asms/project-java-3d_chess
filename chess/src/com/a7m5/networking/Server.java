package com.a7m5.networking;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;

import com.a7m5.chess.ChessBoard;

public class Server implements Runnable {
	
	private int port;
	private boolean running = true;
	private ServerSocket serverSocket = null;
	private ArrayList<Runnable> clientRunnables = new ArrayList<Runnable>();
	private ChessBoard chessBoard;
	
	public Server(int port, ChessBoard chessBoard) {
		this.port = port;
		this.chessBoard = chessBoard;
	}

	@Override
	public void run() {
		try {
			serverSocket = new ServerSocket(port);
			System.out.println("Server started on port " + port);
			Socket socket;


			while(running) {

				try {
					ServerThread client;

					socket = serverSocket.accept();

					System.out.println("Client Accepted: " + socket.getInetAddress().toString());

					client = new ServerThread(this, socket);
					clientRunnables.add(client);
					
					Thread clientThread = new Thread(client);
					clientThread.start();



				} catch(SocketException e) {
					running = false;
					System.out.println("Socket was closed. No longer accepting connections.");
				} catch(IOException e) {
					System.out.println("Failed to accept client.");
				} catch(Exception e) {
					e.printStackTrace();
				}
			}

		} catch(IOException e) {
			System.out.println("Unable to bind to port.");
		}
	}
	
	public void kill() {
		System.out.println("Killing sever...");
		if(running) {
			running = false;
			closeServerSocket();
		}
		
	}
	
	private void closeServerSocket() {
		if(serverSocket != null) {
			try {
				serverSocket.close();
				serverSocket = null;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void sendAll(NetworkCommand command) {
		for(int i = 0 ; i < clientRunnables.size(); i++) {
			Runnable runnable = clientRunnables.get(i);
			if(runnable != null) {
				ServerThread client = (ServerThread) runnable;
				client.send(command);
			} else {
				clientRunnables.remove(i);
			}
			
			
		}
	}
	
	public void setChessBoard(ChessBoard chessBoard) {
		this.chessBoard = chessBoard;
	}

	public ChessBoard getChessBoard() {
		return this.chessBoard;
	}

}