package cn.com.hesc.roundview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.util.AttributeSet;
import android.widget.ImageView;

import cn.com.hesc.devutilslibrary.R;


/**
 * ProjectName: FastDev-master
 * ClassName: RoundImageView
 * Description: 原形imageview
 * Author: liujunlin
 * Date: 2018-02-23 15:52
 * Copyright: (C)HESC Co.,Ltd. 2016. All rights reserved.
 */
public class RoundImageView extends ImageView{
    private Context context;

    // default border color
    private final int DEFAULT_COLOR = 0xFFFFFFFF;
    private final int DEFAUTL_BORDER_THICKNESS = 0;
    // border thickness
    private int borderThickness = 0;
    private int borderInsideThickness = 0;
    private int borderOutsideThickness = 0;

    // border Color,if you want have two border,you can save two colors.
    private int borderColor = 0;
    private int borderInsideColor = 0;
    private int borderOutsideColor = 0;

    // The Image View default size
    private int defaultWidth;
    private int defaultHeight;

    public RoundImageView(Context context) {
        super(context);
        this.context = context;
    }

    public RoundImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        this.setCustomAttributes(attrs);
    }

    public RoundImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        this.setCustomAttributes(attrs);
    }

    /**
     * Set custom attributes<br/>
     * you can use custom label to define the attribute.
     */
    private void setCustomAttributes(AttributeSet attrs) {
        TypedArray types = this.context.obtainStyledAttributes(attrs,
                R.styleable.roundedimageview);

        borderThickness = types.getDimensionPixelSize(
                R.styleable.roundedimageview_border_thickness,
                DEFAUTL_BORDER_THICKNESS);
        borderInsideThickness = types.getDimensionPixelSize(
                R.styleable.roundedimageview_border_inside_thickness,
                DEFAUTL_BORDER_THICKNESS);
        borderOutsideThickness = types.getDimensionPixelSize(
                R.styleable.roundedimageview_border_outside_thickness,
                DEFAUTL_BORDER_THICKNESS);
        // get the border thickness, Single attribute has higher priority
        if (borderInsideThickness == DEFAUTL_BORDER_THICKNESS) {
            borderInsideThickness = borderThickness;
        }
        if (borderOutsideThickness == DEFAUTL_BORDER_THICKNESS) {
            borderOutsideThickness = borderThickness;
        }

        borderColor = types.getColor(R.styleable.roundedimageview_border_color,
                DEFAULT_COLOR);
        borderInsideColor = types
                .getColor(R.styleable.roundedimageview_border_inside_color,
                        DEFAULT_COLOR);
        borderOutsideColor = types.getColor(
                R.styleable.roundedimageview_border_outside_color,
                DEFAULT_COLOR);
        // get The border color,Single attribute has higher priority
        if (borderInsideColor == DEFAULT_COLOR) {
            borderInsideColor = borderColor;
        }
        if (borderOutsideColor == DEFAULT_COLOR) {
            borderOutsideColor = borderColor;
        }

    }

    public void setBorderThickness(int borderThickness) {

        this.borderThickness = borderThickness;
        this.borderOutsideThickness = borderThickness;
        this.borderInsideThickness = borderThickness;
    }

    public void setBorderInsideThickness(int borderInsideThickness) {
        this.borderInsideThickness = borderInsideThickness;
    }

    public void setBorderOutsideThickness(int borderOutsideThickness) {
        this.borderOutsideThickness = borderOutsideThickness;
    }

    public void setBorderColor(int borderColor) {
        this.borderColor = borderColor;
        this.borderOutsideColor = borderColor;
        this.borderInsideColor = borderColor;
    }

    public void setBorderInsideColor(int borderInsideColor) {
        this.borderInsideColor = borderInsideColor;
    }

    public void setBorderOutsideColor(int borderOutsideColor) {
        this.borderOutsideColor = borderOutsideColor;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Drawable drawable = this.getDrawable();
        if (drawable == null) {
            return;
        }

        if (getWidth() == 0 || getHeight() == 0) {
            return;
        }

        this.measure(0, 0);
        if (drawable.getClass() == NinePatchDrawable.class) {
            return;
        }

        Bitmap b = ((BitmapDrawable) drawable).getBitmap();
        Bitmap bitmap = b.copy(Bitmap.Config.ARGB_8888, true);
        if (defaultWidth == 0) {
            defaultWidth = getWidth();

        }
        if (defaultHeight == 0) {
            defaultHeight = getHeight();
        }

        int minSize = Math.min(defaultHeight, defaultHeight);
        // radius = (Radius of the maximum circle that can be drawn in the
        // image) - (inside border thickness) - (outside border thickness)
        int radius = minSize / 2 - borderInsideThickness
                - borderOutsideThickness;

        float cx = Math.max(defaultWidth, minSize) / 2.0f;
        float cy = Math.max(defaultHeight, minSize) / 2.0f;
        System.out.println(cx + "   " + cy + " " + radius + " " + borderInsideThickness + " " + borderInsideColor);
        // drawInsideBorder
        drawCircleBorder(canvas, cx, cy, radius + borderInsideThickness / 2, borderInsideColor, borderInsideThickness);
        // drawOutSideBorder
        drawCircleBorder(canvas, cx, cy, radius + borderInsideThickness + borderOutsideThickness / 2, borderOutsideColor, borderOutsideThickness);
        Bitmap roundBitmap = getCroppedRoundBitmap(bitmap, radius);
        canvas.drawBitmap(roundBitmap, defaultWidth / 2 - radius, defaultHeight
                / 2 - radius, null);

    }


    private Bitmap getCroppedRoundBitmap(Bitmap bmp, int radius) {
        Bitmap scaledSrcBmp;
        int diameter = radius * 2;
        int bmpWidth = bmp.getWidth();
        int bmpHeight = bmp.getHeight();
        int squareWidth = 0, squareHeight = 0;
        int x = 0, y = 0;
        Bitmap squareBitmap;
        if (bmpHeight > bmpWidth) {
            squareWidth = squareHeight = bmpWidth;
            x = 0;
            y = (bmpHeight - bmpWidth) / 2;
            squareBitmap = Bitmap.createBitmap(bmp, x, y, squareWidth,
                    squareHeight);
        } else if (bmpHeight < bmpWidth) {
            squareWidth = squareHeight = bmpHeight;
            x = (bmpWidth - bmpHeight) / 2;
            y = 0;
            squareBitmap = Bitmap.createBitmap(bmp, x, y, squareWidth,
                    squareHeight);
        } else {
            squareBitmap = bmp;
        }

        if (squareBitmap.getWidth() != diameter
                || squareBitmap.getHeight() != diameter) {
            scaledSrcBmp = Bitmap.createScaledBitmap(squareBitmap, diameter,
                    diameter, true);

        } else {
            scaledSrcBmp = squareBitmap;
        }
        Bitmap output = Bitmap.createBitmap(scaledSrcBmp.getWidth(),
                scaledSrcBmp.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        Paint paint = new Paint();
        Rect rect = new Rect(0, 0, scaledSrcBmp.getWidth(),
                scaledSrcBmp.getHeight());

        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);
        canvas.drawARGB(0, 0, 0, 0);
        canvas.drawCircle(scaledSrcBmp.getWidth() / 2,
                scaledSrcBmp.getHeight() / 2, scaledSrcBmp.getWidth() / 2,
                paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(scaledSrcBmp, rect, rect, paint);

        bmp = null;
        squareBitmap = null;
        scaledSrcBmp = null;
        return output;
    }




    /**
     * Draw circle border
     */
    private void drawCircleBorder(Canvas canvas, float cx, float cy, float radius,
                                  int color, int thickness) {
        Paint paint = new Paint();
		/* Anti-aliasing */
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);
        paint.setColor(color);
		/* Set the paint's style is STROKE: Hollow*/
        paint.setStyle(Paint.Style.STROKE);
		/* set stroke width */
        paint.setStrokeWidth(thickness);
        canvas.drawCircle(cx, cy, radius, paint);
    }
}
