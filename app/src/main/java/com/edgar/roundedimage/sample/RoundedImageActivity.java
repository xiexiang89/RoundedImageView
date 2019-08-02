package com.edgar.roundedimage.sample;

import android.animation.ValueAnimator;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SeekBar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.edgar.roundimage.RoundedImageView;

/**
 * Created by Edgar on 2018/12/28.
 */
public class RoundedImageActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener {

    private RoundedImageView mRoundedImageView;
    private SeekBar mStrokeSeekBar;
    private SeekBar mTopLeftSeekBar;
    private SeekBar mTopRightSeekBar;
    private SeekBar mBottomRightSeekBar;
    private SeekBar mBottomLeftSeekBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.round_image_activity);
        mRoundedImageView = findViewById(R.id.rounded_image);
        mStrokeSeekBar = findViewById(R.id.stroke_seek_bar);
        mTopLeftSeekBar = findViewById(R.id.top_left_seek_bar);
        mTopRightSeekBar = findViewById(R.id.top_right_seek_bar);
        mBottomRightSeekBar = findViewById(R.id.bottom_right_seek_bar);
        mBottomLeftSeekBar = findViewById(R.id.bottom_left_seek_bar);
        mStrokeSeekBar.setOnSeekBarChangeListener(this);
        mTopLeftSeekBar.setOnSeekBarChangeListener(this);
        mTopRightSeekBar.setOnSeekBarChangeListener(this);
        mBottomRightSeekBar.setOnSeekBarChangeListener(this);
        mBottomLeftSeekBar.setOnSeekBarChangeListener(this);
        mTopLeftSeekBar.setProgress(100);
        mTopRightSeekBar.setProgress(100);
        mBottomRightSeekBar.setProgress(100);
        mBottomLeftSeekBar.setProgress(100);
        CheckBox checkBox = findViewById(R.id.border_overlay);
        checkBox.setChecked(mRoundedImageView.isBorderOverlay());
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mRoundedImageView.setBorderOverlay(isChecked);
            }
        });
        CheckBox checkOvalBox = findViewById(R.id.check_oval);
        checkOvalBox.setChecked(mRoundedImageView.isOval());
        checkOvalBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mRoundedImageView.setOval(isChecked);
            }
        });
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        switch (seekBar.getId()) {
            case R.id.stroke_seek_bar:
                mRoundedImageView.setBorderSize(progress);
                break;
            case R.id.top_left_seek_bar:
                mRoundedImageView.setTopLeftRadii(getProgressRadius(progress));
                break;
            case R.id.top_right_seek_bar:
                mRoundedImageView.setTopRightRadii(getProgressRadius(progress));
                break;
            case R.id.bottom_left_seek_bar:
                mRoundedImageView.setBottomLeftRadii(getProgressRadius(progress));
                break;
            case R.id.bottom_right_seek_bar:
                mRoundedImageView.setBottomRightRadii(getProgressRadius(progress));
                break;
        }
    }

    private int getProgressRadius(int progress) {
        ViewGroup.LayoutParams p = mRoundedImageView.getLayoutParams();
        return (int) ((float) progress / 100 * p.width) / 2;
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    public void onStartRoundAnimator(View view) {
        if (mRoundedImageView.isOval()) return;
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0,mRoundedImageView.getWidth()/2f);
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.setDuration(1000);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float radii = (float) animation.getAnimatedValue();
                mRoundedImageView.setCornerRadii(radii,radii,radii,radii);
            }
        });
        valueAnimator.start();
    }
}