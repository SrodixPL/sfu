package net.srodix.client.ui.widgets;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;

import java.util.ArrayList;
import java.util.List;

public class WidgetContainer extends AbstractWidget {
    protected final List<AbstractWidget> children = new ArrayList<>();

    public WidgetContainer(int x, int y, int width, int height) {
        super(x, y, width, height);
    }

    public void clearChildren() {
        this.children.clear();
    }

    public <T extends AbstractWidget> T addChild(T widget) {
        this.children.add(widget);
        return widget;
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        for (AbstractWidget child : this.children) {
            child.render(guiGraphics, mouseX, mouseY, partialTick);
        }
    }

    @Override
    protected void renderOverlay(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        for (AbstractWidget child : this.children) {
            child.renderOverlays(guiGraphics, mouseX, mouseY, partialTick);
        }
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent mouseButtonEvent, boolean bl) {
        if (!super.mouseClicked(mouseButtonEvent, bl) && !isMouseOver(mouseButtonEvent.x(), mouseButtonEvent.y())) {
            return false;
        }

        for (AbstractWidget child : this.children) {
            if (child.mouseClicked(mouseButtonEvent, bl)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean keyPressed(KeyEvent keyEvent) {
        for (AbstractWidget child : this.children) {
            if (child.keyPressed(keyEvent)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean keyReleased(KeyEvent keyEvent) {
        for (AbstractWidget child : this.children) {
            if (child.keyReleased(keyEvent)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean charTyped(CharacterEvent characterEvent) {
        for (AbstractWidget child : this.children) {
            if (child.charTyped(characterEvent)) {
                return true;
            }
        }

        return false;
    }
}
