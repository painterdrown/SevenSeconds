package com.goldfish.sevenseconds.tools;

import android.net.Uri;
import android.os.Environment;

import com.jph.takephoto.app.TakePhoto;
import com.jph.takephoto.compress.CompressConfig;
import com.jph.takephoto.model.CropOptions;
import com.jph.takephoto.model.TakePhotoOptions;

import java.io.File;

/**
 * Created by lenovo on 2017/2/26.
 */

public class PhotoHelper {

    public void selectPhotoMethod(TakePhoto takePhoto) {
        File file=new File(Environment.getExternalStorageDirectory(), "/temp/"+System.currentTimeMillis() + ".jpg");
        if (!file.getParentFile().exists())file.getParentFile().mkdirs();
        Uri imageUri = Uri.fromFile(file);
        configCompress(takePhoto);
        configTakePhotoOption(takePhoto);
        takePhoto.onPickFromGalleryWithCrop(imageUri,getCropOptions());
    }

    public void takePhotoMethod(TakePhoto takePhoto) {
        // 保存图片路径
        File file=new File(Environment.getExternalStorageDirectory(), "/temp/"+System.currentTimeMillis() + ".jpg");
        if (!file.getParentFile().exists())file.getParentFile().mkdirs();
        Uri imageUri = Uri.fromFile(file);
        configCompress(takePhoto);
        configTakePhotoOption(takePhoto);
        takePhoto.onPickFromCaptureWithCrop(imageUri,getCropOptions());
    }

    private void configCompress(TakePhoto takePhoto) {
        // 头像的大小
        int maxSize= Integer.parseInt("102400");
        int width= Integer.parseInt("150");
        int height= Integer.parseInt("150");
        boolean showProgressBar = true;
        boolean enableRawFile = true;
        CompressConfig config = new CompressConfig.Builder()
                .setMaxSize(maxSize)
                .setMaxPixel(width>=height? width:height)
                .enableReserveRaw(enableRawFile)
                .create();
        takePhoto.onEnableCompress(config,showProgressBar);
    }

    private void configTakePhotoOption(TakePhoto takePhoto) {
        TakePhotoOptions.Builder builder=new TakePhotoOptions.Builder();
        builder.setWithOwnGallery(true);
        builder.setCorrectImage(true);
        takePhoto.setTakePhotoOptions(builder.create());
    }

    private CropOptions getCropOptions() {
        int height= Integer.parseInt("150");
        int width= Integer.parseInt("150");
        boolean withWonCrop = true;
        CropOptions.Builder builder=new CropOptions.Builder();
        builder.setAspectX(width).setAspectY(height);
        builder.setWithOwnCrop(withWonCrop);
        return builder.create();
    }

}