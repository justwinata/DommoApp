package com.main.dommo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ForgotPasswordActivity extends LogInActivity{
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.forgot_password_page);
		
		Button send = (Button) findViewById(R.id.forgotPasswordSend);
        send.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent forgotPasswordIntent = new Intent();
                setResult(RESULT_OK, forgotPasswordIntent);
                finish();
            }
        });
	}
}
