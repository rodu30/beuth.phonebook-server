package ui;

import dataStructure.EntryPair;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by romanduhr on 07.06.16.
 */
public class Server {

    private EntryPair[] phonebook;
    private int port;
    private ServerSocket serverSocket;
    private String host;


    public Server(EntryPair[] phonebook, int port) {
        this.phonebook = phonebook;
        this.port = port;
    }

    /**
     * starts server, ports etc
     *
     * @throws IOException
     */
    public void execute() throws IOException {

//        host = InetAddress.getLocalHost().getHostName();
        host = InetAddress.getLocalHost().getHostAddress();
        serverSocket = new ServerSocket(port);
        System.out.println("Welcome to the phone server at host: " + host + " and port: " + port);
        read();
    }

    /**
     * reads HTTP requests
     *
     */
    private void read() throws IOException {
        while (true) {

            Socket clientSocket = serverSocket.accept();
            System.out.println("Client connected and waiting for requests");

            // read input
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            String line = in.readLine();
            System.out.println("Incoming: " + line);

            // ignore favicon and send side
            if (line.startsWith("GET /favicon")) {
                System.out.println("Favicon request");
                in.close();
                continue;           // jump to next request ??
            }
//            else if (line.contains("?")) {
//                System.out.println("HTTP request with query data");
//                String[] get = line.split(" ");
//                String[] l = get[1].substring(2).split("&");
//                for (String str : l) {
//                    String[] k = str.split("=");
//                    if (k.length > 1) {
//                        map.put(k[0], URLDecoder.decode(k[1], "UTF-8"));
//                    } else {
//                        map.put(k[0], "");
//                    }
//                }
//            }



//            {
                System.out.println("Request processed");
//                BufferedWriter out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream());
                // Build HTTP response
                out.println("HTTP/1.1 200 OK");              // Header
                out.println("Content-Type: text/html");
                out.println();
                out.println("<!DOCTYPE html>");             // HTML
                out.println("<html lang=\"en\">");
                out.println("<head>");
                out.println("<meta charset=\"UTF-8\">");
                out.println("<title>My phone server</title>");
                out.println("</head>");
                out.println("<body>");
                out.println("<h1>Welcome to Roman`s phone server</h1>");
                out.println("<form method=get action=\"" + host + ":" + port + "\">");
                out.println("Please enter a name:<br>");
                out.println("<input type=\"text\" name=\"name\" value=\"Maier\">");
                out.println("<br>");
                out.println("Please enter a number:<br>");
                out.println("<input type=\"text\" name=\"number\" value=\"123\">");
                out.println("<br>");
                out.println("<input type=\"submit\">");
                out.println("</form>");
                out.println("</body>");
                out.println("</html>");
                out.println();
                out.flush();
                out.close();
                in.close();

//            }

        }
    }





}
