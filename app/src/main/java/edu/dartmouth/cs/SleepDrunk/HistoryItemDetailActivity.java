package edu.dartmouth.cs.SleepDrunk;

import java.text.DecimalFormat;

import edu.dartmouth.cs.SleepDrunk.ReactHighScoreDatabase.HighScoreEntry;
import edu.dartmouth.cs.SleepDrunk.R;



import android.app.Activity;

import android.content.Intent;
import android.os.Bundle;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;

public class HistoryItemDetailActivity extends Activity {

	//private EntriesDataSource datasource;
	//private Entry entry;
	private HighScoreEntry entry;
	private long _id;
	private EditText name;
	private EditText score;
	private EditText bac;
	private EditText drinks;
	
	
	private ReactHighScoreDatabase db;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.history_item_detail);
		Intent intent = getIntent();

		_id = intent.getLongExtra("_id", 5);
		

		//datasource = EntriesDataSource.getInstance(this);
		
		db = ReactHighScoreDatabase.getDatabase(this);
		entry = db.getEntry((int)_id);
		
		
		name = (EditText) findViewById(R.id.name);
		name.setText(entry.getName());

		score = (EditText) findViewById(R.id.score);
		score.setText(String.valueOf(entry.getScore()));
		
		bac = (EditText) findViewById(R.id.bac);
		bac.setText(new DecimalFormat("#.##").format(entry.getBac()));
		
		drinks = (EditText) findViewById(R.id.drinks);
		drinks.setText(String.valueOf(entry.getDrinks()));
		
		
 
		//entry = datasource.getEntry(_id);

		/*activityType = (EditText) findViewById(R.id.activityType);
		activityType.setText(entry.getActivityType());

		dateTime = (EditText) findViewById(R.id.dateTime);
		dateTime.setText(entry.getDateTime());

		duration = (EditText) findViewById(R.id.duration);
		String str = "";
		double dur = entry.getDuration();
		if ((int) dur > 0) {
			str = String.valueOf((int) dur) + "mins ";
		}
		str += String.valueOf(new DecimalFormat("#.##").format((dur - Math
				.floor(dur)) * 60.0)) + " secs";
		duration.setText(str);

		distance = (EditText) findViewById(R.id.distance);

		String unit = PreferenceManager.getDefaultSharedPreferences(this)
				.getString("unit_list_preference", "Miles");
		if (unit.equalsIgnoreCase("Miles")) {
			distance.setText(String.valueOf(new DecimalFormat("#.##")
					.format(entry.getDistance()) + " " + unit));
		} else {
			distance.setText(String.valueOf(new DecimalFormat("#.##")
					.format(entry.getDistance() * 1.60934)) + " " + unit);
		}

		calories = (EditText) findViewById(R.id.calories);
		calories.setText(String.valueOf(entry.getCalories()));

		heartrate = (EditText) findViewById(R.id.heartrate);
		heartrate.setText(String.valueOf(entry.getHeartrate()));

		comment = (EditText) findViewById(R.id.comment);
		comment.setText(entry.getComment());*/

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_delete:
			//datasource.deleteEntry(entry);
			db.deleteEntry(entry);
			finish();
			break;

		}
		return true;
	}
}
