package net.srodix.client.ui;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.srodix.client.ui.GuiContentRegistry.TabDefinition;
import net.srodix.client.ui.sdf.SdfRectStyle;
import net.srodix.client.ui.sdf.SdfRenderer;
import net.srodix.client.ui.sdf.SdfShadowStyle;
import net.srodix.client.ui.theme.GuiTheme;
import net.srodix.client.ui.widgets.AbstractWidget;
import net.srodix.client.ui.widgets.PanelWidget;
import net.srodix.client.ui.widgets.TabListWidget;

import java.util.ArrayList;
import java.util.List;

public class GuiMain extends Screen {
    private static final float PANEL_WIDTH_RATIO = 0.76F;
    private static final float PANEL_HEIGHT_RATIO = 0.75F;
    private static final int PANEL_MIN_WIDTH = 520;
    private static final int PANEL_MIN_HEIGHT = 320;
    private static final int PANEL_INSET = 20;
    private static final int HEADER_HEIGHT = 54;
    private static final int SIDEBAR_GAP = 12;
    private static final int CONTENT_GAP = 22;

    private static float uiScale = 1.0F;

    private final GuiTheme theme = GuiTheme.defaultTheme();
    private final List<AbstractWidget> widgets = new ArrayList<>();
    private float openProgress;
    private boolean closing;
    private boolean closeRequested;
    private long lastAnimationStepMs = -1L;
    private float tabSwitchProgress = 1.0F;
    private int tabSwitchDirection = 1;

    public GuiMain(Component title) {
        super(title);
    }

    public static float getUiScale() {
        return uiScale;
    }

    public static void setUiScale(float scale) {
        uiScale = Math.max(0.65F, Math.min(2.0F, scale));
    }

    @Override
    protected void init() {
        this.openProgress = 0.02F;
        this.closing = false;
        this.closeRequested = false;
        this.lastAnimationStepMs = -1L;
        this.tabSwitchProgress = 1.0F;
        this.tabSwitchDirection = 1;
        GuiGlobals.currentTabIndex = clampTabIndex(GuiGlobals.currentTabIndex);
        rebuildWidgets();
        warmUpResources();
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        rebuildWidgets();
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        stepAnimations();
        renderBlurredBackground(guiGraphics);
        int backgroundAlpha = Math.round(0x26 * this.openProgress);
        guiGraphics.fill(0, 0, this.width, this.height, backgroundAlpha << 24);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        stepAnimations();
        if (this.closing && this.openProgress <= 0.05F && !this.closeRequested) {
            this.closeRequested = true;
            super.onClose();
            return;
        }

        int scaledMouseX = Math.round(mouseX / uiScale);
        int scaledMouseY = Math.round(mouseY / uiScale);
        Layout layout = layout();
        float shellScale = 0.96F + (this.openProgress * 0.04F);
        float centerX = layout.panelX + (layout.panelWidth / 2.0F);
        float centerY = layout.panelY + (layout.panelHeight / 2.0F);
        float translateY = (1.0F - this.openProgress) * 18.0F;

        guiGraphics.pose().pushMatrix();
        guiGraphics.pose().scale(uiScale, uiScale);
        guiGraphics.pose().translate(centerX, centerY + translateY);
        guiGraphics.pose().scale(shellScale, shellScale);
        guiGraphics.pose().translate(-centerX, -centerY);
        drawShell(guiGraphics);

        if (!this.widgets.isEmpty()) {
            this.widgets.get(0).render(guiGraphics, scaledMouseX, scaledMouseY, partialTick);
            this.widgets.get(0).renderOverlays(guiGraphics, scaledMouseX, scaledMouseY, partialTick);
        }

        float tabSlideOffset = (1.0F - this.tabSwitchProgress) * 16.0F * this.tabSwitchDirection;
        guiGraphics.pose().pushMatrix();
        guiGraphics.pose().translate(tabSlideOffset, 0.0F);
        for (int i = 1; i < this.widgets.size(); i++) {
            this.widgets.get(i).render(guiGraphics, scaledMouseX - Math.round(tabSlideOffset), scaledMouseY, partialTick);
        }

        for (int i = 1; i < this.widgets.size(); i++) {
            this.widgets.get(i).renderOverlays(guiGraphics, scaledMouseX - Math.round(tabSlideOffset), scaledMouseY, partialTick);
        }
        guiGraphics.pose().popMatrix();
        guiGraphics.pose().popMatrix();
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent mouseButtonEvent, boolean bl) {
        MouseButtonEvent scaled = new MouseButtonEvent(mouseButtonEvent.x() / uiScale, mouseButtonEvent.y() / uiScale, mouseButtonEvent.buttonInfo());
        for (AbstractWidget widget : this.widgets) {
            if (widget.mouseClicked(scaled, bl)) {
                unfocusOthers(widget);
                return true;
            }
        }

        unfocusOthers(null);
        return super.mouseClicked(mouseButtonEvent, bl);
    }

