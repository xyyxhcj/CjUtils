package com.github.hcj.utils;

import org.csource.common.IniFileReader;
import org.csource.common.NameValuePair;
import org.csource.fastdfs.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

/**
 * FastDFS上传图片工具类
 *
 * @author xyyxhcj@qq.com
 * @date 2018/2/6
 */
public class FastDFSClient {
    private TrackerClient trackerClient = null;
    private TrackerServer trackerServer = null;
    private StorageServer storageServer = null;
    private StorageClient1 storageClient = null;
    private String conf_filename;

    /**
     * @param conf 如果包含"classpath:"->从classes路径加载配置,否则从绝对路径
     * @throws Exception
     */
    public FastDFSClient(String conf) throws Exception {
        if (conf.contains("classpath:")) {
            conf = conf.replace("classpath:", this.getClass().getResource("/").getPath());
        }
        ClientGlobal.init(conf);
        trackerClient = new TrackerClient();
        trackerServer = trackerClient.getConnection();
        storageServer = null;
        storageClient = new StorageClient1(trackerServer, storageServer);
        conf_filename = conf;
    }

    public String[] getTrackerServer() throws IOException {
        IniFileReader iniReader = new IniFileReader(conf_filename);
        return iniReader.getValues("tracker_server");
    }

    /**
     * 上传文件方法
     * <p>Title: uploadFile</p>
     * <p>Description: </p>
     *
     * @param fileName 文件全路径
     * @param extName  文件扩展名,不包含"."
     * @param metas    文件扩展信息
     * @return
     * @throws Exception
     */
    public String uploadFile(String fileName, String extName, NameValuePair[] metas) throws Exception {
        String result = storageClient.upload_file1(fileName, extName, metas);
        return result;
    }

    public String uploadFile(String fileName) throws Exception {
        return uploadFile(fileName, null, null);
    }

    public String uploadFile(String fileName, String extName) throws Exception {
        return uploadFile(fileName, extName, null);
    }

    /**
     * 上传文件方法
     * <p>Title: uploadFile</p>
     * <p>Description: </p>
     *
     * @param fileContent 文件的内容,字节数组
     * @param extName     文件扩展名
     * @param metas       文件扩展信息
     * @return
     * @throws Exception
     */
    public String uploadFile(byte[] fileContent, String extName, NameValuePair[] metas) throws Exception {
        String result = storageClient.upload_file1(fileContent, extName, metas);
        return result;
    }

    public String uploadFile(byte[] fileContent) throws Exception {
        return uploadFile(fileContent, null, null);
    }

    public String uploadFile(byte[] fileContent, String extName) throws Exception {
        return uploadFile(fileContent, extName, null);
    }

    public static void main(String[] args) throws Exception {
        //1.创建一个配置文件fast_dfs.conf,指定TrackerServer的地址
        //2.加载配置文件
        String filePath = FastDFSClient.class.getClassLoader().getResource("fast_dfs.conf").getPath();
        ClientGlobal.init(filePath);
        //3.创建一个TrackerClient对象
        TrackerClient trackerClient = new TrackerClient();
        //4.通过TrackerClient对象获得TrackerServer对象
        TrackerServer trackerServer = trackerClient.getConnection();
        //5.创建一个StorageServer的引用
        StorageServer storageServer = null;
        //6.创建一个StorageClient对象,需要两个参数TrackerServer,StorageServer
        StorageClient storageClient = new StorageClient(trackerServer, storageServer);
        File file = new File("F:\\temp\\images\\1.JPG");
        InputStream inputStream = new FileInputStream(file);
        byte[] bys = new byte[1024000];
        inputStream.read(bys);
        //7.使用StorageClient对象上传文件,参数1:图片路径或字节数组,参数2:扩展名,参数3:文件元数据,可为空
        String[] strings = storageClient.upload_file(bys, "jpg", null);
        System.out.println(Arrays.toString(strings));
    }
}
