package io.chengguo.streaming.rtp;

import java.io.InputStream;

import io.chengguo.streaming.rtsp.IResolver;

/**
 * RTP解析器
 * Created by fingerart on 2018-09-13.
 */
public class RTPResolver implements IResolver<DataFrame> {
    @Override
    public void target(InputStream inputStream) {

    }

    @Override
    public void setResolverCallback(IResolverCallback<DataFrame> resolverCallback) {

    }

    @Override
    public void release() {

    }
}
