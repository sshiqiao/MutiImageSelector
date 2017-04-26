package com.qiaoshi.mutiimageselector.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.qiaoshi.mutiimageselector.R;
import com.qiaoshi.mutiimageselector.model.GalleryItem;

import java.util.List;

public class MutiGallerySelectorAdapter extends BaseRecyclerViewAdapter<GalleryItem> {
    private String selectedGallery;
    public MutiGallerySelectorAdapter(Context context, List<GalleryItem> data, int itemLayoutId, int column, String selectedGallery) {
        super(context, data, itemLayoutId, column);
        this.selectedGallery = selectedGallery;
    }
    @Override
    public void onBindViewHolder(BaseRecyclerViewAdapter.ViewHolder holder, int position) {

        GalleryItem item = data.get(position);
        RelativeLayout itemLayout = (RelativeLayout)holder.getViewById(R.id.item_layout);
        ImageView image = (ImageView)holder.getViewById(R.id.image);
        TextView galleryName = (TextView)holder.getViewById(R.id.gallery_name);
        TextView galleryNum = (TextView)holder.getViewById(R.id.gallery_num);
        ImageView checkbox = (ImageView)holder.getViewById(R.id.checkbox);
        Glide.with(context)
                .load(item.firstImagePath)
                .crossFade()
                .override(200, 200)
                .into(image);
        galleryName.setText(item.galleryName);
        galleryNum.setText(item.galleryNum+"张");
        if(item.galleryName.equals(selectedGallery)){
            checkbox.setVisibility(View.VISIBLE);
        }else{
            checkbox.setVisibility(View.GONE);
        }
        final int index = position;
        itemLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onItemClickListenter!=null){
                    onItemClickListenter.OnItemClick(v, index);
                }
            }
        });
    }

}
