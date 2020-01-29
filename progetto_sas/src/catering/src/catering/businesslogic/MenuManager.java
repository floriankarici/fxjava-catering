package catering.businesslogic;

import java.util.ArrayList;
import java.util.List;

public class MenuManager {

    private List<Menu> allMenus;
    private Menu currentMenu;
    private List<MenuEventReceiver> receivers;

    public MenuManager() {
        receivers = new ArrayList<>();
        receivers.add(new BaseEventReceiver() {
            @Override
            public void notifyMenuCreated(Menu m) {
                allMenus.add(m);
            }

            @Override
            public void notifyMenuDeleted(Menu m) {
                allMenus.remove(m);
            }
        });
    };

    // Nota: nell'inizializzazione non carichiamo l'elenco di ricette
    // perché lo faremo "onDemand", ossia se viene richiesto da qualche altro oggetto
    // L'idea è evitare di caricare tutto se non serve.
    public void initialize() {};

    // Questo metodo non è stato descritto nel Class Diagram perché
    // l'UC che abbiamo analizzato partiva dal presupposto che l'utente
    // avesse già davanti la lista dei menu disponibili. Quindi questa
    // parte va aggiunta direttamente nel codice.
    public List<Menu> getAllMenus() {
        if (allMenus == null) {
            allMenus = new ArrayList<>();
            allMenus.addAll(CateringAppManager.dataManager.loadMenus());
        }

        // Restituisce una copia della propria lista per impedire ad altri oggetti di modificarne
        // il contenuto
        List<Menu> ret = new ArrayList<>();
        ret.addAll(allMenus);
        return ret;

    }

    public Menu createMenu(String title) {
        User u = CateringAppManager.userManager.getCurrentUser();
        if (!u.isChef()) throw new UseCaseLogicException("Solo gli chef possono creare un menu");
        else {
            currentMenu = new Menu(u, title);
            for (MenuEventReceiver r: receivers) {
                r.notifyMenuCreated(currentMenu);
            }
            return currentMenu;
        }
    }

    public Menu getCurrentMenu() {
        return currentMenu;
    }

    public Menu chooseMenu(Menu m) {
        User u = CateringAppManager.userManager.getCurrentUser();
        if (!u.isChef()) throw new UseCaseLogicException("Solo gli chef possono editare un menu");
        if (m.isInUse()) throw new MenuException("Il menu non può essere modificato perché è in uso");
        if (!m.getOwner().equals(u)) throw new MenuException("Solo il proprietario " + u.toString() + " può modificare il menu");
        this.currentMenu = m;
        return this.currentMenu;
    }

    public Section defineSection(String name) {
        if (currentMenu == null) throw new UseCaseLogicException("non è stato specificato il menu su cui si sta lavorando");
        Section s = this.currentMenu.addSection(name);

        for (MenuEventReceiver r: receivers) {
            r.notifySectionAdded(currentMenu, s);
        }
        return s;

    }

    public MenuItem insertItem(Recipe rec, Section sec, String desc) {
        if (this.currentMenu == null) throw new UseCaseLogicException("non è stato specificato il menu su cui si sta lavorando");
        if (!rec.isDish()) throw new UseCaseLogicException(rec.toString() + " non è la ricetta di un piatto finito.");

        if (sec != null && !currentMenu.hasSection(sec)) throw new UseCaseLogicException("la sezione " + sec.toString()
                + " non appartiene al menu corrente.");
        MenuItem it = null;
        if (desc == null || desc.trim().length() == 0) it = currentMenu.addItem(rec, sec);
        else it = currentMenu.addItem(rec, sec, desc);

        if (it != null) {
            for (MenuEventReceiver r: receivers) {
                r.notifyItemAdded(currentMenu, sec, it);
            }
        }
        return it;

    }

    public void publish() {
        if (currentMenu == null) throw new UseCaseLogicException("non è stato specificato il menu su cui si sta lavorando");
        this.currentMenu.setPublished(true);
        for (MenuEventReceiver r: receivers) {
            r.notifyMenuPublished(currentMenu);
        }
    }

    public Menu copyMenu(Menu m) {
        User u = CateringAppManager.userManager.getCurrentUser();
        if (!u.isChef()) throw new UseCaseLogicException("Solo gli chef possono creare un menu");
        else {
            currentMenu = m.clone();
            currentMenu.setPublished(false);
            currentMenu.setOwner(u);
            for (MenuEventReceiver r: receivers) {
                r.notifyMenuCreated(currentMenu);
            }
            return currentMenu;
        }
    }

    public void deleteMenu(Menu m) {
        User u = CateringAppManager.userManager.getCurrentUser();
        if (!u.isChef()) throw new UseCaseLogicException("Solo gli chef possono eliminare un menu");
        if (m.isInUse()) throw new MenuException("Il menu non può essere eliminato perché è in uso");
        if (!m.getOwner().equals(u)) throw new MenuException("Solo il proprietario " + u.toString() + " può eliminare il menu");
        for (MenuEventReceiver r: receivers) {
            r.notifyMenuDeleted(m);
        }
    }

    public void deleteSectionWithItems(Section sec) {
        if (currentMenu == null) throw new UseCaseLogicException("non è stato specificato il menu su cui si sta lavorando");
        if (!currentMenu.hasSection(sec)) throw new UseCaseLogicException("la sezione " + sec.toString() + " non appartiene al menu corrente");
        currentMenu.removeSection(sec, true);
        for (MenuEventReceiver r: receivers) {
            r.notifySectionRemoved(currentMenu, sec);
        }

    }

