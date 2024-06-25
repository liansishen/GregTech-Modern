package com.gregtechceu.gtceu.common.item;

import com.gregtechceu.gtceu.api.GTCEuAPI;
import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.gui.GuiTextures;
import com.gregtechceu.gtceu.api.gui.widget.TerminalInputWidget;
import com.gregtechceu.gtceu.api.item.component.IItemUIFactory;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiController;
import com.gregtechceu.gtceu.config.ConfigHolder;

import com.lowdragmc.lowdraglib.gui.editor.ColorPattern;
import com.lowdragmc.lowdraglib.gui.factory.HeldItemUIFactory;
import com.lowdragmc.lowdraglib.gui.modular.ModularUI;
import com.lowdragmc.lowdraglib.gui.widget.*;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;

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

import lombok.Getter;
import lombok.Setter;

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
                        controller.getPattern().autoBuild(context.getPlayer(), controller.getMultiblockState(), this);
                    }
                    return InteractionResult.sidedSuccess(level.isClientSide);
                }
            }
        }
        return InteractionResult.PASS;
    }

    @Override
    public ModularUI createUI(HeldItemUIFactory.HeldItemHolder holder, Player entityPlayer) {
        return new ModularUI(176, 166, holder, entityPlayer).widget(createWidget());
    }

    @Getter
    @Setter
    @Persisted
    private int coilTier;
    @Getter
    @Setter
    @Persisted
    private int energyTier;
    @Getter
    @Setter
    @Persisted
    private int maintenanceType;
    @Getter
    @Setter
    @Persisted
    private int repeatCount;
    @Getter
    @Setter
    @Persisted
    private int parallerTier;
    @Getter
    @Setter
    @Persisted
    private int inputBusTier;
    @Getter
    @Setter
    @Persisted
    private int inputHatchTier;
    @Getter
    @Setter
    @Persisted
    private int outputBusTier;
    @Getter
    @Setter
    @Persisted
    private int outputHatchTier;

    private Widget createWidget() {
        var group = new WidgetGroup(0, 0, 182 + 8, 117 + 8);
        group.addWidget(
                new DraggableScrollableWidgetGroup(4, 4, 182, 117)
                        .setBackground(GuiTextures.DISPLAY)
                        .setYScrollBarWidth(2)
                        .setYBarStyle(null, ColorPattern.T_WHITE.rectTexture().setRadius(1))
                        .addWidget(new LabelWidget(40, 5, "Terminal auto build Setting"))
                        .addWidget(new LabelWidget(4, 5 + 16, "Coil Tier")
                                .setHoverTooltips("Set the Coil tier"))
                        .addWidget(new TerminalInputWidget(140, 5 + 16, 20, 16, this::getCoilTier, this::setCoilTier)
                                .setMin(0).setMax(GTCEuAPI.HEATING_COILS.size()-1))
                        .addWidget(new LabelWidget(4, 5 + 16 * 2, "Energy Hatch Tier")
                                .setHoverTooltips("Set the tier fot Energy Hatch.From ULV(0) to " +
                                        (GTCEuAPI.isHighTier() ? "MAX(14)" : "UV(8)")))
                        .addWidget(new TerminalInputWidget(140, 5 + 16 * 2, 20, 16, this::getEnergyTier,
                                this::setEnergyTier)
                                .setMin(0).setMax(GTCEuAPI.isHighTier() ? GTValues.MAX : GTValues.UV))
                        .addWidget(new LabelWidget(4, 5 + 16 * 3, "Maintenance Hatch Type")
                                .setHoverTooltips("0:Maintenance Hatch"))
                        .addWidget(new TerminalInputWidget(140, 5 + 16 * 3, 20, 16, this::getMaintenanceType,
                                this::setMaintenanceType)
                                .setMin(0).setMax(3))
                        .addWidget(new LabelWidget(4, 5 + 16 * 4, "Repeat Count")
                                .setHoverTooltips("Repeatable multiblock"))
                        .addWidget(new TerminalInputWidget(140, 5 + 16 * 4, 20, 16, this::getRepeatCount,
                                this::setRepeatCount)
                                .setMin(0).setMax(99))
                        .addWidget(new LabelWidget(4, 5 + 16 * 5, "Parallel Hatch Tier")
                                .setHoverTooltips("From IV(5) to UV(8)"))
                        .addWidget(new TerminalInputWidget(140, 5 + 16 * 5, 20, 16, this::getParallerTier,
                                this::setParallerTier)
                                .setMin(5).setMax(8))
                        .addWidget(new LabelWidget(4, 5 + 16 * 6, "Input Bus Tier")
                                .setHoverTooltips("Set the tier of Input Bus.From ULV(0) to " +
                                        (GTCEuAPI.isHighTier() ? "MAX(14)" : "UV(8)")))
                        .addWidget(new TerminalInputWidget(140, 5 + 16 * 6, 20, 16, this::getInputBusTier,
                                this::setInputBusTier)
                                .setMin(0).setMax(GTCEuAPI.isHighTier() ? GTValues.MAX : GTValues.UV))
                        .addWidget(new LabelWidget(4, 5 + 16 * 7, "Output Bus Tier")
                                .setHoverTooltips("Set the tier of Output Bus.From ULV(0) to " +
                                        (GTCEuAPI.isHighTier() ? "MAX(14)" : "UV(8)")))
                        .addWidget(new TerminalInputWidget(140, 5 + 16 * 7, 20, 16, this::getOutputBusTier,
                                this::setOutputBusTier)
                                .setMin(0).setMax(GTCEuAPI.isHighTier() ? GTValues.MAX : GTValues.UV))
                        .addWidget(new LabelWidget(4, 5 + 16 * 8, "Input Hatch Tier")
                                .setHoverTooltips("Set the tier of Input Hatch.From ULV(0) to " +
                                        (GTCEuAPI.isHighTier() ? "MAX(14)" : "UV(8)")))
                        .addWidget(new TerminalInputWidget(140, 5 + 16 * 8, 20, 16, this::getInputHatchTier,
                                this::setInputHatchTier)
                                .setMin(0).setMax(GTCEuAPI.isHighTier() ? GTValues.MAX : GTValues.UV))
                        .addWidget(new LabelWidget(4, 5 + 16 * 9, "Output Hatch Tier")
                                .setHoverTooltips("Set the tier of Output Hatch.From ULV(0) to " +
                                        (GTCEuAPI.isHighTier() ? "MAX(14)" : "UV(8)")))
                        .addWidget(new TerminalInputWidget(140, 5 + 16 * 9, 20, 16, this::getOutputHatchTier,
                                this::setOutputHatchTier)
                                .setMin(0).setMax(GTCEuAPI.isHighTier() ? GTValues.MAX : GTValues.UV)));
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
            // return InteractionResultHolder.pass(heldItem);
            return InteractionResultHolder.success(heldItem);
        }

        return IItemUIFactory.super.use(item, level, player, usedHand);
    }
}
