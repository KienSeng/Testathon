package Global;

import org.openqa.selenium.WebDriver;
import userInterface.TestDataCreationController;

import java.sql.Connection;
import java.util.HashMap;

/**
 * Created by kienseng.koh on 3/22/2016.
 */
public class Global {
    public static Connection dbConnection;
    public static HashMap<String, String> propertyMap = new HashMap<>();

    public static Double radioButtonWidth = 100.0;
    public static Double standardLabelWidth = 150.0;
    public static Double standardTextBoxWidth = 200.0;
    public static Double standardButtonWidth = 80.0;

    public static String product;
    public static WebDriver driver;

    public static TestDataCreationController testDataController;
}
