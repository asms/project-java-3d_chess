package com.a7m5.chess;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
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
import com.a7m5.chess.chesspieces.ChessTile;

public class ResourceGrabber {

	String resourceDirectoryPath;
	File[] resourceFileList;
	ArrayList<ChessPiece> grabbedPieces = new ArrayList<ChessPiece>();
	ArrayList<ChessTile> grabbedTiles = new ArrayList<ChessTile>();
	ChessBoard grabbedChessBoard;

	public ResourceGrabber(String resourceDirectoryPath) {
		this.resourceDirectoryPath = resourceDirectoryPath;
		File directoryIn = new File(resourceDirectoryPath);
		resourceFileList = directoryIn.listFiles();
		for(int i = 0; i < resourceFileList.length; i++){
			try {

				if(resourceFileList[i].getName().endsWith(".piece.xml")){
					DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
					DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
					Document pieceDoc = dBuilder.parse(resourceFileList[i]);
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

				} else if(resourceFileList[i].getName().endsWith(".tile.xml")){
					// TODO: Import tiles from files.
				} else if(resourceFileList[i].getName().endsWith(".board.xml")){
					// TODO: Import boards from files.
				}
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


}
