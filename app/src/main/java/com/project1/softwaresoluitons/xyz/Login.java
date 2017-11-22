package com.project1.softwaresoluitons.xyz;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.L;

public class Login extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    //Signin button
    ProgressBar mProgressbarMed;
    public Button google_login,r_button,l_button;
    public EditText u_name,pswrd;
    //Signing Options
    private GoogleSignInOptions gso;
    public static int login_status;
    //google api client
    private static GoogleApiClient mGoogleApiClient;
    public ProgressDialog dialog;

    public RequestQueue queue;
    //Signin constant to check the activity result
    private int RC_SIGN_IN = 100;

    DatabaseReference databaseUser;

    List<User> userList;

    String user_id, name, email,contact, location;

    //TextViews

    //Image Loader
    private ImageLoader imageLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        mProgressbarMed = (ProgressBar)findViewById(R.id.progressBarMed);
        mProgressbarMed.setVisibility(View.GONE);

        userList = new ArrayList<>();

        //Initializing Views
        if(login_status==1){
            finish();
            startActivity(new Intent(this,HomeActivity.class));
        }

        //Initializing google signin option
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        //Initializing signinbutton
        queue = Volley.newRequestQueue(getApplicationContext());
        google_login = (Button) findViewById(R.id.google);
        r_button = (Button) findViewById(R.id.register);
        l_button = (Button) findViewById(R.id.login);
        u_name=(EditText)findViewById(R.id.textView);
        pswrd=(EditText)findViewById(R.id.textView2);
        //Initializing google api client
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .addOnConnectionFailedListener((GoogleApiClient.OnConnectionFailedListener) this)
                .build();


        mGoogleApiClient.connect();
        //Log.i("aa",mGoogleApiClient.isConnected()+"");

        //Setting onclick listener to signing button
        google_login.setOnClickListener(this);
        r_button.setOnClickListener(this);
        l_button.setOnClickListener(this);

    }


    //This function will option signing intent
    private void signIn() {

        //Creating an intent
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);

        //Starting intent for result
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    public static void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        // ...

                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //If signin
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            //Calling a new function to handle signin
            handleSignInResult(result);
        }
    }



    //After the signing we are calling this function
    private void handleSignInResult(GoogleSignInResult result) {
        //If the login succeed
        if (result.isSuccess()) {
            //Getting google account
            GoogleSignInAccount acct = result.getSignInAccount();
            String fname=acct.getDisplayName();
            String email=acct.getEmail();
            gf_login(fname,email);
        } else {
            //If login fails
            Toast.makeText(this, "Login Failed", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onClick(View v) {
        if (v == google_login) {
            signIn();
        }
        if(v==l_button){
            if(u_name.getText().toString().isEmpty() || pswrd.getText().toString().isEmpty()){
                Toast.makeText(this, "Field values should not be left empty", Toast.LENGTH_LONG).show();
            }
            else{
                login(u_name.getText().toString(),pswrd.getText().toString());
            }
        }
        if(v==r_button){
            Intent i=new Intent(this,register.class);
            startActivity(i);
        }
    }

    public void login(final String username, final String password){
        dialog = new ProgressDialog(Login.this);
        dialog.setMessage("Please wait !!");
        dialog.show();
        Log.i("Progress dialog: " , dialog.getProgress() + "");
        final FirebaseUser currentUser = mAuth.getCurrentUser();
        databaseUser = FirebaseDatabase.getInstance().getReference("user");

        databaseUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot userSnapshot : dataSnapshot.getChildren()){
                    User us = userSnapshot.getValue(User.class);
                    assert currentUser != null;

                    if(u_name.getText().toString().equals(us.getEmail())){
                        user_id = us.getId();
                        //Toast.makeText(Login.this,user_id+  "", Toast.LENGTH_SHORT).show();
                        name = us.getFname() + " " + us.getLname();
                        email = us.getEmail();
                        contact = us.getMobile();
                        location = us.getLocation();
                        SharedPreferences sp = getSharedPreferences("user",MODE_PRIVATE);
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putString("user_id",user_id);
                        editor.putString("name",name);
                        editor.putString("email",email);
                        editor.putString("contact",contact);
                        editor.putString("location",location);
                        editor.commit();
                        Toast.makeText(Login.this, "Successfully logged in!", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        mAuth.signInWithEmailAndPassword(username, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        final FirebaseUser currentUser = mAuth.getCurrentUser();
                        if (task.isSuccessful()) {
                            assert currentUser != null;
                            if(currentUser.isEmailVerified()) {
                                Toast.makeText(Login.this, "Successfully logged in", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                                startActivity(new Intent(Login.this, HomeActivity.class));
                                finish();
                            }
                            else{
                                Toast.makeText(Login.this, "Please verify your email!", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(Login.this, VerificationActivity.class));
                                finish();
                            }
                        } else {
                            dialog.dismiss();
                            Toast.makeText(Login.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        if (!task.isSuccessful()) {

                        }
                        //hideProgressDialog();
                    }
                });


    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    public void gf_login(final String name,final String email){

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.URL_ROOT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.i("response",response);
                        try {
                            JSONObject res = new JSONObject(response);

                            int first_time=res.getInt("first_time");
                            if(first_time==0){
                                SharedPreferences user=getSharedPreferences("user",MODE_PRIVATE);
                                SharedPreferences.Editor editor=user.edit();
                                int id=res.getInt("usr_id");
                                editor.putInt("usr_id",id);
                                editor.putString("name",name);
                                editor.putString("email",email);
                                editor.putString("contact",res.getString("contact"));
                                editor.putString("location",res.getString("location"));
                                editor.putInt("count",res.getInt("count"));
                                editor.commit();
                                Toast.makeText(Login.this,"Welcome !!",Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(Login.this,HomeActivity.class));
                                login_status=1;
                                finish();
                            }
                            else if(first_time==1){
                                Intent i=new Intent(Login.this,gf_login.class);
                                i.putExtra("name",name);
                                i.putExtra("email",email);
                                startActivity(i);

                            }
                        } catch (JSONException e) {
                            Toast.makeText(Login.this,"login failed",Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(Login.this,"login failed",Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("type","gf_login");
                params.put("fname",name);
                params.put("email",email);
                return params;
            }
        };
        queue.add(stringRequest);
    }

    @Override
    public void onStart() {
       super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            //Toast.makeText(this, "Status " + currentUser.isEmailVerified(), Toast.LENGTH_SHORT).show();
            //currentUser.sendEmailVerification();
            if (currentUser.isEmailVerified()) {
                Intent intent = new Intent(Login.this,HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                finish();
                startActivity(intent);
            }
        }
    }
}