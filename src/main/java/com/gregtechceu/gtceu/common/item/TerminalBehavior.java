package com.gregtechceu.gtceu.common.item;

import com.gregtechceu.gtceu.api.gui.GuiTextures;
import com.gregtechceu.gtceu.api.gui.widget.IntInputWidget;
import com.gregtechceu.gtceu.api.item.component.IItemUIFactory;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiController;
import com.gregtechceu.gtceu.config.ConfigHolder;

import com.lowdragmc.lowdraglib.gui.factory.HeldItemUIFactory;
import com.lowdragmc.lowdraglib.gui.modular.ModularUI;

import com.lowdragmc.lowdraglib.gui.widget.*;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

public class TerminalBehavior implements IItemUIFactory {

    @Override
    public InteractionResult useOn(UseOnContext context) {
        if (context.getPlayer() != null && context.getPlayer().isShiftKeyDown()) {
            Level level = context.getLevel();
            BlockPos blockPos = context.getClickedPos();
            if (context.getPlayer() != null &&
                    MetaMachine.getMachine(level, blockPos) instanceof IMultiController controller) {
                if (!controller.isFormed()) {
                    if (!level.isClientSide) {
                        controller.getPattern().autoBuild(context.getPlayer(), controller.getMultiblockState());
                    }
                    return InteractionResult.sidedSuccess(level.isClientSide);
                }
            }
        }
        return InteractionResult.PASS;
    }

    @Override
    public ModularUI createUI(HeldItemUIFactory.HeldItemHolder holder, Player entityPlayer) {
        var ui = new ModularUI(176, 166, holder, entityPlayer).widget(createWidget());
        return  ui;
//        return new ModularUI(holder, entityPlayer).widget(new CompassView(GTCEu.MOD_ID));
    }
    @Getter
    @Setter
    @Persisted
    private int casingTier;
    @Getter
    @Setter
    @Persisted
    private int energyTier;
    @Getter
    @Setter
    @Persisted
    private int maintenanceType;

    private Widget createWidget() {
        var group = new WidgetGroup(0, 0, 182 + 8, 117 + 8);
        group.addWidget(
                new DraggableScrollableWidgetGroup(4, 4, 182, 117)
                        .setBackground(GuiTextures.DISPLAY)
                        .addWidget(new LabelWidget(4, 5, "Terminal Setting"))
                        .addWidget(new LabelWidget(4,5 + 16,"Casing tier").setHoverTooltips("0 to 20"))
                        .addWidget(new IntInputWidget(100,5 + 16 - 4,60,16,this::getCasingTier,this::setCasingTier).setMin(0).setMax(20))
                        .addWidget(new LabelWidget(4,5 + 16*2,"Energy tier").setHoverTooltips("ULV(0) to MAX(14)"))
                        .addWidget(new IntInputWidget(100,5 + 16*2 - 4,60,16,this::getEnergyTier,this::setEnergyTier).setMin(0).setMax(14))
                        .addWidget(new LabelWidget(4,5 + 16*3,"Maintenance type").setHoverTooltips("normal:0,auto:1 etc"))
                        .addWidget(new IntInputWidget(100,5 + 16*3 - 4,60,16,this::getMaintenanceType,this::setMaintenanceType).setMin(0).setMax(14))
        );
        group.setBackground(GuiTextures.BACKGROUND_INVERSE);
        return group;
    }


    @Override
    public InteractionResultHolder<ItemStack> use(Item item, Level level, Player player, InteractionHand usedHand) {
        if (!ConfigHolder.INSTANCE.gameplay.enableCompass) {
            ItemStack heldItem = player.getItemInHand(usedHand);
            if (player instanceof ServerPlayer serverPlayer) {
                HeldItemUIFactory.INSTANCE.openUI(serverPlayer, usedHand);
            }
            //return InteractionResultHolder.pass(heldItem);
            return InteractionResultHolder.success(heldItem);
        }

        return IItemUIFactory.super.use(item, level, player, usedHand);
    }
}
