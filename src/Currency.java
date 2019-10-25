import java.util.logging.Logger;

/*
 *   A class that represent each currency shown in the rates table using the details from the XML file.
*/
public class Currency
{
    static Logger logger = Logger.getLogger("Currency");
    private String name;
    private int unit;
    private String currencyCode;
    private String country;
    private double rate;

    /*
     *   Currency constructor that builds each currency in our rates table.
    */
    public Currency(String name, int unit, String currencyCode, String country, double rate) {
        this.name = name;
        this.unit = unit;
        this.currencyCode = currencyCode;
        this.country = country;
        this.rate = rate;
    }

    /*
     *   A static method that convert currencies. This method is called when the convert button is pressed.
    */
    static public double convertCurrency(double amount, Currency from, Currency to) {
        logger.info("Currency Converting Currencies.    ---------- Thread: " + Thread.currentThread().getName());

        double fromRate = from.getRate();
        double toRate = to.getRate();

        return (amount*fromRate) / (toRate);
    }

    /*
     *   Getters for the private variables.
    */
    public String getName() {
        return name;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public String getCountry() {
        return country;
    }

    public double getRate() {
        return rate;
    }
}
