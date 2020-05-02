package fr.aboucorp.teamchess.entities.model.moves.movesets;

import fr.aboucorp.teamchess.entities.model.ChessColor;
import fr.aboucorp.teamchess.entities.model.Location;
import fr.aboucorp.teamchess.entities.model.Piece;
import fr.aboucorp.teamchess.entities.model.Square;
import fr.aboucorp.teamchess.entities.model.boards.ClassicBoard;
import fr.aboucorp.teamchess.entities.model.moves.AbstractMoveSet;
import fr.aboucorp.teamchess.entities.model.utils.SquareList;

public class RookMoveSet extends AbstractMoveSet {

    public RookMoveSet(Piece thisPiece, ClassicBoard classicBoard) {
        super(thisPiece, classicBoard);
    }

    @Override
    protected SquareList getPossibleMoves(Piece piece,ChessColor turnColor) {
        SquareList validSquares = new SquareList();
        Location start = piece.getLocation();
        for(float x = start.getX()+1; x < 8 ; x++){
            Square validSquare = (Square) classicBoard.getSquares().getItemByLocation(new Location(x,0,start.getZ()));
            if(validSquare != null && validSquare.getPiece() == null){
                validSquares.add(validSquare);
            }else if(validSquare.getPiece().getChessColor() != turnColor){
                validSquares.add(validSquare);
                break;
            }else{
                break;
            }
        }

        for(float x = start.getX()-1 ; x >= 0 ; x--){
            Square validSquare = (Square) classicBoard.getSquares().getItemByLocation(new Location(x,0,start.getZ()));
            if(validSquare != null && validSquare.getPiece() == null){
                validSquares.add(validSquare);
            }else if(validSquare.getPiece().getChessColor() != turnColor){
                validSquares.add(validSquare);
                break;
            }else{
                break;
            }
        }

        for(float z = start.getZ()-1 ;  z >= 0; z-- ){
            Square validSquare = (Square) classicBoard.getSquares().getItemByLocation(new Location(start.getX(),0,z));
            if(validSquare != null && validSquare.getPiece() == null){
                validSquares.add(validSquare);
            }else if(validSquare.getPiece().getChessColor() != turnColor){
                validSquares.add(validSquare);
                break;
            }else{
                break;
            }
        }

        for(float z = start.getZ()+1 ; z < 8; z++ ){
            Square validSquare = (Square) classicBoard.getSquares().getItemByLocation(new Location(start.getX(),0,z));
            if(validSquare != null && validSquare.getPiece() == null){
                validSquares.add(validSquare);
            }else if(validSquare.getPiece().getChessColor() != turnColor){
                validSquares.add(validSquare);
                break;
            }else{
                break;
            }
        }
        return validSquares;
    }

    @Override
    public SquareList getThreats(Piece piece,ChessColor turnColor) {
        return getPossibleMoves(piece,turnColor);
    }
}