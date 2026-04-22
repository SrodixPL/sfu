package net.srodix.client.ui;

import net.minecraft.network.chat.Component;
import net.srodix.client.ui.theme.GuiTheme;
import net.srodix.client.ui.widgets.ButtonWidget;
import net.srodix.client.ui.widgets.ComboBoxWidget;
import net.srodix.client.ui.widgets.CycleWidget;
import net.srodix.client.ui.widgets.PanelWidget;
import net.srodix.client.ui.widgets.TextBoxWidget;
import net.srodix.client.ui.widgets.ToggleSwitch;

import java.util.List;

public final class GuiContentRegistry {
    private static final int ROW_SPACING = 40;
    private static final List<String> SHOWCASE_CYCLE_OPTIONS = List.of("Default", "Garden", "Contest");
    private static final List<String> SHOWCASE_COMBO_OPTIONS = List.of("Wheat", "Carrot", "Potato", "Nether Wart");
    private static final List<TabDefinition> TABS = List.of(
        new TabDefinition(
            "Showcase",
            "Widgets",
            "Every control type in one place.",
            GuiContentRegistry::populateShowcaseTab
        ),
        new TabDefinition(
            "Empty",
            "Empty Tab",
            "Reserved for future controls.",
            (panel, theme, x, y, width) -> {
            }
        )
    );

    private GuiContentRegistry() {
    }

    public static List<TabDefinition> tabs() {
        return TABS;
    }

    public static TabDefinition tabAt(int index) {
        return TABS.get(Math.max(0, Math.min(index, TABS.size() - 1)));
    }

    public static List<String> tabLabels() {
        return TABS.stream().map(TabDefinition::label).toList();
    }

    private static void populateShowcaseTab(PanelWidget panel, GuiTheme theme, int x, int y, int width) {
        panel.addChild(new ToggleSwitch(
            x,
            y,
            width,
            theme,
            Component.literal("Toggle"),
            Component.literal("Example boolean value stored in GuiGlobals."),
            GuiGlobals.showcaseToggle,
            value -> GuiGlobals.showcaseToggle = value
        ));
        panel.addChild(new CycleWidget(
            x,
            y + ROW_SPACING,
            width,
            theme,
            Component.literal("Cycle"),
            Component.literal("Cycles through a small list of values."),
            SHOWCASE_CYCLE_OPTIONS,
            SHOWCASE_CYCLE_OPTIONS.indexOf(GuiGlobals.showcaseCycleValue),
            value -> GuiGlobals.showcaseCycleValue = value
        ));
        panel.addChild(new TextBoxWidget(
            x,
            y + (ROW_SPACING * 2),
            width,
            theme,
            Component.literal("Textbox"),
            Component.literal("Free-form text persisted in GuiGlobals."),
            GuiGlobals.showcaseTextValue,
            value -> GuiGlobals.showcaseTextValue = value
        ));
        panel.addChild(new ComboBoxWidget(
            x,
            y + (ROW_SPACING * 3),
            width,
            theme,
            Component.literal("Combobox"),
            Component.literal("Dropdown menu with selectable options."),
            SHOWCASE_COMBO_OPTIONS,
            GuiGlobals.showcaseComboValue,
            value -> GuiGlobals.showcaseComboValue = value
        ));
        panel.addChild(new ButtonWidget(
            x,
            y + (ROW_SPACING * 4),
            100,
            theme,
            "Button",
            () -> {
            }
        ));
    }

    @FunctionalInterface
    public interface TabContentBuilder {
        void build(PanelWidget panel, GuiTheme theme, int x, int y, int width);
    }

    public record TabDefinition(
        String label,
        String panelTitle,
        String panelSubtitle,
        TabContentBuilder builder
    ) {
    }
}
