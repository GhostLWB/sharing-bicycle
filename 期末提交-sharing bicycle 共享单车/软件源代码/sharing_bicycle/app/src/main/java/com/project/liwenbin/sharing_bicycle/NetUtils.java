package com.project.liwenbin.sharing_bicycle;

import android.os.Handler;
import android.os.Message;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by 宋羽珩 on 2017/5/3.
 * 用于网络请求的封装
 */
public class NetUtils {
    public static void getRequest(final String url, final Map<String,String> params, final Handler handler)
    {
        new Thread(){
            @Override
            public void run() {
                String response="";
                StringBuilder urlBuilder  = new StringBuilder();
                urlBuilder.append(url);
                //补充url参数params
                if(params!=null)
                {
                    urlBuilder.append("?");
                    Iterator<Map.Entry<String,String>> iterator = params.entrySet().iterator();
                    while (iterator.hasNext())
                    {
                        Map.Entry<String,String > param = iterator.next();
                        try {
                            urlBuilder.append(URLEncoder.encode(param.getKey(),"UTF-8"))
                                    .append("=")
                                    .append(URLEncoder.encode(param.getValue(),"UTF-8"));
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        if(iterator.hasNext())
                        {
                            urlBuilder.append("&");
                        }
                    }
                }
                //创建HttpClient对象
                HttpClient client = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet(urlBuilder.toString());
                try {
                    HttpResponse httpResponse = client.execute(httpGet);
                    if(httpResponse.getStatusLine().getStatusCode()==200)
                    {
                        HttpEntity entity = httpResponse.getEntity();
                        response = EntityUtils.toString(entity,"utf-8");
                        Message message = new Message();
                        message.what=0;
                        message.obj=response;
                        handler.sendMessage(message);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();


    }

    public static void postRequest(final String url, final List<BasicNameValuePair> params, final Handler handler)
    {
        new Thread(){
            @Override
            public void run() {
                String response = "";
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost= new HttpPost(url);
                try {
                    httpPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                try {
                    HttpResponse httpResponse = httpClient.execute(httpPost);
                    if(httpResponse.getStatusLine().getStatusCode()==200)
                    {
                        HttpEntity entity = httpResponse.getEntity();
                        response = EntityUtils.toString(entity,"utf-8");
                        Message message = new Message();
                        message.what=1;
                        message.obj=response;
                        handler.sendMessage(message);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }.start();
    }

}
