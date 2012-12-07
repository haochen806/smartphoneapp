package com.example.termproject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

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
						// TODO Auto-generated method stub
						String userName = nameText.getText().toString();
						String password = passwordText.getText().toString();
						Cloud cloud = new Cloud();
						String response = cloud.signToServer(userName, password, AplicationConstant.signInUri);
						if(response.equals(AplicationConstant.singinOK)) {
							Intent intent = new Intent("android.intent.action.VIEWFRIEND");
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
				// TODO Auto-generated method stub
				startActivity(new Intent("android.intent.action.SIGNUP"));
			}
		});
		
	}
	
}