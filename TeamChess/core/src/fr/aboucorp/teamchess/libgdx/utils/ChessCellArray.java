package fr.aboucorp.teamchess.libgdx.utils;

import com.badlogic.gdx.utils.Array;

import java.util.Iterator;

import fr.aboucorp.teamchess.libgdx.exceptions.CellNotFoundException;
import fr.aboucorp.teamchess.libgdx.models.ChessCell;
import fr.aboucorp.teamchess.libgdx.models.ChessModel;

public class ChessCellArray extends Array<ChessModel> {

    public ChessCell getPieceByLocation(int x , int y, int z) throws CellNotFoundException {
        for (Iterator<ChessModel> iter = this.iterator(); iter.hasNext(); ) {
            ChessCell cell = (ChessCell) iter.next();
           if(cell.isLocatedIn(x,y,z)){
               return cell;
           }
        }
        throw new CellNotFoundException(String.format("No cell found for coordinates x:%s; y:%s; z:%s;", + x , y ,z));
    }
}
