import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HttpServer1Copy {
    private ServerSocket serverSocket = null;

    public HttpServer1Copy(int port) throws IOException {
        serverSocket = new ServerSocket(port);
    }

    public void start() throws IOException {
        System.out.println("服务器启动");
        ExecutorService executorService = Executors.newCachedThreadPool();
        while (true) {
            Socket clientSocket = serverSocket.accept();
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    processConnection(clientSocket);
                }
            });
        }
    }

    private void processConnection(Socket clientSocket) {
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             BufferedWriter bufferedWriter=new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()))){
            // 1. 读取请求并解析


            //  a) 解析首行, 三个部分使用空格切分
            String firstLine = bufferedReader.readLine();
            String[] firstLineTokens = firstLine.split(" ");
            String method = firstLineTokens[0];
            String url = firstLineTokens[1];
            String version = firstLineTokens[2];
            //  b) 解析 header, 按行读取, 然后按照冒号空格来分割键值对
            Map<String,String> headers = new HashMap<>();
            String line = "";
            while ((line = bufferedReader.readLine()) != null && line.length() != 0) {
                String[] headerTokens = line.split(": " );
                headers.put(headerTokens[0],headerTokens[1]);
            }
            //  c) 解析 body (暂时先不考虑)
            System.out.printf("%s %s %s\n",method,url,version);
            for (Map.Entry<String,String>entry : headers.entrySet()) {
                System.out.println(entry.getKey() + ":" + entry.getValue());
            }
            System.out.println();
            // 2. 根据请求计算响应
            String resp = "";
            if (url.equals("/ok")) {
                bufferedWriter.write(version+"200 ok\n");
                resp = "<h1>hello</h1>";
            }else {
                bufferedWriter.write(version+"200 ok\n");
                resp = "<h1>default</h1>";
            }
            // 3. 把响应写回到客户端
            bufferedWriter.write("Content-Type:text/html\n");
            bufferedWriter.write("Content-length:" + resp.getBytes().length + "\n");
            bufferedWriter.write("\n");
            bufferedWriter.write(resp);
            bufferedWriter.flush();
        } catch(IOException e){
                e.printStackTrace();
        }finally {
            try{
                clientSocket.close();
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws IOException {
        HttpServer1Copy httpServer1Copy = new HttpServer1Copy(9090);
        httpServer1Copy.start();

    }
}

