package edu.dartmouth.cs.SleepDrunk;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import edu.dartmouth.cs.SleepDrunk.ReactHighScoreDatabase.HighScoreEntry;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.preference.PreferenceManager;
import android.text.InputFilter;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

public class ReactView extends View {

	private static final int STATE_START = 1;
	private static final int STATE_WAITING = 2;
	private static final int STATE_RED = 3;
	private static final int STATE_AFTER_CHEAT = 4;

	public static final int NUMBER_OF_CLICKS = 2;
	
	public static final int BASELINE = 290;
	static double METABOLISM_CONSTANT = 0.017; //standard metabolism constant - used in to BAC to alcohol calculations
	static double WATER_IN_BLOOD = 0.806; //percentage of water in the blood - used in the BAC to alcohol calculations
	static double CONVERSION_FACTOR = 1.2; //conversion factor needed in the BAC to alcohol calculations

	private int state = STATE_START;
	private int clicks;
	private int totalTime;
	private long startTime = -1;
	private long lastTime = -1;
	private final Paint textPaint = new Paint();
	private Timer timer = new Timer(true);
	private final Random random = new Random();
	private long lastClick;

	public ReactView(Context context, AttributeSet attrs) {
		super(context, attrs);

		setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				stateChange();
			}
		});

		textPaint.setColor(Color.WHITE);
		textPaint.setAntiAlias(true);
		textPaint.setTextAlign(Align.CENTER);

		DisplayMetrics metrics = new DisplayMetrics();
		((Activity) getContext()).getWindowManager().getDefaultDisplay().getMetrics(metrics);
		int smallest = Math.min(metrics.widthPixels, metrics.heightPixels);
		int textSize;
		if (smallest <= 300) {
			textSize = 18;
		} else if (smallest <= 400) {
			textSize = 24;
		} else {
			textSize = 30;
		}

		textPaint.setTextSize(textSize*2);
		begin();
	}

	private void startTurnRedTimer() {
		long delay = 1000 + random.nextInt(2000);
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				post(new Runnable() {
					@Override
					public void run() {
						state = STATE_RED;
						ReactSoundManager.playCorrect();
						invalidate();
						startTime = System.currentTimeMillis();
					}
				});
			}
		}, delay);
	}

	private void begin() {
		clicks = totalTime = 0;
		startTime = -1;
	}

	private void stateChange() {
		switch (state) {
		case STATE_START:
			lastClick = System.currentTimeMillis();
			begin();
			startTurnRedTimer();
			state = STATE_WAITING;
			break;
		case STATE_WAITING:
			long timeSinceLast = System.currentTimeMillis() - lastClick;
			if (timeSinceLast < 800) {
				// a quick click - ignore
				return;
			}
			timer.cancel();
			timer = new Timer(true);
			ReactSoundManager.playIncorrect();
			state = STATE_AFTER_CHEAT;
			break;
		case STATE_RED:
			lastClick = System.currentTimeMillis();
			lastTime = lastClick - startTime;
			totalTime += lastTime;
			clicks++;
			startTime = -1;
			if (clicks == NUMBER_OF_CLICKS) {
				ReactSoundManager.playGameDone();
				gameOver();
			} else {
				startTurnRedTimer();
				state = STATE_WAITING;
			}
			break;
		case STATE_AFTER_CHEAT:
			restart();
			return;
		}
		invalidate();
	}

	void restart() {
		state = STATE_START;
		clicks = 0;
		totalTime = 0;
		timer.cancel();
		timer = new Timer();
		invalidate();
	}

	private void gameOver() {
		final int avgTime = totalTime / NUMBER_OF_CLICKS;
		restart();

		final ReactHighScoreDatabase db = ReactHighScoreDatabase.getDatabase(getContext());
		//int position = db.getPositionForScore(totalTimeCopy);

		final String LAST_NAME_KEY = "last_name";
		SharedPreferences prefs = ((Activity) getContext()).getPreferences(Context.MODE_PRIVATE);
		String initialName = prefs.getString(LAST_NAME_KEY, "");

		AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
		alert.setCancelable(false);
	/*	if (position >= ReactHighScoreDatabase.MAX_ENTRIES) {
			alert.setMessage("AvgTime: " + avgTime + " ms.\n\nYou did not attain high score!");
			alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					getContext().startActivity(new Intent(getContext(), ReactGameActivity.class));
				}
			});
		} else {*/
		
		int rxnTime = avgTime - BASELINE;
		//NumberFormat formatter = new DecimalFormat("#0.00");
		double bac = reactionTimeToBAC(rxnTime);
		//bac=((int)(bac*100))/100; 
		String bacStr = new DecimalFormat("#.##").format(bac);
		
		String name = PreferenceManager.getDefaultSharedPreferences(getContext())
				.getString("name_preference", "Sarah");
		
		int gender = 1;
		String genderStr = PreferenceManager.getDefaultSharedPreferences(getContext())
				.getString("gender_list_preference", "Male");
		if (genderStr.equalsIgnoreCase("female")) {
			gender = 2;
		}
		
		int weight = Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(getContext())
				.getString("weight_preference", "50"));
		
		
		int drinks = BACToDrinks(bac, weight, gender);
		final HighScoreEntry scoreEntry = db.new HighScoreEntry();
		scoreEntry.setName(name);
		scoreEntry.setScore(avgTime);
		scoreEntry.setBac(bac);
		scoreEntry.setDrinks(drinks);
		
			alert.setMessage("Name: "+name+"\n"+
					"AvgTime: " + avgTime  + " ms.\n"+
					"BAC: " + bacStr  + " \n"+
					"Drinks: " + drinks  + " \n"
					);
			alert.setCancelable(true);
			alert.setPositiveButton("Save", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {					
					db.addEntry(scoreEntry);
					
					Intent intent = new Intent();
					intent.setClass(getContext(), MainActivity2.class);
					
					getContext().startActivity(intent);
				}
			}).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {					
					
					Intent intent = new Intent();
					intent.setClass(getContext(), MainActivity2.class);
					getContext().startActivity(intent);
				}
			});
		

		alert.show();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() != MotionEvent.ACTION_DOWN)
			return false;
		stateChange();
		return true;
	}

	private void drawCenteredText(Canvas canvas, String... text) {
		int linePadding = 2;
		int totalHeight = text.length * (int) textPaint.getTextSize() + linePadding;
		int startY = getHeight() / 2 - totalHeight / 2;
		int x = getWidth() / 2;
		for (int i = 0; i < text.length; i++) {
			String s = text[i];
			int y = startY + i * ((int) textPaint.getTextSize());
			canvas.drawText(s, x, y, textPaint);
		}

	}

	@Override
	public void draw(Canvas canvas) {
		switch (state) {
		case STATE_START:
			canvas.drawColor(Color.BLACK);
			drawCenteredText(canvas, "Touch the screen", "as quick as possible",
					"when the screen turns red.", "", "Touch screen to START!");
			break;
		case STATE_WAITING:
			canvas.drawColor(Color.BLACK);
			String lastString = (clicks == 0) ? "" : "Last: " + lastTime + " ms";
			drawCenteredText(canvas, "Reactions: " + clicks + "/" + NUMBER_OF_CLICKS, "", "Average: "
					+ (clicks == 0 ? 0 : totalTime / clicks) + " ms", "", lastString);
			break;
		case STATE_RED:
			canvas.drawColor(Color.RED);
			drawCenteredText(canvas, "React!");
			if (startTime == -1) {
				startTime = System.currentTimeMillis();
			}
			break;
		case STATE_AFTER_CHEAT:
			canvas.drawColor(Color.BLACK);
			drawCenteredText(canvas, "Head start!", "", "Touch to continue.");
			break;

		}
	}

	/**
	 * 
	 * @param rxnTimeDiff The difference in reaction times between the current score and the baseline in milliseconds
	 * @return Returns a BAC value in % (g/100mL) (the standard BAC value) 
	 */
	public double reactionTimeToBAC( double rxnTimeDiff ){
		double calculatedBAC;
		calculatedBAC = 0.0269 * Math.log(rxnTimeDiff) - 0.0225;
		return calculatedBAC;
	}
	
	/**
	 * 
	 * @param calculatedBAC The BAC value calculated in % (g/100ml)
	 * @param weight The user's weight in kilograms
	 * @param gender The user's gender as an integer - 0 for not set, 1 for male, 2 for female
	 * @return Returns the number of drinks ingested based on the given BAC, weight, and gender
	 */
	public int BACToDrinks( double calculatedBAC, double weight, int gender){
		int numberOfDrinks = 0;
		double bodyWater = getBodyWaterConstant(gender);
		double drinks = ((calculatedBAC + METABOLISM_CONSTANT)*bodyWater*weight)/(WATER_IN_BLOOD * CONVERSION_FACTOR);
		numberOfDrinks = (int) drinks;
		return numberOfDrinks;
	}
	
	/**
	 * 
	 * @param gender The user's gender as an integer - 0 for not set, 1 for male, 2 for female
	 * @return Returns the body water constant associated with the gender. If the given gender is not set(0), then the returned constant is for men.
	 */
	public double getBodyWaterConstant(int gender){
		double bodyWater;
		if (gender == 2) { //if they are a woman
			bodyWater = 0.49;
		}
		else {
			bodyWater = 0.58;
		}
		return bodyWater;
	}
}
