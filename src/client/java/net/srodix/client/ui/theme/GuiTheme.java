package net.srodix.client.ui.theme;

public record GuiTheme(
    int panelColor,
    int panelBorderColor,
    int panelShadowColor,
    int panelRadius,
    int cardColor,
    int cardBorderColor,
    int cardRadius,
    int accentColor,
    int titleColor,
    int textColor,
    int mutedTextColor,
    int dividerColor,
    int toggleOffColor,
    int toggleOnColor,
    int toggleBorderColor,
    int toggleThumbColor,
    int buttonColor,
    int buttonHoverColor,
    int buttonBorderColor,
    int tooltipColor,
    int tooltipBorderColor
) {
    public static GuiTheme defaultTheme() {
        return new GuiTheme(
            0xD11A232E,
            0xA93A4A58,
            0x66000000,
            18,
            0x7A252E39,
            0x6044505E,
            14,
            0xFF4BAE9A,
            0xFFF2F5F7,
            0xA6C6CED6,
            0x7FAAB7C2,
            0x2EFFFFFF,
            0xFF2B3440,
            0xFF4BAE9A,
            0x8FFFFFFF,
            0xFFF6FBFF,
            0x88303A45,
            0xB8455564,
            0x5CFFFFFF,
            0xEE151C24,
            0x9A546273
        );
    }
}
