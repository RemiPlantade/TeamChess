package fr.aboucorp.teamchess.app.managers;

import android.util.Log;

import fr.aboucorp.teamchess.app.managers.boards.BoardManager;
import fr.aboucorp.teamchess.entities.model.ChessColor;
import fr.aboucorp.teamchess.entities.model.enums.BoardEventType;
import fr.aboucorp.teamchess.entities.model.events.GameEventManager;
import fr.aboucorp.teamchess.entities.model.events.GameEventSubscriber;
import fr.aboucorp.teamchess.entities.model.events.models.BoardEvent;
import fr.aboucorp.teamchess.entities.model.events.models.GameEvent;
import fr.aboucorp.teamchess.entities.model.events.models.LogEvent;
import fr.aboucorp.teamchess.entities.model.events.models.MoveEvent;
import fr.aboucorp.teamchess.entities.model.events.models.PartyEvent;

public class PartyManager implements GameEventSubscriber {
    private final BoardManager boardManager;
    private final TurnManager turnManager;
    private GameEventManager eventManager;


    public PartyManager(BoardManager boardManager) {
        this.boardManager = boardManager;
        this.turnManager = TurnManager.getINSTANCE();
        this.eventManager = GameEventManager.getINSTANCE();
        this.eventManager.subscribe(PartyEvent.class,this,1);
        this.eventManager.subscribe(BoardEvent.class,this,1);
    }

    public void startGame(){
        this.boardManager.createBoard();
        this.turnManager.newParty(ChessColor.WHITE);
    }

    public void loadBoard(String fenString){
        try {
            ChessColor color = this.boardManager.loadBoard(fenString);
            this.turnManager.newParty(color);
        } catch (Exception e) {
            e.printStackTrace();
            this.eventManager.sendMessage(new LogEvent(String.format("Error during parsing fen string. Message : %s",e.getMessage())));
        }
    }

    public void endTurn() {
        this.turnManager.endTurn();
    }

    public String getPartyInfos(){
        return this.turnManager.getTurnColor().name();
    }

    @Override
    public void receiveGameEvent(GameEvent event) {
        if(event instanceof PartyEvent){
            Log.i("fr.aboucorp.teamchess",event.message);
        }else if(event instanceof BoardEvent && ((BoardEvent) event).type == BoardEventType.CHECKMATE){
            ChessColor winner = boardManager.getWinner();
            this.eventManager.sendMessage(new PartyEvent(String.format("Game finished ! Winner : %s",winner != null ? winner.name() : "EQUALITY")));
        }else if(event instanceof MoveEvent && ((BoardEvent) event).type == BoardEventType.MOVE){
            endTurn();
        }
    }
}