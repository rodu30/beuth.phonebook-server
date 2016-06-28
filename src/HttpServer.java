import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLDecoder;
import java.rmi.Naming;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @className HttpServer
 * @author romanduhr
 * @date   07.06.16
 *
 *  Class creates a HTTP server and establishes a connection with an HTTP client,
 *  serves html interface and sends data to dept server (RMI client)
 */
public class HttpServer {

    private static int port;
    private static String host;
    private static ServerSocket serverSocket;

    public HttpServer() {
    }

    /**
     * main starts server with given port
     *
     * @throws Exception
     * @param args
     */
    public static void main(String[] args) throws Exception {

        port = 3000;
        host = "http://localhost";
//        host = InetAddress.getLocalHost().getHostAddress();
        if (args.length == 1) {
            port = Integer.parseInt(args[0]);
        } else if(args.length == 2) {
            port = Integer.parseInt(args[0]);
            if (args[1].equals("hn")) {
                host = InetAddress.getLocalHost().getHostName();
            } else if (args[1].equals("ha")) {
                host = InetAddress.getLocalHost().getHostAddress();
            } else if (!args[1].isEmpty() && !args[1].equals("hn") && !args[1].equals("ha")) {
                host = "http://" + args[1];
            }
        }
        serverSocket = new ServerSocket(port);
        System.out.println("Welcome to the phone server at host: " + host + " and port: " + port);

        // reads HTTP requests and responds with page, search results etc.
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

                String[] q1 = line.split(" ");                                  // get name & number
                String[] q2 = q1[1].substring(2).split("&");
                for (String s1 : q2) {
                    String[] s2 = s1.split("=");
                    if (s2.length > 1) {
                        queryMap.put(s2[0], URLDecoder.decode(s2[1], "UTF-8"));
                    } else {
                        queryMap.put(s2[0], "");
                    }
                }

                // Access to remote RMI server
                IRemoteSearch remoteSearch = (IRemoteSearch) Naming.lookup("myserver"); //TODO testen mit entferntem Rechner
//                IRemoteSearch remoteSearch = (IRemoteSearch) Naming.lookup("//127.0.0.1/server"); // localhost

                if (queryMap.containsKey("quit")) {                 //quit server
                    sendQuitResponse(clientSocket);
                    in.close();
                    remoteSearch.quit();
                    System.out.println("passed to RMI");
                    System.exit(0);
                } else if (queryMap.containsKey("reset")) {
                    sendStartPage(clientSocket);
                    in.close();
                } else {                                            // start search
                    if (!queryMap.get("name").isEmpty() && queryMap.get("number").isEmpty()) {
                        String inpName = queryMap.get("name");
                        if (isValid(inpName)) {
                            ArrayList<String> result = remoteSearch.getNameSearchResult(inpName);
                            System.out.println("passed to RMI");
                            sendResult(clientSocket, inpName, result);
                            in.close();
                        } else {
                            sendError(clientSocket);
                            in.close();
                        }
                    } else if (queryMap.get("name").isEmpty() && !queryMap.get("number").isEmpty()) {
                        String inpNumber = queryMap.get("number");
                        if (isValid(inpNumber)) {
                            ArrayList<String> result = remoteSearch.getNumberSearchResult(inpNumber);
                            System.out.println("passed to RMI");
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
                            ArrayList<String> result = remoteSearch.getNaNuSearchResult(inpName, inpNumber);
                            System.out.println("passed to RMI");
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
    private static void sendStartPage(Socket clientSocket) throws IOException {
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
        out.println("<input type=\"submit\" name=\"reset\" value=\"Reset form\">");
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
    private static void sendQuitResponse(Socket clientSocket) throws IOException {
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
        out.println("HttpServer & DepartmentServer is shutting down.");
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
    private static void sendResult(Socket clientSocket, String input, ArrayList<String> result) throws IOException {
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
            out.println("<input type=\"submit\" name=\"reset\" value=\"Reset form\">");
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
            out.println("<input type=\"submit\" name=\"reset\" value=\"Reset form\">");
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
    private static void sendError(Socket clientSocket) throws IOException {
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
        out.println("<input type=\"submit\" name=\"reset\" value=\"Reset form\">");
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
    private static boolean isValid(String input) {
        if (input.isEmpty() || input.matches("\\s+") || input.matches("\\t+")) {
            return false;
        } else {
            return true;
        }
    }

}