package com.project1.softwaresoluitons.xyz;

import android.*;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.List;

import static android.R.attr.data;
import static com.project1.softwaresoluitons.xyz.R.drawable.map1;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener,
        OnMapReadyCallback, LocationListener {

    public Button profile_button;
    public Button trainings_button;
    List<Address> addresses = null;
    Geocoder geocoder;
    TextView tvEmail, tvName;
    LinearLayout mainLayout;
    String driverEmail;
    double lat, lon;
    LatLng latLng;
    LocationManager locationManager;
    Marker marker;
    MarkerOptions markerOptions;
    String addressText;
    DatabaseReference databaseTraining;
    private FirebaseAuth mAuth;
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mAuth = FirebaseAuth.getInstance();

        geocoder = new Geocoder(getApplicationContext());

        FirebaseUser currentUser = mAuth.getCurrentUser();

        SharedPreferences sh = getSharedPreferences("user", MODE_PRIVATE);
        int count = sh.getInt("count", 0);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Home");

        View v = findViewById(R.id.app_bar_home);
        v = v.findViewById(R.id.content_home);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        Log.i("notify", navigationView.toString());
        View headerLayout = navigationView.getHeaderView(0);
        tvEmail = (TextView) headerLayout.findViewById(R.id.textView);
        tvEmail.setText(currentUser.getEmail());
        tvName = (TextView) headerLayout.findViewById(R.id.username);
        tvName.setText(sh.getString("name", "SkillBook"));
        navigationView.setNavigationItemSelectedListener(this);
        profile_button = (Button) v.findViewById(R.id.pro_button);
        trainings_button = (Button) v.findViewById(R.id.train_button);
        trainings_button.setOnClickListener(this);
        profile_button.setOnClickListener(this);

        fetchLocations();
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();


        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.create_training) {
            startActivity(new Intent(this, crt_training.class));
        } else if (id == R.id.notifications) {
            startActivity(new Intent(this, Notifications.class));
        } else if (id == R.id.logout) {
            //Login.signOut();
            mAuth.signOut();
            finish();
            // Login.login_status=0;
            startActivity(new Intent(this, Login.class));

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onClick(View v) {
        if (v == profile_button) {
            startActivity(new Intent(this, MainActivity.class));
        } else if (v == trainings_button) {
            startActivity(new Intent(this, TrainingCardActivity.class));
        }
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

    public void fetchLocations() {
        Log.i("check : " , "inside fetch");

        //mMap.clear();

        databaseTraining = FirebaseDatabase.getInstance().getReference("Trainings");
        databaseTraining.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Training tr = snapshot.getValue(Training.class);

                    String location = tr.getLocation();
                    String trainingTitle = tr.getName();

                    //Check whether the network provider is enabled
                    Log.i("Location:", " "+ location);

                    if (ActivityCompat.checkSelfPermission(HomeActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                            ActivityCompat.checkSelfPermission(HomeActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                        return;
                    }

                    if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {

                        try {
                            // Getting a maximum of 3 Address that matches the input
                            // text
                            addresses = geocoder.getFromLocationName(location, 1);
                            if (addresses != null && !addresses.equals(""))
                                search(addresses , trainingTitle);

                        } catch (Exception e) {

                        }
                    } else if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                        try {
                            // Getting a maximum of 3 Address that matches the input
                            // text
                            addresses = geocoder.getFromLocationName(location, 1);
                            if (addresses != null && !addresses.equals(""))
                                search(addresses , trainingTitle);

                        } catch (Exception e) {

                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    protected void search(List<Address> addresses, String trainingTitle) {


        Address address = addresses.get(0);
        lon = address.getLongitude();
        lat = address.getLatitude();

        Log.i("check lat-lon : " , lon + " " + lat);
        latLng = new LatLng(lat, lon);

        //addressText = String.format("%s, %s", address.getMaxAddressLineIndex() > 0 ? address.
              //  getAddressLine(0) : "", address.getCountryName());

        //markerOptions = new MarkerOptions();

        //markerOptions.position(latLng);
        //markerOptions.title(lat + " , " + lon);

        mMap.addMarker(new MarkerOptions().position(latLng).title(trainingTitle));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10.2f));

        /*
        //mMap.clear();
        mMap.addMarker(markerOptions);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
        //locationTv.setText("Latitude:" + address.getLatitude() + ", Longitude:"+ address.getLongitude());
        */

    }
}
