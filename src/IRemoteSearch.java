import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

/**
 * Created by romanduhr on 21.06.16.
 */
public interface IRemoteSearch extends Remote {

    /**
     * remote method
     *
     * @throws RemoteException
     */
    ArrayList<String> getNameSearchResult(String s) throws RemoteException;

    /**
     * @throws RemoteException
     */
    ArrayList<String> getNumberSearchResult(String s) throws RemoteException;

    /**
     * @throws RemoteException
     */
    ArrayList<String> getNaNuSearchResult(String na, String nu) throws RemoteException;

    void quit() throws RemoteException;

}
