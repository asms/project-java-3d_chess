package com.a7m5.chess;

import java.io.File;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import com.a7m5.chess.chesspieces.ChessOwner;
import com.a7m5.chess.chesspieces.ChessPiece;
import com.a7m5.chess.chesspieces.ChessPieceSet;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;

public class ResourceGrabber {
	String cacheDir;
	ChessPieceSet set = null;
	ArrayList<ChessBoard> boards = null;

	public ResourceGrabber(String file) {
		cacheDir = file;
		set = grabChessPieceSet();
		boards = grabBoards();
		new Object();
		
		//grabInternalPieces();	// Need to get the pieces to populate boards.
		//grabPieces("C://Users/Peter/Desktop/assets/data/");	// A test of an external grab.
		//getGrabbedChessPieceSet(); // Used by grabBoard.
		//grabInternalBoard();	// Gets the standard board.
		//grabBoard("C://Users/Peter/Desktop/assets/data/standardChess.board.xml");
	}
	
	private ChessPieceSet grabChessPieceSet() {
		ChessPieceSet set = null;
		File piecesDir = new File(cacheDir + "pieces/");
		File[] pieceFiles = piecesDir.listFiles();
		ChessPiece[] pieces = new ChessPiece[pieceFiles.length];
		for(int i = 0; i < pieceFiles.length; i++) {
			File file = pieceFiles[i];
			ChessPiece piece = grabPiece(file);
			if(piece != null) {
				pieces[i] = piece;
			}
		}
		set = new ChessPieceSet(pieces);
		return set;
	}

	private ChessPiece grabPiece(File file) {
		ChessPiece piece = null;
		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(file);
			Element root = doc.getDocumentElement();
			root.normalize();
			
			int id = Integer.parseInt(root.getAttribute("uniquePieceID"));
			String name = root.getElementsByTagName("pieceName").item(0).getTextContent().toString();
			String blackArtFile = root.getElementsByTagName("blackArtFile").item(0).getTextContent().toString();
			String whiteArtFile = root.getElementsByTagName("whiteArtFile").item(0).getTextContent().toString();
			String NPCArtFile = root.getElementsByTagName("NPCArtFile").item(0).getTextContent().toString();
			
			// Get all the vectors.
			Vector2[] attackDirectionVectors = grabVectors(doc, "attackDirectionVectors");
			Vector2[] movementDirectionVectors = grabVectors(doc, "movementDirectionVectors");
			Vector2[] specialMovementVectors = grabVectors(doc, "specialMovementVectors");
			Vector2[] movementVectors = grabVectors(doc, "movementVectors");
			Vector2[] attackVectors = grabVectors(doc, "attackVectors");

			
			piece = new ChessPiece(
					name,
					id,
					attackDirectionVectors,
					movementDirectionVectors,
					specialMovementVectors,
					movementVectors,
					attackVectors,
					blackArtFile,
					whiteArtFile,
					NPCArtFile
					);
			
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		return piece;
	}

	public ArrayList<ChessBoard> grabBoards() {
		ArrayList<ChessBoard> boards = new ArrayList<ChessBoard>();
		File boardsDir = new File(cacheDir + "boards/");
		File[] boardFiles = boardsDir.listFiles();
		for(int i = 0; i < boardFiles.length; i++) {
			ChessBoard board = grabBoard(boardFiles[i]);
			if(board != null) {
				boards.add(board);
			}
		}
		return boards;
	}

