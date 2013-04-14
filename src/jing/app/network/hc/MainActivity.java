package jing.app.network.hc;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import org.apache.commons.io.IOUtils;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

public class MainActivity extends Activity implements OnEditorActionListener {
	
	private EditText mUrl;
	private TextView mContent;
	private ImageView mImage;
	private Bitmap mBitmap;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        mUrl = (EditText) findViewById(R.id.url);
        mUrl.setOnEditorActionListener(this);
        
        mContent = (TextView) findViewById(R.id.content);
        mContent.setVisibility(View.GONE);
        mImage = (ImageView) findViewById(R.id.image);
        mImage.setVisibility(View.GONE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.menu_settings) {
			openSettings();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void openSettings() {
		Intent intent = new Intent(this, SettingsActivity.class);
		startActivity(intent);
	}

	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		boolean handled = false;
		if (actionId == EditorInfo.IME_ACTION_DONE) {
			onLoadUrl();
			handled = true;
		}
		return handled;
	}

	private void onLoadUrl() {
		String stringUrl = mUrl.getText().toString();
		if (NetworkUtils.isNetworkAvailable(getApplicationContext())) {
			new AccessHttpUrlTask().execute(stringUrl);
		} else {
			Toast.makeText(this, "The network is unavailable.", Toast.LENGTH_SHORT).show();
		}
	}
	
	private class AccessHttpUrlTask extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... urls) {
			String result = "";
			try {
				result = downloadUrl(urls[0]);
			} catch (IOException e) {
				Log.d("Zhang Jing: ", e.getMessage());
			}
			
			return result;
		}

		@Override
		protected void onPostExecute(String result) {
			if (result != null) {
				mImage.setVisibility(View.GONE);
				mContent.setVisibility(View.VISIBLE);
				mContent.setText(result);
			} else if (mBitmap != null) {
				mImage.setVisibility(View.VISIBLE);
				mContent.setVisibility(View.GONE);
				mImage.setImageBitmap(mBitmap);
			}
		}

	}

	public String downloadUrl(String stringUrl) throws IOException {
		URL url = new URL(stringUrl);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setReadTimeout(10000);
		conn.setConnectTimeout(15000);
		conn.setRequestMethod("GET");
		conn.setDoInput(true);
		
		conn.connect();
		int response = conn.getResponseCode();
		String type = conn.getContentType();
		
		StringBuilder result = new StringBuilder();
		result.append("Response code: ")
			  .append(response)
			  .append('\n')
			  .append("ContentType: ")
			  .append(conn.getContentType())
			  .append('\n')
			  .append("ContentEncoding: ")
			  .append(conn.getContentEncoding())
			  .append('\n')
			  .append("ContentLength: ")
			  .append(conn.getContentLength())
			  .append('\n')
			  .append('\n');
		
		InputStream is = conn.getInputStream();
		if (type.startsWith("text")) {
			List<String> lines = IOUtils.readLines(is);
			for (String line : lines) {
				result.append(line)
				      .append('\n');
			}
			
			return result.toString();
		} else if (type.startsWith("image")) {
			mBitmap = BitmapFactory.decodeStream(is);
			return null;
		}
		
		return null;
	}

}
