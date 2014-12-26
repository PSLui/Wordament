package com.arist.wordament;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Wordament extends Activity {

	protected static final String TAG = "Wordament";

	private static final int DIALOG_NO_SAVED = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
		try {
       		super.onCreate(savedInstanceState);
			splashScreen();
		} catch (Exception e) {
			// Log.e(TAG,"top level",e);
		}
    }

	private void splashScreen() {
		setContentView(R.layout.splash);

		Button b = (Button) findViewById(R.id.new_game);
		b.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent("com.arist.wordament.action.NEW_GAME"));
			}
		});

		if(savedGame()) {
			b = (Button) findViewById(R.id.restore_game);
			b.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if(savedGame()) {
						// Log.d(TAG,"restoring game");
						startActivity(new 
							Intent("com.arist.wordament.action.RESTORE_GAME"));
					} else {
						// Log.d(TAG,"no saved game :(");
						showDialog(DIALOG_NO_SAVED);
					}
				}
			});
			b.setEnabled(true);
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		// Log.d(TAG,"Pausing");
	}

	@Override
	public void onResume() {
		super.onResume();
		splashScreen();
	}

	public boolean savedGame() {
		Resources res = getResources();
		SharedPreferences prefs = getSharedPreferences("prefs_game_file",
			MODE_PRIVATE);

		return prefs.getBoolean("activeGame",false);
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch(id) {
			case DIALOG_NO_SAVED:
				return new AlertDialog.Builder(this)
					.setTitle(getResources().
						getString(R.string.dialog_no_saved))
					.setPositiveButton(R.string.dialog_ok, 
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, 
								int whichButton) {
									// do nothing.
								}
						})
					.create();
		}
		return null;
	}

}
