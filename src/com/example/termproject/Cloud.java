package com.example.termproject;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
			List<NameValuePair> postData = new ArrayList<NameValuePair>();
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
		HttpPost messagePost = new HttpPost(ApplicationConstant.postMessage);
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
	
	public static void getMessage(String userName, int friendId, ArrayList<String> key, ArrayList<String> type) {
		HttpClient client = new DefaultHttpClient();
		HttpPost getMessage = new HttpPost(ApplicationConstant.getMessage);
		HttpResponse messageResponse = null;
		String line = null;
		StringBuffer buffer = new StringBuffer();
		
		Log.d(TAG, "in cloud get message");
		
		Log.d(TAG, ApplicationConstant.getMessage);
		Log.d(TAG, "username is  " + userName);
		Log.d(TAG, "friendId is  "+ friendId);
		
		try {
			List<NameValuePair> postData = new ArrayList<NameValuePair>();
			postData.add(new BasicNameValuePair("usrname", ApplicationConstant.user));
			postData.add(new BasicNameValuePair("friendId", Integer.toString(friendId)));
			getMessage.setEntity(new UrlEncodedFormEntity(postData));
			messageResponse = client.execute(getMessage);
			//Log.d(TAG, "get here");
			
			BufferedReader reader = new BufferedReader(new InputStreamReader(messageResponse.getEntity().getContent()));
			line = reader.readLine();
			while(line != null) {
				//Log.d(TAG, "line is  " + line);
				String[] keyAndType = line.split(";");
				key.add(keyAndType[0]);
				type.add(keyAndType[1]);
				Log.d(TAG, " line is not null");
				Log.d(TAG, "key  " + keyAndType[0]);
				Log.d(TAG,"type  " + keyAndType[1]);
				buffer.append(line);
				line = reader.readLine();
			}
			//Log.e(TAG, buffer.toString());
		} catch(Exception e) {
			Log.e(TAG, e.toString());
		}
	}
	
	public static Map<String, byte[]> getMessageData(ArrayList<String> key) {
		Map<String, byte[]> map = new HashMap<String, byte[]>();
		HttpClient client = new DefaultHttpClient();
		HttpPost postRequest = null;
		HttpResponse response = null;
		for(int i = 0; i < key.size(); i++) {
			String currentKey = key.get(i);
			postRequest = new HttpPost(ApplicationConstant.getMessageData);
			try {
				List<NameValuePair> postData = new ArrayList<NameValuePair>();
				postData.add(new BasicNameValuePair("id", currentKey));
				postRequest.setEntity(new UrlEncodedFormEntity(postData));
				response = client.execute(postRequest);
				InputStream inputStream = response.getEntity().getContent();
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				byte[] data = new byte[1024];
				int length = 0;
				while ((length = inputStream.read(data))!=-1) {
				    	out.write(data, 0, length);
				}
				map.put(currentKey, out.toByteArray());
			} catch(Exception e) {
				Log.e(TAG, e.toString());
			}
		}
		
		return map;
	}
}