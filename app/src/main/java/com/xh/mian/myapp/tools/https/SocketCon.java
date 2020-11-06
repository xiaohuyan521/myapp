package com.xh.mian.myapp.tools.https;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;

/**
 * Created by hasee on 2019/1/11.
 */

public class SocketCon {
    //设置一个接口 提供外部数据 类似handle功能
    private OnOkButtonFireListener okListener;
    public interface OnOkButtonFireListener{
        public void onMessage(String resultStr);
    }
    public void setOkListener(OnOkButtonFireListener okListener) {
        this.okListener = okListener;
    }

    private BufferedReader br;
    private BufferedWriter bw;
    private InputStream in;
    private OutputStream out;
    private Socket socket = null;
    private SocketSender sender;
    private SocketReceiver receiver;

    public SocketCon(){
        try {
            socket = new Socket("ip地址", 8080);
            socket.setSoTimeout(1000);
            in = socket.getInputStream();
            out = socket.getOutputStream();
            br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            bw = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));
            //接收数据开启一个线程
            receiver = new SocketReceiver();
            //发送数据开启一个线程
            sender = new SocketSender();
            //
        }catch (Exception e){
            e.getMessage();
        }

    }

    //判断是否连接成功
    public boolean isConnected(){
        return socket.isConnected();
    }

    //发送消息
    public void sendMag(String mag){
        if(null!=sender && null!=mag){
            sender.sendStr = mag;
        }
    }

    public class SocketSender implements Runnable {
        private boolean alive;
        public Thread thread;
        public boolean ThreadStop;
        public String sendStr = "";
        public SocketSender() {
            ThreadStop=false;
            alive = true;
            sendStr = "";
            thread = new Thread(this);
            thread.start();
        }
        public void run() {
            while (alive) {
                if(ThreadStop){
                    break;
                }
                try {
                    if (null!=sendStr && !"".equals(sendStr)) {
                        bw.write(sendStr, 0, sendStr.length());
                        bw.flush();
                        sendStr = "";
                    }
                    synchronized (this) {
                        wait(100);
                    }
                    Thread.yield();
                } catch (Exception e) {
                    e.printStackTrace();
                    close();
                }
            }
        }

        public void free() {
            alive = false;
            ThreadStop=true;
        }
    }

    public class SocketReceiver implements Runnable {
        public boolean alive;
        public Thread thread;
        public boolean ThreadStop;

        public SocketReceiver() {
            alive = true;
            ThreadStop= false;
            thread = new Thread(this);
            thread.start();
        }

        public void run() {
            while (alive) {
                try {
                    if(ThreadStop){
                        break;
                    }
                    String text=br.readLine();
                    if(text!=null){
                        okListener.onMessage(text);
                    }
                    synchronized (this) {
                        wait(100);
                    }
                    Thread.yield();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        public void free() {
            alive = false;
            ThreadStop= true;
        }
    }

    public void close() {
        try {
            if (socket != null) {
                socket.close();
                socket = null;
            }
            if (sender != null) {
                sender.free();
                sender = null;
            }
            if (receiver != null) {
                receiver.free();
                receiver = null;
            }
            in = null;
            out = null;
            br = null;
            bw = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
