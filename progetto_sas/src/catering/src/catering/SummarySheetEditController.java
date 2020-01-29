/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package catering;

import catering.businesslogic.*;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author davide
 */
public class SummarySheetEditController implements Initializable {
    private List<Job> jobs;
    private ObservableList<Job> observableJobs;
    private SummarySheet selectedSheet;
    private Job selectedJob;

    private ObservableList<Shift> observableShifts;
    private ObservableList<User> observableCook;
    

    private List<SheetEditListener> editListeners;
    
    @FXML
    private FlowPane bottomPane;
    @FXML
    private Button btnClose;
    @FXML
    private BorderPane mainPane;
    @FXML
    private BorderPane titlePane;
    @FXML
    private Label menuTitle;
    @FXML
    private Button btnTabelloneTurni;
    @FXML
    private HBox detailsPane;
    @FXML
    private BorderPane sectionsPane;
    @FXML
    private Button btnAddJob;
    @FXML
    private Button btnRemoveJob;
    @FXML
    private Button btnRemoveAllJob;
    @FXML
    private Button btnMoveDown;
    @FXML
    private Button btnMoveUp;
    
    @FXML
    private Button btnModifyAssignment;
    @FXML
    private ListView<Job> jobList;
    @FXML
    private BorderPane assignmentPane;
    
    @FXML
    private BorderPane defMain;
    @FXML
    private Label sectname;
    @FXML
    private ListView<?> assignmentList;
    @FXML
    private BorderPane setAssegnmentPane;
    @FXML
    private Button btnAddAssignment;
    @FXML
    private Button btnCancelAssignment;
    @FXML
    private DatePicker dtpDay;
    @FXML
    private ComboBox<Shift> cmbShift;
    @FXML
    private ComboBox<User> cmbCook;
    @FXML
    private TextField txtEval;
    @FXML
    private TextField txtDose;
    @FXML
    private CheckBox chkCompleted;
    
    SummarySheet sheet;
    
    boolean detailsMode = false;
    
    public SummarySheetEditController() {
        editListeners = new ArrayList<>();
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        hideAssignmentPane();
    }    
    
