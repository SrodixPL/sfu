package net.srodix.client.ui.widgets;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.srodix.client.ui.UiRender;
import net.srodix.client.ui.sdf.SdfRectStyle;
import net.srodix.client.ui.sdf.SdfRenderer;
import net.srodix.client.ui.theme.GuiTheme;

import java.util.List;
import java.util.function.Consumer;

public class CycleWidget extends AbstractWidget {
    private static final int BOX_WIDTH = 80;
    private static final int BOX_HEIGHT = 22;
    private static final int ROW_HEIGHT = 34;
    private static final int TEXT_GAP = 2;

    private final GuiTheme theme;
    private final Component label;
    private final Component description;
    private final List<String> options;
    private final Consumer<String> onValueChanged;
    private int selectedIndex;
    private float hoverProgress;
    private float textSwapProgress = 1.0F;
    private String previousValue;

    public CycleWidget(int x, int y, int width, GuiTheme theme, Component label, Component description, List<String> options, int selectedIndex, Consumer<String> onValueChanged) {
        super(x, y, width, ROW_HEIGHT);
        this.theme = theme;
        this.label = label;
        this.description = description;
        this.options = options;
        this.selectedIndex = Math.max(0, Math.min(selectedIndex, options.size() - 1));
        this.onValueChanged = onValueChanged;
        this.previousValue = getSelectedValue();
    }

    public String getSelectedValue() {
        return this.options.get(this.selectedIndex);
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        int boxX = this.x;
        float rowCenterY = this.y + (this.height / 2.0F);
        int boxY = Math.round(rowCenterY - (BOX_HEIGHT / 2.0F));
        int labelX = boxX + BOX_WIDTH + 14;
        int controlCenterY = boxY + (BOX_HEIGHT / 2);
        float textScale = 1.0F + (this.hoverProgress * 0.04F);
        int textBlockHeight = this.description != null ? UiRender.fontHeight() + TEXT_GAP + UiRender.fontHeight() : UiRender.fontHeight();
        int labelY = Math.round(rowCenterY - (textBlockHeight / 2.0F)) + 1;
        int descriptionY = labelY + UiRender.fontHeight() + TEXT_GAP;

        boolean hovered = UiRender.contains(boxX, boxY, BOX_WIDTH, BOX_HEIGHT, mouseX, mouseY);
        this.hoverProgress = UiRender.animate(this.hoverProgress, hovered ? 1.0F : 0.0F, 0.22F);
        this.textSwapProgress = UiRender.animate(this.textSwapProgress, 1.0F, 0.18F);
        int fill = UiRender.lerpColor(this.theme.buttonColor(), this.theme.buttonHoverColor(), this.hoverProgress);
        
        SdfRenderer.drawRoundedRect(guiGraphics, boxX, boxY, BOX_WIDTH, BOX_HEIGHT, SdfRectStyle.bordered(fill, this.theme.buttonBorderColor(), 6.0F, 1.0F));
        
        String text = getSelectedValue();
        float currentCenterX = boxX + (BOX_WIDTH / 2.0F) + ((1.0F - this.textSwapProgress) * 10.0F);
        float currentAlpha = Math.min(1.0F, this.textSwapProgress + 0.15F);
        UiRender.scaledCenteredText(guiGraphics, text, currentCenterX, controlCenterY, textScale, UiRender.withAlpha(this.theme.titleColor(), Math.round(255 * currentAlpha)), false);

        if (this.textSwapProgress < 0.99F && this.previousValue != null && !this.previousValue.equals(text)) {
            float previousCenterX = boxX + (BOX_WIDTH / 2.0F) - (this.textSwapProgress * 10.0F);
            float previousAlpha = Math.max(0.0F, 1.0F - this.textSwapProgress);
            UiRender.scaledCenteredText(guiGraphics, this.previousValue, previousCenterX, controlCenterY, 1.0F, UiRender.withAlpha(this.theme.titleColor(), Math.round(255 * previousAlpha)), false);
        }

        UiRender.text(guiGraphics, this.label, labelX, labelY, this.theme.titleColor(), true);
        if (this.description != null) {
            UiRender.text(guiGraphics, this.description, labelX, descriptionY, this.theme.mutedTextColor(), false);
        }
    }

    @Override
    protected boolean onClick(MouseButtonEvent mouseButtonEvent, boolean bl) {
        if (mouseButtonEvent.button() != 0 || this.options.isEmpty()) {
            return false;
        }

        float rowCenterY = this.y + (this.height / 2.0F);
        int boxY = Math.round(rowCenterY - (BOX_HEIGHT / 2.0F));
        if (UiRender.contains(this.x, boxY, BOX_WIDTH, BOX_HEIGHT, mouseButtonEvent.x(), mouseButtonEvent.y())) {
            this.previousValue = getSelectedValue();
            this.selectedIndex = (this.selectedIndex + 1) % this.options.size();
            this.textSwapProgress = 0.0F;
            this.onValueChanged.accept(getSelectedValue());
            return true;
        }
        
        return false;
    }
}