    public void deleteSection(Section sec) {
        if (currentMenu == null) throw new UseCaseLogicException("non è stato specificato il menu su cui si sta lavorando");
        if (!currentMenu.hasSection(sec)) throw new UseCaseLogicException("la sezione " + sec.toString() + " non appartiene al menu corrente");
        List<MenuItem> its = sec.getItems();
        currentMenu.removeSection(sec, false);
        for (MenuEventReceiver r: receivers) {
            for (MenuItem it: its) {
                r.notifyItemMoved(currentMenu, sec, null, it);
            }
            r.notifySectionRemoved(currentMenu, sec);
        }
    }

    public void changeSectionName(Section sec, String name) {
        if (currentMenu == null) throw new UseCaseLogicException("non è stato specificato il menu su cui si sta lavorando");
        if (!currentMenu.hasSection(sec)) throw new UseCaseLogicException("la sezione " + sec.toString() + " non appartiene al menu corrente");
        sec.setName(name);
        for (MenuEventReceiver r: receivers) {
            r.notifySectionNameChanged(currentMenu, sec);
        }
    }

    public void moveSection(Section sec, int pos) {
        if (currentMenu == null) throw new UseCaseLogicException("non è stato specificato il menu su cui si sta lavorando");
        if (!currentMenu.hasSection(sec)) throw new UseCaseLogicException("la sezione " + sec.toString() + " non appartiene al menu corrente");

        if (pos >= 0 && pos < currentMenu.getSectionCount()) {
            this.currentMenu.moveSection(sec, pos);
            for (MenuEventReceiver r: receivers) {
                r.notifySectionsRearranged(currentMenu);
            }
        }
    }

    public void moveItemsWithoutSection(MenuItem it, int pos) {
        if (currentMenu == null) throw new UseCaseLogicException("non è stato specificato il menu su cui si sta lavorando");
        if (!currentMenu.hasItemWithoutSection(it)) throw new UseCaseLogicException("la voce " + it.toString() + " non " +
                "appartiene direttamente al menu corrente");
        if (pos >= 0 && pos < currentMenu.getItemsWithoutSectionCount()) {
            this.currentMenu.moveItemWithoutSection(it, pos);
            for (MenuEventReceiver r: receivers) {
                r.notifyItemsRearrangedInMenu(currentMenu);
            }
        }

    }

    public void moveItemsInSection(Section sec, MenuItem it, int pos) {
        if (currentMenu == null)
            throw new UseCaseLogicException("non è stato specificato il menu su cui si sta lavorando");
        if (!sec.hasItem(it)) throw new UseCaseLogicException("la voce " + it.toString() + " non " +
                "appartiene alla sezione " + sec.toString());
        if (pos >= 0 && pos < sec.getItemsCount()) {
            sec.moveItem(it, pos);
            for (MenuEventReceiver r: receivers) {
                r.notifyItemsRearranged(currentMenu, sec);
            }
        }
    }

    public void assignItemToSection(MenuItem it, Section sec) {
        if (currentMenu == null)
            throw new UseCaseLogicException("non è stato specificato il menu su cui si sta lavorando");
        if (sec != null && !currentMenu.hasSection(sec)) throw new UseCaseLogicException("la sezione " + sec.toString() +
                " non appartiene al menu corrente");
        Section oldsec = currentMenu.getSection(it);
        if (!currentMenu.hasItemWithoutSection(it) && oldsec==null) throw new UseCaseLogicException("la voce " + it.toString() +
                " non appartiene al menu corrente");
        currentMenu.changeSection(it, sec);

        for (MenuEventReceiver r: receivers) {
            r.notifyItemMoved(currentMenu, oldsec, sec, it);
        }
    }

    public void changeItemDescription(MenuItem it, String newDesc) {
        if (currentMenu == null) throw new UseCaseLogicException("non è stato specificato il menu su cui si sta lavorando");
        if (!currentMenu.hasItem(it)) throw new UseCaseLogicException("la voce " + it.toString() + " non " +
                "appartiene al menu corrente");

        it.setDescription(newDesc);
        for (MenuEventReceiver r: receivers) {
            r.notifyItemDescriptionChanged(currentMenu, it);
        }
    }

    public void deleteItem(MenuItem it) {
        if (currentMenu == null) throw new UseCaseLogicException("non è stato specificato il menu su cui si sta lavorando");
        if (!currentMenu.hasItem(it)) throw new UseCaseLogicException("la voce " + it.toString() + " non " +
                "appartiene al menu corrente");
        currentMenu.removeItem(it);
        for (MenuEventReceiver r: receivers) {
            r.notifyItemDeleted(currentMenu, it);
        }
    }

    public void setMenuTitle(String title) {
        if (currentMenu == null) throw new UseCaseLogicException("non è stato specificato il menu su cui si sta lavorando");
        currentMenu.setTitle(title);
        for (MenuEventReceiver r: receivers) {
            r.notifyMenuTitleChanged(currentMenu);
        }
    }

    public void addReceiver(MenuEventReceiver rec) {
        this.receivers.add(rec);
    }

    public void removeReceiver(MenuEventReceiver rec) {
        this.receivers.remove(rec);
    }

}
