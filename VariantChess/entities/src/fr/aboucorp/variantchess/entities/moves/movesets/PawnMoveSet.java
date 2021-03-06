package fr.aboucorp.variantchess.entities.moves.movesets;

import fr.aboucorp.variantchess.entities.enums.BoardEventType;
import fr.aboucorp.variantchess.entities.ChessColor;
import fr.aboucorp.variantchess.entities.Location;
import fr.aboucorp.variantchess.entities.Piece;
import fr.aboucorp.variantchess.entities.Square;
import fr.aboucorp.variantchess.entities.boards.ClassicBoard;
import fr.aboucorp.variantchess.entities.enums.PieceId;
import fr.aboucorp.variantchess.entities.events.models.EnPassantEvent;
import fr.aboucorp.variantchess.entities.moves.AbstractMoveSet;
import fr.aboucorp.variantchess.entities.utils.SquareList;

public class PawnMoveSet extends AbstractMoveSet {

    public PawnMoveSet(Piece thisPiece, ClassicBoard classicBoard) {
        super(thisPiece, classicBoard);
    }

    @Override
    protected SquareList getPossibleMoves(Piece piece, ChessColor turnColor) {
        SquareList validSquares = getClassicMoves(piece);
        validSquares.addAll(getEatingMoves(piece,  turnColor, false));
        return validSquares;
    }

    private SquareList getClassicMoves(Piece piece) {
        SquareList classicMoves = new SquareList();
        Location start = piece.getLocation();
        Square simpleMove;
        float zpos = piece.getChessColor() == ChessColor.WHITE ? start.getZ() + 1 : start.getZ() - 1;
        simpleMove = (Square) classicBoard.getSquares().getItemByLocation(new Location(start.getX(), 0, zpos));
        if (simpleMove != null && simpleMove.getPiece() == null) {
            classicMoves.add(simpleMove);
            Square doubleMove;
            if (piece.isFirstMove()) {
                zpos = piece.getChessColor() == ChessColor.WHITE ? start.getZ() + 2 : start.getZ() - 2;
                doubleMove = (Square) classicBoard.getSquares().getItemByLocation(new Location(start.getX(), 0, zpos));
                if (doubleMove != null && doubleMove.getPiece() == null) {
                    classicMoves.add(doubleMove);
                }
            }
        }
        return classicMoves;
    }


    private SquareList getEatingMoves(Piece piece, ChessColor turnColor, boolean isThreat) {
        Location start = piece.getLocation();
        SquareList validSquares = new SquareList();
        Square diagRight;
        Square diagLeft;
        int zpos = piece.getChessColor() == ChessColor.WHITE ? 1 : -1;
        diagRight = (Square) classicBoard.getSquares().getItemByLocation(new Location(start.getX() - 1, 0, start.getZ() + zpos));
        diagLeft = (Square) classicBoard.getSquares().getItemByLocation(new Location(start.getX() + 1, 0, start.getZ() + zpos));

        if (diagRight != null
                && (isThreat
                || (diagRight.getPiece() != null && diagRight.getPiece().getChessColor() != turnColor))) {
            validSquares.add(diagRight);
        } else if (diagRight != null && diagRight.getPiece() == null && enPassantRightIsPossible(diagRight)){
            validSquares.add(diagRight);
        }
        if (diagLeft != null
                && (isThreat
                || (diagLeft.getPiece() != null && diagLeft.getPiece().getChessColor() != turnColor))) {
            validSquares.add(diagLeft);
        }else if(diagLeft != null && diagLeft.getPiece() == null && enPassantLeftIsPossible(diagLeft)){
            validSquares.add(diagLeft);
        }
        return validSquares;
    }

    private boolean enPassantRightIsPossible(Square diagRight) {
        if (enPassantIsPossible() && diagRight.getLocation().getX() == this.previousTurn.to.getLocation().getX() ) {
            this.eventManager.sendMessage(new EnPassantEvent("En passant", BoardEventType.EN_PASSANT,diagRight));
            return true;
        }
        return false;
    }

    private boolean enPassantLeftIsPossible(Square diagLeft) {
        if (enPassantIsPossible() && diagLeft.getLocation().getX() == this.previousTurn.to.getLocation().getX()) {
            this.eventManager.sendMessage(new EnPassantEvent("En passant", fr.aboucorp.variantchess.entities.enums.BoardEventType.EN_PASSANT, diagLeft));
            return true;
        }
        return false;
    }

    private boolean enPassantIsPossible() {
        return ((this.piece.getChessColor() == ChessColor.WHITE && this.piece.getLocation().getZ() == 4)
                || (this.piece.getChessColor() == ChessColor.BLACK && this.piece.getLocation().getZ() == 3))
                && PieceId.isPawn(this.previousTurn.played.getPieceId())
                && Math.abs(this.previousTurn.to.getLocation().getZ() - this.previousTurn.from.getLocation().getZ()) == 2
                && Math.abs(this.previousTurn.to.getLocation().getX() - this.piece.getLocation().getX()) == 1;
    }

    @Override
    public SquareList getThreats(Piece piece, ChessColor turnColor) {
        return getEatingMoves(piece, turnColor, true);
    }

}
