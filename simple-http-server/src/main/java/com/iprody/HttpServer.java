package com.iprody;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;

public class HttpServer {

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(8080);
        System.out.println("Server started at http://localhost:8080");
        while (true) {
            String pageAddress = "static";
            Socket clientSocket = serverSocket.accept();
            BufferedReader in = new BufferedReader(new
                    InputStreamReader(clientSocket.getInputStream()));
            BufferedWriter out = new BufferedWriter(new
                    OutputStreamWriter(clientSocket.getOutputStream()));
            // отдельный для картинок и прочего что передается байтами
            // я не уверена что это правильно но и как впихнуть в текущую реализацию картинки я не понимаю
            OutputStream outBytes = clientSocket.getOutputStream();
// Чтение запроса
            String line;
            boolean first = true;
            while ((line = in.readLine()) != null && !line.isEmpty()) {
                if (first) {
                    String[] getData = line.split(" ");
                    //адрес страницы идет сразу после метода в первой строке
                    pageAddress += getData[1];//.substring(1);
                    //System.out.println("THIS IS OUR FILE PATH "+pageAddress);
                }
                // пока закомментировала а то неудобно отлаживать
                //System.out.println(line);
                // помеяли флаг чтобы больше ничего не тащить
                first = false;
            }
// Простой ответ
            String status = "HTTP/1.1 ";
            String response = "";
            boolean fileOK = false;
            try {
                PageHtml findedPage = new PageHtml(pageAddress);
                System.out.println("THIS IS OUR FILE PATH "+findedPage.pagePath);
                System.out.println("THIS IS OUR FILE SIZE "+findedPage.pageSize);
                System.out.println("THIS IS OUR CONTENT TYPE "+findedPage.pageContentType);
                status += "200 OK\r\n";
                fileOK = true;
                // кажется что можно формирование заголовка добавить в методы класса но пока не стала
                status += ("Content-Type: "+findedPage.pageContentType);
                if (!findedPage.pageBytesFlag)
                {
                    status += ("; charset=UTF-8");
                }
                status += "\r\n";
                status += ("Content-Length: " + findedPage.pageSize + "\r\n");
                System.out.println(status);
                out.write(status);
                out.write("\r\n");
                out.flush();
                if (!findedPage.pageBytesFlag) {
                    // текст пишем как в учебном примере в BufferedWriter
                    response = new String(Files.readAllBytes(findedPage.pagePath), StandardCharsets.UTF_8);
                    out.write(response);
                }
                else {
                    // а байты в OutputStream
                    InputStream is = Files.newInputStream(findedPage.pagePath);
                    is.transferTo(outBytes);
                    //outBytes.write(findedPage.pageBody);
                    // flush тоже вразнобой и некрасиво: общий для аута я пишу в самом конце
                    // так как там он происходит и для ответа текста и для ошибки
                    outBytes.flush();
                }
            } catch (IllegalArgumentException e) {
                status += "404 Not Found\r\n";
                status += "Content-Type: text/html; charset=UTF-8\r\n";
                response=e.getMessage();
                // тут немножко некрасивое ветвление с частичным дублированием того что внутри try выше
                // не получилось аккуратно задедублировать из-за ветвления и записи в разные out-ы
                out.write(status);
                out.write("\r\n");
                out.write(response);
            }
            System.out.println(status);
            System.out.println(response);
            out.flush();
            clientSocket.close();
        }
    }

    static class PageHtml {
        private final Path pagePath;
        private final Long pageSize;
        //private final byte[] pageBody;
        private final String pageContentType;
        private boolean pageBytesFlag = false; // флаг является ли файл бинарным
        PageHtml(String pathString) throws IOException {
            //System.out.println(System.getProperty("user.dir"));
            System.out.println("IS EXISTS "+Files.exists(Paths.get(pathString)));
            if (! Files.exists(Paths.get(pathString)) || !Files.isRegularFile(Paths.get(pathString))) {
                throw new IllegalArgumentException(
                        // не уверена насколько правильно делать проверку на существование файла в конструкторе
                        // но пусть будет здесь
                        // проверку на директорию надо бы тоже вынести в отдельную ошибку чтобы было понятно
                        "Files not found"
                );
            }
            this.pagePath = Paths.get(pathString);
            this.pageSize = Files.size(Paths.get(pathString));
            //this.pageBody = Files.readAllBytes(this.pagePath);
            this.pageContentType = Files.probeContentType(this.pagePath);
            if (!this.pageContentType.startsWith("text/")
                    && !this.pageContentType.equals("application/json")
                    && !this.pageContentType.endsWith("xml"))
            {
                this.pageBytesFlag = true;
            }
        }
    }
}