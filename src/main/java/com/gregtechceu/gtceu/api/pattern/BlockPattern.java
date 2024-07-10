package com.gregtechceu.gtceu.api.pattern;

import com.gregtechceu.gtceu.api.block.ActiveBlock;
import com.gregtechceu.gtceu.api.block.MetaMachineBlock;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiController;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiPart;
import com.gregtechceu.gtceu.api.pattern.error.PatternError;
import com.gregtechceu.gtceu.api.pattern.error.PatternStringError;
import com.gregtechceu.gtceu.api.pattern.error.SinglePredicateError;
import com.gregtechceu.gtceu.api.pattern.predicates.SimplePredicate;
import com.gregtechceu.gtceu.api.pattern.util.PatternMatchContext;
import com.gregtechceu.gtceu.api.pattern.util.RelativeDirection;
import com.gregtechceu.gtceu.common.item.TerminalBehavior;

import com.lowdragmc.lowdraglib.utils.BlockInfo;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Array;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Consumer;

import static com.gregtechceu.gtceu.common.data.GTBlocks.*;
import static com.gregtechceu.gtceu.common.data.GTMachines.*;

public class BlockPattern {

    static Direction[] FACINGS = { Direction.SOUTH, Direction.NORTH, Direction.WEST, Direction.EAST, Direction.UP,
            Direction.DOWN };
    static Direction[] FACINGS_H = { Direction.SOUTH, Direction.NORTH, Direction.WEST, Direction.EAST };
    public final int[][] aisleRepetitions;
    public final RelativeDirection[] structureDir;
    protected final TraceabilityPredicate[][][] blockMatches; // [z][y][x]
    protected final int fingerLength; // z size
    protected final int thumbLength; // y size
    protected final int palmLength; // x size
    protected final int[] centerOffset; // x, y, z, minZ, maxZ

    public BlockPattern(TraceabilityPredicate[][][] predicatesIn, RelativeDirection[] structureDir,
                        int[][] aisleRepetitions, int[] centerOffset) {
        this.blockMatches = predicatesIn;
        this.fingerLength = predicatesIn.length;
        this.structureDir = structureDir;
        this.aisleRepetitions = aisleRepetitions;

        if (this.fingerLength > 0) {
            this.thumbLength = predicatesIn[0].length;

            if (this.thumbLength > 0) {
                this.palmLength = predicatesIn[0][0].length;
            } else {
                this.palmLength = 0;
            }
        } else {
            this.thumbLength = 0;
            this.palmLength = 0;
        }

        this.centerOffset = centerOffset;
    }

    public boolean checkPatternAt(MultiblockState worldState, boolean savePredicate) {
        IMultiController controller = worldState.getController();
        if (controller == null) {
            worldState.setError(new PatternStringError("no controller found"));
            return false;
        }
        BlockPos centerPos = controller.self().getPos();
        Direction frontFacing = controller.self().getFrontFacing();
        Direction[] facings = controller.hasFrontFacing() ? new Direction[] { frontFacing } :
                new Direction[] { Direction.SOUTH, Direction.NORTH, Direction.EAST, Direction.WEST };
        Direction upwardsFacing = controller.self().getUpwardsFacing();
        boolean allowsFlip = controller.self().allowFlip();
        for (Direction direction : facings) {
            boolean result = checkPatternAt(worldState, centerPos, direction, upwardsFacing, false, savePredicate);
            if (result) {
                return true;
            } else if (allowsFlip) {
                return checkPatternAt(worldState, centerPos, direction, upwardsFacing, true, savePredicate);
            }
        }
        return false;
    }

