package cn.com.hesc.moreshapeimageview;

import android.content.Context;
import android.util.AttributeSet;

import cn.com.hesc.moreshapeimageview.shader.BubbleShader;
import cn.com.hesc.moreshapeimageview.shader.ShaderHelper;


/**
 * ProjectName: FastDev-master
 * ClassName: BubbleImageView
 * Description: 类似微信聊天图片展示组件
 * Author: liujunlin
 * Date: 2017-05-15 16:27 
 * Copyright: (C)HESC Co.,Ltd. 2016. All rights reserved.
 */
public class BubbleImageView extends ShaderImageView{

    private BubbleShader shader;

    public BubbleImageView(Context context) {
        super(context);
    }

    public BubbleImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BubbleImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected ShaderHelper createImageViewHelper() {
        shader = new BubbleShader();
        return shader;
    }

    public int getTriangleHeightPx() {
        if(shader != null) {
            return shader.getTriangleHeightPx();
        }
        return 0;
    }

    public void setTriangleHeightPx(final int triangleHeightPx) {
        if(shader != null) {
            shader.setTriangleHeightPx(triangleHeightPx);
            invalidate();
        }
    }

    public BubbleShader.ArrowPosition getArrowPosition() {
        if(shader != null) {
            return shader.getArrowPosition();
        }

        return BubbleShader.ArrowPosition.LEFT_CENTER;
    }

    public void setArrowPosition(final BubbleShader.ArrowPosition arrowPosition) {
        if(shader != null) {
            shader.setArrowPosition(arrowPosition);
            invalidate();
        }
    }
}
