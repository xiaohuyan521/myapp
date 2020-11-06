package com.xh.mian.myapp.tools.https;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;

/**
     Created by hasee on 2019/1/10.
     StringBuilder paramBld = new StringBuilder();
     for (NameValuePair pair : paramList){
         paramBld.append("&");
         paramBld.append(URLEncoder.encode(pair.getName(), "UTF-8"));
         paramBld.append("=");
         paramBld.append(URLEncoder.encode(pair.getValue(), "UTF-8"));
     }
     if(paramBld.length()>0)
         paramBld.deleteCharAt(0);
 */

public class HttpUrl {

    /**
     * 字符串请求数据
     * @param strUrl 请求地址
     * @param paramBld 请求参数
     */
    public String postString(String strUrl, String paramBld) {
        String s = downloadFileUrlConn(strUrl,paramBld,null,null);
        return s;
    }

    /**
     * 上传文件
     * @param strUrl 请求地址
     * @param paramBld 请求参数
     * @param uploadFilePaths 上传文件批量
     */
    public String postPcth(String strUrl, String paramBld,String[]uploadFilePaths) {
        String s = downloadFileUrlConn(strUrl,paramBld,uploadFilePaths,null);
        return s;
    }

    /**
     * 下载文件
     * @param strUrl 请求地址
     * @param paramBld 请求参数
     * @param mdestFileName 下载到本地路径
     */
    public String postmdest(String strUrl, String paramBld,final String mdestFileName) {
        String s = downloadFileUrlConn(strUrl,paramBld,null,mdestFileName);
        return s;
    }
    //下载文件流到本地
    public String downloadFileUrlConn(String urlStr,String paramBld,String[]uploadFilePaths,String destFilename){

        URL url = null;
        HttpURLConnection conn = null;
        InputStream is = null;
        FileOutputStream os = null;
        try {
            url = new URL(urlStr);
            conn=(HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("char", "UTF-8");

            if(null!=paramBld && !"".equals(paramBld)){
                OutputStream connOs = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(connOs, "UTF-8"));
                writer.write(paramBld);
                writer.flush();
                writer.close();
                connOs.close();
            }
            if(null!=uploadFilePaths && uploadFilePaths.length>0){
                setfile(conn,uploadFilePaths);
            }

            conn.connect();
            int respCode=conn.getResponseCode();
            if (respCode == HttpURLConnection.HTTP_OK) {
                if(null==destFilename || "".equals(destFilename)){
                    int length = conn.getContentLength();
                    if (length < 0)
                        length = 10000;
                    StringBuilder inBld = new StringBuilder(length);
                    InputStreamReader inputStreamReader = new InputStreamReader(
                            conn.getInputStream(),  "UTF-8");
                    char buffer[] = new char[length];
                    int count;
                    while ((count = inputStreamReader.read(buffer, 0, length - 1)) > 0) {
                        inBld.append(buffer, 0, count);
                    }
                    conn.disconnect();
                    return inBld.toString();
                }else{
                    String disposition = conn.getHeaderField("Content-Disposition");
                    String contentType = conn.getContentType();
                    // 文件大小
                    int contentLength = conn.getContentLength();
                    is = conn.getInputStream();
                    os = new FileOutputStream(destFilename, false);
                    byte buffer[] = new byte[8192];
                    int readSize = 0;
                    while((readSize = is.read(buffer)) > 0){
                        os.write(buffer, 0, readSize);
                        os.flush();
                    }
                    os.close();
                    conn.disconnect();

                    return "SUCCESS";
                }
            }
        } catch (MalformedURLException e) {
            return e.toString();
        }catch (SocketTimeoutException e) {
            return e.toString();
        } catch (IOException e) {
            return e.toString();
        }
        return "";
    }
    private void setfile(HttpURLConnection httpURLConnection,String[]uploadFilePaths){
        try {
            String end = "\r\n";
            String twoHyphens = "--";
            String boundary = "*****";
            DataOutputStream ds = new DataOutputStream(httpURLConnection.getOutputStream());
            for (int i = 0; i < uploadFilePaths.length; i++) {
                String uploadFile = uploadFilePaths[i];
                String filename = uploadFile.substring(uploadFile.lastIndexOf("//") + 1);
                ds.writeBytes(twoHyphens + boundary + end);
                ds.writeBytes("Content-Disposition: form-data; " + "name=\"file" + i + "\";filename=\"" + filename
                        + "\"" + end);
                ds.writeBytes(end);
                FileInputStream fStream = new FileInputStream(uploadFile);
                int bufferSize = 1024;
                byte[] buffer = new byte[bufferSize];
                int length = -1;
                while ((length = fStream.read(buffer)) != -1) {
                    ds.write(buffer, 0, length);
                }
                ds.writeBytes(end);
                /* close streams */
                fStream.close();
            }
            ds.writeBytes(twoHyphens + boundary + twoHyphens + end);
        }catch (Exception e){
            e.getMessage();
        }
    }
}