    public boolean checkPatternAt(MultiblockState worldState, BlockPos centerPos, Direction frontFacing,
                                  Direction upwardsFacing, boolean isFlipped, boolean savePredicate) {
        boolean findFirstAisle = false;
        int minZ = -centerOffset[4];
        worldState.clean();
        PatternMatchContext matchContext = worldState.getMatchContext();
        Map<SimplePredicate, Integer> globalCount = worldState.getGlobalCount();
        Map<SimplePredicate, Integer> layerCount = worldState.getLayerCount();
        // Checking aisles
        for (int c = 0, z = minZ++, r; c < this.fingerLength; c++) {
            // Checking repeatable slices
            loop:
            for (r = 0; (findFirstAisle ? r < aisleRepetitions[c][1] : z <= -centerOffset[3]); r++) {
                // Checking single slice
                layerCount.clear();

                for (int b = 0, y = -centerOffset[1]; b < this.thumbLength; b++, y++) {
                    for (int a = 0, x = -centerOffset[0]; a < this.palmLength; a++, x++) {
                        worldState.setError(null);
                        TraceabilityPredicate predicate = this.blockMatches[c][b][a];
                        BlockPos pos = setActualRelativeOffset(x, y, z, frontFacing, upwardsFacing, isFlipped)
                                .offset(centerPos.getX(), centerPos.getY(), centerPos.getZ());
                        if (!worldState.update(pos, predicate)) {
                            return false;
                        }
                        if (predicate.addCache()) {
                            worldState.addPosCache(pos);
                            if (savePredicate) {
                                matchContext.getOrCreate("predicates", HashMap::new).put(pos, predicate);
                            }
                        }
                        boolean canPartShared = true;
                        if (worldState.getTileEntity() instanceof IMachineBlockEntity machineBlockEntity &&
                                machineBlockEntity.getMetaMachine() instanceof IMultiPart part) { // add detected parts
                            if (!predicate.isAny()) {
                                if (part.isFormed() && !part.canShared() &&
                                        !part.hasController(worldState.controllerPos)) { // check part can be shared
                                    canPartShared = false;
                                    worldState.setError(new PatternStringError("multiblocked.pattern.error.share"));
                                } else {
                                    matchContext.getOrCreate("parts", HashSet::new).add(part);
                                }
                            }
                        }
                        if (worldState.getBlockState().getBlock() instanceof ActiveBlock) {
                            matchContext.getOrCreate("vaBlocks", LongOpenHashSet::new)
                                    .add(worldState.getPos().asLong());
                        }
                        if (!predicate.test(worldState) || !canPartShared) { // matching failed
                            if (findFirstAisle) {
                                if (r < aisleRepetitions[c][0]) {// retreat to see if the first aisle can start later
                                    r = c = 0;
                                    z = minZ++;
                                    matchContext.reset();
                                    findFirstAisle = false;
                                }
                            } else {
                                z++;// continue searching for the first aisle
                            }
                            continue loop;
                        }
                        matchContext.getOrCreate("ioMap", Long2ObjectOpenHashMap::new).put(worldState.getPos().asLong(),
                                worldState.io);
                    }
                }
                findFirstAisle = true;
                z++;

                // Check layer-local matcher predicate
                for (Map.Entry<SimplePredicate, Integer> entry : layerCount.entrySet()) {
                    if (entry.getValue() < entry.getKey().minLayerCount) {
                        worldState.setError(new SinglePredicateError(entry.getKey(), 3));
                        return false;
                    }
                }
            }
            // Repetitions out of range
            if (r < aisleRepetitions[c][0] || worldState.hasError() || !findFirstAisle) {
                if (!worldState.hasError()) {
                    worldState.setError(new PatternError());
                }
                return false;
            }
        }

        // Check count matches amount
        for (Map.Entry<SimplePredicate, Integer> entry : globalCount.entrySet()) {
            if (entry.getValue() < entry.getKey().minCount) {
                worldState.setError(new SinglePredicateError(entry.getKey(), 1));
                return false;
            }
        }

        worldState.setError(null);
        worldState.setNeededFlip(isFlipped);
        return true;
    }

