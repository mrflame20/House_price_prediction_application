package com.justm.ssip;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.JsObject;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Cartesian;
import com.anychart.core.cartesian.series.Line;
import com.anychart.data.Mapping;
import com.anychart.data.Set;
import com.anychart.enums.Anchor;
import com.anychart.enums.MarkerType;
import com.anychart.enums.TooltipPositionMode;
import com.anychart.graphics.vector.Stroke;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class moreInfo extends AppCompatActivity {

    LinearLayout nearByLandmarksLayout,priceLayout,populationLayout ;
    ImageView backBtn ,nearbyDropdown,priceDropdown;
    ConstraintLayout nearbyConstrain,priceConstrain;
    String[] radius = { "100","250","500","750","1000"};
    String API_KEY = "xAOx5oHHgpB2lfmAV1OVSh9KVAFCJeGd",locationRadius="100";
    Spinner rangeRadiusSpinner;
    TextView hospitalCount,restaurantCount, nonFurnishedPrice,FurnishedPrice ;
    ArrayAdapter<String> rangeRadiusAdapter;
    DatabaseReference graphReference ;
    AnyChartView anyChartView ;
    Cartesian cartesian;
    JSONArray hospitalJsonArray, restaurantsJsonArray;
    public static JSONArray jsonArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_info);
        graphReference = FirebaseDatabase.getInstance().getReference("Graphs/places/Gota");

        backBtn = findViewById(R.id.back_button);
        nearByLandmarksLayout = findViewById(R.id.nearby_landmark_click_layout);
        priceLayout = findViewById(R.id.price_click_layout);
        priceConstrain = findViewById(R.id.furnished_constrain_layout);
        priceDropdown = findViewById(R.id.price_nextIcon);
        populationLayout = findViewById(R.id.population_layout);
        nearbyConstrain = findViewById(R.id.nearby_constrain_layout);
        nearbyDropdown = findViewById(R.id.nearby_landmark_nextIcon);
        rangeRadiusSpinner = findViewById(R.id.nearby_range_spinner);
        hospitalCount = findViewById(R.id.hospitalCountView);
        restaurantCount = findViewById(R.id.restaurantCountView);
        anyChartView = findViewById(R.id.anyChartView);
        nonFurnishedPrice = findViewById(R.id.non_furnished_amount_textView);
        FurnishedPrice = findViewById(R.id.fully_furnished_amount_textView);


        nonFurnishedPrice.setText(String.format("₹ %.2f /SqFt", MainActivity.finalPrice));

        FurnishedPrice.setText(String.format("₹ %.2f /SqFt", MainActivity.finalPrice * 1.4));

        rangeRadiusAdapter = new ArrayAdapter<>(this, R.layout.spinner_bg, radius);
        rangeRadiusSpinner.setAdapter(rangeRadiusAdapter);

        hospitalCountFetcher("hospitals", locationRadius);
        restaurantCountFetcher("restaurants", locationRadius);

        setGraph();


        priceLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (priceConstrain.getVisibility() == View.VISIBLE) {
                    priceDropdown.setRotation(90);
                    priceConstrain.setVisibility(View.GONE);

                } else {
                    priceDropdown.setRotation(270);
                    priceConstrain.setVisibility(View.VISIBLE);
                }

            }
        });

        rangeRadiusSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                locationRadius = rangeRadiusSpinner.getSelectedItem().toString();
                hospitalCountFetcher("hospitals", locationRadius);
                restaurantCountFetcher("restaurants", locationRadius);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        nearByLandmarksLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (nearbyConstrain.getVisibility() == View.VISIBLE) {
                    nearbyDropdown.setRotation(90);
                    nearbyConstrain.setVisibility(View.GONE);

                } else {
                    nearbyDropdown.setRotation(270);
                    nearbyConstrain.setVisibility(View.VISIBLE);
                }

            }

        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void setGraph() {

        cartesian = AnyChart.line();

        cartesian.animation(true);

        cartesian.padding(10d, 20d, 5d, 20d);

        cartesian.crosshair().enabled(true);
        cartesian.crosshair()
                .yLabel(true)
                // TODO ystroke
                .yStroke((Stroke) null, null, null, (String) null, (String) null);

        cartesian.tooltip().positionMode(TooltipPositionMode.POINT);

        cartesian.title("Price v/s Year Graph");

        cartesian.yAxis(0).title("Price");
        cartesian.xAxis(0).labels().padding(5d, 5d, 5d, 5d);

        FirebaseDatabase.getInstance().getReference("Graph/places/"+MainActivity.locality).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<DataEntry> seriesData = new ArrayList<>();
                for(DataSnapshot priceDetails : dataSnapshot.getChildren())
                {
                        seriesData.add(new CustomDataEntry(priceDetails.child("years").toString(), Float.valueOf(priceDetails.child("residential").getValue().toString())*1000, Float.valueOf(priceDetails.child("commercial").getValue().toString())*1000));
                }
                Set set = Set.instantiate();
                set.data(seriesData);
                Mapping series1Mapping = set.mapAs("{ x: 'x', value: 'value' }");
                Mapping series2Mapping = set.mapAs("{ x: 'x', value: 'value2' }");

                Line series1 = cartesian.line(series1Mapping);
                series1.name("Residential");
                series1.hovered().markers().enabled(true);
                series1.hovered().markers()
                        .type(MarkerType.CIRCLE)
                        .size(4d);
                series1.tooltip()
                        .position("right")
                        .anchor(Anchor.LEFT_CENTER)
                        .offsetX(5d)
                        .offsetY(5d);

                Line series2 = cartesian.line(series2Mapping);
                series2.name("Commercial");
                series2.hovered().markers().enabled(true);
                series2.hovered().markers()
                        .type(MarkerType.CIRCLE)
                        .size(4d);
                series2.tooltip()
                        .position("right")
                        .anchor(Anchor.LEFT_CENTER)
                        .offsetX(5d)
                        .offsetY(5d);

                cartesian.legend().enabled(true);
                cartesian.legend().fontSize(13d);
                cartesian.legend().padding(0d, 0d, 10d, 0d);

                anyChartView.setChart(cartesian);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        /*seriesData.add(new CustomDataEntry("2005", 502000, 2408000));
        seriesData.add(new CustomDataEntry("2006", 495000, 2045000));
        seriesData.add(new CustomDataEntry("2007", 495000, 2052000));
        seriesData.add(new CustomDataEntry("2008", 494000, 2032000));
        seriesData.add(new CustomDataEntry("2009", 500000, 2062000));
        seriesData.add(new CustomDataEntry("2010", 496000, 2265000));
        seriesData.add(new CustomDataEntry("2011", 501000, 2250000));
        seriesData.add(new CustomDataEntry("2012", 503000, 2650000));
        seriesData.add(new CustomDataEntry("2013", 512000, 2201000));
        seriesData.add(new CustomDataEntry("2014", 503000, 2250000));
        seriesData.add(new CustomDataEntry("2015", 498000, 2012000));
        seriesData.add(new CustomDataEntry("2016", 520000, 2501000));
        seriesData.add(new CustomDataEntry("2017", 536000, 2604000));
        seriesData.add(new CustomDataEntry("2018", 574000, 2809000));*/


    }

    private void restaurantCountFetcher(String restaurant,String locationradius) {

        StringBuilder urlBuilder = new StringBuilder("https://api.tomtom.com/search/2/categorySearch/"+ restaurant +".json?");
        urlBuilder.append("lat=23.0722");
        urlBuilder.append("&lon=72.559291");
        urlBuilder.append("&key=" + API_KEY);
        urlBuilder.append("&radius="+locationradius);
        urlBuilder.append("&limit=500");
        JsonObjectRequest mapurlrequest = new JsonObjectRequest(urlBuilder.toString(), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                JSONArray resultJson = null;
                try {

                    restaurantsJsonArray = response.getJSONArray("results");
                    restaurantCount.setText(String.valueOf(restaurantsJsonArray.length()));
                    Log.i("JSON : ", restaurantsJsonArray.toString());
                    Log.i("JSON LENGTH :", String.valueOf(response.getJSONArray("results").length()));
//                    hospitalCount.setText(String.valueOf(count));

                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), "Unable to Fetch Data, Try Again Later", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
            }
        });

        MapUrlSingleton.getInstance(getApplicationContext()).addToRequestQueue(mapurlrequest);
    }

    private void hospitalCountFetcher(String hospital,String locationRadius) {

        StringBuilder urlBuilder = new StringBuilder("https://api.tomtom.com/search/2/categorySearch/"+ hospital +".json?");
        urlBuilder.append("lat=23.0722");
        urlBuilder.append("&lon=72.559291");
        urlBuilder.append("&key=" + API_KEY);
        urlBuilder.append("&radius="+locationRadius);
        urlBuilder.append("&limit=500");

        JsonObjectRequest mapUrlRequest = new JsonObjectRequest(urlBuilder.toString(), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                JSONArray resultJson = null;
                try {

                    hospitalJsonArray = response.getJSONArray("results");
                    hospitalCount.setText(String.valueOf(hospitalJsonArray.length()));
                    Log.i("JSON : ", hospitalJsonArray.toString());
                    Log.i("JSON LENGTH :", String.valueOf(response.getJSONArray("results").length()));
//                    hospitalCount.setText(String.valueOf(count));

                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), "Unable to Fetch Data, Try Again Later", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
            }
        });

        MapUrlSingleton.getInstance(getApplicationContext()).addToRequestQueue(mapUrlRequest);
    }

    private class CustomDataEntry extends ValueDataEntry {

        CustomDataEntry(String x, Number value, Number value2) {
            super(x, value);
            setValue("value2", value2);
        }

    }

    public void viewInfo(View view)
    {
        if(view.getTag().equals("hospital"))
        {
            jsonArray = hospitalJsonArray;
            startActivity(new Intent(getApplicationContext(), PlaceList.class));

        }
        else
        {
            jsonArray = restaurantsJsonArray;
            startActivity(new Intent(getApplicationContext(), PlaceList.class));

        }

    }
}

