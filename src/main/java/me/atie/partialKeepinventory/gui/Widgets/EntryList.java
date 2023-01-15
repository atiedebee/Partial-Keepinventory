package me.atie.partialKeepinventory.gui.Widgets;

import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.util.math.MatrixStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class EntryList extends Entry {
    protected ArrayList<Entry> children = new ArrayList<>();


    public EntryList(int y) {
        super(y);
    }

    public void addChild(Entry child){
        children.add(child);
    }

    public void addChildren(Collection<Entry> collection) {
        children.addAll(collection);
    }

    public List<Entry> getChildren(){
        return this.children;
    }

    @Override
    public int updateY(int y) {
        this.yPos = y;
        Iterator<Entry> it = children.stream()
                .filter(e -> !e.hidden)
                .iterator();

        while( it.hasNext() ) {
            Entry child = it.next();
            y = child.updateY(y);
        }
        return y;
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        if( !hidden ) {
            for (var child : children) {
                child.render(matrices, mouseX, mouseY, delta);
            }
        }
    }

    @Override
    public <T extends Element & Selectable> List<T> getSelectables(){
        List<T> selectables = new ArrayList<>(children.size());
        for( var e: children ){
            selectables.addAll(e.getSelectables());
        }

        return selectables;
    }

    @Override
    public void updateDimensions(final int windowWidth) {
        children.forEach(c -> c.updateDimensions(windowWidth));
    }

}
