package com.example.termproject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;



// sign up process
public class SignupActivity extends Activity {
	EditText nameText;
	EditText passwordText;
	EditText confirmation;
	Button signUpButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sign_up);
		
		nameText = (EditText)findViewById(R.id.SignupUserName);
		passwordText = (EditText)findViewById(R.id.SingupPassword);
		confirmation = (EditText)findViewById(R.id.SingupPasswordConfirmation);
		signUpButton = (Button)findViewById(R.id.SignUpUser);
		
		signUpButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String userName = nameText.getText().toString();
				if(userName.trim().length() == 0) {
					AlertDialog.Builder alert = new AlertDialog.Builder(SignupActivity.this);
    			 	alert.setTitle("error"); 
	                alert.setMessage("Confirmation need to be same");
	                alert.setPositiveButton(R.string.errorButton, null); 
	                alert.show();
	                return;
				} else {
					String pw = passwordText.getText().toString();
					String conf = confirmation.getText().toString();
					if(pw.equals(conf)) {
						Log.d("singup", "before");
						String result = Cloud.signToServer(userName, pw, ApplicationConstant.signUpUri);
						Log.d("singup", "after");
						if(result == null) {
							Log.d("singup", "result is null");
						}
						if(result.equals(ApplicationConstant.singUpOK)) {
							Intent intent = new Intent("android.intent.action.ALLFRIENDS");
							startActivity(intent);
						} else {
							AlertDialog.Builder alert = new AlertDialog.Builder(SignupActivity.this);
		    			 	alert.setTitle("error"); 
			                alert.setMessage(result);
			                alert.setPositiveButton(R.string.errorButton, null); 
			                alert.show();
						}
					}
				}
			}
		});
	}
	
}