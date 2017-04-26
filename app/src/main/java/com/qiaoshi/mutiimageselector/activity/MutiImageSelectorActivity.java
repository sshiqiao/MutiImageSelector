package com.qiaoshi.mutiimageselector.activity;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.os.Bundle;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.qiaoshi.mutiimageselector.R;
import com.qiaoshi.mutiimageselector.Utils;
import com.qiaoshi.mutiimageselector.adapter.BaseRecyclerViewAdapter;
import com.qiaoshi.mutiimageselector.adapter.MutiGallerySelectorAdapter;
import com.qiaoshi.mutiimageselector.adapter.MutiImageSelectorAdapter;
import com.qiaoshi.mutiimageselector.model.GalleryItem;
import com.qiaoshi.mutiimageselector.model.ImageItem;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


import pub.devrel.easypermissions.EasyPermissions;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;


public class MutiImageSelectorActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks, View.OnClickListener{

    public static final int PERMMISSION_WRITE_READ_EXTERNAL_STORAGE = 1000;
    public static final int PERMMISSION_CAMER = 2000;

    public static final int REQUEST_CAMERA = 1000;

    public static final int SELECTED_FINISH = 1000;

    public static final String MAX_SELECT_NUM = "MAX_SELECT_NUM";
    public static final String SELECTED_DATA = "SELECTED_DATA";

    private Context context;
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private MutiImageSelectorAdapter mutiImageSelectorAdapter;
    private Map<String,List<ImageItem>> dataMap = new HashMap<>();

    private boolean isChoose;
    private boolean isAnimEnd = true;
    private LinearLayout choose;
    private RecyclerView chooseGalleryRecyclerView;
    private MutiGallerySelectorAdapter mutiGallerySelectorAdapter;
    private List<ImageItem> selectedGallerytData = new ArrayList<>();
    private List<GalleryItem> galleryItemList = new ArrayList<>();

    private int maxSelectedNum;
    private int selectedNum;
    private ArrayList<String> selectedData = new ArrayList<>();

