package me.atie.partialKeepinventory.gui.Widgets;

import me.atie.partialKeepinventory.rules.RuleGroup;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

import java.util.List;

public class RuleGroupList extends AlwaysSelectedEntryListWidget<RuleGroupList.RuleGroupEntry> {
    public RuleGroupList(MinecraftClient minecraftClient, int i, int j, int k, int l, int m, List<RuleGroup> ruleGroups) {
        super(minecraftClient, i, j, k, l, m);

//        for( var group: ruleGroups ) {
//            this.addEntry(new RuleGroupEntry());
//        }
    }

    public static class RuleGroupEntry extends AlwaysSelectedEntryListWidget.Entry<RuleGroupEntry> {
        private ButtonWidget addButton;
        private RuleList rules;
        private RuleGroup group;

        public RuleGroupEntry(MinecraftClient minecraftClient, int i, int j, int k, int l, int m, RuleGroup group){
            this.rules = new RuleList(minecraftClient, i, j, k, l, m, group);

//            this.addButton = new ButtonWidget(0, 0, 40, 20, Text.translatable("pki.rulegroupentry.addbutton"),
//                    (buttonWidget) -> {
//                this.rules.addEntry(new DropRule(null, RuleComparison.Equal, null));
//            }, null);
        }


        @Override
        public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            rules.render(context, mouseX, mouseY, tickDelta);
        }


        @Override
        public Text getNarration() {
            return null;
        }
    }
}