    public void autoBuild(Player player, MultiblockState worldState,
                          TerminalBehavior.AutoBuildSetting autoBuildSetting) {
        Level world = player.level();
        int minZ = -centerOffset[4];
        worldState.clean();
        IMultiController controller = worldState.getController();
        BlockPos centerPos = controller.self().getPos();
        Direction facing = controller.self().getFrontFacing();
        Direction upwardsFacing = controller.self().getUpwardsFacing();
        boolean isFlipped = controller.self().isFlipped();
        Map<SimplePredicate, Integer> cacheGlobal = worldState.getGlobalCount();
        Map<SimplePredicate, Integer> cacheLayer = worldState.getLayerCount();
        Map<BlockPos, Object> blocks = new HashMap<>();
        Set<BlockPos> placeBlockPos = new HashSet<>();
        blocks.put(centerPos, controller);

        int[] repeat = new int[this.fingerLength];
        for (int h = 0; h < this.fingerLength; h++) {
            var minH = aisleRepetitions[h][0];
            var maxH = aisleRepetitions[h][1];
            if (minH != maxH) {
                repeat[h] = Math.max(minH, Math.min(maxH, autoBuildSetting.getRepeatCount()));
            } else {
                repeat[h] = minH;
            }
        }

        for (int c = 0, z = minZ++, r; c < this.fingerLength; c++) {
            for (r = 0; r < repeat[c]; r++) {
                cacheLayer.clear();
                for (int b = 0, y = -centerOffset[1]; b < this.thumbLength; b++, y++) {
                    for (int a = 0, x = -centerOffset[0]; a < this.palmLength; a++, x++) {
                        TraceabilityPredicate predicate = this.blockMatches[c][b][a];
                        BlockPos pos = setActualRelativeOffset(x, y, z, facing, upwardsFacing, isFlipped)
                                .offset(centerPos.getX(), centerPos.getY(), centerPos.getZ());
                        worldState.update(pos, predicate);
                        if (!world.isEmptyBlock(pos)) {
                            blocks.put(pos, world.getBlockState(pos));
                            for (SimplePredicate limit : predicate.limited) {
                                limit.testLimited(worldState);
                            }
                        } else {
                            boolean find = false;
                            BlockInfo[] infos = new BlockInfo[0];
                            for (SimplePredicate limit : predicate.limited) {
                                if (limit.minLayerCount > 0) {
                                    if (!cacheLayer.containsKey(limit)) {
                                        cacheLayer.put(limit, 1);
                                    } else
                                        if (cacheLayer.get(limit) < limit.minLayerCount && (limit.maxLayerCount == -1 ||
                                                cacheLayer.get(limit) < limit.maxLayerCount)) {
                                                    cacheLayer.put(limit, cacheLayer.get(limit) + 1);
                                                } else {
                                                    continue;
                                                }
                                } else {
                                    continue;
                                }
                                infos = limit.candidates == null ? null : limit.candidates.get();
                                find = true;
                                break;
                            }
                            if (!find) {
                                for (SimplePredicate limit : predicate.limited) {
                                    if (limit.minCount > 0) {
                                        if (!cacheGlobal.containsKey(limit)) {
                                            cacheGlobal.put(limit, 1);
                                        } else if (cacheGlobal.get(limit) < limit.minCount &&
                                                (limit.maxCount == -1 || cacheGlobal.get(limit) < limit.maxCount)) {
                                                    cacheGlobal.put(limit, cacheGlobal.get(limit) + 1);
                                                } else {
                                                    continue;
                                                }
                                    } else {
                                        continue;
                                    }
                                    infos = limit.candidates == null ? null : limit.candidates.get();
                                    find = true;
                                    break;
                                }
                            }
                            if (!find) { // no limited
                                for (SimplePredicate limit : predicate.limited) {
                                    if (limit.maxLayerCount != -1 &&
                                            cacheLayer.getOrDefault(limit, Integer.MAX_VALUE) == limit.maxLayerCount)
                                        continue;
                                    if (limit.maxCount != -1 &&
                                            cacheGlobal.getOrDefault(limit, Integer.MAX_VALUE) == limit.maxCount)
                                        continue;
                                    if (cacheLayer.containsKey(limit)) {
                                        cacheLayer.put(limit, cacheLayer.get(limit) + 1);
                                    } else {
                                        cacheLayer.put(limit, 1);
                                    }
                                    if (cacheGlobal.containsKey(limit)) {
                                        cacheGlobal.put(limit, cacheGlobal.get(limit) + 1);
                                    } else {
                                        cacheGlobal.put(limit, 1);
                                    }
                                    infos = ArrayUtils.addAll(infos,
                                            limit.candidates == null ? null : limit.candidates.get());
                                }
                                for (SimplePredicate common : predicate.common) {
                                    infos = ArrayUtils.addAll(infos,
                                            common.candidates == null ? null : common.candidates.get());
                                }
                            }

                            List<ItemStack> candidates = autoBuildSetting.apply(infos);
                            // if (infos != null) {
                            // for (BlockInfo info : infos) {
                            // if (info.getBlockState().getBlock() != Blocks.AIR) {
                            // final Block finalBlock = info.getBlockState().getBlock();
                            // if (Arrays.stream(COIL_BLOCKS).anyMatch(obj -> obj == finalBlock) &&
                            // terminalBehavior.getCoilTier() > 0) {
                            // info = new BlockInfo(COIL_BLOCKS[terminalBehavior.getCoilTier()]);
                            // candidates.add(info.getItemStackForm());
                            // break;
                            // } else if (Arrays.stream(ENERGY_INPUT_HATCH).anyMatch(
                            // hatch -> hatch.getBlock() == finalBlock) &&
                            // terminalBehavior.getEnergyTier() > 0) {
                            // info = new BlockInfo(
                            // ENERGY_INPUT_HATCH[terminalBehavior.getEnergyTier()]
                            // .getBlock());
                            // candidates.add(info.getItemStackForm());
                            // break;
                            // } else
                            // if (Arrays.stream(PARALLEL_HATCH).anyMatch(
                            // hatch -> (hatch != null) && (hatch.getBlock() == finalBlock)) &&
                            // terminalBehavior.getParallerTier() > 5) {
                            // info = new BlockInfo(
                            // PARALLEL_HATCH[terminalBehavior.getParallerTier()]
                            // .getBlock());
                            // candidates.add(info.getItemStackForm());
                            // break;
                            // } else
                            // if (Arrays.stream(MAINTENANCE_HATCHS)
                            // .anyMatch(hatch -> hatch.getBlock() == finalBlock) &&
                            // terminalBehavior.getMaintenanceType() > 0) {
                            // info = new BlockInfo(
                            // MAINTENANCE_HATCHS[terminalBehavior
                            // .getMaintenanceType()]
                            // .getBlock());
                            // candidates.add(info.getItemStackForm());
                            // break;
                            // } else
                            // if (Arrays.stream(ITEM_IMPORT_BUS)
                            // .anyMatch(bus -> bus.getBlock() == finalBlock) &&
                            // terminalBehavior.getInputBusTier() > 0) {
                            // info = new BlockInfo(ITEM_IMPORT_BUS[terminalBehavior
                            // .getInputBusTier()]
                            // .getBlock());
                            // candidates.add(info.getItemStackForm());
                            // break;
                            // } else
                            // if (Arrays.stream(ITEM_EXPORT_BUS)
                            // .anyMatch(bus -> bus.getBlock() == finalBlock) &&
                            // terminalBehavior.getOutputBusTier() > 0) {
                            // info = new BlockInfo(
                            // ITEM_EXPORT_BUS[terminalBehavior
                            // .getOutputBusTier()]
                            // .getBlock());
                            // candidates.add(info.getItemStackForm());
                            // break;
                            // } else
                            // if (Arrays.stream(FLUID_IMPORT_HATCH)
                            // .anyMatch(bus -> bus.getBlock() == finalBlock) &&
                            // terminalBehavior.getInputHatchTier() > 0) {
                            // info = new BlockInfo(
                            // FLUID_IMPORT_HATCH[terminalBehavior
                            // .getInputHatchTier()]
                            // .getBlock());
                            // candidates.add(info.getItemStackForm());
                            // break;
                            // } else
                            // if (Arrays.stream(FLUID_EXPORT_HATCH)
                            // .anyMatch(
                            // bus -> bus.getBlock() == finalBlock) &&
                            // terminalBehavior.getOutputHatchTier() > 0) {
                            // info = new BlockInfo(
                            // FLUID_EXPORT_HATCH[terminalBehavior
                            // .getOutputHatchTier()]
                            // .getBlock());
                            // candidates.add(info.getItemStackForm());
                            // break;
                            // }
                            // candidates.add(info.getItemStackForm());
                            // }
                            // }
                            // }

                            // check inventory
                            ItemStack found = null;
                            int foundSlot = -1;
                            IItemHandler handler = null;
                            if (!player.isCreative()) {
                                var foundHandler = getMatchStackWithHandler(candidates,
                                        player.getCapability(ForgeCapabilities.ITEM_HANDLER));
                                if (foundHandler != null) {
                                    foundSlot = foundHandler.getFirst();
                                    handler = foundHandler.getSecond();
                                    found = handler.getStackInSlot(foundSlot).copy();
                                }
                            } else {
                                for (ItemStack candidate : candidates) {
                                    found = candidate.copy();
                                    if (!found.isEmpty() && found.getItem() instanceof BlockItem) {
                                        break;
                                    }
                                    found = null;
                                }
                            }
                            if (found == null) continue;
                            BlockItem itemBlock = (BlockItem) found.getItem();
                            BlockPlaceContext context = new BlockPlaceContext(world, player, InteractionHand.MAIN_HAND,
                                    found, BlockHitResult.miss(player.getEyePosition(0), Direction.UP, pos));
                            InteractionResult interactionResult = itemBlock.place(context);
                            if (interactionResult != InteractionResult.FAIL) {
                                placeBlockPos.add(pos);
                                if (handler != null) {
                                    handler.extractItem(foundSlot, 1, false);
                                }
                            }
                            if (world.getBlockEntity(pos) instanceof IMachineBlockEntity machineBlockEntity) {
                                blocks.put(pos, machineBlockEntity.getMetaMachine());
                            } else {
                                blocks.put(pos, world.getBlockState(pos));
                            }
                        }
                    }
                }
                z++;
            }
        }
        Direction frontFacing = controller.self().getFrontFacing();
        blocks.forEach((pos, block) -> { // adjust facing
            if (!(block instanceof IMultiController)) {
                if (block instanceof BlockState && placeBlockPos.contains(pos)) {
                    resetFacing(pos, (BlockState) block, frontFacing, (p, f) -> {
                        Object object = blocks.get(p.relative(f));
                        return object == null ||
                                (object instanceof BlockState && ((BlockState) object).getBlock() == Blocks.AIR);
                    }, state -> world.setBlock(pos, state, 3));
                } else if (block instanceof MetaMachine machine) {
                    resetFacing(pos, machine.getBlockState(), frontFacing, (p, f) -> {
                        Object object = blocks.get(p.relative(f));
                        if (object == null || (object instanceof BlockState blockState && blockState.isAir())) {
                            return machine.isFacingValid(f);
                        }
                        return false;
                    }, state -> world.setBlock(pos, state, 3));
                }
            }
        });
    }

