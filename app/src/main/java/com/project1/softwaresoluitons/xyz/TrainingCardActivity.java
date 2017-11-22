package com.project1.softwaresoluitons.xyz;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class TrainingCardActivity extends AppCompatActivity implements SearchView.OnQueryTextListener{


    private RecyclerView recyclerView;
    private AlbumsAdapter adapter;
    private static ArrayList<item> albumLi;
    private static List<Album> albumList1;
    public static ArrayList<Album> trainings,items;
    public static ArrayList<Training> currentTraining;
    public ProgressDialog dialog;
    public RequestQueue queue;
    public static String cat_nam;
    public ArrayList<Album> sorted_by_category;
    public ArrayList<String> categories;
    private FirebaseAuth mAuth;
    FirebaseUser currentUser;
    DatabaseReference databaseUser;
    String mobile;
    int pos;
    DatabaseReference databaseTraining;
    DatabaseReference databaseCategory;

    @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training_card);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        assert currentUser != null;
        String contact = currentUser.getEmail();

        trainings=new ArrayList<Album>();
        categories=new ArrayList<>();
        currentTraining = new ArrayList<>();

        initCollapsingToolbar();

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        trainings = new ArrayList<>();


        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(this, recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Intent i = new Intent(TrainingCardActivity.this, training_detail.class);
                        pos = position;
                        i.putExtra("training_title",items.get(position).getName());
                        i.putExtra("training_id", items.get(position).getId());
                        i.putExtra("calling_activity", 1);
                        Log.i("training_id", items.get(position).getId() + "");
                        startActivity(i);
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {
                        Intent i = new Intent(TrainingCardActivity.this, training_detail.class);
                        i.putExtra("training_id", items.get(position).getId());
                        i.putExtra("training_title",items.get(position).getName());
                        i.putExtra("calling_activity", 1);
                        startActivity(i);
                    }
                }));

        fetch_trainings();

        try {
            Glide.with(this).load(R.drawable.back33).into((ImageView) findViewById(R.id.backdrop));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if(resultCode == RESULT_OK) {
                if (data == null) {
                    Toast.makeText(this, "No category choosen", Toast.LENGTH_LONG);
                } else {
                    String c = data.getStringExtra("category");
                    sorted_by_category=new ArrayList<>();
                    for(int i=0;i<trainings.size();i++){
                        if(trainings.get(i).getCategory().equals(c)){
                            sorted_by_category.add(trainings.get(i));
                        }
                    }
                    items=sorted_by_category;
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
                Collections.sort(items, new Comparator<Album>() {
                    @Override
                    public int compare(Album lhs, Album rhs) {
                        Float l=Float.parseFloat(lhs.getPrice());
                        Float r=Float.parseFloat(rhs.getPrice());
                        return l.compareTo(r);
                    }
                });
                adapter.notifyDataSetChanged();
                Toast.makeText(this,"Highest price first",Toast.LENGTH_LONG);
                return true;
            case R.id.srt_by_name:
                // app icon in action bar clicked; go home
                Collections.sort(items, new Comparator<Album>() {
                    @Override
                    public int compare(Album lhs, Album rhs) {
                        return lhs.getName().toLowerCase().compareTo(rhs.getName().toLowerCase());
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
                items=trainings;
                adapter.notifyDataSetChanged();
                Toast.makeText(this,"Highest price first",Toast.LENGTH_LONG);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);

        final MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(TrainingCardActivity.this);

        return true;
    }


    private void initCollapsingToolbar() {
        final CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(" ");
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appbar);
        appBarLayout.setExpanded(true);

        // hiding & showing the title when toolbar expanded & collapsed
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbar.setTitle(getString(R.string.app_name));
                    isShow = true;
                } else if (isShow) {
                    collapsingToolbar.setTitle(" ");
                    isShow = false;
                }
            }
        });
    }


    public void fetch_categories(){
        databaseCategory = FirebaseDatabase.getInstance().getReference("Category");
        databaseCategory.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot catSnapshot : dataSnapshot.getChildren()){
                    Category cat = catSnapshot.getValue(Category.class);
                    categories.add(cat.getName());
                }

                String[] cat = new String[categories.size()];
                cat = categories.toArray(cat);
                Bundle b = new Bundle();
                b.putStringArray("categories",cat);
                Intent intent = new Intent(TrainingCardActivity.this,sel_cat.class);
                intent.putExtras(b);
                startActivityForResult(intent,1);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void fetch_trainings() {

        final int[] covers = new int[]{
                R.drawable.others,
                R.drawable.computer,
                R.drawable.cooking,
                R.drawable.language,
                R.drawable.singing,
                R.drawable.sports,
                R.drawable.science,
                R.drawable.meditation,
                R.drawable.academics};

        dialog = new ProgressDialog(TrainingCardActivity.this);
        dialog.setMessage("Please Wait");
        dialog.show();
        databaseTraining = FirebaseDatabase.getInstance().getReference("Trainings");
        databaseTraining.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                trainings.clear();
                int temp = covers[0];
                for(DataSnapshot trainSnapshot : dataSnapshot.getChildren()){
                    Training tr = trainSnapshot.getValue(Training.class);
                    FirebaseUser currentUser = mAuth.getCurrentUser();
                    assert currentUser != null;
                    if(tr.getCategory().equals("Others")){
                        temp = covers[0];
                    }
                    else if(tr.getCategory().equals("Computer")){
                        temp = covers[1];
                    }
                    else if(tr.getCategory().equals("Cooking")){
                        temp = covers[2];
                    }
                    else if(tr.getCategory().equals("Language")){
                        temp = covers[3];
                    }
                    else if(tr.getCategory().equals("Singing and Dancing")){
                        temp = covers[4];
                    }
                    else if(tr.getCategory().equals("Sports")){
                        temp = covers[5];
                    }
                    else if(tr.getCategory().equals("Science")){
                        temp = covers[6];
                    }
                    else if(tr.getCategory().equals("Mind, Soul and Body")){
                        temp = covers[7];
                    }
                    else{
                        temp = covers[8];
                    }
                    Album a = new Album(tr.getId(),tr.getCategory(),tr.getName(),tr.getLocation(),temp,tr.getPrice());
                    trainings.add(a);
                }
                dialog.dismiss();
                adapter = new AlbumsAdapter(TrainingCardActivity.this, trainings);
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        adapter.filter(query);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String query) {
        adapter.filter(query);
        return true;
    }

    /**
     * RecyclerView item decoration - give equal margin around grid item
     */
    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }

    /**
     * Converting dp to pixel
     */
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }
}
