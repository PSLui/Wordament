package com.arist.wordament;

import android.os.Handler;
import android.util.Log;
import java.util.LinkedList;
import java.util.ListIterator;

public class Synchronizer implements Runnable {
	private static String TAG = "Synchronizer";

	public static final int TICK_FREQ = 10;

	public interface Counter {
		public int tick();
	}

	public interface Event {
		public void tick(int i);
	}

	public interface Finalizer {
		public void doFinalEvent();
	}

	private Counter mainCounter;
	private Finalizer mainFinalizer;
	private LinkedList<Event> events;
	private boolean done;
	private Handler handler;

	public Synchronizer() {
		mainCounter = null;
		mainFinalizer = null;
		events = new LinkedList();
		done = false;

		handler = new Handler();
	}

	public void setCounter(Counter c) {
		mainCounter = c;
	}

	public void setFinalizer(Finalizer f) {
		mainFinalizer = f;
	}

	public void addEvent(Event e) {
		events.add(e);
	}

	public void start() {
		// Log.d(TAG,"calling start()");
		done = false;
		handler.postDelayed(this,TICK_FREQ);
	}

	public void run() {
		if(done) return;
		int time = mainCounter.tick();
		
		ListIterator<Event> iter = events.listIterator();

		while(iter.hasNext()) {
			Event e = iter.next();
			e.tick(time);
		}

		if (time <= 0) {
			if(mainFinalizer != null) {
				mainFinalizer.doFinalEvent();
			}
		}

		handler.postDelayed(this,TICK_FREQ);
	}

	public void abort() {
		// Log.d(TAG,"abort() has been called");
		if(done) return; // bail if abort has already been called
		done = true;
	}

}


