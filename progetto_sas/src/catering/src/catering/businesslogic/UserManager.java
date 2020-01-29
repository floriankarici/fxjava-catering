package catering.businesslogic;

import catering.persistence.DataManager;

public class UserManager {

    private User currentUser;

    public void initialize() {
        // Questa è una versione "mockup" di UserManager
        // Ossia una versione semplificata che ha lo scopo di testare l'applicazione
        // Per questa ragione il metodo initialize() carica un utente di default dal DB
        this.currentUser = CateringAppManager.dataManager.loadUser("Viola");
    }


    public User getCurrentUser() {
        return currentUser;
    }
}
