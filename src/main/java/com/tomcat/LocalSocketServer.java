package com.tomcat;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.swing.Spring;

public class LocalSocketServer {

  // 定义线程数
  static ExecutorService pool = Executors.newFixedThreadPool(150);

  public static boolean isNull (String line) {
    if (line != null && line.trim().length() > 10) {
      return true;
    }
    return false;
  }

  // main方法
  public static void main(String[] args) throws Exception {
    // 开启端口
    final ServerSocket server = new ServerSocket(8080);
    // server一直等待连接的到来
    while (true) {
      final Socket accept = server.accept();
      // 请求到来写入线程
      pool.execute(new Runnable() {
        public void run() {
          InputStream inputStream = null;
          InputStreamReader reader = null;
          BufferedReader bufferedReader = null;
          try {
            System.out.println("=================START=======================");
            // 接收数据
            inputStream = accept.getInputStream();
            reader = new InputStreamReader(inputStream);
            bufferedReader = new BufferedReader(reader);
            // 接收到数据打印
            String line = null;
            StringBuffer StringBuffer = new StringBuffer();
            while (isNull((line = bufferedReader.readLine()))) {
              System.out.println(line);
              StringBuffer.append(line);
            }
            String toString = StringBuffer.toString();
            // 返回数据
            //单独写一个类，处理接收的Socket，类的定义在下面
            accept.getOutputStream().
                write(("HTTP/1.1 200 OK\r\n" +  //响应头第一行
                    "Content-Type: text/html; charset=utf-8\r\n" +  //简单放一个头部信息
                    "\r\n" +  //这个空行是来分隔请求头与请求体的
                    "Hello world!\r\n").getBytes());
            accept.close();
            System.out.println("=================END=======================");
            System.out.println("Hello world!");
          } catch (IOException e) {
            e.printStackTrace();
          } catch (Exception e) {
            e.printStackTrace();
          } finally {
            try {
              bufferedReader.close();
            } catch (IOException e) {
            }
            try {
              reader.close();
            } catch (IOException e) {
            }
            try {
              inputStream.close();
            } catch (IOException e) {
            }
          }
        }
      });
    }
  }

  // 将InputStream转换成String
  public static String inputStreamTOString(InputStream in) throws Exception {
    ByteArrayOutputStream outStream = new ByteArrayOutputStream();
    byte[] data = new byte[4096];
    int count = -1;
    while ((count = in.read(data, 0, 4096)) != -1) {
      outStream.write(data, 0, count);
    }
    return new String(outStream.toByteArray(), "ISO-8859-1");
  }
}
