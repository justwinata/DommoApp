package com.main.dommo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class SignUpActivity extends LogInActivity{
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sign_up_page);
		
		Button signup = (Button) findViewById(R.id.signUpButton);
        signup.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent signUpIntent = new Intent();
                setResult(RESULT_OK, signUpIntent);
                finish();
                //startActivityForResult(signUpIntent, 0);
            }
        });
	}
}
