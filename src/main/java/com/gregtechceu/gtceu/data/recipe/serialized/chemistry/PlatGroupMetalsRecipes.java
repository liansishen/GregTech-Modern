package com.gregtechceu.gtceu.data.recipe.serialized.chemistry;

import net.minecraft.data.recipes.FinishedRecipe;

import java.util.function.Consumer;

import static com.gregtechceu.gtceu.api.GTValues.*;
import static com.gregtechceu.gtceu.api.data.tag.TagPrefix.*;
import static com.gregtechceu.gtceu.common.data.GTMaterials.*;
import static com.gregtechceu.gtceu.common.data.GTRecipeTypes.*;

public class PlatGroupMetalsRecipes {

    public static void init(Consumer<FinishedRecipe> provider) {
        // Primary Chain

        // Platinum Group Sludge Production
        CHEMICAL_RECIPES.recipeBuilder("pgs_from_chalcocite").duration(50).EUt(VA[LV])
                .inputItems(crushedPurified, Chalcocite)
                .inputFluids(NitricAcid.getFluid(100))
                .outputItems(dust, PlatinumGroupSludge, 2)
                .outputFluids(SulfuricCopperSolution.getFluid(1000))
                .save(provider);

        CHEMICAL_RECIPES.recipeBuilder("pgs_from_bornite").duration(50).EUt(VA[LV])
                .inputItems(crushedPurified, Bornite)
                .inputFluids(NitricAcid.getFluid(100))
                .outputItems(dust, PlatinumGroupSludge, 2)
                .outputFluids(SulfuricCopperSolution.getFluid(1000))
                .save(provider);

        CHEMICAL_RECIPES.recipeBuilder("pgs_from_tetrahedrite").duration(50).EUt(VA[LV])
                .inputItems(crushedPurified, Tetrahedrite)
                .inputFluids(NitricAcid.getFluid(100))
                .outputItems(dust, PlatinumGroupSludge, 2)
                .outputFluids(SulfuricCopperSolution.getFluid(1000))
                .save(provider);

        CHEMICAL_RECIPES.recipeBuilder("pgs_from_cooperite").duration(50).EUt(VA[LV])
                .inputItems(crushedPurified, Cooperite)
                .inputFluids(NitricAcid.getFluid(100))
                .outputItems(dust, PlatinumGroupSludge, 4)
                .outputFluids(SulfuricNickelSolution.getFluid(1000))
                .save(provider);

        // Aqua Regia
        // HNO3 + HCl -> [HNO3 + HCl]
        MIXER_RECIPES.recipeBuilder("aqua_regia").duration(30).EUt(VA[LV])
                .inputFluids(NitricAcid.getFluid(1000))
                .inputFluids(HydrochloricAcid.getFluid(2000))
                .outputFluids(AquaRegia.getFluid(3000))
                .save(provider);

        CENTRIFUGE_RECIPES.recipeBuilder("pgs_separation").duration(200).EUt(VA[HV])
                .inputItems(dust, PlatinumGroupSludge, 6)
                .inputFluids(AquaRegia.getFluid(1200))
                .chancedOutput(dust, PlatinumRaw, 4, 8000, 500)
                .chancedOutput(dust, PalladiumRaw, 4, 8000, 500)
                .chancedOutput(dust, InertMetalMixture, 3, 8500, 500)
                .chancedOutput(dust, RarestMetalMixture, 3, 7500, 500)
                .chancedOutput(dust, PlatinumSludgeResidue, 2, 9000, 0)
                .save(provider);

        // Formic Acid
        CHEMICAL_RECIPES.recipeBuilder("sodium_formate")
                .inputItems(dust, SodiumHydroxide, 3)
                .inputFluids(CarbonMonoxide.getFluid(1000))
                .outputFluids(SodiumFormate.getFluid(1000))
                .duration(15).EUt(VA[LV]).save(provider);

        CHEMICAL_RECIPES.recipeBuilder("formic_acid_a")
                .inputFluids(SodiumFormate.getFluid(2000))
                .inputFluids(SulfuricAcid.getFluid(1000))
                .circuitMeta(1)
                .outputFluids(FormicAcid.getFluid(2000))
                .outputItems(dust, SodiumSulfate, 7)
                .duration(15).EUt(VA[LV]).save(provider);

        // PLATINUM

        CHEMICAL_RECIPES.recipeBuilder("raw_platinum_separation")
                .inputItems(dust, PlatinumRaw, 3)
                .inputItems(dust, Calcium, 1)
                .outputItems(dust, Platinum, 1)
                .outputItems(dust, CalciumChloride, 3)
                .duration(30).EUt(VA[LV]).save(provider);

        // PALLADIUM

        LARGE_CHEMICAL_RECIPES.recipeBuilder("raw_palladium_separation")
                .inputItems(dust, PalladiumRaw, 4)
                .inputFluids(FormicAcid.getFluid(4000))
                .outputItems(dust, Palladium, 2)
                .outputFluids(Ammonia.getFluid(4000))
                .outputFluids(Ethylene.getFluid(1000))
                .outputFluids(Water.getFluid(1000))
                .duration(250).EUt(VA[LV]).save(provider);

        // RHODIUM / RUTHENIUM
        BLAST_RECIPES.recipeBuilder("leach_residue_one")
                .inputItems(dust, InertMetalMixture, 6)
                .inputItems(dust, Saltpeter, 10)
                .inputFluids(SulfuricAcid.getFluid(1000))
                .outputFluids(RhodiumSulfateGas.getFluid(500))
                .outputItems(dust, SodiumRutheniate, 6)
                .blastFurnaceTemp(775)
                .duration(200).EUt(VA[MV]).save(provider);

        CHEMICAL_RECIPES.recipeBuilder("ruthenium_tetroxide")
                .inputItems(dust, SodiumRutheniate, 6)
                .inputFluids(Chlorine.getFluid(3000))
                .outputFluids(RutheniumTetroxide.getFluid(3000))
                .duration(140).EUt(VA[LV]).save(provider);

        CRACKING_RECIPES.recipeBuilder("hot_ruthenium_tetroxide")
                .inputFluids(RutheniumTetroxide.getFluid(1000))
                .inputFluids(Steam.getFluid(1000))
                .circuitMeta(1)
                .outputFluids(RutheniumTetroxideHot.getFluid(2000))
                .duration(120).EUt(VA[HV]).save(provider);

        DISTILLATION_RECIPES.recipeBuilder("hot_ruthenium_tetroxide_distill")
                .inputFluids(RutheniumTetroxideHot.getFluid(3000))
                .outputItems(dust, Salt, 6)
                .outputFluids(Water.getFluid(1800))
                .outputFluids(RutheniumTetroxideLQ.getFluid(1200))
                .duration(130).EUt(VA[HV]).save(provider);

        FLUID_SOLIDFICATION_RECIPES.recipeBuilder("ruthenium_tetroxide_dust")
                .inputFluids(RutheniumTetroxideLQ.getFluid(1000))
                .outputItems(dust, RutheniumTetroxide, 5)
                .duration(120).EUt(VA[LV]).save(provider);

        CHEMICAL_RECIPES.recipeBuilder("ruthenium_tetroxide_separation")
                .inputItems(dust, RutheniumTetroxide, 5)
                .inputFluids(HydrochloricAcid.getFluid(6000))
                .outputItems(dust, Ruthenium, 1)
                .outputFluids(Water.getFluid(2000))
                .outputFluids(Chlorine.getFluid(6000))
                .duration(80).EUt(VA[LV]).save(provider);

        CHEMICAL_RECIPES.recipeBuilder("rhodium_sulfate_to_solution")
                .inputFluids(Water.getFluid(1000))
                .inputFluids(RhodiumSulfateGas.getFluid(1000))
                .outputFluids(RhodiumSulfate.getFluid(1000))
                .duration(140).EUt(VA[LV]).save(provider);

        CHEMICAL_BATH_RECIPES.recipeBuilder("crude_rhodium_metallic_powder")
                .inputItems(dust, Zinc, 1)
                .inputFluids(RhodiumSulfate.getFluid(1000))
                .outputItems(dust, ZincSulfate, 6)
                .outputItems(dust, RoughlyRhodiumMetal, 1)
                .duration(200).EUt(VA[LV]).save(provider);

        BLAST_RECIPES.recipeBuilder("rhodium_salt_ebf")
                .inputItems(dust, RoughlyRhodiumMetal, 1)
                .inputItems(dust, Salt, 2)
                .inputFluids(Chlorine.getFluid(1000))
                .outputItems(dust, RhodiumSalt, 3)
                .blastFurnaceTemp(600)
                .duration(120).EUt(VA[MV]).save(provider);

        MIXER_RECIPES.recipeBuilder("rhodium_salt_solution")
                .inputItems(dust, RhodiumSalt, 3)
                .inputFluids(Water.getFluid(1000))
                .outputFluids(RhodiumSaltSolution.getFluid(1000))
                .duration(60).EUt(VA[LV]).save(provider);

        CHEMICAL_RECIPES.recipeBuilder("sodium_nitrate")
                .inputItems(dust, Sodium)
                .inputFluids(NitricAcid.getFluid(1000))
                .outputFluids(Hydrogen.getFluid(1000))
                .outputItems(dust, SodiumNitrate, 5)
                .duration(60).EUt(VA[LV]).save(provider);

        CHEMICAL_RECIPES.recipeBuilder("rhodium_nitrate")
                .inputItems(dust, SodiumNitrate, 5)
                .inputFluids(Oxygen.getFluid(2000))
                .inputFluids(NitrogenDioxide.getFluid(2000))
                .inputFluids(RhodiumSaltSolution.getFluid(1000))
                .outputItems(dust, RhodiumNitrate, 13)
                .outputItems(dust, Salt, 6)
                .duration(160).EUt(VA[LV]).save(provider);

        SIFTER_RECIPES.recipeBuilder("rhodium_filter_cake")
                .inputItems(dust, RhodiumNitrate, 13)
                .chancedOutput(dust, RhodiumFilterCake, 8000, 200)
                .chancedOutput(dust, RhodiumFilterCake, 8000, 200)
                .chancedOutput(dust, RhodiumFilterCake, 6000, 100)
                .chancedOutput(dust, RhodiumFilterCake, 6000, 100)
                .chancedOutput(dust, RhodiumFilterCake, 4000, 50)
                .chancedOutput(dust, RhodiumFilterCake, 4000, 50)
                .duration(200).EUt(VA[LV]).save(provider);

        MIXER_RECIPES.recipeBuilder("rhodium_filter_cake_solution")
                .inputItems(dust, RhodiumFilterCake, 6)
                .inputFluids(Water.getFluid(1000))
                .outputFluids(RhodiumFilterCakeSolution.getFluid(1000))
                .duration(240).EUt(VA[LV]).save(provider);

        DISTILLERY_RECIPES.recipeBuilder("reprecipitated_rhodium")
                .inputFluids(RhodiumFilterCakeSolution.getFluid(1000))
                .circuitMeta(1)
                .outputItems(dust, ReprecipitatedRhodium, 7)
                .duration(220).EUt(VA[LV]).save(provider);

        CHEMICAL_RECIPES.recipeBuilder("rhodium_dust")
                .inputItems(dust, ReprecipitatedRhodium, 7)
                .inputFluids(HydrochloricAcid.getFluid(1000))
                .outputItems(dust, Rhodium, 2)
                .outputItems(dust, AmmoniumChloride, 6)
                .outputFluids(Hydrogen.getFluid(1000))
                .duration(240).EUt(VA[LV]).save(provider);

        // OSMIUM / IRIDIUM
        BLAST_RECIPES.recipeBuilder("rarest_metal_residue_ebf")
                .inputItems(dust, RarestMetalMixture, 7)
                .inputFluids(HydrochloricAcid.getFluid(4000))
                .outputItems(dust, IridiumMetalResidue, 5)
                .outputFluids(AcidicOsmiumSolution.getFluid(2000))
                .blastFurnaceTemp(775)
                .duration(100).EUt(VA[MV]).save(provider);

        DISTILLATION_RECIPES.recipeBuilder("acidic_osmium_solution_separation").duration(400).EUt(VA[LV])
                .inputFluids(AcidicOsmiumSolution.getFluid(2000))
                .outputItems(dust, OsmiumTetroxide, 5)
                .outputFluids(HydrochloricAcid.getFluid(1000))
                .outputFluids(Water.getFluid(1000))
                .save(provider);

        CHEMICAL_RECIPES.recipeBuilder("iridium_dioxide_dissolving")
                .inputItems(dust, IridiumDioxide, 3)
                .inputFluids(HydrochloricAcid.getFluid(1000))
                .outputFluids(AcidicIridium.getFluid(1000))
                .duration(240).EUt(VA[LV]).save(provider);

        CHEMICAL_RECIPES.recipeBuilder("iridium_chloride")
                .inputItems(dust, AmmoniumChloride, 18)
                .inputFluids(AcidicIridium.getFluid(1000))
                .outputItems(dust, IridiumChloride, 4)
                .outputFluids(Ammonia.getFluid(3000))
                .duration(160).EUt(VA[LV]).save(provider);

        CHEMICAL_RECIPES.recipeBuilder("iridium_chloride_separation")
                .inputItems(dust, IridiumChloride, 4)
                .inputItems(dust, Calcium, 1)
                .outputItems(dust, Iridium, 1)
                .outputItems(dust, CalciumChloride, 4)
                .duration(80).EUt(VA[EV]).save(provider);

        CHEMICAL_RECIPES.recipeBuilder("osmium_tetroxide_separation").duration(200).EUt(VA[LV])
                .inputItems(dust, OsmiumTetroxide, 5)
                .inputFluids(Hydrogen.getFluid(8000))
                .outputItems(dust, Osmium)
                .outputFluids(Water.getFluid(4000))
                .save(provider);
    }
}
