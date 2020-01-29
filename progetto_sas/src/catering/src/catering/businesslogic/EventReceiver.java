/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package catering.businesslogic;

import java.time.LocalDate;

/**
 *
 * @author davide
 */
public interface EventReceiver {
    public void notifySummarySheetCreated(Event e,SummarySheet s);
    public void notifyJobAdded(Event e, Job j);
    public void notifyJobDeleted(Job j);
    public void notifyAllJobsDeleted(Event e, Recipe rec);
    public void notifyAssignJob(Event e, Job j, LocalDate date);
    public void notifyAssignmentChanged(Event e, Job j, User cook);
    public void notifyRemoveAssignment(Job j);
    public void notifyJobsRearranged(SummarySheet sheet);
    public void notifySummarySheetDeleted(Event e);
}
