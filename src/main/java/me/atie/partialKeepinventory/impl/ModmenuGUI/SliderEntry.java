package me.atie.partialKeepinventory.impl.ModmenuGUI;

import me.atie.partialKeepinventory.impl.SettingsGUI;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class SliderEntry extends EntryImpl implements Entry{
    final private TextWidget nameWidget;
    final private SliderWidget sliderWidget;

    final private Supplier<Double> get;
    final private Consumer<Double> set;
    final private Function<Double, Text> toText;


    public SliderEntry(TextRenderer textRenderer, Text name, Supplier<Double> getter, Consumer<Double> setter, Function<Double, Text> toText, int yPos, float min, float max) {
        super(yPos);
        this.get = getter;
        this.set = setter;
        this.toText = toText;

        int w = MinecraftClient.getInstance().getWindow().getScaledWidth();

        int nameWidth = textRenderer.getWidth(name);
        nameWidget = new TextWidget(SettingsGUI.sideMargin, yPos, nameWidth, 20, name, textRenderer);

        double val = get.get();
        sliderWidget = new SliderWidget(w - SettingsGUI.sliderWidth - SettingsGUI.sideMargin, yPos, SettingsGUI.sliderWidth, 20, toText.apply(val), val) {
            @Override
            protected void updateMessage() {
                // value * (max - min) + min is to set the value between these 2 values
                this.setMessage( toText.apply( value * (max - min) + min ) );
            }

            @Override
            protected void applyValue() {
                set.accept(value * (max - min) + min );
            }
        };
    }

    public int updateY(int y){
        nameWidget.setY(y);
        sliderWidget.setY(y);
        return super.updateY(y);
    }

    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        if(!hidden) {
            this.nameWidget.render(matrices, mouseX, mouseY, delta);
            this.sliderWidget.render(matrices, mouseX, mouseY, delta);
        }
    }

    public SliderWidget getSliderWidget(){
        return this.sliderWidget;
    }
    public TextWidget getTextWidget() {return this.nameWidget;}

    public static class Builder {
        private Consumer<Double> set;
        private Supplier<Double> get;

        private Function<Double, Text> toText;


        private Text name = Text.literal("placeholder name");
        private TextRenderer textRenderer;

        private float min = 0.0f;
        private float max = 1.0f;
        private int yPos = 0;

        public Builder(TextRenderer textRenderer){
            this.textRenderer = textRenderer;
        }


        public Builder setMin(int min) {
            this.min = (float) min;
            return this;
        }
        public Builder setMax(int max) {
            this.max = (float) max;
            return this;
        }

        public Builder setMin(float min) {
            this.min = min;
            return this;
        }
        public Builder setMax(float max) {
            this.max = max;
            return this;
        }

        public Builder setLimits(int min, int max) {
            this.min = (float) min;
            this.max = (float) max;
            return this;
        }
        public Builder setLimits(float min, float max) {
            this.min = min;
            this.max = max;
            return this;
        }

        public Builder intGetter(Supplier<Integer> get) throws InstantiationException{
            this.get = () -> get.get().doubleValue();
            return this;
        }
        public Builder intSetter(Consumer<Integer> set) throws InstantiationException{
            this.set = (d) -> set.accept(d.intValue());
            return this;
        }

        public Builder floatGetter(Supplier<Double> get) throws InstantiationException{
            this.get = get;
            return this;
        }
        public Builder floatSetter(Consumer<Double> set) throws InstantiationException{
            this.set = set;
            return this;
        }

        public Builder toText(Function<Double, Text> toText) {
            this.toText = toText;
            return this;
        }
        public Builder setY(int y){
            yPos = y;
            return this;
        }

        public Builder setName(Text name) {
            this.name = name;
            return this;
        }


        public SliderEntry build() throws InstantiationException {
            if( set == null || get == null || toText == null ) {
                throw new InstantiationException("Getter, setter, toText weren't set");
            }
            return new SliderEntry(textRenderer, name, get, set, toText, yPos, min, max);
        }
    }



}
