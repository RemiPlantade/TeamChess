package fr.aboucorp.variantchess.app.views.activities;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStore;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.preference.PreferenceManager;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.heroiclabs.nakama.api.User;

import fr.aboucorp.variantchess.R;
import fr.aboucorp.variantchess.app.listeners.MatchEventListener;
import fr.aboucorp.variantchess.app.managers.MatchManager;
import fr.aboucorp.variantchess.app.managers.boards.ClassicBoardManager;
import fr.aboucorp.variantchess.app.multiplayer.SessionManager;
import fr.aboucorp.variantchess.app.viewmodel.UserViewModel;
import fr.aboucorp.variantchess.app.views.fragments.SettingsFragment;
import fr.aboucorp.variantchess.entities.ChessMatch;
import fr.aboucorp.variantchess.entities.boards.Board;
import fr.aboucorp.variantchess.entities.boards.ClassicBoard;
import fr.aboucorp.variantchess.entities.events.GameEventManager;
import fr.aboucorp.variantchess.entities.events.GameEventSubscriber;
import fr.aboucorp.variantchess.entities.events.models.BoardEvent;
import fr.aboucorp.variantchess.entities.events.models.GameEvent;
import fr.aboucorp.variantchess.entities.events.models.TurnEvent;
import fr.aboucorp.variantchess.entities.rules.ClassicRuleSet;
import fr.aboucorp.variantchess.libgdx.Board3dManager;

public class BoardActivity extends AndroidApplication implements GameEventSubscriber, MatchEventListener, ViewModelStoreOwner {

    public FrameLayout board_panel;
    private Button btn_end_turn;
    private Switch switch_tactical;
    private TextView lbl_turn;
    private TextView party_logs;
    private Board3dManager board3dManager;
    private ClassicBoardManager boardManager;
    private MatchManager matchManager;
    private SessionManager sessionManager;
    private Toolbar toolbar;
    private User user;
    private ChessMatch chessMatch;
    private GameEventManager gameEventManager;

    @Override
    public void OnMatchEvent(GameEvent event) {
        if (!(event instanceof TurnEvent)) {
            this.runOnUiThread(() ->
                    this.party_logs.append("\n" + event.message));
        }
    }

    @NonNull
    @Override
    public ViewModelStore getViewModelStore() {
        return null;
    }

    @Override
    public void onConfigurationChanged(Configuration config) {
        super.onConfigurationChanged(config);
        this.setContentView(R.layout.board_layout);
        this.bindViews();
        this.bindListeners();
        this.setToolbar();
        AndroidApplicationConfiguration libgdxConfig = new AndroidApplicationConfiguration();
        this.board_panel.addView(this.initializeForView(this.board3dManager, libgdxConfig));
    }

    @Override
    public void exit() {
        super.exit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        UserViewModel userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.board_layout);
        if (savedInstanceState != null) {
            this.chessMatch = (ChessMatch) savedInstanceState.getSerializable("chessMatch");
        } else {
            this.chessMatch = (ChessMatch) this.getIntent().getExtras().getSerializable("chessMatch");
        }
        this.bindViews();
        this.bindListeners();
        this.setToolbar();
        this.initializeBoard();
        this.matchManager.setEventListener(this);
        this.matchManager.startParty(this.chessMatch);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putSerializable("chessMatch", this.chessMatch);
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("If you exit the game you will loose the chessMatch");
        alertDialogBuilder.setPositiveButton("yes",
                (dialog, arg1) -> this.stopParty());
        alertDialogBuilder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    @Override
    public void receiveGameEvent(GameEvent event) {
        this.runOnUiThread(() -> {
            if (event instanceof BoardEvent || event instanceof TurnEvent) {
                BoardActivity.this.party_logs.setText(BoardActivity.this.party_logs.getText() + "\n" + event.message);
            }
            BoardActivity.this.lbl_turn.setText("Turn of " + BoardActivity.this.matchManager.getPartyInfos());
        });
    }

    private void bindViews() {
        this.board_panel = this.findViewById(R.id.board);
        this.btn_end_turn = this.findViewById(R.id.btn_end_turn);
        this.lbl_turn = this.findViewById(R.id.lbl_turn);
        this.party_logs = this.findViewById(R.id.party_logs);
        this.switch_tactical = this.findViewById(R.id.switch_tactical);
    }

    private void bindListeners() {
        this.btn_end_turn.setOnClickListener(v -> {
            BoardActivity.this.matchManager.endTurn(null);
            this.runOnUiThread(() ->
                    BoardActivity.this.lbl_turn.setText("Turn of " + BoardActivity.this.matchManager.getPartyInfos()));
        });
        this.switch_tactical.setOnClickListener(v -> BoardActivity.this.boardManager.toogleTacticalView());
    }

    private void setToolbar() {
        this.toolbar = this.findViewById(R.id.main_toolbar);
        this.toolbar.setTitle(this.getString(R.string.app_name));
        this.toolbar.setSubtitle(this.user != null ? this.user.getDisplayName() : "Disconnected");
        this.setActionBar(this.toolbar);
        this.toolbar.setNavigationOnClickListener(v -> this.onBackPressed());
        this.getActionBar().setDisplayHomeAsUpEnabled(true);
        this.getActionBar().setHomeButtonEnabled(false);
        this.getActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back);
    }

    private void initializeBoard() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isTactical = sharedPref.getBoolean(SettingsFragment.IS_TACTICAL_MODE_ON, false);
        this.gameEventManager = new GameEventManager();
        this.gameEventManager.subscribe(GameEvent.class, this, 1);
        this.board3dManager = new Board3dManager();
        this.board3dManager.setTacticalViewEnabled(isTactical);
        Board classicBoard = new ClassicBoard(this.gameEventManager);
        ClassicRuleSet classicRules = new ClassicRuleSet(classicBoard, this.gameEventManager);
        this.boardManager = new ClassicBoardManager(this.board3dManager, classicBoard, classicRules, this.gameEventManager);
        this.sessionManager = SessionManager.getInstance(this);
        this.matchManager = new MatchManager(this.boardManager, this.gameEventManager, this.sessionManager);
        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
        this.board_panel.addView(this.initializeForView(this.board3dManager, config));
    }

    private void stopParty() {
        this.matchManager.stopParty();
        this.board3dManager.exit();
    }
}
