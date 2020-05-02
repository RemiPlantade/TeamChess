package fr.aboucorp.teamchess.app.views;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

import fr.aboucorp.teamchess.R;
import fr.aboucorp.teamchess.app.listeners.GDXGestureListener;
import fr.aboucorp.teamchess.app.listeners.GDXInputAdapter;
import fr.aboucorp.teamchess.app.managers.PartyManager;
import fr.aboucorp.teamchess.app.managers.boards.BoardManager;
import fr.aboucorp.teamchess.app.managers.boards.ClassicBoardManager;
import fr.aboucorp.teamchess.entities.model.boards.Board;
import fr.aboucorp.teamchess.entities.model.boards.ClassicBoard;
import fr.aboucorp.teamchess.entities.model.events.GameEventManager;
import fr.aboucorp.teamchess.entities.model.events.GameEventSubscriber;
import fr.aboucorp.teamchess.entities.model.events.models.BoardEvent;
import fr.aboucorp.teamchess.entities.model.events.models.GameEvent;
import fr.aboucorp.teamchess.entities.model.events.models.TurnEvent;
import fr.aboucorp.teamchess.entities.model.rules.ClassicRuleSet;
import fr.aboucorp.teamchess.libgdx.Board3dManager;

public class BoardActivity extends AndroidApplication implements GameEventSubscriber {

    public FrameLayout board_panel;
    public Button btn_end_turn;
    public Button btn_test;
    public Switch switch_tactical;
    public GameEventManager eventManager;
    private PartyManager partyManager;
    private BoardManager boardManager;
    private TextView lbl_turn;
    private TextView party_logs;
    private EditText fen_txt;

    public BoardActivity(){
        this.eventManager = GameEventManager.getINSTANCE();
        this.eventManager.subscribe(GameEvent.class,this,1);
    }

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.board);
        bindViews();
        bindListeners();
        this.initializeBoard();
    }

    private void initializeBoard(){
        Board3dManager board3dManager = new Board3dManager();
        Board classicBoard = new ClassicBoard();
        ClassicRuleSet classicRules = new ClassicRuleSet(classicBoard);
        boardManager = new ClassicBoardManager(board3dManager, classicBoard,classicRules);
        this.partyManager = new PartyManager(boardManager);
        InputAdapter inputAdapter = new GDXInputAdapter(board3dManager);
        board3dManager.setAndroidInputAdapter(inputAdapter);
        GDXGestureListener gestureListener = new GDXGestureListener(boardManager);
        board3dManager.setAndroidListener(gestureListener);
        this.bindViews();
        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
        board_panel.addView(initializeForView(board3dManager, config));
        this.partyManager.startGame();
        this.lbl_turn.setText("Turn of " + BoardActivity.this.partyManager.getPartyInfos());
    }

    public void bindViews() {
        this.board_panel = findViewById(R.id.board_layout);
        this.btn_end_turn = findViewById(R.id.btn_end_turn);
        this.lbl_turn = findViewById(R.id.lbl_turn);
        this.party_logs = findViewById(R.id.party_logs);
        this.btn_test = findViewById(R.id.btn_test);
        this.fen_txt = findViewById(R.id.fen_txt);
        this.switch_tactical = findViewById(R.id.switch_tactical);
    }

    public void bindListeners() {
        btn_end_turn.setOnClickListener(v -> {
           BoardActivity.this.partyManager.endTurn();
           BoardActivity.this.lbl_turn.setText("Turn of " + BoardActivity.this.partyManager.getPartyInfos());
        });
        btn_test.setOnClickListener(v -> BoardActivity.this.partyManager.loadBoard(fen_txt.getText().toString().trim()));

        switch_tactical.setOnClickListener(v -> BoardActivity.this.boardManager.toogleTacticalView());
    }

    @Override
    public void receiveGameEvent(final GameEvent event) {
        this.runOnUiThread(() -> {
            if(event instanceof BoardEvent || event instanceof TurnEvent){
                BoardActivity.this.party_logs.setText( BoardActivity.this.party_logs.getText() + "\nLOG :" + event.message);
            }
            BoardActivity.this.lbl_turn.setText("Turn of " + BoardActivity.this.partyManager.getPartyInfos());
            Log.i("fr.aboucorp.teamchess",event.message);
        });
    }
}