    public BlockInfo[][][] getPreview(int[] repetition) {
        Map<SimplePredicate, Integer> cacheGlobal = new HashMap<>();
        Map<BlockPos, BlockInfo> blocks = new HashMap<>();
        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int minZ = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int maxY = Integer.MIN_VALUE;
        int maxZ = Integer.MIN_VALUE;
        for (int l = 0, x = 0; l < this.fingerLength; l++) {
            for (int r = 0; r < repetition[l]; r++) {
                // Checking single slice
                Map<SimplePredicate, Integer> cacheLayer = new HashMap<>();
                for (int y = 0; y < this.thumbLength; y++) {
                    for (int z = 0; z < this.palmLength; z++) {
                        TraceabilityPredicate predicate = this.blockMatches[l][y][z];
                        boolean find = false;
                        BlockInfo[] infos = null;
                        for (SimplePredicate limit : predicate.limited) { // check layer and previewCount
                            if (limit.minLayerCount > 0) {
                                if (!cacheLayer.containsKey(limit)) {
                                    cacheLayer.put(limit, 1);
                                } else if (cacheLayer.get(limit) < limit.minLayerCount) {
                                    cacheLayer.put(limit, cacheLayer.get(limit) + 1);
                                } else {
                                    continue;
                                }
                                if (cacheGlobal.getOrDefault(limit, 0) < limit.previewCount) {
                                    if (!cacheGlobal.containsKey(limit)) {
                                        cacheGlobal.put(limit, 1);
                                    } else if (cacheGlobal.get(limit) < limit.previewCount) {
                                        cacheGlobal.put(limit, cacheGlobal.get(limit) + 1);
                                    } else {
                                        continue;
                                    }
                                }
                            } else {
                                continue;
                            }
                            infos = limit.candidates == null ? null : limit.candidates.get();
                            find = true;
                            break;
                        }
                        if (!find) { // check global and previewCount
                            for (SimplePredicate limit : predicate.limited) {
                                if (limit.minCount == -1 && limit.previewCount == -1) continue;
                                if (cacheGlobal.getOrDefault(limit, 0) < limit.previewCount) {
                                    if (!cacheGlobal.containsKey(limit)) {
                                        cacheGlobal.put(limit, 1);
                                    } else if (cacheGlobal.get(limit) < limit.previewCount) {
                                        cacheGlobal.put(limit, cacheGlobal.get(limit) + 1);
                                    } else {
                                        continue;
                                    }
                                } else if (limit.minCount > 0) {
                                    if (!cacheGlobal.containsKey(limit)) {
                                        cacheGlobal.put(limit, 1);
                                    } else if (cacheGlobal.get(limit) < limit.minCount) {
                                        cacheGlobal.put(limit, cacheGlobal.get(limit) + 1);
                                    } else {
                                        continue;
                                    }
                                } else {
                                    continue;
                                }
                                infos = limit.candidates == null ? null : limit.candidates.get();
                                find = true;
                                break;
                            }
                        }
                        if (!find) { // check common with previewCount
                            for (SimplePredicate common : predicate.common) {
                                if (common.previewCount > 0) {
                                    if (!cacheGlobal.containsKey(common)) {
                                        cacheGlobal.put(common, 1);
                                    } else if (cacheGlobal.get(common) < common.previewCount) {
                                        cacheGlobal.put(common, cacheGlobal.get(common) + 1);
                                    } else {
                                        continue;
                                    }
                                } else {
                                    continue;
                                }
                                infos = common.candidates == null ? null : common.candidates.get();
                                find = true;
                                break;
                            }
                        }
                        if (!find) { // check without previewCount
                            for (SimplePredicate common : predicate.common) {
                                if (common.previewCount == -1) {
                                    infos = common.candidates == null ? null : common.candidates.get();
                                    find = true;
                                    break;
                                }
                            }
                        }
                        if (!find) { // check max
                            for (SimplePredicate limit : predicate.limited) {
                                if (limit.previewCount != -1) {
                                    continue;
                                } else if (limit.maxCount != -1 || limit.maxLayerCount != -1) {
                                    if (cacheGlobal.getOrDefault(limit, 0) < limit.maxCount) {
                                        if (!cacheGlobal.containsKey(limit)) {
                                            cacheGlobal.put(limit, 1);
                                        } else {
                                            cacheGlobal.put(limit, cacheGlobal.get(limit) + 1);
                                        }
                                    } else if (cacheLayer.getOrDefault(limit, 0) < limit.maxLayerCount) {
                                        if (!cacheLayer.containsKey(limit)) {
                                            cacheLayer.put(limit, 1);
                                        } else {
                                            cacheLayer.put(limit, cacheLayer.get(limit) + 1);
                                        }
                                    } else {
                                        continue;
                                    }
                                }

                                infos = limit.candidates == null ? null : limit.candidates.get();
                                break;
                            }
                        }
                        BlockInfo info = infos == null || infos.length == 0 ? BlockInfo.EMPTY : infos[0];
                        BlockPos pos = setActualRelativeOffset(z, y, x, Direction.NORTH, Direction.UP, false);

                        blocks.put(pos, info);
                        minX = Math.min(pos.getX(), minX);
                        minY = Math.min(pos.getY(), minY);
                        minZ = Math.min(pos.getZ(), minZ);
                        maxX = Math.max(pos.getX(), maxX);
                        maxY = Math.max(pos.getY(), maxY);
                        maxZ = Math.max(pos.getZ(), maxZ);
                    }
                }
                x++;
            }
        }
        BlockInfo[][][] result = (BlockInfo[][][]) Array.newInstance(BlockInfo.class, maxX - minX + 1, maxY - minY + 1,
                maxZ - minZ + 1);
        int finalMinX = minX;
        int finalMinY = minY;
        int finalMinZ = minZ;
        blocks.forEach((pos, info) -> {
            resetFacing(pos, info.getBlockState(), null, (p, f) -> {
                BlockInfo blockInfo = blocks.get(p.relative(f));
                if (blockInfo == null || blockInfo.getBlockState().getBlock() == Blocks.AIR) {
                    if (blocks.get(pos).getBlockState().getBlock() instanceof MetaMachineBlock machineBlock) {
                        if (machineBlock.newBlockEntity(BlockPos.ZERO,
                                machineBlock.defaultBlockState()) instanceof IMachineBlockEntity machineBlockEntity) {
                            var machine = machineBlockEntity.getMetaMachine();
                            if (machine instanceof IMultiController) {
                                return false;
                            } else {
                                return machine.isFacingValid(f);
                            }
                        }
                    }
                    return true;
                }
                return false;
            }, info::setBlockState);
            result[pos.getX() - finalMinX][pos.getY() - finalMinY][pos.getZ() - finalMinZ] = info;
        });
        return result;
    }

