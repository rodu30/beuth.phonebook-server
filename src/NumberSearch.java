import java.util.ArrayList;

/**
 * @className NameSearch
 * @author romanduhr
 * @date   30.04.16
 *
 *  Thread that searches given phonebook for given name.
 */
public class NumberSearch extends Search {

    public NumberSearch(EntryPair[] phonebook, String input, ArrayList<String> result) {
        super(phonebook, input, result);
    }

    @Override
    public String checkEntry(EntryPair e) {
        return e.getNumber();
    }

}
