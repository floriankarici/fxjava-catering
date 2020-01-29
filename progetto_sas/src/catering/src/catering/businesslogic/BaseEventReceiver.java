package catering.businesslogic;

import java.time.LocalDate;

public class BaseEventReceiver implements MenuEventReceiver, EventReceiver {
    @Override
    public void notifyMenuCreated(Menu m) {
    }

    @Override
    public void notifySectionAdded(Menu m, Section s) {

    }

    @Override
    public void notifyItemAdded(Menu m, Section s, MenuItem it) {

    }

    @Override
    public void notifyMenuPublished(Menu m) {

    }

    @Override
    public void notifyMenuDeleted(Menu m) {

    }

    @Override
    public void notifySectionRemoved(Menu m, Section s) {

    }

    @Override
    public void notifySectionNameChanged(Menu m, Section s) {

    }

    @Override
    public void notifySectionsRearranged(Menu m) {

    }

    @Override
    public void notifyItemsRearranged(Menu m, Section s) {

    }

    @Override
    public void notifyItemsRearrangedInMenu(Menu m) {

    }

    @Override
    public void notifyItemMoved(Menu m, Section oldS, Section newS, MenuItem it) {

    }

    @Override
    public void notifyItemDescriptionChanged(Menu m, MenuItem it) {

    }

    @Override
    public void notifyItemDeleted(Menu m, MenuItem it) {

    }

    @Override
    public void notifyMenuTitleChanged(Menu m) {

    }
    
    //EventReceiver
    
    @Override
    public void notifySummarySheetCreated(Event e,SummarySheet s) {

    }

    @Override
    public void notifyJobAdded(Event e, Job j) {

    }

    @Override
    public void notifyJobDeleted(Job j) {
     
    }

    @Override
    public void notifyAllJobsDeleted(Event e, Recipe rec) {
       
    }

    @Override
    public void notifyAssignJob(Event e, Job j, LocalDate date) {
        
    }

    @Override
    public void notifyAssignmentChanged(Event e, Job j, User cook) {
        
    }

    @Override
    public void notifyRemoveAssignment(Job j) {
        
    }
    
    @Override
    public void notifyJobsRearranged(SummarySheet sheet) {
        
    }
    
    @Override
    public void notifySummarySheetDeleted(Event e) {
        
    }

    
}
