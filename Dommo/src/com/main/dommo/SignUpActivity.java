package com.main.dommo;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import library.DatabaseHandler;
import library.UserFunctions;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SignUpActivity extends LogInActivity{
	
	private static String KEY_SUCCESS = "success";
    private static String KEY_UID = "uid";
    private static String KEY_FIRSTNAME = "firstName";
    private static String KEY_LASTNAME = "lastName";
    private static String KEY_EMAIL = "email";
    private static String KEY_CREATED_AT = "created_at";
    private static String KEY_ERROR = "error";
    
    EditText inputFirstName;
    EditText inputLastName;
    EditText inputEmail;
    EditText inputPassword;
    Button buttonSignUp;
    TextView registerErrorMessage;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sign_up_page);
		
		inputFirstName = (EditText) findViewById(R.id.signUpFirstName);
        inputLastName = (EditText) findViewById(R.id.signUpLastName);
        inputEmail = (EditText) findViewById(R.id.signUpEmail);
        inputPassword = (EditText) findViewById(R.id.signUpPassword);
        registerErrorMessage = (TextView) findViewById(R.id.signUpError);
        buttonSignUp = (Button) findViewById(R.id.signUpButton);
		
        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent logInIntent = new Intent(view.getContext(), LogInActivity.class);
                startActivityForResult(logInIntent, 0);
                finish();
            }
        });
        
        /**
         * Register Button click event.
         * A Toast is set to alert when the fields are empty.
         * Another toast is set to alert password must be 8 characters.
         **/
        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if((!inputPassword.getText().toString().equals("")) && (!inputFirstName.getText().toString().equals("")) && (!inputLastName.getText().toString().equals("")) && (!inputEmail.getText().toString().equals(""))) {
                	//need to check for same user email and give a toast if it does
                	if(inputPassword.getText().toString().length() > 8){
                		NetAsync(view);
                    }
                    else {
                        Toast.makeText(getApplicationContext(),
                                "Password should be minimum 8 characters", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(getApplicationContext(),
                            "One or more fields are empty", Toast.LENGTH_SHORT).show();
                }
            }
        });
	}
	
    private class NetCheck extends AsyncTask {
        private ProgressDialog nDialog;
        
        protected void onPreExecute() {
            super.onPreExecute();
            nDialog = new ProgressDialog(SignUpActivity.this);
            nDialog.setMessage("Loading..");
            nDialog.setTitle("Checking Network");
            nDialog.setIndeterminate(false);
            nDialog.setCancelable(true);
            nDialog.show();
        }
        
        protected Boolean doInBackground(Object... args) {
			/**
			 * Gets current device state and checks for working Internet connection by trying Google.
			 **/
            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            if (netInfo != null && netInfo.isConnected()) {
                try {
                    URL url = new URL("http://www.google.com");
                    HttpURLConnection urlc = (HttpURLConnection) url.openConnection();
                    urlc.setConnectTimeout(3000);
                    urlc.connect();
                    if (urlc.getResponseCode() == 200) {
                        return true;
                    }
                } catch (MalformedURLException e1) {
                    e1.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return false;
        }
        
        protected void onPostExecute(Boolean th) {
            if(th == true) {
                nDialog.dismiss();
                new ProcessSignUp().execute();
            }
            else {
                nDialog.dismiss();
                registerErrorMessage.setText("Error in Network Connection");
            }
        }
    }
	
	private class ProcessSignUp extends AsyncTask {
		private ProgressDialog pDialog;
		String email, password, firstName, lastName;
		
		protected void onPreExecute() {
			super.onPreExecute();
			inputEmail = (EditText) findViewById(R.id.signUpEmail);
			inputPassword = (EditText) findViewById(R.id.signUpPassword);
			firstName = inputFirstName.getText().toString();
			lastName = inputLastName.getText().toString();
			email = inputEmail.getText().toString();
			password = inputPassword.getText().toString();
			
			pDialog = new ProgressDialog(SignUpActivity.this);
			pDialog.setTitle("Contacting Servers");
			pDialog.setMessage("Registering ...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		protected JSONObject doInBackground(Object... args) {
			UserFunctions userFunction = new UserFunctions();
			JSONObject json = userFunction.registerUser(firstName, lastName, email, password);
			return json;
		}
		
		protected void onPostExecute(JSONObject json) {
			try {
				if (json.getString(KEY_SUCCESS) != null) {
					registerErrorMessage.setText("");
					String res = json.getString(KEY_SUCCESS);
					String red = json.getString(KEY_ERROR);
					if(Integer.parseInt(res) == 1){
						pDialog.setTitle("Getting Data");
						pDialog.setMessage("Loading Info");
						registerErrorMessage.setText("Successfully Registered");
						DatabaseHandler db = new DatabaseHandler(getApplicationContext());
						JSONObject json_user = json.getJSONObject("user");
		                
						/**
						 * Removes all the previous data in the SQlite database
						 **/
						UserFunctions logout = new UserFunctions();
						logout.logoutUser(getApplicationContext());
						db.addUser(json_user.getString(KEY_FIRSTNAME),json_user.getString(KEY_LASTNAME),json_user.getString(KEY_EMAIL),json_user.getString(KEY_UID),json_user.getString(KEY_CREATED_AT));
		                
						/**
						 * Stores registered data in SQlite Database
						 * Launch Registered screen
						 **/
						Intent registered = new Intent(getApplicationContext(), SignUpActivity.class);
		                
						/**
						 * Close all views before launching Registered screen
						 **/
						registered.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						pDialog.dismiss();
						startActivity(registered);
						finish();
					}
					else if (Integer.parseInt(red)==2) {
						pDialog.dismiss();
						registerErrorMessage.setText("User already exists");
					}
					else if (Integer.parseInt(red)==3) {
						pDialog.dismiss();
						registerErrorMessage.setText("Invalid Email id");
					}
				}
				else {
					pDialog.dismiss();
					registerErrorMessage.setText("Error occured in registration");
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
