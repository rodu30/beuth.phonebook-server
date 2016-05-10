package ui;

import dataStructure.EntryPair;
import search.NameSearch;
import search.NumberSearch;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

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
            // start input-reader
            String inp = in.readLine();

            if (inp.equals("exit")) {
                System.exit(0);

            } else if (inp.equals("1")) {
                System.out.println("Please enter name: ");
                String inpNa = in.readLine();
                if (inpNa.equals(" ") || inpNa.equals("")) {
                    System.out.println("Please try again.");
                } else {
                    ArrayList<String> result = new ArrayList<>();
                    Thread t1 = new Thread(new NameSearch(phonebook, inpNa, result));
                    t1.start();
                    try {
                        t1.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    printResult(inpNa, result);
                }

            } else if (inp.equals("2")) {
                System.out.println("Please enter number: ");
                String inpNu = in.readLine();
                if (inpNu.equals(" ") || inpNu.equals("")) {
                    System.out.println("Please try again.");
                } else {
                    ArrayList<String> result = new ArrayList<>();
                    Thread t2 = new Thread(new NumberSearch(phonebook, inpNu, result));
                    t2.start();
                    try {
                        t2.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    printResult(inpNu, result);
                }

            } else if (inp.equals("3")) {
                System.out.println("Please enter 'name-number': ");
                String inpNaNu = in.readLine();
                if (inpNaNu.equals(" ") || inpNaNu.equals("")) {
                    System.out.println("Please try again.");
                } else {
                    ArrayList<String> result = new ArrayList<>();
                    String[] p = inpNaNu.split("-");
                    Thread t3 = new Thread(new NameSearch(phonebook, p[0], result));
                    Thread t5 = new Thread(new NumberSearch(phonebook, p[1], result));
                    t3.start();
                    t5.start();
                    try {
                        t3.join();
                        t5.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    printResult(inpNaNu, result);
                }

            } else {
                System.out.println("Please try again.");
            }
            System.out.println("");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * checks if given result list is empty and print results
     *
     * @param input
     * @param result
     */
    private void printResult(String input, ArrayList<String> result) {
        if (result.isEmpty()) {
            System.out.println("Search for " + input + " not successful. Please try again.");
        } else {
            for (String e : result) {
                System.out.println(e);
            }
        }
    }

    private boolean isValid(String input) {
        if (input.isEmpty()) return false;
        if (input.matches())
    }


}
