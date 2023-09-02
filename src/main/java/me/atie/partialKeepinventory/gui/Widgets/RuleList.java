package me.atie.partialKeepinventory.gui.Widgets;

import me.atie.partialKeepinventory.rules.DropRule;
import me.atie.partialKeepinventory.rules.RuleGroup;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

public class RuleList extends AlwaysSelectedEntryListWidget<RuleList.RuleListEntry> {
    private RuleGroup group;

    public RuleList(MinecraftClient minecraftClient, int i, int j, int k, int l, int m, RuleGroup group) {
        super(minecraftClient, i, j, k, l, m);
        this.group = group;

        for( var rule: group.rules ){
            addEntry(new RuleListEntry(rule));
        }
    }

    public void addEntry(DropRule rule){
        addEntry(new RuleListEntry(rule));
    }

    public static class RuleListEntry extends AlwaysSelectedEntryListWidget.Entry<RuleListEntry> {
        private ButtonWidget upButton;
        private ButtonWidget downButton;
        private ButtonWidget deleteButton;
        private TextFieldWidget variableButton;
        private ButtonWidget comparisonButton;
        private TextFieldWidget valueButton;
        private DropRule rule;

        public RuleListEntry(DropRule rule){
            this.rule = rule;
        }



        @Override
        public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            upButton.render(context, mouseX, mouseY, tickDelta);
            downButton.render(context, mouseX, mouseY, tickDelta);
            deleteButton.render(context, mouseX, mouseY, tickDelta);
            variableButton.render(context, mouseX, mouseY, tickDelta);
            comparisonButton.render(context, mouseX, mouseY, tickDelta);
            valueButton.render(context, mouseX, mouseY, tickDelta);
        }

        @Override
        public Text getNarration() {
            return null;
        }
    }
}
