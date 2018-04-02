package com.github.hcj.utils;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;

/**
 * 发送短信的工具类,该平台需要先添加ip白名单
 * @author xyyxhcj@qq.com
 * @date 2018/1/5
 */
public class UcpaasUtils {
    private static String url = "https://open.ucpaas.com/ol/sms/sendsms";

    public static class Sms {
        String sid = "4b76187ef71b662dbc0d26ff7444c6de"; //官网查看,每个用户不同
        String token = "eaecd1e38937a0bff7994205341aa250"; //官网查看,每个用户不同
        String appid = "35ed0e5096b24052aa28c61f65370d6c"; //官网查看,每个用户不同
        String templateId; //官网查看,短信模板id
        String param;
        String mobile;
        String uid; //回传识别数据

        public Sms(String mobile, String param, String uid,String templateId) {
            this.param = param;
            this.mobile = mobile;
            this.uid = uid;
            this.templateId=templateId;
        }

        public String getSid() {
            return sid;
        }

        public String getToken() {
            return token;
        }

        public String getAppid() {
            return appid;
        }

        public String getTemplateId() {
            return templateId;
        }

        public String getParam() {
            return param;
        }

        public String getMobile() {
            return mobile;
        }

        public String getUid() {
            return uid;
        }
    }

    public static String sendSms(String mobile, String param,String templateId) {
        Sms sms = new Sms(mobile, param, mobile,templateId);
        //使用JacksonJson
        String jsonString = JsonUtils.objectToJson(sms);
        //使用FastJson
        //String jsonString = JSONObject.toJSONString(sms);
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        try {
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            URLConnection conn = realUrl.openConnection();
            // 设置请求属性
            conn.setRequestProperty("accept", "application/json");
            conn.setRequestProperty("Content-Type", "application/json;charset=utf-8");
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            // 获取URLConnection对象对应的输出流
            out = new PrintWriter(conn.getOutputStream());
            // 发送请求参数
            System.out.println(jsonString);
            out.print(jsonString);
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            System.out.println("发送 POST 请求出现异常！"+e);
            e.printStackTrace();
        }
        //使用finally块来关闭输出流、输入流
        finally{
            try{
                if(out!=null){
                    out.close();
                }
                if(in!=null){
                    in.close();
                }
            }
            catch(IOException ex){
                ex.printStackTrace();
            }
        }
        return result;
    }

    public static void main(String[] args) {
        //String result = sendSms("18817237197", "6789","263192");
        //System.out.println(result);
    }
}