    private void resetFacing(BlockPos pos, BlockState blockState, Direction facing,
                             BiFunction<BlockPos, Direction, Boolean> checker, Consumer<BlockState> consumer) {
        if (blockState.hasProperty(BlockStateProperties.FACING)) {
            tryFacings(blockState, pos, checker, consumer, BlockStateProperties.FACING,
                    facing == null ? FACINGS : ArrayUtils.addAll(new Direction[] { facing }, FACINGS));
        } else if (blockState.hasProperty(BlockStateProperties.HORIZONTAL_FACING)) {
            tryFacings(blockState, pos, checker, consumer, BlockStateProperties.HORIZONTAL_FACING,
                    facing == null || facing.getAxis() == Direction.Axis.Y ? FACINGS_H :
                            ArrayUtils.addAll(new Direction[] { facing }, FACINGS_H));
        }
    }

    private void tryFacings(BlockState blockState, BlockPos pos, BiFunction<BlockPos, Direction, Boolean> checker,
                            Consumer<BlockState> consumer, Property<Direction> property, Direction[] facings) {
        Direction found = null;
        for (Direction facing : facings) {
            if (checker.apply(pos, facing)) {
                found = facing;
                break;
            }
        }
        if (found == null) {
            found = Direction.NORTH;
        }
        consumer.accept(blockState.setValue(property, found));
    }

