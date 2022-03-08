/*
 * File: Main.java
 * Names: Chloe Zhang, Matt Cerrato, Baron Wang
 * Class: CS 361
 * Project 5
 * Date: March 7
 */

package proj5CerratoWangZhang;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.WindowEvent;
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.CodeArea;

import java.io.IOException;


/**
 * Main class sets up the stage
 *
 * @author Matt Cerrato, Baron Wang, Chloe Zhang
 *
 */
public class Main extends Application {

    /**
     * Implements the start method of the Application class. This method will
     * be called after {@code launch} method, and it is responsible for initializing
     * the contents of the window.
     *
     * @param primaryStage A Stage object that is created by the {@code launch}
     *                     method
     *                     inherited from the Application class.
     */
    @Override
    public void start(Stage primaryStage) throws IOException {

        // Load fxml file
        Controller controller = new Controller();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Main.fxml"));
        fxmlLoader.setController(controller);
        Parent root = fxmlLoader.load();
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent windowEvent) {
                controller.handleWindowExit();
            }
        });

        primaryStage.setTitle("Project 5");

        // make VirtualizedScrollPane hold CodeArea
        MyCodeArea myCodeArea = new MyCodeArea();
        CodeArea codeArea = myCodeArea.getCodeArea();
        VirtualizedScrollPane tabScene = new VirtualizedScrollPane<>(codeArea);
        tabScene.setPrefSize(600,200);
        // traverse the child node of root to find TabPane
        for (Node node : root.getChildrenUnmodifiable()) {
            if (node instanceof TabPane) {
                Tab initialTab = ((TabPane) node).getTabs().get(0);
                initialTab.setContent(tabScene);

            }
        }



        // Load css file
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("Main.css").toExternalForm());
        scene.getStylesheets().add(getClass().getResource("java-keywords.css").toExternalForm());
        primaryStage.setScene(scene);

        // Set the minimum height and width of the main stage
        primaryStage.setMinHeight(600);
        primaryStage.setMinWidth(800);

        // Show the stage
        primaryStage.show();

    }

    // stop() should be removed once clicking "x" to quit window is handled
    @FXML
    public void stop() {
        System.exit(0);
    }

    /**
     * Main method of the program that calls {@code launch} inherited from the
     * Application class
     *
     * @param args
     */
    public static void main(String[] args) {
        launch(args);
    }
}
