/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package catering;

import catering.businesslogic.CateringAppManager;
import catering.businesslogic.Shift;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;

/**
 * FXML Controller class
 *
 * @author davide
 */
public class ShiftBoardController implements Initializable {

    @FXML
    private BorderPane mainContainer;
    @FXML
    private BorderPane eventListPane;
    @FXML
    private DatePicker datepicker;
    @FXML
    private ComboBox<Shift> cmbShift;
    @FXML
    private Button btnCloseShiftBoard;
    @FXML
    private ListView<String> shiftList;
    
    @FXML
    private RadioButton radioJob;
    
    @FXML
    private RadioButton radioFree;

    private List<ShiftBoardListener> editListeners;
    private ObservableList<Shift> observableShifts;
    private ObservableList<String> observableList;
    private Shift selectedShift;
    
    public ShiftBoardController() {
        editListeners = new ArrayList<>();
    }
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
   
    public void setup() {
        //Impostazione data odierna
        datepicker.setValue(LocalDate.now());
        
        //Formazione radio group
        ToggleGroup radioGroup = new ToggleGroup();
        radioJob.setToggleGroup(radioGroup);
        radioFree.setToggleGroup(radioGroup);
        radioJob.setSelected(true);
        
        //Inserimento turni nel combobox
        observableShifts = FXCollections.observableList(CateringAppManager.eventManager.getAllShift());
        cmbShift.setItems(observableShifts);
        
        //Listener cambio valore data
        datepicker.valueProperty().addListener((ov, oldValue, newValue) -> {
            fillShiftList();
        });
        
        //Listener cambio selezione radiogroup
        radioGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            public void changed(ObservableValue<? extends Toggle> ov,Toggle old_toggle, Toggle new_toggle) {
                shiftList.getItems().clear();
                fillShiftList();
            }
        });
        
        //Selezione turno, visualizzo tabellone turni per giorno indicato
        cmbShift.getSelectionModel().selectedItemProperty().addListener((observable) -> {
            selectedShift = cmbShift.getSelectionModel().getSelectedItem();
            fillShiftList();
        });
    }
    
    //Inserimento turni nella tabella
    private void fillShiftList()
    {      
        if(selectedShift != null && datepicker.getValue() != null) {
            if(radioJob.isSelected()) //Visualizza compiti
                observableList = FXCollections.observableList(CateringAppManager.eventManager.getShiftBoardJob(selectedShift, datepicker.getValue()));
            else if(radioFree.isSelected()) //Visualizza disponibilità
                observableList = FXCollections.observableList(CateringAppManager.eventManager.getShiftBoardAvailable(selectedShift, datepicker.getValue()));
            
            shiftList.setItems(observableList);
        }   
    }
    
    @FXML
    private void onClose(ActionEvent event) {
        for (ShiftBoardListener listener : editListeners) {
            listener.onClose();
        }       
    }
    
    public void listen(ShiftBoardListener l) {
        editListeners.add(l);
    }
}
