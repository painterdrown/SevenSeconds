package com.goldfish.sevenseconds.activities;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ScrollingView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.goldfish.sevenseconds.R;
import com.goldfish.sevenseconds.bean.LastUser;
import com.goldfish.sevenseconds.tools.ImageUtils;
import com.goldfish.sevenseconds.tools.ScreenUtils;
import com.sendtion.xrichtext.RichTextEditor;
import com.sendtion.xrichtext.SDCardUtil;

import org.json.JSONObject;
import org.litepal.crud.DataSupport;
import org.litepal.tablemanager.Connector;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.jar.Manifest;

import me.originqiu.library.EditTag;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static android.R.attr.height;
import static android.R.attr.width;
import static android.widget.Toast.LENGTH_SHORT;
import static com.goldfish.sevenseconds.tools.Http.addMemory;
import static com.goldfish.sevenseconds.tools.Http.getUserInfo;

public class Addmem extends AppCompatActivity {
    private EditTag editText_tag;
    private EditText editText_title;

    private  ProgressDialog progressDialog;
    private int length;
    private EditTag editTagView;
    private List<String> tagStrings = new ArrayList<>();
    private List<String> addimages = new ArrayList<>();
    private String up_contents;
    private String up_tags;
    private String up_title;
    private Boolean re;
    private String imagerealpath;
    private LinearLayout contents;
    private RichTextEditor users_content;

    private ProgressDialog loadingDialog;
    private ProgressDialog insertDialog;

    private Subscription subsLoading;
    private Subscription subsInsert;
    private String getEditData() {
        List<RichTextEditor.EditData> editList = users_content.buildEditData();
        StringBuffer content = new StringBuffer();
        for (RichTextEditor.EditData itemData : editList) {
            if (itemData.inputStr != null) {
                content.append(itemData.inputStr);
                //Log.d("RichEditor", "commit inputStr=" + itemData.inputStr);
            } else if (itemData.imagePath != null) {
                content.append("<img src=\"").append(itemData.imagePath).append("\"/>");
                //Log.d("RichEditor", "commit imgePath=" + itemData.imagePath);
                //imageList.add(itemData.imagePath);
            }
        }
        return content.toString();
    }

