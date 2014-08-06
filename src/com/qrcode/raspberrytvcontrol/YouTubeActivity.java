package com.qrcode.raspberrytvcontrol;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class YouTubeActivity extends ActionBarActivity {
	EditText searchQuery;
	TextView connectedLight;
	ListView videoList;
	Button searchButton;
	private String ip;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        ip = getIntent().getExtras().getString("ip");
        
        //Toast.makeText(YouTubeActivity.this, ip, Toast.LENGTH_SHORT).show();
        
        // get reference to layout views
        //jsonResponse = (EditText) findViewById(R.id.jsonResponse);
        connectedLight = (TextView) findViewById(R.id.connectedLight);
        videoList = (ListView) findViewById(R.id.listView1);
        searchQuery = (EditText) findViewById(R.id.editText1);
        searchButton = (Button) findViewById(R.id.button1);
 
        // check connection
        if(isConnected()){
        	connectedLight.setBackgroundColor(0xFF00CC22);
        	connectedLight.setText("connected");
        }
        else{
        	connectedLight.setText("NOT CONNECTED");
        }
 
        // call async http task to get request sample json data
        //new HttpAsyncTask().execute("https://www.googleapis.com/youtube/v3/search?part=snippet&q=cats&key=AIzaSyCsKvQOoaJ3jZMH-DwFSfS3LCYM1wTvaWs");
    
        videoList.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                
            	
                String title = ((TextView)((LinearLayout)((TableRow)((TableLayout)view).getChildAt(0)).getChildAt(1)).getChildAt(0)).getText().toString();
                String url = ((TextView)((LinearLayout)((TableRow)((TableLayout)view).getChildAt(0)).getChildAt(1)).getChildAt(1)).getText().toString();
                
               Toast.makeText(getBaseContext(), "Now downloading: "+url, Toast.LENGTH_LONG).show();
                
                new HttpAsyncPiTask().execute("http://"+ip+":5000/YTDownload/",url);
			    
                
            }
        });
        
        searchButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				String query = searchQuery.getText().toString();
				
				//serialize query for http request by replacing white space with '+'
				query = query.replaceAll(" ", "+");
				
				new HttpAsyncYouTask().execute("https://www.googleapis.com/youtube/v3/search?part=snippet&maxResults=25&key=AIzaSyCsKvQOoaJ3jZMH-DwFSfS3LCYM1wTvaWs&q="+query);
			    
			}
		});
        
    }
    
    
    //HTTP PI SEND VIDEO
    public static String POST(String url, String urlData){
        InputStream inputStream = null;
        String result = "";
        try {
 
            // create HttpClient
            HttpClient httpclient = new DefaultHttpClient();
            
            
            HttpPost httppost = new HttpPost(url);
            
            //add data
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
            nameValuePairs.add(new BasicNameValuePair("url",urlData));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            
            HttpResponse httpResponse = httpclient.execute(httppost);
            
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
    
    public class HttpAsyncPiTask extends AsyncTask<String, Void, String> {
    	
    	
    	
        @Override
        protected String doInBackground(String... urls) {
 
            return POST(urls[0], urls[1]);
        }
        
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
        	
        	
        	//display toast once message is received
            //Toast.makeText(getBaseContext(), "Received! "+result, Toast.LENGTH_LONG).show();
           
         
       }
    }
    
    
    //HTTP YOUTUBE
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
 
    //Method checks network connection // permissions must be given in manifest
    public boolean isConnected(){
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) 
                return true;
            else
                return false;   
    }
    public class HttpAsyncYouTask extends AsyncTask<String, Void, String> {
    	
    	
    	
        @Override
        protected String doInBackground(String... urls) {
 
            return GET(urls[0]);
        }
        
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
        	
        	//display toast once message is received
            //Toast.makeText(getBaseContext(), "Received!", Toast.LENGTH_LONG).show();
           
         
            //display json data as JSONObject
            JSONObject json;
			try {
				
				json = new JSONObject(result);
				System.out.println(result);
				
				
				JSONArray ytItems = json.getJSONArray("items");
				ArrayList<String> titleList = new ArrayList<String>(); 
				ArrayList<String> imgList = new ArrayList<String>(); 
				ArrayList<String> urlList = new ArrayList<String>();
				
				for(int i=0; i<ytItems.length(); i++){
					JSONObject tempItem = ytItems.getJSONObject(i);
					//snippet object
					JSONObject details = tempItem.getJSONObject("snippet");
					titleList.add(details.getString("title"));
					
					JSONObject thumbnails = details.getJSONObject("thumbnails");
					JSONObject mediumThumb = thumbnails.getJSONObject("default");
					imgList.add(mediumThumb.getString("url"));
					
					//id object
					JSONObject id = tempItem.getJSONObject("id");
					
					
					String tempS = "www.youtube.com/watch?v="+id.getString("videoId");
					
					//Toast.makeText(getBaseContext(), tempS, Toast.LENGTH_LONG).show();
					urlList.add(tempS);
					
					//System.out.println(":: "+mediumThumb.getString("url"));
				}
				
				
				CustomList adapter = new CustomList(YouTubeActivity.this, titleList, imgList, urlList);
				videoList.setAdapter(adapter);
				
				
				
			//	ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, arrList);
				
				
			//	videoList.setAdapter(adapter);
				
				//jsonResponse.setText(json.toString(1));
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
       }
    }
    
    public class CustomList extends ArrayAdapter<String>{
    	private final Activity context;
    	private final String[] web;
    	private final String[] imageId;
    	private final String[] url;
    	
    	public CustomList(Activity context,ArrayList<String> web, ArrayList<String> imageId ,ArrayList<String> url) {
	    	super(context, R.layout.list_layout, web);
	    	this.context = context;
	    	
	    	String[] webArr = new String[web.size()];
	    	String[] imageIdArr = new String[imageId.size()];
	    	String[] urlArr = new String[url.size()];
	    	
	    	webArr = web.toArray(webArr);
	    	imageIdArr = imageId.toArray(imageIdArr);
	    	urlArr = url.toArray(urlArr);
	    	
	    	
	    	this.web = webArr;
	    	this.imageId = imageIdArr;
	    	this.url = urlArr;
    	}
    	@Override
    	public View getView(int position, View view, ViewGroup parent) {
	    	LayoutInflater inflater = context.getLayoutInflater();
	    	View rowView= inflater.inflate(R.layout.list_layout, null, true);
	    	
	    	TextView txtTitle = (TextView) rowView.findViewById(R.id.txt);
	    	txtTitle.setText(web[position]);
	    	
	    	TextView urlTitle = (TextView) rowView.findViewById(R.id.url);
	    	urlTitle.setText(url[position]);
	    	
	    	ImageView imageView = (ImageView) rowView.findViewById(R.id.img);
	    	//imageView.setImageURI(Uri.parse(imageId[position]));
	    	new DownloadImageTask(imageView).execute(imageId[position]);
	    	return rowView;
    	}
    }
    
    
    public class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
    
    
    
    //Default stuff
    /////////////////////////////////////////

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