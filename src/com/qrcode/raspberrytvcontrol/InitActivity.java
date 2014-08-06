package com.qrcode.raspberrytvcontrol;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.Toast;



public class InitActivity extends ActionBarActivity{

	private String ip;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_init);
        
        Bundle b  = getIntent().getExtras();
        ip = (String) b.get("ip");
        
        //Toast.makeText(InitActivity.this, ip, Toast.LENGTH_SHORT).show();

        //((GlobalApplication) this.getApplication()).setIP("192.168.0.105");
        
        
        LinearLayout ytClick = (LinearLayout) findViewById(R.id.youtubeLayout);
        LinearLayout lmClick = (LinearLayout) findViewById(R.id.localMediaLayout);
        LinearLayout controlClick = (LinearLayout) findViewById(R.id.controllerLayout);
        
        ytClick.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	Intent i = new Intent(getApplicationContext(), YouTubeActivity.class);
            	i.putExtra("ip", ip);
            	startActivity(i);
            }
        });
        lmClick.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	Intent i = new Intent(getApplicationContext(), LocalMediaActivity.class);
            	i.putExtra("ip", ip);
            	startActivity(i);
            }
        });
        
        controlClick.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	Intent i = new Intent(getApplicationContext(), ControllerActivity.class);
            	i.putExtra("ip", ip);
            	startActivity(i);
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

    /**
     * A placeholder fragment containing a simple view.
     */
    /*public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_init, container, false);
            return rootView;
        }
    }*/

}
