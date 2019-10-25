/*
 *   Runner class with main method that execute the entire program.
*/
public class runner {
    public static void main(String[] args)
    {
        // creating a GUI.
        GUI appGUI = new GUI();

        // creating a parser.
        XMLDomParser parser = new XMLDomParser(appGUI);

        // creating a new thread that will take care to update the rate table.
        parser.start();
    }
}
