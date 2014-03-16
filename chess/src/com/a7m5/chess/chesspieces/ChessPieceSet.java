package com.a7m5.chess.chesspieces;

public class ChessPieceSet {
	private ChessPiece[] set;

	public ChessPieceSet(ChessPiece[] set) {
		this.set = set;
	}
	
	public ChessPiece getPieceByIndex(int i){
		return set[i];
	}
	
	public ChessPiece getPieceByName(String name){
		ChessPiece temp = new ChessPiece(null);
		
		for(int i = 0; i < set.length; i++){
			if(set[i].getPieceName().compareTo(name) == 0){
				return set[i];
			}
		}
		return temp;
	}

	public int getLength(){
		return set.length;
	}

	public ChessPiece[] getPieces() {
		return set;
	}
}
