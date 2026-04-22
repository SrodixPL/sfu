package net.srodix.client.ui.widgets;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.srodix.client.ui.UiRender;
import net.srodix.client.ui.sdf.SdfRectStyle;
import net.srodix.client.ui.sdf.SdfRenderer;
import net.srodix.client.ui.theme.GuiTheme;

import java.util.function.Consumer;

public class ToggleSwitch extends AbstractWidget {
    private static final int TOGGLE_WIDTH = 46;
    private static final int TOGGLE_HEIGHT = 22;
    private static final int ROW_HEIGHT = 34;
    private static final int TEXT_GAP = 2;

    private final GuiTheme theme;
    private final Component label;
    private final Component description;
    private final Consumer<Boolean> onToggle;
    private boolean value;
    private float toggleProgress;
    private float hoverProgress;

    public ToggleSwitch(int x, int y, int width, GuiTheme theme, Component label, Component description, boolean value, Consumer<Boolean> onToggle) {
        super(x, y, width, ROW_HEIGHT);
        this.theme = theme;
        this.label = label;
        this.description = description;
        this.value = value;
        this.onToggle = onToggle;
        this.toggleProgress = value ? 1.0F : 0.0F;
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        int toggleX = this.x;
        float rowCenterY = this.y + (this.height / 2.0F);
        int toggleY = Math.round(rowCenterY - (TOGGLE_HEIGHT / 2.0F));
        int labelX = toggleX + TOGGLE_WIDTH + 14;
        int textBlockHeight = UiRender.fontHeight() + TEXT_GAP + UiRender.fontHeight();
        int labelY = Math.round(rowCenterY - (textBlockHeight / 2.0F)) + 1;
        int descriptionY = labelY + UiRender.fontHeight() + TEXT_GAP;

        boolean toggleHovered = UiRender.contains(toggleX, toggleY, TOGGLE_WIDTH, TOGGLE_HEIGHT, mouseX, mouseY);
        this.toggleProgress = UiRender.animate(this.toggleProgress, this.value ? 1.0F : 0.0F, 0.18F);
        this.hoverProgress = UiRender.animate(this.hoverProgress, toggleHovered ? 1.0F : 0.0F, 0.22F);
        int baseColor = UiRender.lerpColor(this.theme.toggleOffColor(), this.theme.toggleOnColor(), this.toggleProgress);
        int switchColor = UiRender.lerpColor(baseColor, UiRender.multiplyColor(baseColor, 1.08F), this.hoverProgress);

        SdfRenderer.drawRoundedRect(guiGraphics, toggleX, toggleY, TOGGLE_WIDTH, TOGGLE_HEIGHT, SdfRectStyle.bordered(switchColor, this.theme.toggleBorderColor(), TOGGLE_HEIGHT / 2.0F, 1.0F));
        int thumbRadius = (TOGGLE_HEIGHT - 5) / 2;
        int thumbCenterX = Math.round((toggleX + thumbRadius + 2) + ((TOGGLE_WIDTH - ((thumbRadius + 2) * 2)) * this.toggleProgress));
        int thumbCenterY = toggleY + (TOGGLE_HEIGHT / 2);
        SdfRenderer.drawCircle(guiGraphics, thumbCenterX, thumbCenterY, thumbRadius, this.theme.toggleThumbColor(), 0x40FFFFFF, 1.0F);

        UiRender.text(guiGraphics, this.label, labelX, labelY, this.theme.titleColor(), true);
        UiRender.text(guiGraphics, this.description, labelX, descriptionY, this.theme.mutedTextColor(), false);
    }

    @Override
    protected boolean onClick(MouseButtonEvent mouseButtonEvent, boolean bl) {
        if (mouseButtonEvent.button() != 0) {
            return false;
        }

        float rowCenterY = this.y + (this.height / 2.0F);
        int toggleY = Math.round(rowCenterY - (TOGGLE_HEIGHT / 2.0F));
        if (UiRender.contains(this.x, toggleY, TOGGLE_WIDTH, TOGGLE_HEIGHT, mouseButtonEvent.x(), mouseButtonEvent.y())) {
            this.value = !this.value;
            this.onToggle.accept(this.value);
            return true;
        }

        return false;
    }
}
