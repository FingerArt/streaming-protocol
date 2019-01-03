package io.chengguo.streaming.rtsp;

import android.util.Log;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

import io.chengguo.streaming.rtcp.IPacket;
import io.chengguo.streaming.rtcp.RTCPResolver;
import io.chengguo.streaming.rtp.RTPResolver;
import io.chengguo.streaming.rtp.RtpPacket;
import io.chengguo.streaming.rtsp.header.CSeqHeader;
import io.chengguo.streaming.rtsp.header.SessionHeader;
import io.chengguo.streaming.rtsp.header.UserAgentHeader;
import io.chengguo.streaming.transport.ITransport;
import io.chengguo.streaming.transport.TransportMethod;
import io.chengguo.streaming.utils.Utils;

/**
 * RTSP session
 * Created by fingerart on 2018-09-08.
 */
public class RTSPSession {

    private static final String TAG = RTSPSession.class.getSimpleName();

    private String host;
    private int port;
    private TransportMethod method;
    private ITransport transport;
    private String session;
    private AtomicInteger sequence = new AtomicInteger();
    private HashMap<Integer, Request> requestList = new HashMap<>();
    private IResolver.IResolverCallback<Response> mRtspResolverCallback;
    private IResolver.IResolverCallback<RtpPacket> mRtpResolverCallback;

    public RTSPSession(String host, int port, TransportMethod method) {
        this.host = host;
        this.port = port;
        this.method = method;
        setCallbacks();
    }

    private void setCallbacks() {
        transport = method.createTransport(host, port, 3000);
        transport.setRtspResolver(new RTSPResolver());
        transport.setRtpResolver(new RTPResolver());
        transport.setRtcpResolver(new RTCPResolver());
        transport.getRtspResolver().setResolverCallback(new IResolver.IResolverCallback<Response>() {
            @Override
            public void onResolve(Response response) {
                //获取暂存中的Request
                CSeqHeader cSeqHeader = response.getHeader(CSeqHeader.DEFAULT_NAME);
                if (cSeqHeader != null) {
                    Request request = requestList.get(cSeqHeader.getRawValue());
                    response.setRequest(request);
                    requestList.remove(cSeqHeader.getRawValue());
                }
                SessionHeader sessionHeader = response.getHeader(SessionHeader.DEFAULT_NAME);
                if (sessionHeader != null) {
                    session = sessionHeader.getSession();
                }
                //回调
                if (mRtspResolverCallback != null) {
                    mRtspResolverCallback.onResolve(response);
                }
            }
        });
        transport.getRtcpResolver().setResolverCallback(new IResolver.IResolverCallback<IPacket>() {
            @Override
            public void onResolve(IPacket iReport) {
                Log.d(TAG, iReport.toString());

            }
        });
        transport.getRtpResolver().setResolverCallback(new IResolver.IResolverCallback<RtpPacket>() {
            @Override
            public void onResolve(RtpPacket rtpPacket) {
                //回调
                mRtpResolverCallback.onResolve(rtpPacket);
            }
        });
    }

    public void connect() {
        transport.connectAsync();
    }

    /**
     * 设置传输监听器
     *
     * @param transportListener
     */
    public void setTransportListener(ITransportListener transportListener) {
        transport.setTransportListener(transportListener);
    }

    /**
     * 设置RTSP解析回调
     *
     * @param resolverCallback
     */
    public void setRTSPResolverCallback(IResolver.IResolverCallback<Response> resolverCallback) {
        mRtspResolverCallback = resolverCallback;
    }

    /**
     * 设置RTP解析回调
     *
     * @param rtpResolverCallback
     */
    public void setRTPResolverCallback(IResolver.IResolverCallback<RtpPacket> rtpResolverCallback) {
        mRtpResolverCallback = rtpResolverCallback;
    }

    /**
     * 设置RTCP解析回调
     *
     * @param rtcpResolverCallback
     */
    public void setRTCPResolverCallback(IResolver.IResolverCallback<IPacket> rtcpResolverCallback) {
        IResolver<Integer, IPacket> rtcpResolver = transport.getRtcpResolver();
        if (rtcpResolver != null) {
            rtcpResolver.setResolverCallback(rtcpResolverCallback);
        }
    }

    /**
     * 发送RTSP请求
     *
     * @param request
     */
    public void send(Request request) {
        int cseq = sequence.incrementAndGet();
        request.addHeader(new CSeqHeader(cseq));
        if (!Utils.isEmpty(session)) {
            request.addHeader(new SessionHeader(session, 0));
        }
        request.addHeader(new UserAgentHeader("ChengGuo"));
        StringBuilder sb = new StringBuilder();
        sb.append(">----------------------- ").append(request.getLine().getMethod()).append("\r\n")
                .append(request.toString())
                .append(">----------------------- ").append(request.getLine().getMethod());
        System.out.println(sb);
        //暂存Request
        requestList.put(cseq, request);
        //发送请求
        transport.send(request);
    }

    /**
     * 是否链接
     *
     * @return
     */
    public boolean isConnected() {
        return transport.isConnected();
    }

    /**
     * 断开链接
     */
    public void disconnect() {
        if (isConnected()) {
            transport.disconnect();
        }
    }
}