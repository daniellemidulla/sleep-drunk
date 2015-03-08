package edu.dartmouth.cs.SleepDrunk;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.app.ListFragment;
import android.widget.TextView;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import edu.dartmouth.cs.SleepDrunk.ReactHighScoreDatabase.HighScoreEntry;




public class HistoryFragment extends ListFragment {
	
	private ReactHighScoreDatabase db ;
	private IntentFilter mMessageIntentFilter;
	private BroadcastReceiver mMessageUpdateReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			long id = intent.getLongExtra("id",-1);
			String regid_rec = intent.getStringExtra("regid");
			if (id != -1 && regid_rec!=null && regid_rec.equals(MainActivity2.regid)) {
				try{
					Thread.sleep(100);
				}catch(Exception e){
					e.printStackTrace();
				}
				
				refresh();
			}
		}
	};
	


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		
		View view = inflater.inflate(R.layout.historyfragment, container, false);
		mMessageIntentFilter = new IntentFilter();
		mMessageIntentFilter.addAction("GCM_NOTIFY");
		db= ReactHighScoreDatabase.getDatabase(getActivity());
		return view;
	}

	public void refresh(){
		ReactHighScoreDatabase db = ReactHighScoreDatabase.getDatabase(getActivity());
		Cursor cursor = db.createCursor(new String[] {
				"_id","name","score","bac","drinks" }, null);
		
		ActivityEntriesCursorAdapter mAdapter = new ActivityEntriesCursorAdapter(
				getActivity(), cursor);
		setListAdapter(mAdapter);
		
		/*ReactHighScoreDatabase db = ReactHighScoreDatabase.getDatabase(getActivity());
		final List<String> list = new ArrayList<String>();
		for (HighScoreEntry entry : db.getAllEntries()) {
			list.add(entry.score + " ms - " + entry.name);
		}
		setListAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, list));*/
	}
	
	
	@Override
	public void onResume() {
		super.onResume();
		getActivity().registerReceiver(mMessageUpdateReceiver, mMessageIntentFilter);
		refresh();
	}
	

	@Override
	public void onPause() {

		getActivity().unregisterReceiver(mMessageUpdateReceiver);
		super.onPause();
	}
	
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		ReactHighScoreDatabase db = ReactHighScoreDatabase.getDatabase(getActivity());
		//HighScoreEntry entry = db.getEntry((int)id);
		Intent intent = new Intent();
		intent.putExtra("_id", id);
		intent.setClass(getActivity(), HistoryItemDetailActivity.class);
		/*if(entry.getLatLngList() == null){
			intent.setClass(getActivity(), HistoryItemDetailActivity.class);
		}else{
			intent.setClass(getActivity(), MapDisplayActivity.class);
			intent.putExtra("displayType", "history");	
		}*/
		startActivity(intent);
	}

	private class ActivityEntriesCursorAdapter extends CursorAdapter {

		private LayoutInflater mInflater;

		public ActivityEntriesCursorAdapter(Context context, Cursor c) {
			super(context, c, FLAG_REGISTER_CONTENT_OBSERVER);
			mInflater = LayoutInflater.from(context);
		}

		// Override the BindView function to set our data which means,
		// take the data from the cursor and put it into views.
		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			TextView tv1 = (TextView) view.findViewById(android.R.id.text1);
		//	TextView tv2 = (TextView) view.findViewById(android.R.id.text2);

			String name = cursor.getString(1);
			String score = cursor.getString(2);
			String bac = new DecimalFormat("#.##").format(cursor.getDouble(3));
			double drinks = cursor.getDouble(4);
			tv1.setText(name + "  score:"+ score +"ms  bac:"+bac+"  drinks:"+drinks );




	/*		part2 = "";
			double duration = cursor.getDouble(3);
			if ((int) duration > 0) {
				part2 = String.valueOf((int) duration) + "mins ";
			}
			part2 += String.valueOf(new DecimalFormat("#.##")
					.format((duration - Math.floor(duration)) * 60.0))
					+ " secs";
   */
		
		}

		// When the view will be created for first time,
		// we need to tell the adapters, how each item will look
		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			return mInflater.inflate(android.R.layout.simple_list_item_1, null);
		}
	}

}
