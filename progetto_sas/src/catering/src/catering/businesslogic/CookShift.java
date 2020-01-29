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
public class CookShift {
    
    private User cook;

    /**
     * Get the value of cook
     *
     * @return the value of cook
     */
    public User getCook() {
        return cook;
    }

    /**
     * Set the value of cook
     *
     * @param cook new value of cook
     */
    public void setCook(User cook) {
        this.cook = cook;
    }

    private Shift shift;

    /**
     * Get the value of shift
     *
     * @return the value of shift
     */
    public Shift getCookShift() {
        return shift;
    }

    /**
     * Set the value of shift
     *
     * @param shift new value of shift
     */
    public void setCookShift(Shift shift) {
        this.shift = shift;
    }

    private LocalDate date;

    /**
     * Get the value of date
     *
     * @return the value of date
     */
    public LocalDate getDate() {
        return date;
    }

    /**
     * Set the value of date
     *
     * @param date new value of date
     */
    public void setDate(LocalDate date) {
        this.date = date;
    }

    public CookShift(User cook, Shift shift, LocalDate date) {
        this.cook = cook;
        this.shift = shift;
        this.date = date;
    }

    @Override
    public String toString() {
        return shift.toString() + " - " + cook.toString();
    }
    
    

}
