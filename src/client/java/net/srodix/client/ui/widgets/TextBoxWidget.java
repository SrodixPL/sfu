package net.srodix.client.ui.widgets;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.srodix.client.ui.UiRender;
import net.srodix.client.ui.sdf.SdfRectStyle;
import net.srodix.client.ui.sdf.SdfRenderer;
import net.srodix.client.ui.theme.GuiTheme;

import java.util.function.Consumer;

public class TextBoxWidget extends AbstractWidget {
    private static final int BOX_WIDTH = 100;
    private static final int BOX_HEIGHT = 22;
    private static final int ROW_HEIGHT = 34;
    private static final int TEXT_GAP = 2;

    private final GuiTheme theme;
    private final Component label;
    private final Component description;
    private final Consumer<String> onValueChanged;
    private String value;

    public TextBoxWidget(int x, int y, int width, GuiTheme theme, Component label, Component description, String value, Consumer<String> onValueChanged) {
        super(x, y, width, ROW_HEIGHT);
        this.theme = theme;
        this.label = label;
        this.description = description;
        this.value = value;
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

        boolean active = this.focused || UiRender.contains(boxX, boxY, BOX_WIDTH, BOX_HEIGHT, mouseX, mouseY);
        int fill = active ? this.theme.buttonHoverColor() : this.theme.buttonColor();
        SdfRenderer.drawRoundedRect(guiGraphics, boxX, boxY, BOX_WIDTH, BOX_HEIGHT, SdfRectStyle.bordered(fill, this.theme.buttonBorderColor(), 6.0F, 1.0F));
        
        String displayText = this.value + (this.focused && (System.currentTimeMillis() / 500L) % 2L == 0L ? "|" : "");
        int textY = UiRender.textTopForCenter(rowCenterY);
        UiRender.text(guiGraphics, displayText, boxX + 8, textY, this.theme.titleColor(), false);

        UiRender.text(guiGraphics, this.label, labelX, labelY, this.theme.titleColor(), true);
        if (this.description != null) {
            UiRender.text(guiGraphics, this.description, labelX, descriptionY, this.theme.mutedTextColor(), false);
        }
    }

    @Override
    protected boolean onClick(MouseButtonEvent mouseButtonEvent, boolean bl) {
        if (mouseButtonEvent.button() != 0) return false;

        float rowCenterY = this.y + (this.height / 2.0F);
        int boxY = Math.round(rowCenterY - (BOX_HEIGHT / 2.0F));
        return UiRender.contains(this.x, boxY, BOX_WIDTH, BOX_HEIGHT, mouseButtonEvent.x(), mouseButtonEvent.y());
    }

    @Override
    public boolean keyPressed(KeyEvent keyEvent) {
        if (!this.focused) {
            return false;
        }

        if (keyEvent.key() == InputConstants.KEY_BACKSPACE && !this.value.isEmpty()) {
            this.value = this.value.substring(0, this.value.length() - 1);
            this.onValueChanged.accept(this.value);
            return true;
        }

        return false;
    }

    @Override
    public boolean charTyped(CharacterEvent characterEvent) {
        if (!this.focused) {
            return false;
        }

        String character = characterEvent.codepointAsString();
        if (character.isBlank()) {
            return false;
        }

        this.value += character;
        this.onValueChanged.accept(this.value);
        return true;
    }
}
