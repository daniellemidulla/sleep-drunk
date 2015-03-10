package edu.dartmouth.cs.SleepDrunk;

import java.text.DecimalFormat;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import edu.dartmouth.cs.SleepDrunk.ReactHighScoreDatabase.HighScoreEntry;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;

import java.util.Calendar;
import java.util.List;
import org.json.JSONObject;
import org.json.JSONArray;
import android.os.AsyncTask;
import java.util.HashMap;
import java.util.Map;

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
    private Context context;
    private static int personalBaseline = 0;



    public static int lastUpdatedTimestamp = Calendar.getInstance().get(Calendar.SECOND);
    public static double sleeptime = 0;

    public   ReactHighScoreDatabase db = ReactHighScoreDatabase.getDatabase(getContext());

    public  HighScoreEntry scoreEntry = db.new HighScoreEntry();

	public ReactView(Context context, AttributeSet attrs) {
		super(context, attrs);
        this.context = context;

		setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				stateChange();
			}
		});

		textPaint.setColor(Color.BLACK);
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

		textPaint.setTextSize(textSize*3);
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
		//restart();
        state = STATE_WAITING;
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
        int rxnTime = 0;
        //get reaction time deterioration
        System.out.println("==============personalBaseline BEFORE: " + personalBaseline);
        //System.out.println("==============avgTime: " + avgTime);
        //System.out.println("==============baseline: "+ BASELINE);
        //System.out.println("==============sleeptime: " + ReactGameActivity.sleeptime);
		if (personalBaseline == 0) { //if there is no personal baseline
            rxnTime = avgTime - BASELINE; //use the hard-coded baseline
            //if they reported more than 7 hours of sleep or the current score is better than the baseline
            if ((ReactGameActivity.sleeptime >= 7) || (avgTime <= BASELINE) ){
                personalBaseline = avgTime; //set the score as the new personal baseline
            }
        }
        else{ //if there is a personal baseline
            rxnTime = avgTime - personalBaseline; //use the personal baseline
            if (avgTime < personalBaseline){ //if the score is faster than the personal baseline
                personalBaseline = avgTime; //reset the personal baseline
            }
        }
        System.out.println("==============personalBaseline AFTER: " + personalBaseline);
        //System.out.println("rxnTime outside: " + rxnTime);

        //NumberFormat formatter = new DecimalFormat("#0.00");
		double bac = reactionTimeToBAC(rxnTime);
        //System.out.println("bac: " + bac);
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
				.getString("weight_preference", "0"));

        //System.out.println("weight: " + weight);
        //System.out.println("gender: " + gender);
		int drinks = BACToDrinks(bac, weight, gender);
        //System.out.println("drinks: " + drinks);

		scoreEntry.setName(name);
		scoreEntry.setScore(avgTime);
		scoreEntry.setBac(bac);
		scoreEntry.setDrinks(drinks);
        scoreEntry.setSleeptime(ReactGameActivity.sleeptime);
        db.addEntry(scoreEntry);


        Intent intent = new Intent();
        StartFragment.entry = db.new HighScoreEntry();
        StartFragment.entry.setName(name);
        StartFragment.entry.setScore(avgTime);
        StartFragment.entry.setBac(bac);
        StartFragment.entry.setDrinks(drinks);
        StartFragment.entry.setSleeptime(sleeptime);


        List<HighScoreEntry>entries = db.getAllEntries();
        JSONArray array = new JSONArray();
        JSONObject jsonData = new JSONObject();



        try{

            jsonData.put(MainActivity2.PROPERTY_REG_ID, MainActivity2.regid);


            for(HighScoreEntry entry:entries){
                JSONObject obj = new JSONObject();
                //obj.put(MainActivity.PROPERTY_REG_ID, MainActivity.regid);
                obj.put("id", entry.getId());
                obj.put("name", entry.getName());
                obj.put("score", entry.getScore());
                obj.put("bac", entry.getBac());
                obj.put("drinks", entry.getDrinks());
                obj.put("sleeptime", entry.getSleeptime());


                array.put(obj);
            }
            jsonData.put("array", array);
        }catch(Exception e){
            e.printStackTrace();
        }

        String out = jsonData.toString();
        syncExercise(out);




        intent.setClass(getContext(), MainActivity2.class);

        getContext().startActivity(intent);

			/*alert.setMessage("Name: "+name+"\n"+
					"AvgTime: " + avgTime  + " ms.\n"+
					"BAC: " + bacStr  + " \n"+
					"Drinks: " + drinks  + " \n"
					);
			alert.setCancelable(true);
			alert.setPositiveButton("Save", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {					

					
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
		*/
	}


