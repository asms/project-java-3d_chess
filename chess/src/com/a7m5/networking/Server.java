package com.a7m5.networking;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;

import com.a7m5.chess.ChessBoard;
import com.a7m5.chess.chesspieces.Bishop;
import com.a7m5.chess.chesspieces.ChessPiece;
import com.a7m5.chess.chesspieces.King;
import com.a7m5.chess.chesspieces.Knight;
import com.a7m5.chess.chesspieces.Pawn;
import com.a7m5.chess.chesspieces.Queen;
import com.a7m5.chess.chesspieces.Rook;

public class Server implements Runnable {
	
	private int port;
	private static int MAX_CONNECTIONS = 13; // arbitrary # of conn.
	private static int connections = 0;
	private boolean running = true;
	private boolean closing = true;
	private ServerSocket serverSocket = null;
	private ArrayList<Thread> clientThreads = new ArrayList<Thread>();
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
					clientThreads.add(clientThread);
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
		for(Runnable runnable : clientRunnables) {
			ServerThread client = (ServerThread) runnable;
			client.send(command);
		}
	}
	
	public void setChessBoard(ChessBoard chessBoard) {
		this.chessBoard = chessBoard;
	}

	public ChessBoard getChessBoard() {
		return this.chessBoard;
	}

}
