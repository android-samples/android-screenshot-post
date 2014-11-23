package com.example.myscreenshot;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	// ViewをBitmapに変換
	// http://wada811.blogspot.com/2013/07/programmatically-screenshot-in-android.html
	public Bitmap getViewBitmap(View view){
	    view.setDrawingCacheEnabled(true);
	    Bitmap cache = view.getDrawingCache();
	    if(cache == null){
	        return null;
	    }
	    Bitmap bitmap = Bitmap.createBitmap(cache);
	    view.setDrawingCacheEnabled(false);
	    return bitmap;
	}
	
	// 実験用：変換した画像を表示
	public void buttonMethod(View button){
		// 画像を取得
		View view = findViewById(R.id.myframe);
		Bitmap bitmap = getViewBitmap(view);
		
		// 画像を表示
		ImageView image = (ImageView)findViewById(R.id.imageView3);
		image.setImageBitmap(bitmap);
		TextView text = (TextView)findViewById(R.id.textView1);
		image.setVisibility(View.VISIBLE);
		text.setVisibility(View.GONE);
	}
	
	// 実験用：変換した画像を送信
	public void buttonMethod2(View button){
		// 画像を取得
		View view = findViewById(R.id.myframe);
		Bitmap bitmap = getViewBitmap(view);
		
		// 画像を送信 （別スレッドで）
		ImageView image = (ImageView)findViewById(R.id.imageView3);
		TextView text = (TextView)findViewById(R.id.textView1);
		image.setVisibility(View.GONE);
		text.setVisibility(View.VISIBLE);
		text.setText("送信中...");
		//
		MyTask task = new MyTask();
		task.execute(bitmap);
	}
	
	class MyTask extends AsyncTask<Bitmap, String, String>{
		@Override
		protected String doInBackground(Bitmap... params) {
			String url = "http://myhost/image_post.php";
			try{
				// Bitmap取得
				Bitmap bitmap = params[0];

				// Bitmap -> byte[]
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				bitmap.compress(CompressFormat.PNG, 100, baos);
				byte[] bytes = baos.toByteArray();

				// 送信
				String res = postMultipart(url, bytes, "");
				
				// 送信結果
				Log.i("test", "res: " + res);
				return res;
			}
			catch(Exception ex){
				Log.i("test", "error: " + ex.toString());
				return ex.toString();
			}
			//return null;
		}

		@Override
		protected void onPostExecute(String result) {
			TextView text = (TextView)findViewById(R.id.textView1);
			text.setText(result);
		}
		
		
		
	}
	
	// 取得できた画像をサーバに送る関数
	public String postMultipart(String url, byte[] image, String params) throws Exception {
		try{
			HttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(url);
			ResponseHandler<String> responseHandler = new BasicResponseHandler();
			MultipartEntity multipartEntity =
			new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
			  
			ContentBody content = new InputStreamBody(new ByteArrayInputStream(image), "screenshot.png");
			multipartEntity.addPart("upfile", content);
			  
			httpPost.setEntity(multipartEntity);
			String res = httpClient.execute(httpPost, responseHandler);
			return res;
		}
		catch(Exception ex){
			throw ex;
		}
    }
}
