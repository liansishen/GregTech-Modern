package com.gregtechceu.gtceu.common.machine.multiblock.electric;

import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.recipe.ingredient.SizedIngredient;

import com.lowdragmc.lowdraglib.gui.util.ClickData;
import com.lowdragmc.lowdraglib.gui.widget.ComponentPanelWidget;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.util.FakePlayer;

import com.mojang.authlib.GameProfile;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Slaughterhouse extends WorkableElectricMultiblockMachine {

    String[] mobList;

    public Slaughterhouse(IMachineBlockEntity holder, String[] mobList) {
        super(holder);
        this.mobList = mobList;
    }

    private boolean getIsSpawn() {
        return holder.self().getPersistentData().getBoolean("IsSpawnData");
    }

    private void getItem(Level level, BlockPos Pos, Slaughterhouse machine) {
        Player fakePlayer = new FakePlayer((ServerLevel) level, new GameProfile(UUID.randomUUID(), "Slaughterhouse"));
        DamageSource Source = new DamageSource(level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE)
                .getHolderOrThrow(DamageTypes.GENERIC_KILL), fakePlayer);
        List<Entity> entities = level.getEntitiesOfClass(Entity.class, new AABB(
                Pos.getX() - 3,
                Pos.getY() - 1,
                Pos.getZ() - 3,
                Pos.getX() + 3,
                Pos.getY() + 6,
                Pos.getZ() + 3));
        for (Entity en : entities) {
            if (!en.kjs$isPlayer()) {
                if (en.kjs$isLiving()) {
                    en.hurt(Source, 10000);
                } else if (en instanceof ItemEntity itemEntity) {
                    ItemStack stack = itemEntity.getItem();
                    itemEntity.kill();
                    machine.output(false,
                            getContentBuilder().items(SizedIngredient.create(stack)).build());
                }
                en.kill();
            }
        }
    }

    @Override
    public boolean onWorking() {
        boolean value = super.onWorking();
        if (getRecipeLogic().getProgress() == 19) {
            BlockPos pos = getPos();
            Level level = getLevel();
            BlockPos[][] coordinates = new BlockPos[][] {
                    new BlockPos[] { pos.offset(1, 0, 0), pos.offset(3, 1, 0) },
                    new BlockPos[] { pos.offset(-1, 0, 0), pos.offset(-3, 1, 0) },
                    new BlockPos[] { pos.offset(0, 0, 1), pos.offset(0, 1, 3) },
                    new BlockPos[] { pos.offset(0, 0, -1), pos.offset(0, 1, -3) } };
            for (BlockPos[] blockPos : coordinates) {
                if (Objects.equals(level.kjs$getBlock(blockPos[0]).getId(), "gtceu:steel_gearbox")) {
                    BlockPos mobPos = blockPos[1];
                    if (getRecipeLogic().getMachine() instanceof Slaughterhouse wmachine) {
                        if (!getIsSpawn()) {
                            getItem(level, mobPos, wmachine);
                            for (int am = 0; am <= (getTier() - 2) * 8; am++) {
                                int index = (int) (Math.random() * mobList.length);
                                Objects.requireNonNull(level.getServer()).kjs$runCommandSilent("execute in " +
                                        level.kjs$getDimension().toString() + " run summon minecraft:" +
                                        mobList[index] + " " + mobPos.getX() + " " +
                                        (mobPos.getY() + 1) + " " + mobPos.getZ());
                            }
                        } else {
                            LootParams lootparams = new LootParams.Builder((ServerLevel) level)
                                    .create(LootContextParamSets.EMPTY);
                            getItem(level, mobPos, wmachine);
                            for (int am = 0; am <= (getTier() - 2) * 8; am++) {
                                int index = (int) (Math.random() * mobList.length);
                                ObjectArrayList<ItemStack> loottable = level.getServer().getLootData()
                                        .getLootTable(new ResourceLocation("minecraft:entities/" + mobList[index]))
                                        .getRandomItems(lootparams);
                                loottable.forEach(itemStack -> wmachine.output(false,
                                        getContentBuilder().items(SizedIngredient.create(itemStack)).build()));
                            }
                        }
                    }
                }
            }
        }
        return value;
    }

    @Override
    public void addDisplayText(@NotNull List<Component> textList) {
        super.addDisplayText(textList);
        if (!this.isFormed) return;
        textList.add(Component.literal("实体生成：")
                .append(ComponentPanelWidget.withButton(Component.literal("[")
                        .append(getIsSpawn() ?
                                Component.literal("关闭") :
                                Component.literal("打开"))
                        .append(Component.literal("]")), "spawn_switch")));
    }

    @Override
    public void handleDisplayClick(String componentData, ClickData clickData) {
        if (!clickData.isRemote) {
            if (componentData.equals("spawn_switch")) {
                holder.self().getPersistentData().putBoolean("IsSpawnData", !getIsSpawn());
            }
        }
    }
}
