package com.project1.softwaresoluitons.xyz;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Path;
import android.graphics.Rect;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class training_detail extends AppCompatActivity implements View.OnClickListener {
    public TextView key_learning10,key_learning20,trainer10,trainer20,time_venue10,time_venue20,time_venue30,time_venue40,pre10;
    String id,title,k_l1,k_l2,k_l3,price,mobile_no,name,venue,category,available,from,to,date,description,pre,duration;

    public ProgressDialog dialog;
    public RequestQueue queue;
    public Bitmap b;
    String trainerId;
    String trainingTitle;
    public ImageView img;
    public CollapsingToolbarLayout collapsingToolbar;
    public static ArrayList<training_detail_item> items;
    public RecyclerView recyclerView;
    public Button register,register1;
    public static adapter adapter;
    public static int status;
    private FirebaseAuth mAuth;
    String training_title;
    String user_id;
    int flag;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        flag=0;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.all_trainings_detail);
        items=new ArrayList<>();
        Intent i=getIntent();
        id = i.getStringExtra("training_id");
        training_title = i.getStringExtra("training_title");

        SharedPreferences sp = getSharedPreferences("user",MODE_PRIVATE);
        user_id = sp.getString("user_id",null);

        img=(ImageView)findViewById(R.id.header);
        queue = Volley.newRequestQueue(getApplicationContext());

        Log.i("training_id11"," "+id+" "+trainingTitle);
        Toolbar toolbar = (Toolbar) findViewById(R.id.anim_toolbar);
        toolbar.setTitle(training_title);


        collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        //collapsingToolbar.setTitle(training_title);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        recyclerView=(RecyclerView)findViewById(R.id.rview);
        register=(Button)findViewById(R.id.register);
        register.setOnClickListener(this);
        register1=(Button)findViewById(R.id.register1);
        register1.setOnClickListener(this);
        register.setVisibility(View.VISIBLE);
        register1.setVisibility(View.VISIBLE);
        int u=i.getIntExtra("calling_activity",0);
        /*    if(u==0){
            register.setVisibility(View.GONE);
            register1.setVisibility(View.GONE);
        }*/
        fetch_trainings();

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (status == 3) {
            register.setVisibility(View.GONE);
            register1.setText("Created Training");
            int whiteColor = Color.parseColor("#FFFFFF");
            int greenColor = Color.parseColor("#99cc00");
            register1.setBackgroundColor(greenColor);
            register1.setTextColor(whiteColor);
            register1.setEnabled(false);
        } else if (status == 2) {
            register.setVisibility(View.GONE);
            register1.setText("Already Registered");
            int whiteColor = Color.parseColor("#FFFFFF");
            int greenColor = Color.parseColor("#99cc00");
            register1.setBackgroundColor(greenColor);
            register1.setTextColor(whiteColor);
            register1.setEnabled(false);
        } else if (status == 1) {
            register.setText("Already Enquired");
            int whiteColor = Color.parseColor("#FFFFFF");
            int greenColor = Color.parseColor("#99cc00");
            register.setBackgroundColor(greenColor);
            register.setTextColor(whiteColor);
            register.setEnabled(false);
        }
    }

    @Override
    public void onClick(View v) {
        if(v==register){
            Intent i=new Intent(this,enquiry.class);
            i.putExtra("training_id",id);
            if(!trainerId.equals(null)) {
                i.putExtra("trainer_id", trainerId);
                i.putExtra("training_title",trainingTitle);
                startActivity(i);
            }

        }
        else if(v==register1){
            if(flag == 0) {
                Intent i = new Intent(this, amount.class);
                i.putExtra("price", price);
                i.putExtra("training_id", id);
                if (!trainerId.equals(null)) {
                    i.putExtra("trainer_id", trainerId);
                    i.putExtra("training_title", trainingTitle);
                    startActivity(i);
                }
            }
            else{
                remove_trainee();
            }
        }
    }

    public void remove_trainee(){
        dialog = new ProgressDialog(training_detail.this);
        dialog.setMessage("Please wait !!");
        dialog.show();
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("registration");
        //Query applesQuery = databaseReference.child("registration").orderByChild("title").equalTo("Apple");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Registration reg = snapshot.getValue(Registration.class);
                    if(reg.getTraineeId().equals(user_id) && reg.getTrainingId().equals(id)){
                        databaseReference.child(snapshot.getKey()).removeValue();
                    }
                }
                dialog.dismiss();
                /*register1.setText("Register");
                register1.setBackgroundResource(android.R.drawable.btn_default);
                register1.setEnabled(true);*/
                startActivity(new Intent(training_detail.this,HomeActivity.class));
                finish();
                Toast.makeText(training_detail.this, "Cancellation Successful", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void check_registered(){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("registration");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Registration reg = snapshot.getValue(Registration.class);
                    if(reg.getTrainingId().equals(id) && reg.getTraineeId().equals(user_id)){
                        register1.setText("Cancel Registration");
                        int whiteColor = Color.parseColor("#FFFFFF");
                        int greenColor = Color.parseColor("#99cc00");
                        register1.setBackgroundColor(greenColor);
                        register1.setTextColor(whiteColor);
                        flag=1;
                        register1.setEnabled(true);
                        break;
                    }
                }
                dialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void fetch_trainings(){
        dialog = new ProgressDialog(training_detail.this);
        dialog.setMessage("Please wait !!");
        dialog.show();
        DatabaseReference databaseTrainings = FirebaseDatabase.getInstance().getReference("Trainings");

        databaseTrainings.addValueEventListener(new ValueEventListener() {


            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                items.clear();
                for(DataSnapshot Snapshot : dataSnapshot.getChildren()){
                    Training categ = Snapshot.getValue(Training.class);
                    if(categ.getId().equals(id)) {
                        if (categ.getKeyLearining1().equals("") && categ.getKeyLearining2().equals("") && categ.getKeyLearining3().equals("")) {
                            items.add(new training_detail_item("Key Learnings", "null"));
                        } else {
                            items.add(new training_detail_item("Key Learnings", "1. " + categ.getKeyLearining1() + "\n2. " + categ.getKeyLearining2() + "\n3. " + categ.getKeyLearining3()));
                        }
                        if(categ.getUserId().equals(user_id)){
                            register1.setVisibility(View.INVISIBLE);
                            register.setVisibility(View.INVISIBLE);
                        }


                        items.add(new training_detail_item("Description", categ.getDescription()));
                        items.add(new training_detail_item("Fee(Rs.)/month",categ.getPrice()));
                        items.add(new training_detail_item("Contact", "8880390936"));
                        items.add(new training_detail_item("Mobile No", "9828017751"));
                        items.add(new training_detail_item("Availability", categ.getAvailability()));
                        items.add(new training_detail_item("Date", categ.getDate()));
                        items.add(new training_detail_item("Duration hrs/day", categ.getDuration()));
                        items.add(new training_detail_item("Venue", categ.getLocation()));
                        price = categ.getPrice();
                        trainerId = categ.getUserId();
                        trainingTitle = categ.getName();
                        //items.add(new training_detail_item("Key Learning","1. " + categ.getKeyLearining1() +"\n"+"2. "+categ.getKeyLearining2()+"\n"+"3. "+categ.getKeyLearining3()));
                        dialog.dismiss();
                        collapsingToolbar.setTitle(title);
                    }
                }


                adapter=new adapter(training_detail.this,items);

                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(training_detail.this));

                check_registered();
                //ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(training_detail.this,android.R.layout.simple_spinner_item,categories);
                //dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                //category.setAdapter(dataAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}

class adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    ArrayList<training_detail_item> trainings;
    Context context;
    LayoutInflater l;

    adapter(Context c, ArrayList<training_detail_item> t) {
        this.context = c;
        this.trainings = t;
        l=LayoutInflater.from(context);
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = l.inflate(R.layout.training_detail_item, parent,false);
        holder vh = new holder(v);
        return vh;

    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder1, int position) {
        holder holder=(holder)holder1;
        holder.title.setText(trainings.get(position).title);
        if(!trainings.get(position).description.equals("null")){
            holder.description.setText(trainings.get(position).description);
        }

    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return trainings.size();
    }

    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }
    public Bitmap getRoundedShape(Bitmap scaleBitmapImage) {
        int targetWidth = 120;
        int targetHeight = 120;
        Bitmap targetBitmap = Bitmap.createBitmap(targetWidth,
                targetHeight,Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(targetBitmap);
        Path path = new Path();
        path.addCircle(((float) targetWidth - 1) / 2,
                ((float) targetHeight - 1) / 2,
                (Math.min(((float) targetWidth),
                        ((float) targetHeight)) / 2),
                Path.Direction.CCW);

        canvas.clipPath(path);
        Bitmap sourceBitmap = scaleBitmapImage;
        canvas.drawBitmap(sourceBitmap,
                new Rect(0, 0, sourceBitmap.getWidth(),
                        sourceBitmap.getHeight()),
                new Rect(0, 0, targetWidth, targetHeight), null);
        return targetBitmap;
    }



}

class holder extends RecyclerView.ViewHolder{

    TextView title;
    TextView description;


    holder(View v) {
        super(v);
        title = (TextView) v.findViewById(R.id.title);
        description = (TextView) v.findViewById(R.id.description);
    }
}

class training_detail_item{
    String title;
    String description;
    training_detail_item(String t,String t1){
        title=t;
        description=t1;
    }
}

