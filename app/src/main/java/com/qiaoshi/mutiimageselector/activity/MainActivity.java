package com.qiaoshi.mutiimageselector.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import com.qiaoshi.mutiimageselector.R;
import com.qiaoshi.mutiimageselector.Utils;
import com.qiaoshi.mutiimageselector.adapter.BaseRecyclerViewAdapter;
import java.util.ArrayList;
import java.util.List;

import static com.qiaoshi.mutiimageselector.activity.MutiImageSelectorActivity.MAX_SELECT_NUM;
import static com.qiaoshi.mutiimageselector.activity.MutiImageSelectorActivity.SELECTED_DATA;
import static com.qiaoshi.mutiimageselector.activity.MutiImageSelectorActivity.SELECTED_FINISH;

public class MainActivity extends AppCompatActivity {

    private Context context;
    private RecyclerView recyclerview;
    private PhotoWallAdapter photoWallAdapter;
    private ArrayList<String> selectedData = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Utils.checkApplicationFolder();
        initView();
        toMutiImageSelectorActivity();
    }

    public void initView() {
        context = this;
        recyclerview = (RecyclerView)findViewById(R.id.recyclerview);
        recyclerview.setLayoutManager(new GridLayoutManager(context, 3));

    }
    public void toMutiImageSelectorActivity(){
        Intent intent = new Intent(MainActivity.this, MutiImageSelectorActivity.class);
        intent.putExtra(MAX_SELECT_NUM,25);
        intent.putStringArrayListExtra(SELECTED_DATA, selectedData);
        startActivityForResult(intent,SELECTED_FINISH);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode){
            case SELECTED_FINISH:
                if(resultCode == RESULT_OK) {
                    selectedData = data.getStringArrayListExtra(SELECTED_DATA);
                    photoWallAdapter = new PhotoWallAdapter(context, selectedData, R.layout.item_photo_wall);
                    recyclerview.setAdapter(photoWallAdapter);
                    photoWallAdapter.setOnItemClickListener(new BaseRecyclerViewAdapter.OnItemClickListenter() {
                        @Override
                        public void OnItemClick(View view, int position) {
                            if(selectedData.size()==position){
                                toMutiImageSelectorActivity();
                            }
                        }
                    });
                }
                break;
        }
    }

    class PhotoWallAdapter extends BaseRecyclerViewAdapter<String> {

        public PhotoWallAdapter(Context context, List<String> data, int itemLayoutId) {
            super(context, data, itemLayoutId, 3);
            ArrayList<String> tempList = new ArrayList<>();
            tempList.addAll(data);
            tempList.add("add");
            this.data = tempList;
        }
        @Override
        public void onBindViewHolder(BaseRecyclerViewAdapter.ViewHolder holder, final int position) {
            RelativeLayout addLayout = (RelativeLayout)holder.getViewById(R.id.add_layout);
            ImageView image = holder.setImageUrl(R.id.image, data.get(position));
            if(data.get(position).equals("add")) {
                addLayout.setVisibility(View.VISIBLE);
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) addLayout.getLayoutParams();
                params.width = params.height = itemWidth;
            }else{
                addLayout.setVisibility(View.GONE);
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) image.getLayoutParams();
                params.width = params.height = itemWidth;
            }
            final int index = position;
            addLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(onItemClickListenter!=null){
                        onItemClickListenter.OnItemClick(v, index);
                    }
                }
            });
        }
    }
}
