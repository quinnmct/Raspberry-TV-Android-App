package com.qrcode.raspberrytvcontrol;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

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
import android.widget.ImageButton;
import android.widget.Toast;
import android.widget.ToggleButton;


public class ControllerActivity extends ActionBarActivity {
	
	//ImageButton play, pause, ffwd, rwnd, stop, 
	//			rwnd2, ffwd2, mute, unmute;
	
	ImageButton ffwd, rwnd, stop, rwnd2, ffwd2, incVol, decVol, togglePause;
	//ToggleButton togglePlay;
	private String ip;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.controller_init);
        
        ip = getIntent().getExtras().getString("ip");
        
        
       
        
        
        togglePause = (ImageButton) findViewById(R.id.togglePlay);
        togglePause.setOnClickListener(new View.OnClickListener(){
        	public void onClick(View v) {
        		new HttpAsyncTask().execute("http://"+ip+":5000/togglepause/");
        	}
        });
        
      
        ffwd = (ImageButton) findViewById(R.id.ffwd);
        ffwd.setOnClickListener(new View.OnClickListener(){
        	public void onClick(View v) {
        		new HttpAsyncTask().execute("http://"+ip+":5000/seek/ff/30/");
        	}
        });
        
        rwnd = (ImageButton) findViewById(R.id.rwnd);
        rwnd.setOnClickListener(new View.OnClickListener(){
        	public void onClick(View v) {
        		new HttpAsyncTask().execute("http://"+ip+":5000/seek/rewind/30/");
        	}
        });
        
        ffwd2 = (ImageButton) findViewById(R.id.ffwd2);
        ffwd2.setOnClickListener(new View.OnClickListener(){
        	public void onClick(View v) {
        		new HttpAsyncTask().execute("http://"+ip+":5000/seek/ff/600/");
        	}
        });
        
        rwnd2 = (ImageButton) findViewById(R.id.rwnd2);
        rwnd2.setOnClickListener(new View.OnClickListener(){
        	public void onClick(View v) {
        		new HttpAsyncTask().execute("http://"+ip+":5000/seek/rewind/600/");
        	}
        });
        
        stop = (ImageButton) findViewById(R.id.stop);
        stop.setOnClickListener(new View.OnClickListener(){
        	public void onClick(View v) {
        		
        		new HttpAsyncTask().execute("http://"+ip+":5000/stop/");
			}
        });
        
        incVol = (ImageButton) findViewById(R.id.increaseVol);
        incVol.setOnClickListener(new View.OnClickListener(){
        	public void onClick(View v) {
				new HttpAsyncTask().execute("http://"+ip+":5000/increaseVolume/");
			    
        	}
        });
        
        decVol = (ImageButton) findViewById(R.id.decreaseVol);
        decVol.setOnClickListener(new View.OnClickListener(){
        	public void onClick(View v) {
        		new HttpAsyncTask().execute("http://"+ip+":5000/decreaseVolume/");
			}
        });
        

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

    
    public static String GET(String url){
        InputStream inputStream = null;
        String result = "";
        try {
 
            // create HttpClient
            HttpClient httpclient = new DefaultHttpClient();
 
            // make GET request to the given URL
            HttpResponse httpResponse = httpclient.execute(new HttpGet(url));
 
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
        	
        	System.out.println(result);
        	
        	JSONObject json;
        	try {
				json = new JSONObject(result);
			
        	
        	
				//display toast once message is received
				Toast.makeText(getBaseContext(), json.toString(), Toast.LENGTH_LONG).show();
           
         
         
				
				
        	} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
       }
    }
    
    
    
    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_init, container, false);
            return rootView;
        }
    }
}
