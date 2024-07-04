package com.gregtechceu.gtceu.common.data;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.GTCEuAPI;
import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.item.ComponentItem;
import com.gregtechceu.gtceu.api.item.component.ElectricStats;
import com.gregtechceu.gtceu.common.item.CoverPlaceBehavior;
import com.gregtechceu.gtceu.common.item.StructureWriteBehavior;
import com.gregtechceu.gtceu.common.item.TooltipBehavior;
import com.gregtechceu.gtceu.utils.TextUtil;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.model.generators.ConfiguredModel;

import appeng.api.stacks.AEKeyType;
import appeng.block.crafting.AbstractCraftingUnitBlock;
import appeng.block.crafting.CraftingUnitBlock;
import appeng.block.crafting.ICraftingUnitType;
import appeng.blockentity.AEBaseBlockEntity;
import appeng.blockentity.crafting.CraftingBlockEntity;
import appeng.core.definitions.ItemDefinition;
import appeng.items.materials.MaterialItem;
import appeng.items.materials.StorageComponentItem;
import appeng.items.storage.BasicStorageCell;
import com.tterrag.registrate.util.entry.BlockEntityEntry;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.entry.ItemEntry;
import lombok.Getter;

import static com.gregtechceu.gtceu.common.data.GTCreativeModeTabs.ITEM;
import static com.gregtechceu.gtceu.common.data.GTItems.*;
import static com.gregtechceu.gtceu.common.registry.GTRegistration.REGISTRATE;

public class AddItemsAndBlocks {

    public static void init() {}

    static {
        REGISTRATE.creativeModeTab(() -> ITEM);
    }

    private static ItemEntry<StorageComponentItem> registerStorageComponentItem(int tier) {
        return REGISTRATE
                .item("cell_component_" + tier + "m", p -> new StorageComponentItem(p, 1048576 * tier))
                .register();
    }

    private static ItemEntry<BasicStorageCell> registerStorageCell(int tier,
                                                                   ItemEntry<StorageComponentItem> StorageComponent,
                                                                   boolean isItem) {
        ItemDefinition<MaterialItem> CELL_HOUSING = isItem ? appeng.core.definitions.AEItems.ITEM_CELL_HOUSING :
                appeng.core.definitions.AEItems.FLUID_CELL_HOUSING;
        return REGISTRATE
                .item((isItem ? "item" : "fluid") + "_storage_cell_" + tier + "m", p -> new BasicStorageCell(
                        p.stacksTo(1),
                        StorageComponent,
                        CELL_HOUSING,
                        3 + 0.5 * Math.log(tier) / Math.log(4),
                        1024 * tier,
                        8192 * tier,
                        isItem ? 63 : 18,
                        isItem ? AEKeyType.items() : AEKeyType.fluids()))
                .register();
    }

    public enum CraftingUnitType implements ICraftingUnitType {

        STORAGE_1M(1, "1m_storage"),
        STORAGE_4M(4, "4m_storage"),
        STORAGE_16M(16, "16m_storage"),
        STORAGE_64M(64, "64m_storage"),
        STORAGE_256M(256, "256m_storage"),
        STORAGE_256G(262144, "256g_storage");

        private final int storageMb;
        @Getter
        private final String affix;

        CraftingUnitType(int storageMb, String affix) {
            this.storageMb = storageMb;
            this.affix = affix;
        }

        @Override
        public long getStorageBytes() {
            return 1024L * 1024 * storageMb;
        }

        @Override
        public int getAcceleratorThreads() {
            return 0;
        }

        public BlockEntry<CraftingUnitBlock> getDefinition() {
            return switch (this) {
                case STORAGE_1M -> CRAFTING_STORAGE_1M;
                case STORAGE_4M -> CRAFTING_STORAGE_4M;
                case STORAGE_16M -> CRAFTING_STORAGE_16M;
                case STORAGE_64M -> CRAFTING_STORAGE_64M;
                case STORAGE_256M -> CRAFTING_STORAGE_256M;
                case STORAGE_256G -> CRAFTING_STORAGE_256G;
            };
        }

        @Override
        public Item getItemFromType() {
            return getDefinition().asItem();
        }
    }

