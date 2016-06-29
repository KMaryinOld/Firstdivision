package com.airsoft.goodwin.PrivateOffice;

import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.airsoft.goodwin.R;
import com.airsoft.goodwin.UserInfo.UserInfo;
import com.airsoft.goodwin.connection.FirstdivisionRestClient;
import com.airsoft.goodwin.settings.Settings;
import com.airsoft.goodwin.utils.DragRectView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.loopj.android.http.TextHttpResponseHandler;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Set;

import cz.msebera.android.httpclient.Header;

public class ImageChangerFragment extends DialogFragment {
    private View oldImageLayout;
    private View newImageLayout;

    private ImageView oldImageView;
    private ImageView pickedImage;
    private ImageView shadedArea;
    private DragRectView rectanglePicker;

    public ImageChangerFragment() {
        // Required empty public constructor
    }

    public static ImageChangerFragment newInstance() {
        return new ImageChangerFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_image_changer, container, false);

        oldImageLayout = view.findViewById(R.id.image_changer_old_image_layout);
        newImageLayout = view.findViewById(R.id.image_changer_new_image_layout);

        oldImageView = (ImageView)oldImageLayout.findViewById(R.id.image_changer_old_image);
        if (!Settings.applicationUserInfo.photoPath.equals("-")) {
            Glide.with(getActivity()).load(String.format("%s/%s", Settings.serverAddress, Settings.applicationUserInfo.photoPath))
                .into(oldImageView);
        }

        pickedImage = (ImageView)newImageLayout.findViewById(R.id.image_changer_new_image);
        rectanglePicker = (DragRectView) newImageLayout.findViewById(R.id.image_changer_area_selector);
        shadedArea = (ImageView)newImageLayout.findViewById(R.id.image_changer_shaded_area);

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onPause() {
        super.onPause();
        showOldImageLayout();
    }

    public ImageView getPickedImage() {
        return pickedImage;
    }

    public void showImgAreaSelectLayout() {
        oldImageLayout.setVisibility(View.GONE);
        newImageLayout.setVisibility(View.VISIBLE);

        Point actualBoundsPoint = getActualImageBounds();
        int startBoundX = oldImageView.getWidth() / 2 - actualBoundsPoint.x / 2;
        int startBoundY = oldImageView.getHeight() / 2 - actualBoundsPoint.y / 2;
        rectanglePicker.setImageRect(new Rect(startBoundX, startBoundY, startBoundX + actualBoundsPoint.x,
                startBoundY + actualBoundsPoint.y));
    }

    public void getSelectedBitmap() {
        Point imageBounds = getActualImageBounds();
        double drawableWidth = pickedImage.getDrawable().getIntrinsicWidth();
        double drawableHeight = pickedImage.getDrawable().getIntrinsicHeight();
        double scaleX = drawableWidth / imageBounds.x;
        double scaleY = drawableHeight / imageBounds.y;

        Rect rect = rectanglePicker.getSelectedRect();
        if (Math.abs(rect.left - rect.right) < 20 || Math.abs(rect.top - rect.bottom) < 20) {
            Toast.makeText(getActivity(), getString(R.string.short_selected_area), Toast.LENGTH_SHORT).show();
            return;
        }
        rectanglePicker.lock();
        Bitmap selectedBitmap = Bitmap.createBitmap((int)(rect.width() * scaleX),
                (int)(rect.height() * scaleY), Bitmap.Config.ARGB_8888);

        Bitmap sourceBitmap = ((BitmapDrawable)pickedImage.getDrawable()).getBitmap();
        int startX = (int)(rect.left * scaleX), startY = (int)(rect.top * scaleY);
        for (int x = 0; x < selectedBitmap.getWidth(); ++x) {
            for (int y = 0; y  < selectedBitmap.getHeight(); ++y) {
                selectedBitmap.setPixel(x, y, sourceBitmap.getPixel(startX + x, startY + y));
            }
        }

        //pickedImage.setImageBitmap(selectedBitmap);
        Bitmap shadedBitmap = Bitmap.createBitmap(imageBounds.x, imageBounds.y, Bitmap.Config.ARGB_4444);
        for (int x = 0; x < shadedBitmap.getWidth(); ++x) {
            for (int y = 0; y < shadedBitmap.getHeight(); ++y) {
                if (x < rect.left || y < rect.top || x > rect.right || y > rect.bottom) {
                    shadedBitmap.setPixel(x, y, 0xBB000000);
                } else {
                    shadedBitmap.setPixel(x, y, 0x00000000);
                }
            }
        }

        shadedArea.setImageBitmap(shadedBitmap);
        shadedArea.setVisibility(View.VISIBLE);
        shadedArea.setAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.alpha_appearance));
        shadedArea.animate();

        final Button confirmButton = (Button) newImageLayout.findViewById(R.id.image_changer_confirm_button);
        final ProgressBar confirmProgressBar = (ProgressBar) newImageLayout.findViewById(R.id.image_changer_confirm_progress_bar);
        confirmButton.setVisibility(View.GONE);
        confirmProgressBar.setVisibility(View.VISIBLE);
        FirstdivisionRestClient.getInstance().changePersonalImageAvatar(selectedBitmap, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                confirmButton.setVisibility(View.VISIBLE);
                confirmProgressBar.setVisibility(View.GONE);
                Toast.makeText(getActivity(), getString(R.string.server_connection_error), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                Settings.applicationUserInfo = new UserInfo();
                Intent intent = new Intent(getActivity(), PrivateOfficeActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });
    }

    public void showOldImageLayout() {
        oldImageLayout.setVisibility(View.VISIBLE);
        newImageLayout.setVisibility(View.GONE);
    }

    private Point getActualImageBounds() {
        final double actualHeight, actualWidth;
        final double imageViewHeight = oldImageView.getHeight(), imageViewWidth = oldImageView.getWidth();
        final double bitmapHeight = pickedImage.getDrawable().getIntrinsicHeight(),
            bitmapWidth = pickedImage.getDrawable().getIntrinsicWidth();
        if (imageViewHeight * bitmapWidth <= imageViewWidth * bitmapHeight) {
            actualWidth = bitmapWidth * imageViewHeight / bitmapHeight;
            actualHeight = imageViewHeight;
        } else {
            actualHeight = bitmapHeight * imageViewWidth / bitmapWidth;
            actualWidth = imageViewWidth;
        }

        return new Point((int)actualWidth,(int)actualHeight);
    }
}
