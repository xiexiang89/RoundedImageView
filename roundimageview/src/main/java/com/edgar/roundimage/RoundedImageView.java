package com.edgar.roundimage;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

/**
 * Created by Edgar on 2018/12/29.
 */
public class RoundedImageView extends AppCompatImageView {

    private static final String TAG = "RoundedImageView";
    private static final ScaleType CENTER_CROP = ScaleType.CENTER_CROP;
    private static final int COLOR_DRAWABLE_SIZE = 2;
    private static final Bitmap.Config BITMAP_CONFIG = Bitmap.Config.ARGB_8888;
    //顺时针方向
    private static final int TOP_LEFT = 0;
    private static final int TOP_RIGHT = 2;
    private static final int BOTTOM_RIGHT = 4;
    private static final int BOTTOM_LEFT = 6;
    private static final int[] DIRECTION = {TOP_LEFT,TOP_RIGHT,BOTTOM_RIGHT,BOTTOM_LEFT};

    private boolean mHaveFrame = false;
    private Bitmap mBitmap;
    private BitmapShader mBitmapShader;
    private Paint mDrawablePaint;
    private Matrix mDrawableMatrix;
    private Path mDrawablePath;
    private Path mBorderPath;
    private int mBorderSize;
    private final RectF mDrawableRectF;
    private final RectF mBorderRectF;
    private final Paint mBorderPaint;
    private float[] mDrawableRadii;
    private float[] mBorderRadii;
    private boolean mIsOval;  //圆形
    private boolean mSupportRounded;
    private boolean mBorderOverlay;
    private Paint mMaskPaint;
    private int mCurMaskColor;
    private ColorStateList mMaskColor;

    public RoundedImageView(Context context) {
        this(context, null);
    }

