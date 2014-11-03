package com.main.dommo;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import library.DatabaseHandler;
import library.UserFunctions;

import org.json.JSONException;
import org.json.JSONObject;

import android.support.v7.app.ActionBarActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class LogInActivity extends ActionBarActivity {

	EditText inputEmail;
    EditText inputPassword;
    TextView logInErrorMessage;
    Button buttonLogin;
	
	private static String KEY_SUCCESS = "success";
    private static String KEY_UID = "uid";
    private static String KEY_USERNAME = "uname";
    private static String KEY_FIRSTNAME = "fname";
    private static String KEY_LASTNAME = "lname";
    private static String KEY_EMAIL = "email";
    private static String KEY_CREATED_AT = "created_at";
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.log_in_page);
        
        inputEmail = (EditText) findViewById(R.id.logInEmail);
        inputPassword = (EditText) findViewById(R.id.logInPassword);
        logInErrorMessage = (TextView) findViewById(R.id.logInError);
        
        Button login = (Button) findViewById(R.id.logInLogInButton);
		login.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				if ((!inputEmail.getText().toString().equals("")) && (!inputPassword.getText().toString().equals(""))) {
                    NetAsync(view);
                }
                else if ((!inputEmail.getText().toString().equals(""))) {
                    Toast.makeText(getApplicationContext(),
                            "Password field empty", Toast.LENGTH_SHORT).show();
                }
                else if ((!inputPassword.getText().toString().equals(""))) {
                    Toast.makeText(getApplicationContext(),
                            "Email field empty", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(getApplicationContext(),
                            "Email and Password field are empty", Toast.LENGTH_SHORT).show();
                }
			}
		});
		
		Button signup = (Button) findViewById(R.id.logInSignUpButton);
        signup.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent signUpIntent= new Intent(view.getContext(), SignUpActivity.class);
                startActivityForResult(signUpIntent, 0);
            }
        });
        
        Button forgot = (Button) findViewById(R.id.logInForgot);
        forgot.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent forgotIntent = new Intent(view.getContext(), ForgotPasswordActivity.class);
                startActivityForResult(forgotIntent, 0);
                finish();
            }
        });
    }

    private class NetCheck extends AsyncTask
    {
        private ProgressDialog nDialog;
        
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            nDialog = new ProgressDialog(LogInActivity.this);
            nDialog.setTitle("Checking Network");
            nDialog.setMessage("Loading..");
            nDialog.setIndeterminate(false);
            nDialog.setCancelable(true);
            nDialog.show();
        }
        
        protected Boolean doInBackground(Object... args) {
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
    	            // TODO Auto-generated catch block
    	            e1.printStackTrace();
    	        } catch (IOException e) {
    	            // TODO Auto-generated catch block
    	            e.printStackTrace();
    	        }
    	    }
    	    return false;
    	}
    
    	protected void onPostExecute(Boolean th){
    	    if(th == true){
    	        nDialog.dismiss();
    	        new ProcessLogIn().execute();
    	    }
    	    else{
    	        nDialog.dismiss();
    	        logInErrorMessage.setText("Error in Network Connection");
    	    }
    	}
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.log_in, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    private class ProcessLogIn extends AsyncTask {
        private ProgressDialog pDialog;
        EditText inputEmail, inputPassword;
        String email,password;

        protected void onPreExecute() {
        	super.onPreExecute();
            inputEmail = (EditText) findViewById(R.id.logInEmail);
            inputPassword = (EditText) findViewById(R.id.logInPassword);
            email = inputEmail.getText().toString();
            password = inputPassword.getText().toString();
            pDialog = new ProgressDialog(LogInActivity.this);
            pDialog.setTitle("Contacting Servers");
            pDialog.setMessage("Logging in ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }
        
        protected JSONObject doInBackground(Object... args) {
            UserFunctions userFunction = new UserFunctions();
            JSONObject json = userFunction.loginUser(email, password);
            return json;
        }
        
        protected void onPostExecute(JSONObject json) {
            try {
               if (json.getString(KEY_SUCCESS) != null) {
            	   String res = json.getString(KEY_SUCCESS);
            	   if(Integer.parseInt(res) == 1){
            		   pDialog.setMessage("Loading User Space");
            		   pDialog.setTitle("Getting Data");
                       DatabaseHandler db = new DatabaseHandler(getApplicationContext());
                       JSONObject json_user = json.getJSONObject("user");
                       /**
                        * Clear all previous data in SQlite database.
                        **/
                       UserFunctions logout = new UserFunctions();
                       logout.logoutUser(getApplicationContext());
                       db.addUser(json_user.getString(KEY_FIRSTNAME),json_user.getString(KEY_LASTNAME),json_user.getString(KEY_EMAIL),json_user.getString(KEY_UID),json_user.getString(KEY_CREATED_AT));
                       /**
                        *If JSON array details are stored in SQlite it launches the User Panel.
                        **/
                       Intent upanel = new Intent(getApplicationContext(), PurgatoryActivity.class);
                       upanel.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                       pDialog.dismiss();
                       startActivity(upanel);
                       /**
                        * Close Login Screen
                        **/
                       finish();
            	   } else {
            		   pDialog.dismiss();
            		   logInErrorMessage.setText("Incorrect username/password");
            	   }
               }
            } catch (JSONException e) {
            	e.printStackTrace();
            }
        }
    }
    
    public void NetAsync(View view){
        new NetCheck().execute();
    }
}
