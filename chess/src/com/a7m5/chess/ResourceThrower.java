package com.a7m5.chess;

import java.io.File;
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

public class ResourceThrower {
	String resourceDirectoryPath;

	public ResourceThrower(String resourceDirectoryPath) {
		this.resourceDirectoryPath = resourceDirectoryPath;
	}

	public void createPieceFile(ChessPiece outgoingPiece){
		File tempFile = new File(resourceDirectoryPath + "\\" + outgoingPiece.getPieceName() + "_ID" + outgoingPiece.getUniquePieceID() + ".piece.xml");

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



}
