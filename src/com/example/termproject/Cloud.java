package com.example.termproject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.util.Log;

public class Cloud {
	private static final String TAG = "Cloud";
	//private static String signUpUri = "autographbook641.appspot.com";
	
	public Cloud() {
		
	}
	
	public String signToServer(String name, String password, String Uri) {
		HttpClient client = new DefaultHttpClient();
		HttpPost signUpPost = new HttpPost(Uri);
		Log.d("cloud", Uri);
		String result = null;
		HttpResponse signUpResponse = null;
		try {
			List<NameValuePair> postData = new ArrayList<NameValuePair>(2);
			postData.add(new BasicNameValuePair("username", name));
			postData.add(new BasicNameValuePair("password", password));
			signUpPost.setEntity(new UrlEncodedFormEntity(postData));
			
			Log.d("cloud", name);
			Log.d("cloud", password);
			
			signUpResponse = client.execute(signUpPost);
			BufferedReader reader = new BufferedReader(new InputStreamReader(signUpResponse.getEntity().getContent()));
			result = reader.readLine();
			reader.close();

		} catch(ClientProtocolException e) {
			Log.e(TAG, e.toString());
		} catch(IOException e) {
			Log.e(TAG, e.toString());
		}
		
		return result;
		
	}
}