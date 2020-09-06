package com.justm.ssip;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.ml.common.FirebaseMLException;
import com.google.firebase.ml.custom.FirebaseCustomLocalModel;
import com.google.firebase.ml.custom.FirebaseModelDataType;
import com.google.firebase.ml.custom.FirebaseModelInputOutputOptions;
import com.google.firebase.ml.custom.FirebaseModelInputs;
import com.google.firebase.ml.custom.FirebaseModelInterpreter;
import com.google.firebase.ml.custom.FirebaseModelInterpreterOptions;
import com.google.firebase.ml.custom.FirebaseModelOutputs;

import java.util.HashMap;

import static android.text.TextUtils.isEmpty;

public class MainActivity extends AppCompatActivity {

    Spinner spinnerProperty, spinnerBHK,spinneryearsold,spinnertotalBathroom, spinnerCondition, spinnerBalcony;
    String[] condition = { "1","2","3","4","5"};
    String[] noOfBalcony = {"0","1","2","3","4"};
    String[] propertyType = { "Residential","Commercial"};
    String[] BHK = { "1","2","3","4","5"};
    String[] yearsOld = { "0","1","2","3","4","5","6","7","8","9","10","11","12","13","14"};
    String[] bathrooms = { "1","2","3","4"};
    String[] locality_str = {"Gota", "Sola", "Navarangpura","Shilaj","Panchvati"};
    String selectedLocality, selectedLivingArea, sqFeetArea, selectedFloors, selectedBhk, selectedNoOfBathrooms, selectedYeasOld, selectedCondition, selectedNoOfBalconies;
    boolean dataReceived = false ;
    ArrayAdapter<String> propertyAdapter,BHKAdapter, yearsOldAdapter,bathroomAdapter, conditionAdapter, balconyAdapter,localityAdapter;
    TextView moreInfo, priceTextView;
    EditText livingAreaEditBox,totalSqFeetEditBox,floorEditBox;
    Button estimatePriceBtn ;
    TextInputLayout livingAreaLayout,floorLayout,localityLayout,totalSqFtLayout;
    ScrollView mainScroll;
    AutoCompleteTextView localityTextView;
    FirebaseModelInterpreter interpreter;
    FirebaseModelInputOutputOptions inputOutputOptions;
    public static float finalPrice;
    boolean canEstimatePrice = true;
    public static String locality;
    HashMap<String, Integer > localityID= new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        spinnerProperty=findViewById(R.id.property_type);
        mainScroll=findViewById(R.id.main_scroll);
        floorLayout=findViewById(R.id.floor_layout);
        localityLayout=findViewById(R.id.locality_layout);
        totalSqFtLayout=findViewById(R.id.total_sq_ft_layout);
        livingAreaLayout =findViewById(R.id.living_area_layout);
        spinnerBHK=findViewById(R.id.bhk_spinner);
        spinneryearsold=findViewById(R.id.yearsold_spinner);
        spinnertotalBathroom=findViewById(R.id.bathroom_spinner);
        spinnerCondition=findViewById(R.id.condition_spinner);
        spinnerBalcony=findViewById(R.id.balcony_spinner);
        moreInfo=findViewById(R.id.moreinfo_textview);
        livingAreaEditBox = findViewById(R.id.living_area_text_box);
        totalSqFeetEditBox = findViewById(R.id.total_sq_ft_text_box);
        localityTextView =findViewById(R.id.locality_textbox);
        //localityEditBox = findViewById(R.id.locality_text_box) ;
        floorEditBox = findViewById(R.id.floor_title);
        estimatePriceBtn = findViewById(R.id.estimatePriceButton);
        priceTextView = findViewById(R.id.textView3);

        localityID.put("Gota",1);
        localityID.put("Sola",5);
        localityID.put("Navarangpura",2);
        localityID.put("Shilaj",4);
        localityID.put("Panchvati",3);

        localityAdapter=new ArrayAdapter<>(this,R.layout.spinner_bg,locality_str);
        localityTextView.setAdapter(localityAdapter);

        propertyAdapter=new ArrayAdapter<>(this,R.layout.spinner_bg,propertyType);
        spinnerProperty.setAdapter(propertyAdapter);
        spinnerProperty.setEnabled(false);

        BHKAdapter=new ArrayAdapter<>(this,R.layout.spinner_bg,BHK);
        spinnerBHK.setAdapter(BHKAdapter);
        spinnerBHK.setEnabled(true);
        spinnerBHK.setSelection(0);

        yearsOldAdapter =new ArrayAdapter<>(this,R.layout.spinner_bg, yearsOld);
        spinneryearsold.setAdapter(yearsOldAdapter);
        spinneryearsold.setEnabled(true);
        spinneryearsold.setSelection(0);

        bathroomAdapter=new ArrayAdapter<>(this,R.layout.spinner_bg,bathrooms);
        spinnertotalBathroom.setAdapter(bathroomAdapter);
        spinnertotalBathroom.setEnabled(true);
        spinnertotalBathroom.setSelection(0);

        conditionAdapter=new ArrayAdapter<>(this,R.layout.spinner_bg,condition);
        spinnerCondition.setAdapter(conditionAdapter);
        spinnerCondition.setEnabled(true);
        spinnerCondition.setSelection(0);

        balconyAdapter=new ArrayAdapter<>(this,R.layout.spinner_bg,noOfBalcony);
        spinnerBalcony.setAdapter(balconyAdapter);
        spinnerBalcony.setEnabled(true);
        spinnerBalcony.setSelection(0);

//        View lastChild = mainScroll.getChildAt(mainScroll.getChildCount() - 1);
//        int bottom = lastChild.getBottom() + mainScroll.getPaddingBottom();
//        int sy = mainScroll.getScrollY();
//        int sh = mainScroll.getHeight();
//        int delta = bottom - (sy + sh);
//
//        mainScroll.smoothScrollBy(0, delta);
        //mainScroll.fullScroll(View.FOCUS_UP);




        estimatePriceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               selectedFloors = floorEditBox.getText().toString();
               selectedLivingArea = livingAreaEditBox.getText().toString();
               sqFeetArea = totalSqFeetEditBox.getText().toString();
               selectedLocality = localityTextView.getText().toString();
               selectedBhk = spinnerBHK.getSelectedItem().toString();
               selectedNoOfBathrooms = spinnertotalBathroom.getSelectedItem().toString();
               selectedYeasOld = spinneryearsold.getSelectedItem().toString();
               selectedCondition = spinnerCondition.getSelectedItem().toString();
               selectedNoOfBalconies = spinnerBalcony.getSelectedItem().toString();
               locality = selectedLocality;


               if( isEmpty(selectedFloors) || isEmpty(selectedLivingArea) || isEmpty(sqFeetArea) || isEmpty(selectedLocality))
               {
                   Toast.makeText(getApplicationContext(),"Enter Details Properly",Toast.LENGTH_SHORT).show();
                   if(isEmpty(selectedLocality))
                   {
                       localityLayout.setErrorEnabled(true);
                       localityLayout.setError("Please Enter Locality");
                   }
                   else
                       localityLayout.setErrorEnabled(false);
                   if(isEmpty(sqFeetArea))
                   {
                       totalSqFtLayout.setErrorEnabled(true);
                       totalSqFtLayout.setError("Please Enter Total SqFt");
                   }
                   else
                       totalSqFtLayout.setErrorEnabled(false);
                   if(isEmpty(selectedLivingArea))
                   {
                       livingAreaLayout.setErrorEnabled(true);
                       livingAreaLayout.setError("Please Enter Living Area");
                   }
                   else
                       livingAreaLayout.setErrorEnabled(false);
                   if(isEmpty(selectedFloors))
                   {
                       floorLayout.setErrorEnabled(true);
                       floorLayout.setError("Please Enter Floors");
                   }
                   else
                       floorLayout.setErrorEnabled(false);
                   dataReceived= false;


               }

               else
               {
                   dataReceived = true ;
                   floorLayout.setErrorEnabled(false);
                   livingAreaLayout.setErrorEnabled(false);
                   totalSqFtLayout.setErrorEnabled(false);
                   localityLayout.setErrorEnabled(false);
               }

               if(dataReceived)
                {
                    if(Integer.parseInt(selectedLivingArea) >= Integer.parseInt(sqFeetArea))
                    {
                        Toast.makeText(MainActivity.this, "Living Area can't be bigger than total SqFt", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        EstimatePrice();
                        moreInfo.setVisibility(View.VISIBLE);
                    }

                }
               else
                   moreInfo.setVisibility(View.GONE);
            }

        });

        moreInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent moreInfo = new Intent(MainActivity.this, moreInfo.class);
                startActivity(moreInfo);
            }
        });

    }

    void EstimatePrice()
    {
        FirebaseCustomLocalModel localModel = new FirebaseCustomLocalModel.Builder().setAssetFilePath("ssip_model_final.tflite").build();

        FirebaseModelInterpreterOptions options = new FirebaseModelInterpreterOptions.Builder(localModel).build();
        try {
            interpreter = FirebaseModelInterpreter.getInstance(options);
        } catch (FirebaseMLException e) {
            e.printStackTrace();
        }


        try {
            inputOutputOptions = new FirebaseModelInputOutputOptions.Builder()
                    .setInputFormat(0, FirebaseModelDataType.FLOAT32, new int[]{10})
                    .setOutputFormat(0, FirebaseModelDataType.FLOAT32, new int[]{1,1})
                    .build();
        } catch (FirebaseMLException e) {
            e.printStackTrace();
        }

        float[] input = new float[10];

        canEstimatePrice = true;
        input[0] = Integer.parseInt(selectedBhk);
        input[1] = Integer.parseInt(selectedNoOfBathrooms);
        input[2] = Float.valueOf(selectedLivingArea)/1000;
        input[3] = Float.valueOf(sqFeetArea)/1000;
        input[4] = Integer.parseInt(selectedFloors);
        input[5] = Integer.parseInt(selectedNoOfBalconies);
        input[6] = Integer.parseInt(selectedCondition);
        input[7] = Integer.parseInt(selectedYeasOld);
        input[8] = 0;
        try {
            input[9] = localityID.get(localityTextView.getText().toString());
        }catch (NullPointerException e)
        {
            Toast.makeText(this, "Please enter locality properly", Toast.LENGTH_SHORT).show();
            canEstimatePrice = false;
        }


        if(canEstimatePrice)
        {
            try {
                FirebaseModelInputs modelInputs = new FirebaseModelInputs.Builder().add(input).build();

                interpreter.run(modelInputs,inputOutputOptions).addOnSuccessListener(new OnSuccessListener<FirebaseModelOutputs>() {
                    @Override
                    public void onSuccess(FirebaseModelOutputs firebaseModelOutputs) {

                        float[][] output = firebaseModelOutputs.getOutput(0);
                        finalPrice = output[0][0]*1000;
                        priceTextView.setText(String.format("₹ %.2f @ ₹ %.2f/Sq.Ft",finalPrice,finalPrice/Integer.parseInt(sqFeetArea)));

                        //Toast.makeText(MainActivity.this, "RESULT :: "+output[0][0] + "   SQFEET :: "+sqFeetArea, Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, "Task Failed...  "+"Exception : "+e.getMessage()
                                , Toast.LENGTH_LONG).show();
                    }
                });
            } catch (FirebaseMLException e) {
                e.printStackTrace();
            }
        }


    }
}
