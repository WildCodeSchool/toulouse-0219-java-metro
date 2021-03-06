package fr.wildcodeschool.metro;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class StopSchedule extends AppCompatActivity {

    private final static String API_KEY = "&key=e083e127-3c7c-4d1b-b5c8-a5838936e4cf";
    private static int REFRESH_DELAY = 1000;
    private FirebaseAuth mAuth;
    SingletonLocation singletonLocation = SingletonLocation.getLocationInstance();
    UserLocation userLocation = singletonLocation.getUserLocation();

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menulauncher, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.btMapView:
                Intent goToMapView = new Intent(StopSchedule.this, MapsActivity.class);
                startActivity(goToMapView);
                return true;
            case R.id.btListView:
                Intent goToListView = new Intent(StopSchedule.this, RecycleViewStation.class);
                startActivity(goToListView);
                return true;
            case R.id.itemMenuRegister:
                Intent goToRegisterView = new Intent(StopSchedule.this, RegisterActivity.class);
                startActivity(goToRegisterView);
                return true;
            case R.id.itemMenuLogin:
                Intent goToMainActivity = new Intent(StopSchedule.this, MainActivity.class);
                startActivity(goToMainActivity);
                return true;
            case R.id.itemMenuFav:
                Intent goToFavorites = new Intent(StopSchedule.this, Favorites.class);
                startActivity(goToFavorites);
                return true;
            case R.id.itemMenuLogout:
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                mAuth.signOut();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void loadSchedule(String stationId) {
        final RequestQueue requestQueue = Volley.newRequestQueue(this);

        String url = "https://api.tisseo.fr/v1/stops_schedules.json?&stopsList=" + stationId + "&timetableByArea=1&number=2" + API_KEY;

        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject departures = (JSONObject) response.getJSONObject("departures");
                            JSONArray stopAreas = departures.getJSONArray("stopAreas");
                            JSONObject number = stopAreas.getJSONObject(0);
                            String directionName = number.getString("name");
                            JSONArray schedules = number.getJSONArray("schedules");
                            JSONObject num = (JSONObject) schedules.get(0);
                            JSONObject destination = (JSONObject) num.get("destination");
                            String stationName = destination.getString("name");
                            JSONArray journeys = num.getJSONArray("journeys");
                            JSONObject nextMetro = (JSONObject) journeys.get(0);
                            String waitime = nextMetro.getString("waiting_time");
                            JSONObject nextMetro2 = (JSONObject) journeys.get(1);
                            String waitsecond = nextMetro2.getString("waiting_time");
                            JSONObject num2 = (JSONObject) schedules.get(1);
                            JSONObject destination2 = (JSONObject) num2.get("destination");
                            String stationName2 = destination2.getString("name");
                            JSONArray journeys2 = num2.getJSONArray("journeys");
                            JSONObject nextMetro3 = (JSONObject) journeys2.get(0);
                            String waitime2 = nextMetro3.getString("waiting_time");
                            JSONObject nextMetro4 = (JSONObject) journeys.get(1);
                            String waitsecond2 = nextMetro4.getString("waiting_time");

                            TextView directionNam = findViewById(R.id.tvStopName);
                            directionNam.setText(directionName);
                            TextView stationNam = findViewById(R.id.tvDirection1);
                            stationNam.setText(stationName);
                            TextView waitIM = findViewById(R.id.tvNext1);
                            waitIM.setText(waitime);
                            TextView waitSecon = findViewById(R.id.tvNext2);
                            waitSecon.setText(waitsecond);
                            TextView otherWay = findViewById(R.id.tvDirection2);
                            otherWay.setText(stationName2);
                            TextView dep1 = findViewById(R.id.tvDeparture1);
                            dep1.setText(waitime2);
                            TextView dep2 = findViewById(R.id.tvDeparture2);
                            dep2.setText(waitsecond2);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("VOLLEY_ERROR", "onErrorResponse: " + error.getMessage());
                    }
                }
        );
        requestQueue.add(jsonObjectRequest);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stop_schedule);

        Intent intent = getIntent();
        final String stationId = intent.getStringExtra("STATION_ID");

        final Handler handler = new Handler();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                loadSchedule(stationId);
                handler.postDelayed(this, REFRESH_DELAY);
            }
        };
        handler.postDelayed(runnable, 0);
    }
}
