package me.atie.partialKeepinventory.impl.ModmenuGUI;

import net.minecraft.client.util.math.MatrixStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class EntryList extends EntryImpl implements Entry {
    protected ArrayList<EntryImpl> children = new ArrayList<>();


    public EntryList(int y) {
        super(y);
    }

    public void addChild(EntryImpl child){
        children.add(child);
    }

    public void addChildren(Collection<EntryImpl> collection) {
        children.addAll(collection);
    }

    public List<EntryImpl> getChildren(){
        return this.children;
    }

    @Override
    public int updateY(int y) {
        this.yPos = y;
        Iterator<EntryImpl> it = children.stream()
                .filter(e -> !e.hidden)
                .iterator();

        while( it.hasNext() ) {
            EntryImpl child = it.next();
            y = child.updateY(y);
        }
        return y;
    }

    public int getY() {
        return yPos;
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        if( !hidden ) {
            for (var child : children) {
                ((Entry) child).render(matrices, mouseX, mouseY, delta);
            }
        }
    }

}
