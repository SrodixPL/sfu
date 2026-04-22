package net.srodix.client.ui.widgets;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.srodix.client.ui.GuiMain;
import net.srodix.client.ui.UiRender;
import net.srodix.client.ui.sdf.SdfRectStyle;
import net.srodix.client.ui.sdf.SdfRenderer;
import net.srodix.client.ui.theme.GuiTheme;

public class TooltipIconWidget extends AbstractWidget {
    private static final int SIZE = 18;
    private static final int CIRCLE_RADIUS = 8;

    private final GuiTheme theme;
    private final Component tooltip;
    private float hoverProgress;

    public TooltipIconWidget(int x, int y, GuiTheme theme, Component tooltip) {
        super(x, y, SIZE, SIZE);
        this.theme = theme;
        this.tooltip = tooltip;
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        boolean hovered = isMouseOver(mouseX, mouseY);
        this.hoverProgress = UiRender.animate(this.hoverProgress, hovered ? 1.0F : 0.0F, 0.22F);
        int fill = UiRender.lerpColor(this.theme.buttonColor(), this.theme.buttonHoverColor(), this.hoverProgress);
        int centerX = this.x + (this.width / 2);
        int centerY = this.y + (this.height / 2);
        int glyphY = UiRender.textTopForCenter(centerY) + 1;
        SdfRenderer.drawCircle(guiGraphics, centerX, centerY, CIRCLE_RADIUS, fill, this.theme.buttonBorderColor(), 1.0F);
        UiRender.centeredText(guiGraphics, "?", centerX, glyphY, this.theme.titleColor());
    }

    @Override
    protected void renderOverlay(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        if (!isMouseOver(mouseX, mouseY)) {
            return;
        }

        int tooltipTextPadding = 10;
        int minTooltipWidth = 132;
        int maxTooltipWidth = 220;
        int tooltipTextWidth = Math.max(minTooltipWidth - (tooltipTextPadding * 2), Math.min(maxTooltipWidth - (tooltipTextPadding * 2), UiRender.textWidth(this.tooltip.getString())));
        int tooltipWidth = Math.max(minTooltipWidth, Math.min(maxTooltipWidth, tooltipTextWidth + (tooltipTextPadding * 2)));
        int tooltipHeight = UiRender.wrappedTextHeight(this.tooltip, tooltipWidth - (tooltipTextPadding * 2)) + 18;
        int logicalScreenWidth = Math.round(Minecraft.getInstance().getWindow().getGuiScaledWidth() / GuiMain.getUiScale());
        int logicalScreenHeight = Math.round(Minecraft.getInstance().getWindow().getGuiScaledHeight() / GuiMain.getUiScale());
        int tooltipX = Math.max(12, Math.min(this.x - tooltipWidth + this.width, logicalScreenWidth - tooltipWidth - 12));
        int tooltipY = this.y + this.height + 8;
        if (tooltipY + tooltipHeight > logicalScreenHeight - 12) {
            tooltipY = this.y - tooltipHeight - 8;
        }
        tooltipY = Math.max(12, tooltipY);

        SdfRenderer.drawRoundedRect(guiGraphics, tooltipX, tooltipY, tooltipWidth, tooltipHeight, SdfRectStyle.bordered(this.theme.tooltipColor(), this.theme.tooltipBorderColor(), 10.0F, 1.0F));
        UiRender.wrappedText(guiGraphics, this.tooltip, tooltipX + tooltipTextPadding, tooltipY + 9, tooltipWidth - (tooltipTextPadding * 2), this.theme.titleColor());
    }

    @Override
    protected boolean onClick(MouseButtonEvent mouseButtonEvent, boolean bl) {
        return mouseButtonEvent.button() == 0;
    }

    @Override
    public boolean shouldTakeFocusAfterInteraction() {
        return false;
    }
}
