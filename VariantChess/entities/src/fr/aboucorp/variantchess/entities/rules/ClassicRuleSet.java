package fr.aboucorp.variantchess.entities.rules;

import java.util.List;

import fr.aboucorp.variantchess.entities.ChessColor;
import fr.aboucorp.variantchess.entities.Piece;
import fr.aboucorp.variantchess.entities.Square;
import fr.aboucorp.variantchess.entities.Turn;
import fr.aboucorp.variantchess.entities.boards.Board;
import fr.aboucorp.variantchess.entities.enums.BoardEventType;
import fr.aboucorp.variantchess.entities.enums.PieceId;
import fr.aboucorp.variantchess.entities.events.GameEventManager;
import fr.aboucorp.variantchess.entities.events.GameEventSubscriber;
import fr.aboucorp.variantchess.entities.events.models.BoardEvent;
import fr.aboucorp.variantchess.entities.events.models.CastlingEvent;
import fr.aboucorp.variantchess.entities.events.models.CheckInEvent;
import fr.aboucorp.variantchess.entities.events.models.CheckOutEvent;
import fr.aboucorp.variantchess.entities.events.models.EnPassantEvent;
import fr.aboucorp.variantchess.entities.events.models.GameEvent;
import fr.aboucorp.variantchess.entities.events.models.PartyEvent;
import fr.aboucorp.variantchess.entities.events.models.PieceEvent;
import fr.aboucorp.variantchess.entities.events.models.TurnEndEvent;
import fr.aboucorp.variantchess.entities.events.models.TurnEvent;
import fr.aboucorp.variantchess.entities.events.models.TurnStartEvent;
import fr.aboucorp.variantchess.entities.utils.PieceList;

public class ClassicRuleSet extends AbstractRuleSet implements GameEventSubscriber {

    private final Board board;
    public static int FIFTY_MOVE_RULE_NUMBER = 75;
    public int fiftyMoveCounter = 0;
    public boolean whiteCanCastleKing;
    public boolean whiteCanCastleQueen;
    public boolean blackCanCastleKing;
    public boolean blackCanCastleQueen;
    private boolean kingIsInCheck;
    public Square enPassant;
    private fr.aboucorp.variantchess.entities.Turn previousTurn;
    private Turn actualTurn;

    private GameEventManager eventManager;

    public ClassicRuleSet(Board board) {
        this.board = board;
        this.eventManager = GameEventManager.getINSTANCE();
        this.eventManager.subscribe(TurnEvent.class,this,1);
    }

    public void isKingInCheck(Piece piece) {
        if (kingIsInCheck) {
            this.eventManager.sendMessage(new CheckOutEvent("King out of check", BoardEventType.CHECK_OUT, piece));
            this.kingIsInCheck = false;
        }
        List<Piece> causingCheck = piece.getMoveSet().moveCauseCheck(actualTurn.getTurnColor());
        if (causingCheck.size() > 0) {
            this.kingIsInCheck = true;
            Piece kingInCheck = this.previousTurn.getTurnColor() == ChessColor.WHITE
                    ? this.board.getWhitePieces().getPieceById(PieceId.WK)
                    : this.board.getBlackPieces().getPieceById(PieceId.BK);
            this.eventManager.sendMessage(new CheckInEvent("King is in check", BoardEventType.CHECK_IN, kingInCheck, causingCheck));
        }
    }

    public boolean isEnPassantMove(Piece piece, Square destination) {
        return this.enPassant != null
                && destination.getPiece() == null
                && piece.getLocation().getX() != destination.getLocation().getX();
    }

