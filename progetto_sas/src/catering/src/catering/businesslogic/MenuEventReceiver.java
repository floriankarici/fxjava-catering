package catering.businesslogic;

public interface MenuEventReceiver {
    public void notifyMenuCreated(Menu m);
    public void notifySectionAdded(Menu m, Section s);
    public void notifyItemAdded(Menu m, Section s, MenuItem it);
    public void notifyMenuPublished(Menu m);
    public void notifyMenuDeleted(Menu m);
    public void notifySectionRemoved(Menu m, Section s);
    public void notifySectionNameChanged(Menu m, Section s);
    public void notifySectionsRearranged(Menu m);
    public void notifyItemsRearranged(Menu m, Section s);
    public void notifyItemsRearrangedInMenu(Menu m);
    public void notifyItemMoved(Menu m, Section oldS, Section newS, MenuItem it);
    public void notifyItemDescriptionChanged(Menu m, MenuItem it);
    public void notifyItemDeleted(Menu m, MenuItem it);
    public void notifyMenuTitleChanged(Menu m);
}
