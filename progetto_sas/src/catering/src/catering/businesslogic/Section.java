package catering.businesslogic;

import java.util.ArrayList;
import java.util.List;

public class Section implements Cloneable {
    private String name;
    private List<MenuItem> items;

    public Section(String name) {
        this.name = name;
        this.items = new ArrayList<>();
    }

    public void addItem(MenuItem it) {
        this.items.add(it);
    }

    public List<MenuItem> getItems() {
        List<MenuItem> ret = new ArrayList<>();
        ret.addAll(items);
        return ret;
    }

    public String toString() {
        return this.name;
    }

    public Section clone() {
        Section copia = new Section(name);
        for (MenuItem it: items) {
            copia.items.add(it.clone());
        }
        return copia;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean hasItem(MenuItem it) {
        return this.items.contains(it);
    }

    public int getItemsCount() {
        return this.items.size();
    }

    public void moveItem(MenuItem it, int pos) {
        items.remove(it);
        items.add(pos, it);
    }

    public void removeItem(MenuItem it) {
        items.remove(it);
    }

    public String getName() {
        return name;
    }

    public int getItemPosition(MenuItem it) {
        return items.indexOf(it);
    }
}