    private static BlockEntry<CraftingUnitBlock> registerCraftingUnitBlock(int tier, CraftingUnitType Type) {
        return REGISTRATE
                .block(tier == -1 ? "256g_storage" : tier + "m_storage",
                        p -> new CraftingUnitBlock(Type))
                .blockstate((ctx, provider) -> {
                    String formed = "block/crafting/" + ctx.getName() + "_formed";
                    String unformed = "block/crafting/" + ctx.getName();
                    provider.models().cubeAll(unformed, provider.modLoc("block/crafting/" + ctx.getName()));
                    provider.models().getBuilder(formed);
                    provider.getVariantBuilder(ctx.get())
                            .forAllStatesExcept(state -> {
                                boolean b = state.getValue(AbstractCraftingUnitBlock.FORMED);
                                return ConfiguredModel.builder()
                                        .modelFile(provider.models()
                                                .getExistingFile(provider.modLoc(b ? formed : unformed)))
                                        .build();
                            }, AbstractCraftingUnitBlock.POWERED);
                })
                .defaultLoot()
                .item(BlockItem::new)
                .model((ctx, provider) -> provider.withExistingParent(ctx.getName(),
                        provider.modLoc("block/crafting/" + ctx.getName())))
                .build()
                .register();
    }

    public static ItemEntry<ComponentItem> REALLY_ULTIMATE_BATTERY = REGISTRATE
            .item("really_max_battery", ComponentItem::create)
            .lang("Really Ultimate Battery")
            .onRegister(
                    attach(new TooltipBehavior(lines -> lines.add(Component.literal("§7填满就能通关GregTechCEu Modern")))))
            .onRegister(compassNodeExist(GTCompassSections.BATTERIES, "really_ultimate_battery"))
            .onRegister(modelPredicate(GTCEu.id("battery"), ElectricStats::getStoredPredicate))
            .onRegister(attach(ElectricStats.createRechargeableBattery(Long.MAX_VALUE, GTValues.UEV)))
            .register();
    public static ItemEntry<ComponentItem> TRANSCENDENT_ULTIMATE_BATTERY = REGISTRATE
            .item("transcendent_max_battery", ComponentItem::create)
            .lang("Transcendent Ultimate Battery")
            .onRegister(
                    attach(new TooltipBehavior(lines -> lines.add(Component.literal("§7填满就能通关GregTech Leisure")))))
            .onRegister(compassNodeExist(GTCompassSections.BATTERIES, "transcendent_ultimate_battery"))
            .onRegister(modelPredicate(GTCEu.id("battery"), ElectricStats::getStoredPredicate))
            .onRegister(attach(ElectricStats.createRechargeableBattery(Long.MAX_VALUE, GTValues.UIV)))
            .register();
    public static ItemEntry<ComponentItem> EXTREMELY_ULTIMATE_BATTERY = REGISTRATE
            .item("extremely_max_battery", ComponentItem::create)
            .lang("Extremely Ultimate Battery")
            .onRegister(
                    attach(new TooltipBehavior(lines -> lines.add(Component.literal("§7有生之年将它填满")))))
            .onRegister(compassNodeExist(GTCompassSections.BATTERIES, "extremely_ultimate_battery"))
            .onRegister(modelPredicate(GTCEu.id("battery"), ElectricStats::getStoredPredicate))
            .onRegister(attach(ElectricStats.createRechargeableBattery(Long.MAX_VALUE, GTValues.UXV)))
            .register();
    public static ItemEntry<ComponentItem> INSANELY_ULTIMATE_BATTERY = REGISTRATE
            .item("insanely_max_battery", ComponentItem::create)
            .lang("Insanely Ultimate Battery")
            .onRegister(
                    attach(new TooltipBehavior(
                            lines -> lines.add(Component.literal(TextUtil.dark_purplish_red("填满也就图一乐"))))))
            .onRegister(compassNodeExist(GTCompassSections.BATTERIES, "insanely_ultimate_battery"))
            .onRegister(modelPredicate(GTCEu.id("battery"), ElectricStats::getStoredPredicate))
            .onRegister(attach(ElectricStats.createRechargeableBattery(Long.MAX_VALUE, GTValues.OpV)))
            .register();
    public static ItemEntry<ComponentItem> MEGA_ULTIMATE_BATTERY = REGISTRATE
            .item("mega_max_battery", ComponentItem::create)
            .lang("Mega Ultimate Battery")
            .onRegister(
                    attach(new TooltipBehavior(
                            lines -> lines.add(Component.literal(TextUtil.full_color("填满电池 机械飞升"))))))
            .onRegister(compassNodeExist(GTCompassSections.BATTERIES, "mega_ultimate_battery"))
            .onRegister(modelPredicate(GTCEu.id("battery"), ElectricStats::getStoredPredicate))
            .onRegister(attach(ElectricStats.createRechargeableBattery(Long.MAX_VALUE, GTValues.MAX)))
            .register();

