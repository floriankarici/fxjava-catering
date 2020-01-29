package catering.businesslogic;

import java.util.ArrayList;
import java.util.List;

public class RecipeManager {

    private List<Recipe> recipes;

    public RecipeManager() {
    }

    // Nota: nell'inizializzazione non carichiamo l'elenco di ricette
    // perché lo faremo "onDemand", ossia se viene richiesto da qualche altro oggetto
    // L'idea è evitare di caricare tutto se non serve.
    public void initialize() {
    }

    public List<Recipe> getRecipes() {
        //if (recipes == null) {
            recipes = new ArrayList<>();
            this.recipes.addAll(CateringAppManager.dataManager.loadRecipes());
        //}

        // Restituisce una copia della propria lista per impedire ad altri oggetti di modificarne
        // il contenuto
        List<Recipe> ret = new ArrayList<>();
        ret.addAll(recipes);
        return ret;
    }
}
