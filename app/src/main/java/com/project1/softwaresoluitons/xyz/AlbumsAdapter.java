package com.project1.softwaresoluitons.xyz;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import static com.project1.softwaresoluitons.xyz.TrainingCardActivity.items;



public class AlbumsAdapter extends RecyclerView.Adapter<AlbumsAdapter.MyViewHolder> {

    Context mContext;
    //private List<Album> albumList;
    ArrayList<Album> trainings;
    //Context context;
    LayoutInflater l;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, count, location;
        public ImageView thumbnail, overflow;

        public MyViewHolder(View view) {
            super(view);
            location = (TextView)view.findViewById(R.id.location);
            title = (TextView) view.findViewById(R.id.title);
            count = (TextView) view.findViewById(R.id.count);
            thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
            overflow = (ImageView) view.findViewById(R.id.overflow);
        }
    }


    public AlbumsAdapter(Context c,  ArrayList<Album> t) {
        this.mContext = c;
        this.trainings = t;
        items = new ArrayList<Album>();
        items = t;
        l = LayoutInflater.from(mContext);
    }

    @Override
    public  MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.album_card, parent, false);

        return new MyViewHolder(itemView);
    }

    public void filter(String text) {
        ArrayList<Album> temp=new ArrayList<Album>();
        if(text.isEmpty()){
            for(int i=0;i < trainings.size();i++){
                temp.add(trainings.get(i));
            }
            items=temp;
        } else{
            text = text.toLowerCase();
            for(int i=0;i < trainings.size();i++){
                if(trainings.get(i).getName().toLowerCase().contains(text)){
                    temp.add(trainings.get(i));
                }
            }
            items=temp;
        }
        notifyDataSetChanged();
    }


    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        //holder.title.setText(items.get(position).getName());

        //holder.price.setText("Rs. " + items.get(position).price);
        //Bitmap y = getRoundedShape(items.get(position).b);
        //holder.img.setImageBitmap(y);
        //holder.location.setText(items.get(position).location);
        //holder.contact.setText(items.get(position).contact);
        Album album = items.get(position);
        holder.title.setText(album.getName());
        holder.count.setText("₹" + album.getPrice());
        holder.location.setText(album.getPlace());

        // loading album cover using Glide library
        Glide.with(mContext).load(album.getThumbnail()).into(holder.thumbnail);

        holder.overflow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu(holder.overflow);
            }
        });
    }

    /**
     * Showing popup menu when tapping on 3 dots
     */
    private void showPopupMenu(View view) {
        // inflate menu
        PopupMenu popup = new PopupMenu(mContext, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_album, popup.getMenu());
        popup.setOnMenuItemClickListener(new MyMenuItemClickListener());
        popup.show();
    }

    /**
     * Click listener for popup menu items
     */
    class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {

        public MyMenuItemClickListener() {
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.action_add_favourite:
                    Toast.makeText(mContext, "Add to favourite", Toast.LENGTH_SHORT).show();
                    return true;
                case R.id.action_play_next:
                    Toast.makeText(mContext, "Play next", Toast.LENGTH_SHORT).show();
                    return true;
                default:
            }
            return false;
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
