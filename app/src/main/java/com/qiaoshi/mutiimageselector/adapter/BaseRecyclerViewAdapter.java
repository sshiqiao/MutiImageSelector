package com.qiaoshi.mutiimageselector.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.qiaoshi.mutiimageselector.R;
import com.qiaoshi.mutiimageselector.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by qiaoshi on 2017/4/25.
 */

public abstract class BaseRecyclerViewAdapter<T> extends RecyclerView.Adapter<BaseRecyclerViewAdapter.ViewHolder> {
    protected Context context;
    protected int itemWidth = 0;
    protected List<T> data = new ArrayList<>();
    protected int itemLayoutId;
    protected OnItemClickListenter onItemClickListenter;

    public BaseRecyclerViewAdapter(Context context, List<T> data, int itemLayoutId, int column) {
        this.context = context;
        if(column>0) {
            this.itemWidth = Utils.screenWidth() / column;
        }
        this.data = data;
        this.itemLayoutId = itemLayoutId;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(itemLayoutId, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public interface OnItemClickListenter {
        void OnItemClick(View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListenter onItemClickListener){
        this.onItemClickListenter = onItemClickListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private View itemView;
        public ViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
        }
        public void setText(int viewId, String text) {
            TextView textView = (TextView)itemView.findViewById(viewId);
            textView.setText(text);
        }
        public ImageView setImageUrl(int viewId, String url) {
            Log.e("url",url);
            ImageView imageView = (ImageView)itemView.findViewById(viewId);
            Glide.with(context)
                    .load(url)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .error(R.mipmap.ic_launcher)
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            Log.e("errrrrr",e.getMessage());
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            return false;
                        }
                    })
                    .crossFade()
                    .into(imageView);
            return imageView;
        }
        public ImageView setImageResource(int viewId, int res) {
            ImageView imageView = (ImageView)itemView.findViewById(viewId);
            imageView.setImageResource(res);
            return imageView;
        }
        public void setImageDrawable(int viewId, Drawable drawable) {
            ImageView imageView = (ImageView)itemView.findViewById(viewId);
            imageView.setImageDrawable(drawable);
        }
        public View getViewById(int viewId) {
            return itemView.findViewById(viewId);
        }
    }
}
