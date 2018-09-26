package io.chengguo.streaming.rtp;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

import io.chengguo.streaming.rtsp.IResolver;

/**
 * RTP解析器
 * Created by fingerart on 2018-09-13.
 */
public class RTPResolver implements IResolver<Integer, RtpPacket> {

    private DataInputStream inputStream;
    private IResolverCallback<RtpPacket> resolverCallback;

    @Override
    public void regist(InputStream inputStream) {
        this.inputStream = new DataInputStream(inputStream);
    }

    @Override
    public void resolve(Integer rtpLength) throws IOException {

    }

    @Override
    public void setResolverCallback(IResolverCallback<RtpPacket> resolverCallback) {
        this.resolverCallback = resolverCallback;
    }

    @Override
    public void release() {
        inputStream = null;
        resolverCallback = null;
    }
}