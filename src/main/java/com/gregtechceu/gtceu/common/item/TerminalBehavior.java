package com.gregtechceu.gtceu.common.item;

import com.gregtechceu.gtceu.api.GTCEuAPI;
import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.gui.GuiTextures;
import com.gregtechceu.gtceu.api.gui.widget.TerminalInputWidget;
import com.gregtechceu.gtceu.api.item.component.IItemUIFactory;
import com.gregtechceu.gtceu.api.machine.MachineDefinition;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiController;
import com.gregtechceu.gtceu.common.block.CoilBlock;
import com.gregtechceu.gtceu.config.ConfigHolder;

import com.lowdragmc.lowdraglib.gui.editor.ColorPattern;
import com.lowdragmc.lowdraglib.gui.factory.HeldItemUIFactory;
import com.lowdragmc.lowdraglib.gui.modular.ModularUI;
import com.lowdragmc.lowdraglib.gui.widget.*;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.utils.BlockInfo;

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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.gregtechceu.gtceu.common.data.GTBlocks.*;
import static com.gregtechceu.gtceu.common.data.GTBlocks.COIL_TRITANIUM;
import static com.gregtechceu.gtceu.common.data.GTMachines.*;
import static com.gregtechceu.gtceu.common.data.GTMachines.AUTO_MAINTENANCE_HATCH;
import static com.gregtechceu.gtceu.common.data.machines.GCyMMachines.PARALLEL_HATCH;

public class TerminalBehavior implements IItemUIFactory {

    @Persisted
    private AutoBuildSetting autoBuildSetting;

    private final MachineDefinition[] MAINTENANCE_HATCHS = { MAINTENANCE_HATCH, CONFIGURABLE_MAINTENANCE_HATCH,
            CLEANING_MAINTENANCE_HATCH, AUTO_MAINTENANCE_HATCH };
    private final CoilBlock[] COIL_BLOCKS = { COIL_CUPRONICKEL.get(), COIL_KANTHAL.get(), COIL_NICHROME.get(),
            COIL_RTMALLOY.get(), COIL_HSSG.get(), COIL_NAQUADAH.get(), COIL_TRINIUM.get(), COIL_TRITANIUM.get() };

