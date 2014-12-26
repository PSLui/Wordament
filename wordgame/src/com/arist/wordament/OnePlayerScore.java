package com.arist.wordament;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TabHost;
import android.widget.TextView;

import java.util.regex.Pattern;
import java.util.Iterator;
import java.util.Set;

import com.arist.trie.Trie;
import com.arist.wordament.game.Game;
import com.arist.wordament.view.BoardView;

@SuppressWarnings("deprecation")
public class OnePlayerScore extends TabActivity {

	private static final String TAG = "OnePlayerScore";

	public static final Pattern DEFINE_PAT = Pattern.compile("\\w+");
	public static final String DEFINE_URL = 
		"http://www.google.com/search?q=define%3a+";

	private Game game;
	private BoardView bv;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
     	super.onCreate(savedInstanceState);

		if(savedInstanceState != null) {
			game = new Game(this, savedInstanceState);

		} else {
			Intent intent = getIntent();
			Bundle bun = intent.getExtras();
			game = new Game(this,bun);
		}
		game.initializeDictionary();

		// Set up the tabs
		TabHost host = getTabHost();
		LayoutInflater.from(this).inflate(R.layout.score_view,
			host.getTabContentView(), true);
		host.addTab(host.newTabSpec("found").setIndicator("Found Words").
			setContent(R.id.found_words));
		host.addTab(host.newTabSpec("missed").setIndicator("Missed Words").
			setContent(R.id.missed_words));

		bv = (BoardView) findViewById(R.id.missed_board);
		bv.setBoard(game.getBoard());

		Set<String> possible = game.getSolutions().keySet();

		ViewGroup foundVG = initializeScrollView(R.id.found_scroll);
		ViewGroup missedVG = initializeScrollView(R.id.missed_scroll);

		int score = 0;
		int max_score = 0;
		int words = 0;
		int max_words = possible.size();

		Iterator<String> li = game.uniqueListIterator();
		while(li.hasNext()) {
			String w = li.next();

			if(game.isWord(w) && game.WORD_POINTS[w.length()] > 0) {
				int points = game.WORD_POINTS[w.length()];
				addWord(foundVG,w,points,0xff000000);
				score += game.WORD_POINTS[w.length()];
				words++;
			} else {
				addWord(foundVG,w,0,0xffff0000);
			}

			possible.remove(w);
		}
	
		max_score = score;
		li = possible.iterator();

		while(li.hasNext()) {
			String w = li.next();
			max_score += game.WORD_POINTS[w.length()];
			addMissedWord(missedVG,game.getSolutions().get(w));
		}

		TextView t = (TextView) findViewById(R.id.score_points);
		t.setText(""+score+"/"+max_score);

		t = (TextView) findViewById(R.id.score_words);
		t.setText(""+words+"/"+max_words);

		Button b = (Button) findViewById(R.id.close_score);
		b.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});

		b = (Button) findViewById(R.id.missed_close_score);
		b.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});

	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		game.save(outState);
	}

	private ViewGroup initializeScrollView(int resId) {
		ScrollView sv = (ScrollView) findViewById(resId);
		sv.setScrollBarStyle(sv.SCROLLBARS_OUTSIDE_INSET);

		ViewGroup.LayoutParams llLp = new ViewGroup.LayoutParams(
			ViewGroup.LayoutParams.FILL_PARENT,
			ViewGroup.LayoutParams.WRAP_CONTENT);

		LinearLayout ll = new LinearLayout(this);
		ll.setLayoutParams(llLp);
		ll.setOrientation(LinearLayout.VERTICAL);
		sv.addView(ll);

		return ll;
	}

	private void addWord(ViewGroup vg, String w, int points, int color) {
		LinearLayout.LayoutParams text1Lp = new LinearLayout.LayoutParams(
			ViewGroup.LayoutParams.WRAP_CONTENT,
			ViewGroup.LayoutParams.WRAP_CONTENT);
		TextView tv1 = new TextView(this);
		tv1.setGravity(Gravity.LEFT);
		tv1.setLayoutParams(text1Lp);
		tv1.setTextSize(16);
		tv1.setTextColor(color);
		tv1.setText(w);

		LinearLayout.LayoutParams text2Lp = new LinearLayout.LayoutParams(
			ViewGroup.LayoutParams.WRAP_CONTENT,
			ViewGroup.LayoutParams.WRAP_CONTENT,
			(float) 1.0);
		TextView tv2 = new TextView(this);
		tv2.setGravity(Gravity.RIGHT);
		tv2.setLayoutParams(text2Lp);
		tv2.setTextSize(16);
		tv2.setTextColor(color);
		tv2.setText(""+points+" ");

		LinearLayout ll = new LinearLayout(this);
		ll.setOrientation(LinearLayout.HORIZONTAL);

		ll.addView(tv1);
		ll.addView(tv2);
			
		vg.addView(ll, new LinearLayout.LayoutParams(
			ViewGroup.LayoutParams.FILL_PARENT,
			ViewGroup.LayoutParams.WRAP_CONTENT));

	}

	private void addMissedWord(ViewGroup vg, Trie.Solution solution) {
		String w = solution.getWord();

		ViewGroup.LayoutParams text1Lp = new LinearLayout.LayoutParams(
			ViewGroup.LayoutParams.WRAP_CONTENT,
			ViewGroup.LayoutParams.WRAP_CONTENT);
		TextView tv1 = new TextView(this);
		tv1.setGravity(Gravity.LEFT);
		tv1.setLayoutParams(text1Lp);
		tv1.setTextSize(16);
		tv1.setTextColor(0xffffff00);
		tv1.setText(w);
		
		ViewGroup.LayoutParams text2Lp = new LinearLayout.LayoutParams(
			ViewGroup.LayoutParams.WRAP_CONTENT,
			ViewGroup.LayoutParams.WRAP_CONTENT,
			(float) 1.0);

		LinearLayout ll = new LinearLayout(this);
		ll.setOrientation(LinearLayout.HORIZONTAL);

		ll.addView(tv1);

			
		vg.addView(ll, new LinearLayout.LayoutParams(
			ViewGroup.LayoutParams.FILL_PARENT,
			ViewGroup.LayoutParams.WRAP_CONTENT));
	}

}

