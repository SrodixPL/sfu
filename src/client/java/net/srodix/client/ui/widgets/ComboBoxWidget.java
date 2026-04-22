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

public class ComboBoxWidget extends AbstractWidget {
    private static final int BOX_WIDTH = 110;
    private static final int BOX_HEIGHT = 22;
    private static final int ROW_HEIGHT = 34;
    private static final int TEXT_GAP = 2;
    private static final int OPTION_HEIGHT = 20;
    private static final int OPTION_GAP = 2;
    private static final int DROPDOWN_PADDING = 4;

    private final GuiTheme theme;
    private final Component label;
    private final Component description;
    private final List<String> options;
    private final Consumer<String> onValueChanged;
    private String value;
    private boolean expanded;
    private float hoverProgress;

    public ComboBoxWidget(int x, int y, int width, GuiTheme theme, Component label, Component description, List<String> options, String value, Consumer<String> onValueChanged) {
        super(x, y, width, ROW_HEIGHT);
        this.theme = theme;
        this.label = label;
        this.description = description;
        this.options = options;
        this.value = options.contains(value) ? value : options.getFirst();
        this.onValueChanged = onValueChanged;
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        int boxX = this.x;
        float rowCenterY = this.y + (this.height / 2.0F);
        int boxY = Math.round(rowCenterY - (BOX_HEIGHT / 2.0F));
        int labelX = boxX + BOX_WIDTH + 14;
        int textBlockHeight = this.description != null ? UiRender.fontHeight() + TEXT_GAP + UiRender.fontHeight() : UiRender.fontHeight();
        int labelY = Math.round(rowCenterY - (textBlockHeight / 2.0F)) + 1;
        int descriptionY = labelY + UiRender.fontHeight() + TEXT_GAP;

        boolean hovered = UiRender.contains(boxX, boxY, BOX_WIDTH, BOX_HEIGHT, mouseX, mouseY);
        this.hoverProgress = UiRender.animate(this.hoverProgress, hovered || this.expanded ? 1.0F : 0.0F, 0.22F);
        int fill = UiRender.lerpColor(this.theme.buttonColor(), this.theme.buttonHoverColor(), this.hoverProgress);
        SdfRenderer.drawRoundedRect(guiGraphics, boxX, boxY, BOX_WIDTH, BOX_HEIGHT, SdfRectStyle.bordered(fill, this.theme.buttonBorderColor(), 6.0F, 1.0F));

        int textY = UiRender.textTopForCenter(rowCenterY);
        UiRender.text(guiGraphics, this.value, boxX + 8, textY, this.theme.titleColor(), false);
        UiRender.centeredText(guiGraphics, this.expanded ? "^" : "v", boxX + BOX_WIDTH - 10, textY, this.theme.mutedTextColor());

        UiRender.text(guiGraphics, this.label, labelX, labelY, this.theme.titleColor(), true);
        if (this.description != null) {
            UiRender.text(guiGraphics, this.description, labelX, descriptionY, this.theme.mutedTextColor(), false);
        }
    }

    @Override
    protected void renderOverlay(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        if (!this.expanded || this.options.isEmpty()) {
            return;
        }

        int dropdownX = this.x;
        int dropdownY = this.y + 26;
        int dropdownHeight = (this.options.size() * OPTION_HEIGHT) + ((this.options.size() - 1) * OPTION_GAP) + (DROPDOWN_PADDING * 2);
        SdfRenderer.drawRoundedRect(guiGraphics, dropdownX, dropdownY, BOX_WIDTH, dropdownHeight, SdfRectStyle.bordered(this.theme.cardColor(), this.theme.cardBorderColor(), 8.0F, 1.0F));

        for (int i = 0; i < this.options.size(); i++) {
            int optionY = dropdownY + DROPDOWN_PADDING + (i * (OPTION_HEIGHT + OPTION_GAP));
            String option = this.options.get(i);
            boolean hovered = UiRender.contains(dropdownX + 2, optionY, BOX_WIDTH - 4, OPTION_HEIGHT, mouseX, mouseY);
            boolean selected = option.equals(this.value);

            if (hovered || selected) {
                int optionFill = selected ? UiRender.withAlpha(this.theme.accentColor(), 34) : UiRender.withAlpha(this.theme.textColor(), 18);
                SdfRenderer.drawRoundedRect(guiGraphics, dropdownX + 2, optionY, BOX_WIDTH - 4, OPTION_HEIGHT, SdfRectStyle.filled(optionFill, 5.0F));
            }

            UiRender.text(guiGraphics, option, dropdownX + 10, UiRender.centeredTextY(optionY, OPTION_HEIGHT), selected ? this.theme.titleColor() : this.theme.textColor(), false);
        }
    }

    @Override
    protected boolean onClick(MouseButtonEvent mouseButtonEvent, boolean bl) {
        if (mouseButtonEvent.button() != 0) {
            return false;
        }

        int boxY = Math.round((this.y + (this.height / 2.0F)) - (BOX_HEIGHT / 2.0F));
        if (UiRender.contains(this.x, boxY, BOX_WIDTH, BOX_HEIGHT, mouseButtonEvent.x(), mouseButtonEvent.y())) {
            this.expanded = !this.expanded;
            return true;
        }

        if (!this.expanded) {
            return false;
        }

        int dropdownX = this.x;
        int dropdownY = this.y + 26;
        for (int i = 0; i < this.options.size(); i++) {
            int optionY = dropdownY + DROPDOWN_PADDING + (i * (OPTION_HEIGHT + OPTION_GAP));
            if (UiRender.contains(dropdownX + 2, optionY, BOX_WIDTH - 4, OPTION_HEIGHT, mouseButtonEvent.x(), mouseButtonEvent.y())) {
                this.value = this.options.get(i);
                this.expanded = false;
                this.onValueChanged.accept(this.value);
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        if (super.isMouseOver(mouseX, mouseY)) {
            return true;
        }

        if (!this.expanded) {
            return false;
        }

        int dropdownY = this.y + 26;
        int dropdownHeight = (this.options.size() * OPTION_HEIGHT) + ((this.options.size() - 1) * OPTION_GAP) + (DROPDOWN_PADDING * 2);
        return UiRender.contains(this.x, dropdownY, BOX_WIDTH, dropdownHeight, mouseX, mouseY);
    }

    @Override
    public void setFocused(boolean focused) {
        super.setFocused(focused);
        if (!focused) {
            this.expanded = false;
        }
    }
}
