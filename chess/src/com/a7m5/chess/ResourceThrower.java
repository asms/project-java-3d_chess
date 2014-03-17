package com.a7m5.chess;

import java.io.File;
import java.sql.Time;
import java.util.Date;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.a7m5.chess.chesspieces.ChessPiece;
import com.badlogic.gdx.graphics.Color;

public class ResourceThrower {
	String resourceDirectoryPath;


	public void saveBoard(ChessBoard board) {
		String filePath = board.getAbsoluteFilePath();
		if(filePath == null) {
			filePath = ChessGame3D.getCacheDirectory() + File.pathSeparator + "boards" + File.pathSeparator + String.valueOf(new Date().getTime()) + ".xml";
		}
		createBoardFile(board, filePath);
	}
	
	public void savePiece(ChessPiece piece) {
		String filePath = piece.getAbsolutePath();
		if(filePath == null) {
			filePath = ChessGame3D.getCacheDirectory() + File.pathSeparator + "pieces" + File.pathSeparator + String.valueOf(new Date().getTime()) + ".xml";
		}
		createPieceFile(piece, filePath);
	}

	public void createPieceFile(ChessPiece outgoingPiece, String filePath){
		File tempFile = new File(filePath);

		try {

			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

			// root element, sets ID
			Document doc = docBuilder.newDocument();
			Element rootElement = doc.createElement("chessPiece");
			rootElement.setAttribute("uniquePieceID", Integer.toString(outgoingPiece.getUniquePieceID()));
			doc.appendChild(rootElement);

			// Add all the standalone tags.
			addStandaloneStr("pieceName", outgoingPiece.getPieceName(), doc, rootElement);
			addStandaloneStr("blackArtFile", outgoingPiece.getBlackArtFile(), doc, rootElement);
			addStandaloneStr("whiteArtFile", outgoingPiece.getWhiteArtFile(), doc, rootElement);
			addStandaloneStr("NPCArtFile", outgoingPiece.getNPCArtFile(), doc, rootElement);

			// Add all the vectors.
			addVectors("movementVectors", outgoingPiece.getMovementVectors(), doc, rootElement);
			addVectors("movementDirectionVectors", outgoingPiece.getMovementDirectionVectors(), doc, rootElement);
			addVectors("specialMovementVectors", outgoingPiece.getSpecialMovementVectors(), doc, rootElement);
			addVectors("attackVectors", outgoingPiece.getAttackVectors(), doc, rootElement);
			addVectors("attackDirectionVectors", outgoingPiece.getAttackDirectionVectors(), doc, rootElement);

			// write the content into xml file
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(tempFile);
			transformer.transform(source, result);

			System.out.println("File saved: " + tempFile.getAbsolutePath());

		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		} catch (TransformerException tfe) {
			tfe.printStackTrace();
		}
	}

	private void addStandaloneStr(String tagName, String tagString, Document docIn, Element root){
		// Add the tag/string
		Element temp = docIn.createElement(tagName);
		temp.appendChild(docIn.createTextNode(tagString));
		root.appendChild(temp);
	}

	private void addVectors(String vectorName, Vector2[] tempVects, Document docIn, Element root){
		// Add the tag/string
		if(tempVects != null){
			Element vectorGroup = docIn.createElement(vectorName);
			for(int i = 0; i < tempVects.length; i++){
				Element temp = docIn.createElement("vector");
				temp.setAttribute("x", Integer.toString((int) tempVects[i].getX()));
				temp.setAttribute("y", Integer.toString((int) tempVects[i].getY()));
				vectorGroup.appendChild(temp);
			}
			root.appendChild(vectorGroup);
		}
	}

	private void addTiles(Document doc, Tile[][] tiles, Element root){
		if(tiles != null){
			Element tileGroup = doc.createElement("tiles");
			for(int x = 0; x < tiles.length; x++){
				Tile[] tileRow = tiles[x];
				for(int y = 0; y < tileRow.length; y++) {
					Tile tile = tileRow[y];
					if(tile != null) {
						Element el = doc.createElement("tile");

						el.setAttribute("x", String.valueOf(x));
						el.setAttribute("y", String.valueOf(y));

						Color color = tile.getColor();
						el.setAttribute("r", String.valueOf(color.r));
						el.setAttribute("g", String.valueOf(color.g));
						el.setAttribute("b", String.valueOf(color.b));
						el.setAttribute("a", String.valueOf(color.a));

						tileGroup.appendChild(el);
					}
				}
			}
			root.appendChild(tileGroup);
		}
	}

	public void createBoardFile(ChessBoard outgoingBoard, String filePath){

		File tempFile = new File(filePath);

		try {

			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

			// root element, sets ID
			Document doc = docBuilder.newDocument();
			Element rootElement = doc.createElement("chessBoard");
			rootElement.setAttribute("width", Integer.toString(outgoingBoard.getBoardWidth()));
			rootElement.setAttribute("name", outgoingBoard.getName());
			doc.appendChild(rootElement);

			// Piece element
			Element pieceElement = doc.createElement("pieces");
			rootElement.appendChild(pieceElement);

			// Add all the pieces on the board.
			for(int x =0; x < outgoingBoard.getBoardWidth(); x++){
				for(int y =0; y < outgoingBoard.getBoardWidth(); y++){
					
					addPiece(outgoingBoard.getChessPieceByXYTile(x, y),  doc, pieceElement);
				}
			}
			addTiles(doc, outgoingBoard.getTileArray(), pieceElement);

			// write the content into xml file
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(tempFile);
			transformer.transform(source, result);

			System.out.println("File saved: " + tempFile.getAbsolutePath());

		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		} catch (TransformerException tfe) {
			tfe.printStackTrace();
		}
	}


	private void addPiece(ChessPiece pieceIn, Document docIn, Element root){
		// Add the tag/string
		if(pieceIn != null){
			Element temp = docIn.createElement("piece");
			temp.setAttribute("x", Integer.toString(pieceIn.getX()));
			temp.setAttribute("y", Integer.toString(pieceIn.getY()));
			temp.setAttribute("name", pieceIn.getPieceName());
			temp.setAttribute("owner", pieceIn.getOwner().toString());

			root.appendChild(temp);
		}
	}

}
