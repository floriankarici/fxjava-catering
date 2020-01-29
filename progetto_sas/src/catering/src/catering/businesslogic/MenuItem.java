package catering.businesslogic;

public class MenuItem implements Cloneable {
    private Recipe recipe;
    private String description;

    public MenuItem(Recipe rec, String desc) {
        this.recipe = rec;
        this.description = desc;
    }

    public MenuItem(Recipe rec) {
        this(rec, null);
        this.description = rec.getName();
    }

    public String toString() {
        return this.description;
    }

    public MenuItem clone() {
        MenuItem copia = new MenuItem(this.recipe, this.description);
        return copia;
    }

    public Recipe getRecipe() {
        return recipe;
    }

    public void setRecipe(Recipe recipe) {
        this.recipe = recipe;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
