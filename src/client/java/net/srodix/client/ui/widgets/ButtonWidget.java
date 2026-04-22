package net.srodix.client.ui.widgets;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.input.MouseButtonEvent;
import net.srodix.client.ui.UiRender;
import net.srodix.client.ui.sdf.SdfRectStyle;
import net.srodix.client.ui.sdf.SdfRenderer;
import net.srodix.client.ui.theme.GuiTheme;

public class ButtonWidget extends AbstractWidget {
    private static final int BUTTON_HEIGHT = 22;
    private float hoverProgress;
    private final GuiTheme theme;
    private final String label;
    private final Runnable onClick;

    public ButtonWidget(int x, int y, int width, GuiTheme theme, String label, Runnable onClick) {
        super(x, y, width, BUTTON_HEIGHT);
        this.theme = theme;
        this.label = label;
        this.onClick = onClick;
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        boolean hovered = isMouseOver(mouseX, mouseY);
        this.hoverProgress = UiRender.animate(this.hoverProgress, hovered ? 1.0F : 0.0F, 0.22F);
        int fill = hovered ? this.theme.buttonHoverColor() : this.theme.buttonColor();
        float textScale = 1.0F + (this.hoverProgress * 0.05F);
        SdfRenderer.drawRoundedRect(guiGraphics, this.x, this.y, this.width, this.height, SdfRectStyle.bordered(fill, this.theme.buttonBorderColor(), 6.0F, 1.0F));
        UiRender.scaledCenteredText(guiGraphics, this.label, this.x + (this.width / 2.0F), this.y + (this.height / 2.0F), textScale, this.theme.titleColor(), false);
    }

    @Override
    protected boolean onClick(MouseButtonEvent mouseButtonEvent, boolean bl) {
        if (mouseButtonEvent.button() != 0) {
            return false;
        }

        this.onClick.run();
        return true;
    }
}
