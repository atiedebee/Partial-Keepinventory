package me.atie.partialKeepinventory.gui.Widgets;

import me.atie.partialKeepinventory.gui.ParentSettingsScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.util.math.MatrixStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

@Environment(EnvType.CLIENT)
public class EntryList extends Entry {
    protected ArrayList<Entry> children = new ArrayList<>();

    public EntryList(int y) {
        super(y);
        y = this.updateY(y);
        this.height = y - yPos;
    }

    public void addChild(Entry child){
        children.add(child);
        height += child.getHeight();
    }

    public void addChildren(Collection<Entry> collection) {
        children.addAll(collection);
        height = 0;
        for( var child: children ){
            height += child.getHeight() + ParentSettingsScreen.vertOptionMargin;
        }
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

        height = 0;
        while( it.hasNext() ) {
            Entry child = it.next();
            height += child.getHeight() + ParentSettingsScreen.vertOptionMargin;
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
    public int getHeight() {
        return height;
    }

    @Override
    public void updateDimensions(final int windowWidth) {
        children.forEach(c -> c.updateDimensions(windowWidth));
    }

    @Override
    public void show(){
        super.show();
        for(var child: children){
            child.show();
        }
    }

    @Override
    public void hide(){
        super.hide();
        for( var child: children ){
            child.hide();
        }
    }
}
