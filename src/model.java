import java.util.ArrayList;

/*
 *   An interface for the XML parser.
 *   Making sure it will implement these two important methods.
*/
public interface model {
    ArrayList<Currency> getOfflineXML();
    ArrayList<Currency> createCurrenciesList();
}
