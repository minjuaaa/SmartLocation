package com.location.daku.myloctest;

import android.app.ProgressDialog;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import static java.lang.String.valueOf;

public class SignupActivity extends AppCompatActivity {
    LocationManager manager;
    GPSListener gpsListener;


    private static String IP_ADDRESS = "10.0.2.2";
    private static String TAG = "phpsignup";

    private EditText mEditTextID, mEditTextState, mEditTextType;
    private TextView mTextViewResult;
    private TextView textView1;

    private double latitude0;
    private double longtitude0;

    private Button button1;


    //준비

    public void startLocationService() {
        try {
            Location location = null;

            long minTime = 0;        // 0초마다 갱신 - 바로바로갱신
            float minDistance = 0;

            if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                location = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                if (location != null) {
                    latitude0 = location.getLatitude();
                    longtitude0 = location.getLongitude();
                    String message = "최근 위치1 -> \nLatitude : " + latitude0 + "\n Longitude : " + longtitude0;

                    textView1.setText(message);
                    //showCurrentLocation(latitude, longitude);
                    Log.i("MyLocTest", "최근 위치1 호출");
                }

                //위치 요청하기
                manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime, minDistance, gpsListener);
                //manager.removeUpdates(gpsListener);
                Toast.makeText(getApplicationContext(), "내 위치1확인 요청함", Toast.LENGTH_SHORT).show();
                Log.i("MyLocTest", "requestLocationUpdates() 내 위치1에서 호출시작 ~~ ");

            } else if (manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {

                location = manager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                if (location != null) {
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();
                    String message = "최근 위치2 -> \nLatitude : " + latitude + "\n Longitude : " + longitude;

                    textView1.setText(message);
                    //showCurrentLocation(latitude,longitude);

                    Log.i("MyLocTest","최근 위치2 호출");
                }


                //위치 요청하기
                manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, minTime, minDistance, gpsListener);
                //manager.removeUpdates(gpsListener);
                Toast.makeText(getApplicationContext(), "내 위치2확인 요청함", Toast.LENGTH_SHORT).show();
                Log.i("MyLocTest","requestLocationUpdates() 내 위치2에서 호출시작 ~~ ");
            }

        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }


    class GPSListener implements LocationListener {

        // 위치 확인되었을때 자동으로 호출됨 (일정시간 and 일정거리)
        @Override
        public void onLocationChanged(Location location) {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            String message = "내 위치는 \nLatitude : 37.483216" +  "\nLongtitude : 127.123180 ";
            //lattitude longtitude 삭제
            textView1.setText(message);

            //showCurrentLocation(latitude,longitude);
            Log.i("MyLocTest","onLocationChanged() 호출되었습니다.");
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mEditTextID = (EditText)findViewById(R.id.et_id);
        mEditTextState = (EditText)findViewById(R.id.state);
        mEditTextType = (EditText)findViewById(R.id.type);
        mTextViewResult = (TextView)findViewById(R.id.textView_result);

        mTextViewResult.setMovementMethod(new ScrollingMovementMethod());




        //준비2

        button1 = findViewById(R.id.button1);
        textView1 = findViewById(R.id.textView1);
        manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        gpsListener = new GPSListener();

//        private void showCurrentLocation(double latitude, double longitude) {
//            LatLng curPoint = new LatLng(latitude, longitude);
//
//        }


        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                startLocationService();
            }
        });











        Button buttonInsert = (Button)findViewById(R.id.btn_register);
        buttonInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                String ID = mEditTextID.getText().toString();


                String ID = mEditTextID.getText().toString();
                String State = mEditTextState.getText().toString();
                String Type = mEditTextType.getText().toString();
                String Latitude = valueOf(latitude0);
                String Longtitude = valueOf(longtitude0);



                InsertData task = new InsertData();
                task.execute("http://" + IP_ADDRESS + "/insert.php", ID,State,Type,Latitude,Longtitude);




            }
        });

    }


    class InsertData extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(SignupActivity.this,
                    "Please Wait", null, true, true);
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
            mTextViewResult.setText(result);
            Log.d(TAG, "POST response  - " + result);
        }


        @Override
        protected String doInBackground(String... params) {

            String userID = (String)params[1];
            String state = (String)params[2];
            String type = (String)params[3];
            String latitude = (String)params[4];
            String longtitude = (String)params[5];
            String serverURL = (String)params[0];
            String postParameters = "userID=" + userID + "&state=" + state + "&type=" + type + "&latitude=" + latitude + "&longtitude=" + longtitude;


            try {

                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();


                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.connect();


                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();


                int responseStatusCode = httpURLConnection.getResponseCode();
                Log.d(TAG, "POST response code - " + responseStatusCode);

                InputStream inputStream;
                if(responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                }
                else{
                    inputStream = httpURLConnection.getErrorStream();
                }


                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line = null;

                while((line = bufferedReader.readLine()) != null){
                    sb.append(line);
                }


                bufferedReader.close();


                return sb.toString();


            } catch (Exception e) {

                Log.d(TAG, "InsertData: Error ", e);

                return new String("Error: " + e.getMessage());
            }

        }
    }


}
