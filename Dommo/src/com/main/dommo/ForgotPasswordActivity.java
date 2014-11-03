package com.main.dommo;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import library.UserFunctions;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class ForgotPasswordActivity extends LogInActivity {

	private static String KEY_SUCCESS = "success";
	private static String KEY_ERROR = "error";

	EditText email;
	TextView alert;
	Button resetpass;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.forgot_password_page);

		email = (EditText) findViewById(R.id.forgotPasswordEmail);
		resetpass = (Button) findViewById(R.id.forgotPasswordSend);
		resetpass.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				NetAsync(view);
			}
		});
	}

	private class NetCheck extends AsyncTask {
		private ProgressDialog nDialog;

		protected void onPreExecute() {
			super.onPreExecute();
			nDialog = new ProgressDialog(ForgotPasswordActivity.this);
			nDialog.setMessage("Loading..");
			nDialog.setTitle("Checking Network");
			nDialog.setIndeterminate(false);
			nDialog.setCancelable(true);
			nDialog.show();
		}

		protected Boolean doInBackground(Object... args) {
			ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo netInfo = cm.getActiveNetworkInfo();
			if (netInfo != null && netInfo.isConnected()) {
				try {
					URL url = new URL("http://www.google .com");
					HttpURLConnection urlc = (HttpURLConnection) url
							.openConnection();
					urlc.setConnectTimeout(3000);
					urlc.connect();
					if (urlc.getResponseCode() == 200) {
						return true;
					}
				} catch (MalformedURLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			return false;
		}

		protected void onPostExecute(Boolean th) {
			if (th == true) {
				nDialog.dismiss();
				new ProcessRegister().execute();
			} else {
				nDialog.dismiss();
				alert.setText("Error in Network Connection");
			}
		}
	}

	private class ProcessRegister extends AsyncTask {
		private ProgressDialog pDialog;
		String forgotpassword;

		protected void onPreExecute() {
			super.onPreExecute();
			forgotpassword = email.getText().toString();
			pDialog = new ProgressDialog(ForgotPasswordActivity.this);
			pDialog.setTitle("Contacting Servers");
			pDialog.setMessage("Getting Data ...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		protected JSONObject doInBackground(Object... args) {
			UserFunctions userFunction = new UserFunctions();
			JSONObject json = userFunction.forPass(forgotpassword);
			return json;
		}

		protected void onPostExecute(JSONObject json) {
			/**
			 * Checks if the Password Change Process is success
			 **/
			try {
				if (json.getString(KEY_SUCCESS) != null) {
					alert.setText("");
					String res = json.getString(KEY_SUCCESS);
					String red = json.getString(KEY_ERROR);
					if (Integer.parseInt(res) == 1) {
						pDialog.dismiss();
						alert.setText("A recovery email is sent to you, see it for more details.");
					} else if (Integer.parseInt(red) == 2) {
						pDialog.dismiss();
						alert.setText("Your email does not exist in our database.");
					} else {
						pDialog.dismiss();
						alert.setText("Error occured in changing Password");
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	public void NetAsync(View view) {
		new NetCheck().execute();
	}
}
