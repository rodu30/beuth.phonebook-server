import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @className Main
 * @author romanduhr
 * @date   30.04.16
 *
 *  Main class creates phonebook and starts server threat.
 */
public class Main {

    public static void main(String[] args) throws UnknownHostException {

        // Create example phonebook
        EntryPair[] phonebook = {
                new EntryPair("Meier", "4711"),
                new EntryPair("Schmitt", "0815"),
                new EntryPair("Müller", "4711"),
                new EntryPair("Meier", "0816"),
                new EntryPair("von Schulz", "4792")
        };

        // Create HttpServer and start
        int port = 3000;
        String host = "http://localhost";
        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
            if (args[1].equals("hn")) {
                host = InetAddress.getLocalHost().getHostName();
            } else if (args[1].equals("ha")) {
                host = InetAddress.getLocalHost().getHostAddress();
            } else if (!args[1].isEmpty() && !args[1].equals("hn") && !args[1].equals("ha")) {
                host = args[1];
            }
        }

        HttpServer server = new HttpServer(phonebook, port, host);
        try {
            server.execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
