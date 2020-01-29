package catering.persistence;

import catering.businesslogic.*;

import java.sql.*;
import java.time.LocalDate;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DataManager {
    private String userName = "root";
    private String password = "";
    //private String password = "root";
    private String serverName = "localhost";
    //private String portNumber = "8889";
    private String portNumber = "3306";

    private Connection connection;

    // Il DataManager deve tener traccia di quali oggetti in memoria
    // corrispondono a quali record del DB. Per questo usa una doppia
    // mappa per ciascun tipo di oggetto caricato
    private Map<User, Integer> userObjects;
    private Map<Integer, User> idToUserObject;

    private Map<Recipe, Integer> recipeObjects;
    private Map<Integer, Recipe> idToRecipeObject;

    private Map<Menu, Integer> menuObjects;
    private Map<Integer, Menu> idToMenuObject;

    private Map<Section, Integer> sectionObjects;
    private Map<Integer, Section> idToSectionObject;

    private Map<MenuItem, Integer> itemObjects;
    private Map<Integer, MenuItem> idToItemObject;
    
    private Map<Event, Integer> eventObjects;
    private Map<Integer, Event> idToEventObject;
    
    private Map<SummarySheet, Integer> summarySheetObjects;
    private Map<Integer, SummarySheet> idToSummarySheetObject;
    
    private Map<Job, Integer> jobObjects;
    private Map<Integer, Job> idToJobObject;
    
    private Map<Shift, Integer> shiftObjects;
    private Map<Integer, Shift> idToShiftObject;

    public DataManager() {

        this.userObjects = new HashMap<>();
        this.idToUserObject = new HashMap<>();
        this.recipeObjects = new HashMap<>();
        this.idToRecipeObject = new HashMap<>();
        this.menuObjects = new HashMap<>();
        this.idToMenuObject = new HashMap<>();
        this.sectionObjects = new HashMap<>();
        this.idToSectionObject = new HashMap<>();
        this.itemObjects = new HashMap<>();
        this.idToItemObject = new HashMap<>();
        this.eventObjects = new HashMap<>();
        this.idToEventObject = new HashMap<>();
        this.summarySheetObjects = new HashMap<>();
        this.idToSummarySheetObject = new HashMap<>();
        this.jobObjects = new HashMap<>();
        this.idToJobObject = new HashMap<>();
        this.shiftObjects = new HashMap<>();
        this.idToShiftObject = new HashMap<>();
    }

    public void initialize() throws SQLException {
        Connection conn = null;
        Properties connectionProps = new Properties();
        connectionProps.put("user", this.userName);
        connectionProps.put("password", this.password);
        /*
        connectionProps.put("useUnicode", true);
        connectionProps.put("useJDBCCompliantTimezoneShift", true);
        connectionProps.put("useLegacyDatetimeCode", false);
        connectionProps.put("serverTimezone", "UTC");
        */
        conn = DriverManager.getConnection(
                "jdbc:mysql://" +
                        this.serverName +
                        ":" + this.portNumber + "/catering",
                connectionProps);

        System.out.println("Connected to database");
        this.connection = conn;

        
        //Receiver EventManager
        CateringAppManager.eventManager.addReceiver(new EventReceiver() {
            @Override
            public void notifySummarySheetCreated(Event e, SummarySheet s) {
                //Creazione summarysheet
                int id = writeNewSummarySheet(e, s);
                
                //Inserimento nelle map
                summarySheetObjects.put(s, id);
                idToSummarySheetObject.put(id, s);
            }

            @Override
            public void notifyJobAdded(Event e, Job j) {
                //Creazione job
                int position = e.getSummarySheet().getJobPosition(j);
                int id = writeNewJob(e,j, position);
                
                //Inserimento nelle map
                jobObjects.put(j, id);
                idToJobObject.put(id, j);
            }

            @Override
            public void notifyJobDeleted(Job j) {
                removeJob(j);
            }

            @Override
            public void notifyAllJobsDeleted(Event e, Recipe rec) {
                removeAllJobs(e, rec);
            }

            @Override
            public void notifyAssignJob(Event e, Job j, LocalDate date) {
                writeNewAssignment(e, j, date);
            }

            @Override
            public void notifyAssignmentChanged(Event e, Job j, User cook) {
                changeAssignment(e, j, cook);
            }

            @Override
            public void notifyRemoveAssignment(Job j) {
                removeAssignment(j);
            }

            @Override
            public void notifyJobsRearranged(SummarySheet sheet) {
                List<Job> jobs = sheet.getJobs();
                int sheetId = summarySheetObjects.get(sheet);
                for (int i = 0; i < jobs.size(); i++) {
                    writeJobChanges(sheetId, i, jobs.get(i));
                }
            }
            
            @Override
            public void notifySummarySheetDeleted(Event e) {
                deleteSummarySheet(e);
            }
        });
    }
    
    /************************************METODI*************************************************/

    /**
     * Carica Utente
     * @param userName username utente
     * @return User caricato
     */
    public User loadUser(String userName) {
        PreparedStatement pst = null;
        String sql = "SELECT Users.id, Users.name, UserRoles.role FROM Users LEFT JOIN UserRoles on Users.id = "
                + "UserRoles.user where Users.name=?";
        User u = null;

        try {
            pst = this.connection.prepareStatement(sql);
            pst.setString(1, userName);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                if (u == null) {
                    u = new User(userName);
                    int id = rs.getInt("id");
                    this.userObjects.put(u, id);
                    this.idToUserObject.put(id, u);
                }

                addUserRole(u, rs);

            }
            pst.close();
        } catch (SQLException exc) {
            exc.printStackTrace();
        } finally {
            try {
                if (pst != null) pst.close();
            } catch (SQLException exc2) {
                exc2.printStackTrace();
            }
        }
        return u;
    }
    
    /**
     * Carica lista procedure di cucina
     * @return Lista di oggetti Recipe
     */
    public List<Recipe> loadRecipes() {
        Statement st = null;
        String query = "SELECT * FROM Recipes";
        List<Recipe> ret = new ArrayList<>();

        try {
            st = this.connection.createStatement();
            ResultSet rs = st.executeQuery(query);
            while (rs.next()) {
                String name = rs.getString("name");
                char type = rs.getString("type").charAt(0);
                int id = rs.getInt("id");

                // Verifica se per caso l'ha già caricata
                Recipe rec = this.idToRecipeObject.get(id);

                if (rec == null) {
                    rec = createRecipeWithType(name, type);

                    if (rec != null) {
                        ret.add(rec);
                        this.recipeObjects.put(rec, id);
                        this.idToRecipeObject.put(id, rec);
                    }
                }
                else ret.add(rec);
            }
        } catch (SQLException exc) {
            exc.printStackTrace();
        } finally {
            try {
                if (st != null) st.close();
            } catch (SQLException exc2) {
                exc2.printStackTrace();
            }
        }
        return ret;
    }

    /**
     * Carica lista di Menu
     * @return Lista di Menu
     */
    public List<Menu> loadMenus() {
        List<Menu> ret = new ArrayList<>();
        Statement st = null;
        String query = "SELECT * FROM Menus";

        try {
            st = this.connection.createStatement();
            ResultSet rs = st.executeQuery(query);
            while (rs.next()) {
                int id = rs.getInt("id");

                // Verifica se per caso l'ha già caricato
                Menu m = this.idToMenuObject.get(id);
                if (m == null) {

                    String title = rs.getString("title");

                    int ownerid = rs.getInt("menuowner");
                    User owner = this.innerLoadUser(ownerid);

                    m = new Menu(owner, title);
                    m.setPublished(rs.getBoolean("published"));
                    m.setBuffet(rs.getBoolean("buffet"));
                    m.setCookRequired(rs.getBoolean("cookRequired"));
                    m.setFingerFood(rs.getBoolean("fingerFood"));
                    m.setHotDishes(rs.getBoolean("hotDishes"));
                    m.setKitchenRequired(rs.getBoolean("kitchenRequired"));

                    // per sapere se il menu è in uso consulto la tabella degli eventi
                    // NdR: un menu è in uso anche se l'evento che lo usa è concluso o annullato
                    Statement st2 = this.connection.createStatement();
                    String query2 = "SELECT Events.id FROM Events JOIN Menus M on Events.menu = M.id WHERE M.id=" + id;
                    ResultSet rs2 = st2.executeQuery(query2);
                    m.setInUse(rs2.next());
                    st2.close();
                    loadMenuSections(id, m);
                    loadMenuItems(id, m);


                    ret.add(m);
                    this.menuObjects.put(m, id);
                    this.idToMenuObject.put(id, m);
                }
            }
        } catch (SQLException exc) {
            exc.printStackTrace();
        } finally {
            try {
                if (st != null) st.close();
            } catch (SQLException exc2) {
                exc2.printStackTrace();
            }
        }
        return ret;
    }
    
    //Carica item di un Menu
    private void loadMenuItems(int id, Menu m) {
        // Caricamento voci
        // Non verifichiamo se un MenuItem è già stato creato perché
        // questo può avvenire solo nel contesto del caricamento di un Menu
        // e il MenuItem può essere già creato solo se il Menu è stato creato;
        // il controllo sul Menu avviene già in loadMenus
        Statement st = null;
        String query = "SELECT MenuItems.* FROM MenuItems WHERE MenuItems.menu=" + id
                + " ORDER BY MenuItems.position";
        try {
            st = this.connection.createStatement();

            ResultSet rs = st.executeQuery(query);

            while (rs.next()) {
                String description = rs.getString("description");
                int idSec = rs.getInt("section");
                int idIt = rs.getInt("id");
                int idRec = rs.getInt("recipe");

                Recipe rec = this.innerLoadRecipe(idRec);

                Section sec = null;
                if (idSec > 0) {
                    // la sezione a questo punto dovrebbe essere già stata aggiunta
                    sec = this.idToSectionObject.get(idSec);
                }
                MenuItem it = m.addItem(rec, sec, description);
                this.itemObjects.put(it, idIt);
                this.idToItemObject.put(idIt, it);
            }
        } catch (SQLException exc) {
            exc.printStackTrace();
        } finally {
            try {
                if (st != null) st.close();
            } catch (SQLException exc2) {
                exc2.printStackTrace();
            }
        }
    }
    
    //Ritorna la procedura di cucina indicata con ID
    //Se è già stata caricata la ottiene tramite Map
    //Altrimenti la legge dal database
    private Recipe innerLoadRecipe(int idRec) {
        // verifico se l'ho già caricato in precedenza
        Recipe rec = this.idToRecipeObject.get(idRec);
        if (rec != null) return rec;

        Statement st = null;

        String query = "SELECT * FROM Recipes WHERE Recipes.id = " + idRec;
        try {
            st = this.connection.createStatement();
            ResultSet rs = st.executeQuery(query);
            if (rs.next()) {
                String name = rs.getString("name");
                char type = rs.getString("type").charAt(0);
                rec = createRecipeWithType(name, type);
                this.recipeObjects.put(rec, idRec);
                this.idToRecipeObject.put(idRec, rec);
            }
        } catch (SQLException exc) {
            exc.printStackTrace();
        } finally {
            try {
                if (st != null) st.close();
            } catch (SQLException exc2) {
                exc2.printStackTrace();
            }
        }

        return rec;
    }
    
    //Tipo di Procedura di Cucina
    private Recipe createRecipeWithType(String name, char type) {
        switch (type) {
            case 'r':
                return new Recipe(name, Recipe.Type.Dish);
            case 'p':
                return new Recipe(name, Recipe.Type.Preparation);

        }
        return null;
    }
    
    //Carica Sezioni del Menu
    private void loadMenuSections(int id, Menu m) {
        // Caricamento sezioni
        // Non verifichiamo se una Section è già stata creata perché
        // questo può avvenire solo nel contesto del caricamento di un Menu
        // e la Section può essere già creata solo se il Menu è stato creato;
        // il controllo sul Menu avviene già in loadMenus
        Statement st = null;
        String query = "SELECT Sections.* FROM Sections WHERE Sections.menu=" + id + " ORDER BY Sections.position";

        try {
            st = this.connection.createStatement();
            ResultSet rs = st.executeQuery(query);

            while (rs.next()) {
                String name = rs.getString("name");
                int idSec = rs.getInt("id");

                Section sec = m.addSection(name);
                this.sectionObjects.put(sec, idSec);
                this.idToSectionObject.put(idSec, sec);
            }
        } catch (SQLException exc) {
            exc.printStackTrace();
        } finally {
            try {
                if (st != null) st.close();
            } catch (SQLException exc2) {
                exc2.printStackTrace();
            }
        }

    }
    
    //Ritorna utente indicato dall'ID
    private User innerLoadUser(int userId) {
        // verifico se l'ho già caricato in precedenza
        User u = this.idToUserObject.get(userId);
        if (u != null) return u;

        Statement st = null;
        String query = "SELECT Users.id, Users.name, UserRoles.role FROM Users LEFT JOIN UserRoles on Users.id = " +
                "UserRoles.user where Users.id=" + userId;
        try {
            st = this.connection.createStatement();
            ResultSet rs = st.executeQuery(query);
            while (rs.next()) {
                if (u == null) {
                    u = new User(rs.getString("name"));
                    this.userObjects.put(u, userId);
                    this.idToUserObject.put(userId, u);
                }
                addUserRole(u, rs);
            }

        } catch (SQLException exc) {
            exc.printStackTrace();
        } finally {
            try {
                if (st != null) st.close();
            } catch (SQLException exc2) {
                exc2.printStackTrace();
            }
        }

        return u;
    }
    
    //Aggiunge tipo di utente
    private void addUserRole(User u, ResultSet rs) throws SQLException {
        char roleName = rs.getString("role").charAt(0);
        switch (roleName) {
            case 'c':
                u.addRole(User.Role.Cuoco);
                break;
            case 'h':
                u.addRole(User.Role.Chef);
                break;
            case 'o':
                u.addRole(User.Role.Organizzatore);
                break;
            case 's':
                u.addRole(User.Role.Servizio);
                break;
        }
    }
    
    /**********************METODI DI ASSEGNARE COMPITI***********************/
    
    /**
     * Lettura lista di eventi dal database
     * @return Lista di eventi
     */
    public List<Event> loadEvents() {
        List<Event> ret = new ArrayList<>();
        Statement st = null;
        String query = "SELECT * FROM Events";

        try {
            st = this.connection.createStatement();
            ResultSet rs = st.executeQuery(query);
            while (rs.next()) {
                int eventId = rs.getInt("id");

                // Verifica se per caso l'ha già caricato
                Event event = this.idToEventObject.get(eventId);
                if (event == null) {

                    int ownerid = rs.getInt("Chef");
                    String title = rs.getString("title");
                    User owner = this.innerLoadUser(ownerid);

                    event = new Event(owner,title);
                    loadEventSummarySheet(eventId, event);
                    
                    ret.add(event);
                    this.eventObjects.put(event, eventId);
                    this.idToEventObject.put(eventId, event);
                }
            }
        } catch (SQLException exc) {
            exc.printStackTrace();
        } finally {
            try {
                if (st != null) st.close();
            } catch (SQLException exc2) {
                exc2.printStackTrace();
            }
        }
        return ret;
    }
    
    /**
     * Lettura Foglio Riepilogativo dell'evento
     * @param eventId ID Evento
     * @param event Evento del foglio riepilogativo
     */
    private void loadEventSummarySheet(int eventId, Event event) {
        Statement st = null;
        String query = "select * from SummarySheet where SummarySheet.Event = " + eventId;
        try {
            st = this.connection.createStatement();

            ResultSet rs = st.executeQuery(query);

            while (rs.next()) {
                int idSheet = rs.getInt("id");
                
                SummarySheet sheet = this.idToSummarySheetObject.get(idSheet);
                if (sheet == null) {
                    int idOwner = rs.getInt("owner");
                    User owner = this.innerLoadUser(idOwner);
                    sheet = new SummarySheet(owner);
                    event.setSummarySheet(sheet);
                    
                    loadSummarySheetJobs(idSheet, sheet);
                    
                    this.summarySheetObjects.put(sheet, idSheet);
                    this.idToSummarySheetObject.put(idSheet, sheet);
                }
            }
        } catch (SQLException exc) {
            exc.printStackTrace();
        } finally {
            try {
                if (st != null) st.close();
            } catch (SQLException exc2) {
                exc2.printStackTrace();
            }
        }
    }

    /**
     * Lettura dei compiti di un foglio riepilogativo
     * @param idSheet ID foglio riepilogativo
     * @param summarySheet Foglio riepilogativo di cui leggere i compiti
     */
    private void loadSummarySheetJobs(int idSheet, SummarySheet summarySheet) {
        Statement st = null;
        //Left join perchè ci possono essere compiti senza cookshift assegnato
        String query = "SELECT * FROM Job LEFT JOIN CookShift ON Job.shift = CookShift.id WHERE Job.summarysheet = " + idSheet + " ORDER BY Position";
        try {
            st = this.connection.createStatement();

            ResultSet rs = st.executeQuery(query);

            while (rs.next()) {
                int idJob = rs.getInt("Job.id");
                int idRec = rs.getInt("recipe");
                int idUser = rs.getInt("CookShift.cook");
                int eval = rs.getInt("eval");
                double portions = rs.getDouble("portions");
                boolean completed = rs.getBoolean("completed");
                
                Recipe rec = this.innerLoadRecipe(idRec);
                User cook = this.innerLoadUser(idUser);
                Shift shift = this.innerLoadShift(rs.getInt("CookShift.shift"));
                
                Job job = new Job(rec, cook, eval, portions, completed);
                job.setShift(shift);
                     
                //Inserimento compito nel foglio riepilogativo
                summarySheet.addJob(job);
                
                this.jobObjects.put(job, idJob);
                this.idToJobObject.put(idJob, job);
            }
        } catch (SQLException exc) {
            exc.printStackTrace();
        } finally {
            try {
                if (st != null) st.close();
            } catch (SQLException exc2) {
                exc2.printStackTrace();
            }
        }
    }
    
    public List<Shift> loadShift() {
        Statement st = null;
        String query = "SELECT * FROM Shift";
        List<Shift> ret = new ArrayList<>();

        try {
            st = this.connection.createStatement();
            ResultSet rs = st.executeQuery(query);
            while (rs.next()) {
                String timeFrom = rs.getString("timeFrom");
                String timeTo = rs.getString("timeTo");
                int id = rs.getInt("id");

                // Verifica se per caso l'ha già caricata
                Shift sh = this.idToShiftObject.get(id);

                if (sh == null) {
                    sh = new Shift(timeFrom, timeTo);

                    this.shiftObjects.put(sh, id);
                    this.idToShiftObject.put(id, sh);
                }
                ret.add(sh);
            }
        } catch (SQLException exc) {
            exc.printStackTrace();
        } finally {
            try {
                if (st != null) st.close();
            } catch (SQLException exc2) {
                exc2.printStackTrace();
            }
        }
        return ret;
    }
    
    /**
     * Lettura dei cuochi da DB
     * @return Lista dei cuochi
     */
    public List<User> loadCook() {
        PreparedStatement pst = null;
        String sql = "SELECT Users.id, Users.name, UserRoles.role FROM Users LEFT JOIN UserRoles on Users.id = "
                + "UserRoles.user where UserRoles.role = 'c'";
        User u = null;
        List<User> ret = new ArrayList<>();
        try {
            pst = this.connection.prepareStatement(sql);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {                
                u = new User(userName);
                int id = rs.getInt("id");
                this.userObjects.put(u, id);
                this.idToUserObject.put(id, u);
                ret.add(u);

                addUserRole(u, rs);

            }
            pst.close();
        } catch (SQLException exc) {
            exc.printStackTrace();
        } finally {
            try {
                if (pst != null) pst.close();
            } catch (SQLException exc2) {
                exc2.printStackTrace();
            }
        }
        return ret;
    }
    
    /**
     * Lettura cuochi che hanno fornito una disponibilità
     * @param s Turno disponibilità
     * @param date Data disponibilità
     * @return Lista cuochi
     */
    public List<User> loadCook(Shift s, LocalDate date) {
        PreparedStatement pst = null;
        
        String sql = "SELECT * FROM CookShift, Users WHERE shift=? and date = ? and CookShift.cook = Users.id";
        User u = null;
        List<User> ret = new ArrayList<>();
        try {
            pst = this.connection.prepareStatement(sql);
            pst.setInt(1, shiftObjects.get(s));
            pst.setDate(2, java.sql.Date.valueOf(date));
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {                
                u = new User(rs.getString("Users.name"));
                int id = rs.getInt("Users.id");
                
                if (idToUserObject.containsKey(id)) {
                    u = idToUserObject.get(id);
                }
                else {
                    this.userObjects.put(u, id);
                    this.idToUserObject.put(id, u);
                }
                
                if (!ret.contains(u)) {
                    ret.add(u);
                }
            }
            pst.close();
        } catch (SQLException exc) {
            exc.printStackTrace();
        } finally {
            try {
                if (pst != null) pst.close();
            } catch (SQLException exc2) {
                exc2.printStackTrace();
            }
        }
        return ret;
    }
    
    /**
     * Lettura Shift
     * @param shiftId ID del turno
     * @return Shift
     */
    private Shift innerLoadShift(int shiftId) {
        // verifico se l'ho già caricato in precedenza
        Shift shift = this.idToShiftObject.get(shiftId);
        if (shift != null) return shift;

        Statement st = null;
        String query = "SELECT * FROM Shift WHERE id = " + shiftId;
        try {
            st = this.connection.createStatement();
            ResultSet rs = st.executeQuery(query);
            while (rs.next()) {
                if (shift == null) {
                    shift = new Shift(rs.getString("timeFrom"), rs.getString("timeTo"));
                    this.shiftObjects.put(shift, shiftId);
                    this.idToShiftObject.put(shiftId, shift);
                }
            }

        } catch (SQLException exc) {
            exc.printStackTrace();
        } finally {
            try {
                if (st != null) st.close();
            } catch (SQLException exc2) {
                exc2.printStackTrace();
            }
        }

        return shift;
    }
    
    /**
     * Modifica compito
     * @param idSheet ID foglio riepilogativo
     * @param position Nuova posizione
     * @param job Job da modificare
     */
    private void writeJobChanges(int idSheet, int position, Job job) {
        int jobId = jobObjects.get(job);
        String sql = "UPDATE Job "
                + "SET recipe=?, eval=?, portions=?, position=?, summarysheet=? "
                + "WHERE id=" + jobId + ";";
        
        int recipeId = recipeObjects.get(job.getRecipe());
        PreparedStatement pstmt = null;
        try {
            pstmt = this.connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setInt(1, recipeId);
            pstmt.setInt(2, job.getEval());
            pstmt.setDouble(3, job.getPortions());
            pstmt.setInt(4, position);
            pstmt.setInt(5, idSheet);

            if (job.getCook() != null) {
                sql += " UPDATE Job SET cook=" + userObjects.get(job.getCook()) + ";";
            }
            if (job.getShift() != null) {
                sql += " UPDATE Job SET shift=" + shiftObjects.get(job.getShift()) + ";";
            }
            pstmt.executeUpdate();

        } catch (SQLException exc) {
            exc.printStackTrace();
        } finally {
            try {
                if (pstmt != null) pstmt.close();
            } catch (SQLException exc2) {
                exc2.printStackTrace();
            }
        }
    }
    
    /**
     * Creazione foglio riepilogativo di un evento
     * @param e Evento
     * @param s SummarySheet da inserire
     * @return id nuovo foglio creato
     */
    private int writeNewSummarySheet(Event e, SummarySheet s) {

        String sql = "INSERT INTO SummarySheet(owner, event) "
                + "VALUES(?,?)";
        int id = -1;
        PreparedStatement pstmt = null;
        try {
            pstmt = this.connection.prepareStatement(sql,
                    Statement.RETURN_GENERATED_KEYS);
            pstmt.setInt(1, userObjects.get(e.getOwner()));
            pstmt.setInt(2, eventObjects.get(e));

            int r = pstmt.executeUpdate();

            if (r == 1) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    id = rs.getInt(1);
                }
            }
        } catch (SQLException exc) {
            exc.printStackTrace();
        } finally {
            try {
                if (pstmt != null) pstmt.close();
            } catch (SQLException exc2) {
                exc2.printStackTrace();
            }
        }
        return id;
    }
    
    /**
     * Creazione nuovo Job per un foglio riepilogativo
     * @param e Evento
     * @param j Job da creare
     * @param position posizione nell'elenco del job
     * @return ID job creato
     */
    private int writeNewJob(Event e, Job j, int position) {

        String sql = "INSERT INTO Job(recipe, summarysheet, position) VALUES(?, ?, ?)";
        int id = -1;
        PreparedStatement pstmt = null;
        try {
            pstmt = this.connection.prepareStatement(sql,
                    Statement.RETURN_GENERATED_KEYS);
            pstmt.setInt(1, recipeObjects.get(j.getRecipe()));
            pstmt.setInt(2, summarySheetObjects.get(e.getSummarySheet()));
            pstmt.setInt(3, position);

            int r = pstmt.executeUpdate();

            if (r == 1) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    id = rs.getInt(1);
                }
            }
        } catch (SQLException exc) {
            exc.printStackTrace();
        } finally {
            try {
                if (pstmt != null) pstmt.close();
            } catch (SQLException exc2) {
                exc2.printStackTrace();
            }
        }
        return id;
    }
    
    /**
     * Rimuove un Job dal foglio riepilogativo
     * @param j Job da rimuovere     
     */
    private void removeJob(Job j) {
        
        if(j.getCook() == null)
        {
            // Se il job aveva assegnato un turno ma non il cuoco, il turno del job fa riferimento a un record
            // in cookshift avente cook = NULL. In quel caso elimino quel record che non serve piu'
            deleteCookShift(j);
        }
        
        String sql = "DELETE FROM Job WHERE id=?";
        
        PreparedStatement pstmt = null;
        try {
            pstmt = this.connection.prepareStatement(sql,
                    Statement.RETURN_GENERATED_KEYS);
            pstmt.setInt(1, jobObjects.get(j));

            pstmt.executeUpdate();
        } catch (SQLException exc) {
            exc.printStackTrace();
        } finally {
            try {
                if (pstmt != null) pstmt.close();
            } catch (SQLException exc2) {
                exc2.printStackTrace();
            }
        }
    }
    
    /**
     * Cancella tutti i compiti collegati ad una procedura di cucina dell'evento
     * @param e Evento
     * @param rec Procedura di cucina
     */
    private void removeAllJobs(Event e, Recipe rec) {
        //Rimozione di tutti i Job con assegnato solo il turno
        PreparedStatement pstmt = null;
        //Cancella cookshift senza cuoco il cui ID è collegato ad un compito da eliminare
        String sql2 = "DELETE FROM CookShift WHERE cook IS NULL AND id = ANY (SELECT shift FROM Job WHERE summarysheet=? and recipe=?)";
        try {
            pstmt = this.connection.prepareStatement(sql2, Statement.RETURN_GENERATED_KEYS);
            pstmt.setInt(1, summarySheetObjects.get(e.getSummarySheet()));
            pstmt.setInt(2, recipeObjects.get(rec));
            pstmt.executeUpdate();
        } catch (SQLException exc) {
            exc.printStackTrace();
        } finally {
            try {
                if (pstmt != null) pstmt.close();
            } catch (SQLException exc2) {
                exc2.printStackTrace();
            }
        }
        
        String sql = "DELETE FROM Job WHERE summarysheet=? and recipe=?";
        try {
            pstmt = this.connection.prepareStatement(sql,
                    Statement.RETURN_GENERATED_KEYS);
            pstmt.setInt(1, summarySheetObjects.get(e.getSummarySheet()));
            pstmt.setInt(2, recipeObjects.get(rec));

            pstmt.executeUpdate();

        } catch (SQLException exc) {
            exc.printStackTrace();
        } finally {
            try {
                if (pstmt != null) pstmt.close();
            } catch (SQLException exc2) {
                exc2.printStackTrace();
            }
        }
    }
           
    /**
     * Inserisce nuovo assegnamento
     * @param e Evento
     * @param j Job
     * @param date Data assegnamento
     */
    private void writeNewAssignment(Event e, Job j, LocalDate date) {
        int jid = jobObjects.get(j);
        String sql = "";
        PreparedStatement pstmt = null;
        //Se il cuoco è specificato
        if(j.getCook() != null)
        {
            try {
                //Eliminazione del turno precedentemente assegnato, se presente
                sql = "SELECT CookShift.cook FROM CookShift JOIN Job on Job.shift = CookShift.id WHERE Job.id = " + jid;
                pstmt = this.connection.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    if (rs.getString("cook") == null) {
                        rs.close();
                        
                        //Cancello cookshift
                        deleteCookShift(j);
                    }
                }

                //Aggiornamento Job per assegnamento
                sql = "UPDATE Job SET Job.shift=(SELECT CookShift.id FROM CookShift WHERE CookShift.shift=? and CookShift.date=? and CookShift.cook = ?), eval=?, portions=?, completed=? "
                    + "WHERE id=" + jid;
                pstmt = this.connection.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
                pstmt.setInt(1, shiftObjects.get(j.getShift()));
                pstmt.setDate(2, java.sql.Date.valueOf(date));
                pstmt.setInt(3, userObjects.get(j.getCook()));
                pstmt.setInt(4, j.getEval());
                pstmt.setDouble(5, j.getPortions());
                pstmt.setBoolean(6, j.isCompleted());
                
                int r = pstmt.executeUpdate();
            } catch (SQLException exc) {
                exc.printStackTrace();
            } finally {
                try {
                    if (pstmt != null) pstmt.close();
                } catch (SQLException exc2) {
                    exc2.printStackTrace();
                }
            }
        }
        else {
            //Cuoco non inserito, creo cookshift e aggiorno Job
            try {
                int idInsertedCookShift = createCookShift(j, j.getShift(), date);
                
                // Ora modifico il job, assegnando come shift quello appena inserito
                sql = "UPDATE Job SET shift=?, eval=?, portions=?, completed=? "
                    + "WHERE id=" + jid;
                pstmt = this.connection.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
                pstmt.setInt(1, idInsertedCookShift);
                pstmt.setInt(2, j.getEval());
                pstmt.setDouble(3, j.getPortions());
                pstmt.setBoolean(4, j.isCompleted());

                int r = pstmt.executeUpdate();
            } catch (SQLException exc) {
                exc.printStackTrace();
            } finally {
                try {
                    if (pstmt != null) pstmt.close();
                } catch (SQLException exc2) {
                    exc2.printStackTrace();
                }
            }
        }
    }

    /**
     * Ritorna lista dei compiti assegnati in un certo turno in un giorno per Tabellone turni
     * @param date Data compito
     * @param sh Turno compito
     * @return Lista compiti assegnati
     */
    public List<String> getShiftBoardJob(LocalDate date, Shift sh) {
        PreparedStatement pst = null;
        
        String sql = "SELECT * FROM CookShift, Job, Users, Recipes WHERE CookShift.shift=? and CookShift.date=? and CookShift.cook = Users.id " 
                + " AND CookShift.id = Job.shift AND Job.recipe = Recipes.id";
        List<String> ret = new ArrayList<>();
        try {
            pst = this.connection.prepareStatement(sql);
            pst.setInt(1, shiftObjects.get(sh));
            pst.setDate(2, java.sql.Date.valueOf(date));
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {                
                ret.add(rs.getString("Users.name") + ": " + rs.getString("Recipes.name") + "," + rs.getString("Job.portions") + "porz, completo :" + rs.getBoolean("Job.completed"));
            }
            pst.close();
        } catch (SQLException exc) {
            exc.printStackTrace();
        } finally {
            try {
                if (pst != null) pst.close();
            } catch (SQLException exc2) {
                exc2.printStackTrace();
            }
        }
        
        /** AGGIUNTA PER I JOB A CUI NON SI ASSEGNA IL CUOCO, MA IL TURNO SI-*/
        
        sql = "SELECT * FROM Job, CookShift, Recipes WHERE Job.recipe=Recipes.id AND Job.shift=CookShift.id AND CookShift.date=? AND CookShift.shift=? AND CookShift.cook IS NULL ";
        try {
            pst = this.connection.prepareStatement(sql);
            pst.setDate(1, java.sql.Date.valueOf(date));
            pst.setInt(2, shiftObjects.get(sh));
            
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {                
                ret.add("Nessun cuoco assegnato" + ": " + rs.getString("Recipes.name") + "," + rs.getString("Job.portions") + "porz, completo :" + rs.getBoolean("Job.completed"));
            }
            pst.close();
        } catch (SQLException exc) {
            exc.printStackTrace();
        } finally {
            try {
                if (pst != null) pst.close();
            } catch (SQLException exc2) {
                exc2.printStackTrace();
            }
        }
        
        return ret;
    }
    
    /**
     * Ritorna disponibilità cuochi per tabellone turni
     * @param date Data disponibilità
     * @param sh Turno disponibilità
     * @return Lista di disponibilità
     */
    public List<String> getShiftBoardAvailable(LocalDate date, Shift sh) {
        PreparedStatement pst = null;
        
        String sql = "SELECT * FROM CookShift, Users WHERE CookShift.shift=? and CookShift.date=? and CookShift.cook = Users.id ";
                //+ " AND CookShift.id NOT IN (SELECT shift FROM Job)"; -> x solo un compito a cuoco per turno
        List<String> ret = new ArrayList<>();
        try {
            pst = this.connection.prepareStatement(sql);
            pst.setInt(1, shiftObjects.get(sh));
            pst.setDate(2, java.sql.Date.valueOf(date));
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {                
                ret.add(rs.getString("Users.name"));
            }
            pst.close();
        } catch (SQLException exc) {
            exc.printStackTrace();
        } finally {
            try {
                if (pst != null) pst.close();
            } catch (SQLException exc2) {
                exc2.printStackTrace();
            }
        }
        return ret;
    }
    
    /**
     * Lettura data assegnata ad un compito
     * @param j Job
     * @return Data assegnata
     */
    public LocalDate getShiftDate(Job j) {
        PreparedStatement pst = null;
        
        String sql = "SELECT date FROM CookShift,Job WHERE CookShift.id=Job.shift and Job.id = ?";
        LocalDate ret = null;
        try {
            pst = this.connection.prepareStatement(sql);
            pst.setInt(1, jobObjects.get(j));
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {                
                ret = LocalDate.parse(rs.getString("date"));
            }
            pst.close();
        } catch (SQLException exc) {
            exc.printStackTrace();
        } finally {
            try {
                if (pst != null) pst.close();
            } catch (SQLException exc2) {
                exc2.printStackTrace();
            }
        }
        return ret;
    }
    
    /**
     * Rimozione assegnamento ad un compito
     * @param j Job
     */
    private void removeAssignment(Job j) {
        int iId = jobObjects.get(j);
        
        //Se cuoco non assegnato, cancello cookshift collegato
        if(j.getCook() == null) this.deleteCookShift(j);
       
        String sqlItems = "UPDATE Job SET shift=NULL, completed=0 WHERE id=?";
        PreparedStatement pstItems = null;
        try {
            pstItems = connection.prepareStatement(sqlItems);
            pstItems.setInt(1, iId);
            pstItems.executeUpdate();
        } catch (SQLException exc) {
            exc.printStackTrace();

        } finally {
            try {
                if (pstItems != null) pstItems.close();
            } catch (SQLException exc2) {
                exc2.printStackTrace();
            }

        }
    }
    
    /**
     * Cancellazione summarysheet, e tutti i compiti assegnati
     * @param e 
     */
    public void deleteSummarySheet(Event e) {
        int iId = summarySheetObjects.get(e.getSummarySheet());
        String sqlItems = "DELETE FROM Job WHERE SummarySheet=?";
        PreparedStatement pstItems = null;
        try {
            pstItems = connection.prepareStatement(sqlItems);
            pstItems.setInt(1, iId);
            pstItems.executeUpdate();
        } catch (SQLException exc) {
            exc.printStackTrace();

        } finally {
            try {
                if (pstItems != null) pstItems.close();
            } catch (SQLException exc2) {
                exc2.printStackTrace();
            }

        }
        sqlItems = "DELETE FROM SummarySheet WHERE id=?";
        pstItems = null;
        try {
            pstItems = connection.prepareStatement(sqlItems);
            pstItems.setInt(1, iId);
            pstItems.executeUpdate();
        } catch (SQLException exc) {
            exc.printStackTrace();

        } finally {
            try {
                if (pstItems != null) pstItems.close();
            } catch (SQLException exc2) {
                exc2.printStackTrace();
            }

        }
    }
    
    /**
     * Crea un nuovo CookShift per il Job assegnato senza cuoco
     * @param s Shift 
     * @param date Data
     * @return id cookshift creato
     */
    private int createCookShift(Job j, Shift s, LocalDate date)
    {
        // Se non ha selezionato un cuoco, creo un record all'interno di cookshift 
        // con data e shift quelli selezionati e cook NULL
        String sql = "INSERT INTO CookShift(shift, date) values (?, ?)";
        PreparedStatement pstmt = null;
        int idInsertedCookShift = -1;
        try {
            pstmt = this.connection.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
            pstmt.setInt(1, shiftObjects.get(j.getShift()));
            pstmt.setDate(2, java.sql.Date.valueOf(date));
            int affectedRows = pstmt.executeUpdate();
            

            if (affectedRows == 0) {
                throw new SQLException("Inserimento in cookshift fallito.");
            }

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    idInsertedCookShift = generatedKeys.getInt(1);
                }
                else {
                    throw new SQLException("Creating user failed, no ID obtained.");
                }
            }
        } catch (SQLException exc) {
            exc.printStackTrace();
        } finally {
            try {
                if (pstmt != null) pstmt.close();
            } catch (SQLException exc2) {
                    exc2.printStackTrace();
                }
            }
        return idInsertedCookShift;
    }
    
    /**
     * Cancella il cookshift del job senza cuoco assegnato
     * @param j Job
     */
    private void deleteCookShift(Job j) {
        try {
            String sql = "DELETE FROM CookShift WHERE id = (SELECT shift FROM Job WHERE id = " + jobObjects.get(j) + ")";
            PreparedStatement pstmt = this.connection.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
            pstmt.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(DataManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Modifica assegnamento di un cuoco ad un compito
     * @param e Evento
     * @param j Job da modificare 
     * @param cook Nuovo cuoco
     */
    public void changeAssignment(Event e, Job j, User cook) {
        PreparedStatement pst = null;
        String sql = "";
        LocalDate date = getShiftDate(j);
        if(j.getCook() == null) //Cancello cookshift vecchio
            deleteCookShift(j);
        if(cook == null)//Se il nuovo cuoco non è selezionato, creo cookshift
        {
            int idC = createCookShift(j, j.getShift(), date);
            sql = "UPDATE Job SET shift = ? WHERE id=?";
            try {
                pst = this.connection.prepareStatement(sql);
                pst.setInt(1, idC);
                pst.setInt(2, jobObjects.get(j));
                
                pst.executeUpdate();
            } catch (SQLException exc) {
                exc.printStackTrace();
            } finally {
                try {
                    if (pst != null) pst.close();
                } catch (SQLException exc2) {
                    exc2.printStackTrace();
                }
            }
        }
        else
        {
            sql = "UPDATE Job SET shift = (SELECT id FROM CookShift WHERE shift=? AND date=? AND cook=?) WHERE id=?";
            try {
                pst = this.connection.prepareStatement(sql);
                pst.setInt(1, shiftObjects.get(j.getShift()));
                pst.setString(2, date.toString());
                pst.setInt(3, userObjects.get(cook));
                pst.setInt(4, jobObjects.get(j));
                
                pst.executeUpdate();
            } catch (SQLException exc) {
                exc.printStackTrace();
            } finally {
                try {
                    if (pst != null) pst.close();
                } catch (SQLException exc2) {
                    exc2.printStackTrace();
                }
            }
        }
    }
}
