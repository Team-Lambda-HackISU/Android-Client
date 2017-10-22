package com.pewick.hackisugrocerylist;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static android.R.attr.data;

/**
 * Created by Chris on 10/21/2017.
 */
public class ScannedListActivity extends AppCompatActivity {
    private final String TAG = getClass().getSimpleName();
    private static final String POST_URL = "https://hackisu-banhawy.c9users.io/gotMilk";
    private EditText listEditText;
    private ImageView sendButton;
    private ProgressBar spinner;
    private ArrayList<String> list;

    private RequestQueue requestQueue;  // Assume this exists.


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanned_list);

        spinner = (ProgressBar)findViewById(R.id.loading_spinner);
        listEditText = (EditText)findViewById(R.id.list_edit_text);
        sendButton = (ImageView)findViewById(R.id.send_button);
        configureSendButton();

        Bundle bundle = getIntent().getExtras();
        if(bundle != null) {
            list = bundle.getStringArrayList("list");
            listEditText.setText(list.get(0));
        }

        for(int i = 1; i < list.size(); i++){
            listEditText.setText(listEditText.getText() +"\n"+list.get(i));
        }
        listEditText.setSelection(listEditText.getText().length());

        // Instantiate the RequestQueue.
        requestQueue = Volley.newRequestQueue(this);

    }

    private void configureSendButton(){
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //start a loading spinner
                showSpinner();
                //TODO: Send call to server
                Log.i("Scanned","Zip: "+getZipCode());

                //For now, just pass the list along, and display in a new listveiw activity
//                try {
//                    Thread.sleep(3000);
//                } catch (InterruptedException e){
//                    e.printStackTrace();
//                }

                //Read from EditText, in case user changed things
                String fullText = listEditText.getText().toString();
                String delimiter = "\n";
                final String[] newList = fullText.split(delimiter);

                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("list", new JSONArray(newList));
                    jsonObject.put("zip", getZipCode());
                } catch (JSONException e){
                    e.printStackTrace();
                }

                JsonObjectRequest postRequest = new JsonObjectRequest
                        (Request.Method.POST, POST_URL, jsonObject, new Response.Listener<JSONObject>() {

                            @Override
                            public void onResponse(JSONObject response) {
                                Log.i("Volley","Post Response: "+ response.toString());
                                //Handle errors, for nw, move on without price
                                ArrayList<GroceryItem> groceryItems = new ArrayList<>();
                                try {
                                    if(response.get("error") != null) {
                                        for (String item : newList) {
                                            groceryItems.add(new GroceryItem(item, 0));
                                        }
                                        if (groceryItems.size() > 0) {
                                            Intent intent = new Intent(ScannedListActivity.this, ShoppingListActivity.class);
                                            Bundle b = new Bundle();
                                            b.putParcelableArrayList("list", groceryItems);
                                            intent.putExtras(b);
                                            startActivity(intent);
                                        } else {
                                            Toast.makeText(getBaseContext(), "No items detected...", Toast.LENGTH_SHORT).show();
                                        }
                                        hideSpinner();
                                        return;
                                    }
                                } catch (JSONException e){
                                    e.printStackTrace();
                                }

                                //TODO: build grocery list and launch next activity

                                //Get store TODO: something with that if have time "nearest store"

                                for(String item : newList){
                                    try {
                                        String price = (String)response.get(item);
                                        Log.i("Volley", "price: "+price);
//                                        price = price.substring(0, price.charAt(price.indexOf('.')))
//                                                + price.substring(price.charAt(price.indexOf('.')));
                                        //OR multiply by 100
                                        GroceryItem groceryItem = new GroceryItem(item, (int)(Double.parseDouble(price)*100));

                                        try {
                                            String url = (String) response.get(item + "url");
                                            Log.i("Volley", "url: " + url);
                                            groceryItem.setURL(url);
                                        } catch(JSONException e){
                                            e.printStackTrace();
                                        }
                                        groceryItems.add(groceryItem);
                                    } catch (JSONException e){
                                        e.printStackTrace();
                                        groceryItems.add(new GroceryItem(item, 0));
                                    }
                                }

                                if(groceryItems.size() > 0) {
                                    Intent intent = new Intent(ScannedListActivity.this, ShoppingListActivity.class);
                                    Bundle b = new Bundle();
                                    b.putParcelableArrayList("list", groceryItems);
                                    intent.putExtras(b);
                                    startActivity(intent);

                                } else{
                                    Toast.makeText(getBaseContext(), "No items detected...", Toast.LENGTH_SHORT).show();
                                }
                                hideSpinner();

                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.i("Volley","Post Error!"+error);
                                hideSpinner();
                            }
                        });

                //TODO: Need to send get request now
