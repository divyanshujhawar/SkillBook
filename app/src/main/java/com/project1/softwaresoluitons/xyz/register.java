package com.project1.softwaresoluitons.xyz;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class register extends AppCompatActivity implements View.OnClickListener{
    public EditText f_name1,l_name1,location1,mobile1,email1,pswrd1,cpswrd1;
    public Button register;
    public ProgressDialog dialog;
    public RequestQueue queue;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    DatabaseReference databaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        queue = Volley.newRequestQueue(getApplicationContext());
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        f_name1=(EditText)findViewById(R.id.f_name1);
        l_name1=(EditText)findViewById(R.id.l_name1);
        location1=(EditText)findViewById(R.id.location1);
        mobile1=(EditText)findViewById(R.id.mobile1);
        email1=(EditText)findViewById(R.id.email1);
        pswrd1=(EditText)findViewById(R.id.pswrd1);
        cpswrd1=(EditText)findViewById(R.id.cpswrd1);
        register=(Button)findViewById(R.id.reg);
        register.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v==register){
            if(f_name1.getText()==null|| l_name1.getText()==null ||location1.getText()==null ||mobile1.getText()==null
                    ||pswrd1.getText()==null ||cpswrd1.getText()==null || email1.getText()==null  ){
                Toast.makeText(this, "Field values should not be left empty", Toast.LENGTH_LONG).show();
            }
            else if(!pswrd1.getText().toString().equals(cpswrd1.getText().toString())){
                Toast.makeText(this, "Confirmed password do not match", Toast.LENGTH_LONG).show();
            }
            else{
                register(f_name1.getText().toString(),l_name1.getText().toString(),location1.getText().toString(),
                        mobile1.getText().toString(),email1.getText().toString(),pswrd1.getText().toString());
            }
        }
    }
    public void register(final String fname,final String lname,final String location,final String mobile,final String email, final String password){

        dialog = new ProgressDialog(this);
        dialog.setMessage("Please wait !!");
        dialog.show();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(register.this, "User successfully registered", Toast.LENGTH_SHORT).show();
                            addUser(fname,lname,location,mobile,email);
                            startActivity(new Intent(register.this, VerificationActivity.class));
                            finish();
                        } else {
                            //Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(register.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }
                    }
                });

    }

    public void addUser(String fname, String lname, String location, String mobile, String email){
        databaseUser = FirebaseDatabase.getInstance().getReference("user");

        String id = databaseUser.push().getKey();
        User user = new User(fname,lname,location,mobile,email,id);
        databaseUser.child(id).setValue(user);

    }

}
