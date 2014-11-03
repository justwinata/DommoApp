package library;

import java.util.ArrayList;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;
import android.content.Context;

public class UserFunctions {
	private JSONParser jsonParser;
	// URL of the PHP API
	private static String loginURL = "http://10.0.2.2/dommo_login_api/";
	private static String registerURL = "http://10.0.2.2/dommo_login_api/";
	private static String forpassURL = "http://10.0.2.2/dommo_login_api/";
	private static String login_tag = "login";
	private static String register_tag = "register";
	private static String forpass_tag = "forpass";

	// constructor
	public UserFunctions() {
		jsonParser = new JSONParser();
	}

	/**
	 * Function to Login
	 **/
	public JSONObject loginUser(String email, String password) {
		// Building Parameters
		List params = new ArrayList();
		params.add(new BasicNameValuePair("tag", login_tag));
		params.add(new BasicNameValuePair("email", email));
		params.add(new BasicNameValuePair("password", password));
		JSONObject json = jsonParser.getJSONFromUrl(loginURL, params);
		return json;
	}
	
	/**
     * Function to reset the password
     **/
    public JSONObject forPass(String forgotpassword){
        List params = new ArrayList();
        params.add(new BasicNameValuePair("tag", forpass_tag));
        params.add(new BasicNameValuePair("forgotpassword", forgotpassword));
        JSONObject json = jsonParser.getJSONFromUrl(forpassURL, params);
        return json;
    }

	/**
	 * Function to SignUp
	 **/
	public JSONObject registerUser(String firstName, String lastName, String email, String password) {
		// Building Parameters
		List params = new ArrayList();
		params.add(new BasicNameValuePair("tag", register_tag));
		params.add(new BasicNameValuePair("firstName", firstName));
		params.add(new BasicNameValuePair("lastName", lastName));
		params.add(new BasicNameValuePair("email", email));
		params.add(new BasicNameValuePair("password", password));
		JSONObject json = jsonParser.getJSONFromUrl(registerURL, params);
		return json;
	}

	/**
	 * Function to logout user Resets the temporary data stored in SQLite
	 * Database
	 * */
	public boolean logoutUser(Context context) {
		DatabaseHandler db = new DatabaseHandler(context);
		db.resetTables();
		return true;
	}
}