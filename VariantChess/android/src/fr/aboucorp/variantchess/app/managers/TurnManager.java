package fr.aboucorp.variantchess.app.managers;

import java.util.LinkedList;

import fr.aboucorp.variantchess.app.parcelables.MatchP;
import fr.aboucorp.variantchess.entities.ChessColor;
import fr.aboucorp.variantchess.entities.Match;
import fr.aboucorp.variantchess.entities.PartyLifeCycle;
import fr.aboucorp.variantchess.entities.Player;
import fr.aboucorp.variantchess.entities.Turn;
import fr.aboucorp.variantchess.entities.events.GameEventManager;
import fr.aboucorp.variantchess.entities.events.models.MoveEvent;
import fr.aboucorp.variantchess.entities.events.models.TurnEndEvent;
import fr.aboucorp.variantchess.entities.events.models.TurnStartEvent;

public class TurnManager implements PartyLifeCycle {

    private static TurnManager INSTANCE;
    private LinkedList<Turn> turns;
    private GameEventManager eventManager;
    private Player whitePlayer;
    private Player blackPlayer;

    private TurnManager() {
        this.turns = new LinkedList<>();
        this.eventManager = GameEventManager.getINSTANCE();
    }

    public void startTurn(){
        Turn nextTurn;
        Player player = null;
        if (this.turns.getLast().getTurnColor() == ChessColor.WHITE) {
            player =  this.blackPlayer;
        }else{
            player =  this.whitePlayer;
        }
        nextTurn = new Turn(this.turns.size()+1, player);
        this.turns.add(nextTurn);
        String eventMessage = String.format("Turn %s, color : %s",nextTurn.getTurnNumber(), nextTurn.getTurnColor());
        this.eventManager.sendMessage(new TurnStartEvent(eventMessage,nextTurn));
    }

    public Turn endTurn(MoveEvent event, String fenFromBoard){
        if(event !=null) {
            this.turns.getLast().setPlayed(event.played);
            this.turns.getLast().setFen(fenFromBoard);
            this.turns.getLast().setTo(event.to);
            this.turns.getLast().setFrom( event.from);
            this.turns.getLast().setDeadPiece(event.deadPiece);
        }
        this.eventManager.sendMessage(new TurnEndEvent("Ending turn",this.turns.getLast()));
        return this.turns.getLast();
    }


    public ChessColor getTurnColor(){
        if(!this.turns.isEmpty()) {
            return this.turns.getLast().getPlayer().getColor();
        }else{
            return ChessColor.WHITE;
        }
    }

    public static TurnManager getINSTANCE(){
        if(INSTANCE == null){
            INSTANCE = new TurnManager();
        }
        return INSTANCE;
    }

    @Override
    public void startParty(Match match) {
        this.whitePlayer = ((MatchP)match).getWhitePlayer();
        this.blackPlayer = ((MatchP)match).getBlackPlayer();
        Player player;
        int turnNumber;
        if(((MatchP)match).getTurns().size() > 0){
            this.turns.addAll(((MatchP)match).getTurns());
            if( this.turns.getLast().getPlayer().getColor() == ChessColor.WHITE){
                player = this.blackPlayer;
            }else{
                player = this.whitePlayer;
            }
            turnNumber = ((MatchP)match).getTurns().size() + 1;
        }else{
            player = whitePlayer;
            turnNumber = 1;
        }
        Turn turn = new Turn(turnNumber,player);
        this.turns.add(turn);
        String eventMessage = String.format("Turn %s color : %s",turn.getTurnNumber(), turn.getTurnColor());
        this.eventManager.sendMessage(new TurnStartEvent(eventMessage,turn));
    }

    @Override
    public void stopParty() {
        this.turns = new LinkedList<>();
    }

    public void startAtTurn(int turnNumber) {
        Player player = turnNumber % 2 == 1 ? this.whitePlayer : this.blackPlayer;
        Turn firsTurn = new Turn(turnNumber,player);
        this.turns.add(firsTurn);
        String eventMessage = String.format("Turn color : %s",firsTurn.getTurnColor());
        this.eventManager.sendMessage(new TurnStartEvent(eventMessage,firsTurn));
    }
}