    private BlockPos setActualRelativeOffset(int x, int y, int z, Direction facing, Direction upwardsFacing,
                                             boolean isFlipped) {
        int[] c0 = new int[] { x, y, z }, c1 = new int[3];
        if (facing == Direction.UP || facing == Direction.DOWN) {
            Direction of = facing == Direction.DOWN ? upwardsFacing : upwardsFacing.getOpposite();
            for (int i = 0; i < 3; i++) {
                switch (structureDir[i].getActualFacing(of)) {
                    case UP -> c1[1] = c0[i];
                    case DOWN -> c1[1] = -c0[i];
                    case WEST -> c1[0] = -c0[i];
                    case EAST -> c1[0] = c0[i];
                    case NORTH -> c1[2] = -c0[i];
                    case SOUTH -> c1[2] = c0[i];
                }
            }
            int xOffset = upwardsFacing.getStepX();
            int zOffset = upwardsFacing.getStepZ();
            int tmp;
            if (xOffset == 0) {
                tmp = c1[2];
                c1[2] = zOffset > 0 ? c1[1] : -c1[1];
                c1[1] = zOffset > 0 ? -tmp : tmp;
            } else {
                tmp = c1[0];
                c1[0] = xOffset > 0 ? c1[1] : -c1[1];
                c1[1] = xOffset > 0 ? -tmp : tmp;
            }
            if (isFlipped) {
                if (upwardsFacing == Direction.NORTH || upwardsFacing == Direction.SOUTH) {
                    c1[0] = -c1[0]; // flip X-axis
                } else {
                    c1[2] = -c1[2]; // flip Z-axis
                }
            }
        } else {
            for (int i = 0; i < 3; i++) {
                switch (structureDir[i].getActualFacing(facing)) {
                    case UP -> c1[1] = c0[i];
                    case DOWN -> c1[1] = -c0[i];
                    case WEST -> c1[0] = -c0[i];
                    case EAST -> c1[0] = c0[i];
                    case NORTH -> c1[2] = -c0[i];
                    case SOUTH -> c1[2] = c0[i];
                }
            }
            if (upwardsFacing == Direction.WEST || upwardsFacing == Direction.EAST) {
                int xOffset = upwardsFacing == Direction.WEST ? facing.getClockWise().getStepX() :
                        facing.getClockWise().getOpposite().getStepX();
                int zOffset = upwardsFacing == Direction.WEST ? facing.getClockWise().getStepZ() :
                        facing.getClockWise().getOpposite().getStepZ();
                int tmp;
                if (xOffset == 0) {
                    tmp = c1[2];
                    c1[2] = zOffset > 0 ? -c1[1] : c1[1];
                    c1[1] = zOffset > 0 ? tmp : -tmp;
                } else {
                    tmp = c1[0];
                    c1[0] = xOffset > 0 ? -c1[1] : c1[1];
                    c1[1] = xOffset > 0 ? tmp : -tmp;
                }
            } else if (upwardsFacing == Direction.SOUTH) {
                c1[1] = -c1[1];
                if (facing.getStepX() == 0) {
                    c1[0] = -c1[0];
                } else {
                    c1[2] = -c1[2];
                }
            }
            if (isFlipped) {
                if (upwardsFacing == Direction.NORTH || upwardsFacing == Direction.SOUTH) {
                    if (facing == Direction.NORTH || facing == Direction.SOUTH) {
                        c1[0] = -c1[0]; // flip X-axis
                    } else {
                        c1[2] = -c1[2]; // flip Z-axis
                    }
                } else {
                    c1[1] = -c1[1]; // flip Y-axis
                }
            }
        }
        return new BlockPos(c1[0], c1[1], c1[2]);
    }

    @Nullable
    private static Pair<Integer, IItemHandler> getMatchStackWithHandler(
                                                                        List<ItemStack> candidates,
                                                                        LazyOptional<IItemHandler> cap) {
        IItemHandler handler = cap.orElse(null);
        if (handler == null) {
            return null;
        }
        for (int i = 0; i < handler.getSlots(); i++) {
            @NotNull
            ItemStack stack = handler.getStackInSlot(i);
            if (stack.isEmpty()) continue;

            @NotNull
            LazyOptional<IItemHandler> stackCap = stack.getCapability(ForgeCapabilities.ITEM_HANDLER);
            if (stackCap.isPresent()) {
                var rt = getMatchStackWithHandler(candidates, stackCap);
                if (rt != null) {
                    return rt;
                }
            } else if (candidates.stream().anyMatch(candidate -> ItemStack.isSameItemSameTags(candidate, stack)) &&
                    !stack.isEmpty() && stack.getItem() instanceof BlockItem) {
                        return Pair.of(i, handler);
                    }
        }
        return null;
    }
}
