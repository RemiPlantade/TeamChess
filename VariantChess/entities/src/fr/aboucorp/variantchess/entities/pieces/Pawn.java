package fr.aboucorp.variantchess.entities.pieces;

import fr.aboucorp.variantchess.entities.ChessColor;
import fr.aboucorp.variantchess.entities.Piece;
import fr.aboucorp.variantchess.entities.Square;
import fr.aboucorp.variantchess.entities.boards.ClassicBoard;
import fr.aboucorp.variantchess.entities.enums.PieceId;
import fr.aboucorp.variantchess.entities.events.GameEventManager;
import fr.aboucorp.variantchess.entities.moves.movesets.PawnMoveSet;

public class Pawn extends Piece {

    private boolean isFirstMove = true;

    public Pawn(Square square, ChessColor chessColor, PieceId pieceId, ClassicBoard classicBoard, GameEventManager gameEventManager) {
        super(square, chessColor, pieceId);
        this.moveSet = new PawnMoveSet(this, classicBoard, gameEventManager);
    }

    @Override
    public void move(Square square) {
        super.move(square);
        this.isFirstMove = false;
    }

    @Override
    public boolean isFirstMove() {
        return this.isFirstMove;
    }

    @Override
    public char fen() {
        return this.getChessColor() == ChessColor.WHITE ? 'P' : 'p';
    }
}