	public ChessBoard grabBoard(File file){
		ChessBoard board = null;
		try {

			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(file);
			Element root = doc.getDocumentElement();
			root.normalize();
			
			String name = root.getAttribute("name");
			int width = Integer.parseInt(root.getAttribute("width"));
			
			board = new ChessBoard();
			board.setName(name);
			board.setBoardWidth(width);

			ChessPiece[][] pieces = grabBoardPieces(doc, width);
			Tile[][] tiles = grabTiles(doc, width);
			
			board.setTileArray(tiles);
			board.setChessPieces(pieces);
		} catch(NumberFormatException e){
			System.out.println("Error parsing attribute `width` from xml file");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return board;	

	}

	private Vector2[] grabVectors(Document inDoc, String vectorType){
		NodeList tempNodeList = inDoc.getElementsByTagName("vector");	// Get all the vectors in the file.
		ArrayList<Vector2> ourVectors = new ArrayList<Vector2>();
		for(int i = 0; i < tempNodeList.getLength(); i++){
			if(tempNodeList.item(i).getParentNode().getNodeName().toString().compareTo(vectorType) == 0){
				int xTemp = 0, yTemp = 0;
				try{
					xTemp = Integer.parseInt(tempNodeList.item(i).getAttributes().getNamedItem("x").getNodeValue());
					yTemp = Integer.parseInt(tempNodeList.item(i).getAttributes().getNamedItem("y").getNodeValue());
				} catch(NumberFormatException e){
					System.out.println("Number Format Error in XML Read!!!");
				}
				ourVectors.add(new Vector2(xTemp, yTemp));
			}
		}
		Vector2[] tempVectorArray = new Vector2[ourVectors.size()];
		for( int i = 0; i < ourVectors.size(); i++){
			tempVectorArray[i] = ourVectors.get(i);
		}
		return tempVectorArray;
	}


	private ChessPiece[][] grabBoardPieces(Document doc, int boardWidth){
		ChessPiece[][] pieces = new ChessPiece[boardWidth][boardWidth];
		NodeList piecesNodeList = doc.getElementsByTagName("piece");
		for(int i = 0; i < piecesNodeList.getLength(); i++){
			Node pieceNode = piecesNodeList.item(i);
			NamedNodeMap attributes = pieceNode.getAttributes();

			try{
				int x = Integer.parseInt(attributes.getNamedItem("x").getNodeValue());
				int y = Integer.parseInt(attributes.getNamedItem("y").getNodeValue());
				String name = attributes.getNamedItem("name").getNodeValue();
				String ownerAttribute = attributes.getNamedItem("owner").getNodeValue();
				ChessOwner owner;
				if(ownerAttribute.compareTo("BLACK") == 0){
					owner = ChessOwner.BLACK;
				} else if(ownerAttribute.compareTo("WHITE") == 0){
					owner = ChessOwner.WHITE;
				} else {
					owner = ChessOwner.NC;
				}
				ChessPiece piece = set.getPieceByName(name).getClone(owner);
				pieces[x][y] = piece;
			} catch(NumberFormatException e){
				System.out.println("grabBoardPieces: NumberFormatException");
			}
		}
		return pieces;
	}
	
	private Tile[][] grabTiles(Document doc, int width) {
		Tile[][] tiles = new Tile[width][width];
		NodeList nodeListTiles = doc.getElementsByTagName("tile");
		for(int i = 0; i < nodeListTiles.getLength(); i++) {
			try {
				NamedNodeMap attributes = nodeListTiles.item(i).getAttributes();
				Tile tile = new Tile();
				
				int x = Integer.parseInt(attributes.getNamedItem("x").getNodeValue());
				int y = Integer.parseInt(attributes.getNamedItem("y").getNodeValue());
				tile.setPosition(new Vector2(x, y));
				
				float r = Float.parseFloat(attributes.getNamedItem("r").getNodeValue());
				float g = Float.parseFloat(attributes.getNamedItem("g").getNodeValue());
				float b = Float.parseFloat(attributes.getNamedItem("b").getNodeValue());
				float a = Float.parseFloat(attributes.getNamedItem("a").getNodeValue());
				tile.setColor(new Color(r, g, b, a));
				tiles[x][y] = tile;
			} catch(NumberFormatException e) {
				Gdx.app.log("grabTiles", "Error parsing number from tile attributes.");
			}
		}
		return tiles;
	}

	public ChessPieceSet getChessPieceSet() {
		return set;
	}

	public ArrayList<ChessBoard> getBoards() {
		return boards;
	}
}
