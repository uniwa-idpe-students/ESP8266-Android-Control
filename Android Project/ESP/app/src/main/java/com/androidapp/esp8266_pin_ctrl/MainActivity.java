package com.androidapp.esp8266_pin_ctrl;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    EditText editIp;
    Button btnOn_D13, btnOff_D13, btnOn_D15, btnOff_D15;
    TextView textInfo1, textInfo2;
    String onoff;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editIp = (EditText)findViewById(R.id.ip);

        //Lights on/off
        btnOn_D13 = (Button)findViewById(R.id.bon_D13);
        btnOff_D13 = (Button)findViewById(R.id.boff_D13);

        //Computer on/off
        btnOn_D15 = (Button)findViewById(R.id.bon_D15);
        btnOff_D15 = (Button)findViewById(R.id.boff_D15);

        textInfo1 = (TextView)findViewById(R.id.info1);
        textInfo2 = (TextView)findViewById(R.id.info2);

        btnOn_D13.setOnClickListener(btnOnOffClickListener);
        btnOff_D13.setOnClickListener(btnOnOffClickListener);

        btnOn_D15.setOnClickListener(btnOnOffClickListener);
        btnOff_D15.setOnClickListener(btnOnOffClickListener);

    }

    View.OnClickListener btnOnOffClickListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {

            if(v==btnOn_D13)
            {
                onoff="/on_D13";
            }
            else if (v==btnOff_D13)
                {
                onoff="/off_D13";
                }
            else if (v==btnOn_D15)
            {
                onoff = "/on_D15";
            }
            else if (v==btnOff_D15)
            {
                onoff = "/off_D15";
            }

            btnOn_D13.setEnabled(false);
            btnOff_D13.setEnabled(false);

            btnOn_D15.setEnabled(false);
            btnOff_D15.setEnabled(false);

            String serverIP = editIp.getText().toString()+ onoff;

            TaskEsp taskEsp = new TaskEsp(serverIP);
            taskEsp.execute();

        }
    };

    private class TaskEsp extends AsyncTask<Void, Void, String> {

        String server;

        TaskEsp(String server){
            this.server = server;
        }

        @Override
        protected String doInBackground(Void... params) {

            final String p = "http://"+server;

            runOnUiThread(new Runnable(){
                @Override
                public void run() {
                    textInfo1.setText(p);
                }
            });

            String serverResponse = "";

            //Using java.net.HttpURLConnection
            try {
                HttpURLConnection httpURLConnection = (HttpURLConnection)(new URL(p).openConnection());

                if(httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {

                    InputStream inputStream = null;
                    inputStream = httpURLConnection.getInputStream();
                    BufferedReader bufferedReader =
                            new BufferedReader(new InputStreamReader(inputStream));
                    serverResponse = bufferedReader.readLine();

                    inputStream.close();
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
                serverResponse = e.getMessage();
            } catch (IOException e) {
                e.printStackTrace();
                serverResponse = e.getMessage();
            }
            //

            //Using org.apache.http
            /*
            HttpClient httpclient = new DefaultHttpClient();
            try {
                HttpGet httpGet = new HttpGet();
                httpGet.setURI(new URI(p));
                HttpResponse httpResponse = httpclient.execute(httpGet);

                InputStream inputStream = null;
                inputStream = httpResponse.getEntity().getContent();
                BufferedReader bufferedReader =
                        new BufferedReader(new InputStreamReader(inputStream));
                serverResponse = bufferedReader.readLine();

                inputStream.close();
            } catch (URISyntaxException e) {
                e.printStackTrace();
                serverResponse = e.getMessage();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
                serverResponse = e.getMessage();
            } catch (IOException e) {
                e.printStackTrace();
                serverResponse = e.getMessage();
            }
            */

            return serverResponse;
        }

        @Override
        protected void onPostExecute(String s) {
            textInfo2.setText(s);
            btnOn_D13.setEnabled(true);
            btnOff_D13.setEnabled(true);

            btnOn_D15.setEnabled(true);
            btnOff_D15.setEnabled(true);
        }
    }
}