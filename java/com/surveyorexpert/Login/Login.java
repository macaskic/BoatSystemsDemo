package com.surveyorexpert.Login;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.surveyorexpert.DataBase.JSONParser;
import com.surveyorexpert.BoatControl.GesturesActivity;
import com.bornander.gestures.R;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

//import android.graphics.Color;
//import android.widget.Toast;

public class Login extends Activity implements OnClickListener {

	private EditText user, pass;
	private Button mSubmit, mRegister;
	private String userName, user_id, domain, ONLINE;
	
	// Progress Dialog
	private ProgressDialog pDialog;

	// JSON parser class
	JSONParser jsonParser = new JSONParser();


	// php login script location:
	private static final String LOGIN_URL =
	 "http://www.surveyorexpert.com/webservice/AndroidLogin.php";

	// JSON element ids from repsonse of php script:
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_MESSAGE = "message";
	private static final String TAG_DOMAIN = "domain";

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);


		// setup input fields
		user = (EditText) findViewById(R.id.etLoginUsername);
		pass = (EditText) findViewById(R.id.etLoginPassword);

		// setup buttons
		mSubmit = (Button) findViewById(R.id.bLoginLogin);
		mRegister = (Button) findViewById(R.id.bLoginRegister);

		// register listeners
		mSubmit.setOnClickListener(this);
		mRegister.setOnClickListener(this);


		
	}
	
	
	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.bLoginLogin:
			new TryLogin().execute();
			break;
		case R.id.bLoginRegister:
		//	Intent i = new Intent(this, Register.class);
		//	i.putExtra("userName", userName);
		//	startActivity(i);
			break;
		default:
			break;
		}
	}

	class TryLogin extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute( ) {
			super.onPreExecute();
         //   pDialog = new ProgressDialog(Login.this, AlertDialog.THEME_DEVICE_DEFAULT_DARK);
            pDialog = new ProgressDialog(Login.this, AlertDialog.BUTTON_POSITIVE);
            pDialog.setMessage("Attempting Login...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		@Override
		protected String doInBackground(String... args) {
			// Check for success tag
			Intent i = null;
			JSONObject json = new JSONObject();
			int success = 0;
			String username = user.getText().toString();
			String password = pass.getText().toString();
			userName = username;
			SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(Login.this);
			SharedPreferences.Editor editor = preferences.edit();
			
			try {
				// Building Parameters
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("username", username));
				params.add(new BasicNameValuePair("password", password));
			//	Log.d("LOGIN", "Attempt Login");
				
				// getting product details by making HTTP request
				try{

					json = jsonParser.makeHttpRequest(LOGIN_URL, "POST",params);
					success = json.getInt(TAG_SUCCESS);

                    //		editor.putString("ONLINE","true");
			//		editor.apply();
			//		ONLINE = "true";
			//		Toast.makeText(Login.this, "Attempt Login", Toast.LENGTH_LONG).show();
				
				} catch(Exception e){
					ONLINE = "false";
					Log.d("LOGIN", "Login failed");
			//		editor.putString("ONLINE","false");
			//		editor.apply();
				//	e.printStackTrace();
				}
				if (success == 1) {
								
					user_id = json.getString(TAG_MESSAGE);
					domain = json.getString(TAG_DOMAIN);
					
					if (domain.contains("ADMIN"))
					{
				//		i = new Intent(Login.this, AdminIntroduction.class);
					}
					else
					{
						i = new Intent(Login.this, GesturesActivity.class);
					}
					
					i.putExtra("userName", userName);
					i.putExtra("user_id", user_id);
					i.putExtra("domain", domain);
					i.putExtra("ONLINE", ONLINE);
					


					Log.d("Login Ok with ", json.getString(TAG_MESSAGE));
					finish();
					startActivity(i);				
				
					return json.getString(TAG_MESSAGE);
				} else {
				
				//	Log.d("LOGIN", "Login failed starting admin");
				//	Toast.makeText(Login.this, "Login Failed", Toast.LENGTH_LONG).show();
				//	i = new Intent(Login.this, AdminIntroduction.class);
					finish();
                //    i = new Intent(Login.this, GesturesActivity.class);
                //    i = new Intent(Login.this, Register.class);
                    // TODO temp work around
                    i = new Intent(Login.this, GesturesActivity.class);
					startActivity(i);

					return null;
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			return null;
		}

		protected void onPostExecute(String file_url) {
			/*
			Toast.makeText(Login.this, 
					"Login \n userName = " + userName +
					"\n user_id = " + user_id +
					"\n domain = " + domain + 	
					"\n ONLINE = " + ONLINE , 
					Toast.LENGTH_LONG).show();	
		*/
			pDialog.dismiss();
		}
	}
}
