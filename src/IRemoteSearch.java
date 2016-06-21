import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

/**
 * @className IRemoteSearch
 * @author romanduhr
 * @date   21.06.16
 *
 *  Interface for RMI server
 */
public interface IRemoteSearch extends Remote {

    /**
     * remote method
     *
     * @throws RemoteException
     */
    ArrayList<String> getNameSearchResult(String s) throws RemoteException;

    /**
     *
     * remote method
     *
     * @throws RemoteException
     */
    ArrayList<String> getNumberSearchResult(String s) throws RemoteException;

    /**
     *
     * remote method
     *
     * @throws RemoteException
     */
    ArrayList<String> getNaNuSearchResult(String na, String nu) throws RemoteException;

    void quit() throws RemoteException;

}
