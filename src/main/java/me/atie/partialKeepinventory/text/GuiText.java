package me.atie.partialKeepinventory.text;

import me.atie.partialKeepinventory.PartialKeepInventory;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class GuiText {
    private final static String prefix = PartialKeepInventory.getID();


    public static class screenHeader{
        public final static Text mod_enabled_button_true = Text.translatable(prefix + ".gui.modenabled.true");
        public final static Text mod_enabled_button_false = Text.translatable(prefix + ".gui.modenabled.false");
        public final static Text mod_enabled_tooltip = Text.translatable(prefix + ".gui.modenabled.tooltip");
        public final static Text close_button = Text.translatable(prefix + ".gui.menu.close");
        public final static Text inv_button = Text.translatable(prefix + ".gui.menu.inventory");
        public final static Text xp_button = Text.translatable(prefix + ".gui.menu.experience");
    }

    /// error screen ///
    public static class errorScreen{
        public final static Text no_server = Text.translatable(prefix + ".gui.error.server_req");
        public final static Text no_config = Text.translatable(prefix + ".gui.error.config_null");
        public final static Text invalid_config = Text.translatable(prefix + ".gui.error.config_invalid");
    }


    /// inv screen ///
    public static class invScreen{
        public final static Text name = Text.translatable(PartialKeepInventory.getID() + ".gui.screen.inv");
        public final static Text header = Text.translatable(PartialKeepInventory.getID() + ".gui.header.inv");
        public final static Text slider_static = Text.translatable(prefix + ".gui.slider.static");
        public final static Text slider_common = Text.translatable(prefix + ".gui.slider.common");
        public final static Text slider_uncommon = Text.translatable(prefix + ".gui.slider.uncommon");
        public final static Text slider_rare = Text.translatable(prefix + ".gui.slider.rare");
        public final static Text slider_epic = Text.translatable(prefix + ".gui.slider.epic");
        public final static Text text_invMode = Text.translatable(prefix + ".gui.text.partialinvmode");
        public final static Text tooltip_common = Text.translatable(prefix + ".gui.tooltip.common");
        public final static Text tooltip_epic = Text.translatable(prefix + ".gui.tooltip.epic");
        public final static Text tooltip_invMode = Text.translatable(prefix + ".gui.tooltip.partialinvmode");
        public final static Text tooltip_rare = Text.translatable(prefix + ".gui.tooltip.rare");
        public final static Text tooltip_static = Text.translatable(prefix + ".gui.tooltip.static");
        public final static Text tooltip_uncommon = Text.translatable(prefix + ".gui.tooltip.uncommon");
    }


    /// custom inv screen ///
    public static class customInvScreen{
        public final static Text text_header = Text.translatable(prefix + ".gui.text.customheader");
        public final static Text saveExpr_button_name = Text.translatable(prefix + ".gui.inv.button.saveexpression");
        public final static Text saveExpr_tooltip_base = Text.translatable(prefix + ".gui.inv.button.saveexpression.tooltip_base");
        public final static Text saveExpr_tooltip_failure = Text.translatable(prefix + ".gui.inv.button.saveexpression.tooltip_failure");
        public final static Text saveExpr_tooltip_success = Text.translatable(prefix + ".gui.inv.button.saveexpression.tooltip_success");
        public final static Text invExpr_textfield_name = Text.translatable(prefix + ".gui.textfield.invexpression");
        public final static Text invExpr_textfield_tooltip = Text.translatable(prefix + ".gui.tooltip.invexpression");
        public final static Text invExpr_list = Text.translatable(prefix + ".gui.list.invexpression");
        public final static Text invExpr_text = Text.translatable(prefix + ".gui.text.invexpression");
    }


    public static class customXpScreen{
        public final static Text header = Text.translatable(prefix + ".gui.text.customxpheader");
        public final static Text saveExpr_name = Text.translatable(prefix + ".gui.inv.button.saveexpression");
        public final static Text saveExpr_tooltip_base = Text.translatable(prefix + ".gui.inv.button.saveexpression.tooltip_base");
        public final static Text saveExpr_tooltip_success = Text.translatable(prefix + ".gui.inv.button.saveexpression.tooltip_success");
        public final static Text saveExpr_tooltip_failure = Text.translatable(prefix + ".gui.inv.button.saveexpression.tooltip_failure");
        public final static Text xpdrop_textfield = Text.translatable(prefix + ".gui.textfield.xpdrop-invExpression");
        public final static Text xpdrop_tooltip = Text.translatable(prefix + ".gui.tooltip.xpdrop-invExpression");
        public final static Text xploss_textfield = Text.translatable(prefix + ".gui.textfield.xploss-invExpression");
        public final static Text xploss_tooltip = Text.translatable(prefix + ".gui.tooltip.xploss-invExpression");
        public final static Text xpExpr_list = Text.translatable(prefix + ".gui.list.xpexpression");
        public final static Text xpExpr_guide = Text.translatable(prefix + ".gui.text.xpexpression-guide");
    }

}
