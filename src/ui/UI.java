package ui;

import dataStructure.EntryPair;
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

    private EntryPair[] phonebook;
    private BufferedReader in;

    public UI(EntryPair[] phonebook) {
        System.out.println("Hello");
        this.phonebook = phonebook;
        this.in = new BufferedReader(new InputStreamReader(System.in));
    }

    /**
     * Reads console input and starts search threads.
     */
    public void execute() {
        System.out.println("Type 'exit' if you want to end program.");
        System.out.println("type '1' if you want to search for a name, type '2' for number or type '3' if you want to search for a name and a number:");
        try {
            String inp = in.readLine();

            if (inp.equals("exit")) {
                System.exit(0);

            } else if (inp.equals("1")) {
                System.out.println("Please enter name: ");
                String inpNa = in.readLine();
                if (inpNa.equals(" ") || inpNa.equals("")) {
                    System.out.println("Please try again.");
                } else {
                    Thread t1 = new Thread(new NameSearch(phonebook, inpNa));
                    t1.start();
                    try {
                        t1.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            } else if (inp.equals("2")) {
                System.out.println("Please enter number: ");
                String inpNu = in.readLine();
                if (inpNu.equals(" ") || inpNu.equals("")) {
                    System.out.println("Please try again.");
                } else {
                    Thread t2 = new Thread(new NumberSearch(phonebook, inpNu));
                    t2.start();
                    try {
                        t2.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            } else if (inp.equals("3")) {
                System.out.println("Please enter 'name-number': ");
                String inpNaNu = in.readLine();
                if (inpNaNu.equals(" ") || inpNaNu.equals("")) {
                    System.out.println("Please try again.");
                } else {
                    String[] p = inpNaNu.split("-");
                    Thread t3 = new Thread(new NameSearch(phonebook, p[0]));
                    Thread t5 = new Thread(new NumberSearch(phonebook, p[1]));
                    t3.start();
                    t5.start();
                    try {
                        t3.join();
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
