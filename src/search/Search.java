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
//        System.out.println("Start search...");
        synchronized(result) {
            for (EntryPair e : phonebook) {
                if (checkEntry(e).equals(input)) {
                    result.add(e.getEntry());
                }
            }
        }
    }

    /**
     * when uses returns name or number
     *
     * @param e
     * @return name or number
     */
    public abstract String checkEntry(EntryPair e);

}
