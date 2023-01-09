package me.atie.partialKeepinventory.impl.ModmenuGUI;

import me.atie.partialKeepinventory.impl.SettingsGUI;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class ButtonEntry<T> extends EntryImpl implements Entry {
    private final TextWidget nameWidget;
    private final ButtonWidget buttonWidget;

    private final Supplier<T> get;
    private final Consumer<T> set;
    private final Function<T, Text> toText;

    private final Function<T, T> nextVal;


    private void onPress(ButtonWidget b){
        T newVal = get.get();
        newVal = nextVal.apply(newVal);

        set.accept(newVal);

        b.setMessage(toText.apply(newVal) );
    }


    private ButtonEntry(TextRenderer textRenderer, Text name, Tooltip tooltip, Supplier<T> getter, Consumer<T> setter, Function<T, Text> toText, Function<T, T> nextVal, int y){
        super(y);
        get = getter;
        set = setter;

        this.toText = toText;
        this.nextVal = nextVal;

        int w = MinecraftClient.getInstance().getWindow().getScaledWidth();
        int nameWidth = textRenderer.getWidth(name);
        nameWidget = new TextWidget(SettingsGUI.sideMargin, y, nameWidth, SettingsGUI.widgetHeight, name, textRenderer);


        buttonWidget = ButtonWidget.builder( toText.apply( get.get()), this::onPress)
                .dimensions(w - SettingsGUI.buttonWidth - SettingsGUI.sideMargin, y, SettingsGUI.buttonWidth, SettingsGUI.widgetHeight)
                .build();
    }

    @Override
    public int updateY(int y){
        nameWidget.setY(y);
        buttonWidget.setY(y);
        return super.updateY(y);
    }


    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        if( !hidden ) {
            nameWidget.render(matrices, mouseX, mouseY, delta);
            buttonWidget.render(matrices, mouseX, mouseY, delta);
        }
    }

    public ButtonWidget getButtonWidget(){
        return this.buttonWidget;
    }

    public TextWidget getTextWidget() {
        return this.nameWidget;
    }


    public static class Builder<T> {
        private Supplier<T> get = null;
        private Consumer<T> set = null;
        private Function<T, Text> toText;

        private Function<T, T> nextVal = null;
        int y = 0;

        private Text name = null;
        private Tooltip tooltip = null;
        private TextRenderer textRenderer;

        public Builder(TextRenderer t) {
            toText = (x) -> Text.literal(x.toString());
            textRenderer = t;
        }


        public Builder<T> setY(int y){
            this.y = y;
            return this;
        }

        public Builder<T> setSetter(Consumer<T> setter) {
            set = setter;
            return this;
        }

        public Builder<T> setGetter(Supplier<T> getter) {
            get = getter;
            return this;
        }

        public Builder<T> toText(Function<T, Text> toText){
            this.toText = toText;
            return this;
        }

        public Builder<T> nextVal(Function<T, T> nextVal) {
            this.nextVal = nextVal;
            return this;
        }

        public Builder<T> setName(Text name) {
            this.name = name;
            return this;
        }


        public ButtonEntry<T> build() {
            return new ButtonEntry<T>(textRenderer, name, tooltip, get, set, toText, nextVal, y);
        }




    }

}
