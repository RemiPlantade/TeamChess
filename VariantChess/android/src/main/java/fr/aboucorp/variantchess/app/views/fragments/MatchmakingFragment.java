package fr.aboucorp.variantchess.app.views.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.heroiclabs.nakama.MatchmakerMatched;
import com.heroiclabs.nakama.api.User;

import java.util.List;

import fr.aboucorp.variantchess.R;
import fr.aboucorp.variantchess.app.db.entities.ChessUser;
import fr.aboucorp.variantchess.app.db.entities.GameRules;
import fr.aboucorp.variantchess.app.db.viewmodel.UserViewModel;
import fr.aboucorp.variantchess.app.multiplayer.SessionManager;
import fr.aboucorp.variantchess.app.multiplayer.listeners.MatchmakingListener;
import fr.aboucorp.variantchess.app.utils.AsyncHandler;
import fr.aboucorp.variantchess.entities.ChessColor;
import fr.aboucorp.variantchess.entities.Player;

import static fr.aboucorp.variantchess.app.utils.ArgsKey.GAME_RULES;

public class MatchmakingFragment extends VariantChessFragment implements MatchmakingListener {
    /**
     * Widgets
     */
    private ProgressBar progress_bar;
    private Chronometer matchmaking_chrono;
    private Button btn_cancel;
    private Button btn_retry;
    private ImageView img_warning;
    private TextView txt_status;
    /**
     * Nakama multiplayer session manager
     */
    private SessionManager sessionManager;
    /**
     * Nakama multiplayer session manager
     */
    private String matchmakingTicket;

    private GameRules gameRules;
    private ChessUser chessUser;
    private UserViewModel userViewModel;

    @Override
    protected void bindViews() {
        this.progress_bar = this.getView().findViewById(R.id.progress_bar);
        this.matchmaking_chrono = this.getView().findViewById(R.id.matchmaking_chrono);
        this.btn_cancel = this.getView().findViewById(R.id.btn_cancel);
        this.btn_retry = this.getView().findViewById(R.id.btn_retry);
        this.img_warning = this.getView().findViewById(R.id.img_warning);
        this.txt_status = this.getView().findViewById(R.id.txt_status);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.matchmaking_layout, container, false);
        if (savedInstanceState != null) {
            this.gameRules = (GameRules) savedInstanceState.getSerializable("game_rules");
        }
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (savedInstanceState != null) {
            this.gameRules = (GameRules) savedInstanceState.getSerializable("game_rules");
        }
        this.sessionManager = SessionManager.getInstance();
        this.sessionManager.setMatchmakingListener(this);
        this.bindViews();
        this.bindListeners();
        this.userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        this.userViewModel.getConnected().observe(this.getViewLifecycleOwner(), connected -> {
            chessUser = connected;
            this.launchMatchmaking();
        });
    }

    @Override
    protected void bindListeners() {
        this.btn_cancel.setOnClickListener(v -> cancelMatchMaking(matchmakingTicket));
        this.btn_retry.setOnClickListener(v -> launchMatchmaking());
    }

    @Override
    public void onMatchmakerMatched(MatchmakerMatched matched) {
        Log.i("fr.aboucorp.variantchess", String.format("Match found ! id : %s", matched.getMatchId()));
        matchmaking_chrono.stop();
        AsyncHandler asyncHandler = new AsyncHandler() {
            @Override
            protected Object executeAsync() throws Exception {
                List<User> users = sessionManager.getUsersFromMatched(matched);
                return users;
            }

            @Override
            protected void callbackOnUI(Object arg) {
                super.callbackOnUI(arg);
                List<User> users = (List<User>) arg;
                Player white = new Player(users.get(0).getUsername(), ChessColor.WHITE, users.get(0).getId());
                Player black = new Player(users.get(1).getUsername(), ChessColor.BLACK, users.get(1).getId());
                NavDirections action = MatchmakingFragmentDirections.actionMatchmakingFragmentToBoardFragment(true, matched.getMatchId(), chessUser, gameRules, white, black);
                Navigation.findNavController(getView()).navigate(action);
            }

            @Override
            protected void error(Exception ex) {
                super.error(ex);
                Log.e("fr.aboucorp.variantchess", String.format("Cannot retrieve players from matched"));
            }
        };
        asyncHandler.start();
    }

    private void launchMatchmaking() {
        this.matchmaking_chrono.start();
        this.btn_cancel.setVisibility(View.VISIBLE);
        this.btn_retry.setVisibility(View.GONE);
        this.img_warning.setVisibility(View.GONE);
        this.progress_bar.setVisibility(View.VISIBLE);
        this.txt_status.setText(R.string.matchmaking_in_progress);

        AsyncHandler asyncHandler = new AsyncHandler() {
            @Override
            protected Object executeAsync() throws Exception {
                matchmakingTicket = sessionManager.launchMatchMaking(gameRules.name, matchmakingTicket);
                return null;
            }

            @Override
            protected void error(Exception ex) {
                super.error(ex);
                Toast.makeText(getContext(), R.string.matchmaking_failed, Toast.LENGTH_LONG).show();
            }
        };
        asyncHandler.start();
    }

    @Override
    public void setArguments(@Nullable Bundle args) {
        super.setArguments(args);
        this.gameRules = (GameRules) args.getSerializable(GAME_RULES);
    }



    private void cancelMatchMaking(String ticket) {
        AsyncHandler handler = new AsyncHandler() {
            @Override
            protected Object executeAsync() {
                sessionManager.cancelMatchMaking(ticket);
                btn_cancel.setEnabled(false);
                return null;
            }

            @Override
            protected void callbackOnUI(Object arg) {
                Toast.makeText(getContext(), R.string.matchmaking_cancelled, Toast.LENGTH_LONG).show();
                matchmakingTicket = null;
                matchmaking_chrono.stop();
                btn_cancel.setVisibility(View.GONE);
                progress_bar.setVisibility(View.GONE);
                btn_retry.setVisibility(View.VISIBLE);
            }

            @Override
            protected void error(Exception ex) {
                super.error(ex);
                Toast.makeText(getContext(), R.string.matchmaking_cancelled, Toast.LENGTH_LONG).show();
                matchmakingTicket = null;
                matchmaking_chrono.stop();
                btn_cancel.setVisibility(View.GONE);
                progress_bar.setVisibility(View.GONE);
                btn_retry.setVisibility(View.VISIBLE);
            }
        };
        handler.start();
    }
}
