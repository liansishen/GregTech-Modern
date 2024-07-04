package com.gregtechceu.gtceu.data.recipe.serialized.chemistry;

import net.minecraft.data.recipes.FinishedRecipe;

import java.util.function.Consumer;

import static com.gregtechceu.gtceu.api.GTValues.*;
import static com.gregtechceu.gtceu.api.data.tag.TagPrefix.*;
import static com.gregtechceu.gtceu.common.data.GTMaterials.*;
import static com.gregtechceu.gtceu.common.data.GTRecipeTypes.*;

public class NaquadahRecipes {

    // Rough ratio of Naquadah Dust breakdown from this process:
    //
    // 6 NAQUADAH DUST:
    // |> 1 Enriched Naquadah
    // |> 1 Naquadria
    // |> 1 Titanium
    // |> 1 Sulfur
    // |> 0.5 Indium
    // |> 0.5 Trinium
    // |> 0.5 Phosphorus
    // |> 0.25 Gallium
    // |> 0.25 Barium

    public static void init(Consumer<FinishedRecipe> provider) {
        // FLUOROANTIMONIC ACID

        CHEMICAL_RECIPES.recipeBuilder("antimony_trioxide").EUt(VA[ULV]).duration(60)
                .inputItems(dust, Antimony, 2)
                .inputFluids(Oxygen.getFluid(3000))
                .outputItems(dust, AntimonyTrioxide, 5)
                .save(provider);

        CHEMICAL_RECIPES.recipeBuilder("antimony_trifluoride").EUt(VA[LV]).duration(60)
                .inputItems(dust, AntimonyTrioxide, 5)
                .inputFluids(HydrofluoricAcid.getFluid(6000))
                .outputItems(dust, AntimonyTrifluoride, 8)
                .outputFluids(Water.getFluid(3000))
                .save(provider);

        CHEMICAL_RECIPES.recipeBuilder("fluoroantimonic_acid").EUt(VA[HV]).duration(300)
                .inputItems(dust, AntimonyTrifluoride, 4)
                .inputFluids(HydrofluoricAcid.getFluid(4000))
                .outputFluids(FluoroantimonicAcid.getFluid(1000))
                .outputFluids(Hydrogen.getFluid(2000))
                .save(provider);

        // STARTING POINT

        LARGE_CHEMICAL_RECIPES.recipeBuilder("naquadah_separation").EUt(VA[LuV]).duration(200)
                .inputFluids(FluoroantimonicAcid.getFluid(1000))
                .inputItems(dust, Naquadah, 6)
                .outputFluids(ImpureEnrichedNaquadahSolution.getFluid(4000))
                .outputItems(dust, TitaniumTrifluoride, 4)
                .save(provider);

        // ENRICHED NAQUADAH PROCESS

        CHEMICAL_RECIPES.recipeBuilder("hexafluoride_enriched_naquadah_solution")
                .inputFluids(ImpureEnrichedNaquadahSolution.getFluid(1000))
                .inputFluids(Fluorine.getFluid(6000))
                .outputFluids(HexafluorideEnrichedNaquadahSolution.getFluid(1000))
                .EUt(VA[IV])
                .duration(150)
                .save(provider);

        CHEMICAL_RECIPES.recipeBuilder("xenon_hexafluoro_enriched_naquadate")
                .inputFluids(HexafluorideEnrichedNaquadahSolution.getFluid(1000))
                .inputFluids(Xenon.getFluid(1000))
                .outputFluids(XenonHexafluoroEnrichedNaquadate.getFluid(1000))
                .EUt(VA[HV])
                .duration(200)
                .save(provider);

        ELECTROLYZER_RECIPES.recipeBuilder("xenoauric_fluoroantimonic_acid")
                .inputFluids(XenoauricFluoroantimonicAcid.getFluid(1000))
                .outputItems(dust, Gold)
                .outputItems(dust, AntimonyTrifluoride)
                .outputFluids(Xenon.getFluid(1000))
                .outputFluids(Fluorine.getFluid(3000))
                .EUt(VA[MV])
                .duration(800)
                .save(provider);

        ELECTROLYZER_RECIPES.recipeBuilder("gold_chloride")
                .inputItems(dust, Gold, 2)
                .inputFluids(Chlorine.getFluid(6000))
                .outputFluids(GoldChloride.getFluid(1000))
                .EUt(VA[MV])
                .duration(180)
                .save(provider);

        CHEMICAL_RECIPES.recipeBuilder("bromine_trifluoride")
                .inputFluids(Bromine.getFluid(1000))
                .inputFluids(Fluorine.getFluid(3000))
                .outputFluids(BromineTrifluoride.getFluid(1000))
                .EUt(VA[LV])
                .duration(120)
                .save(provider);

        CHEMICAL_RECIPES.recipeBuilder("gold_chloride")
                .inputFluids(GoldChloride.getFluid(1000))
                .inputFluids(BromineTrifluoride.getFluid(2000))
                .outputItems(dust, GoldTrifluoride, 8)
                .outputFluids(Bromine.getFluid(2000))
                .outputFluids(Chlorine.getFluid(6000))
                .EUt(VA[MV])
                .duration(200)
                .save(provider);

        LARGE_CHEMICAL_RECIPES.recipeBuilder("enriched_naquadah_residue_solution")
                .inputItems(dust, GoldTrifluoride, 4)
                .inputFluids(XenonHexafluoroEnrichedNaquadate.getFluid(1000))
                .inputFluids(FluoroantimonicAcid.getFluid(1000))
                .inputFluids(Hydrogen.getFluid(9000))
                .outputFluids(EnrichedNaquadahSolution.getFluid(1000))
                .outputFluids(XenoauricFluoroantimonicAcid.getFluid(1000))
                .EUt(VA[LuV])
                .duration(160)
                .save(provider);

        MIXER_RECIPES.recipeBuilder("enriched_naquadah_solution_separation").EUt(VA[HV]).duration(100)
                .inputFluids(EnrichedNaquadahSolution.getFluid(1000))
                .inputFluids(SulfuricAcid.getFluid(2000))
                .outputFluids(AcidicEnrichedNaquadahSolution.getFluid(3000))
                .save(provider);

        CENTRIFUGE_RECIPES.recipeBuilder("acidic_enriched_naquadah_separation").EUt(VA[HV]).duration(100)
                .inputFluids(AcidicEnrichedNaquadahSolution.getFluid(3000))
                .outputFluids(EnrichedNaquadahWaste.getFluid(2000))
                .outputFluids(Fluorine.getFluid(500))
                .outputItems(dust, EnrichedNaquadahSulfate, 6) // Nq+SO4
                .save(provider);

        BLAST_RECIPES.recipeBuilder("enriched_naquadah_sulfate_separation").EUt(VA[IV]).duration(500)
                .blastFurnaceTemp(7000)
                .inputItems(dust, EnrichedNaquadahSulfate, 6)
                .inputFluids(Hydrogen.getFluid(2000))
                .outputItems(ingotHot, NaquadahEnriched)
                .outputFluids(SulfuricAcid.getFluid(1000))
                .save(provider);

        DISTILLATION_RECIPES.recipeBuilder("enriched_naquadah_waste_separation").EUt(VA[HV]).duration(300)
                .inputFluids(EnrichedNaquadahWaste.getFluid(2000))
                .chancedOutput(dust, BariumSulfide, 5000, 0)
                .outputFluids(SulfuricAcid.getFluid(500))
                .outputFluids(EnrichedNaquadahSolution.getFluid(250))
                .outputFluids(NaquadriaSolution.getFluid(100))
                .save(provider);

        // NAQUADRIA PROCESS
        LARGE_CHEMICAL_RECIPES.recipeBuilder("enriched_naquadah_separation").EUt(VA[LuV]).duration(400)
                .inputFluids(FluoroantimonicAcid.getFluid(1000))
                .inputItems(dust, NaquadahEnriched, 6)
                .outputFluids(ImpureNaquadriaSolution.getFluid(4000))
                .outputItems(dust, IndiumPhosphide, 1)
                .save(provider);

        CHEMICAL_RECIPES.recipeBuilder("hexafluoride_naquadria_solution")
                .inputFluids(ImpureNaquadriaSolution.getFluid(1000))
                .inputFluids(Fluorine.getFluid(6000))
                .outputFluids(HexafluorideNaquadriaSolution.getFluid(1000))
                .EUt(VA[IV])
                .duration(400)
                .save(provider);

        CHEMICAL_RECIPES.recipeBuilder("radon_naquadria")
                .inputFluids(Radon.getFluid(1000))
                .inputFluids(Fluorine.getFluid(2000))
                .outputFluids(RadonDifluoride.getFluid(1000))
                .EUt(VA[MV])
                .duration(120)
                .save(provider);

        CHEMICAL_RECIPES.recipeBuilder("radon_naquadria_octafluoride")
                .inputFluids(HexafluorideNaquadriaSolution.getFluid(1000))
                .inputFluids(RadonDifluoride.getFluid(1000))
                .outputFluids(RadonNaquadriaOctafluoride.getFluid(1000))
                .outputFluids(NaquadriaSolution.getFluid(1000))
                .EUt(VA[HV])
                .duration(600)
                .save(provider);

        MIXER_RECIPES.recipeBuilder("naquadria_solution_separation").EUt(VA[HV]).duration(100)
                .inputFluids(NaquadriaSolution.getFluid(1000))
                .inputFluids(SulfuricAcid.getFluid(2000))
                .outputFluids(AcidicNaquadriaSolution.getFluid(3000))
                .save(provider);

        CENTRIFUGE_RECIPES.recipeBuilder("acidic_naquadria_solution_separation").EUt(VA[HV]).duration(100)
                .inputFluids(AcidicNaquadriaSolution.getFluid(3000))
                .outputFluids(NaquadriaWaste.getFluid(2000))
                .outputFluids(Fluorine.getFluid(500))
                .outputItems(dustTiny, NaquadriaSulfate, 2)
                .chancedOutput(dustTiny, NaquadriaSulfate, 2, 6000, 0)
                .chancedOutput(dustTiny, NaquadriaSulfate, 2, 4000, 0)
                .save(provider);

        CHEMICAL_RECIPES.recipeBuilder("xenon_trioxide")
                .inputFluids(Xenon.getFluid(1000))
                .inputFluids(Oxygen.getFluid(3000))
                .outputFluids(XenonTrioxide.getFluid(1000))
                .EUt(VA[HV])
                .duration(200)
                .save(provider);

        CHEMICAL_RECIPES.recipeBuilder("caesium_fluoride")
                .inputItems(dust, Caesium)
                .inputFluids(Fluorine.getFluid(1000))
                .outputFluids(CaesiumFluoride.getFluid(1000))
                .EUt(7)
                .duration(60)
                .save(provider);

        CHEMICAL_RECIPES.recipeBuilder("caesium_xenontrioxide_fluoride")
                .inputFluids(CaesiumFluoride.getFluid(1000))
                .inputFluids(XenonTrioxide.getFluid(1000))
                .outputFluids(CaesiumXenontrioxideFluoride.getFluid(1000))
                .EUt(VA[MV])
                .duration(400)
                .save(provider);

        CHEMICAL_RECIPES.recipeBuilder("naquadria_caesium_xenonnonfluoride")
                .inputFluids(RadonNaquadriaOctafluoride.getFluid(1000))
                .inputFluids(CaesiumXenontrioxideFluoride.getFluid(1000))
                .outputFluids(NaquadriaCaesiumXenonnonfluoride.getFluid(1000))
                .outputFluids(RadonTrioxide.getFluid(1000))
                .EUt(VA[IV])
                .duration(400)
                .save(provider);

        CHEMICAL_RECIPES.recipeBuilder("nitryl_fluoride")
                .inputFluids(NitrogenDioxide.getFluid(1000))
                .inputFluids(Fluorine.getFluid(1000))
                .outputFluids(NitrylFluoride.getFluid(1000))
                .EUt(VA[LV])
                .duration(160)
                .save(provider);

        CHEMICAL_RECIPES.recipeBuilder("nitrosonium_octafluoroxenate")
                .inputFluids(NaquadriaCaesiumXenonnonfluoride.getFluid(1000))
                .inputFluids(NitrylFluoride.getFluid(2000))
                .outputFluids(NaquadriaCaesiumfluoride.getFluid(1000))
                .outputFluids(NitrosoniumOctafluoroxenate.getFluid(1000))
                .EUt(VA[EV])
                .duration(400)
                .save(provider);

        MIXER_RECIPES.recipeBuilder("acidic_naquadria_caesiumfluoride")
                .inputFluids(SulfuricAcid.getFluid(2000))
                .inputFluids(NaquadriaCaesiumfluoride.getFluid(1000))
                .outputFluids(AcidicNaquadriaCaesiumfluoride.getFluid(3000))
                .EUt(VA[IV])
                .duration(200)
                .save(provider);

        BLAST_RECIPES.recipeBuilder("naquadria_sulfate_separation").EUt(VA[ZPM]).duration(600).blastFurnaceTemp(9000)
                .inputItems(dust, NaquadriaSulfate, 6)
                .inputFluids(Hydrogen.getFluid(2000))
                .outputItems(ingotHot, Naquadria)
                .outputFluids(SulfuricAcid.getFluid(1000))
                .save(provider);

        DISTILLATION_RECIPES.recipeBuilder("naquadria_waste_separation").EUt(VA[HV]).duration(300)
                .inputFluids(NaquadriaWaste.getFluid(2000))
                .chancedOutput(dust, GalliumSulfide, 5000, 0)
                .outputFluids(SulfuricAcid.getFluid(500))
                .outputFluids(NaquadriaSolution.getFluid(250))
                .outputFluids(EnrichedNaquadahSolution.getFluid(100))
                .save(provider);

        // TRINIUM

        BLAST_RECIPES.recipeBuilder("trinium_sulfide_separation").duration(750).EUt(VA[LuV])
                .blastFurnaceTemp(Trinium.getBlastTemperature())
                .inputItems(dust, TriniumSulfide, 2)
                .inputItems(dust, Zinc)
                .outputItems(ingotHot, Trinium)
                .outputItems(dust, ZincSulfide, 2)
                .save(provider);

        // BYPRODUCT PROCESSING

        // Titanium Trifluoride
        BLAST_RECIPES.recipeBuilder("titanium_trifluoride_separation").EUt(VA[HV]).duration(900).blastFurnaceTemp(1941)
                .inputItems(dust, TitaniumTrifluoride, 4)
                .inputFluids(Hydrogen.getFluid(3000))
                .outputItems(ingotHot, Titanium)
                .outputFluids(HydrofluoricAcid.getFluid(3000))
                .save(provider);

        // Indium Phosphide
        CHEMICAL_RECIPES.recipeBuilder("indium_phosphide_separation").duration(30).EUt(VA[ULV])
                .inputItems(dust, IndiumPhosphide, 2)
                .inputItems(dust, Calcium)
                .outputItems(dust, Indium)
                .outputItems(dust, CalciumPhosphide, 2)
                .save(provider);
    }
}
