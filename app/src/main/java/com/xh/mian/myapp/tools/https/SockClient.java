package com.xh.mian.myapp.tools.https;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.Map;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;

/**
 * Created by hasee on 2019/1/11.
 */

public class SockClient extends WebSocketClient {
    //设置一个接口 提供外部数据 类似handle功能
    private OnOkButtonFireListener okListener;
    public interface OnOkButtonFireListener{
        public void onOpen();
        public void onMessage(String resultStr);
    }
    public void setOkListener(OnOkButtonFireListener okListener) {
        this.okListener = okListener;
    }

    public SockClient(String url,Map<String, String> var3) {
        //1请求地址, 2版本号需和服务端一直, 3上传参数, 4请求超时时间
        super(URI.create(url), new Draft_17(), var3, 120000);
    }

    //添加安全协议
    public void SetAgreement(){
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, null, null);
            SSLSocketFactory factory = sc.getSocketFactory();
            this.setSocket(factory.createSocket());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //连接服务器
    public void connect(){
        try {
            this.connectBlocking();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        System.out.println("握手成功");
        okListener.onOpen();
    }

    @Override
    public void onMessage(String s) {
        System.out.println("返回消息");
        //次位置可以对返回的s 数据进行处理使外面的数据可以更便捷使用 根据需求而定
        okListener.onMessage(s);
    }

    @Override
    public void onClose(int i, String s, boolean b) {
        System.out.println("关闭连接");
    }

    @Override
    public void onError(Exception e) {
        System.out.println("连接失败："+e.getMessage());
    }
}
