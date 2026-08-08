package com.moulberry.lattice.widget;

import com.moulberry.lattice.multiversion.LatticeMultiversion;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.util.FormattedCharSequence;

public class CenteredStringWidget extends AbstractWidget {

    private final Font font;

    public CenteredStringWidget(int width, int height, Component component, Font font) {
        this(0, 0, width, height, component, font);
    }

    public CenteredStringWidget(int x, int y, int width, int height, Component component, Font font) {
        super(x, y, width, height, component);
        this.active = false;
        this.font = font;
    }

    public void renderWidget(GuiGraphics guiGraphics, int i, int j, float f) {
        Component component = this.getMessage();
        int widgetWidth = this.getWidth();
        int textWidth = font.width(component);

        FormattedCharSequence formattedCharSequence;
        if (textWidth > widgetWidth) {
            formattedCharSequence = this.clipText(component, widgetWidth);
            textWidth = widgetWidth;
        } else {
            formattedCharSequence = component.getVisualOrderText();
        }

        int x = this.getX() + (widgetWidth - textWidth) / 2;
        int y = this.getY() + (this.getHeight() - this.font.lineHeight) / 2;
        LatticeMultiversion.drawString(guiGraphics, this.font, formattedCharSequence, x, y, -1);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
    }

    private FormattedCharSequence clipText(Component component, int i) {
        FormattedText formattedText = font.substrByWidth(component, i - this.font.width(CommonComponents.ELLIPSIS));
        return Language.getInstance().getVisualOrder(FormattedText.composite(new FormattedText[]{formattedText, CommonComponents.ELLIPSIS}));
    }
}
