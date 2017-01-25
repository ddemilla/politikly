/**
 * Add your package below. Package name can be found in the project's AndroidManifest.xml file.
 * This is the package name our example uses:
 *
 * package com.example.android.justjava;
 */
package com.example.android.politikly;

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
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

/**
 * This app displays an order form to order coffee.
 */
public class DisplayReps extends AppCompatActivity {

    int quantity = 1;

    //    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_reps);

        final CheckBox checkBox = (CheckBox) findViewById(R.id.checkbox_contact_info);
        if (checkBox.isChecked()) {
            checkBox.setChecked(false);
        }

    }

    /**
     * This method is called when the order button is clicked.
     */
    public void submitOrder(View view) {
        CheckBox contactInfoCheckBox = (CheckBox) findViewById(R.id.checkbox_contact_info);
        boolean returnContactInfo = contactInfoCheckBox.isChecked();

        EditText nameText = (EditText) findViewById(R.id.name);
        String name = nameText.getText().toString();

        if ( ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {
            displayMessage("NO permissions");
            Log.v("Error","This is true");
        }

        int permissionCheck = ContextCompat.checkSelfPermission(DisplayReps.this,
                Manifest.permission.ACCESS_COARSE_LOCATION);

        Log.v("permission", Integer.toString(permissionCheck));

        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(DisplayReps.this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(DisplayReps.this,
                    Manifest.permission.ACCESS_COARSE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(DisplayReps.this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        1);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }

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

        String priceMessage = createOrderSummary(returnContactInfo, name, longitude, latitude);

//        Intent intent = new Intent(Intent.ACTION_SENDTO);
//        intent.setData(Uri.parse("mailto:")); // only email apps should handle this
//        intent.putExtra(Intent.EXTRA_SUBJECT, "Just Java order for " + name);
//        intent.putExtra(Intent.EXTRA_TEXT, priceMessage);
//        if (intent.resolveActivity(getPackageManager()) != null) {
//            startActivity(intent);
//        }

//        displayMessage(priceMessage);
    }

    public String createOrderSummary(boolean returnContactInfo, String name, double longitude, double latitude) {
        return "Name: " + name + "\nLongitude " + longitude + "\nLatitude: " + latitude ;
    }

    public String getDistrictUrl(String longitude, String latitude){
        String base_url = "https://geocoding.geo.census.gov/geocoder/geographies/coordinates?benchmark=Public_AR_Current&format=json&vintage=Current_Current&layers=54,84";
        base_url += "&x=" + longitude + "&y=" + latitude ;

        return base_url;
    };

    public String getRepUrl(String district, String state){
        String base_url = "https://www.govtrack.us/api/v2/role?current=true";
        base_url += "&district=" + district + "&state=" + state;

        return base_url;
    };


    private int calculatePrice(boolean hasWhippedCream, boolean hasChocolate) {
        int price = 5;
        if (hasWhippedCream) {
            price += 1;
        }

        if (hasChocolate) {
            price += 2;
        }
        return price * quantity;
    }

    /**
     * This method displays the given text on the screen.
     */
    private void displayMessage(String message) {
        TextView orderSummaryTextView = (TextView) findViewById(R.id.order_summary_text_view);
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

            displayMessage(result.representative);
        }
    }
}