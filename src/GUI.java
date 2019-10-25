import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.ArrayList;
import java.util.logging.Logger;

/*
 *   A class for the user interface.
 *   The user will be able to watch the rates table and convert currencies using this GUI.
*/
public class GUI {

    static Logger logger = Logger.getLogger("GUI");
    private JFrame appFrame;
    private JTable currenciesRatesTable;
    private JTextField amountTextField;
    private JComboBox fromComboBox;
    private JComboBox toComboBox;
    private JButton convertButton;
    private JTextField resultTextField;
    private JLabel amountLabel;
    private JLabel fromLabel;
    private JLabel toLabel;
    private JPanel tablePanel;
    private JPanel datePanel;
    private JPanel activePanel;
    private JLabel dateLabel;
    private JScrollPane scrollPane;
    private DefaultTableModel model;
    private ArrayList<Currency> currenciesList;

    /*
     * This constructor will build the GUI components and place them in the right spot.
    */
    public GUI() {
        // logger message when constructing the GUI
        logger.info(this.getClass().getName() + " Constructor.    ---------- Thread: " + Thread.currentThread().getName());

        // creating GUI components
        appFrame = new JFrame();
        appFrame.setTitle("Currency Exchange Rates Application");
        appFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        appFrame.setLayout(new BorderLayout());

        dateLabel = new JLabel("Date");
        dateLabel.setFont(new Font("Ariel", Font.BOLD, 12));

        amountLabel = new JLabel("Amount");
        amountTextField = new JTextField();
        amountTextField.setFont(new Font("Ariel", Font.BOLD, 12));
        amountTextField.setPreferredSize(new Dimension(80,25));

        fromLabel = new JLabel("From");
        fromComboBox = new JComboBox();
        fromComboBox.setPreferredSize(new Dimension(60,25));

        toLabel = new JLabel("To");
        toComboBox = new JComboBox();
        toComboBox.setPreferredSize(new Dimension(60,25));

        convertButton = new JButton("Convert");
        convertButton.setBackground(new Color(76, 113, 155));
        convertButton.setForeground(Color.WHITE);

        resultTextField = new JTextField();
        resultTextField.setFont(new Font("Ariel", Font.BOLD, 12));
        resultTextField.setPreferredSize(new Dimension(80,25));
        resultTextField.setEditable(false);
        resultTextField.setBackground(Color.WHITE);

        // table for all currencies
        String JTableColumns[] = {"Name", "Currency Code", "Country", "Rate"};
        model = new DefaultTableModel();
        for (String column: JTableColumns) { model.addColumn(column); }
        currenciesRatesTable = new JTable(model);
        currenciesRatesTable.setDefaultEditor(Object.class, null);
        currenciesRatesTable.setRowHeight(20);
        currenciesRatesTable.setFont(new Font("Ariel", Font.PLAIN, 14));
        JTableHeader header = currenciesRatesTable.getTableHeader();
        header.setBackground(new Color(156, 178, 205));
        scrollPane = new JScrollPane(currenciesRatesTable);

        tablePanel = new JPanel();
        tablePanel.setBackground(new Color(211, 221, 255));
        datePanel = new JPanel();
        datePanel.setBackground(new Color(197, 197, 215));
        activePanel = new JPanel();
        activePanel.setBackground(new Color(173, 182, 215));

        tablePanel.add(scrollPane);
        datePanel.add(dateLabel);
        activePanel.add(amountLabel);
        activePanel.add(amountTextField);
        activePanel.add(fromLabel);
        activePanel.add(fromComboBox);
        activePanel.add(toLabel);
        activePanel.add(toComboBox);
        activePanel.add(convertButton);
        activePanel.add(resultTextField);

        appFrame.add(tablePanel, BorderLayout.NORTH);
        appFrame.add(datePanel, BorderLayout.CENTER);
        appFrame.add(activePanel, BorderLayout.SOUTH);
        appFrame.pack();
        appFrame.setVisible(true);


        // Action Listener for the convert button. (using Lambda Expressions)
        convertButton.addActionListener((ActionListener) -> {
            logger.info(this.getClass().getName() + " Convert Button Activation.    ---------- Thread: " + Thread.currentThread().getName());
            Currency from = null , to = null;

            String regex = "([0-9]*[.])?[0-9]+";
            String amountStr = getAmountTextField().getText();

            // checks for illegal input.
            if (amountStr.isEmpty() || amountStr.matches(regex) == false) {
                JOptionPane.showMessageDialog(appFrame, "Invalid input. Please try again.");
                return;
            }

            double amount = Double.parseDouble(amountStr);
            double result = 0;

            for (Currency currency:currenciesList) {
                if (currency.getCurrencyCode().equals((String)getFromComboBox().getSelectedItem())) {
                    from = currency;
                }
                if (currency.getCurrencyCode().equals((String)getToComboBox().getSelectedItem())) {
                    to = currency;
                }
            }

            result = Currency.convertCurrency(amount, from, to);
            setResultTextField(String.format("%.4f", result));
        });
    }

    /*
     *   Getters and Setters for the private variables.
    */
    public JTextField getAmountTextField() {
        return amountTextField;
    }

    public JComboBox getFromComboBox() {
        return fromComboBox;
    }

    public JComboBox getToComboBox() {
        return toComboBox;
    }

    public void setResultTextField(String str) {
        this.resultTextField.setText(str);
    }

    public JLabel getDateLabel() {
        return dateLabel;
    }

    public DefaultTableModel getModel() {
        return model;
    }

    /*
     *   This method is creating the rates table using the XML parsed details.
    */
    public void createTable(DefaultTableModel model, ArrayList<Currency> currenciesList) {
        logger.info(this.getClass().getName() + " Updating Rates Table.    ---------- Thread: " + Thread.currentThread().getName());
        this.currenciesList = currenciesList;
        model.setRowCount(0);
        for (Currency currency:currenciesList) {
            Object[] temp = new Object[] {currency.getName(), currency.getCurrencyCode(), currency.getCountry(), String.format("%.4f", currency.getRate())};
            model.addRow(temp);
            if (fromComboBox.getItemCount() != currenciesList.size()) {
                fromComboBox.addItem(currency.getCurrencyCode());
                toComboBox.addItem(currency.getCurrencyCode());
            }
        }
    }
}
