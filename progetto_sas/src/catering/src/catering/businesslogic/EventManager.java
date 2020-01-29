/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package catering.businesslogic;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Davide
 */
public class EventManager {
    private List<Event> allEvents;
    private Event currentEvent;
    private List<EventReceiver> receivers;
    
    private List<Shift> allShift;
    
    public EventManager() {
        receivers = new ArrayList<>();    
    };
    
    /**
     * @return Lista degli eventi
     */
    public List<Event> getAllEvents() {
        if (allEvents == null) {
            allEvents = new ArrayList<>();
            allEvents.addAll(CateringAppManager.dataManager.loadEvents());
        }

        // Restituisce una copia della propria lista per impedire ad altri oggetti di modificarne
        // il contenuto
        List<Event> ret = new ArrayList<>();
        ret.addAll(allEvents);
        return ret;

    }
    
    /**
     * 1
     * Creazione nuovo foglio riepilogativo
     * @param e Evento di cui creare il foglio
     */
    public void createSummarySheet(Event e) {
        User u = CateringAppManager.userManager.getCurrentUser();
        if (!u.isChef()) throw new UseCaseLogicException("Solo gli chef possono creare un foglio riepilogativo");
        if (e.getOwner() != u) throw new EventException("Lo chef non è proprietario dell'evento");
        else {
            currentEvent = e;
            
            SummarySheet s = currentEvent.createSummarySheet(u);
            
            for (EventReceiver r: receivers) {
                r.notifySummarySheetCreated(currentEvent, s);
            }
        }
    }
    
    /**
     * 1a
     * Scelta foglio riepilogativo
     * @param e Evento di cui aprire il foglio riepilogativo
     * @return 
     */
    public Event chooseSheet(Event e) {
        User u = CateringAppManager.userManager.getCurrentUser();
        if (!u.isChef()) throw new UseCaseLogicException("Solo gli chef possono editare un menu");
        if (!e.getOwner().equals(u)) throw new EventException("Solo il proprietario " + u.toString() + " può modificare il foglio riepilogativo");
        if (e.getSummarySheet() == null) throw new EventException("L'evento non ha un foglio riepilogativo!");
        this.currentEvent = e;
        return this.currentEvent;
    }
    
    /**
     * 1b
     * Eliminazione foglio riepilogativo -> elimina tutti i job associati
     * @param e Evento di cui eliminare il foglio riepilogativo
     */
    public void deleteSummarySheet(Event e)
    {
        User u = CateringAppManager.userManager.getCurrentUser();
        if (!u.isChef()) throw new UseCaseLogicException("Solo gli chef possono eliminare un foglio riepilogativo");
        if (e.getSummarySheet() == null) throw new EventException("L'evento non ha un foglio riepilogativo!");
        for (EventReceiver r: receivers) {
            r.notifySummarySheetDeleted(currentEvent);
        }
        e.deleteSummarySheet();
    }
    
    /**
     * 2
     * Inserimento nuovo compito nel foglio riepilogativo
     * @param rec Procedura di cucina da inserire nel nuovo job
     * @return Job creato
     */
    public Job addJob(Recipe rec) {
        if(currentEvent == null) throw new UseCaseLogicException("non è stato specificato il foglio riepilogativo su cui si sta lavorando");
        //Creazione job
        Job j = this.currentEvent.getSummarySheet().addJob(rec);
        
        for (EventReceiver r: receivers) {
            r.notifyJobAdded(currentEvent, j);
        }
        return j;
    }
    
    /**
     * 2a
     * Rimozione di tutti i compiti riferiti ad una procedura di cucina
     * @param rec Procedura di cucina di cui cancellare i compiti
     */
    public void deleteJobs(Recipe rec) {
        if(currentEvent == null) throw new UseCaseLogicException("non è stato specificato il foglio riepilogativo su cui si sta lavorando");
        
        this.currentEvent.getSummarySheet().deleteJobs(rec);
        for (EventReceiver r: receivers) {
            r.notifyAllJobsDeleted(currentEvent, rec);
        }
        
        for (EventReceiver r: receivers) {
            r.notifyJobsRearranged(currentEvent.getSummarySheet());
        }
    }
    
    /**
     * 2b
     * Rimozione compito dal foglio riepilogativo
     * @param j Compito da rimuovere
     */
    public void deleteJob(Job j) {
        if(currentEvent == null) throw new UseCaseLogicException("non è stato specificato il foglio riepilogativo su cui si sta lavorando");
        if(!currentEvent.getSummarySheet().hasJob(j)) throw new EventException("il compito " + j.toString() + " non appartiene al foglio riepilogativo dell'evento corrente");
        
        this.currentEvent.getSummarySheet().deleteJob(j);
        
        for (EventReceiver r: receivers) {
                r.notifyJobDeleted(j);
            }
        for (EventReceiver r: receivers) {
            r.notifyJobsRearranged(currentEvent.getSummarySheet());
        }
        
    }
    
