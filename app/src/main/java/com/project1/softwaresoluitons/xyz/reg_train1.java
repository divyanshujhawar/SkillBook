package com.project1.softwaresoluitons.xyz;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link reg_train1.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link reg_train1#newInstance} factory method to
 * create an instance of this fragment.
 */
public class reg_train1 extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    String user_id;
    public static Context context;
    public ArrayList<String> training_ids;
    DatabaseReference databaseTraining;
    DatabaseReference databaseRegistration;
    public ArrayList<item> trainings;
    public ArrayList<String> r_trainings;
    public ProgressDialog dialog;
    public RequestQueue queue;
    public RecyclerView recyclerView;
    myAdapter adapter;
    private FirebaseAuth mAuth;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public reg_train1() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment reg_train1.
     */
    // TODO: Rename and change types and number of parameters
    public static reg_train1 newInstance(String param1, String param2) {
        reg_train1 fragment = new reg_train1();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        trainings = new ArrayList<item>();
        r_trainings = new ArrayList<>();
        queue = Volley.newRequestQueue(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_reg_train, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SharedPreferences sp = getActivity().getSharedPreferences("user",Context.MODE_PRIVATE);
        user_id = sp.getString("user_id",null);
        recyclerView = (RecyclerView) view.findViewById(R.id.reg_train);
        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(context, recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Intent i = new Intent(context, training_detail.class);
                        i.putExtra("training_title",trainings.get(position).getTitle());
                        i.putExtra("training_id", trainings.get(position).id);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(i);
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {
                        Intent i = new Intent(context, training_detail.class);
                        i.putExtra("training_id", trainings.get(position).id);
                        i.putExtra("training_title",trainings.get(position).getTitle());
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(i);
                    }
                }));
        context = getActivity().getApplicationContext();
        training_ids = new ArrayList<String>();
        adapter = new myAdapter(getContext(),trainings);

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(),2));
        fetch_trainings();
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.spacing);
        recyclerView.addItemDecoration(new SpacesItemDecoration(spacingInPixels));
        super.onViewCreated(view, savedInstanceState);



    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void fetch_trainings(){
        dialog = new ProgressDialog(getActivity());
        dialog.setMessage("Please wait !!");
        dialog.show();
        databaseRegistration = FirebaseDatabase.getInstance().getReference("registration");

        r_trainings.clear();

        databaseRegistration.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot trainSnapshot : dataSnapshot.getChildren()){
                    Registration reg = trainSnapshot.getValue(Registration.class);
                    Log.i("user id",user_id + " " + reg.toString() + " " + reg.getTraineeId() + " " + reg.getTrainingId());
                    if(reg.getTraineeId().equals(user_id)) {
                        r_trainings.add(reg.getTrainingId());
                    }
                }
                fetchTrainingDetail();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    public void fetchTrainingDetail(){
        databaseTraining = FirebaseDatabase.getInstance().getReference("Trainings");
        databaseTraining.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                trainings.clear();
                for (DataSnapshot trainSnapshot : dataSnapshot.getChildren()) {
                    Training tr = trainSnapshot.getValue(Training.class);
                    if (r_trainings.contains(tr.getId())) {
                        Bitmap b = BitmapFactory.decodeResource(reg_train.context.getResources(), R.drawable.ic_ac_unit_white_48dp);
                        item it = new item(tr.getId(), tr.getName(), tr.getLocation(), tr.getPrice(), b, tr.getCategory());
                        trainings.add(it);
                        adapter.notifyDataSetChanged();
                    }
                }

                dialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public String makePlaceholders() {
        StringBuilder sb = new StringBuilder(training_ids.size() * 2 - 1);
        sb.append(training_ids.get(0));
        for (int i = 1; i < training_ids.size(); i++) {
            sb.append("," + training_ids.get(i));
        }
        return sb.toString();

    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }


}


