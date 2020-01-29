package catering.businesslogic;

import catering.persistence.DataManager;

import java.sql.SQLException;

public class CateringAppManager {
    private static CateringAppManager singleInstance;
    public static UserManager userManager;
    public static MenuManager menuManager;
    public static RecipeManager recipeManager;
    public static EventManager eventManager;

    // il data manager non è presente nel DSD perché non fa parte della business logic
    public static DataManager dataManager;

    public static CateringAppManager getInstance() {
        if (CateringAppManager.singleInstance == null){
            CateringAppManager.singleInstance = new CateringAppManager();
        }
        return CateringAppManager.singleInstance;
    }
    private CateringAppManager() {
        CateringAppManager.dataManager = new DataManager();
        CateringAppManager.userManager = new UserManager();
        CateringAppManager.menuManager = new MenuManager();
        CateringAppManager.eventManager = new EventManager();
        CateringAppManager.recipeManager = new RecipeManager();
        // Inizializza i GRASP controller e i servizi da utilizzare

        try {
            CateringAppManager.dataManager.initialize();
        } catch (SQLException exc) {
            // Rimando l'eccezione a terminale
            exc.printStackTrace();
        }
        CateringAppManager.userManager.initialize();
        //CateringAppManager.menuManager.initialize();
        CateringAppManager.recipeManager.initialize();
    }


}
