import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

/**
 * @className DeptServer
 * @author romanduhr
 * @date   21.06.16
 *
 *  Class creates a server for a department phone book, which receives a data from HttpServer via RMI interface (RMI server).
 */
public class DeptServer extends UnicastRemoteObject implements IRemoteSearch {

    private EntryPair[] phonebook;

    public DeptServer() throws RemoteException {
        super();
        // Create example phonebook
        phonebook = new EntryPair[]{
                new EntryPair("Meier", "4711"),
                new EntryPair("Schmitt", "0815"),
                new EntryPair("Müller", "4711"),
                new EntryPair("Meier", "0816"),
                new EntryPair("von Schulz", "4792")
        };
    }

    @Override
    public ArrayList<String> getNameSearchResult(String name) throws RemoteException {
        System.out.println("RMI received");
        System.out.println("looking for " + name);
        ArrayList<String> result = new ArrayList<>();
        Thread t1 = new Thread(new NameSearch(phonebook, name, result));
        t1.start();
        try {
            t1.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("search done");
        return result;
    }

    @Override
    public ArrayList<String> getNumberSearchResult(String number) throws RemoteException {
        System.out.println("RMI received");
        System.out.println("looking for " + number);
        ArrayList<String> result = new ArrayList<>();
        Thread t1 = new Thread(new NumberSearch(phonebook, number, result));
        t1.start();
        try {
            t1.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("search done");
        return result;
    }

    @Override
    public ArrayList<String> getNaNuSearchResult(String name, String number) throws RemoteException {
        System.out.println("RMI received");
        System.out.println("looking for " + name + " & " + number);
        ArrayList<String> result = new ArrayList<>();
        Thread t3 = new Thread(new NameSearch(phonebook, name, result));
        Thread t5 = new Thread(new NumberSearch(phonebook, number, result));
        t3.start();
        t5.start();
        try {
            t3.join();
            t5.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("search done");
        return result;
    }

    @Override
    public void quit() throws RemoteException {
        System.out.println("start quitting department server");
        System.exit(0);
    }

    /**
     * main creates remote obj and exports and registers it with RMI
     *
     * @param args
     */
    public static void main(String args[]) {

        try {
            DeptServer server = new DeptServer();

            LocateRegistry.createRegistry(Registry.REGISTRY_PORT);     // Port 1099
            Naming.rebind("myserver", server);
            System.out.println("Server ready and waiting for RMIs on port " + Registry.REGISTRY_PORT);
        } catch (Exception e) {
            System.err.println("Server exception: " + e.toString());
            e.printStackTrace();
        }
    }
}
