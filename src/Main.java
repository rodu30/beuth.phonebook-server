import dataStructure.EntryPair;
import ui.UI;

/**
 * @className Main
 * @author romanduhr
 * @date   30.04.16
 *
 *  Main class creates phonebook and starts ui threat.
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

        // Create UI and start infinite loop
        UI ui = new UI(phonebook);
        while(true) {
            ui.execute();
        }

    }
}
