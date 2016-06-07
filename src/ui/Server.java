package ui;

import dataStructure.EntryPair;
import search.NameSearch;
import search.NumberSearch;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLDecoder;
import java.util.ArrayList;

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
            System.out.println("Incoming: '" + line + "'");

            // ignore favicon
            if (line.startsWith("GET /favicon")) {
                System.out.println("Favicon request");
                in.close();
                continue;           // jump to next request ??
            }

            // process request and send result
            if (line.contains("?")) {
                System.out.println("HTTP request with query data");
                // get name & number
                String[] query = line.split("=");
                String[] s1 = query[1].split("&");
                String inpName = URLDecoder.decode(s1[0], "UTF-8");
                String[] s2 = query[2].split("&");
                String inpNumber = URLDecoder.decode(s2[0], "UTF-8");
                String quit = URLDecoder.decode(query[3], "UTF-8");
                // start search
                if (inpName != "" && inpNumber == "") {
                    if (isValid(inpName)) {
                        System.out.println("looking for " + inpName);
                        ArrayList<String> result = new ArrayList<>();
                        Thread t1 = new Thread(new NameSearch(phonebook, inpName, result));
                        t1.start();
                        try {
                            t1.join();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        // TODO: zu HTML ändern
                        printResult(inpName, result);
                    } else {
                        // TODO: zu HTML ändern
                        printError();
                    }
                } else if (inpName == "" && inpNumber != "") {
                    if (isValid(inpNumber)) {
                        System.out.println("looking for " + inpNumber);
                        ArrayList<String> result = new ArrayList<>();
                        Thread t1 = new Thread(new NumberSearch(phonebook, inpNumber, result));
                        t1.start();
                        try {
                            t1.join();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        // TODO: zu HTML ändern
                        printResult(inpNumber, result);
                    } else {
                        // TODO: zu HTML ändern
                        printError();
                    }
                } else if (inpName != "" && inpNumber != "") {
                    if (isValid(inpName + inpNumber)) {
                        System.out.println("looking for " + inpName + " & " + inpNumber);
                        ArrayList<String> result = new ArrayList<>();
                        Thread t3 = new Thread(new NameSearch(phonebook, inpName, result));
                        Thread t5 = new Thread(new NumberSearch(phonebook, inpNumber, result));
                        t3.start();
                        t5.start();
                        try {
                            t3.join();
                            t5.join();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        // TODO: zu HTML ändern
                        printResult(inpName + inpNumber, result);
                    } else {
                        // TODO: zu HTML ändern
                        printError();
                    }
                } else if (quit == "Quit server") {
                    printQuit();
                    System.exit(0);
                } else {
                    // TODO: zu HTML ändern
                    printError();
                }
            }


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
                out.println("<input type=\"submit\" value=\"Search\">");
                out.println("<input type=\"submit\" name=\"quitserver\" value=\"Quit server\">");
                out.println("</form>");
                out.println("</body>");
                out.println("</html>");
                out.println();
                out.flush();
                out.close();
                in.close();

        }
    }

    private void printQuit() {
    }


    /**
     * checks if given result list is empty and print results
     *
     * @param input
     * @param result
     */
    private void printResult(String input, ArrayList<String> result) {
        if (result.isEmpty()) {
            System.out.println("Search for " + input + " not successful. Please try again.");
        } else {
            for (String e : result) {
                System.out.println(e);
            }
        }
    }

    /**
     * checks if input is valid (no empty input, no whitespaces, no tabs)
     *
     * @param input
     * @return boolean
     */
    private boolean isValid(String input) {
        if (input.isEmpty() || input.matches("\\s+") || input.matches("\\t+")) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * prints error message
     */
    private void printError() {
        System.out.println("Not a valid input, please try again.");
    }

}

