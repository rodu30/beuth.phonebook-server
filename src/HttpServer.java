import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @className HttpServer
 * @author romanduhr
 * @date   07.06.16
 *
 *  Class creates a HTTP server and establishes a connection with an HTTP client and serves html interface.
 */
public class HttpServer {

    private EntryPair[] phonebook;
    private int port;
    private ServerSocket serverSocket;
    private String host;

    public HttpServer(EntryPair[] phonebook, int port, String host) {
        this.phonebook = phonebook;
        this.port = port;
        this.host = host;
     }

    /**
     * starts server with given port
     *
     * @throws IOException
     */
    public void execute() throws IOException {
//        host = "http://localhost";
//        host = InetAddress.getLocalHost().getHostName();          Adresse benutzen
//        host = InetAddress.getLocalHost().getHostAddress();
        serverSocket = new ServerSocket(port);
        System.out.println("Welcome to the phone server at host: " + host + " and port: " + port);
        read();
    }

    /**
     * reads HTTP requests and responds with page, search results etc.
     *
     * @throws IOException
     */
    private void read() throws IOException {
        while (true) {
            Socket clientSocket = serverSocket.accept();
            System.out.println("Client connected and waiting for requests");

            // read input
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            String line = in.readLine();
            System.out.println("Incoming request: '" + line + "'");

            // ignore favicon
            if (line.startsWith("GET /favicon")) {
                System.out.println("Favicon request");
                in.close();
                continue;
            }

            // process request and send result
            if (line.contains("?")) {
                System.out.println("HTTP request with query data");
                HashMap<String, String> queryMap = new HashMap<>();
                // get name & number
                String[] q1 = line.split(" ");
                String[] q2 = q1[1].substring(2).split("&");
                for (String s1 : q2) {
                    String[] s2 = s1.split("=");
                    if (s2.length > 1) {
                        queryMap.put(s2[0], URLDecoder.decode(s2[1], "UTF-8"));
                    } else {
                        queryMap.put(s2[0], "");
                    }
                }
                if (queryMap.containsKey("quit")) {                 //quit server
                    sendQuitResponse(clientSocket);
                    in.close();
                    System.exit(0);
                } else {                                            // start search
                    if (!queryMap.get("name").isEmpty() && queryMap.get("number").isEmpty()) {
                        String inpName = queryMap.get("name");
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
                            sendResult(clientSocket, inpName, result);
                            in.close();
                        } else {
                            sendError(clientSocket);
                            in.close();
                        }
                    } else if (queryMap.get("name").isEmpty() && !queryMap.get("number").isEmpty()) {
                        String inpNumber = queryMap.get("number");
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
                            sendResult(clientSocket, inpNumber, result);
                            in.close();
                        } else {
                            sendError(clientSocket);
                            in.close();
                        }
                    } else if (!queryMap.get("name").isEmpty() && !queryMap.get("number").isEmpty()) {
                        String inpName = queryMap.get("name");
                        String inpNumber = queryMap.get("number");
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
                            sendResult(clientSocket, inpName + inpNumber, result);
                            in.close();
                        } else {
                            sendError(clientSocket);
                            in.close();
                        }
                    } else {
                        sendError(clientSocket);
                        in.close();
                    }
                }

            } else {
                sendStartPage(clientSocket);
                in.close();
            }
        }
    }

    /**
     * send normal search form
     *
     * @param clientSocket
     * @throws IOException
     */
    private void sendStartPage(Socket clientSocket) throws IOException {
        PrintWriter out = new PrintWriter(clientSocket.getOutputStream());
        System.out.println("Request processed");
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
        out.println("<input type=\"submit\" name=\"quit\" value=\"Quit server\">");
        out.println("</form>");
        out.println("</body>");
        out.println("</html>");
        out.println();
        out.flush();
        out.close();
    }

    /**
     * sends confirmation that server is shutting down
     *
     * @param clientSocket
     * @throws IOException
     */
    private void sendQuitResponse(Socket clientSocket) throws IOException {
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
        out.println("HttpServer is shutting down.");
        out.println("</body>");
        out.println("</html>");
        out.println();
        out.flush();
        out.close();
    }

    /**
     * checks if given result list is empty and sends results to client
     *
     * @param clientSocket
     * @param input
     * @param result
     * @throws IOException
     */
    private void sendResult(Socket clientSocket, String input, ArrayList<String> result) throws IOException {
        PrintWriter out = new PrintWriter(clientSocket.getOutputStream());
        System.out.println("Request processed");
        if (result.isEmpty()) {
            System.out.println("Search for " + input + " not successful.");
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
            out.println("<input type=\"text\" name=\"name\">");
            out.println("<br>");
            out.println("Please enter a number:<br>");
            out.println("<input type=\"text\" name=\"number\">");
            out.println("<br>");
            out.println("<p style=\"color:red\">Search for '" + input + "' not successful. Please try again.</p>");
            out.println("<br>");
            out.println("<input type=\"submit\" value=\"Search\">");
            out.println("<input type=\"submit\" name=\"quit\" value=\"Quit server\">");
            out.println("</form>");
            out.println("</body>");
            out.println("</html>");
            out.println();
            out.flush();
            out.close();
        } else {
            System.out.println("Search for " + input + " successful.");
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
            out.println("<input type=\"text\" name=\"name\">");
            out.println("<br>");
            out.println("Please enter a number:<br>");
            out.println("<input type=\"text\" name=\"number\">");
            out.println("<br>");
            out.println("<p style=\"color:green\">Result:<br>");
            for (String e : result) {
                out.println(e + "<br>");
            }
            out.println("<br>");
            out.println("<input type=\"submit\" value=\"Search\">");
            out.println("<input type=\"submit\" name=\"quit\" value=\"Quit server\">");
            out.println("</form>");
            out.println("</body>");
            out.println("</html>");
            out.println();
            out.flush();
            out.close();
        }
    }

    /**
     * sends error message to client
     *
     * @param clientSocket
     * @throws IOException
     */
    private void sendError(Socket clientSocket) throws IOException {
        PrintWriter out = new PrintWriter(clientSocket.getOutputStream());
        System.out.println("Request processed");
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
        out.println("<input type=\"text\" name=\"name\">");
        out.println("<br>");
        out.println("Please enter a number:<br>");
        out.println("<input type=\"text\" name=\"number\">");
        out.println("<br>");
        out.println("<p style=\"color:red\">Not a valid input, please try again.</p>");
        out.println("<br>");
        out.println("<input type=\"submit\" value=\"Search\">");
        out.println("<input type=\"submit\" name=\"quit\" value=\"Quit server\">");
        out.println("</form>");
        out.println("</body>");
        out.println("</html>");
        out.println();
        out.flush();
        out.close();
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

}