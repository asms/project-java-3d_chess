package com.a7m5.chess;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import com.a7m5.chess.chesspieces.ChessOwner;
import com.a7m5.chess.chesspieces.ChessPiece;
import com.a7m5.chess.chesspieces.ChessPieceSet;
import com.a7m5.chess.chesspieces.ChessTile;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

public class ResourceGrabber {

	File[] resourceFileList;
	ArrayList<ChessPiece> grabbedPieces = new ArrayList<ChessPiece>();
	ChessPieceSet grabbedChessPieceSet;
	ArrayList<ChessTile> grabbedTiles = new ArrayList<ChessTile>();
	ChessBoard grabbedChessBoard;
	
	String[] fileNameList = {
			"Bishop_ID10.piece.xml",
			"King_ID20.piece.xml",
			"Knight_ID30.piece.xml",
			"Pawn_ID40.piece.xml",
			"Queen_ID50.piece.xml",
			"Rook_ID60.piece.xml",
			"test.piece.xml"
	};

	public ResourceGrabber() {
		grabPieces();	// Need to get the pieces to populate boards.
		getGrabbedChessPieceSet(); // Used by grabBoard.
		grabBoard();	// Gets the board.
	}
	
	public void grabPieces(){
		for(int i = 0; i < fileNameList.length; i++){
			try {
				InputStream fileStream = ResourceGrabber.class.getResourceAsStream("/data/" + fileNameList[i]);

				DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
				Document pieceDoc = dBuilder.parse(fileStream);
				pieceDoc.getDocumentElement().normalize();

				// Get the piece ID
				int tempID = -1;
				String tempIDString = pieceDoc.getDocumentElement().getAttribute("uniquePieceID").toString();
				if(tempIDString != null){
					try{
						tempID = Integer.parseInt(tempIDString);
					} catch(NumberFormatException e){
						System.out.println("Number Format in XML Read!!!");
					}
				}
				//System.out.println("pieceID: " + tempID);

				// Get the pieceName
				String tempName = grabFirstString(pieceDoc, "pieceName");
				//System.out.println("pieceName: " + tempName);

				// Get the blackArtFile
				String tempBlackArtFile = grabFirstString(pieceDoc, "blackArtFile");
				//System.out.println("blackArtFile: " + tempBlackArtFile);

				// Get the whiteArtFile
				String tempWhiteArtFile = grabFirstString(pieceDoc, "whiteArtFile");
				//System.out.println("whiteArtFile: " + tempWhiteArtFile);

				// Get the NPCArtFile
				String tempNPCArtFile = grabFirstString(pieceDoc, "NPCArtFile");
				//System.out.println("NPCArtFile: " + tempNPCArtFile);

				// Get all the vectors.
				Vector2[] tempAttackDirectionVectors = grabVectors(pieceDoc, "attackDirectionVectors");
				Vector2[] tempMovementDirectionVectors = grabVectors(pieceDoc, "movementDirectionVectors");
				Vector2[] tempSpecialMovementVectors = grabVectors(pieceDoc, "specialMovementVectors");
				Vector2[] tempMovementVectors = grabVectors(pieceDoc, "movementVectors");
				Vector2[] tempAttackVectors = grabVectors(pieceDoc, "attackVectors");

				// Create the piece and add it to the array of pieces.
				ChessPiece tempChessPiece = new ChessPiece(tempName, tempID, tempAttackDirectionVectors,
						tempMovementDirectionVectors, tempSpecialMovementVectors, tempMovementVectors,
						tempAttackVectors, tempBlackArtFile, tempWhiteArtFile, tempNPCArtFile);
				tempChessPiece.setOwner(ChessOwner.WHITE);	// TODO: correct settings for the chess piece owner.
				grabbedPieces.add(tempChessPiece);

				// TODO: Import tiles from files.
				// TODO: Import boards from files.
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ParserConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SAXException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
		}
	}
// TODO make it take a filepath
	public ChessBoard grabBoard(){
			try {
				InputStream fileStream = ResourceGrabber.class.getResourceAsStream("/data/standardChess.board.xml");

				DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
				Document boardDoc = dBuilder.parse(fileStream);
				boardDoc.getDocumentElement().normalize();

				// Get the piece ID
				int tempWidth = -1;
				String tempWidthStr = boardDoc.getDocumentElement().getAttribute("width").toString();
				if(tempWidthStr != null){
					try{
						tempWidth = Integer.parseInt(tempWidthStr);
					} catch(NumberFormatException e){
						System.out.println("Number Format in XML Read!!!");
					}
				}
				System.out.println("Temp Width: " + tempWidth);
				
				grabBoardPieces(boardDoc, tempWidth);
				
				
/*
				// Get the pieceName
				String tempName = grabFirstString(pieceDoc, "pieceName");
				//System.out.println("pieceName: " + tempName);

				// Get the blackArtFile
				String tempBlackArtFile = grabFirstString(pieceDoc, "blackArtFile");
				//System.out.println("blackArtFile: " + tempBlackArtFile);

				// Get the whiteArtFile
				String tempWhiteArtFile = grabFirstString(pieceDoc, "whiteArtFile");
				//System.out.println("whiteArtFile: " + tempWhiteArtFile);

				// Get the NPCArtFile
				String tempNPCArtFile = grabFirstString(pieceDoc, "NPCArtFile");
				//System.out.println("NPCArtFile: " + tempNPCArtFile);

				// Get all the vectors.
				Vector2[] tempAttackDirectionVectors = grabVectors(pieceDoc, "attackDirectionVectors");
				Vector2[] tempMovementDirectionVectors = grabVectors(pieceDoc, "movementDirectionVectors");
				Vector2[] tempSpecialMovementVectors = grabVectors(pieceDoc, "specialMovementVectors");
				Vector2[] tempMovementVectors = grabVectors(pieceDoc, "movementVectors");
				Vector2[] tempAttackVectors = grabVectors(pieceDoc, "attackVectors");

				// Create the piece and add it to the array of pieces.
				ChessPiece tempChessPiece = new ChessPiece(tempName, tempID, tempAttackDirectionVectors,
						tempMovementDirectionVectors, tempSpecialMovementVectors, tempMovementVectors,
						tempAttackVectors, tempBlackArtFile, tempWhiteArtFile, tempNPCArtFile);
				tempChessPiece.setOwner(ChessOwner.WHITE);	// TODO: correct settings for the chess piece owner.
				grabbedPieces.add(tempChessPiece);
*/
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ParserConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SAXException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return grabbedChessBoard;	
		
	}

	private String grabFirstString(Document inDoc, String tagName){
		String tempString = "";
		NodeList tempList = inDoc.getElementsByTagName(tagName);
		if(tempList.getLength() >= 1){
			tempString = tempList.item(0).getTextContent().toString();	
		}
		return tempString;
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
					// TODO: NumberFormatException handling.
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

	public ChessPiece[] getGrabbedPieces(){
		ChessPiece[] temp = new ChessPiece[grabbedPieces.size()];
		for( int i = 0; i < grabbedPieces.size(); i++){
			temp[i] = grabbedPieces.get(i);
		}
		return temp;
	}

	private ChessBoard grabBoardPieces(Document inDoc, int boardWidth){
		grabbedChessBoard = new ChessBoard(grabbedChessPieceSet);
		grabbedChessBoard.setBoardWidth(boardWidth);
		NodeList tempNodeList = inDoc.getElementsByTagName("piece");	// Get all the pieces
		ArrayList<ChessPiece> ourPieces = new ArrayList<ChessPiece>();
		for(int i = 0; i < tempNodeList.getLength(); i++){
				String tempName, tempOwner;
				int xTemp = 0, yTemp = 0;
				
				try{
					xTemp = Integer.parseInt(tempNodeList.item(i).getAttributes().getNamedItem("x").getNodeValue());
					yTemp = Integer.parseInt(tempNodeList.item(i).getAttributes().getNamedItem("y").getNodeValue());
					tempName = tempNodeList.item(i).getAttributes().getNamedItem("name").getNodeValue();
					tempOwner = tempNodeList.item(i).getAttributes().getNamedItem("owner").getNodeValue();	// TODO parse owner.
					ChessPiece tempPiece = grabbedChessPieceSet.getPieceByName(tempName).getClone(ChessOwner.BLACK);
					grabbedChessBoard.addPiece(xTemp, yTemp, tempPiece);
					
					System.out.println("Name: " + grabbedChessBoard.getChessPieceByXYTile(xTemp, yTemp).getPieceName()
							+ " X: " + grabbedChessBoard.getChessPieceByXYTile(xTemp, yTemp).getX()
							+ " Y: " + grabbedChessBoard.getChessPieceByXYTile(xTemp, yTemp).getY());
					
				} catch(NumberFormatException e){
					// TODO: NumberFormatException handling.
					System.out.println("Number Format Error in XML Read!!!");
				}
				
			
		}
		return grabbedChessBoard;
	}

	public ChessPieceSet getGrabbedChessPieceSet() {
		grabbedChessPieceSet = new ChessPieceSet(getGrabbedPieces());
		return grabbedChessPieceSet;
	}

}
