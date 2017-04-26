package com.qiaoshi.mutiimageselector.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.qiaoshi.mutiimageselector.R;
import com.qiaoshi.mutiimageselector.model.ImageItem;

import java.util.ArrayList;
import java.util.List;

public class MutiImageSelectorAdapter extends BaseRecyclerViewAdapter<ImageItem> {

    public MutiImageSelectorAdapter(Context context, List<ImageItem> data, int itemLayoutId, int column) {
        super(context, data, itemLayoutId, column);
        List<ImageItem> tempList = new ArrayList<>();
        tempList.add(new ImageItem("camera",false));
        tempList.addAll(data);
        this.data = tempList;
    }

    @Override
    public void onBindViewHolder(BaseRecyclerViewAdapter.ViewHolder holder, int position) {

        ImageItem item = data.get(position);

        RelativeLayout itemLayout = (RelativeLayout)holder.getViewById(R.id.item_layout);
        ImageView image = (ImageView)holder.getViewById(R.id.image);
        ImageView cover = (ImageView)holder.getViewById(R.id.cover);
        LinearLayout icon = (LinearLayout)holder.getViewById(R.id.icon);
        ImageView select = (ImageView)holder.getViewById(R.id.select);

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)image.getLayoutParams();
        params.width = params.height = itemWidth;
        RelativeLayout.LayoutParams params2 = (RelativeLayout.LayoutParams)cover.getLayoutParams();
        params2.width = params2.height = itemWidth;
        if("camera".equals(item.imagePath)){
            icon.setVisibility(View.VISIBLE);
            image.setVisibility(View.GONE);
            cover.setVisibility(View.INVISIBLE);
            select.setVisibility(View.GONE);
        }else{
            icon.setVisibility(View.GONE);

            Glide.with(context)
                    .load(item.imagePath)
                    .crossFade()
                    .override(200, 200)
                    .into(image);
            select.setVisibility(View.VISIBLE);
            if(item.isSelected) {
                cover.setVisibility(View.VISIBLE);
                select.setImageResource(R.drawable.ic_check_circle_white_24dp);
            }else {
                cover.setVisibility(View.INVISIBLE);
                select.setImageResource(R.drawable.ic_radio_button_unchecked_white_24dp);
            }
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
