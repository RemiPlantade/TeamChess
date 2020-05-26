package fr.aboucorp.variantchess.entities.moves.movesets;

import fr.aboucorp.variantchess.entities.ChessColor;
import fr.aboucorp.variantchess.entities.Location;
import fr.aboucorp.variantchess.entities.Piece;
import fr.aboucorp.variantchess.entities.Square;
import fr.aboucorp.variantchess.entities.boards.ClassicBoard;
import fr.aboucorp.variantchess.entities.events.GameEventManager;
import fr.aboucorp.variantchess.entities.moves.AbstractMoveSet;
import fr.aboucorp.variantchess.entities.utils.SquareList;

public class RookMoveSet extends AbstractMoveSet {

    public RookMoveSet(Piece thisPiece, ClassicBoard classicBoard, GameEventManager gameEventManager) {
        super(thisPiece, classicBoard, gameEventManager);
    }

    @Override
    protected SquareList getPossibleMoves(Piece piece, ChessColor turnColor) {
        SquareList validSquares = new SquareList();
        Location start = piece.getLocation();
        for (float x = start.getX() + 1; x < 8; x++) {
            Square validSquare = (Square) this.classicBoard.getSquares().getItemByLocation(new Location(x, 0, start.getZ()));
            if (validSquare != null && validSquare.getPiece() == null) {
                validSquares.add(validSquare);
            } else if (validSquare.getPiece().getChessColor() != turnColor) {
                validSquares.add(validSquare);
                break;
            } else {
                break;
            }
        }

        for (float x = start.getX() - 1; x >= 0; x--) {
            Square validSquare = (Square) this.classicBoard.getSquares().getItemByLocation(new Location(x, 0, start.getZ()));
            if (validSquare != null && validSquare.getPiece() == null) {
                validSquares.add(validSquare);
            } else if (validSquare.getPiece().getChessColor() != turnColor) {
                validSquares.add(validSquare);
                break;
            } else {
                break;
            }
        }

        for (float z = start.getZ() - 1; z >= 0; z--) {
            Square validSquare = (Square) this.classicBoard.getSquares().getItemByLocation(new Location(start.getX(), 0, z));
            if (validSquare != null && validSquare.getPiece() == null) {
                validSquares.add(validSquare);
            } else if (validSquare.getPiece().getChessColor() != turnColor) {
                validSquares.add(validSquare);
                break;
            } else {
                break;
            }
        }

        for (float z = start.getZ() + 1; z < 8; z++) {
            Square validSquare = (Square) this.classicBoard.getSquares().getItemByLocation(new Location(start.getX(), 0, z));
            if (validSquare != null && validSquare.getPiece() == null) {
                validSquares.add(validSquare);
            } else if (validSquare.getPiece().getChessColor() != turnColor) {
                validSquares.add(validSquare);
                break;
            } else {
                break;
            }
        }
        return validSquares;
    }

    @Override
    public SquareList getThreats(Piece piece, ChessColor turnColor) {
        return this.getPossibleMoves(piece, turnColor);
    }
}
