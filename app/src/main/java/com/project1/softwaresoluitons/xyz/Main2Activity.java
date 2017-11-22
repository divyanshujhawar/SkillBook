package com.project1.softwaresoluitons.xyz;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Rect;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Main2Activity extends AppCompatActivity {
    public ArrayList<item> trainings, items;
    public ArrayList<Training> currentTraining;
    public ProgressDialog dialog;
    public RequestQueue queue;
    public RecyclerView recyclerView;
    public myadapter adapter;
    public static String cat_nam;
    public ArrayList<item> sorted_by_category;
    public ArrayList<String> categories;
    private FirebaseAuth mAuth;
    FirebaseUser currentUser;
    String trainingTitle;
    DatabaseReference databaseUser;
    String mobile;
    int pos;
    DatabaseReference databaseTraining;
    DatabaseReference databaseCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_reg_train);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        assert currentUser != null;
        String contact = currentUser.getEmail();

        trainings = new ArrayList<item>();
        categories = new ArrayList<>();
        currentTraining = new ArrayList<>();
        queue = Volley.newRequestQueue(getApplicationContext());
        recyclerView = (RecyclerView) findViewById(R.id.reg_train);
        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(this, recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Intent i = new Intent(Main2Activity.this, training_detail.class);
                        pos = position;
                        i.putExtra("training_title",items.get(position).getTitle());
                        i.putExtra("training_id", items.get(position).getId());
                        i.putExtra("calling_activity", 1);
                        Log.i("training_id", items.get(position).id + "");
                        startActivity(i);
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {
                        Intent i = new Intent(Main2Activity.this, training_detail.class);
                        i.putExtra("training_id", items.get(position).id);
                        i.putExtra("calling_activity", 1);
                        startActivity(i);
                    }
                }));
        getSupportActionBar().setTitle("Search Trainings");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.spacing);
        recyclerView.addItemDecoration(new SpacesItemDecoration(spacingInPixels));
        fetch_trainings();
        //registerForContextMenu(recyclerView);
    }

/*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);

        final MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(Main2Activity.this);

        return true;
    }
*/

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                if (data == null) {
                    Toast.makeText(this, "No category choosen", Toast.LENGTH_LONG);
                } else {
                    String c = data.getStringExtra("category");
                    sorted_by_category = new ArrayList<>();
                    for (int i = 0; i < trainings.size(); i++) {
                        if (trainings.get(i).category.equals(c)) {
                            sorted_by_category.add(trainings.get(i));
                        }
                    }
                    items = sorted_by_category;
                    adapter.notifyDataSetChanged();
                }
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; go home
                finish();
                Intent intent = new Intent(this, HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;
            case R.id.srt_by_price:
                // app icon in action bar clicked; go home
                Collections.sort(items, new Comparator<item>() {
                    @Override
                    public int compare(item lhs, item rhs) {
                        Float l = Float.parseFloat(lhs.price);
                        Float r = Float.parseFloat(rhs.price);
                        return l.compareTo(r);
                    }
                });
                adapter.notifyDataSetChanged();
                Toast.makeText(this, "Highest price first", Toast.LENGTH_LONG);
                return true;
            case R.id.srt_by_name:
                // app icon in action bar clicked; go home
                Collections.sort(items, new Comparator<item>() {
                    @Override
                    public int compare(item lhs, item rhs) {
                        return lhs.title.toLowerCase().compareTo(rhs.title.toLowerCase());
                    }
                });
                adapter.notifyDataSetChanged();
                return true;
            case R.id.srt_by_category:
                // app icon in action bar clicked; go home
                fetch_categories();
                return true;
            case R.id.all_trainings:
                // app icon in action bar clicked; go home
                items = trainings;
                adapter.notifyDataSetChanged();
                Toast.makeText(this, "Highest price first", Toast.LENGTH_LONG);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }



    public void fetch_categories() {
        databaseCategory = FirebaseDatabase.getInstance().getReference("Category");
        databaseCategory.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot catSnapshot : dataSnapshot.getChildren()) {
                    Category cat = catSnapshot.getValue(Category.class);
                    categories.add(cat.getName());
                }

                String[] cat = new String[categories.size()];
                cat = categories.toArray(cat);
                Bundle b = new Bundle();
                b.putStringArray("categories", cat);
                Intent intent = new Intent(Main2Activity.this, sel_cat.class);
                intent.putExtras(b);
                startActivityForResult(intent, 1);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void fetch_trainings() {
        dialog = new ProgressDialog(Main2Activity.this);
        dialog.setMessage("Please Wait");
        dialog.show();
        databaseTraining = FirebaseDatabase.getInstance().getReference("Trainings");
        databaseTraining.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                trainings.clear();
                for (DataSnapshot trainSnapshot : dataSnapshot.getChildren()) {
                    Training tr = trainSnapshot.getValue(Training.class);
                    FirebaseUser currentUser = mAuth.getCurrentUser();
                    assert currentUser != null;
                    String contact = currentUser.getEmail();
                    Training train = new Training(tr.getId(), tr.getName(), tr.getUserId(), tr.getLocation(), tr.getPrice(),
                            tr.getDuration(), tr.getDescription(), tr.getCategory(), tr.getAvailability(), tr.getKeyLearining1(),
                            tr.getKeyLearining2(), tr.getKeyLearining3(), tr.getDate());
                    currentTraining.add(train);
                    Bitmap b = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
                    item it = new item(tr.getId(), tr.getName(), tr.getLocation(), tr.getPrice(), b, tr.getCategory());
                    trainings.add(it);
                }
                dialog.dismiss();

                adapter = new myadapter(getApplicationContext(), trainings);
                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 2));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    class myadapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        ArrayList<item> trainings;
        Context context;
        LayoutInflater l;

        myadapter(Context c, ArrayList<item> t) {
            this.context = c;
            this.trainings = t;
            items = new ArrayList<item>();
            items = t;
            l = LayoutInflater.from(context);
        }


        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View v = l.inflate(R.layout.training_item, null, false);
            viewHolder vh = new viewHolder(v);
            return vh;

        }

        /**
         * This will be invoked when an item in the listview is long pressed
         */


        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder1, int position) {
            viewHolder holder = (viewHolder) holder1;
            holder.title.setText(items.get(position).title);

            holder.price.setText("Rs. " + items.get(position).price);
            Bitmap y = getRoundedShape(items.get(position).b);
            holder.img.setImageBitmap(y);
            holder.location.setText(items.get(position).location);
            holder.contact.setText(items.get(position).contact);

        }


        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getItemCount() {
            return items.size();
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
                    targetHeight, Bitmap.Config.ARGB_8888);

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
}


