package com.example.termproject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


// sign in process
public class SignInActivity extends Activity{
	
	EditText nameText;
	EditText passwordText;
	Button signInButton;
	Button signUpButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sign_in);
		
		nameText = (EditText)findViewById(R.id.SigninUserName);
		passwordText = (EditText)findViewById(R.id.SingInPassword);
		signInButton = (Button)findViewById(R.id.SignInButton);
		signUpButton = (Button)findViewById(R.id.SignUpButton);
		
		signInButton.setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						String userName = nameText.getText().toString();
						String password = passwordText.getText().toString();
						String response = Cloud.signToServer(userName, password, ApplicationConstant.signInUri);
						if(response.equals(ApplicationConstant.singinOK)) {
							ApplicationConstant.user = userName;
							Intent intent = new Intent("android.intent.action.ALLFRIENDS");
							startActivity(intent);
						} else {
							AlertDialog.Builder alert = new AlertDialog.Builder(SignInActivity.this);
		    			 	alert.setTitle("error"); 
			                alert.setMessage(response);
			                alert.setPositiveButton(R.string.errorButton, null); 
			                alert.show();
						}
					}
					
				}
			);
		
		signUpButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startActivity(new Intent("android.intent.action.SIGNUP"));
			}
		});
		
	}
	
}