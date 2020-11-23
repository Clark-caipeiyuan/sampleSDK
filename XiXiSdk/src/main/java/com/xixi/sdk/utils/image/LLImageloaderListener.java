package com.xixi.sdk.utils.image;
 

import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by GFT on 2016/4/14.
 */
public abstract class LLImageloaderListener implements ImageLoadingListener {

    abstract public int getDefaultResId() ;

    private void loadDefaultIcon(ImageView view ) {
        String defaultSrcId = String.valueOf(getDefaultResId());
        if (!TextUtils.equals(defaultSrcId, (String) view.getTag())) {
            view.setImageResource(getDefaultResId());
            view.setTag(defaultSrcId);
        }
    }

    @Override
    public void onLoadingStarted(String imageUri, View view) {
        if ( TextUtils.isEmpty(imageUri) ||  !TextUtils.equals(imageUri , (String)view.getTag()) ) {
            loadDefaultIcon((ImageView) view);
        }
    }

    @Override
    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
        loadDefaultIcon((ImageView) view);
    }

    @Override
    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
        if ( TextUtils.isEmpty(imageUri)) {
            view.setTag("forced"); // a trick way
            loadDefaultIcon((ImageView) view);
        }
        else {
            view.setTag(imageUri);
        }
    }

    @Override
    public void onLoadingCancelled(String imageUri, View view) {

    }
}
