package edu.dartmouth.cs.SleepDrunk;

import org.json.JSONObject;
import org.json.JSONArray;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.List;



import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;
import android.widget.Button;
import android.widget.ImageButton;

import edu.dartmouth.cs.SleepDrunk.R;
import edu.dartmouth.cs.SleepDrunk.ReactHighScoreDatabase.HighScoreEntry;

public class StartFragment extends Fragment {
	private Context mContext;
	private Spinner inputType;
	private Spinner activityType;
	private ImageButton btnStart;
	//private Button btnSync;
	private Intent intent;
	private ReactHighScoreDatabase db;
	private HighScoreEntry entry;

	

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View view = inflater.inflate(R.layout.startfragment, container, false);
		mContext = getActivity();
		//activityType = (Spinner) view.findViewById(R.id.spinnerActivityType);
		//inputType = (Spinner) view.findViewById(R.id.spinnerInputType);

		btnStart = (ImageButton) view.findViewById(R.id.btnStart);
		//btnSync = (Button) view.findViewById(R.id.btnSync);
		db =  ReactHighScoreDatabase.getDatabase(getActivity());

		
		
		
		intent = new Intent();
		
		btnStart.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				//int index = inputType.getSelectedItemPosition();
				//intent.putExtra("activityType", activityType.getSelectedItem().toString());	
				//intent.putExtra("inputType", inputType.getSelectedItem().toString());	
				//entry.setInputType(inputType.getSelectedItem().toString());
				/*if(index == 0) {
					intent.setClass(mContext, ManualInputActivity.class);					
				}
				else{
					intent.setClass(mContext, MapDisplayActivity.class);
					intent.putExtra("displayType", "new");	
					intent.putExtra("inputType", inputType.getSelectedItem().toString());
				}*/
				intent.setClass(mContext, ReactGameActivity.class);
				startActivity(intent);

			}
		});
		/*btnSync.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
											
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

			}
		});*/
	   return view;	
	}
	
	
	
	private void syncExercise(String data) {
		new AsyncTask<String, Void, String>() {

			@Override
			protected String doInBackground(String... arg0) {
				String url = getString(R.string.server_addr) + "/sync.do";
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
				
			

	
	

}
