package fr.aboucorp.variantchess.entities.pieces;

import fr.aboucorp.variantchess.entities.ChessColor;
import fr.aboucorp.variantchess.entities.Piece;
import fr.aboucorp.variantchess.entities.Square;
import fr.aboucorp.variantchess.entities.boards.ClassicBoard;
import fr.aboucorp.variantchess.entities.enums.PieceId;
import fr.aboucorp.variantchess.entities.events.GameEventManager;
import fr.aboucorp.variantchess.entities.events.models.GameEvent;
import fr.aboucorp.variantchess.entities.moves.movesets.KingMoveSet;


public class King extends Piece {

    public King(Square square, ChessColor chessColor, PieceId pieceId, ClassicBoard classicBoard, GameEventManager gameEventManager) {
        super(square, chessColor,pieceId);
        this.moveSet = new KingMoveSet(this, classicBoard,gameEventManager);
    }

    @Override
    public char fen() {
        return this.getChessColor() == ChessColor.WHITE ? 'K' : 'k';
    }
}