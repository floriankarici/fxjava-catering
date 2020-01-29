/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package catering;

import catering.businesslogic.*;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.BorderPane;

/**
 * FXML Controller class
 *
 * @author Davide
 */
public class EventListController {

    private List<Event> events;
    private ObservableList<Event> observableEvents;
    private SummarySheetEditController sheetEditController;
    private Event selectedEvent;

    @FXML
    private ListView<Event> eventList;

    @FXML
    private BorderPane mainContainer;

    @FXML
    private BorderPane eventListPane;

    private BorderPane sheetEditPane;

    @FXML
    Button editSheetButton;

    @FXML
    Button createSheetButton;

    @FXML
    Button deleteSheetButton;

    
    @FXML
    public void initialize() {
        
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("sheetedit.fxml"));
            sheetEditPane = loader.load();
            sheetEditController = loader.getController();
            sheetEditController.listen(new SheetEditListener() {
                @Override
                public void onClose() {
                    resetEventList();
                    mainContainer.setCenter(eventListPane);
                }
            });
        } catch (IOException exc) {
            exc.printStackTrace();
        }
        
        eventList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        this.resetEventList();


        
        eventList.getSelectionModel().selectedIndexProperty().addListener((observable) -> {
            selectedEvent = eventList.getSelectionModel().getSelectedItem();
            //verifica che l'evento selezionato è dell'utente collegato
            boolean ownershipOk = (selectedEvent != null) && (selectedEvent.getOwner().equals(CateringAppManager.userManager.getCurrentUser()));
            boolean editable = ownershipOk;
            
            if(ownershipOk)
            {
                 //Se l'evento non ha ancora un foglio riepilogativo, si può creare
                if(selectedEvent.getSummarySheet() == null)
                {
                    createSheetButton.setDisable(false);
                    editSheetButton.setDisable(true);
                    deleteSheetButton.setDisable(true);
                }
                else
                {
                    createSheetButton.setDisable(true);
                    editSheetButton.setDisable(false);
                    deleteSheetButton.setDisable(false);
                }

            }
            else
            {
                //Nessuna operazione se non è proprietario dell'evento
                createSheetButton.setDisable(true);
                editSheetButton.setDisable(true);
                deleteSheetButton.setDisable(true);
            }
            
        });
        

    }

    
    private void resetEventList() {
        events = CateringAppManager.eventManager.getAllEvents();
        observableEvents = FXCollections.observableList(events);       
        eventList.setItems(observableEvents);
        eventList.refresh();
    }

    
    /**
     * Creazione foglio riepilogativo per evento che non lo ha ancora
     */
    @FXML
    private void createSummarySheetAction() {
        if(selectedEvent != null) {
            CateringAppManager.eventManager.createSummarySheet(selectedEvent);
            sheetEditController.setup();
            mainContainer.setCenter(sheetEditPane);
        }
        resetEventList();
    }
    
    /**
     * Apertura per modificare il foglio riepilogativo dell'evento selezionato
     */
    @FXML
    private void editSheetAction() {
        // il foglio riepilogativo devi crearlo quando pigia salva dall'altra schermata
        CateringAppManager.eventManager.chooseSheet(this.selectedEvent);
        sheetEditController.setup();
        mainContainer.setCenter(sheetEditPane);
    }

    @FXML
    private void deleteSheetAction() {
        CateringAppManager.eventManager.deleteSummarySheet(this.selectedEvent);
        this.resetEventList();
    }
    
}
