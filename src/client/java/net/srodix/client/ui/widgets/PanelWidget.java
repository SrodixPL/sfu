package net.srodix.client.ui.widgets;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.input.MouseButtonEvent;
import net.srodix.client.ui.UiRender;
import net.srodix.client.ui.sdf.SdfRectStyle;
import net.srodix.client.ui.sdf.SdfRenderer;
import net.srodix.client.ui.sdf.SdfShadowStyle;
import net.srodix.client.ui.theme.GuiTheme;

public class PanelWidget extends WidgetContainer {
    private static final int HEADER_SIDE_PADDING = 18;
    private static final int HEADER_TOP_PADDING = 14;
    private static final int CONTENT_SIDE_PADDING = 18;
    private static final int CONTENT_TOP_PADDING = 52;
    private static final int CONTENT_BOTTOM_PADDING = 18;
    private static final int VIEWPORT_PADDING = 2;

    private final GuiTheme theme;
    private String title;
    private String subtitle;
    private boolean drawShadow = false;
    private int scrollOffset;

    public PanelWidget(int x, int y, int width, int height, GuiTheme theme, String title, String subtitle) {
        super(x, y, width, height);
        this.theme = theme;
        this.title = title;
        this.subtitle = subtitle;
    }

    public void setHeader(String title, String subtitle) {
        this.title = title;
        this.subtitle = subtitle;
    }

    public void setDrawShadow(boolean drawShadow) {
        this.drawShadow = drawShadow;
    }

    public int getContentX() {
        return this.x + CONTENT_SIDE_PADDING;
    }

    public int getContentY() {
        return this.y + CONTENT_TOP_PADDING;
    }

    public int getContentWidth() {
        return this.width - (CONTENT_SIDE_PADDING * 2);
    }

    public int getContentHeight() {
        return this.height - CONTENT_TOP_PADDING - CONTENT_BOTTOM_PADDING;
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        clampScroll();
        if (this.drawShadow) {
            SdfRenderer.drawShadow(guiGraphics, this.x, this.y, this.width, this.height, new SdfShadowStyle(this.theme.panelShadowColor(), this.theme.panelRadius(), 4.0F, 0, 1));
        }

        SdfRenderer.drawRoundedRect(guiGraphics, this.x, this.y, this.width, this.height, SdfRectStyle.bordered(this.theme.cardColor(), this.theme.cardBorderColor(), this.theme.cardRadius(), 1.0F));

        if (this.title != null && !this.title.isEmpty()) {
            UiRender.text(guiGraphics, this.title, this.x + HEADER_SIDE_PADDING, this.y + HEADER_TOP_PADDING, this.theme.titleColor(), true);
        }

        if (this.subtitle != null && !this.subtitle.isEmpty()) {
            UiRender.text(guiGraphics, this.subtitle, this.x + HEADER_SIDE_PADDING, this.y + HEADER_TOP_PADDING + 18, this.theme.textColor(), false);
        }

        int viewportX = getContentX();
        int viewportY = getContentY();
        int viewportWidth = getContentWidth();
        int viewportHeight = getContentHeight();
        guiGraphics.enableScissor(viewportX - VIEWPORT_PADDING, viewportY - VIEWPORT_PADDING, viewportX + viewportWidth + VIEWPORT_PADDING, viewportY + viewportHeight + VIEWPORT_PADDING);
        guiGraphics.pose().pushMatrix();
        guiGraphics.pose().translate(0.0F, (float) -this.scrollOffset);
        super.renderWidget(guiGraphics, mouseX, mouseY + this.scrollOffset, partialTick);
        guiGraphics.pose().popMatrix();
        guiGraphics.disableScissor();

        guiGraphics.pose().pushMatrix();
        guiGraphics.pose().translate(0.0F, (float) -this.scrollOffset);
        super.renderOverlay(guiGraphics, mouseX, mouseY + this.scrollOffset, partialTick);
        guiGraphics.pose().popMatrix();
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent mouseButtonEvent, boolean bl) {
        if (!UiRender.contains(this.x, this.y, this.width, this.height, mouseButtonEvent.x(), mouseButtonEvent.y())) {
            return false;
        }

        if (!UiRender.contains(getContentX(), getContentY(), getContentWidth(), getContentHeight(), mouseButtonEvent.x(), mouseButtonEvent.y())) {
            return false;
        }

        MouseButtonEvent translated = new MouseButtonEvent(mouseButtonEvent.x(), mouseButtonEvent.y() + this.scrollOffset, mouseButtonEvent.buttonInfo());
        for (AbstractWidget child : this.children) {
            if (child.mouseClicked(translated, bl)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amountX, double amountY) {
        if (!UiRender.contains(getContentX(), getContentY(), getContentWidth(), getContentHeight(), mouseX, mouseY)) {
            return false;
        }

        int maxScroll = maxScroll();
        if (maxScroll <= 0) {
            return false;
        }

        this.scrollOffset = Math.max(0, Math.min(maxScroll, this.scrollOffset - (int) Math.round(amountY * 16.0D)));
        return true;
    }

    @Override
    public boolean shouldTakeFocusAfterInteraction() {
        return false;
    }

    private int contentBottom() {
        int maxBottom = getContentY();
        for (AbstractWidget child : this.children) {
            maxBottom = Math.max(maxBottom, child.getBottom());
        }

        return maxBottom;
    }

    private int maxScroll() {
        return Math.max(0, contentBottom() - getContentY() - getContentHeight());
    }

    private void clampScroll() {
        this.scrollOffset = Math.max(0, Math.min(this.scrollOffset, maxScroll()));
    }
}
