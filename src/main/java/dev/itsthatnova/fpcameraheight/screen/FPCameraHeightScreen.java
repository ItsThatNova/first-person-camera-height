package dev.itsthatnova.fpcameraheight.screen;

import dev.itsthatnova.fpcameraheight.config.FPCameraHeightConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;

import java.text.DecimalFormat;

public class FPCameraHeightScreen extends Screen {

    private static final DecimalFormat FORMAT = new DecimalFormat("0.00");

    private final Screen parent;

    // Working values - only committed on Done
    private boolean workingEnabled;
    private float workingOffset;
    private boolean workingDebugLogging;

    private ButtonWidget enabledButton;
    private ButtonWidget debugButton;
    private OffsetSlider offsetSlider;

    public FPCameraHeightScreen(Screen parent) {
        super(Text.translatable("fpcameraheight.config.title"));
        this.parent = parent;
        this.workingEnabled = FPCameraHeightConfig.isEnabled();
        this.workingOffset = FPCameraHeightConfig.getOffset();
        this.workingDebugLogging = FPCameraHeightConfig.isDebugLogging();
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int startY = this.height / 2 - 60;

        // Enabled toggle button
        enabledButton = ButtonWidget.builder(
            getEnabledText(),
            button -> {
                workingEnabled = !workingEnabled;
                button.setMessage(getEnabledText());
            }
        ).dimensions(centerX - 100, startY, 200, 20).build();
        addDrawableChild(enabledButton);

        // Offset slider
        offsetSlider = new OffsetSlider(centerX - 100, startY + 30, 200, 20, workingOffset);
        addDrawableChild(offsetSlider);

        // Reset button
        addDrawableChild(ButtonWidget.builder(
            Text.translatable("fpcameraheight.config.reset"),
            button -> {
                workingOffset = FPCameraHeightConfig.DEFAULT_OFFSET;
                offsetSlider.setOffsetValue(workingOffset);
            }
        ).dimensions(centerX - 100, startY + 60, 200, 20).build());

        // Debug logging toggle
        debugButton = ButtonWidget.builder(
            getDebugText(),
            button -> {
                workingDebugLogging = !workingDebugLogging;
                button.setMessage(getDebugText());
            }
        ).dimensions(centerX - 100, startY + 90, 200, 20).build();
        addDrawableChild(debugButton);

        // Done button
        addDrawableChild(ButtonWidget.builder(
            Text.translatable("fpcameraheight.config.done"),
            button -> {
                FPCameraHeightConfig.setEnabled(workingEnabled);
                FPCameraHeightConfig.setOffset(workingOffset);
                FPCameraHeightConfig.setDebugLogging(workingDebugLogging);
                MinecraftClient.getInstance().setScreen(parent);
            }
        ).dimensions(centerX - 102, startY + 130, 100, 20).build());

        // Cancel button
        addDrawableChild(ButtonWidget.builder(
            Text.translatable("fpcameraheight.config.cancel"),
            button -> MinecraftClient.getInstance().setScreen(parent)
        ).dimensions(centerX + 2, startY + 130, 100, 20).build());
    }

    private Text getEnabledText() {
        return Text.translatable("fpcameraheight.config.enabled")
            .append(": ")
            .append(workingEnabled ? ScreenTexts.ON : ScreenTexts.OFF);
    }

    private Text getDebugText() {
        return Text.translatable("fpcameraheight.config.debug")
            .append(": ")
            .append(workingDebugLogging ? ScreenTexts.ON : ScreenTexts.OFF);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(
            textRenderer,
            title,
            this.width / 2,
            this.height / 2 - 90,
            0xFFFFFF
        );
    }

    @Override
    public void close() {
        MinecraftClient.getInstance().setScreen(parent);
    }

    // Inner slider class
    private class OffsetSlider extends SliderWidget {

        public OffsetSlider(int x, int y, int width, int height, float initialValue) {
            super(x, y, width, height,
                Text.translatable("fpcameraheight.config.offset.value",
                    FORMAT.format(initialValue)),
                valueToSlider(initialValue));
        }

        @Override
        protected void updateMessage() {
            float current = sliderToValue();
            setMessage(Text.translatable("fpcameraheight.config.offset.value",
                FORMAT.format(current)));
        }

        @Override
        protected void applyValue() {
            workingOffset = sliderToValue();
        }

        private float sliderToValue() {
            float range = FPCameraHeightConfig.MAX_OFFSET - FPCameraHeightConfig.MIN_OFFSET;
            float raw = (float) this.value * range + FPCameraHeightConfig.MIN_OFFSET;
            // Round to nearest 0.05
            return Math.round(raw / 0.05f) * 0.05f;
        }

        private static double valueToSlider(float offset) {
            float range = FPCameraHeightConfig.MAX_OFFSET - FPCameraHeightConfig.MIN_OFFSET;
            return (offset - FPCameraHeightConfig.MIN_OFFSET) / range;
        }

        public void setOffsetValue(float offset) {
            this.value = valueToSlider(offset);
            workingOffset = offset;
            updateMessage();
        }
    }
}