//                JsonObjectRequest getRequest = new JsonObjectRequest
//                        (Request.Method.GET, POST_URL, null, new Response.Listener<JSONObject>() {
//                            @Override
//                            public void onResponse(JSONObject response) {
//                                Log.i("Volley"," Get Response: "+ response.toString());
//                                //TODO: build grocery list and launch next activity
//
//                                //Get store TODO: something with that if have time
//
//                                ArrayList<GroceryItem> groceryItems = new ArrayList<>();
//
//                                for(String item : newList){
//                                    try {
//                                        String price = (String)response.get(item);
//                                        Log.i("Volley", "price: "+price);
//                                    } catch (JSONException e){
//                                        e.printStackTrace();
//                                    }
//                                    groceryItems.add(new GroceryItem(item, 0));
//                                }
//
//                                if(groceryItems.size() > 0) {
//                                    Intent intent = new Intent(ScannedListActivity.this, ShoppingListActivity.class);
//                                    Bundle b = new Bundle();
//                                    b.putParcelableArrayList("list", groceryItems);
//                                    intent.putExtras(b);
//                                    startActivity(intent);
//
//                                } else{
//                                    Toast.makeText(getBaseContext(), "No words detected...",Toast.LENGTH_SHORT).show();
//                                }
//                                hideSpinner();
//                            }
//                        }, new Response.ErrorListener() {
//                            @Override
//                            public void onErrorResponse(VolleyError error) {
//                                Log.i("Volley","Get Error!"+error);
//                                hideSpinner();
//                            }
//                        });


                postRequest.setTag(TAG);
//                getRequest.setTag(TAG);
                requestQueue.add(postRequest);
//                requestQueue.add(getRequest);
            }
        });
    }

    private int getZipCode(){
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED){
            //Get the user's zip code
            String locationProvider = LocationManager.NETWORK_PROVIDER;
            // Or use LocationManager.GPS_PROVIDER
            LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            Location lastKnownLocation = locationManager.getLastKnownLocation(locationProvider);

            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses;
            try {
                addresses = geocoder.getFromLocation(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude(), 1);
                if(addresses != null){
                    return Integer.parseInt(addresses.get(0).getPostalCode());
                }
            } catch (Exception e){
                e.printStackTrace();
                return 50010;
            }
            return  50010;
        } else{
            //Use Ames' zip code as default
            return 50010;
        }
    }

    private void showSpinner(){
        Log.i("ScannedList","showSpinner()");
        listEditText.setVisibility(View.GONE);
        sendButton.setVisibility(View.GONE);
        spinner.setVisibility(View.VISIBLE);
    }

    private void hideSpinner(){
        listEditText.setVisibility(View.VISIBLE);
        sendButton.setVisibility(View.VISIBLE);
        spinner.setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed() {
        if(spinner.getVisibility() == View.VISIBLE ){ // OR request unfinished
            if (requestQueue != null) {
                requestQueue.cancelAll(TAG);
            }
            hideSpinner();
        } else {
            super.onBackPressed();
            if (requestQueue != null) {
                requestQueue.cancelAll(TAG);
            }
        }
    }

    @Override
    protected void onStop () {
        super.onStop();
        if (requestQueue != null) {
            requestQueue.cancelAll(TAG);
        }
    }
}