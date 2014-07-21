package com.example.musicchallenge;

import android.os.Bundle;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.PhoneNumberUtils;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * 
 * MainActivity is the initial screen when you have not login.
 * It asks for the phone number and checks its with a simple validation.
 * TODO:
 * 	Use SMS validation for the phone-number.
 *
 */

public class MainActivity extends Activity {
	
	// Context to use in OnClickListener View 
	private Context mContext;
	// SharedPreference file name
	public static final String PREFS_NAME = "MyPrefsFile";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		mContext = this;
		
		//Try to load the phone number.
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
		String val = settings.getString("phoneNumber", "");

		//If there is a number, login.
		//val = "";
		if (val.length()>0) {
			Intent intent = new Intent(this, GameActivity.class);
			startActivity(intent);
		} else {
			Button enterBtn = (Button) findViewById(R.id.enter_btn);
			
			enterBtn.setOnClickListener(new View.OnClickListener() {
	
				@Override
				public void onClick(View v) {
					
					EditText phoneNumber = (EditText) findViewById(R.id.phone_number);
					
					String val = phoneNumber.getText().toString();
					
					Log.w("hello", val);
					
					//Basic validation for the phone number.
					if (PhoneNumberUtils.isGlobalPhoneNumber(val) && val.length()>=9 && val.length()<=13) {
						SharedPreferences settings = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
						SharedPreferences.Editor editor = settings.edit();
						editor.putString("phoneNumber", (String) val);
						
						editor.commit();
						
						Intent intent = new Intent(mContext, GameActivity.class);
						startActivity(intent);
					}
					else {
						String incorrectNumberMessage = getResources().getString(R.string.incorrect_phone_number);
						Toast toast = Toast.makeText(mContext, incorrectNumberMessage, Toast.LENGTH_LONG);
						toast.show();
					}
				}
				}
			);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