    private ImageView back;
    private TextView finish;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_muti_image_selector);

        initView();
        grandExternalFilePermmision();
    }

    public void initView() {
        context = this;
        maxSelectedNum = getIntent().getIntExtra(MAX_SELECT_NUM,1);
        selectedData = getIntent().getStringArrayListExtra(SELECTED_DATA);
        selectedNum = selectedData==null?0:selectedData.size();

        toolbar = (Toolbar)findViewById(R.id.toolbar);
        RelativeLayout toolbarView = (RelativeLayout)getLayoutInflater().inflate(R.layout.toolbar_muti_image,null);
        toolbar.addView(toolbarView);
        back = (ImageView)toolbarView.findViewById(R.id.back);
        back.setOnClickListener(this);
        finish = (TextView)toolbarView.findViewById(R.id.finish);
        String finishStr = selectedNum==0?"完成":"完成("+selectedNum+"/"+maxSelectedNum+")";
        finish.setText(finishStr);
        finish.setOnClickListener(this);

        chooseGalleryRecyclerView = (RecyclerView)findViewById(R.id.choose_gallery_recyclerview);
        chooseGalleryRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        refreshMutiGallerySelectorAdapter(context.getResources().getString(R.string.all_images));

        choose = (LinearLayout)findViewById(R.id.choose);
        choose.setOnClickListener(this);

        recyclerView = (RecyclerView)findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new GridLayoutManager(context, 3));
        refreshMutiImageSelectorAdapter();
    }
    public boolean isImageUrlContained(String url){
        for (String imageUrl : selectedData){
            if(imageUrl.equals(url))
                return true;
        }
        return false;
    }

    public void getImages(){
        Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        ContentResolver mContentResolver = context.getContentResolver();
        Cursor mCursor = mContentResolver.query(
                mImageUri,
                null,
                MediaStore.Images.Media.MIME_TYPE + "=? or " + MediaStore.Images.Media.MIME_TYPE + "=?",
                new String[] { "image/jpeg", "image/png" },
                MediaStore.Images.Media.DATE_MODIFIED+" desc");
        if(mCursor == null){
            return;
        }
        selectedGallerytData.clear();
        while (mCursor.moveToNext()) {
            String path = mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.Media.DATA));
            String parentFileName = new File(path).getParentFile().getName();
            if(!dataMap.containsKey(parentFileName)){
                List<ImageItem> childList = new ArrayList<>();
                childList.add(new ImageItem(path, isImageUrlContained(path)));
                dataMap.put(parentFileName, childList);
            }else{
                dataMap.get(parentFileName).add(new ImageItem(path, isImageUrlContained(path)));
            }
            selectedGallerytData.add(new ImageItem(path, isImageUrlContained(path)));
        }
        dataMap.put(context.getResources().getString(R.string.all_images), selectedGallerytData);
        mCursor.close();

        map2GalleryList();
        refreshMutiImageSelectorAdapter();
    }

    public void map2GalleryList(){
        List<ImageItem> allData = dataMap.get(context.getResources().getString(R.string.all_images));
        galleryItemList.add(new GalleryItem(allData.get(0).imagePath, context.getResources().getString(R.string.all_images), allData.size()));
        Iterator iterator = dataMap.entrySet().iterator();
        while (iterator.hasNext()){
            Map.Entry entry = (Map.Entry) iterator.next();
            String key = (String)entry.getKey();
            if(key!=context.getResources().getString(R.string.all_images)){
                List<ImageItem> value = (List<ImageItem>)entry.getValue();
                galleryItemList.add(new GalleryItem(value.get(0).imagePath, key, value.size()));
            }
        }
    }

    public boolean filterSelectedDataContained() {
        ArrayList<String> copyData = (ArrayList<String>) selectedData.clone();
        selectedData.clear();
        for(String imageUrl:copyData) {
            boolean isContained = false;
            for(String selectedUrl:selectedData){
                if(imageUrl.equals(selectedUrl)){
                    isContained = true;
                    break;
                }
            }
            if(!isContained){
                selectedData.add(imageUrl);
            }
        }
        return false;
    }

    public void refreshMutiImageSelectorAdapter() {
        mutiImageSelectorAdapter = new MutiImageSelectorAdapter(this,selectedGallerytData,R.layout.item_muti_image,3);
        recyclerView.setAdapter(mutiImageSelectorAdapter);
        mutiImageSelectorAdapter.setOnItemClickListener(new MutiImageSelectorAdapter.OnItemClickListenter() {
            @Override
            public void OnItemClick(View view, int position) {
                if(!isChoose){
                    int index = position-1;
                    if(index<0){
                        grantCameraPermmision();
                    }else {
                        ImageItem imageItem = selectedGallerytData.get(index);
                        if (!imageItem.isSelected) {
                            if(selectedNum<maxSelectedNum) {
                                selectedNum++;
                                selectedData.add(imageItem.imagePath);
                                (selectedGallerytData.get(index)).isSelected = !imageItem.isSelected;
                            }
                        } else {
                            if(selectedNum>0) {
                                selectedNum--;
                                selectedData.remove(imageItem.imagePath);
                                selectedGallerytData.get(index).isSelected = !imageItem.isSelected;
                            }
                        }
                        String finishStr = selectedNum==0?"完成":"完成("+selectedNum+"/"+maxSelectedNum+")";
                        finish.setText(finishStr);
                        mutiImageSelectorAdapter.notifyDataSetChanged();
                    }
                }
            }
        });
    }

    public void grandExternalFilePermmision() {
        String[] perms = {READ_EXTERNAL_STORAGE,WRITE_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(this, perms)) {
            getImages();
        } else {
            EasyPermissions.requestPermissions(this, "需要文件读取权限", PERMMISSION_WRITE_READ_EXTERNAL_STORAGE, perms);
        }
    }
    public void grantCameraPermmision() {
        String[] perms = {CAMERA};
        if (EasyPermissions.hasPermissions(this, perms)) {
            openCamera();
        } else {
            EasyPermissions.requestPermissions(this, "需要拍照权限", PERMMISSION_CAMER, perms);
        }
    }
    private File cameraPhotoFile;
    public void openCamera(){
        cameraPhotoFile = new File(Utils.getApplicationFolderPath() + "/camera" + System.currentTimeMillis() + ".png");
        Uri photoURI = null;
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){
            photoURI = FileProvider.getUriForFile(context, "com.qiaoshi.mutiimageselector.provider", cameraPhotoFile);
        }else {
            photoURI = Uri.fromFile(cameraPhotoFile);
        }

        Intent takeIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takeIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        takeIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
        startActivityForResult(takeIntent, REQUEST_CAMERA);
    }
    public void refreshMutiGallerySelectorAdapter(String selectedGallery) {
        mutiGallerySelectorAdapter = new MutiGallerySelectorAdapter(context, galleryItemList, R.layout.item_muti_gallery, 0 ,selectedGallery);
        chooseGalleryRecyclerView.setAdapter(mutiGallerySelectorAdapter);
        mutiGallerySelectorAdapter.setOnItemClickListener(new BaseRecyclerViewAdapter.OnItemClickListenter(){
            @Override
            public void OnItemClick(View view, int position) {
                String selectedGallery = galleryItemList.get(position).galleryName;
                selectedGallerytData = dataMap.get(selectedGallery);
                refreshMutiGallerySelectorAdapter(selectedGallery);
                refreshMutiImageSelectorAdapter();
                setRecyclerViewState();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        switch (requestCode) {
            case PERMMISSION_WRITE_READ_EXTERNAL_STORAGE:
                getImages();
                break;
            case PERMMISSION_CAMER:
                openCamera();
                break;
        }
    }
    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        switch (requestCode) {
            case PERMMISSION_WRITE_READ_EXTERNAL_STORAGE:
                grandExternalFilePermmision();
                break;
            case PERMMISSION_CAMER:
                grantCameraPermmision();
                break;
        }
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK && isChoose)) {
            setRecyclerViewState();
            return false;
        }else {
            return super.onKeyDown(keyCode, event);
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case REQUEST_CAMERA:
                if(Utils.fileExists(cameraPhotoFile.getPath())){
                    selectedData.add(cameraPhotoFile.getPath());
                    finishWithData();
                }else {
                    Toast.makeText(context,"图片创建失败",Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
    public void setRecyclerViewState() {
        if(!isAnimEnd){
            return;
        }

        isAnimEnd = false;

        if(isChoose) {
            galleryRecyclerViewHideAnim(Utils.translateAnimation(0, 0, 0, (Utils.screenHeight()-Utils.dp2px(50)), 300));
        } else {
            galleryRecyclerViewShowAnim(Utils.translateAnimation(0, 0,  (Utils.screenHeight()-Utils.dp2px(50)), 0, 300));
        }
        isChoose = !isChoose;
    }
    public void galleryRecyclerViewShowAnim(TranslateAnimation translateAnimation){
        chooseGalleryRecyclerView.setVisibility(View.VISIBLE);
        translateAnimation.setAnimationListener(new Utils.AnimationListener(){
            @Override
            public void onAnimationEnd(Animation animation) {
                isAnimEnd = true;
                recyclerView.setLayoutFrozen(true);
            }
        });
        chooseGalleryRecyclerView.startAnimation(translateAnimation);
    }
    public void galleryRecyclerViewHideAnim(TranslateAnimation translateAnimation){
        translateAnimation.setAnimationListener(new Utils.AnimationListener(){
            @Override
            public void onAnimationEnd(Animation animation) {
                isAnimEnd = true;
                chooseGalleryRecyclerView.setVisibility(View.GONE);
                recyclerView.setLayoutFrozen(false);
            }
        });
        chooseGalleryRecyclerView.startAnimation(translateAnimation);
    }
    public void finishWithData(){
        Intent intent = new Intent();
        intent.putStringArrayListExtra(SELECTED_DATA, selectedData);
        setResult(RESULT_OK, intent);
        finish();
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.choose:
                setRecyclerViewState();
                break;
            case R.id.back:
                finish();
                break;
            case R.id.finish:
                filterSelectedDataContained();
                finishWithData();
                break;
            default:
                break;
        }
    }
}
