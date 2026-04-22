package net.srodix.client.ui.widgets;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.srodix.client.ui.UiRender;

public abstract class AbstractWidget implements Renderable, GuiEventListener {
    protected int x;
    protected int y;
    protected int width;
    protected int height;
    protected boolean visible = true;
    protected boolean enabled = true;
    protected boolean focused;

    protected AbstractWidget(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void setBounds(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    @Override
    public final void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        if (!this.visible) {
            return;
        }

        renderWidget(guiGraphics, mouseX, mouseY, partialTick);
    }

    public final void renderOverlays(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        if (!this.visible) {
            return;
        }

        renderOverlay(guiGraphics, mouseX, mouseY, partialTick);
    }

    protected abstract void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick);

    protected void renderOverlay(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent mouseButtonEvent, boolean bl) {
        if (!this.visible || !this.enabled) {
            return false;
        }

        boolean hovered = isMouseOver(mouseButtonEvent.x(), mouseButtonEvent.y());
        setFocused(hovered && shouldTakeFocusAfterInteraction());
        return hovered && onClick(mouseButtonEvent, bl);
    }

    protected boolean onClick(MouseButtonEvent mouseButtonEvent, boolean bl) {
        return false;
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent mouseButtonEvent) {
        return false;
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent mouseButtonEvent, double d, double e) {
        return false;
    }

    @Override
    public boolean mouseScrolled(double d, double e, double f, double g) {
        return false;
    }

    @Override
    public boolean keyPressed(KeyEvent keyEvent) {
        return false;
    }

    @Override
    public boolean keyReleased(KeyEvent keyEvent) {
        return false;
    }

    @Override
    public boolean charTyped(CharacterEvent characterEvent) {
        return false;
    }

    @Override
    public boolean isMouseOver(double d, double e) {
        return this.visible && UiRender.contains(this.x, this.y, this.width, this.height, d, e);
    }

    @Override
    public void setFocused(boolean focused) {
        this.focused = focused;
    }

    @Override
    public boolean isFocused() {
        return this.focused;
    }

    @Override
    public boolean shouldTakeFocusAfterInteraction() {
        return true;
    }

    @Override
    public ScreenRectangle getRectangle() {
        return new ScreenRectangle(this.x, this.y, this.width, this.height);
    }

    public int getBottom() {
        return this.y + this.height;
    }
}
