/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package catering.businesslogic;

/**
 *
 * @author Davide
 */
public class Event {
    private User chef;
    private String title;
    private Menu menu;
    private SummarySheet summarySheet;

    public Event(User owner, String title) {
        this(owner, title, null, null);
    }
    
    public Event (User owner, String title, Menu menu) {
        this(owner, title, menu, null);
    }
    
    public Event(User owner, String title, Menu menu, SummarySheet summarySheet) {
        this.chef = owner;
        this.title = title;
        this.menu = menu;
        this.summarySheet = summarySheet;
    }
    
    public void setSummarySheet(SummarySheet summarySheet) {
        this.summarySheet = summarySheet;
    }
    
    public User getOwner() {
        return this.chef;
    }
    
    public String getTitle() {
        return this.title;
    }
    
    public SummarySheet getSummarySheet()
    {
        return this.summarySheet;
    }
    
    @Override
    public String toString() {
        String ret = "";
        if (chef != null) {
            ret += "\t\tChef: " + chef;
        }
        if (summarySheet != null) {
            ret += "\t\tFoglio gia' creato";
        }
        ret += "\t\t" + this.title;
        if (menu != null) {
            ret += "\t\tMenu: " + menu.getTitle();
        }
        return ret;
    }
    
    public SummarySheet createSummarySheet(User u) {
        SummarySheet s = new SummarySheet(u);
        this.setSummarySheet(s);
        return s;
    }
    
    public void deleteSummarySheet() { this.summarySheet = null; }
}
