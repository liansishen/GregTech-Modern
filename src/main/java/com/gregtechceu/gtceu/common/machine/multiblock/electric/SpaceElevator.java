package com.gregtechceu.gtceu.common.machine.multiblock.electric;

import com.gregtechceu.gtceu.api.capability.GTCapabilityHelper;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.machine.trait.RecipeLogic;

import com.lowdragmc.lowdraglib.gui.util.ClickData;
import com.lowdragmc.lowdraglib.gui.widget.ComponentPanelWidget;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

public class SpaceElevator extends WorkableElectricMultiblockMachine {

    public SpaceElevator(IMachineBlockEntity holder) {
        super(holder);
    }

    private int mam = 0;

    private BlockPos getPowerCore(BlockPos pos, Level level) {
        BlockPos[] coordinates = new BlockPos[] { pos.offset(3, -2, 0),
                pos.offset(-3, -2, 0),
                pos.offset(0, -2, 3),
                pos.offset(0, -2, -3) };
        for (BlockPos blockPos : coordinates) {
            if (Objects.equals(level.kjs$getBlock(blockPos).getId(), "gtceu:power_core")) {
                return blockPos;
            }
        }
        return null;
    }

    private int getMAM() {
        if (getOffsetTimer() % 20 == 0) {
            Level level = getLevel();
            BlockPos blockPos = getPowerCore(getPos(), level);
            if (blockPos != null) {
                BlockPos[] coordinatess = new BlockPos[] { blockPos.offset(8, 2, 3),
                        blockPos.offset(8, 2, -3),
                        blockPos.offset(-8, 2, 3),
                        blockPos.offset(-8, 2, -3),
                        blockPos.offset(3, 2, 8),
                        blockPos.offset(-3, 2, 8),
                        blockPos.offset(3, 2, -8),
                        blockPos.offset(-3, 2, -8) };
                mam = 0;
                for (BlockPos blockPoss : coordinatess) {
                    RecipeLogic logic = GTCapabilityHelper.getRecipeLogic(level, blockPoss, null);
                    if (logic != null && logic.machine instanceof SpaceElevator mbmachine &&
                            mbmachine.isFormed()) {
                        String bid = mbmachine.getBlockState().getBlock().kjs$getId();
                        if (bid.equals("gtceu:assembler_module") || bid.equals("gtceu:resource_collection")) {
                            mam++;
                        }
                    }
                }
                return mam;
            }
        }
        return mam;
    }

    @Override
    public boolean onWorking() {
        boolean value = super.onWorking();
        if (getOffsetTimer() % 20 == 0) {
            if (getRecipeLogic().getProgress() > 240) {
                getRecipeLogic().setProgress(120);
            }
            Level level = getLevel();
            BlockPos blockPos = getPowerCore(getPos(), level);
            if (blockPos != null) {
                BlockPos pos = blockPos.offset(0, 140, 0);
                Objects.requireNonNull(level.getServer()).kjs$runCommandSilent("execute in " +
                        level.kjs$getDimension().toString() + " run particle minecraft:end_rod " +
                        pos.getX() + " " + pos.getY() + " " + pos.getZ() + " 0.1 40 0.1 0.001 10000 force");
            }
        }
        return value;
    }

    @Override
    public void addDisplayText(@NotNull List<Component> textList) {
        super.addDisplayText(textList);
        if (!this.isFormed) return;
        textList.add(Component.literal("已安装的模块数量：" + getMAM()));
        textList.add(
                ComponentPanelWidget.withButton(Component.literal("启程"), "set_out"));
    }

    @Override
    public void handleDisplayClick(String componentData, ClickData clickData) {
        if (componentData.equals("set_out") && getRecipeLogic().isWorking()) {
            BlockPos pos = getPos();
            List<Entity> entities = getLevel().getEntitiesOfClass(Entity.class, new AABB(pos.getX() - 2,
                    pos.getY() - 2,
                    pos.getZ() - 2,
                    pos.getX() + 2,
                    pos.getY() + 2,
                    pos.getZ() + 2));
            for (Entity en : entities) {
                if (en != null) {
                    if (en instanceof Player pr) {
                        pr.kjs$sendData("gt.se.st");
                    }
                }
            }
        }
    }
}
