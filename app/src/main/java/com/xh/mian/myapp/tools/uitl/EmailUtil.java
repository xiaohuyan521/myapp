package com.xh.mian.myapp.tools.uitl;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;

import com.sun.mail.util.MailSSLSocketFactory;

import java.io.File;
import java.security.GeneralSecurityException;
import java.util.Properties;

import javax.activation.CommandMap;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.activation.MailcapCommandMap;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

/**
 * Created by hasee on 2019/2/28.
 */

public class EmailUtil {
    private Context mContext;
    private String patch;
    private String title;
    private String bodystr;
    private String receive;

    public EmailUtil(Context mContext,String patch,String receive,String title,String bodystr){
        this.mContext = mContext;
        this.patch = patch;
        this.title = title;
        this.bodystr = bodystr;
        this.receive = receive;
    }
    public void sendmial(String mgs){
        MailcapCommandMap mc = (MailcapCommandMap) CommandMap.getDefaultCommandMap();
        mc.addMailcap("text/html;; x-java-content-handler=com.sun.mail.handlers.text_html");
        mc.addMailcap("text/xml;; x-java-content-handler=com.sun.mail.handlers.text_xml");
        mc.addMailcap("text/plain;; x-java-content-handler=com.sun.mail.handlers.text_plain");
        mc.addMailcap("multipart/*;; x-java-content-handler=com.sun.mail.handlers.multipart_mixed");
        mc.addMailcap("message/rfc822;; x-java-content-handler=com.sun.mail.handlers.message_rfc822");
        CommandMap.setDefaultCommandMap(mc);
        Thread.currentThread().setContextClassLoader(getClass().getClassLoader());

        final ProgressDialog progressSync = new ProgressDialog(mContext);
        progressSync.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressSync.setMessage(mgs);
        progressSync.setCancelable(false);
        progressSync.setButton(DialogInterface.BUTTON_NEGATIVE, "取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        progressSync.show();

        AsyncTask<String, String, Integer> getTypeAsyncTask = new AsyncTask<String, String, Integer>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }
            @Override
            protected void onCancelled() {
                super.onCancelled();
            }
            @Override
            protected void onProgressUpdate(String... values) {
                super.onProgressUpdate(values);
            }
            @Override
            protected Integer doInBackground(String... params) {
                send();
                return null;
            }

            @Override
            protected void onPostExecute(Integer result) {
                progressSync.dismiss();
                okListener.success();
                //Toast.makeText(mContext, "发送完成", Toast.LENGTH_SHORT).show();
                super.onPostExecute(result);
            }

        };
        getTypeAsyncTask.execute();

    }

    public void send() {
        try {
            Properties props = new Properties();
            // 开启debug调试
            props.setProperty("mail.debug", "true");
            // 发送服务器需要身份验证
            props.setProperty("mail.smtp.auth", "true");
            // 设置邮件服务器主机名
            //props.setProperty("mail.host", "smtp.163.com");
            // 设置邮件服务器主机名
            props.setProperty("mail.host", "smtp.qq.com");
            // 发送邮件协议名称
            props.setProperty("mail.transport.protocol", "smtp");

            MailSSLSocketFactory msf = new MailSSLSocketFactory();
            msf.setTrustAllHosts(true);
            props.put("mail.smtp.ssl.enable", "true");
            props.put("mail.smtp.ssl.socketFactory", msf);

            // 设置环境信息
            Session session = Session.getInstance(props);

            // 创建邮件对象
            Message msg = new MimeMessage(session);
            msg.setSubject(title);
            // 设置邮件内容
            //msg.setText("这是一封由大当家发送的邮件！");
            // 设置发件人
            msg.setFrom(new InternetAddress("2112430294@qq.com"));
            msg.setRecipient(Message.RecipientType.TO, new InternetAddress(receive) );

            BodyPart contentPart = new MimeBodyPart();
            MimeMultipart multipart = new MimeMultipart();
            contentPart.setContent(bodystr, "text/html;charset=UTF-8");
            multipart.addBodyPart(contentPart);

            if(null!=patch && !"".equals(patch)){
                File file = new File(patch);
                BodyPart attachmentBodyPart = new MimeBodyPart();
                DataSource source = new FileDataSource(file);
                attachmentBodyPart.setDataHandler(new DataHandler(source));
                attachmentBodyPart.setFileName(file.getName());
                multipart.addBodyPart(attachmentBodyPart);
            }

            // 将多媒体对象放到message中
            msg.setContent(multipart);
            // 保存邮件
            msg.saveChanges();

            Transport transport = session.getTransport();
            // 连接邮件服务器
            transport.connect("smtp.qq.com","2112430294@qq.com", "hunkningioxzbjea");
            // 发送邮件
            //transport.sendMessage(msg, new Address[]{new InternetAddress("xh1818111@163.com")});
            transport.sendMessage(msg, msg.getAllRecipients());
            // 关闭连接
            transport.close();

        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
    }

    private OnOkButtonFireListener okListener;
    public interface OnOkButtonFireListener{
        public void success();
    }
    public void setOkListener(OnOkButtonFireListener okListener) {
        this.okListener = okListener;
    }
}
