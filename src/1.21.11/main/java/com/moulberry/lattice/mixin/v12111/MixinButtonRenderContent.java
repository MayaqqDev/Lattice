package com.moulberry.lattice.mixin.v12111;

import com.moulberry.lattice.widget.DropdownWidget;
import com.moulberry.lattice.widget.KeybindButton;
import com.moulberry.lattice.widget.SubcategoryButton;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({KeybindButton.class, SubcategoryButton.class, DropdownWidget.class})
public class MixinButtonRenderContent extends Button {

    protected MixinButtonRenderContent(int $$0, int $$1, int $$2, int $$3, Component $$4, OnPress $$5, CreateNarration $$6) {
        super($$0, $$1, $$2, $$3, $$4, $$5, $$6);
    }

    public void renderContents(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderDefaultSprite(guiGraphics);
        this.renderDefaultLabel(guiGraphics.textRendererForWidget(this, GuiGraphics.HoveredTextEffects.NONE));
    }

}
