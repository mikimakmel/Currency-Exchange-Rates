import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import javax.net.ssl.HttpsURLConnection;
import javax.swing.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.logging.Logger;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

/*
 *   This class is using to parse the XML file.
 *   Also it is in charge to refresh the rates table using another thread every 30 seconds.
*/
public class XMLDomParser extends Thread implements Runnable, model
{
    static Logger logger = Logger.getLogger("XMLDomParser");

    // GUI reference. Being used to refresh the rates table.
    private GUI myGUI;

    /*
     *   XMLDomParser constructor that assign GUI reference and prints the log info for the current thread.
    */
    public XMLDomParser(GUI myGUI) {
        logger.info(this.getClass().getName() + " Constructor.    ---------- Thread: " + Thread.currentThread().getName());
        this.myGUI = myGUI;
    }

    /*
     *   The method is being called in case of a connection error or offline status.
     *   Loading the most recent updated XML file brought online and saved locally, and creating a currencies list for the rates table.
    */
    public ArrayList<Currency> getOfflineXML() {
        logger.info(this.getClass().getName() + " Getting offline XML.    ---------- Thread: " + Thread.currentThread().getName());
        File currenciesFILE = new File("src/currency.xml");
        NodeList list;
        ArrayList<Currency> currenciesList = new ArrayList<>();

        try
        {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(currenciesFILE);

            myGUI.getDateLabel().setText(doc.getElementsByTagName("LAST_UPDATE").item(0).getFirstChild().getNodeValue());
            list = doc.getElementsByTagName("CURRENCY");
            Currency NIScurrency = new Currency("Shekel",1,"NIS", "Israel", 1);
            currenciesList.add(NIScurrency);

            // creates a list of all the currencies brought from the XML
            for (int i = 0; i < list.getLength(); i++) {
                NodeList currencyNodes = list.item(i).getChildNodes();

                String name = currencyNodes.item(1).getTextContent();
                int unit = Integer.parseInt(currencyNodes.item(3).getTextContent());
                String currencyCode = currencyNodes.item(5).getTextContent();
                String country = currencyNodes.item(7).getTextContent();
                double rate = Double.parseDouble(currencyNodes.item(9).getTextContent());

                Currency newCurrency = new Currency(name, unit, currencyCode, country, rate/unit);
                currenciesList.add(newCurrency);
            }
        } catch (IOException | ParserConfigurationException | SAXException e) {
            e.printStackTrace();
        }

        return currenciesList;
    }

    /*
     *   This method is using an http request to bring the XML file from the Bank Of Israel.
     *   Using this XML file it will create a currencies list for the rates table.
     *   In case of a connection failure or offline status it will call the getOfflineXML method,
      *  and will use the local XML file stored in our project.
    */
    public ArrayList<Currency> createCurrenciesList() {
        logger.info(this.getClass().getName() + " Getting online XML.    ---------- Thread: " + Thread.currentThread().getName());
        InputStream is = null;
        HttpsURLConnection con = null;
        NodeList list;
        ArrayList<Currency> currenciesList = new ArrayList<>();
        Currency NIScurrency = new Currency("Shekel",1, "NIS", "Israel", 1);
        currenciesList.add(NIScurrency);

        try
        {
            URL url = new URL("https://www.boi.org.il/currency.xml");
            con = (HttpsURLConnection)url.openConnection();
            con.setRequestMethod("GET");
            con.connect();

            is = con.getInputStream();

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(is);

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource domSource = new DOMSource(doc);
            StreamResult streamResult = new StreamResult(new File("src/currency.xml"));
            transformer.transform(domSource, streamResult);

            myGUI.getDateLabel().setText("Last update: " + doc.getElementsByTagName("LAST_UPDATE").item(0).getFirstChild().getNodeValue());
            list = doc.getElementsByTagName("CURRENCY");

            // creates a list of all the currencies brought from the XML
            for (int i = 0; i < list.getLength(); i++) {
                NodeList currencyNodes = list.item(i).getChildNodes();

                String name = currencyNodes.item(1).getTextContent();
                int unit = Integer.parseInt(currencyNodes.item(3).getTextContent());
                String currencyCode = currencyNodes.item(5).getTextContent();
                String country = currencyNodes.item(7).getTextContent();
                double rate = Double.parseDouble(currencyNodes.item(9).getTextContent());

                Currency newCurrency = new Currency(name, unit, currencyCode, country, rate/unit);
                currenciesList.add(newCurrency);
            }
        } catch (TransformerException | IOException | ParserConfigurationException | SAXException e) {
            logger.info(this.getClass().getName() + " online XML ERROR.    ---------- Thread: " + Thread.currentThread().getName());
            currenciesList = getOfflineXML();
        } finally {
            if(is != null){
                try {
                    is.close();
                } catch(IOException e){
                    e.printStackTrace();
                }
            }
            if(con != null){
                con.disconnect();
            }
        }

        return currenciesList;
    }

    /*
     *   Lambda Runnable.
     *   Calling the GUI createTable method to refresh the rates table.
    */
    Runnable task = () -> myGUI.createTable(myGUI.getModel(), this.createCurrenciesList());

    /*
     *   Overriding run for the new Thread. Executing our task every 30 seconds to refresh our rates table.
     *   This Thread will put our task in the GUI Event Queue, and invoke it in the right time.
     */
    @Override
    public void run() {
        while(true) {
            try {
                logger.info(this.getClass().getName() + " run()    ---------- Thread: " + Thread.currentThread().getName());
                SwingUtilities.invokeLater(task);
                Thread.sleep(30000);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
