package userInterface;


import Global.Time;
import PropertiesFile.PropertiesFileReader;
import ServerMonitor.PingTool;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerMonitorController implements Initializable, Runnable{

    @FXML private FlowPane serverMonitor_FlowPane;
    @FXML private Label lbl_contents;
    @FXML private VBox serverMonitor_vBox;
    @FXML private Label lbl_healthStatus;

    int contentsFontSize = 13;

    String serverName = "NA";
    String pingTime = "NA";
    String healthStatus = "NA";
    String lastCheck = "NA";

    String[] singleServer;

    static boolean paneIsActive = false;
    ExecutorService executor;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        paneIsActive = true;
    }

    @FXML
    public void listAllPanePropertyFile() throws Exception{
        serverMonitor_FlowPane.setOrientation(Orientation.HORIZONTAL);
        serverMonitor_FlowPane.setHgap(15);
        serverMonitor_FlowPane.setVgap(15);
        serverMonitor_FlowPane.setPadding(new Insets(15,15,15,15));
        serverMonitor_FlowPane.setRowValignment(VPos.TOP);
        serverMonitor_FlowPane.setStyle("-fx-background-color: #FFFFFF;");

        PropertiesFileReader propFile = new PropertiesFileReader();
        String serverList = propFile.readFromPropertyFile("DashboardSettings.properties", "Server_To_Monitor");

//        serverList = "vela.jobstreet.com,libra.jobstreet.com,orion.jobstreet.com,ta-controller.jobstreet.com, Drone, Lobster, Chiru, Duiker, Dule, Simian, Horde, Coley, Catla, Maleo, Millerbird, Morepork, Baryonyx, Poacher, Dove, Duck, Swarm, Filly, Maggot";
        singleServer = serverList.split(",");

        for(int i = 0; i < singleServer.length; i++) {
            serverName = singleServer[i].trim();

            lbl_contents = new Label();
            lbl_contents.setId("lbl_ServerName_" + serverName);
            lbl_contents.setText(updateServerDetailsLabel());
            lbl_contents.setFont(Font.font(contentsFontSize));
            lbl_contents.setAlignment(Pos.CENTER_LEFT);

            lbl_healthStatus = new Label();
            lbl_healthStatus.setText(healthStatus);
            lbl_healthStatus.setId("lbl_HealthStatus_" + serverName);
            lbl_healthStatus.setFont(Font.font(contentsFontSize + 10));
            lbl_healthStatus.setAlignment(Pos.CENTER);

            serverMonitor_vBox = new VBox();
            serverMonitor_vBox.getChildren().add(lbl_contents);
            serverMonitor_vBox.getChildren().add(lbl_healthStatus);
            serverMonitor_vBox.setStyle("-fx-background-color: #C0C0C0");
            serverMonitor_vBox.setSpacing(5);
            serverMonitor_vBox.setMinWidth(150);
            serverMonitor_vBox.setMinHeight(70);
            serverMonitor_vBox.setEffect(new DropShadow());

            serverMonitor_FlowPane.getChildren().add(serverMonitor_vBox);

            executor = Executors.newFixedThreadPool(3);
            Thread thread = new Thread(task);
            thread.setDaemon(true);
            thread.setPriority(Thread.MIN_PRIORITY);
            thread.start();
        }
    }

    private String updateServerDetailsLabel() throws Exception{
        StringBuilder str = new StringBuilder();

        if(pingTime.equals("-1")){
            pingTime = "NA";
        }

        str.append("Server: " + serverName + "\n");
        str.append("Ping Time: " + pingTime + "\n");
        str.append("Last Check: " + lastCheck + "\n");

        return str.toString();
    }

    public Runnable startPing() throws Exception{
        PingTool svrMonitor = new PingTool();

        for(int i = 0; i < singleServer.length; i++){
            VBox vBox = (VBox) serverMonitor_FlowPane.getChildren().get(i);
            Label label = (Label) vBox.getChildren().get(0);
            Label healthLabel = (Label) vBox.getChildren().get(1);

            serverName = singleServer[i];
            pingTime = String.valueOf(svrMonitor.ping(singleServer[i])[1]);

            Time time = new Time();
            lastCheck = time.getCurrentTime("HH:mm:ss");

            if(Integer.parseInt(pingTime) <= 0){
                vBox.setStyle("-fx-background-color: #FF0000");
                healthLabel.setTextFill(Color.web("#FFFFFF"));
                label.setTextFill(Color.web("#FFFFFF"));
                healthLabel.setText("OFFLINE");
            } else {
                vBox.setStyle("-fx-background-color: #00FF00");
                healthLabel.setTextFill(Color.web("#000000"));
                label.setTextFill(Color.web("#000000"));
                healthLabel.setText("ONLINE");
            }

            label.setText(updateServerDetailsLabel());
        }
        return null;
    }

    Task task = new Task<String>() {
        @Override
        protected String call() throws Exception {
            while(true){
                try {
                    Platform.runLater(() -> {
                        try {

                            startPing();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });

                    if(!paneIsActive){
                        System.out.println("Application Stopped");
                        return null;
                    }
                    Thread.sleep(30000);
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }
        }
    };

    public static void stopThread() throws Exception{
        paneIsActive = false;

    }

    @Override
    public void run() {

    }
}
