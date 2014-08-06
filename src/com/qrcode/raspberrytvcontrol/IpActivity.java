package com.qrcode.raspberrytvcontrol;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.qrcode.raspberrytvcontrol.LocalMediaActivity.CustomList;
import com.qrcode.raspberrytvcontrol.LocalMediaActivity.HttpAsyncTask;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class IpActivity extends ActionBarActivity{
	
	
	EditText ipText;
	Button submit;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ip_init);

        
        ipText = (EditText) findViewById(R.id.editText1);
        
        //Toast.makeText(IpActivity.this, ipText.getText(), Toast.LENGTH_SHORT).show();
        
        submit = (Button) findViewById(R.id.button1);
        
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            	
            	//Toast.makeText(IpActivity.this, ipText.getText(), Toast.LENGTH_SHORT).show();
                
            	new HttpAsyncTask().execute("http://"+ipText.getText()+":5000/isConnected/");
            }
        });
    }


	public static String GET(String url){
        InputStream inputStream = null;
        String result = "";
        try {
 
            // create HttpClient
            HttpClient httpclient = new DefaultHttpClient();
 
            // make GET request to the given URL
            HttpResponse httpResponse = httpclient.execute(new HttpGet(url));
            
            //HttpGet tt = new HttpGet(url);
           // HttpPost ttt = new HttpPost(url);
 
            // receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();
 
            // convert inputstream to string
            if(inputStream != null){
                result=convertInputStreamToString(inputStream);
            }else{
                result="Didn't work.";
            }
 
        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }
 
        return result;
    }
    
    private static String convertInputStreamToString(InputStream inputStream) throws IOException{
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;
 
        inputStream.close();
        return result;
 
    }
    
    
    public class HttpAsyncTask extends AsyncTask<String, Void, String> {
    	
    	
    	
        @Override
        protected String doInBackground(String... urls) {
 
            return GET(urls[0]);
        }
        
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
        	
        	JSONObject json;
        	try {
				json = new JSONObject(result);
				
				
				Toast.makeText(IpActivity.this, result, Toast.LENGTH_SHORT).show();
				
				if(result != null){
					Intent i = new Intent(getApplicationContext(), InitActivity.class);
					i.putExtra("ip", ipText.getText().toString());
					startActivity(i);
				}else{
					Toast.makeText(IpActivity.this, "Incorrect IP", Toast.LENGTH_SHORT).show();
				}
				/*
				JSONObject localItems = json.getJSONObject("data");
				JSONArray dirs = localItems.getJSONArray("dirs");
				JSONArray media = localItems.getJSONArray("files");
				
				ArrayList<String> titleList = new ArrayList<String>(); 
				ArrayList<String> imgList = new ArrayList<String>(); 
				ArrayList<String> typeList = new ArrayList<String>();
				
				for(int i=0; i<dirs.length(); i++){
					String temp = dirs.getString(i);
					titleList.add(temp);
					imgList.add("http://cdn.flaticon.com/png/256/32527.png");
					typeList.add("folder");
				}
				for(int i=0; i<media.length(); i++){
					String temp = media.getString(i);
					titleList.add(temp);
					imgList.add("https://cdn0.iconfinder.com/data/icons/thin-movies/57/thin-390_music_video_play_youtube-128.png");
					typeList.add("media");
				}
				
				
				CustomList adapter = new CustomList(LocalMediaActivity.this, titleList, imgList, typeList);
				videoList.setAdapter(adapter);
				
				*/
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
       }
    }
	
	
	
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.init, menu);
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

   

}
