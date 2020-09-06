package com.justm.ssip;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.justm.ssip.moreInfo.jsonArray;

public class PlaceList extends AppCompatActivity {


    List<AddressClass> addressClassList = new ArrayList<>();
    List<String> placename =new ArrayList<>() ;
    List<String> placeaddress = new ArrayList<>() ;
    RecyclerView placeListRecyclerView ;
    PlaceListAdapter placeListAdapter ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_list);

        placeListAdapter = new PlaceListAdapter(placename,placeaddress);

        placeListRecyclerView = findViewById(R.id.place_list_recycler);
        addressClassList.clear();
        try {
            jsonDataParsing();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        placeListRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        placeListRecyclerView.setAdapter(placeListAdapter);

    }

    private void jsonDataParsing() throws JSONException {
        AddressClass addressClass = new AddressClass();
        for(int i = 0; i<jsonArray.length(); i++)
        {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            JSONObject addressData = jsonObject.getJSONObject("address");
            placename.add(jsonObject.getJSONObject("poi").getString("name"));
            placeaddress.add(addressData.getString("freeformAddress")) ;
            addressClassList.add(addressClass);
            placeListAdapter.notifyDataSetChanged();
        }
    }


}