    @Override
    public boolean keyPressed(KeyEvent keyEvent) {
        if ((keyEvent.key() == InputConstants.KEY_RSHIFT || keyEvent.key() == InputConstants.KEY_ESCAPE) && !this.closing) {
            onClose();
            return true;
        }

        for (AbstractWidget widget : this.widgets) {
            if (widget.keyPressed(keyEvent)) {
                return true;
            }
        }

        return super.keyPressed(keyEvent);
    }

    @Override
    public boolean charTyped(CharacterEvent characterEvent) {
        for (AbstractWidget widget : this.widgets) {
            if (widget.charTyped(characterEvent)) {
                return true;
            }
        }

        return super.charTyped(characterEvent);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amountX, double amountY) {
        double scaledMouseX = mouseX / uiScale;
        double scaledMouseY = mouseY / uiScale;

        for (AbstractWidget widget : this.widgets) {
            if (widget.mouseScrolled(scaledMouseX, scaledMouseY, amountX, amountY)) {
                return true;
            }
        }

        return super.mouseScrolled(mouseX, mouseY, amountX, amountY);
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void onClose() {
        if (!this.closing) {
            this.closing = true;
            return;
        }

        super.onClose();
    }

    private void drawShell(GuiGraphics guiGraphics) {
        Layout layout = layout();
        SdfRenderer.drawShadow(guiGraphics, layout.panelX, layout.panelY, layout.panelWidth, layout.panelHeight, new SdfShadowStyle(this.theme.panelShadowColor(), this.theme.panelRadius(), 4.0F, 0, 1));
        SdfRenderer.drawRoundedRect(guiGraphics, layout.panelX, layout.panelY, layout.panelWidth, layout.panelHeight, SdfRectStyle.bordered(this.theme.panelColor(), this.theme.panelBorderColor(), this.theme.panelRadius(), 1.0F));
        UiRender.text(guiGraphics, "Skyblock Farming Utils", layout.contentX, layout.panelY + 20, this.theme.titleColor(), true);
        UiRender.fill(guiGraphics, layout.dividerX, layout.panelY + 1, 1, layout.panelHeight - 2, this.theme.dividerColor());
    }

    protected void rebuildWidgets() {
        this.widgets.clear();
        Layout layout = layout();
        int selectedTabIndex = clampTabIndex(GuiGlobals.currentTabIndex);
        TabDefinition selectedTab = GuiContentRegistry.tabAt(selectedTabIndex);

        this.widgets.add(new TabListWidget(
            layout.sidebarX,
            layout.contentTop,
            layout.sidebarWidth,
            layout.contentHeight,
            this.theme,
            GuiContentRegistry.tabLabels(),
            selectedTabIndex,
            this::switchTab
        ));

        PanelWidget panel = new PanelWidget(layout.mainX, layout.panelY + 14, layout.mainWidth, layout.panelHeight - 28, this.theme, selectedTab.panelTitle(), selectedTab.panelSubtitle());
        this.widgets.add(panel);
        selectedTab.builder().build(panel, this.theme, panel.getContentX(), panel.getContentY(), panel.getContentWidth());
    }

    private void unfocusOthers(AbstractWidget focusedWidget) {
        for (AbstractWidget widget : this.widgets) {
            if (widget != focusedWidget) {
                widget.setFocused(false);
            }
        }
    }

    private void stepAnimations() {
        long now = System.currentTimeMillis();
        if (this.lastAnimationStepMs == now) {
            return;
        }

        this.lastAnimationStepMs = now;
        this.openProgress = UiRender.animate(this.openProgress, this.closing ? 0.0F : 1.0F, 0.18F);
        this.tabSwitchProgress = UiRender.animate(this.tabSwitchProgress, 1.0F, 0.2F);
    }

    private void switchTab(int index) {
        int nextIndex = clampTabIndex(index);
        if (GuiGlobals.currentTabIndex == nextIndex) {
            return;
        }

        this.tabSwitchDirection = Integer.compare(nextIndex, GuiGlobals.currentTabIndex);
        if (this.tabSwitchDirection == 0) {
            this.tabSwitchDirection = 1;
        }

        GuiGlobals.currentTabIndex = nextIndex;
        this.tabSwitchProgress = 0.0F;
        rebuildWidgets();
    }

    private void warmUpResources() {
        Layout layout = layout();
        SdfRenderer.warmShadow(layout.panelWidth, layout.panelHeight, new SdfShadowStyle(this.theme.panelShadowColor(), this.theme.panelRadius(), 4.0F, 0, 1));
        SdfRenderer.warmRoundedRect(layout.panelWidth, layout.panelHeight, SdfRectStyle.bordered(this.theme.panelColor(), this.theme.panelBorderColor(), this.theme.panelRadius(), 1.0F));
        SdfRenderer.warmRoundedRect(layout.mainWidth, layout.panelHeight - 28, SdfRectStyle.bordered(this.theme.cardColor(), this.theme.cardBorderColor(), this.theme.cardRadius(), 1.0F));
        SdfRenderer.warmRoundedRect(46, 22, SdfRectStyle.bordered(this.theme.toggleOffColor(), this.theme.toggleBorderColor(), 11.0F, 1.0F));
        SdfRenderer.warmRoundedRect(46, 22, SdfRectStyle.bordered(this.theme.toggleOnColor(), this.theme.toggleBorderColor(), 11.0F, 1.0F));
        SdfRenderer.warmRoundedRect(80, 22, SdfRectStyle.bordered(this.theme.buttonColor(), this.theme.buttonBorderColor(), 6.0F, 1.0F));
        SdfRenderer.warmRoundedRect(100, 22, SdfRectStyle.bordered(this.theme.buttonColor(), this.theme.buttonBorderColor(), 6.0F, 1.0F));
        SdfRenderer.warmRoundedRect(110, 22, SdfRectStyle.bordered(this.theme.buttonColor(), this.theme.buttonBorderColor(), 6.0F, 1.0F));
        SdfRenderer.warmCircle(8, this.theme.toggleThumbColor(), 0x40FFFFFF, 1.0F);
        SdfRenderer.warmCircle(9, this.theme.buttonColor(), this.theme.buttonBorderColor(), 1.0F);
        UiRender.textWidth("Skyblock Farming Utils");
        for (String label : GuiContentRegistry.tabLabels()) {
            UiRender.textWidth(label);
        }
        UiRender.textWidth(GuiGlobals.showcaseCycleValue);
        UiRender.textWidth(GuiGlobals.showcaseComboValue);
        UiRender.textWidth("Button");
        UiRender.textWidth("v");
    }

    private int clampTabIndex(int index) {
        return Math.max(0, Math.min(index, GuiContentRegistry.tabs().size() - 1));
    }

    private Layout layout() {
        int scaledWidth = Math.round(this.width / uiScale);
        int scaledHeight = Math.round(this.height / uiScale);
        int panelWidth = Math.max(PANEL_MIN_WIDTH, Math.round(scaledWidth * PANEL_WIDTH_RATIO));
        int panelHeight = Math.max(PANEL_MIN_HEIGHT, Math.round(scaledHeight * PANEL_HEIGHT_RATIO));
        panelWidth = Math.min(panelWidth, scaledWidth - 32);
        panelHeight = Math.min(panelHeight, scaledHeight - 32);

        int panelX = (scaledWidth - panelWidth) / 2;
        int panelY = (scaledHeight - panelHeight) / 2;
        int contentX = panelX + PANEL_INSET;
        int contentTop = panelY + HEADER_HEIGHT;
        int contentHeight = panelHeight - (HEADER_HEIGHT + PANEL_INSET + 4);
        int sidebarOuterPadding = 4;
        int sidebarColumnWidth = Math.max(126, panelWidth / 5);
        int sidebarX = contentX + sidebarOuterPadding;
        int sidebarWidth = sidebarColumnWidth - (sidebarOuterPadding * 2);
        int dividerX = contentX + sidebarColumnWidth + SIDEBAR_GAP;
        int mainX = dividerX + CONTENT_GAP;
        int mainWidth = panelX + panelWidth - PANEL_INSET - mainX;
        int cardHeight = contentHeight;

        return new Layout(panelX, panelY, panelWidth, panelHeight, contentX, contentTop, contentHeight, sidebarX, sidebarWidth, dividerX, mainX, mainWidth, cardHeight);
    }

    private record Layout(
        int panelX,
        int panelY,
        int panelWidth,
        int panelHeight,
        int contentX,
        int contentTop,
        int contentHeight,
        int sidebarX,
        int sidebarWidth,
        int dividerX,
        int mainX,
        int mainWidth,
        int cardHeight
    ) {
    }
}
