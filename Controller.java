/*
 * File: Controller.java
 * Names: Erik Cohen, Matt Cerrato, Andy Xu
 * Class: CS 361
 * Project 4
 * Date: February 28
 */

package proj4CerratoCohenXu;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Optional;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.CodeArea;


/**
 * Controller class contains handler methods for buttons and menu items
 *
 */
public class Controller {

    @FXML
    private Button helloButton;
    @FXML
    private TabPane tabPane;
    @FXML
    private MenuItem undoMI,redoMI;
    @FXML
    private MenuItem selectAllMI,cutMI,copyMI,pasteMI;
    @FXML
    private MenuItem saveMI, saveAsMI, closeMI;

    // list of saved tabs and their content
    private HashMap<Tab,String> savedContents = new HashMap<>();
    // list of saved tabs and their saving path
    private HashMap<Tab,String> savedPaths = new HashMap<>();
    // keep track of the id for new tabs created
    private int newTabID = 1;


    /**
     * TODO: disable menu items here using noTabs() condition
     */
    @FXML
    private void initialize() {
        // handles clicking "x" for initial tab and primary window

        tabPane.getTabs().get(0).setOnCloseRequest(new EventHandler<Event>() {
            @Override
            public void handle(Event event) {
                tabPane.getSelectionModel().select(0);

                handleClose(new ActionEvent());
            }
        });

//        // trying to access primary stage here does not work
//        Stage primaryStage = (Stage) tabPane.getScene().getWindow();
//        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
//            @Override
//            public void handle(WindowEvent event) {
//                handleExit(new ActionEvent());
//            }
//        });

        // set disable property when no tabs are open
        saveMI.disableProperty().bind(noTabs());
        saveAsMI.disableProperty().bind(noTabs());
        closeMI.disableProperty().bind(noTabs());
        undoMI.disableProperty().bind(noTabs());
        redoMI.disableProperty().bind(noTabs());
        selectAllMI.disableProperty().bind(noTabs());
        cutMI.disableProperty().bind(noTabs());
        copyMI.disableProperty().bind(noTabs());
        pasteMI.disableProperty().bind(noTabs());
    }

    /**
     * returns a new BooleanBinding that holds true if
     * there is no tab in TabPane
     */
    private BooleanBinding noTabs() {
        return Bindings.isEmpty(tabPane.getTabs());
    }

    /**
     * Handler method for hello button. When hello button is clicked,
     * a text input dialog is created, and it will wait for user input.
     * The text of hello button will be changed to the user input if
     * user clicks ok button of the dialogue.
     *
     * @param event An ActionEvent object that gives information about the event
     *              and its source.
     */
    @FXML
    private void handleCompileButton(ActionEvent event) {
        // create a new input dialog
        TextInputDialog inputDialog = new TextInputDialog();
        inputDialog.setTitle("Give me a number");
        inputDialog.setHeaderText("Give me an integer from 0 to 255:");
        // showAndWait() returns an optional that contains the result of the dialogue
        Optional<String> result = inputDialog.showAndWait();

        // If the ok button is clicked
        if (result.isPresent()) {
            helloButton.setText(inputDialog.getEditor().getText());
        }
    }


    /**
     * Handler method for goodbye button. When goodbye button is clicked,
     * the word "Goodbye" will append to the text box in the selected tab.
     *
     * @param event An ActionEvent object that gives information about the event
     *              and its source.
     */
    @FXML
    private void handleCompileAndRunButton(ActionEvent event) {
        this.getSelectedTextBox().appendText(" Goodbye");
    }

    /**
     * Handler to stop a process (UNDER CONSTRUCTION) //TODO: handle stop button
     * @param event
     */
    @FXML
    private void handleStopButton(ActionEvent event){
        System.out.println("Stop Button Under Construction");
    }


    /**
     * helper function to get the currently selected tab in tabPane
     *
     * @return Tab  the selected tab
     */
    private Tab getSelectedTab () {
        return tabPane.getSelectionModel().getSelectedItem();
    }

    /**
     * helper function to get the text box in the selected tab
     *
     * @return TextArea  the text box in the selected tab
     */
    private CodeArea getSelectedTextBox() {
        return (CodeArea) ((VirtualizedScrollPane) getSelectedTab().getContent()).getContent();
    }


    /**
     * Handler method for menu bar item About. It shows a dialog that contains
     * program information.
     *
     * @param event An ActionEvent object that gives information about the event
     *              and its source.
     */
    @FXML
    private void handleAbout(ActionEvent event) {
        // create a new dialog
        Dialog dialog = new Dialog();
        dialog.setContentText("This is a code editor! \n\n "
                + "Authors: Erik Cohen, Matt Cerrato, Andy Xu");
        // add a close button so that dialog closing rule is fulfilled
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.showAndWait();
    }


