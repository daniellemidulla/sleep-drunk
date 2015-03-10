package edu.dartmouth.cs.SleepDrunk;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;

import java.util.Calendar;

import edu.dartmouth.cs.SleepDrunk.R;

public class ReactGameActivity extends Activity {

	private ReactView view;
    public static int lastUpdatedTimestamp = -1;
    public static double sleeptime = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		ReactSoundManager.initSounds(this);

		setVolumeControlStream(AudioManager.STREAM_MUSIC);

        updateSleepTime();
		view = new ReactView(this, null);
		view.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		view.setFocusable(true);
		view.setFocusableInTouchMode(true);
		setContentView(view);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.restart_menuitem:
			view.restart();
			return true;

		}
		return super.onOptionsItemSelected(item);
	}

    public  void updateSleepTime(){

        if(lastUpdatedTimestamp != -1 && (Calendar.getInstance().get(Calendar.SECOND) - lastUpdatedTimestamp) < 3600) {
            return;
        }
        final EditText textComment = new EditText(this);
        //textComment.setHint("enter sleeptime");
        textComment.setInputType(InputType.TYPE_CLASS_NUMBER);
        AlertDialog ad = new AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.ui_profile_sleeptime_question))
                        // .setCancelable(false)
                .setView(textComment)
                .setPositiveButton("Ok",
                        new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog,
                                                int whichButton) {

                                String comment = textComment.getText()
                                        .toString();

                                sleeptime = Double.parseDouble(comment);
                                lastUpdatedTimestamp = Calendar.getInstance().get(Calendar.SECOND);
                            }
                        }).create();
        ad.show();
        //  Window window = ad.getWindow();
        //window.setContentView(ManagerDialogLayout_.build(context,ad));
    }

}