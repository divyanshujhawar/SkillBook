package com.project1.softwaresoluitons.xyz;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class crt_training extends Activity implements View.OnClickListener{
    private EditText  name,location,price,duration,description,kl1,kl2,kl3,date;
    private Button crt_training;
    private Spinner category,availability;
    public ProgressDialog dialog;
    public RequestQueue queue;
    public CollapsingToolbarLayout collapsingToolbar;
    public ArrayList<String> categories;
    List<Category> categoryList;
    private FirebaseAuth mAuth;

    DatabaseReference databaseCategory;
    DatabaseReference databaseTraining;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crt_training);

        categoryList = new ArrayList<>();

        queue= Volley.newRequestQueue(getApplicationContext());
        name=(EditText)findViewById(R.id.name1);
        location=(EditText)findViewById(R.id.location1);
        price=(EditText)findViewById(R.id.price1);
        duration=(EditText)findViewById(R.id.duration1);
        description=(EditText)findViewById(R.id.description1);
        crt_training=(Button)findViewById(R.id.rgstr);
        category=(Spinner)findViewById(R.id.category1);
        kl1=(EditText)findViewById(R.id.kl1_1);
        kl2=(EditText)findViewById(R.id.kl2_1);
        kl3=(EditText)findViewById(R.id.kl3_1);
        date=(EditText)findViewById(R.id.date_1);
        availability=(Spinner)findViewById(R.id.availability1);
        crt_training.setOnClickListener(this);
        collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle("Create Training");
        categories=new ArrayList<>();
        Toolbar toolbar = (Toolbar) findViewById(R.id.anim_toolbar);
        toolbar.setNavigationIcon(ContextCompat.getDrawable(this,R.drawable.back_arrow));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                crt_training.this.finish();
                startActivity(new Intent(crt_training.this,HomeActivity.class));
            }
        });
        String[] availabilities=getResources().getStringArray(R.array.availability);
        ArrayAdapter<String> dataAdapter1 = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, availabilities);
        dataAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        availability.setAdapter(dataAdapter1);
        fetch_categories();
    }


    @Override
    public void onClick(View v) {
        if(v==crt_training){
            if(name.getText().toString().equals("")||location.getText().toString().equals("")||price.getText().toString().equals("")
                    ||duration.getText().toString().equals("")||description.getText().toString().equals("")||category.getSelectedItem().toString().equals("")||
                    availability.getSelectedItem().toString().equals("")||date.getText().toString().equals("")){
                //do nothing;
                Toast.makeText(this,"only optional fields can be left empty",Toast.LENGTH_LONG).show();
            }
            else{
                Date d = null;
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/mm/yyyy");
                    d = sdf.parse(date.getText().toString());
                    if (!date.getText().toString().equals(sdf.format(d))) {
                        d = null;
                    }
                } catch (ParseException ex) {
                    ex.printStackTrace();
                }
                if (d == null) {
                    Toast.makeText(this,"Date format is invalid ",Toast.LENGTH_LONG).show();
                } else {
                    crt_training();
                }

            }
        }
    }

    public void fetch_categories(){
        databaseCategory = FirebaseDatabase.getInstance().getReference("Category");
        databaseCategory.addValueEventListener(new ValueEventListener() {


            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot Snapshot : dataSnapshot.getChildren()){
                    Category categ = Snapshot.getValue(Category.class);
                    categoryList.add(categ);
                    categories.add(categ.getName());
                    //Toast.makeText(crt_training.this, categ.getName() + "", Toast.LENGTH_SHORT).show();
                }

                ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(crt_training.this,android.R.layout.simple_spinner_item,categories);
                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                category.setAdapter(dataAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void crt_training() {

        databaseTraining = FirebaseDatabase.getInstance().getReference("Trainings");
        SharedPreferences sp = getSharedPreferences("user",MODE_PRIVATE);

        String id = databaseTraining.push().getKey();
        String tname = name.getText().toString();
        String tuser_id = sp.getString("user_id",null);
        Log.i("users id",tuser_id + " is user id");
        String tlocation = location.getText().toString();
        String tprice = price.getText().toString();
        String tduration = duration.getText().toString();
        String tdescription = description.getText().toString();
        String tcategory = category.getSelectedItem().toString();
        String tavailability = availability.getSelectedItem().toString();
        String tkl1 = kl1.getText().toString();
        String tkl2 = kl2.getText().toString();
        String tkl3 = kl3.getText().toString();
        String tdate = date.getText().toString();
        Training train = new Training(id , tname, tuser_id, tlocation, tprice, tduration, tdescription, tcategory, tavailability, tkl1,
                tkl2, tkl3, tdate);
        databaseTraining.child(id).setValue(train);

        Toast.makeText(this, "Training created!", Toast.LENGTH_SHORT).show();

        finish();

        startActivity(new Intent(crt_training.this,HomeActivity.class));
    }
}