    public static ItemEntry<Item> ELECTRIC_MOTOR_MAX = GTCEuAPI.isHighTier() ?
            REGISTRATE.item("max_electric_motor", Item::new).lang("MAX Electric Motor")
                    .onRegister(compassNodeExist(GTCompassSections.COMPONENTS, "electric_motor")).register() :
            null;

    public static ItemEntry<ComponentItem> ELECTRIC_PUMP_MAX = GTCEuAPI.isHighTier() ?
            REGISTRATE.item("max_electric_pump", ComponentItem::create)
                    .lang("MAX Electric Pump")
                    .onRegister(attach(new CoverPlaceBehavior(GTCovers.PUMPS[13])))
                    .onRegister(attach(new TooltipBehavior(lines -> {
                        lines.add(Component.translatable("item.gtceu.electric.pump.tooltip"));
                        lines.add(Component.translatable("gtceu.universal.tooltip.fluid_transfer_rate",
                                1280 * 64 * 64 * 4 / 20));
                    })))
                    .onRegister(compassNodeExist(GTCompassSections.COVERS, "pump", GTCompassNodes.COVER))
                    .register() :
            null;

    public static ItemEntry<ComponentItem> CONVEYOR_MODULE_MAX = GTCEuAPI.isHighTier() ?
            REGISTRATE.item("max_conveyor_module", ComponentItem::create)
                    .lang("MAX Conveyor Module")
                    .onRegister(attach(new CoverPlaceBehavior(GTCovers.CONVEYORS[13])))
                    .onRegister(attach(new TooltipBehavior(lines -> {
                        lines.add(Component.translatable("item.gtceu.conveyor.module.tooltip"));
                        lines.add(Component.translatable("gtceu.universal.tooltip.item_transfer_rate_stacks", 16));
                    })))
                    .onRegister(compassNodeExist(GTCompassSections.COVERS, "conveyor", GTCompassNodes.COVER))
                    .register() :
            null;

    public static ItemEntry<Item> ELECTRIC_PISTON_MAX = GTCEuAPI.isHighTier() ?
            REGISTRATE.item("max_electric_piston", Item::new).lang("MAX Electric Piston")
                    .onRegister(compassNodeExist(GTCompassSections.COMPONENTS, "piston")).register() :
            null;

    public static ItemEntry<ComponentItem> ROBOT_ARM_MAX = GTCEuAPI.isHighTier() ?
            REGISTRATE.item("max_robot_arm", ComponentItem::create)
                    .lang("MAX Robot Arm")
                    .onRegister(attach(new CoverPlaceBehavior(GTCovers.ROBOT_ARMS[13])))
                    .onRegister(attach(new TooltipBehavior(lines -> {
                        lines.add(Component.translatable("item.gtceu.robot.arm.tooltip"));
                        lines.add(Component.translatable("gtceu.universal.tooltip.item_transfer_rate_stacks", 16));
                    })))
                    .onRegister(compassNodeExist(GTCompassSections.COVERS, "robot_arm", GTCompassNodes.COVER))
                    .register() :
            null;

    public static ItemEntry<Item> FIELD_GENERATOR_MAX = GTCEuAPI.isHighTier() ?
            REGISTRATE.item("max_field_generator", Item::new).lang("MAX Field Generator")
                    .onRegister(compassNodeExist(GTCompassSections.COMPONENTS, "field_generator")).register() :
            null;

    public static ItemEntry<Item> EMITTER_MAX = GTCEuAPI.isHighTier() ? REGISTRATE.item("max_emitter", Item::new)
            .lang("MAX Emitter").onRegister(compassNodeExist(GTCompassSections.COMPONENTS, "emitter")).register() :
            null;

    public static ItemEntry<Item> SENSOR_MAX = GTCEuAPI.isHighTier() ? REGISTRATE.item("max_sensor", Item::new)
            .lang("MAX Sensor").onRegister(compassNodeExist(GTCompassSections.COMPONENTS, "sensor")).register() : null;

