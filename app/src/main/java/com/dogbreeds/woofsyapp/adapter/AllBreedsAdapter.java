package com.dogbreeds.woofsyapp.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.dogbreeds.woofsyapp.R;

import java.util.HashMap;
import java.util.List;

public class AllBreedsAdapter extends RecyclerView.Adapter<AllBreedsAdapter.MyViewHolder> {
    private List<String> breeds;
    private Context context;
    private OnItemClickListener onItemClickListener;
    private HashMap<String,String> map;

    public AllBreedsAdapter(List<String> breeds, Context context, HashMap<String,String> map) {
        this.breeds = breeds;
        this.context = context;
        this.map = map;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_dog_breed, parent, false);
        return new MyViewHolder(view, onItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holders, int position) {
        final MyViewHolder holder = holders;
        String breedName = breeds.get(position);
        String imageUrl = "";

        if(map.containsKey(breedName)){
            imageUrl = map.get(breedName);
        }

        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(R.drawable.ic_dog_six);
        requestOptions.error(R.drawable.ic_dog_six);
        requestOptions.diskCacheStrategy(DiskCacheStrategy.ALL);
        requestOptions.circleCrop();

        Glide.with(context)
                .load(imageUrl)
                .apply(requestOptions)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        holder.progressBar.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        holder.progressBar.setVisibility(View.GONE);
                        return false;
                    }
                })
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(holder.imageView);

        holder.TVbreed.setText(breedName);
    }

    // Clean all elements of the recycler
    public void clear() {
        breeds.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<String> list) {
        breeds.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return breeds.size();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener{
        void onItemClick(View view, int position);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView TVbreed;
        ImageView imageView;
        ProgressBar progressBar;
        OnItemClickListener onItemClickListener;

        public MyViewHolder(@NonNull View itemView, OnItemClickListener onItemClickListener) {
            super(itemView);

            itemView.setOnClickListener(this);
            TVbreed = itemView.findViewById(R.id.textview_breed);
            imageView = itemView.findViewById(R.id.iv_breed);
            progressBar = itemView.findViewById(R.id.progress_load_photo);

            this.onItemClickListener = onItemClickListener;
        }

        @Override
        public void onClick(View view) {
            onItemClickListener.onItemClick(view, getAdapterPosition());
        }
    }
}