    /**
     * 3
     * Sposta job nella lista del foglio riepilogativo
     * @param job Job da spostare
     * @param pos Nuova posizione   
     */
    public void moveJob(Job job, int pos) {
        if (this.currentEvent == null) throw new UseCaseLogicException("non è stato specificato l'evento su cui si sta lavorando");
        SummarySheet sheet = this.currentEvent.getSummarySheet();
        if (sheet == null || !sheet.hasJob(job)) 
            throw new EventException("il compito " + job.toString() + " non appartiene al foglio riepilogativo dell'evento corrente");

        
        if (pos >= 0 && pos < sheet.getJobs().size()) {
            sheet.moveJob(job, pos);
            for (EventReceiver r: receivers) {
                r.notifyJobsRearranged(sheet);
            }
        }
    }
    
    /**
     * 4
     * Ritorna tabellone turni con i compiti
     * @param sh Turno
     * @param date Data
     * @return 
     */
    public List<String> getShiftBoardJob(Shift sh, LocalDate date) {
        return CateringAppManager.dataManager.getShiftBoardJob(date, sh);
    }
    
    /**
     * 4
     * Ritorna tabellone turni delle disponibilità
     * @param sh Turno
     * @param date Data
     * @return 
     */
    public List<String> getShiftBoardAvailable(Shift sh, LocalDate date) {
        return CateringAppManager.dataManager.getShiftBoardAvailable(date, sh);
    }
    
    /**
     * 5
     * Assegnamento Compito
     * @param j Job da assegnare
     * @param s Turno
     * @param cook Cuoco
     * @param eval Stima
     * @param portions Porzioni
     * @param completed Completo
     * @param date Data
     */
    public void assignJob(Job j, Shift s, User cook, int eval, double portions, boolean completed, LocalDate date) {
        if(currentEvent == null) throw new UseCaseLogicException("non è stato specificato il foglio riepilogativo su cui si sta lavorando");
        if(!currentEvent.getSummarySheet().hasJob(j)) throw new EventException("il compito " + j.toString() + " non appartiene al foglio riepilogativo dell'evento corrente");
        
        j.assignJob(s, cook, eval, portions, completed);
        
        for (EventReceiver r: receivers) {
            r.notifyAssignJob(currentEvent, j, date);
        }
    }
    
    /**
     * 5a
     * Modifica assegnamento del compito
     * @param j Job
     */
    public void changeAssignment(Job j, User cook) {
        if(currentEvent == null) throw new UseCaseLogicException("non è stato specificato il foglio riepilogativo su cui si sta lavorando");
        if(!currentEvent.getSummarySheet().hasJob(j)) throw new EventException("il compito " + j.toString() + " non appartiene al foglio riepilogativo dell'evento corrente");
       
        for (EventReceiver r: receivers) {
            r.notifyAssignmentChanged(currentEvent, j, cook);
        }
        j.setCook(cook);
    }
    
    /**
     * 5b
     * Rimuove assegnamento del cuoco al compito
     * @param j Job
     */
    public void removeAssignment(Job j) {
        if(currentEvent == null) throw new UseCaseLogicException("non è stato specificato il foglio riepilogativo su cui si sta lavorando");
        if(!currentEvent.getSummarySheet().hasJob(j)) throw new EventException("il compito " + j.toString() + " non appartiene al foglio riepilogativo dell'evento corrente");
        
        for (EventReceiver r: receivers) {
            r.notifyRemoveAssignment(j);
        }
        j.removeAssignment();
    }
    
    /**
     * Ritorna lista di tutti i turni
     * @return 
     */
    public List<Shift> getAllShift() {
        if(allShift == null)
        {
            allShift = new ArrayList<>();
            allShift.addAll(CateringAppManager.dataManager.loadShift());
        }
        
        // Restituisce una copia della propria lista per impedire ad altri oggetti di modificarne
        // il contenuto
        List<Shift> ret = new ArrayList<>();
        ret.addAll(allShift);
        return ret;
    }
    
    /**
     * Ritorna lista della disponibilità dei cuochi
     * @param s Turno
     * @param date Data
     * @return 
     */
    public List<User> getAllCook(Shift s, LocalDate date) {
        List<User> allCook = new ArrayList<>();
        allCook.addAll(CateringAppManager.dataManager.loadCook(s,date));
        
        // Restituisce una copia della propria lista per impedire ad altri oggetti di modificarne
        // il contenuto
        List<User> ret = new ArrayList<>();
        ret.addAll(allCook);
        return ret;
    }
    
    /**
     * Ritorna la data associata al compito
     * @param j Job
     * @return 
     */
    public LocalDate getDateJob(Job j)
    {
        return CateringAppManager.dataManager.getShiftDate(j);
    }
    
    public void addReceiver(EventReceiver rec) {
        this.receivers.add(rec);
    }

    public void removeReceiver(EventReceiver rec) {
        this.receivers.remove(rec);
    }
    
    public Event getCurrentEvent() {
        return this.currentEvent;
    }
}
