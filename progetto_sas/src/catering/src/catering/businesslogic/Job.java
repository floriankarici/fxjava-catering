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
public class Job {
    private Recipe recipe;
    private Shift shift;
    private User cook;
    private int eval;
    private double portions;
    private boolean completed;
    
    public Job(Recipe recipe)
    {
        this(recipe, null);
    }
    
    public Job(Recipe recipe, User cook) {
        this(recipe, cook, 0, 0, false);
    }
    
    public Job(Recipe recipe, User cook, int eval, double portions, boolean completed) {
        this.recipe = recipe;
        this.cook = cook;
        this.eval = eval;
        this.portions = portions;
        this.completed = completed;
    }

    public Recipe getRecipe() {
        return recipe;
    }

    public User getCook() {
        return cook;
    }

    public int getEval() {
        return eval;
    }

    public double getPortions() {
        return portions;
    }

    public boolean isCompleted() {
        return completed;
    }
    
    public void setShift(Shift s) {
        this.shift = s;
    }
    
    public Shift getShift() {
        return this.shift;
    }
    
    public String toString() {
        String ret = recipe.getName();
        if (!this.isAssigned()) {
            ret += ", DA ASSEGNARE";
        }
        return ret;
    }

    public void setRecipe(Recipe recipe) {
        this.recipe = recipe;
    }

    public void setCook(User cook) {
        this.cook = cook;
    }

    public void setEval(int eval) {
        this.eval = eval;
    }

    public void setPortions(double portions) {
        this.portions = portions;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
    
    public boolean isAssigned() {
        return this.shift != null;// && this.cook != null;
    }
    
    public void removeAssignment()
    {
        this.cook = null;
        this.shift = null;
        this.completed = false;
    }
    
    public void assignJob(Shift s, User cook, int eval, double portions, boolean completed)
    {
        this.setShift(s);
        this.setCook(cook);
        this.setEval(eval);
        this.setPortions(portions);
        this.setCompleted(completed);
    }
}