    public void checkIfCastling(Square square) {
        ChessColor turnColor = this.actualTurn.getTurnColor();
        Piece rookToMove = null;
        Square destination = null;
        fr.aboucorp.variantchess.entities.enums.BoardEventType castlingType = null;
        if (this.whiteCanCastleQueen && turnColor == ChessColor.WHITE && square.getSquareLabel().equals("C1")) {
            rookToMove = this.board.getWhitePieces().getPieceById(PieceId.WLR);
            destination = this.board.getSquares().getSquareByLabel("D1");
            castlingType = BoardEventType.CASTLE_QUEEN;
        }
        if (this.blackCanCastleQueen && turnColor == ChessColor.BLACK && square.getSquareLabel().equals("C8")) {
            rookToMove = this.board.getBlackPieces().getPieceById(PieceId.BLR);
            destination = this.board.getSquares().getSquareByLabel("D8");
            castlingType = BoardEventType.CASTLE_QUEEN;
        }
        if (this.whiteCanCastleKing && turnColor == ChessColor.WHITE && square.getSquareLabel().equals("G1")) {
            rookToMove = this.board.getWhitePieces().getPieceById(PieceId.WRR);
            destination = this.board.getSquares().getSquareByLabel("F1");
            castlingType = BoardEventType.CASTLE_KING;
        }
        if (this.blackCanCastleKing && turnColor == ChessColor.BLACK && square.getSquareLabel().equals("G8")) {
            rookToMove = this.board.getBlackPieces().getPieceById(PieceId.BRR);
            destination = this.board.getSquares().getSquareByLabel("F8");
            castlingType = BoardEventType.CASTLE_KING;
        }
        if (rookToMove != null && destination != null) {
            this.eventManager.sendMessage(new CastlingEvent("Castling",castlingType,rookToMove,destination));
        }
    }

    public void canCastle() {
        if (whiteCanCastleKingNow()) {
            this.whiteCanCastleKing = true;
            this.eventManager.sendMessage(new fr.aboucorp.variantchess.entities.events.models.PieceEvent("White can castle on king side", BoardEventType.CASTLE_KING, this.board.getWhitePieces().getPieceById(PieceId.WK)));
        } else {
            this.whiteCanCastleKing = false;
        }
        if (whiteCanCastleQueenNow()) {
            this.whiteCanCastleQueen = true;
            this.eventManager.sendMessage(new fr.aboucorp.variantchess.entities.events.models.PieceEvent("White can castle on queen side", BoardEventType.CASTLE_QUEEN, this.board.getWhitePieces().getPieceById(PieceId.WK)));
        } else {
            this.whiteCanCastleQueen = false;
        }
        if (blackCanCastleKingNow()) {
            this.blackCanCastleKing = true;
            this.eventManager.sendMessage(new fr.aboucorp.variantchess.entities.events.models.PieceEvent("Black can castle on king side", BoardEventType.CASTLE_KING, this.board.getBlackPieces().getPieceById(PieceId.BK)));
        } else {
            this.blackCanCastleKing = false;
        }
        if (blackCanCastleQueenNow()) {
            this.blackCanCastleQueen = true;
            this.eventManager.sendMessage(new PieceEvent("Black can castle on queen side", BoardEventType.CASTLE_QUEEN, this.board.getBlackPieces().getPieceById(PieceId.BK)));
        } else {
            this.blackCanCastleQueen = false;
        }
    }

    private boolean blackCanCastleQueenNow() {
        return this.blackCanCastleQueen()
                && this.board.getSquares().getSquareByLabel("D8").getPiece() == null
                && this.board.getSquares().getSquareByLabel("C8").getPiece() == null;
    }

    public boolean blackCanCastleQueen() {
        return this.board.getBlackPieces().getPieceById(PieceId.BK).isFirstMove()
                && this.board.getBlackPieces().getPieceById(PieceId.BLR).isFirstMove();
    }

    private boolean blackCanCastleKingNow() {
        return this.blackCanCastleKing()
                && this.board.getSquares().getSquareByLabel("F8").getPiece() == null
                && this.board.getSquares().getSquareByLabel("G8").getPiece() == null;
    }

    public boolean blackCanCastleKing() {
        return this.board.getBlackPieces().getPieceById(PieceId.BK).isFirstMove()
                && this.board.getBlackPieces().getPieceById(PieceId.BLR).isFirstMove();
    }

