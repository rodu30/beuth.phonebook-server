package search;

import dataStructure.EntryPair;

/**
 * @className NameSearch
 * @author romanduhr
 * @date   30.04.16
 *
 *  Thread that searches given phonebook for given name.
 */
public class NameSearch extends Search {

    public NameSearch(EntryPair[] phonebook, String input) {
        super(phonebook, input);
    }

    @Override
    public String checkEntry(EntryPair e) {
        return e.getName();
    }

}
