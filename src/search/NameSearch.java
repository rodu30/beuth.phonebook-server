package search;

import dataStructure.EntryPair;

import java.util.ArrayList;

/**
 * @className NameSearch
 * @author romanduhr
 * @date   30.04.16
 *
 *  Thread that searches given phonebook for given name.
 */
public class NameSearch extends Search {

    public NameSearch(EntryPair[] phonebook, String input, ArrayList<String> result) {
        super(phonebook, input, result);
    }

    @Override
    public String checkEntry(EntryPair e) {
        return e.getName();
    }

}