    private boolean whiteCanCastleQueenNow() {
        return whiteCanCastleQueen()
                && this.board.getSquares().getSquareByLabel("D1").getPiece() == null
                && this.board.getSquares().getSquareByLabel("C1").getPiece() == null
                && this.board.getSquares().getSquareByLabel("CB1").getPiece() == null;
    }

    public boolean whiteCanCastleQueen() {
        return this.board.getWhitePieces().getPieceById(PieceId.WK).isFirstMove()
                && this.board.getWhitePieces().getPieceById(PieceId.WLR).isFirstMove() ;
    }

    private boolean whiteCanCastleKingNow() {
        return whiteCanCastleKing()
                && this.board.getSquares().getSquareByLabel("G1").getPiece() == null
                && this.board.getSquares().getSquareByLabel("F1").getPiece() == null;
    }

    public boolean whiteCanCastleKing() {
        return this.board.getWhitePieces().getPieceById(PieceId.WK).isFirstMove()
                && this.board.getWhitePieces().getPieceById(PieceId.WRR).isFirstMove();

    }

    public void canClaimADraw() {
        fiftyMoveRule();
    }

    private void fiftyMoveRule() {
        if (previousTurn != null) {
            if (PieceId.isPawn(this.previousTurn.played.getPieceId()) || previousTurn.getDeadPiece() != null) {
                this.fiftyMoveCounter = 0;
            } else {
                this.fiftyMoveCounter++;
            }
        }
        if (fiftyMoveCounter >= FIFTY_MOVE_RULE_NUMBER * 2) {
            this.eventManager.sendMessage(new PartyEvent("Player can claim a draw following the fifty move rule "));
        }
    }

    public void isGameFinished() {
        if (previousTurn != null) {
            boolean cantMove = true;
            for (Piece piece : board.getPiecesByColor(this.actualTurn.getTurnColor())) {
                if (piece.getMoveSet().getNextMoves().size() > 0) {
                    cantMove = false;
                }
            }
            if (cantMove) {
                if (this.kingIsInCheck) {
                    this.eventManager.sendMessage(new BoardEvent("Game is finished", BoardEventType.MATE));
                } else {
                    this.eventManager.sendMessage(new BoardEvent("Game is finished", BoardEventType.DRAW));
                }
            }
        }
    }

    public ChessColor getWinner() {
        PieceList opposites;
        ChessColor oppositeColor = actualTurn.getTurnColor() == ChessColor.WHITE ? ChessColor.BLACK : ChessColor.WHITE;
        if (actualTurn.getTurnColor() == ChessColor.WHITE) {
            opposites = this.board.getBlackPieces();
        } else {
            opposites = this.board.getWhitePieces();
        }
        boolean canMove = false;
        for (Piece piece : opposites) {
            piece.getMoveSet().calculateNextMoves(oppositeColor);
            if (piece.getMoveSet().getNextMoves().size() > 0) {
                canMove = true;
            }
        }
        if (canMove) {
            return oppositeColor;
        }
        return null;
    }

    @Override
    public void receiveGameEvent(GameEvent event) {
        if (event instanceof EnPassantEvent) {
            if (((EnPassantEvent) event).type == fr.aboucorp.variantchess.entities.enums.BoardEventType.EN_PASSANT) {
                this.enPassant = ((EnPassantEvent) event).destination;
            }
        }else if(event instanceof TurnStartEvent){
            if(previousTurn != null) {
                this.canClaimADraw();
                this.canCastle();
                isGameFinished();
            }
            this.previousTurn = actualTurn;
            this.actualTurn = ((TurnStartEvent) event).turn;
            if (((TurnStartEvent) event).turn.getTurnColor() == ChessColor.WHITE) {
                this.moveNumber++;
            }
        }else if(event instanceof TurnEndEvent){
            this.enPassant = null;
        }
    }

}