    public static final ItemEntry<ComponentItem> DEBUG_STRUCTURE_WRITER = REGISTRATE
            .item("debug_structure_writer", ComponentItem::create)
            .onRegister(GTItems.attach(StructureWriteBehavior.INSTANCE))
            .register();

    public static final ItemEntry<StorageComponentItem> CELL_COMPONENT_1M = registerStorageComponentItem(1);
    public static final ItemEntry<StorageComponentItem> CELL_COMPONENT_4M = registerStorageComponentItem(4);
    public static final ItemEntry<StorageComponentItem> CELL_COMPONENT_16M = registerStorageComponentItem(16);
    public static final ItemEntry<StorageComponentItem> CELL_COMPONENT_64M = registerStorageComponentItem(64);
    public static final ItemEntry<StorageComponentItem> CELL_COMPONENT_256M = registerStorageComponentItem(256);

    public static final ItemEntry<BasicStorageCell> ITEM_CELL_1M = registerStorageCell(1, CELL_COMPONENT_1M, true);
    public static final ItemEntry<BasicStorageCell> ITEM_CELL_4M = registerStorageCell(4, CELL_COMPONENT_4M, true);
    public static final ItemEntry<BasicStorageCell> ITEM_CELL_16M = registerStorageCell(16, CELL_COMPONENT_16M, true);
    public static final ItemEntry<BasicStorageCell> ITEM_CELL_64M = registerStorageCell(64, CELL_COMPONENT_64M, true);
    public static final ItemEntry<BasicStorageCell> ITEM_CELL_256M = registerStorageCell(256, CELL_COMPONENT_256M,
            true);

    public static final ItemEntry<BasicStorageCell> FLUID_CELL_1M = registerStorageCell(1, CELL_COMPONENT_1M, false);
    public static final ItemEntry<BasicStorageCell> FLUID_CELL_4M = registerStorageCell(4, CELL_COMPONENT_4M, false);
    public static final ItemEntry<BasicStorageCell> FLUID_CELL_16M = registerStorageCell(16, CELL_COMPONENT_16M, false);
    public static final ItemEntry<BasicStorageCell> FLUID_CELL_64M = registerStorageCell(64, CELL_COMPONENT_64M, false);
    public static final ItemEntry<BasicStorageCell> FLUID_CELL_256M = registerStorageCell(256, CELL_COMPONENT_256M,
            false);

    public static final BlockEntry<CraftingUnitBlock> CRAFTING_STORAGE_1M = registerCraftingUnitBlock(1,
            CraftingUnitType.STORAGE_1M);
    public static final BlockEntry<CraftingUnitBlock> CRAFTING_STORAGE_4M = registerCraftingUnitBlock(4,
            CraftingUnitType.STORAGE_4M);
    public static final BlockEntry<CraftingUnitBlock> CRAFTING_STORAGE_16M = registerCraftingUnitBlock(16,
            CraftingUnitType.STORAGE_16M);
    public static final BlockEntry<CraftingUnitBlock> CRAFTING_STORAGE_64M = registerCraftingUnitBlock(64,
            CraftingUnitType.STORAGE_64M);
    public static final BlockEntry<CraftingUnitBlock> CRAFTING_STORAGE_256M = registerCraftingUnitBlock(256,
            CraftingUnitType.STORAGE_256M);
    public static final BlockEntry<CraftingUnitBlock> CRAFTING_STORAGE_256G = registerCraftingUnitBlock(-1,
            CraftingUnitType.STORAGE_256G);

    public static BlockEntityEntry<CraftingBlockEntity> CRAFTING_STORAGE = REGISTRATE
            .blockEntity("crafting_storage", CraftingBlockEntity::new)
            .validBlocks(
                    CRAFTING_STORAGE_1M,
                    CRAFTING_STORAGE_4M,
                    CRAFTING_STORAGE_16M,
                    CRAFTING_STORAGE_64M,
                    CRAFTING_STORAGE_256M,
                    CRAFTING_STORAGE_256G)
            .onRegister(type -> {
                for (CraftingUnitType craftingUnitType : CraftingUnitType.values()) {
                    AEBaseBlockEntity.registerBlockEntityItem(type, craftingUnitType.getItemFromType());
                    craftingUnitType.getDefinition().get().setBlockEntity(CraftingBlockEntity.class, type, null, null);
                }
            })
            .register();
}
