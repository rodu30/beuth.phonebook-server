package ui;

import helper.EntryPair;
import search.NameSearch;
import search.NumberSearch;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @className UI
 * @author romanduhr
 * @date   30.04.16
 *
 * User Interface for communication with user.
 */
public class UI {

    EntryPair[] phonebook;
    BufferedReader in;

    public UI(EntryPair[] phonebook) {
        System.out.println("Hello");
        this.phonebook = phonebook;
        this.in = new BufferedReader(new InputStreamReader(System.in));
    }

    public void execute() {
        System.out.println("Type 'exit' if you want to end program.");
        System.out.println("type 'name' if you want to search for a name, enter 'num' for number or enter 'name+num' for an name and a number: ");
        try {
            String inp1 = in.readLine();
            if (inp1.equals("exit")) {
                System.exit(0);
            } else if (inp1.equals("name")) {
                System.out.println("Please enter name: ");
                String inpNa = in.readLine();
                if (inpNa == "") {
                    System.out.println("Please try again.");
                } else {
                    Thread t2 = new Thread(new NameSearch(phonebook, inpNa));
                    t2.start();
                    try {
                        t2.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            } else if (inp1.equals("num")) {
                System.out.println("Please enter number: ");
                String inpNu = in.readLine();
                if (inpNu == "") {
                    System.out.println("Please try again.");
                } else {
                    Thread t3 = new Thread(new NumberSearch(phonebook, inpNu));
                    t3.start();
                    try {
                        t3.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            } else if (inp1.equals("name+num")) {
                System.out.println("Please enter name-number: ");
                String inpNaNu = in.readLine();
                if (inpNaNu == "") {
                    System.out.println("Please try again.");
                } else {
                    String[] p = inpNaNu.split("-");
                    Thread t4 = new Thread(new NameSearch(phonebook, p[0]));
                    Thread t5 = new Thread(new NumberSearch(phonebook, p[1]));
                    t4.start();
                    t5.start();
                    try {
                        t4.join();
                        t5.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                System.out.println("Please try again.");
            }
            System.out.println("");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
