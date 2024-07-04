package com.gregtechceu.gtceu.common.machine.multiblock.generator;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.RecipeHelper;
import com.gregtechceu.gtceu.common.data.GTRecipeModifiers;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public class DysonSphere extends WorkableElectricMultiblockMachine {

    public DysonSphere(IMachineBlockEntity holder) {
        super(holder);
    }

    @Override
    public boolean onWorking() {
        boolean value = super.onWorking();
        if (getDysonSphereData() < 1000 && getRecipeLogic().getDuration() == 200 &&
                getRecipeLogic().getProgressPercent() == 0.9) {
            holder.self().getPersistentData().putInt("DysonSphereData", getDysonSphereData() + 1);
        }
        if (getRecipeLogic().getDuration() == 20 && getRecipeLogic().getProgressPercent() == 0.9 &&
                Math.random() < 0.01 * (1 + (double) getDysonSphereData() / 64) && getDysonSphereData() > 0) {
            if (getDysonSpheredamageData() == 1) {
                holder.self().getPersistentData().putInt("DysonSphereData", getDysonSphereData() - 1);
                holder.self().getPersistentData().putInt("DysonSpheredamageData", 100);
            } else {
                holder.self().getPersistentData().putInt("DysonSpheredamageData", getDysonSpheredamageData() - 1);
            }
        }
        return value;
    }

    @Override
    public boolean beforeWorking(@Nullable GTRecipe recipe) {
        BlockPos pos = getPos();
        Level level = getLevel();
        BlockPos[] coordinates = new BlockPos[] {
                pos.offset(4, 14, 0),
                pos.offset(-4, 14, 0),
                pos.offset(0, 14, 4),
                pos.offset(0, 14, -4) };
        for (BlockPos blockPos : coordinates) {
            if (Objects.equals(level.kjs$getBlock(blockPos).getId(), "kubejs:dyson_receiver_casing")) {
                for (int i = -6; i < 7; i++) {
                    for (int j = -6; j < 7; j++) {
                        if (i != 0 && j != 0 && level.kjs$getBlock(blockPos.offset(i, 1, j)).getSkyLight() == 0) {
                            getRecipeLogic().resetRecipeLogic();
                            return false;
                        }
                    }
                }
            }
        }
        if (recipe != null && RecipeHelper.getInputEUt(recipe) == GTValues.V[GTValues.UIV]) {
            return getDysonSphereData() < 1000;
        } else {
            return getDysonSphereData() > 0;
        }
    }

    private int getDysonSphereData() {
        return holder.self().getPersistentData().getInt("DysonSphereData");
    }

    private int getDysonSpheredamageData() {
        int DysonSpheredamageData = holder.self().getPersistentData().getInt("DysonSpheredamageData");
        if (DysonSpheredamageData == 0 && getDysonSphereData() > 0) {
            holder.self().getPersistentData().putInt("DysonSpheredamageData", 100);
        }
        return DysonSpheredamageData;
    }

    @Override
    public long getOverclockVoltage() {
        return GTValues.V[GTValues.UXV] * getDysonSphereData();
    }

    @Nullable
    public static GTRecipe recipeModifier(MetaMachine machine, @NotNull GTRecipe recipe) {
        if (machine instanceof DysonSphere engineMachine) {
            if (RecipeHelper.getOutputEUt(recipe) == GTValues.V[GTValues.UXV]) {
                int maxParallel = (int) Math.max(1,
                        engineMachine.getOverclockVoltage() / RecipeHelper.getOutputEUt(recipe));
                var parallelResult = GTRecipeModifiers.fastParallel(engineMachine, recipe, maxParallel, false);
                recipe = parallelResult.getFirst() == recipe ? recipe.copy() : parallelResult.getFirst();
                return recipe;
            }
            return recipe;
        }
        return null;
    }

    @Override
    public void addDisplayText(@NotNull List<Component> textList) {
        super.addDisplayText(textList);
        if (!this.isFormed) return;
        textList.add(Component.literal("发射次数：" + getDysonSphereData() + " / 1000"));
        textList.add(Component.literal("设备损坏度：" + (100 - getDysonSpheredamageData()) + " %"));
        textList.add(Component.literal("最大能量输出：").append(getDysonSphereData() > 0 ?
                Component.literal(getOverclockVoltage() + " EU/t") : Component.literal("0 EU/t")));
    }
}
