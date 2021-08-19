package com.applutions.t2y.ui.tracking;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.applutions.t2y.R;
import com.applutions.t2y.customViews.BoldTextView;
import com.applutions.t2y.ui.notifications.response.BookingDetailsViewModel;
import com.applutions.t2y.utils.ApiResponse;
import com.applutions.t2y.utils.DirectionsJSONParser;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.applutions.t2y.utils.Constants.BASE_URL;

public class TrackingActivity extends AppCompatActivity implements OnMapReadyCallback {


    BoldTextView txtEstimatedTime;
    ImageView imgBack;
    RelativeLayout mLytRefresh;
    private GoogleMap mMap;
    private Socket mSocket= null;
    Double mSouceLat=0.0d;
    Double mDestLong=0.0d;
    Double mSourrceLong=0.0d;
    Double mDestLat=0.0d;
    BookingDetailsViewModel mViewModel;
    String invoiceNumber="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking);
        try {
            mSocket = IO.socket(BASE_URL);

        } catch (URISyntaxException e) {
            Log.i("TAG", "fail to connect socket", e);

        }
        mViewModel=new ViewModelProvider(this).get(BookingDetailsViewModel.class);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        ViewGroup.LayoutParams params = mapFragment.getView().getLayoutParams();
        params.height = displayMetrics.heightPixels/2;
        params.width = displayMetrics.widthPixels;
        mapFragment.getView().setLayoutParams(params);
        setInit();
        setListener();

    }

    @Override
    protected void onResume() {
        super.onResume();
        mViewModel.getRentalDetails(this,getIntent().getStringExtra("invoiceId"));
        mViewModel.getRentalDetailsLiveData().observe(this, response -> {
            ApiResponse.ApiResponseStatus status = response.getApiStatus();
            switch (status) {
                case LOADING:
                    Toast.makeText(this, "Loading.....", Toast.LENGTH_SHORT).show();
                    break;
                case FAILED:
                    Toast.makeText(this, "Failed to get data", Toast.LENGTH_SHORT).show();
                    finish();
                    break;
                case SUCCESS:
                    if(response.getData().getRentalObj()!=null) {
                        invoiceNumber=response.getData().getRentalObj().getInvoiceNumber();
                        mDestLat=19.8762;
                        mDestLong=75.3433;
                        if(response.getData().getRentalObj().isTrackingDropOff()) {
                            mDestLat = response.getData().getRentalObj().getDropOffLocation().getCoordinates().get(0);
                            mDestLong = response.getData().getRentalObj().getDropOffLocation().getCoordinates().get(1);
                        }
                        else if(response.getData().getRentalObj().isTrackingPickUp()) {
                            mDestLat = response.getData().getRentalObj().getPickUpLocation().getCoordinates().get(0);
                            mDestLong = response.getData().getRentalObj().getPickUpLocation().getCoordinates().get(1);
                        }
//                        String url = getUrlForDirection(new LatLng(mSouceLat, mSourrceLong),new  LatLng(mDestLat, mDestLong));
//                        //String url = getUrlForDirection(new LatLng(18.5204, 73.8567),new  LatLng(mDestLat, mDestLong));
//                        new DownloadTask().execute(url);

                        if(mSocket.connected()) {
                            JsonObject jsonObject =new  JsonObject();
                            jsonObject.addProperty("invoiceNumber", invoiceNumber);
                            mSocket.emit("userJoin", jsonObject);
                        }
                        else{
                            mSocket.connect();
                            JsonObject jsonObject =new JsonObject();
                            jsonObject.addProperty("invoiceNumber", invoiceNumber);
                            mSocket.emit("userJoin", jsonObject);
                        }
                        mSocket.on("trackingData", onNewMessage);

                        /*trailerId=response.getData().getRentalObj().getRentedItems().get(0).getItemId();
                        rentalId = response.getData().getRentalObj().getInvoiceId();
                        revisionId = response.getData().getRentalObj().getRevisions().get(0).getRevisionId();
                        txtTrailerName.setText(response.getData().getRentalObj().getRentedItems().get(0).getItemName());
                        HelperMethods.downloadImage(response.getData().getRentalObj().getRentedItems().get(0).getItemPhoto(), this, itemPic);
                        String start=response.getData().getRentalObj().getRentalPeriod().getStart();
                        txtRentalStartDate.setText(start.substring(0,10)+"\n"+start.substring(11,start.length()));
                        String end=response.getData().getRentalObj().getRentalPeriod().getEnd();
                        txtRentalEndDate.setText(end.substring(0,10)+"\n"+end.substring(11,end.length()));
                        txtCustomerAddress.setText(response.getData().getRentalObj().getDropOffLocation().getText());
                        mTxtPriceTotal.setText(response.getData().getRentalObj().getTotalCharges().getTotal() + " USD");
                        mTxtPriceTaxes.setText(response.getData().getRentalObj().getTotalCharges().getTaxes() + " USD");
                        mTxtDelveChanrges.setText(response.getData().getRentalObj().getTotalCharges().getDlrCharges() + " USD");
                        mTxtRentalCharges.setText(response.getData().getRentalObj().getTotalCharges().getRentalCharges() + " USD");*/
                    }
                    else{
                        Toast.makeText(this, "Couldn't fetch data, Please try again later!", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                    //Toast.makeText(this, "Cancel equest
                    // successfully!", Toast.LENGTH_SHORT).show();
                    break;
            }
        });
        //join socket

    }
    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    Double lat;
                    Double lang;
                    try {
                        lat  = Double.parseDouble(data.getString("lat"));
                        lang = Double.parseDouble(data.getString("long"));
                    } catch (JSONException e) {
                        return;
                    }
                    mSouceLat=lat;
                    mSourrceLong=lang;
                    String url = getUrlForDirection(new LatLng(mSouceLat, mSourrceLong),new  LatLng(mDestLat, mDestLong));
                    //String url = getUrlForDirection(new LatLng(18.5204, 73.8567),new  LatLng(mDestLat, mDestLong));
                    new DownloadTask().execute(url);
                }
            });
        }
    };
    private class DownloadTask extends AsyncTask<String, Void, String> {


        @Override
        protected String doInBackground(String... url) {

            String data = "";

            try {
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();


            parserTask.execute(result);

        }
    }

    String duration="";
    /**
     * A class to parse the Google Places in JSON format
     */

    double lat=0.0d;
    double lng=0.0d;
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                routes = parser.parse(jObject);
                JSONArray jLegs = ( (JSONObject)jObject.getJSONArray("routes").get(0)).getJSONArray("legs");
                duration=jLegs.getJSONObject(0).getJSONObject("duration").get("text").toString();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList points = null;
            PolylineOptions lineOptions = null;
            MarkerOptions markerOptions = new MarkerOptions();

            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList();
                lineOptions = new PolylineOptions();

                List<HashMap<String, String>> path = result.get(i);

                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                lineOptions.addAll(points);
                lineOptions.width(12);
                lineOptions.color(Color.RED);
                lineOptions.geodesic(true);

            }

            // Drawing polyline in the Google Map for the i-th route
