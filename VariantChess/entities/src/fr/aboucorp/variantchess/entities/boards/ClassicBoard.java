package fr.aboucorp.variantchess.entities.boards;

import fr.aboucorp.variantchess.entities.ChessColor;
import fr.aboucorp.variantchess.entities.Location;
import fr.aboucorp.variantchess.entities.Square;
import fr.aboucorp.variantchess.entities.enums.PieceId;
import fr.aboucorp.variantchess.entities.exceptions.FenStringBadFormatException;
import fr.aboucorp.variantchess.entities.pieces.Bishop;
import fr.aboucorp.variantchess.entities.pieces.King;
import fr.aboucorp.variantchess.entities.pieces.Knight;
import fr.aboucorp.variantchess.entities.pieces.Pawn;
import fr.aboucorp.variantchess.entities.pieces.Queen;
import fr.aboucorp.variantchess.entities.pieces.Rook;
import fr.aboucorp.variantchess.entities.utils.PieceList;
import fr.aboucorp.variantchess.entities.utils.SquareList;

public class ClassicBoard extends Board{

    private SquareList chessSquares;
    private PieceList blackPieces;
    private PieceList blackDeadPieces;
    private PieceList whitePieces;
    private PieceList whiteDeadPieces;

    public ClassicBoard(){
        this.chessSquares = new SquareList();
        this.blackPieces = new PieceList();
        this.whitePieces = new PieceList();
        this.blackDeadPieces = new PieceList();
        this.whiteDeadPieces = new PieceList();
    }

    @Override
    public void initBoard(){
        this.createSquares();
        this.createWhitePieces();
        this.createBlackPieces();
    }

    private void createSquares(){
        for (float x = 0f; x < 8; x++) {
            for (float z = 0f; z < 8f; z++) {
                Square square;
                if(x % 2 == 0 && z % 2 != 0 || x % 2 != 0 && z % 2 == 0 ){
                    square = new Square(new Location(x, 0, z), ChessColor.WHITE);
                }else{
                square = new Square(new Location(x, 0, z), ChessColor.BLACK);
                }
                this.chessSquares.add(square);
            }
        }

    }

    private void createWhitePieces(){
        for(int  i = 0 ; i < 8 ; i++){
            this.whitePieces.add(createPawn(new Location(i,0,1), ChessColor.WHITE, PieceId.get(i)));
        }
        this.whitePieces.add(createKnight(new Location(6,0,0), ChessColor.WHITE, PieceId.WRN));
        this.whitePieces.add(createKnight(new Location(1,0,0), ChessColor.WHITE, PieceId.WLN));
        this.whitePieces.add(createBishop(new Location(5,0,0), ChessColor.WHITE, PieceId.WRB));
        this.whitePieces.add(createBishop(new Location(2,0,0), ChessColor.WHITE, PieceId.WLB));
        this.whitePieces.add(createQueen(new Location(4,0,0), ChessColor.WHITE, PieceId.WQ));
        this.whitePieces.add(createKing(new Location(3,0,0), ChessColor.WHITE, PieceId.WK));
        this.whitePieces.add(createRook(new Location(7,0,0), ChessColor.WHITE, PieceId.WLR));
        this.whitePieces.add(createRook(new Location(0,0,0), ChessColor.WHITE, PieceId.WRR));
    }

    private void createBlackPieces(){
        for(int  i = 0 ; i < 8 ; i++){
            this.blackPieces.add(createPawn(new Location(i,0,6), ChessColor.BLACK, PieceId.get(i+10)));
        }
        this.blackPieces.add(createKnight(new Location(6,0,7), ChessColor.BLACK, PieceId.BRN));
        this.blackPieces.add(createKnight(new Location(1,0,7), ChessColor.BLACK, PieceId.BLN));
        this.blackPieces.add(createBishop(new Location(5,0,7), ChessColor.BLACK, PieceId.BLB));
        this.blackPieces.add(createBishop(new Location(2,0,7), ChessColor.BLACK, PieceId.BRB));
        this.blackPieces.add(createQueen(new Location(4,0,7), ChessColor.BLACK, PieceId.BQ));
        this.blackPieces.add(createKing(new Location(3,0,7), ChessColor.BLACK, PieceId.BK));
        this.blackPieces.add(createRook(new Location(7,0,7), ChessColor.BLACK, PieceId.BLR));
        this.blackPieces.add(createRook(new Location(0,0,7), ChessColor.BLACK, PieceId.BRR));
    }

    private Knight createKnight(Location location, ChessColor color, PieceId pieceID){
        return new Knight((Square) this.chessSquares.getItemByLocation(location), color,pieceID,this);
    }

    private Bishop createBishop(Location location, ChessColor color, PieceId pieceID){
        return new Bishop((Square) this.chessSquares.getItemByLocation(location), color,pieceID,this);
    }

    private Rook createRook(Location location, ChessColor color, PieceId pieceID){
        return new Rook((Square) this.chessSquares.getItemByLocation(location), color,pieceID,this);
    }

