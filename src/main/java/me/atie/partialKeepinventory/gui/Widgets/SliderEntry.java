package me.atie.partialKeepinventory.gui.Widgets;

import me.atie.partialKeepinventory.gui.ParentSettingsScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class SliderEntry extends Entry {
    final private TextWidget nameWidget;
    final private SliderWidget sliderWidget;
    final private TextRenderer textRenderer;
    final private Consumer<Double> set;


    public SliderEntry(TextRenderer textRenderer, Text name, Text tooltip, Supplier<Double> getter, Consumer<Double> setter, Function<Double, Text> toText, int yPos, float min, float max) {
        super(yPos);
        this.set = setter;
        this.textRenderer = textRenderer;

        int w = MinecraftClient.getInstance().getWindow().getScaledWidth();

        int nameWidth = textRenderer.getWidth(name);
        nameWidget = new TextWidget(ParentSettingsScreen.sideMargin, yPos, nameWidth, 20, name, textRenderer);
        if( tooltip != null) {
            nameWidget.setTooltip(Tooltip.of(tooltip));
        }

        double val = getter.get();
        sliderWidget = new SliderWidget(w - ParentSettingsScreen.sliderWidth - ParentSettingsScreen.sideMargin, yPos, ParentSettingsScreen.sliderWidth, 20, toText.apply(val), val) {
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

    @Override
    public int updateY(int y){
        nameWidget.setY(y);
        sliderWidget.setY(y);
        return super.updateY(y);
    }

    @Override
    public <T extends Element & Selectable> List<T> getSelectables(){
        return List.of( (T)getSliderWidget() );
    }


    @Override
    public void updateDimensions(int windowWidth) {
        int nameWidth = textRenderer.getWidth(nameWidget.getMessage());

        nameWidget.setWidth(nameWidth);
        nameWidget.setX(ParentSettingsScreen.sideMargin);

        sliderWidget.setWidth(ParentSettingsScreen.sliderWidth);
        sliderWidget.setX(windowWidth - ParentSettingsScreen.sliderWidth - ParentSettingsScreen.sideMargin);
    }

        public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        if(!hidden) {
            this.nameWidget.render(matrices, mouseX, mouseY, delta);
            this.sliderWidget.render(matrices, mouseX, mouseY, delta);
        }
    }

    @Override
    public void show(){
        super.show();
        sliderWidget.active = true;
    }

    @Override
    public void hide(){
        super.hide();
        sliderWidget.active = false;
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
        private Text tooltip = null;
        private final TextRenderer textRenderer;

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

        public Builder setTooltip(Text tooltipText){
            this.tooltip = tooltipText;
            return this;
        }


        public SliderEntry build() throws InstantiationException {
            if( set == null || get == null || toText == null ) {
                throw new InstantiationException("Getter, setter, toText weren't set");
            }
            return new SliderEntry(textRenderer, name, tooltip, get, set, toText, yPos, min, max);
        }
    }



}
