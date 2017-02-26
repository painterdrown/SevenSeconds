package com.goldfish.sevenseconds.activities;

import android.content.Intent;
import android.os.Bundle;

import com.goldfish.sevenseconds.tools.PhotoHelper;
import com.jph.takephoto.app.TakePhotoActivity;
import com.jph.takephoto.model.TImage;
import com.jph.takephoto.model.TResult;

import java.util.ArrayList;

/**
 * Created by lenovo on 2017/2/26.
 */

public class PhotographActivity extends TakePhotoActivity {

    private PhotoHelper photoHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        photoHelper = new PhotoHelper();
        photoHelper.takePhotoMethod(getTakePhoto());
    }

    @Override
    public void takeCancel() {
        super.takeCancel();
    }

    @Override
    public void takeFail(TResult result, String msg) {
        super.takeFail(result, msg);
    }

    @Override
    public void takeSuccess(TResult result) {
        super.takeSuccess(result);
        showImg(result.getImages());
    }

    private void showImg(ArrayList<TImage> images) {
        Intent intent = new Intent();
        intent.putExtra("images", images);
        setResult(RESULT_OK, intent);
        finish();
        /*Intent intent=new Intent(this,ResultActivity.class);
        intent.putExtra("images",images);
        startActivity(intent);*/
    }
}
