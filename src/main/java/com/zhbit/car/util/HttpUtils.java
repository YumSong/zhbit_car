package com.zhbit.car.util;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * http请求工具类
 * Created by lgj on 2016/11/1.
 */
public class HttpUtils {

    public static String sendGetRequest(String url) throws Exception {

        // (1) 创建HttpGet实例
        HttpGet get = new HttpGet(url);

        // (2) 使用HttpClient发送get请求，获得返回结果HttpResponse
        HttpClient http = new DefaultHttpClient();
        HttpResponse response = http.execute(get);

        // (3) 读取返回结果
        if (response.getStatusLine().getStatusCode() == 200) {
            HttpEntity entity = response.getEntity();
            InputStream in = entity.getContent();
            return readResponse(in);
        }else {
            return "{\"success\":false,\"err_msg\":\"网络请求失败\",\"data\":\"\"{}\"";
        }
    }

    public static String sendPostRequest(String url, Map<String, String> map) throws Exception {
        HttpPost post = new HttpPost(url);
        //添加参数
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            params.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
        }
        post.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
        // (2) 使用HttpClient发送get请求，获得返回结果HttpResponse
        HttpClient http = new DefaultHttpClient();
        HttpResponse response = http.execute(post);
        // (3) 读取返回结果
        if (response.getStatusLine().getStatusCode() == 200) {
            HttpEntity entity = response.getEntity();
            InputStream in = entity.getContent();
            return readResponse(in);
        }else {
            return "{\"success\":false,\"err_msg\":\"网络请求失败\",\"data\":\"\"{}\"";
        }
    }

    /**
     * 功能: postBody形式发送数据
     *
     * @param urlPath 对方地址
     * @param json    要传送的数据
     * @return
     * @throws Exception
     */
    public static String sendPostBody(String urlPath, String json) throws Exception {
        try{
            // Configure and open a connection to the site you will send the request
            URL url = new URL(urlPath);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            // 设置doOutput属性为true表示将使用此urlConnection写入数据
            urlConnection.setDoOutput(true);
            // 定义待写入数据的内容类型，我们设置为application/x-www-form-urlencoded类型
            urlConnection.setRequestProperty("content-type", "raw");
            // 得到请求的输出流对象
            OutputStreamWriter out = new OutputStreamWriter(urlConnection.getOutputStream());
            // 把数据写入请求的Body
            out.write(json);
            out.flush();
            out.close();

            // 从服务器读取响应
            if(urlConnection.getResponseCode()==200){
                InputStream inputStream = urlConnection.getInputStream();
                return readResponse(inputStream);
            }
        }catch(IOException e){
            e.printStackTrace();
        }
        return "失败";
    }

    public static String readResponse(InputStream in) throws Exception{
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        String line = null;
        String result = "";
        while ((line = reader.readLine()) != null) {
            result += line;
        }
        return result;
    }

}
