package net.srodix.client.ui.widgets;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.input.MouseButtonEvent;
import net.srodix.client.ui.UiRender;
import net.srodix.client.ui.sdf.SdfRectStyle;
import net.srodix.client.ui.sdf.SdfRenderer;
import net.srodix.client.ui.theme.GuiTheme;

import java.util.List;
import java.util.function.Consumer;

public class TabListWidget extends AbstractWidget {
    private static final int TAB_HEIGHT = 22;
    private static final int TAB_SPACING = 6;
    private static final int LABEL_TOP_PADDING = 8;
    private static final int LABEL_BOTTOM_PADDING = 12;
    private static final int ENTRY_SIDE_PADDING = 8;
    private static final int ENTRY_BACKGROUND_INSET = 4;
    private static final int VIEWPORT_PADDING = 2;

    private final GuiTheme theme;
    private final List<String> tabs;
    private final Consumer<Integer> onSelected;
    private int selectedIndex;
    private int scrollOffset;
    private final float[] hoverProgress;
    private final float[] selectedProgress;

    public TabListWidget(int x, int y, int width, int height, GuiTheme theme, List<String> tabs, int selectedIndex, Consumer<Integer> onSelected) {
        super(x, y, width, height);
        this.theme = theme;
        this.tabs = tabs;
        this.selectedIndex = selectedIndex;
        this.onSelected = onSelected;
        this.hoverProgress = new float[tabs.size()];
        this.selectedProgress = new float[tabs.size()];
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        clampScroll();
        UiRender.text(guiGraphics, "MENU", this.x + ENTRY_SIDE_PADDING, this.y + LABEL_TOP_PADDING, this.theme.mutedTextColor(), false);

        int viewportY = viewportY();
        int viewportBottom = this.y + this.height;
        guiGraphics.enableScissor(this.x - ENTRY_BACKGROUND_INSET - VIEWPORT_PADDING, viewportY - VIEWPORT_PADDING, this.x + this.width + ENTRY_BACKGROUND_INSET + VIEWPORT_PADDING, viewportBottom + VIEWPORT_PADDING);
        for (int index = 0; index < this.tabs.size(); index++) {
            int entryY = entryY(index);
            if (entryY + TAB_HEIGHT < viewportY || entryY > viewportBottom) {
                continue;
            }

            boolean selected = index == this.selectedIndex;
            boolean hovered = UiRender.contains(this.x - ENTRY_BACKGROUND_INSET, entryY, this.width + (ENTRY_BACKGROUND_INSET * 2), TAB_HEIGHT, mouseX, mouseY)
                && mouseY >= viewportY
                && mouseY < viewportBottom;
            this.hoverProgress[index] = UiRender.animate(this.hoverProgress[index], hovered ? 1.0F : 0.0F, 0.22F);
            this.selectedProgress[index] = UiRender.animate(this.selectedProgress[index], selected ? 1.0F : 0.0F, 0.18F);

            if (selected) {
                SdfRenderer.drawRoundedRect(guiGraphics, this.x - ENTRY_BACKGROUND_INSET, entryY, this.width + (ENTRY_BACKGROUND_INSET * 2), TAB_HEIGHT, SdfRectStyle.filled(UiRender.withAlpha(this.theme.accentColor(), 40), 6.0F));
                SdfRenderer.drawRoundedRect(guiGraphics, this.x - ENTRY_BACKGROUND_INSET, entryY + 4, 2, TAB_HEIGHT - 8, SdfRectStyle.filled(this.theme.accentColor(), 1.0F));
            } else if (hovered) {
                SdfRenderer.drawRoundedRect(guiGraphics, this.x - ENTRY_BACKGROUND_INSET, entryY, this.width + (ENTRY_BACKGROUND_INSET * 2), TAB_HEIGHT, SdfRectStyle.filled(UiRender.withAlpha(this.theme.textColor(), 20), 6.0F));
            }

            int textColor = UiRender.lerpColor(this.theme.textColor(), this.theme.titleColor(), this.hoverProgress[index]);
            textColor = UiRender.lerpColor(textColor, this.theme.accentColor(), this.selectedProgress[index]);
            UiRender.scaledText(guiGraphics, this.tabs.get(index), this.x + ENTRY_SIDE_PADDING, UiRender.centeredTextY(entryY, TAB_HEIGHT), 1.0F + (this.hoverProgress[index] * 0.04F) + (this.selectedProgress[index] * 0.03F), textColor, selected);
        }
        guiGraphics.disableScissor();
    }

    @Override
    protected boolean onClick(MouseButtonEvent mouseButtonEvent, boolean bl) {
        if (mouseButtonEvent.button() != 0) {
            return false;
        }

        int viewportY = viewportY();
        int viewportBottom = this.y + this.height;
        if (!UiRender.contains(this.x - ENTRY_BACKGROUND_INSET, viewportY, this.width + (ENTRY_BACKGROUND_INSET * 2), viewportBottom - viewportY, mouseButtonEvent.x(), mouseButtonEvent.y())) {
            return false;
        }

        for (int index = 0; index < this.tabs.size(); index++) {
            int entryY = entryY(index);
            if (UiRender.contains(this.x - ENTRY_BACKGROUND_INSET, entryY, this.width + (ENTRY_BACKGROUND_INSET * 2), TAB_HEIGHT, mouseButtonEvent.x(), mouseButtonEvent.y())) {
                if (this.selectedIndex != index) {
                    this.selectedIndex = index;
                    this.onSelected.accept(index);
                }
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amountX, double amountY) {
        int viewportY = viewportY();
        if (!UiRender.contains(this.x - ENTRY_BACKGROUND_INSET, viewportY, this.width + (ENTRY_BACKGROUND_INSET * 2), this.height - (viewportY - this.y), mouseX, mouseY)) {
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

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return this.visible && UiRender.contains(this.x - ENTRY_BACKGROUND_INSET, this.y, this.width + (ENTRY_BACKGROUND_INSET * 2), this.height, mouseX, mouseY);
    }

    private int viewportY() {
        return this.y + LABEL_TOP_PADDING + UiRender.fontHeight() + LABEL_BOTTOM_PADDING;
    }

    private int contentHeight() {
        if (this.tabs.isEmpty()) {
            return 0;
        }

        return (this.tabs.size() * TAB_HEIGHT) + ((this.tabs.size() - 1) * TAB_SPACING);
    }

    private int entryY(int index) {
        return viewportY() - this.scrollOffset + (index * (TAB_HEIGHT + TAB_SPACING));
    }

    private int maxScroll() {
        return Math.max(0, contentHeight() - (this.height - (viewportY() - this.y)));
    }

    private void clampScroll() {
        this.scrollOffset = Math.max(0, Math.min(this.scrollOffset, maxScroll()));
    }
}