    public RoundedImageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RoundedImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        super.setScaleType(CENTER_CROP);
        mDrawablePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mDrawablePaint.setDither(true);
        mBorderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBorderPaint.setDither(true);
        mMaskPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mMaskPaint.setDither(true);
        mBorderRectF = new RectF();
        mDrawableRectF = new RectF();
        mDrawableRadii = new float[8];
        mBorderRadii = mDrawableRadii.clone();
        mDrawablePath = new Path();
        mBorderPath = new Path();
        TypedArray ta = context.obtainStyledAttributes(attrs,R.styleable.RoundedImageView,defStyleAttr,0);
        int borderColor = ta.getColor(R.styleable.RoundedImageView_borderColor, Color.TRANSPARENT);
        mBorderSize = ta.getDimensionPixelSize(R.styleable.RoundedImageView_borderSize,0);
        mBorderOverlay = ta.getBoolean(R.styleable.RoundedImageView_borderOverlay,true);
        mIsOval = ta.getBoolean(R.styleable.RoundedImageView_isOval,false);
        mSupportRounded = ta.getBoolean(R.styleable.RoundedImageView_supportRounded,true);
        ColorStateList maskColor = ta.getColorStateList(R.styleable.RoundedImageView_maskColor);
        float roundRadius = ta.getDimension(R.styleable.RoundedImageView_roundRadius,0);
        float topLeftRadius = ta.getDimension(R.styleable.RoundedImageView_roundTopLeftRadius, roundRadius);
        float topRightRadius = ta.getDimension(R.styleable.RoundedImageView_roundTopRightRadius, roundRadius);
        float bottomLeftRadius = ta.getDimension(R.styleable.RoundedImageView_roundBottomLeftRadius, roundRadius);
        float bottomRightRadius = ta.getDimension(R.styleable.RoundedImageView_roundBottomRightRadius, roundRadius);
        setCornerRadii(topLeftRadius,topRightRadius,bottomLeftRadius,bottomRightRadius);
        ta.recycle();
        setBorderColor(borderColor);
        mBorderPaint.setStrokeWidth(mBorderSize);
        mBorderPaint.setStyle(Paint.Style.STROKE);
        setMaskColor(maskColor);
        initBitmap();
    }

    public void setMaskColor(@ColorInt int maskColor) {
        mMaskColor = ColorStateList.valueOf(maskColor);
        updateColors();
    }

    public void setMaskColor(ColorStateList color) {
        if (color == null) {
            throw new NullPointerException();
        }
        mMaskColor = color;
        updateColors();
    }

    private void updateColors() {
        boolean inval = false;
        final int[] drawableState = getDrawableState();
        int color = mMaskColor.getColorForState(drawableState, 0);
        if (color != mCurMaskColor) {
            mCurMaskColor = color;
            mMaskPaint.setColor(mCurMaskColor);
            inval = true;
        }
        if (inval) {
            invalidate();
        }
    }

    public void setTopLeftRadii(float radii) {
        if (mBorderRadii[TOP_LEFT] == radii) return;
        mBorderRadii[TOP_LEFT] = mBorderRadii[TOP_LEFT+1] = radii;
        mDrawableRadii[TOP_LEFT] = mDrawableRadii[TOP_LEFT+1] = radii;
        updateDrawable();
    }

    public void setTopRightRadii(float radii) {
        if (mBorderRadii[TOP_RIGHT] == radii) return;
        mBorderRadii[TOP_RIGHT] = mBorderRadii[TOP_RIGHT+1] = radii;
        mDrawableRadii[TOP_RIGHT] = mDrawableRadii[TOP_RIGHT+1] = radii;
        updateDrawable();
    }

    public void setBottomLeftRadii(float radii) {
        if (mBorderRadii[BOTTOM_LEFT] == radii) return;
        mBorderRadii[BOTTOM_LEFT] = mBorderRadii[BOTTOM_LEFT+1] = radii;
        mDrawableRadii[BOTTOM_LEFT] = mDrawableRadii[BOTTOM_LEFT+1] = radii;
        updateDrawable();
    }

    public void setBottomRightRadii(float radii) {
        if (mBorderRadii[BOTTOM_RIGHT] == radii) return;
        mBorderRadii[BOTTOM_RIGHT] = mBorderRadii[BOTTOM_RIGHT+1] = radii;
        mDrawableRadii[BOTTOM_RIGHT] = mDrawableRadii[BOTTOM_RIGHT+1] = radii;
        updateDrawable();
    }

    public void setCornerRadii(float topLeft, float topRight, float bottomRight, float bottomLeft) {
        if (mBorderRadii[TOP_LEFT] != topLeft || mBorderRadii[TOP_RIGHT] != TOP_RIGHT
                || mBorderRadii[BOTTOM_RIGHT] != bottomRight || mBorderRadii[BOTTOM_LEFT] != bottomLeft) {
            updateRadii(mBorderRadii,topLeft,topRight,bottomRight,bottomLeft);
            updateRadii(mDrawableRadii,topLeft,topRight,bottomRight,bottomLeft);
            updateDrawable();
        }
    }

    private void updateRadii(float[] cornerRadii, float topLeft, float topRight, float bottomRight, float bottomLeft) {
        cornerRadii[TOP_LEFT] = cornerRadii[TOP_LEFT+1] = topLeft;
        cornerRadii[TOP_RIGHT] = cornerRadii[TOP_RIGHT+1] = topRight;
        cornerRadii[BOTTOM_RIGHT] = cornerRadii[BOTTOM_RIGHT+1] = bottomRight;
        cornerRadii[BOTTOM_LEFT] = cornerRadii[BOTTOM_LEFT+1] = bottomLeft;
    }

    public boolean isBorderOverlay() {
        return mBorderOverlay;
    }

    public void setBorderOverlay(boolean borderOverlay) {
        if (mBorderOverlay != borderOverlay) {
            mBorderOverlay = borderOverlay;
            updateDrawable();
        }
    }

    public void setBorderColor(@ColorInt int borderColor) {
        if (mBorderPaint.getColor() != borderColor) {
            mBorderPaint.setColor(borderColor);
            invalidate();
        }
    }

    public void setBorderSize(int borderSize) {
        if (mBorderSize != borderSize) {
            mBorderSize = borderSize;
            mBorderPaint.setStrokeWidth(mBorderSize);
            updateDrawable();
        }
    }

    public boolean isOval() {
        return mIsOval;
    }

    public void setOval(boolean oval) {
        if (mIsOval != oval) {
            mIsOval = oval;
            updateDrawable();
        }
    }

    @Override
    public void setScaleType(ScaleType scaleType) {
        if (!mSupportRounded) {
            super.setScaleType(scaleType);
        }
    }

    @Override
    public ScaleType getScaleType() {
        if (mSupportRounded) {
            return CENTER_CROP;
        } else {
            return super.getScaleType();
        }
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);
        initBitmap();
    }

    @Override
    public void setImageResource(int resId) {
        super.setImageResource(resId);
        initBitmap();
    }

    @Override
    public void setImageDrawable(@Nullable Drawable drawable) {
        super.setImageDrawable(drawable);
        initBitmap();
    }

    @Override
    protected boolean setFrame(int l, int t, int r, int b) {
        boolean change = super.setFrame(l,t,r,b);
        mHaveFrame = true;
        updateDrawable();
        return change;
    }

    @Override
    protected void onDetachedFromWindow() {
        recycleBitmap();
        super.onDetachedFromWindow();
    }

    private void recycleBitmap() {
        if (mBitmap != null && mBitmap.isMutable() &&!mBitmap.isRecycled()) {
            mBitmap.recycle();
        }
    }

    private void initBitmap() {
        Drawable drawable = getDrawable();
        if (drawable == null) {
            mBitmap = null;
            invalidate();
            return;
        }
        if (drawable instanceof BitmapDrawable) {
            mBitmap = ((BitmapDrawable) drawable).getBitmap();
        } else {
            try {
                Bitmap bitmap;
                if (drawable instanceof ColorDrawable) {
                    bitmap = Bitmap.createBitmap(COLOR_DRAWABLE_SIZE,COLOR_DRAWABLE_SIZE, BITMAP_CONFIG);
                } else {
                    bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),drawable.getIntrinsicHeight(),BITMAP_CONFIG);
                }
                Canvas canvas = new Canvas(bitmap);
                drawable.setBounds(0,0,canvas.getWidth(),canvas.getHeight());
                drawable.draw(canvas);
                mBitmap = bitmap;
                mBitmapShader = null;
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
        }
        updateDrawable();
    }

    private void invalidateDrawable() {
        if (!mIsOval) {
            mBorderPath.reset();
            mBorderPath.addRoundRect(mBorderRectF,mBorderRadii, Path.Direction.CW);
            mDrawablePath.reset();
            mDrawablePath.addRoundRect(mDrawableRectF,mDrawableRadii, Path.Direction.CW);
        }
        invalidate();
    }

    private boolean hasBorder() {
        return mBorderSize > 0;
    }

    private boolean hasRadii(int index) {
        return mBorderRadii[index] > 0;
    }

    private void setDrawableRadius(int index, float radius) {
        mDrawableRadii[index] = mDrawableRadii[index+1] = radius;
    }

    private void setDrawableRadiusOffset(float offset) {
        for (int direction:DIRECTION) {
            if (hasRadii(direction)) {
                setDrawableRadius(direction,mBorderRadii[direction]+offset);
            }
        }
    }

    private void updateDrawable() {
        if (mBitmap == null || !mHaveFrame) {
            return;
        }
        if (mBitmapShader == null) {
            mBitmapShader = new BitmapShader(mBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        }
        int pLeft = getPaddingLeft();
        int pTop = getPaddingTop();
        int availableWidth = getMeasuredWidth() - pLeft - getPaddingRight();
        int availableHeight = getMeasuredHeight() - pTop - getPaddingBottom();
        //set bitmap and border bounds
        mBorderRectF.set(pLeft,pTop,pLeft+availableWidth,pTop+availableHeight);
        mDrawableRectF.set(mBorderRectF);
        if (hasBorder()) {
            float offset = mBorderSize/2f;
            mBorderRectF.inset(offset,offset);
            if (!mBorderOverlay) {
                mDrawableRectF.inset(mBorderSize,mBorderSize);
                if (!mIsOval) {
                    setDrawableRadiusOffset(-mBorderSize);
                }
            } else {
                setDrawableRadiusOffset(mBorderSize);
            }
        } else {
            setDrawableRadiusOffset(0);
        }
        //update image matrix
        updateDrawableMatrix(mDrawableRectF.width(), mDrawableRectF.height());
        invalidateDrawable();
    }

    private void updateDrawableMatrix(float fwidth, float fheight) {
        int bitmapWidth = mBitmap.getWidth();
        int bitmapHeight = mBitmap.getHeight();
        if (bitmapWidth <= 0 || bitmapHeight <= 0) {
            return;
        }
        if (mDrawableMatrix == null) {
            mDrawableMatrix = new Matrix();
        }
        float scale;
        float dx = 0, dy = 0;
        if (bitmapWidth * fheight > fwidth * bitmapHeight) {
            scale = fheight / (float) bitmapHeight;
            dx = (fwidth - bitmapWidth * scale) * 0.5f;
        } else {
            scale = fwidth / (float) bitmapWidth;
            dy = (fheight - bitmapHeight * scale) * 0.5f;
        }

        mDrawableMatrix.set(null);
        mDrawableMatrix.setScale(scale, scale);
        mDrawableMatrix.postTranslate((int) (dx + 0.5f) + mDrawableRectF.left, (int) (dy + 0.5f) + mDrawableRectF.top);
        mBitmapShader.setLocalMatrix(mDrawableMatrix);
        mDrawablePaint.setShader(mBitmapShader);
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        if (mMaskColor != null && mMaskColor.isStateful()) {
            updateColors();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (!mSupportRounded) {
            super.onDraw(canvas);
            return;
        }
        if (mBitmap == null) {
            return;
        }
        if (mIsOval) {
            canvas.drawOval(mDrawableRectF, mDrawablePaint);
            drawOvalMask(canvas);
            if (mBorderSize > 0) {
                canvas.drawOval(mBorderRectF, mBorderPaint);
            }
        } else {
            drawRoundImage(canvas);
        }
    }

    private void drawRoundImage(Canvas canvas) {
        canvas.drawPath(mDrawablePath, mDrawablePaint);
        drawRoundMask(canvas);
        if (hasBorder()) {
            canvas.drawPath(mBorderPath,mBorderPaint);
        }
    }

    private void drawOvalMask(Canvas canvas) {
        if (isPressed()) {
            canvas.drawOval(mDrawableRectF,mMaskPaint);
        }
    }

    private void drawRoundMask(Canvas canvas) {
        if (isPressed()) {
            canvas.drawPath(mDrawablePath,mMaskPaint);
        }
    }
}