    /**
     * Handler method for menu bar item New. It creates a new tab and adds it
     * to the tabPane.
     *
     * @param event An ActionEvent object that gives information about the event
     *              and its source.
     */
    @FXML
    private void handleNew(ActionEvent event) {
        // create a new tab
        this.newTabID++;
        Tab newTab = new Tab("Untitled-" + this.newTabID);
        newTab.setOnCloseRequest(new EventHandler<Event>() {
            @Override
            public void handle(Event event) {
                handleClose(new ActionEvent());
            }
        });

        // create a code area
        MyCodeArea myCodeArea = new MyCodeArea();
        CodeArea codeArea = myCodeArea.getCodeArea();
        newTab.setContent(new VirtualizedScrollPane<>(codeArea));
        // add new tab to the tabPane
        tabPane.getTabs().add(newTab);
        // make the newly created tab the topmost
        tabPane.getSelectionModel().selectLast();
        newTab.setOnCloseRequest(new EventHandler<Event>() {
            @Override
            public void handle(Event event) {
                tabPane.getSelectionModel().select(newTab);

                handleClose(new ActionEvent());
            }
        });
    }

    /**
     * Handler method for menu bar item Open. It shows a dialog and let the user
     * select a text file to be loaded to the text box.
     *
     * @param event An ActionEvent object that gives information about the event
     *              and its source.
     */
    @FXML
    private void handleOpen(ActionEvent event) {
        // create a new file chooser
        FileChooser fileChooser = new FileChooser();
        File initialDir = new File("./saved");
        // handles the case if ./saved directory does not exist
        if (! initialDir.exists()) {
            initialDir = new File("./");
        }
        fileChooser.setInitialDirectory(initialDir);
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Text Files", "*.txt"),
                new FileChooser.ExtensionFilter("FXML Files", "*.fxml"),
                new FileChooser.ExtensionFilter("CSS Files", "*.css"),
                new FileChooser.ExtensionFilter("Java Files", "*.java"));
        File selectedFile = fileChooser.showOpenDialog( tabPane.getScene().getWindow() );
        // if user selects a file (instead of pressing cancel button
        if (selectedFile != null) {
            // open a new tab
            this.handleNew(event);
            // set text/name of the tab to the filename
            this.getSelectedTab().setText(selectedFile.getName());
            this.newTabID--;  // no need to increment
            try {
                // reads the file content to a String
                String content = new String(Files.readAllBytes(
                        Paths.get(selectedFile.getPath())));
                this.getSelectedTextBox().replaceText(content);
                // update savedContents field
                this.savedContents.put(getSelectedTab(), content);
                this.savedPaths.put(getSelectedTab(), selectedFile.getPath());
            } catch (IOException e) {
                Alert alertBox = new Alert(Alert.AlertType.ERROR);
                alertBox.setHeaderText("File Opening Error");
                alertBox.setContentText("You do not have permission to read the selected file.");
                alertBox.show();
            }
        }
    }


    /**
     * Handler method for menu bar item Close. It creates a dialog if
     * the selected tab is unsaved and closes the tab.
     *
     * @param event An ActionEvent object that gives information about the event
     *              and its source.
     */
    @FXML
    public void handleClose(ActionEvent event) {
        // call helper method
        this.closeSelectedTab(event, false);
    }

    /**
     * Helper method that handles unsaved text and closes the tab. Shows a dialog
     * if the text gets modified since its last save or has never been saved.
     * Closes the tab if the text has been saved or user confirms.
     *
     * @param event An ActionEvent object that gives information about the event
     *              and its source.
     *
     * @return Optional the Optional object returned by dialog.showAndWait().
     *                  returns null if tab text is already saved.
     */
    private Optional<ButtonType> closeSelectedTab(ActionEvent event, boolean exiting) {
        // if content is not saved
        if (! selectedTabIsSaved()) {
            // create a new dialog
            Dialog dialog = new Dialog();
            // set the prompt text depending on exiting or closing
            String promptText;
            if (exiting) {
                promptText = String.format(
                        "Do you want to save %s before exiting?",
                        getSelectedTab().getText());
            } else {
                promptText = String.format(
                        "Do you want to save %s before closing it?",
                        getSelectedTab().getText());
            }
            dialog.setContentText(promptText);
            // add Yes, No, Cancel button
            dialog.getDialogPane().getButtonTypes().addAll(
                    ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);
            Optional<ButtonType> result  = dialog.showAndWait();
            // call handleSave() if user chooses YES
            if (result.get() == ButtonType.YES) {
                this.handleSave(event);
                // keep the tab if the save is unsuccessful (eg. canceled)
                if (! this.selectedTabIsSaved()) {
                    return result;
                }
            }
            // quit the dialog and keep selected tab if user chooses CANCEL
            else if (result.get() == ButtonType.CANCEL) {
                return result;
            }
        }
        // remove tab from tabPane if text is saved or user chooses NO
        this.savedContents.remove(getSelectedTab());
        this.savedPaths.remove(getSelectedTab());
        tabPane.getTabs().remove(getSelectedTab());
        return Optional.empty();
    }

    public void handleWindowExit(){
        ActionEvent event = new ActionEvent();
        handleExit(event);
    }

    /**
     * Handler method for menu bar item Exit. When exit item of the menu
     * bar is clicked, the application quits if all tabs in the tabPane are
     * closed properly.
     *
     * @param event An ActionEvent object that gives information about the event
     *              and its source.
     */
    @FXML
    private void handleExit(ActionEvent event) {
        // start while loop iteration from the last tab
        tabPane.getSelectionModel().selectLast();
        // loop through the tabs in tabPane
        while (tabPane.getTabs().size() > 0) {
            // try close the currently selected tab
            Optional<ButtonType> result = closeSelectedTab(event, true);
            // if the user chooses Cancel at any time, then the exiting is canceled,
            // and the application stays running.
            if (result.isPresent() && result.get() == ButtonType.CANCEL) {
                return;
            }
        }
        // exit if all tabs are closed
        System.exit(0);
    }

    /**
     * Helper method that checks if the text in the selected tab is saved.
     *
     * @return boolean  whether the text in the selected tab is saved.
     */
    private boolean selectedTabIsSaved() {
        // if tab name has been changed and current text matches with
        // the saved text in record
        if ( this.savedContents.containsKey(getSelectedTab()) &&
                getSelectedTextBox().getText().equals(
                        this.savedContents.get(getSelectedTab()))) {
            return true;
        }
        return false;
    }


    /**
     * Handler method for menu bar item Save. Behaves like Save as... if the text
     * has never been saved before. Otherwise, save the text to its corresponding
     * text file.
     *
     * @param event An ActionEvent object that gives information about the event
     *              and its source.
     */
    @FXML
    private void handleSave(ActionEvent event) {
        // if the text has been saved before
        if ( this.savedContents.containsKey(getSelectedTab()) ) {
            // create a File object for the corresponding text file
            File savedFile = new File(this.savedPaths.get(getSelectedTab()));
            try {
                // write the new content to the text file
                FileWriter fw = new FileWriter(savedFile);
                fw.write( getSelectedTextBox().getText() );
                fw.close();
                // update savedContents field
                this.savedContents.put(getSelectedTab(), getSelectedTextBox().getText());
            } catch (IOException e) {
                Alert alertBox = new Alert(Alert.AlertType.ERROR);
                alertBox.setHeaderText("File Saving Error");
                alertBox.setContentText("File was not saved successfully.");
                alertBox.show();
            }
        }
        // if text in selected tab was not loaded from a file nor ever saved to a file
        else {
            this.handleSaveAs(event);
        }
    }

    /**
     * Handler method for menu bar item Save as....  a dialog appears
     * in which the user is asked for the name of the file into which the
     * contents of the current text area are to be saved.
     *
     * @param event An ActionEvent object that gives information about the event
     *              and its source.
     */
    @FXML
    private void handleSaveAs(ActionEvent event) {
        // create a new fileChooser
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Text Files", "*.txt"),
                new FileChooser.ExtensionFilter("FXML Files", "*.fxml"),
                new FileChooser.ExtensionFilter("CSS Files", "*.css"),
                new FileChooser.ExtensionFilter("Java Files", "*.java"));
        File fileToSave = fileChooser.showSaveDialog(tabPane.getScene().getWindow());
        // if user did not choose CANCEL
        if ( fileToSave != null ) {
            try {
                // save file
                FileWriter fw = new FileWriter(fileToSave);
                fw.write(this.getSelectedTextBox().getText());
                fw.close();
                // update savedContents field and tab text
                this.savedContents.put(getSelectedTab(), getSelectedTextBox().getText());
                this.savedPaths.put(getSelectedTab(), fileToSave.getPath());
                this.getSelectedTab().setText(fileToSave.getName());
            } catch ( IOException e ) {
                Alert alertBox = new Alert(Alert.AlertType.ERROR);
                alertBox.setHeaderText("File Saving Error");
                alertBox.setContentText("File was not saved successfully.");
                alertBox.show();
            }
        }
    }


    /**
     * Handler method for menu bar item Undo.
     *
     * @param event An ActionEvent object that gives information about the event
     *              and its source.
     */
    @FXML
    private void handleUndo(ActionEvent event) {
        getSelectedTextBox().undo();
    }

    /**
     * Handler method for menu bar item Redo.
     *
     * @param event An ActionEvent object that gives information about the event
     *              and its source.
     */
    @FXML
    private void handleRedo(ActionEvent event) {
        getSelectedTextBox().redo();
    }

    /**
     * Handler method for menu bar item Cut.
     *
     * @param event An ActionEvent object that gives information about the event
     *              and its source.
     */
    @FXML
    private void handleCut(ActionEvent event) {
        getSelectedTextBox().cut();
    }

    /**
     * Handler method for menu bar item Copy.
     *
     * @param event An ActionEvent object that gives information about the event
     *              and its source.
     */
    @FXML
    private void handleCopy(ActionEvent event) {
        getSelectedTextBox().copy();
    }

    /**
     * Handler method for menu bar item Paste.
     *
     * @param event An ActionEvent object that gives information about the event
     *              and its source.
     */
    @FXML
    private void handlePaste(ActionEvent event) {
        getSelectedTextBox().paste();
    }

    /**
     * Handler method for menu bar item Select all.
     *
     * @param event An ActionEvent object that gives information about the event
     *              and its source.
     */
    @FXML
    private void handleSelectAll(ActionEvent event) {
        getSelectedTextBox().selectAll();
    }

}