    class addTask extends AsyncTask<Void , Integer,Boolean>{
        @Override
        protected void onPreExecute(){
            progressDialog.show();
        }
        @Override
        protected Boolean doInBackground(Void... params){
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("title",up_title);
                List<LastUser> lastUsers = DataSupport.findAll(LastUser.class);
                LastUser user = lastUsers.get(0);
                /*JSONObject jsonObjectinfo = new JSONObject();
                jsonObjectinfo.put("account",user.getName());
                JSONObject userinfo = getUserInfo(jsonObjectinfo);*/
                jsonObject.put("author",user.getName());
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date curDate = new Date(System.currentTimeMillis());//获取当前时间
                String str = formatter.format(curDate);
                jsonObject.put("time",str);
                jsonObject.put("description",up_contents.substring(0,(up_contents.length()<1000)?up_contents.length():200));
                jsonObject.put("content",up_contents);
                jsonObject.put("labels",up_tags);
                jsonObject.put("authority", 1);
                JSONObject retob;
                retob = addMemory(jsonObject,addimages);
                re = retob.getBoolean("ok");
            }catch (Exception e){
                Log.d("erro upload",e.getMessage());
                return false;
            }
            return true;
        }
        @Override
        protected void onPostExecute(Boolean result){
            progressDialog.dismiss();
            if (result){
                if (re){
                Toast.makeText(Addmem.this,"上传完成",Toast.LENGTH_LONG);
                finish();
                }
                else {
                    Toast.makeText(Addmem.this,"上传成功但校验失败请询问服务器原因",Toast.LENGTH_LONG);
                }
            }
            else{
                Toast.makeText(Addmem.this,"上传失败，请检查你的网络",Toast.LENGTH_LONG);
            }
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.add_toolbar,menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
            case R.id.a_mem_add:
                addTask add = new addTask();
                up_title = editText_title.getText().toString();
                up_contents = getEditData();
                tagStrings = editText_tag.getTagList();
                up_tags = "";
                for (int i =0 ;i<tagStrings.size();i++){
                    if (i != tagStrings.size()-1) up_tags = up_tags+tagStrings.get(i)+",";
                    else up_tags = up_tags + tagStrings.get(i);
                }
                int now = 0;
                int st = up_contents.indexOf("<img src"); int ed = up_contents.indexOf("\"/>");
                String mid;
                while (st>0){
                    addimages.add(up_contents.substring(st+10,ed));
                    Log.d("path",up_contents.substring(st+10,ed));
                    up_contents = up_contents.substring(0,st)+"<img"+now+">"+up_contents.substring(ed+3);
                    st = up_contents.indexOf("<img src"); ed = up_contents.indexOf("\"/>",st);
                    now++;
                }
                /*
                Log.d("uptitle",up_title);
                Log.d("uptags",up_tags);
                Log.d("add",up_contents);*/
                add.execute();
                break;
            default:
        }
        return true;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Connector.getDatabase();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addmem);


        Toolbar toolbar = (Toolbar) findViewById(R.id.admem_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_cancel);

        insertDialog = new ProgressDialog(this);
        insertDialog.setMessage("正在插入图片...");
        insertDialog.setCanceledOnTouchOutside(false);

        loadingDialog = new ProgressDialog(this);
        loadingDialog.setMessage("图片解析中...");
        loadingDialog.setCanceledOnTouchOutside(false);

        progressDialog = new ProgressDialog(Addmem.this);
        progressDialog.setTitle("正在上传");
        progressDialog.setMessage("Loading");
        progressDialog.setCancelable(false);

        editText_title = (EditText)findViewById(R.id.a_mem_title);
        users_content = (RichTextEditor)findViewById(R.id.admem_content);
        editText_tag = (EditTag) findViewById(R.id.admem_edit_tag);
        Button add_pic = (Button) findViewById(R.id.add_pic);
        add_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(Addmem.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(Addmem.this,new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
                }
                else{
                    openalbum();
                }
            }
        });

    }
    private void openalbum(){
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent,2);
    }
    @Override
    public  void onRequestPermissionsResult(int requestCode,String[] permissions,int[] grantResults){
        switch (requestCode){
            case 1:
                if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) openalbum();
                else {
                    Toast.makeText(this,"请允许打开相册选择图片", LENGTH_SHORT).show();
                }
                break;
            default:
        }
    }
    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data){
        switch (requestCode){
            case 2:
                if (resultCode == RESULT_OK) {
                    // 判断手机系统版本号
                    if (Build.VERSION.SDK_INT >= 19) {
                        // 4.4及以上系统使用这个方法处理图片
                        handleImageOnKitKat(data);
                    } else {
                        // 4.4以下系统使用这个方法处理图片
                        handleImageBeforeKitKat(data);
                    }
                }
                break;
            default:
                break;
        }
    }
    @TargetApi(19)
    private void handleImageOnKitKat(Intent data){
        String imagePath = null;
        Uri uri =data.getData();
        if (DocumentsContract.isDocumentUri(this,uri)){
            String docId = DocumentsContract.getDocumentId(uri);
            if("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = docId.split(":")[1]; // 解析出数字格式的id
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
                imagePath = getImagePath(contentUri, null);
            }
        }else if ("content".equalsIgnoreCase(uri.getScheme())) {
            // 如果是content类型的Uri，则使用普通方式处理
            imagePath = getImagePath(uri, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            // 如果是file类型的Uri，直接获取图片路径即可
            imagePath = uri.getPath();
        }
        imagerealpath = imagePath;
        useit();
    }
    private void handleImageBeforeKitKat(Intent data) {
        Uri uri = data.getData();
        String imagePath = getImagePath(uri, null);
        imagerealpath = imagePath;
        useit();
    }
    private  void  useit(){
        if (imagerealpath != null){
            insertDialog.show();
            final ArrayList<String> photos = new ArrayList<>();
            photos.add(imagerealpath);
            subsInsert = Observable.create(new Observable.OnSubscribe<String>() {
                @Override
                public void call(Subscriber<? super String> subscriber) {
                    try{
                        users_content.measure(0, 0);
                        int width = ScreenUtils.getScreenWidth(Addmem.this);
                        int height = ScreenUtils.getScreenHeight(Addmem.this);
                        //可以同时插入多张图片
                        for (String imagePath : photos) {
                            //Log.i("NewActivity", "###path=" + imagePath);
                            Bitmap bitmap = ImageUtils.getSmallBitmap(imagePath, width, height);//压缩图片
                            //bitmap = BitmapFactory.decodeFile(imagePath);
                            imagePath = SDCardUtil.saveToSdCard(bitmap);
                            //Log.i("NewActivity", "###imagePath="+imagePath);
                            subscriber.onNext(imagePath);
                        }
                        subscriber.onCompleted();
                    }catch (Exception e){
                        e.printStackTrace();
                        subscriber.onError(e);
                    }
                }
            })
                    .onBackpressureBuffer()
                    .subscribeOn(Schedulers.io())//生产事件在io
                    .observeOn(AndroidSchedulers.mainThread())//消费事件在UI线程
                    .subscribe(new Observer<String>() {
                        @Override
                        public void onCompleted() {
                            insertDialog.dismiss();
                            users_content.addEditTextAtIndex(users_content.getLastIndex(), " ");
                            Toast.makeText(Addmem.this,"图片插入成功", LENGTH_SHORT).show();
                        }

                        @Override
                        public void onError(Throwable e) {
                            insertDialog.dismiss();
                            Toast.makeText(Addmem.this,"图片插入失败"+e.getMessage(), LENGTH_SHORT).show();
                        }

                        @Override
                        public void onNext(String imagePath) {
                            users_content.insertImage(imagePath, users_content.getMeasuredWidth());
                        }
                    });
        }else {
            Toast.makeText(Addmem.this,"无法选取图片", LENGTH_SHORT).show();
        }
    }
    private String getImagePath(Uri uri, String selection) {
        String path = null;
        // 通过Uri和selection来获取真实的图片路径
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }
}