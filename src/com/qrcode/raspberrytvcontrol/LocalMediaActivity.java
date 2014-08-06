package com.qrcode.raspberrytvcontrol;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.qrcode.raspberrytvcontrol.YouTubeActivity.CustomList;
import com.qrcode.raspberrytvcontrol.YouTubeActivity.DownloadImageTask;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class LocalMediaActivity extends ActionBarActivity {
		
		static TextView t;
		ListView videoList;
		public String curDir;
		private String ip;
	
		@Override
	    protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.local_media_init);
	        
	        ip = getIntent().getExtras().getString("ip");
	        
	        //Toast.makeText(LocalMediaActivity.this, ip, Toast.LENGTH_SHORT).show();
	        
	        t = (TextView) findViewById(R.id.textView1);
	        
	        videoList = (ListView) findViewById(R.id.listView1);
	        
	        //Toast.makeText(LocalMediaActivity.this,
    				//"HTTP GET Local Media", Toast.LENGTH_SHORT).show();
	        getLocalMedia("~/");

	        
	        
	        videoList.setOnItemClickListener(new OnItemClickListener() {
	            @Override
	            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
	                
	            	
	                String type = ((TextView)((LinearLayout)((TableRow)((TableLayout)view).getChildAt(0)).getChildAt(1)).getChildAt(1)).getText().toString();
	                String title = ((TextView)((LinearLayout)((TableRow)((TableLayout)view).getChildAt(0)).getChildAt(1)).getChildAt(0)).getText().toString();
	                title = title.replaceAll(" ","%20");
	                if(type == "media"){
	                	//new HttpAsyncTask().execute("http://192.168.0.104:5000/loadMedia/"+title+"/");
	                	//Toast.makeText(getBaseContext(), title, Toast.LENGTH_LONG).show();
	                	
	                	
	                	if(curDir.length() != 2){
	                		new HttpAsyncTask().execute("http://"+ip+":5000/loadMedia/"+curDir.substring(2)+title);
	                	}else{
	                		new HttpAsyncTask().execute("http://"+ip+":5000/loadMedia/"+title);
	                	}
	                		
	                }else{
	                	
	                	curDir += title+"/";
	                	Toast.makeText(getBaseContext(), curDir, Toast.LENGTH_LONG).show();
	                	new HttpAsyncTask().execute("http://"+ip+":5000/listLocalMedia/"+curDir);
	                	
	                }
	                		
	                		
	                		//getText().toString();
	                
	                //Toast.makeText(getBaseContext(), url, Toast.LENGTH_LONG).show();
	                
	               // new HttpAsyncTask().execute("http://192.168.0.104:5000/loadMedia/"+type+"/");
				    
	                
	            }
	        });
	    }
	    
		
		


	    private void getLocalMedia(String mediaUri) {
			//HTTP request for Load Local Media 
	    	curDir = mediaUri;
	    	new HttpAsyncTask().execute("http://"+ip+":5000/listLocalMedia/"+mediaUri);
			
		}


	    //////////////////////////////////
	    /////default /////////////////////
	    //////////////////////////////////
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
	    
	    /*
	    public class CustomList extends ArrayAdapter<String>{
	    	private final Activity context;
	    	private final String[] web;
	    	private final String[] imageId;
	    	
	    	public CustomList(Activity context,ArrayList<String> web, ArrayList<String> imageId) {
		    	super(context, R.layout.list_layout, web);
		    	this.context = context;
		    	
		    	String[] webArr = new String[web.size()];
		    	String[] imageIdArr = new String[imageId.size()];
		    	
		    	webArr = web.toArray(webArr);
		    	imageIdArr = imageId.toArray(imageIdArr);
		    	
		    	
		    	this.web = webArr;
		    	this.imageId = imageIdArr;
	    	}
	    	@Override
	    	public View getView(int position, View view, ViewGroup parent) {
		    	LayoutInflater inflater = context.getLayoutInflater();
		    	View rowView= inflater.inflate(R.layout.list_layout, null, true);
		    	TextView txtTitle = (TextView) rowView.findViewById(R.id.txt);
		    	ImageView imageView = (ImageView) rowView.findViewById(R.id.img);
		    	txtTitle.setText(web[position]);
		    	//imageView.setImageURI(Uri.parse(imageId[position]));
		    	new DownloadImageTask(imageView).execute(imageId[position]);
		    	return rowView;
	    	}
	    }
	    */
	    
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