    public TerminalBehavior() {
        autoBuildSetting = new AutoBuildSetting();
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        if (context.getPlayer() != null && context.getPlayer().isShiftKeyDown()) {
            Level level = context.getLevel();
            BlockPos blockPos = context.getClickedPos();
            if (context.getPlayer() != null &&
                    MetaMachine.getMachine(level, blockPos) instanceof IMultiController controller) {
                if (!controller.isFormed()) {
                    if (!level.isClientSide) {
                        controller.getPattern().autoBuild(context.getPlayer(), controller.getMultiblockState(),
                                autoBuildSetting);
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
                        .addWidget(new TerminalInputWidget(140, 5 + 16, 20, 16, autoBuildSetting::getCoilTier,
                                autoBuildSetting::setCoilTier)
                                .setMin(0).setMax(GTCEuAPI.HEATING_COILS.size() - 1))
                        .addWidget(new LabelWidget(4, 5 + 16 * 2, "Energy Hatch Tier")
                                .setHoverTooltips("Set the tier fot Energy Hatch.From ULV(0) to " +
                                        (GTCEuAPI.isHighTier() ? "MAX(14)" : "UV(8)")))
                        .addWidget(new TerminalInputWidget(140, 5 + 16 * 2, 20, 16, autoBuildSetting::getEnergyTier,
                                autoBuildSetting::setEnergyTier)
                                .setMin(0).setMax(GTCEuAPI.isHighTier() ? GTValues.MAX : GTValues.UV))
                        .addWidget(new LabelWidget(4, 5 + 16 * 3, "Maintenance Hatch Type")
                                .setHoverTooltips("0:Maintenance Hatch"))
                        .addWidget(
                                new TerminalInputWidget(140, 5 + 16 * 3, 20, 16, autoBuildSetting::getMaintenanceType,
                                        autoBuildSetting::setMaintenanceType)
                                        .setMin(0).setMax(3))
                        .addWidget(new LabelWidget(4, 5 + 16 * 4, "Repeat Count")
                                .setHoverTooltips("Repeatable multiblock"))
                        .addWidget(new TerminalInputWidget(140, 5 + 16 * 4, 20, 16, autoBuildSetting::getRepeatCount,
                                autoBuildSetting::setRepeatCount)
                                .setMin(0).setMax(99))
                        .addWidget(new LabelWidget(4, 5 + 16 * 5, "Parallel Hatch Tier")
                                .setHoverTooltips("From IV(5) to UV(8)"))
                        .addWidget(new TerminalInputWidget(140, 5 + 16 * 5, 20, 16, autoBuildSetting::getParallerTier,
                                autoBuildSetting::setParallerTier)
                                .setMin(5).setMax(8))
                        .addWidget(new LabelWidget(4, 5 + 16 * 6, "Input Bus Tier")
                                .setHoverTooltips("Set the tier of Input Bus.From ULV(0) to " +
                                        (GTCEuAPI.isHighTier() ? "MAX(14)" : "UV(8)")))
                        .addWidget(new TerminalInputWidget(140, 5 + 16 * 6, 20, 16, autoBuildSetting::getInputBusTier,
                                autoBuildSetting::setInputBusTier)
                                .setMin(0).setMax(GTCEuAPI.isHighTier() ? GTValues.MAX : GTValues.UV))
                        .addWidget(new LabelWidget(4, 5 + 16 * 7, "Output Bus Tier")
                                .setHoverTooltips("Set the tier of Output Bus.From ULV(0) to " +
                                        (GTCEuAPI.isHighTier() ? "MAX(14)" : "UV(8)")))
                        .addWidget(new TerminalInputWidget(140, 5 + 16 * 7, 20, 16, autoBuildSetting::getOutputBusTier,
                                autoBuildSetting::setOutputBusTier)
                                .setMin(0).setMax(GTCEuAPI.isHighTier() ? GTValues.MAX : GTValues.UV))
                        .addWidget(new LabelWidget(4, 5 + 16 * 8, "Input Hatch Tier")
                                .setHoverTooltips("Set the tier of Input Hatch.From ULV(0) to " +
                                        (GTCEuAPI.isHighTier() ? "MAX(14)" : "UV(8)")))
                        .addWidget(new TerminalInputWidget(140, 5 + 16 * 8, 20, 16, autoBuildSetting::getInputHatchTier,
                                autoBuildSetting::setInputHatchTier)
                                .setMin(0).setMax(GTCEuAPI.isHighTier() ? GTValues.MAX : GTValues.UV))
                        .addWidget(new LabelWidget(4, 5 + 16 * 9, "Output Hatch Tier")
                                .setHoverTooltips("Set the tier of Output Hatch.From ULV(0) to " +
                                        (GTCEuAPI.isHighTier() ? "MAX(14)" : "UV(8)")))
                        .addWidget(
                                new TerminalInputWidget(140, 5 + 16 * 9, 20, 16, autoBuildSetting::getOutputHatchTier,
                                        autoBuildSetting::setOutputHatchTier)
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

    public class AutoBuildSetting {

        @Getter
        @Setter
        private int coilTier, energyTier, maintenanceType, repeatCount, parallerTier, inputBusTier, inputHatchTier,
                outputBusTier, outputHatchTier;

        public AutoBuildSetting() {
            this.coilTier = 0;
            this.energyTier = 0;
            this.maintenanceType = 0;
            this.repeatCount = 0;
            this.parallerTier = 5;
            this.inputBusTier = 0;
            this.inputHatchTier = 0;
            this.outputBusTier = 0;
            this.outputHatchTier = 0;
        }

        public List<ItemStack> apply(BlockInfo[] blockInfos) {
            List<ItemStack> candidates = new ArrayList<>();
            if (blockInfos != null) {
                var blockInfo = Arrays.stream(blockInfos).filter(info -> info.getBlockState().getBlock() == ENERGY_INPUT_HATCH[energyTier].getBlock()).findFirst();
                if (blockInfo.isPresent()) {
                    candidates.add(blockInfo.get().getItemStackForm());
                    return candidates;
                }
                if (Arrays.stream(blockInfos).anyMatch(
                        info -> info.getBlockState().getBlock() == ENERGY_INPUT_HATCH[energyTier].getBlock())) {
                    candidates.add(new BlockInfo(ENERGY_INPUT_HATCH[energyTier].getBlock()).getItemStackForm());
                    return candidates;
                }
                if (Arrays.stream(blockInfos)
                        .anyMatch(info -> info.getBlockState().getBlock() == PARALLEL_HATCH[parallerTier].getBlock())) {
                    candidates.add(new BlockInfo(PARALLEL_HATCH[parallerTier].getBlock()).getItemStackForm());
                    return candidates;
                }
                for (BlockInfo info : blockInfos) {
                    candidates.add(info.getItemStackForm());
                }

                // for (BlockInfo info : blockInfos) {
                // var block = info.getBlockState().getBlock();
                // if (Arrays.stream(COIL_BLOCKS).anyMatch(obj -> obj == block) && coilTier > 0) {
                // info = new BlockInfo(COIL_BLOCKS[coilTier]);
                // candidates.add(info.getItemStackForm());
                // break;
                // }
                // if (Arrays.stream(ENERGY_INPUT_HATCH).anyMatch(
                // hatch -> hatch.getBlock() == block) && energyTier > 0) {
                // info = new BlockInfo(ENERGY_INPUT_HATCH[energyTier].getBlock());
                // candidates.add(info.getItemStackForm());
                // break;
                // }
                // if (Arrays.stream(PARALLEL_HATCH).anyMatch(
                // hatch -> (hatch != null) && (hatch.getBlock() == block)) && parallerTier > 5) {
                // info = new BlockInfo(PARALLEL_HATCH[parallerTier].getBlock());
                // candidates.add(info.getItemStackForm());
                // break;
                // }
                // if (Arrays.stream(MAINTENANCE_HATCHS)
                // .anyMatch(hatch -> hatch.getBlock() == block) && maintenanceType > 0) {
                // info = new BlockInfo(MAINTENANCE_HATCHS[maintenanceType].getBlock());
                // candidates.add(info.getItemStackForm());
                // break;
                // }
                // if (Arrays.stream(ITEM_IMPORT_BUS)
                // .anyMatch(bus -> bus.getBlock() == block) && inputBusTier > 0) {
                // info = new BlockInfo(ITEM_IMPORT_BUS[inputBusTier].getBlock());
                // candidates.add(info.getItemStackForm());
                // break;
                // }
                // if (Arrays.stream(ITEM_EXPORT_BUS)
                // .anyMatch(bus -> bus.getBlock() == block) && outputBusTier > 0) {
                // info = new BlockInfo(ITEM_EXPORT_BUS[outputBusTier].getBlock());
                // candidates.add(info.getItemStackForm());
                // break;
                // }
                // if (Arrays.stream(FLUID_IMPORT_HATCH)
                // .anyMatch(bus -> bus.getBlock() == block) && inputHatchTier > 0) {
                // info = new BlockInfo(FLUID_IMPORT_HATCH[inputHatchTier].getBlock());
                // candidates.add(info.getItemStackForm());
                // break;
                // }
                // if (Arrays.stream(FLUID_EXPORT_HATCH)
                // .anyMatch(bus -> bus.getBlock() == block) && outputHatchTier > 0) {
                // info = new BlockInfo(FLUID_EXPORT_HATCH[outputHatchTier].getBlock());
                // candidates.add(info.getItemStackForm());
                // break;
                // }
                // // if (block != Blocks.AIR) {
                // //
                // // }
                // }
            }
            return candidates;
        }
    }
}