    private Queen createQueen(Location location, ChessColor color, PieceId pieceID){
        return new Queen((Square) this.chessSquares.getItemByLocation(location), color,pieceID,this);
    }

    private King createKing(Location location, ChessColor color, PieceId pieceID){
        return new King((Square) this.chessSquares.getItemByLocation(location), color,pieceID,this);
    }

    private Pawn createPawn(Location location, ChessColor color, PieceId pieceID){
        return new Pawn((Square) this.chessSquares.getItemByLocation(location), color,pieceID,this);
    }

    @Override
    public void loadBoard(String fenString) throws FenStringBadFormatException {
        String[] lines = fenString.split("/");
        if(lines.length != 8){
            throw new FenStringBadFormatException("Cannot load game from fen string, fen string doesn't contains enought lines");
        }
        createPiecesFromFen(lines);
    }

    private void createPiecesFromFen(String[] lines) throws FenStringBadFormatException {
        boolean firstWN = true, firstBN = true, firstWB = true, firstBB = true,firstWR = true,firstBR = true;
        for(int i = 0 ; i < lines.length;i++){
            int caseIndex = 0;
            for(int j = 0 ;  j < lines[i].length() ; j++){
                int xPos = 7-caseIndex;
                int zPos = 7-i;
                switch(lines[i].charAt(j)){
                    case 'p':
                        this.blackPieces.add(createPawn(new Location(xPos,0,zPos), ChessColor.BLACK, PieceId.get(xPos)));
                        break;
                    case 'P':
                        this.whitePieces.add(createPawn(new Location(xPos,0,zPos), ChessColor.WHITE, PieceId.get(xPos+10)));
                        break;
                    case 'r':
                        this.blackPieces.add(createRook(new Location(xPos,0,zPos), ChessColor.BLACK,firstBR ? PieceId.BLR: PieceId.BRR));
                        firstBR = false;
                        break;
                    case 'R':
                        this.whitePieces.add(createRook(new Location(xPos,0,zPos), ChessColor.WHITE,firstWR ? PieceId.WLR: PieceId.WRR));
                        firstWR = false;
                        break;
                    case 'b':
                        this.blackPieces.add(createBishop(new Location(xPos,0,zPos), ChessColor.BLACK,firstBB ? PieceId.BLB: PieceId.BRB));
                        firstBB = false;
                        break;
                    case 'B':
                        this.whitePieces.add(createBishop(new Location(xPos,0,zPos), ChessColor.WHITE,firstWB ? PieceId.WLB: PieceId.WRB));
                        firstWB = false;
                        break;
                    case 'n':
                        this.blackPieces.add(createKnight(new Location(xPos,0,zPos), ChessColor.BLACK,firstBN ? PieceId.BLN: PieceId.BRN));
                        firstBN = false;
                        break;
                    case 'N':
                        this.whitePieces.add(createKnight(new Location(xPos,0,zPos), ChessColor.WHITE,firstWN ? PieceId.WLN: PieceId.WRN));
                        firstWN = false;
                        break;
                    case 'k':
                        this.blackPieces.add(createKing(new Location(xPos,0,zPos), ChessColor.BLACK, PieceId.BK));
                        break;
                    case 'K':
                        this.whitePieces.add(createKing(new Location(xPos,0,zPos), ChessColor.WHITE, PieceId.WK));
                        break;
                    case 'q':
                        this.blackPieces.add(createQueen(new Location(xPos,0,zPos), ChessColor.BLACK, PieceId.BQ));
                        break;
                    case 'Q':
                        this.whitePieces.add(createQueen(new Location(xPos,0,zPos), ChessColor.WHITE, PieceId.WQ));
                        break;
                    default:
                        try {
                            int emptyCells = Integer.parseInt(lines[i].charAt(j) + "");
                            if(emptyCells < 1 || emptyCells > 8){
                                throw new FenStringBadFormatException("Incorrect Number of empty cells");
                            }
                            caseIndex = caseIndex + emptyCells-1;
                        }catch (Exception ex){
                            throw new FenStringBadFormatException("Incorrect Number of empty cells");
                        }
                        break;
                }
                caseIndex++;
            }
        }
    }

    @Override
    public SquareList getSquares() {
        return chessSquares;
    }

    @Override
    public PieceList getBlackPieces() {
        return blackPieces;
    }

    @Override
    public PieceList getWhitePieces() {
        return whitePieces;
    }

    @Override
    public PieceList getPiecesByColor(ChessColor color){
        return color == ChessColor.WHITE ? getWhitePieces() : getBlackPieces();
    }

    @Override
    public void clearBoard() {
        this.whitePieces = new PieceList();
        this.blackPieces = new PieceList();
        this.blackDeadPieces = new PieceList();
        this.whiteDeadPieces = new PieceList();
        this.chessSquares = new SquareList();
    }
}
