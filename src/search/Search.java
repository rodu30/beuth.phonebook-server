package search;

import dataStructure.EntryPair;

import java.util.ArrayList;

/**
 * @className Search
 * @author romanduhr
 * @date   30.04.16
 *
 *  Abstract class for a general search thread that searches in given phonebook.
 */
public abstract class Search implements Runnable {

    private EntryPair[] phonebook;
    private String input;
    private ArrayList<String> result;

    public Search(EntryPair[] phonebook, String searchInput, ArrayList<String> result) {
        this.phonebook = phonebook;
        this.input = searchInput;
        this.result = result;
    }

    @Override
    public void run() {
        System.out.println("Start search...");
        synchronized(result) {
            int hit = 0;
            for (EntryPair e : phonebook) {
                if (checkEntry(e).equals(input)) {
                    result.add(e.getEntry());
                    hit++;
                }
            }
//            if (hit == 0) {
//                System.out.println("Search for " + input + " not successful. Please try again.");
//            }
        }
    }

    public abstract String checkEntry(EntryPair e);

}
