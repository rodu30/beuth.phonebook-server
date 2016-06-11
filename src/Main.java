import dataStructure.EntryPair;
import server.HttpServer;

import java.io.IOException;

/**
 * @className Main
 * @author romanduhr
 * @date   30.04.16
 *
 *  Main class creates phonebook and starts server threat.
 */
public class Main {

    public static void main(String[] args) {

        // Create example phonebook
        EntryPair[] phonebook = {
                new EntryPair("Meier", "4711"),
                new EntryPair("Schmitt", "0815"),
                new EntryPair("Müller", "4711"),
                new EntryPair("Meier", "0816"),
                new EntryPair("von Schulz", "4792")
        };

//        // Create ConsoleUI and start infinite loop
//        ConsoleUI server = new ConsoleUI(phonebook);
//        while(true) {
//            server.execute();
//        }

        // Create HttpServer and start
        int port = 3000;
        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        }
        HttpServer server = new HttpServer(phonebook, port);
        try {
            server.execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
