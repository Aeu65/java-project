package model;

import element.Elem;
import java.util.ArrayList;
import java.util.List;

public class Category implements TreeElem {

    private final String name;
    private final List<TreeElem> children = new ArrayList<>();

    private Category parent;

    public Category(Elem e, Category parent) {
        this.name = e.name;
        this.parent = parent;

        for (Elem f : e.subElems) {
            if (f.subElems != null) { // f is a category
                this.children.add(new Category(f, this));
            } else {
                this.children.add(new Question(f, this));
            }
        }
    }

    @Override
    public String toString() {
        return this.name;
    }

    public List<TreeElem> getChildren() {
        return children;
    }

    public void removeChild(Question q) {
        children.remove(q);
    }

    public void addChild(Question q) {
        children.add(q);
    }
}
