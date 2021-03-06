package fr.aboucorp.variantchess.entities.utils;

import fr.aboucorp.variantchess.entities.Piece;
import fr.aboucorp.variantchess.entities.enums.PieceId;

public class PieceList extends ChessList<Piece> {
    public Piece getPieceById(PieceId id){
        for(Piece piece : this){
            if(piece.getPieceId() == id){
                return piece;
            }
        }
        return null;
    }

}
