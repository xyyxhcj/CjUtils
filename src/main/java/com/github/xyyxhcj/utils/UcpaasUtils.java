package com.github.xyyxhcj.utils;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;

/**
 * 发送短信的工具类,该平台需要先添加ip白名单
 * @author xyyxhcj@qq.com
 * @since 2018/1/5
 */
public class UcpaasUtils {
    private static String url = "https://open.ucpaas.com/ol/sms/sendsms";

    /**
     * 测试
     * @param args args
     */
    public static void main(String[] args) {
        //String result = sendSms("18817237197", "6789","263192");
        //System.out.println(result);
    }

    /**
     *
     * @param mobile mobile
     * @param param param
     * @param templateId templateId
     * @return return
     */
    public static String sendSms(String mobile, String param,String templateId) {
        Sms sms = new Sms(mobile, param, mobile,templateId);
        String jsonString = JsonUtils.objectToJson(sms);
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        try {
            URL realUrl = new URL(url);
            URLConnection conn = realUrl.openConnection();
            conn.setRequestProperty("accept", "application/json");
            conn.setRequestProperty("Content-Type", "application/json;charset=utf-8");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            out = new PrintWriter(conn.getOutputStream());
            System.out.println(jsonString);
            out.print(jsonString);
            out.flush();
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

    public static class Sms {
        /**
         * 官网查看,每个用户不同
         */
        String sid = "4b76187ef71b662dbc0d36ff7449c9de";
        /**
         * 官网查看,每个用户不同
         */
        String token = "eaecd1e38937a0bff7994205341aa999";
        /**
         * 官网查看,每个用户不同
         */
        String appid = "35ed0e5096b24052aa28c61f65379d9c";
        /**
         * 官网查看,短信模板id
         */
        String templateId;
        /**
         * 模板中的参数
         */
        String param;
        /**
         * 接收的手机号
         */
        String mobile;
        /**
         * 回传识别数据
         */
        String uid;

        /**
         * @param mobile     mobile
         * @param param      param
         * @param uid        uid
         * @param templateId templateId
         */
        Sms(String mobile, String param, String uid, String templateId) {
            this.param = param;
            this.mobile = mobile;
            this.uid = uid;
            this.templateId = templateId;
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
}