private void syncExercise(String data) {
        new AsyncTask<String, Void, String>() {

@Override
protected String doInBackground(String... arg0) {
        String url = getResources().getString(R.string.server_addr) + "/sync.do";
        String res = "";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("data", arg0[0]);
        try {
        res = ServerUtilities.post(url, params,"application/json");
        } catch (Exception ex) {
        ex.printStackTrace();
        }

        return res;
        }

@Override
protected void onPostExecute(String res) {
        //mPostText.setText("");
        //refreshPostHistory();
        }

        }.execute(data);

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

			canvas.drawColor(Color.parseColor(getResources().getString(R.string.lightblue)));
            textPaint.setTextSize(72);
			drawCenteredText(canvas, "Touch the screen","as quickly as possible","when it turns green.","","Touch screen to START!");
			break;
		case STATE_WAITING:
			canvas.drawColor(Color.parseColor(getResources().getString(R.string.lightblue)));
			String lastString = (clicks == 0) ? "" : "Last: " + lastTime + " ms";
			drawCenteredText(canvas, "Reactions: " + clicks + "/" + NUMBER_OF_CLICKS, "", "Average: "
					+ (clicks == 0 ? 0 : totalTime / clicks) + " ms", "", lastString);
			break;
		case STATE_RED:
			canvas.drawColor(Color.parseColor(getResources().getString(R.string.lightred)));
			drawCenteredText(canvas, "Tap!");
			if (startTime == -1) {
				startTime = System.currentTimeMillis();
			}
			break;
		case STATE_AFTER_CHEAT:
			canvas.drawColor(Color.parseColor(getResources().getString(R.string.lightblue)));
			drawCenteredText(canvas, "Head start!", "", "Touch to continue.");
			break;

		}
	}

	/**
	 * Calculates the average equivalent blood alcohol concentration (BAC) based on the given reaction time deterioration
	 * @param rxnTimeDiff The difference in reaction times between the current score and the baseline in milliseconds
	 * @return Returns a BAC value in % (g/100mL) (the standard unit to represent BAC ) with a lower bound at 0
	 */
	public double reactionTimeToBAC( double rxnTimeDiff ){
        if ( rxnTimeDiff < 2.4 ) return 0; //ensures that the BAC won't be negative
		double calculatedBAC = 0.0269 * Math.log(rxnTimeDiff) - 0.0225;
		return calculatedBAC;
	}
	
	/**
	 * Calculates the average equivalent number of drinks for the input BAC and a person of the given gender and weight
	 * @param calculatedBAC The BAC value calculated in % (g/100ml)
	 * @param weight The user's weight in kilograms
	 * @param gender The user's gender as an integer - 0 for not set, 1 for male, 2 for female
	 * @return Returns the number of drinks ingested based on the given BAC, weight, and gender with a lower limit at 0
	 */
	public int BACToDrinks( double calculatedBAC, double weight, int gender){
        if (calculatedBAC == 0) return 0;
		double bodyWater = getBodyWaterConstant(gender);
		double drinks = ((calculatedBAC + METABOLISM_CONSTANT)*bodyWater*weight)/(WATER_IN_BLOOD * CONVERSION_FACTOR);
		//System.out.println("drinks as a double: " + drinks);
        int numberOfDrinks = (int) drinks;
        if (numberOfDrinks < 1) return 0;
		return numberOfDrinks;
	}
	
	/**
	 * 
	 * @param gender The user's gender as an integer - 0 for not set, 1 for male, 2 for female
	 * @return Returns the body water constant associated with the gender. If the given gender is not set(0), then the returned constant is for men.
	 */
	public double getBodyWaterConstant(int gender){
		double bodyWater = 0;
		if (gender == 2) { //if they are a woman
			bodyWater = 0.49;
		}
		else {
			bodyWater = 0.58;
		}
		return bodyWater;
	}

//    public static int getUserBaseline(){
//        if (personalBaseline == 0) return BASELINE;
//        else return personalBaseline;
//    }
}