//            mMap.addPolyline(lineOptions);
            if(lineOptions!=null){
                lat = Double.parseDouble(result.get(0).get(0).get("lat"));
                lng = Double.parseDouble(result.get(0).get(0).get("lng"));
            }
            else{
                lat = 18.5204;
                lng = 73.8567;
            }
            LatLng latLng=new LatLng(lat, lng);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            // Zoom in the Google Map
            mMap.animateCamera(CameraUpdateFactory.zoomTo(10f));
            mMap.addMarker(
                    new MarkerOptions()
                            .title("Trailer")
                            .position(new LatLng(latLng.latitude, latLng.longitude))
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_vehicle))
            );
            txtEstimatedTime.setText(duration+" away");
        }
    }

    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.connect();

            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }
    private void setListener() {

        imgBack.setOnClickListener(view -> {
            finish();
        });

        mLytRefresh.setOnClickListener(view -> {
            String url = getUrlForDirection(new LatLng(mSouceLat, mSourrceLong),new  LatLng(mDestLat, mDestLong));
//            String url = getUrlForDirection(new LatLng(18.5204, 73.8567),new  LatLng(19.8762, 75.3433));
            new DownloadTask().execute(url);
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mSocket.connected()) {
            mSocket.disconnect();
        }
    }

    private String getUrlForDirection(LatLng origin, LatLng dest) {
        return "https://maps.googleapis.com/maps/api/directions/json?origin="+origin.latitude+","+origin.longitude+"&destination="+dest.latitude+","+dest.longitude+"&sensor=false&mode=driving&key=AIzaSyDhK8wA8WVQbEqcVZyAVaxYM1kaoIp2jys";
    }
    private void setInit() {
        imgBack=findViewById(R.id.imgBack);
        txtEstimatedTime=findViewById(R.id.txtEsimatedTime);
        mLytRefresh=findViewById(R.id.lytRefresh);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }


}