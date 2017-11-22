package com.project1.softwaresoluitons.xyz;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

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

public class e_not extends Fragment {
    public RequestQueue queue;
    public ProgressDialog dialog;
    String user_id;
    public RecyclerView recyclerView;
    private FirebaseAuth mAuth;
    DatabaseReference databaseEnquire;
    DatabaseReference databaseUser;
    public  static ArrayList<notification_item_le> notifications;
    public static n_adapter_le adapter;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public e_not() {
        // Required empty public constructor
    }


    public static e_not newInstance(String param1, String param2) {
        e_not fragment = new e_not();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_e_not, container, false);
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
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
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SharedPreferences sp = getActivity().getSharedPreferences("user",Context.MODE_PRIVATE);
        user_id = sp.getString("user_id",null);

        queue = Volley.newRequestQueue(getContext());
        recyclerView=(RecyclerView)view.findViewById(R.id.rview);
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.spacing);
        recyclerView.addItemDecoration(new SpacesItemDecoration(spacingInPixels));
        notifications=new ArrayList<>();
        adapter=new n_adapter_le(getContext(),notifications);
        //recyclerView=(RecyclerView)getView().findViewById(R.id.reg_train);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        fetch_notifications_le();
    }

    public void fetch_notifications_le() {
        dialog = new ProgressDialog(getContext());
        dialog.setMessage("Please wait !!");
        dialog.show();
        databaseEnquire = FirebaseDatabase.getInstance().getReference("enquiry");
        databaseEnquire.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                notifications.clear();
                for(DataSnapshot Snapshot : dataSnapshot.getChildren()) {
                    Enquire en = Snapshot.getValue(Enquire.class);
                    if(en.getTraineeId().equals(user_id) && en.getReplyStatus().equals("1")){
                        fetch_user(en.getDate(),en.getTime(),en.getTrainingTitle(),en.getMessage(),
                                en.getId(),en.getReplyStatus(),en.getTrainerId(),en.getReplyMessage());
                    }
                }
                dialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void fetch_user(final String date, final String time, final String title, final String message, final String enId,
                           final String replyStatus, final String trainerId, final String reply_message){

        databaseUser = FirebaseDatabase.getInstance().getReference("user");
        databaseUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot Snapshot : dataSnapshot.getChildren()) {
                    User us = Snapshot.getValue(User.class);
                    if(us.getId().equals(trainerId)) {
                        notifications.add(0,new notification_item_le(date + " " + time, us.getFname() + " " + us.getLname(),message,
                                us.getEmail(), us.getMobile(),title,enId,replyStatus,reply_message));
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

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}

class notification_item_le{
    String date_time,name,message,email,contact,tr_title;
    String id,reply_status,replyMessage;
    notification_item_le(String date_time,String tr_name,String message, String email,String contact,
                         String tr_title,String id,String reply_status,String replyMessage){
        this.date_time=date_time;
        this.contact=contact;
        this.email=email;
        this.name=tr_name;
        this.message=message;
        this.tr_title=tr_title;
        this.id=id;
        this.replyMessage = replyMessage;
        this.reply_status = reply_status;
    }
}

class n_holder_le extends RecyclerView.ViewHolder{

    TextView name,message,date_time,contact,email,tr_title,reply;
    n_holder_le(View v) {
        super(v);
        name = (TextView) v.findViewById(R.id.name);
        message= (TextView) v.findViewById(R.id.message);
        contact= (TextView) v.findViewById(R.id.contact);
        email= (TextView) v.findViewById(R.id.email);
        date_time= (TextView) v.findViewById(R.id.date_time);
        tr_title=(TextView) v.findViewById(R.id.tr_title);
        reply=(TextView)v.findViewById(R.id.replyMessage);
    }
}
class n_adapter_le extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    ArrayList<notification_item_le> notifications;
    Context context;
    LayoutInflater l;
    ProgressDialog dialog;
    RequestQueue queue;

    n_adapter_le(Context c, ArrayList<notification_item_le> t) {
        this.context = c;
        this.notifications = t;
        l= LayoutInflater.from(context);
        queue=Volley.newRequestQueue(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = l.inflate(R.layout.notification_item_le, parent,false);
        n_holder_le vh = new n_holder_le(v);
        return vh;

    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder1, final int position) {
        n_holder_le holder=(n_holder_le) holder1;
        holder.name.setText(notifications.get(position).name);
        holder.message.setText(notifications.get(position).message);
        holder.date_time.setText(notifications.get(position).date_time);
        holder.contact.setText(notifications.get(position).contact);
        holder.email.setText(notifications.get(position).email);
        holder.tr_title.setText(notifications.get(position).tr_title);
        holder.reply.setText(notifications.get(position).replyMessage);
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

}

