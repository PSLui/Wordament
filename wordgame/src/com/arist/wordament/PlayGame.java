package com.arist.wordament;

import com.arist.wordament.game.Game;
import com.arist.wordament.view.GameView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.ViewGroup;

public class PlayGame extends Activity implements Synchronizer.Finalizer {

	protected static final String TAG = "PlayGame";
	private Synchronizer synch;
	private Game game;
	private Menu menu;

    @Override
    public void onCreate(Bundle savedInstanceState) {
     	super.onCreate(savedInstanceState);
		Log.d(TAG,"onCreate");
		if(savedInstanceState != null) {
			Log.d(TAG,"restoring instance state");
			try {
				restoreGame(savedInstanceState);
			} catch (Exception e) {
				Log.e(TAG,"error restoring state",e);
			}
			return;
		}
		try {
			String action = getIntent().getAction();
			if(action.equals("com.arist.wordament.action.RESTORE_GAME")) {
				Log.d(TAG,"restoring game");
				restoreGame();
			} else if(action.equals("com.arist.wordament.action.NEW_GAME")) {
				Log.d(TAG,"starting new game");
				newGame();
			} else {
				Log.d(TAG,"Whoa there, friend!");
			}
		} catch (Exception e) {
			Log.e(TAG,"top level",e);
		}
    }

	@Override
	public boolean onCreateOptionsMenu(Menu m) {
		// Log.d(TAG,"onCreateOptionsMenu");
		
		menu = m;

		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.game_menu,menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Log.d(TAG,"onOptionsItemSelected");
		
		switch(item.getItemId()) {
			case R.id.save_game:
				synch.abort();
				saveGame();
				finish();
			break;
			case R.id.end_game:
				game.endNow();
		}
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		return game.getStatus() == Game.GameStatus.GAME_RUNNING;
	}

	private void newGame() {
		game = new Game(this);
		Log.d(TAG,"created game");

		GameView lv = new GameView(this,game);
		Log.d(TAG,"created view="+lv);

		if(synch != null) {
			synch.abort();
		}
		synch = new Synchronizer();
		synch.setCounter(game);
		synch.addEvent(lv);
		synch.setFinalizer(this);

		ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(
			ViewGroup.LayoutParams.FILL_PARENT,
			ViewGroup.LayoutParams.FILL_PARENT);
		setContentView(lv,lp);
		lv.setKeepScreenOn(true);

		// Log.d(TAG,"set view");
		// Log.d(TAG,"newGame ends");
	}
	
	private void restoreGame() {
		Resources res = getResources();
		SharedPreferences prefs = getSharedPreferences("prefs_game_file",
			Context.MODE_PRIVATE);

		clearSavedGame();

		game = new Game(this,prefs);

		restoreGame(game);
	}

	private void restoreGame(Bundle bun) {
		game = new Game(this,bun);

		restoreGame(game);
	}

	private void restoreGame(Game game) {

		Log.d(TAG,"restored game");

		GameView lv = new GameView(this,game);
		Log.d(TAG,"created view="+lv);

		if(synch != null) {
			synch.abort();
		}
		synch = new Synchronizer();
		synch.setCounter(game);
		synch.addEvent(lv);
		synch.setFinalizer(this);

		ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(
			ViewGroup.LayoutParams.FILL_PARENT,
			ViewGroup.LayoutParams.FILL_PARENT);
		setContentView(lv,lp);
		lv.setKeepScreenOn(true);
		lv.setFocusableInTouchMode(true);
	}

	private void saveGame() {
		if(game.getStatus() == Game.GameStatus.GAME_RUNNING) {
			// Log.d(TAG,"Saving");
			SharedPreferences prefs = getSharedPreferences(
				 "prefs_game_file",Context.MODE_PRIVATE);
			game.pause();
			game.save(prefs.edit());
		}
	}

	private void saveGame(Bundle state) {
		if(game.getStatus() == Game.GameStatus.GAME_RUNNING) {
			// Log.d(TAG,"Saving");
			game.pause();
			game.save(state);
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		Log.d(TAG,"Pausing");
		synch.abort();
		saveGame();
	}

	@Override
	public void onResume() {
		super.onResume();
		Log.d(TAG,"onResume:"+game+","+synch);
		if(game == null) newGame();

		switch(game.getStatus()) {
			case GAME_STARTING:
				Log.d(TAG,"onResume: GAME_STARTING");
				game.start();
				synch.start();
			break;
			case GAME_PAUSED:
				Log.d(TAG,"onResume: GAME_PAUSED");
				game.unpause();
				synch.start();
			break;
			case GAME_FINISHED:
				Log.d(TAG,"onResume: GAME_FINISHED");
				score();
			break;
		}
		Log.d(TAG,"onResume finished");
	}

	@Override
	public void doFinalEvent() {
		score();
	}

	private void clearSavedGame() {
		SharedPreferences prefs = getSharedPreferences("prefs_game_file",
			Context.MODE_PRIVATE);

		SharedPreferences.Editor editor = prefs.edit();
		editor.putBoolean("activeGame",false);
		editor.commit();

	}

	private void score() {
		// Log.d(TAG,"Finishing");

		synch.abort();
		clearSavedGame();

		Bundle bun = new Bundle();
		game.save(bun);

		Intent scoreIntent = new Intent("com.arist.wordament.action.SCORE");
		scoreIntent.putExtras(bun);

		startActivity(scoreIntent);

		finish();
	}

	@Override
	public void onStop() {
		super.onStop();
		// Log.d(TAG,"onStop()");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		// Log.d(TAG,"onDestroy()"+isFinishing());
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		Log.d(TAG,"onSaveInstanceState");
		saveGame(outState);
	}

}