    public void setup()
    {
        hideAssignmentPane();
        
        selectedSheet = CateringAppManager.eventManager.getCurrentEvent().getSummarySheet();
        
        //Inserimento turni
        observableShifts = FXCollections.observableList(CateringAppManager.eventManager.getAllShift());
        cmbShift.setItems(observableShifts);
        
        btnModifyAssignment.setDisable(true);
        
        //Listener selezione job-> completamento campi
        jobList.getSelectionModel().selectedItemProperty().addListener((observable) -> {
            selectedJob = jobList.getSelectionModel().getSelectedItem();
            
            //Se il compito è stato assegnato, visualizzo dettagli
            if(selectedJob != null)
            {   
                
                assignmentPane.setVisible(false);
                assignmentPane.setManaged(false);
                setAssegnmentPane.setManaged(true);
                setAssegnmentPane.setVisible(true);
                
                
                if (selectedJob.isAssigned()) {
                    disableAssignmentPanel(true);
                    detailsMode = true;                    
                    //Impostazione campi dell'assegnamento - parte dx
                    txtDose.setText(String.valueOf(selectedJob.getPortions()));
                    txtEval.setText(String.valueOf(selectedJob.getEval()));
                    chkCompleted.setSelected(selectedJob.isCompleted());
                    cmbShift.getSelectionModel().select(selectedJob.getShift());
                    cmbCook.getSelectionModel().select(selectedJob.getCook());
                    dtpDay.setValue(CateringAppManager.eventManager.getDateJob(selectedJob));
                    if(selectedJob.getCook() == null && selectedJob.getShift() != null)
                        showAvailableCooks();
                    
                    
                    detailsMode=false;
                    
                    //Se job assegnato, tasto cancella assegnamento visibile
                    btnAddAssignment.setDisable(true);
                    btnCancelAssignment.setDisable(false);
                    btnModifyAssignment.setDisable(false);
                }
                else {
                    disableAssignmentPanel(false);
                    //Impostazione campi dell'assegnamento - parte dx
                    txtDose.setText(String.valueOf(selectedJob.getPortions()));
                    txtEval.setText(String.valueOf(selectedJob.getEval()));
                    chkCompleted.setSelected(selectedJob.isCompleted());
                    dtpDay.setValue(LocalDate.now());
                    
                        cmbShift.getSelectionModel().select(selectedJob.getShift());
                    
                        cmbCook.getSelectionModel().select(selectedJob.getCook());
                                       
                    //Se job non assegnato, tasto cancella assegnamento NON visibile
                    btnAddAssignment.setDisable(false);
                    btnCancelAssignment.setDisable(true);
                    btnModifyAssignment.setDisable(true);
                }
                
                
                
            }
            //Attivo button per eliminare compito selezionato - parte sx
            btnMoveDown.setDisable(selectedJob == null);
            btnMoveUp.setDisable(selectedJob == null);
            btnRemoveJob.setDisable(selectedJob == null);
            btnRemoveAllJob.setDisable(selectedJob == null);

        });
        
        //Listener cmbShift - INSERIMENTO CUOCHI DISPONIBILI PER IL GIORNO E IL TURNO INDICATO
        cmbShift.getSelectionModel().selectedItemProperty().addListener((obs) -> {
            showAvailableCooks();
        });
        
        dtpDay.valueProperty().addListener((observable, oldValue, newValue) -> {
            if(!detailsMode) //Non cancella combobox quando imposta la visuale dei dettagli di un job
            {
                cmbShift.getSelectionModel().clearSelection();
                cmbCook.getItems().clear();
                cmbCook.setValue(null);
            }
        });
        
        //Disabilita giorni passati nel datepicker
        dtpDay.setDayCellFactory(picker -> new DateCell() {
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                LocalDate today = LocalDate.now();
                setDisable(empty || date.compareTo(today) < 0 );
            }
        });
        
        
        //Job del current sheet
        this.resetJobList();
    }
    
    private void hideAssignmentPane()
    {
        assignmentPane.setVisible(true);
        assignmentPane.setManaged(true);
        setAssegnmentPane.setManaged(false);
        setAssegnmentPane.setVisible(false);
    }
    
    private void resetJobList()
    {
        jobs = selectedSheet.getJobs();
        observableJobs = FXCollections.observableList(jobs);       
        jobList.setItems(observableJobs);
        jobList.refresh();
        jobList.getFocusModel().focus(-1);
    }

    @FXML
    private void onClose(ActionEvent event) {
        for (SheetEditListener listener : editListeners) {
            listener.onClose();
        }       
    }

    //ShiftBoard
    private BorderPane shiftBoardPane;
    private ShiftBoardController sbController;

    @FXML
    private void onEditMenuTitle(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("shiftboard.fxml"));
            shiftBoardPane = loader.load();
            sbController = loader.getController();
            sbController.listen(new ShiftBoardListener() {
                @Override
                public void onClose() {
                    defMain.setCenter(mainPane);
                }
            });
            sbController.setup();
            defMain.setCenter(shiftBoardPane);
            shiftBoardPane.setMinWidth(defMain.getWidth());
            shiftBoardPane.setMinHeight(defMain.getHeight());
            
        } catch (IOException ex) {
            Logger.getLogger(SummarySheetEditController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    //Click su bottone per aggiungere un nuovo JOB
    @FXML
    private void onAddJob(ActionEvent event) {
        //Prendo lista recipe dal manager
        List<Recipe> allRec = CateringAppManager.recipeManager.getRecipes();
        
        //Choose Dialog
        ChoiceDialog<Recipe> dialog = new ChoiceDialog<>(allRec.get(0),allRec);
        dialog.setTitle("Creazione Compito");
        dialog.setHeaderText("Seleziona una preparazione di cucina per il nuovo compito");
        dialog.setContentText("Scegli :");

        // Traditional way to get the response value.
        Optional<Recipe> result = dialog.showAndWait();
        if (result.isPresent()){
            //Inserimento nuovo compito nel current sheet
            CateringAppManager.eventManager.addJob(result.get());
            resetJobList();
            
            
        }
    }

    @FXML
    private void onDeleteJob(ActionEvent event) {
        //Prendo job selezionato
        selectedJob = jobList.getSelectionModel().getSelectedItem();
        if(selectedJob != null)
        {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Eliminazione compito");
            alert.setHeaderText(null);
            alert.setContentText("Vuoi eliminare il compito selezionato?");
            
            ButtonType eliminaVoci = new ButtonType("Elimina");
            ButtonType annullaEliminazione = new ButtonType("Annulla");
            alert.getButtonTypes().setAll(eliminaVoci,annullaEliminazione);

            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == eliminaVoci){
                CateringAppManager.eventManager.deleteJob(selectedJob);
                this.resetJobList();
                jobList.getSelectionModel().clearSelection();
                assignmentPane.setVisible(true);
                assignmentPane.setManaged(true);
                setAssegnmentPane.setManaged(false);
                setAssegnmentPane.setVisible(false);
            } 
            
        }
    }

    @FXML
    private void onDeleteAllJobs(ActionEvent event) {
        selectedJob = jobList.getSelectionModel().getSelectedItem();
        if(selectedJob != null)
        {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Eliminazione compiti");
            alert.setHeaderText(null);
            alert.setContentText("Vuoi eliminare tutti i compiti che si riferiscono a " + selectedJob.getRecipe() + "?");
            
            ButtonType eliminaVoci = new ButtonType("Elimina");
            ButtonType annullaEliminazione = new ButtonType("Annulla");
            alert.getButtonTypes().setAll(eliminaVoci,annullaEliminazione);

            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == eliminaVoci){
                CateringAppManager.eventManager.deleteJobs(selectedJob.getRecipe());
                this.resetJobList();
                jobList.getSelectionModel().clearSelection();
                assignmentPane.setVisible(true);
                assignmentPane.setManaged(true);
                setAssegnmentPane.setManaged(false);
                setAssegnmentPane.setVisible(false);
            } 
            
        }
    }

    @FXML
    private void onMoveDownJob(ActionEvent event) {
        int pos = jobList.getSelectionModel().getSelectedIndex()/*+1*/;
        Job job = jobs.get(pos);
        CateringAppManager.eventManager.moveJob(job, pos+1);
        this.resetJobList();
    }

    @FXML
    private void onMoveUpJob(ActionEvent event) {
        int pos = jobList.getSelectionModel().getSelectedIndex()/*-1*/;
        Job job = jobs.get(pos);
        CateringAppManager.eventManager.moveJob(job, pos-1);
        this.resetJobList();
    }

    @FXML
    private void onOkAssignment(ActionEvent event) {
        selectedJob = jobList.getSelectionModel().getSelectedItem();
        Shift selectedShift = cmbShift.getSelectionModel().getSelectedItem();
        User selectedCook = cmbCook.getSelectionModel().getSelectedItem();
        int eval = Integer.valueOf(txtEval.getText());
        double portion = Double.valueOf(txtDose.getText());
        
        if (selectedCook != null && selectedShift != null) {
            if(selectedCook == cmbCook.getItems().get(0)) selectedCook = null;
            CateringAppManager.eventManager.assignJob(selectedJob, selectedShift, selectedCook, eval, portion, chkCompleted.isSelected(), dtpDay.getValue());

            assignmentPane.setVisible(true);
            assignmentPane.setManaged(true);
            setAssegnmentPane.setManaged(false);
            setAssegnmentPane.setVisible(false);

            this.resetJobList();
        }
        else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Assegnamento compito");
            alert.setHeaderText(null);
            alert.setContentText("Selezionare un turno per favore");
            alert.showAndWait();
        }
    }

    @FXML
    private void onCancelAssignment(ActionEvent event) {
        if(selectedJob != null && selectedJob.isAssigned())
        {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Eliminazione assegnazione");
            alert.setHeaderText(null);
            if(selectedJob.getCook() != null)
                alert.setContentText("Vuoi eliminare il compito assegnato a " + selectedJob.getCook() + "?");
            else
                alert.setContentText("Vuoi eliminare il compito assegnato?");

            ButtonType eliminaVoci = new ButtonType("Elimina");
            ButtonType annullaEliminazione = new ButtonType("Annulla");
            alert.getButtonTypes().setAll(eliminaVoci,annullaEliminazione);

            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == eliminaVoci){
                //Cancella assegnamento
                CateringAppManager.eventManager.removeAssignment(selectedJob);
                this.resetJobList();
                jobList.getSelectionModel().clearSelection();
                assignmentPane.setVisible(true);
                assignmentPane.setManaged(true);
                setAssegnmentPane.setManaged(false);
                setAssegnmentPane.setVisible(false);
            }
        }
    }
    
    public  void listen(SheetEditListener l) {
        editListeners.add(l);
    }
    
    private void showAvailableCooks() {
        LocalDate selecteDate = dtpDay.getValue();
        Shift selectedShift = cmbShift.getSelectionModel().getSelectedItem();
        
        if (selecteDate != null && selectedShift != null) {
            cmbCook.getSelectionModel().clearSelection();
            observableCook = FXCollections.observableList(CateringAppManager.eventManager.getAllCook(selectedShift, selecteDate));
            cmbCook.setItems(observableCook);
            observableCook.add(0, new User("Nessun Assegnamento"));
            if (observableCook.size() > 0) {
                cmbCook.getSelectionModel().select(0);
            }
        }
    }
    
    
    @FXML
    private void onModifyAssignment(ActionEvent event) {
        if(cmbCook.isDisabled()) {
            disableAssignmentPanel(true);
            btnModifyAssignment.setDisable(false);
            btnAddAssignment.setDisable(true);
            cmbCook.disableProperty().set(false);
            showAvailableCooks();
        }
        else {
            User selectedCook = cmbCook.getSelectionModel().getSelectedItem();
            if(selectedCook == cmbCook.getItems().get(0)) selectedCook = null;
            CateringAppManager.eventManager.changeAssignment(selectedJob, selectedCook);

            assignmentPane.setVisible(true);
            assignmentPane.setManaged(true);
            setAssegnmentPane.setManaged(false);
            setAssegnmentPane.setVisible(false);
            btnModifyAssignment.setDisable(true);
            this.resetJobList();
        }
    }
            
    private void disableAssignmentPanel(boolean enable) {
        txtDose.disableProperty().set(enable);
        txtEval.disableProperty().set(enable);
        dtpDay.disableProperty().set(enable);
        cmbCook.disableProperty().set(enable);
        cmbShift.disableProperty().set(enable);
        chkCompleted.disableProperty().set(enable);
    }
    
    private String getDateFromPicker() {
        String pattern = "dd/MM/yyyy";
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(pattern);
        LocalDate date = dtpDay.getValue();
        return dateFormatter.format(date);
    }
}
