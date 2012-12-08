package com.example.termproject;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
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
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.entity.mime.content.StringBody;

import android.util.Base64;
import android.util.Log;

public class Cloud {
	private static final String TAG = "Cloud";
	
	public static String signToServer(String name, String password, String Uri) {
		HttpClient client = new DefaultHttpClient();
		HttpPost signUpPost = new HttpPost(Uri);
		Log.d("cloud", Uri);
		String result = null;
		HttpResponse signUpResponse = null;
		try {
			List<NameValuePair> postData = new ArrayList<NameValuePair>(2);
			postData.add(new BasicNameValuePair("usrname", name));
			postData.add(new BasicNameValuePair("pwd", password));
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
	
	public static String uploadMessage(String userName, int friendId, byte[] data, int type) {
		HttpClient client = new DefaultHttpClient();
		HttpPost messagePost = new HttpPost(AplicationConstant.postMessage);
		HttpResponse messageResponse = null;
		String result = null;
		//StringBuffer buffer = new StringBuffer();
		//String line;
		Log.d(TAG, "in upload message");
		//String encodedImage = Base64.encode(data, Base64.DEFAULT);
		
		try {
			Log.d(TAG, userName);
			Log.d(TAG, "friendId " + friendId);
			Log.d(TAG, "type " + type);
			//Log.d(TAG, "data  " + data[0]);
			
			MultipartEntity multipartPost = new MultipartEntity();
			multipartPost.addPart("usrname", new StringBody(userName));
			multipartPost.addPart("friendId", new StringBody(Integer.toString(friendId)));
			multipartPost.addPart("type", new StringBody(Integer.toString(type)));
			multipartPost.addPart("data", new InputStreamBody(new ByteArrayInputStream(data), "image"));
			messagePost.setEntity(multipartPost);
			messageResponse = client.execute(messagePost);
			BufferedReader reader = new BufferedReader(new InputStreamReader(messageResponse.getEntity().getContent()));
			result = reader.readLine();
			/*line = reader.readLine();
			while(line != null) {
				buffer.append(line);
				line = reader.readLine();
			}*/
			
			reader.close();
		} catch (IOException e) {
			Log.e(TAG, e.toString());
		}
		
		//result = "OK";
		
		if(result == null) {
			Log.d(TAG, "result error result is null"); 
		}  else {
			Log.d(TAG, "result is not null    " + result);
		}
		
		return result;
	}
	
	public static void getMessage(String userName, int friendId) {
		HttpClient client = new DefaultHttpClient();
		HttpPost getMessage = new HttpPost(AplicationConstant.getMessage);
		HttpResponse messageResponse = null;
		//InputStream 
		try {
			List<NameValuePair> postData = new ArrayList<NameValuePair>(2);
			postData.add(new BasicNameValuePair("usrname", AplicationConstant.user));
			postData.add(new BasicNameValuePair("friendId", Integer.toString(friendId)));
			getMessage.setEntity(new UrlEncodedFormEntity(postData));
			messageResponse = client.execute(getMessage);
			messageResponse.getEntity().getContent();
		} catch(Exception e) {
			
		}
	}
}