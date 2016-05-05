package dataStructure;

/**
 * @className EntryPair
 * @author romanduhr
 * @date   30.04.16
 *
 *  Helper class for storage of a pair of name and phone number.
 */
public class EntryPair {

    private String name;
    private String number;

    public EntryPair(String name, String number) {
        this.name = name;
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public String getNumber() {
        return number;
    }

    public String getEntry() {
        return name + ", " + number;
    }
}
