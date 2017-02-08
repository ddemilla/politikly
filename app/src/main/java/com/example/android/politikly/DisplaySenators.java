package com.example.android.politikly;

/**
 * Created by danieldemillard on 2/5/17.
 */

/**
 * Add your package below. Package name can be found in the project's AndroidManifest.xml file.
 * This is the package name our example uses:
 *
 * package com.example.android.justjava;
 */

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.TextView;

public class DisplaySenators extends AppCompatActivity {

    int quantity = 1;

    //    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_senators);

        final CheckBox checkBox = (CheckBox) findViewById(R.id.checkbox_contact_info);
        if (checkBox.isChecked()) {
            checkBox.setChecked(false);
        }

        CheckBox contactInfoCheckBox = (CheckBox) findViewById(R.id.checkbox_contact_info);
        boolean returnContactInfo = contactInfoCheckBox.isChecked();

//        if ( ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {
//            displayMessage("NO permissions");
//            Log.v("Error","This is true");
//        }

//        int permissionCheck = ContextCompat.checkSelfPermission(DisplaySenators.this,
//                Manifest.permission.ACCESS_FINE_LOCATION);
//
//        Log.v("permission", Integer.toString(permissionCheck));
//
//        // Here, thisActivity is the current activity
//        if (ContextCompat.checkSelfPermission(DisplaySenators.this,
//                Manifest.permission.ACCESS_FINE_LOCATION)
//                != PackageManager.PERMISSION_GRANTED) {
//
//            // Should we show an explanation?
//            if (ActivityCompat.shouldShowRequestPermissionRationale(DisplaySenators.this,
//                    Manifest.permission.ACCESS_FINE_LOCATION)) {
//
//                // Show an explanation to the user *asynchronously* -- don't block
//                // this thread waiting for the user's response! After the user
//                // sees the explanation, try again to request the permission.
//
//            } else {
//
//                // No explanation needed, we can request the permission.
//
//                ActivityCompat.requestPermissions(DisplaySenators.this,
//                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
//                        1);
//
//                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
//                // app-defined int constant. The callback method gets the
//                // result of the request.
//            }
//        }
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
                    Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                    double longitude = location.getLongitude();
                    double latitude = location.getLatitude();

//        Log.v("longitude", Double.toString(longitude));
//        Log.v("latitude", Double.toString(latitude));

                    String url = getDistrictUrl(Double.toString(longitude), Double.toString(latitude));

                    Log.v("url", url);

                    DistrictAsyncTask getDistrictTask = new DistrictAsyncTask();
                    getDistrictTask.execute(url);

                } else {
                    displayMessage("You must provide access to your location.");
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    public String createRepSummary(String name) {
        return "Name: " + name + "\nLongitude ";
    }

    public String getDistrictUrl(String longitude, String latitude){
        String base_url = "https://geocoding.geo.census.gov/geocoder/geographies/coordinates?benchmark=Public_AR_Current&format=json&vintage=Current_Current&layers=54,84";
        base_url += "&x=" + longitude + "&y=" + latitude ;

        return base_url;
    };

    public String getRepUrl(String district, String state){
        String base_url = "https://www.govtrack.us/api/v2/role?current=true";
        base_url += "&state=" + state + "&role_type=senator";

        return base_url;
    };


    /**
     * This method displays the given text on the screen.
     */
    private void displayMessage(String message) {
        TextView orderSummaryTextView = (TextView) findViewById(R.id.senator_summary);
        orderSummaryTextView.setText(message);
    }

    private class DistrictAsyncTask extends AsyncTask<String, Void, District> {
        @Override
        protected District doInBackground(String... urls) {
            if (urls.length < 1 || urls[0] == null) {
                return null;
            }

            District result = Utils.fetchEarthquakeData(urls[0]);
            return result;
        }

        @Override
        protected void onPostExecute(District result) {
            if (result == null) {
                return;
            }

            String rep_url = getRepUrl(result.district, result.state);

            Log.v("url_rep", rep_url);

            RepAsyncTask getRepTask = new RepAsyncTask();
            getRepTask.execute(rep_url);
        }
    }

    private class RepAsyncTask extends AsyncTask<String, Void, Representative> {
        @Override
        protected Representative doInBackground(String... urls) {
            if (urls.length < 1 || urls[0] == null) {
                return null;
            }

            Representative result = Utils.fetchRepresentativeData(urls[0]);
            return result;
        }

        @Override
        protected void onPostExecute(Representative result) {
            if (result == null) {
                return;
            }

            String message = result.representative + " (" + result.party + ")" + "\n"
                    + result.description + "\n" + result.website;
            displayMessage(message);
        }
    }
}
