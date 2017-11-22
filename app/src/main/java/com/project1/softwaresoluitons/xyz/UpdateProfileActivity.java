package com.project1.softwaresoluitons.xyz;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static com.project1.softwaresoluitons.xyz.R.drawable.user;

public class UpdateProfileActivity extends AppCompatActivity {

    EditText location, mobile;
    Button submit;
    DatabaseReference database;
    String id;
    TextView name;
    DatabaseReference databaseUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        mobile = (EditText)findViewById(R.id.mobile1);
        location = (EditText) findViewById(R.id.location1);
        name = (TextView)findViewById(R.id.name1);

        SharedPreferences user = getSharedPreferences("user", Context.MODE_PRIVATE);
        mobile.setText(user.getString("contact"," "));
        name.setText(user.getString("name"," "));
        location.setText(user.getString("location"," "));
        id = user.getString("user_id","");

        submit = (Button)findViewById(R.id.btSubmitChange);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProfile(mobile.getText().toString(),location.getText().toString());
            }
        });

    }


    public void updateProfile(final String mobile, final String location){

        Log.i("check id: " , id + "");

        if(id.isEmpty()){
            return;
        }

        databaseUser = FirebaseDatabase.getInstance().getReference("user");
        databaseUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot Snapshot : dataSnapshot.getChildren()) {
                    User us = Snapshot.getValue(User.class);
                    if(us.getId().equals(id)){
                        database = databaseUser.child(us.getId());
                        us.location = location;
                        us.mobile =  mobile;
                        database.setValue(us);
                        SharedPreferences sp = getSharedPreferences("user",MODE_PRIVATE);
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putString("contact",mobile);
                        editor.putString("location",location);
                        editor.commit();
                        Toast.makeText(UpdateProfileActivity.this, "Details updated successfully!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(UpdateProfileActivity.this,HomeActivity.class));
                        finish();
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
