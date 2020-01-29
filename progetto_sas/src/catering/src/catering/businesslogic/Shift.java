/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package catering.businesslogic;

/**
 *
 * @author davide
 */
public class Shift {
    
    private String timeFrom;

    /**
     * Get the value of timeFrom
     *
     * @return the value of timeFrom
     */
    public String getTimeFrom() {
        return timeFrom;
    }

    /**
     * Set the value of timeFrom
     *
     * @param timeFrom new value of timeFrom
     */
    public void setTimeFrom(String timeFrom) {
        this.timeFrom = timeFrom;
    }
    
        private String timeTo;

    /**
     * Get the value of timeTo
     *
     * @return the value of timeTo
     */
    public String getTimeTo() {
        return timeTo;
    }

    /**
     * Set the value of timeTo
     *
     * @param timeTo new value of timeTo
     */
    public void setTimeTo(String timeTo) {
        this.timeTo = timeTo;
    }

    public Shift(String timeFrom, String timeTo) {
        this.timeFrom = timeFrom;
        this.timeTo = timeTo;
    }

    @Override
    public String toString() {
        return timeFrom + "-" + timeTo;
    }
    
    
}
