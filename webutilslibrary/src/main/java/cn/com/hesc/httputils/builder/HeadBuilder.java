package cn.com.hesc.httputils.builder;

import cn.com.hesc.httputils.OkHttpUtils;
import cn.com.hesc.httputils.request.OtherRequest;
import cn.com.hesc.httputils.request.RequestCall;

/**
 * Created by zhy on 16/3/2.
 */
public class HeadBuilder extends GetBuilder
{
    @Override
    public RequestCall build()
    {
        return new OtherRequest(null, null, OkHttpUtils.METHOD.HEAD, url, tag, params, headers).build();
    }
}
