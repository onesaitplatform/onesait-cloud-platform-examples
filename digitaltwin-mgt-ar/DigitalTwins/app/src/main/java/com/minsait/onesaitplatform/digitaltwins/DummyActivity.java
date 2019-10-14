package com.minsait.onesaitplatform.digitaltwins;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class DummyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dummy);

        new DownloadImageTask().execute();
    }

    class DownloadImageTask extends AsyncTask<Void, Void, Bitmap> {

        private Exception exception;

        protected Bitmap doInBackground(Void ...voids) {
            try {
                URL url = null;
                //url = new URL("https://lab.onesaitplatform.com/controlpanel/dashboards/generateDashboardImage/66075504-93d1-4d42-a08b-947a1b6ff04a?waittime=20000&height=794&width=1123&fullpage=false&token=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJwcmluY2lwYWwiOiJzdHJhbmdlclRlYW0iLCJjbGllbnRJZCI6Im9uZXNhaXRwbGF0Zm9ybSIsInVzZXJfbmFtZSI6InN0cmFuZ2VyVGVhbSIsInNjb3BlIjpbIm9wZW5pZCJdLCJuYW1lIjoic3RyYW5nZXJUZWFtIiwiZXhwIjoxNTcwMTMyMTMwLCJncmFudFR5cGUiOiJwYXNzd29yZCIsInBhcmFtZXRlcnMiOnsiZ3JhbnRfdHlwZSI6InBhc3N3b3JkIiwidXNlcm5hbWUiOiJzdHJhbmdlclRlYW0ifSwiYXV0aG9yaXRpZXMiOlsiUk9MRV9ERVZFTE9QRVIiXSwianRpIjoiNTFkMzg5NTctYjA0Ny00NzIwLTkzYjAtYWMxZjI0ZTllYzI2IiwiY2xpZW50X2lkIjoib25lc2FpdHBsYXRmb3JtIn0.2n_HGwExLF0oHzkm33wPIkJq6FW9biV_Qm3ZWH-kKTQ");
                url = new URL("https://lab.onesaitplatform.com/oauth-server/oauth/token");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                //Headers
                conn.setRequestProperty("Authorization", "Basic b25lc2FpdHBsYXRmb3JtOm9uZXNhaXRwbGF0Zm9ybQ==");
                conn.setRequestProperty( "Content-Type", "application/x-www-form-urlencoded");
                //Body
                OutputStream out = conn.getOutputStream();
                OutputStreamWriter outWriter = new OutputStreamWriter(out, "UTF-8");
                outWriter.write("username=strangerTeam&password=Stranger_001&grant_type=password&scope=openid");
                outWriter.flush();
                outWriter.close();
                out.close();
                StringBuilder result = new StringBuilder();
                InputStream input = new BufferedInputStream(conn.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
                //Bitmap
//

                return null;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute(Bitmap bitmap) {
            ImageView imageView = (ImageView)findViewById(R.id.dummyImageView);
            imageView.setImageBitmap(bitmap);
        }
    }
}
