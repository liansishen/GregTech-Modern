package com.gregtechceu.gtceu.common.data.materials;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.gregtechceu.gtceu.api.data.chemical.material.properties.FluidProperty;
import com.gregtechceu.gtceu.api.data.chemical.material.properties.PropertyKey;
import com.gregtechceu.gtceu.api.fluids.FluidBuilder;
import com.gregtechceu.gtceu.api.fluids.attribute.FluidAttributes;
import com.gregtechceu.gtceu.api.fluids.store.FluidStorageKeys;

import static com.gregtechceu.gtceu.api.data.chemical.material.info.MaterialFlags.DISABLE_DECOMPOSITION;
import static com.gregtechceu.gtceu.api.data.chemical.material.info.MaterialFlags.NO_UNIFICATION;
import static com.gregtechceu.gtceu.api.data.chemical.material.info.MaterialIconSet.*;
import static com.gregtechceu.gtceu.api.data.chemical.material.info.MaterialIconSet.ROUGH;
import static com.gregtechceu.gtceu.common.data.GTMaterials.*;

public class GTLMaterials {

    public static void register() {
        RutheniumTetroxide.setProperty(PropertyKey.FLUID, new FluidProperty());
        RutheniumTetroxide.getProperty(PropertyKey.FLUID).getStorage().enqueueRegistration(FluidStorageKeys.LIQUID,
                new FluidBuilder());
        RutheniumTetroxide.addFlags(NO_UNIFICATION);
        ZincSulfate = new Material.Builder(GTCEu.id("zinc_sulfate"))
                .dust()
                .components(Zinc, 1, Sulfur, 1, Oxygen, 4)
                .color(0x533c1b).iconSet(SAND)
                .buildAndRegister();
        RhodiumNitrate = new Material.Builder(GTCEu.id("rhodium_nitrate"))
                .dust()
                .color(0x8C5A0C).iconSet(SAND)
                .flags(DISABLE_DECOMPOSITION)
                .components(Rhodium, 1, Nitrogen, 1, Oxygen, 3)
                .buildAndRegister();
        RoughlyRhodiumMetal = new Material.Builder(GTCEu.id("roughly_rhodium_metal"))
                .dust()
                .color(0x594C1A).iconSet(SAND)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister().setFormula("?Rh?");
        ReprecipitatedRhodium = new Material.Builder(GTCEu.id("reprecipitated_rhodium"))
                .dust()
                .color(0xD40849).iconSet(SAND)
                .flags(DISABLE_DECOMPOSITION)
                .components(Rhodium, 2, Nitrogen, 1, Hydrogen, 4)
                .buildAndRegister();
        SodiumNitrate = new Material.Builder(GTCEu.id("sodium_nitrate"))
                .dust()
                .color(0x4e2a40).iconSet(SAND)
                .flags(DISABLE_DECOMPOSITION)
                .components(Sodium, 1, Nitrogen, 1, Oxygen, 3)
                .buildAndRegister();
        RhodiumSalt = new Material.Builder(GTCEu.id("rhodium_salt"))
                .dust()
                .color(0x61200A).iconSet(SAND)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister().setFormula("NaRhCl?");;
        RhodiumSaltSolution = new Material.Builder(GTCEu.id("rhodium_salt_solution"))
                .fluid()
                .color(0x61200A).iconSet(SAND)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();
        RhodiumFilterCake = new Material.Builder(GTCEu.id("rhodium_filter_cake"))
                .dust()
                .color(0x87350C).iconSet(ROUGH)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister().setFormula("?Ru?");
        RhodiumFilterCakeSolution = new Material.Builder(GTCEu.id("rhodium_filter_cake_solution"))
                .fluid()
                .color(0x87350C).iconSet(ROUGH)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister().setFormula("?Ru?");
        SodiumRutheniate = new Material.Builder(GTCEu.id("sodium_rutheniate"))
                .dust()
                .color(0x282C8C).iconSet(METALLIC)
                .flags(DISABLE_DECOMPOSITION)
                .components(Sodium, 2, Rhodium, 1, Oxygen, 3)
                .buildAndRegister();
        IridiumDioxide = new Material.Builder(GTCEu.id("iridium_dioxide"))
                .dust()
                .color(0xA2BFFF).iconSet(METALLIC)
                .flags(DISABLE_DECOMPOSITION)
                .components(Iridium, 1, Oxygen, 2)
                .buildAndRegister();
        RutheniumTetroxideLQ = new Material.Builder(GTCEu.id("ruthenium_tetroxide_lq"))
                .fluid()
                .color(0xA8A8A8).iconSet(ROUGH)
                .buildAndRegister();
        SodiumFormate = new Material.Builder(GTCEu.id("sodium_formate"))
                .fluid()
                .color(0xf1939c).iconSet(ROUGH)
                .buildAndRegister();
        RhodiumSulfateGas = new Material.Builder(GTCEu.id("rhodium_sulfate_gas"))
                .gas()
                .color(0xBD8743).iconSet(ROUGH)
                .buildAndRegister();
        AcidicIridium = new Material.Builder(GTCEu.id("acidic_iridium"))
                .gas()
                .color(0x634E3A).iconSet(ROUGH)
                .buildAndRegister().setFormula("???");
        RutheniumTetroxideHot = new Material.Builder(GTCEu.id("ruthenium_tetroxide_hot"))
                .gas()
                .color(0x9B9B9B).iconSet(ROUGH)
                .buildAndRegister();
        SodiumSulfate = new Material.Builder(GTCEu.id("sodium_sulfate"))
                .dust()
                .components(Sodium, 2, Sulfur, 1, Oxygen, 4)
                .color(0xF9F6CF).iconSet(SAND)
                .buildAndRegister();

        MutatedLivingSolder = new Material.Builder(GTCEu.id("mutated_living_solder"))
                .fluid()
                .color(0xC18FCC).iconSet(METALLIC)
                .radioactiveHazard(1)
                .buildAndRegister();
        SuperMutatedLivingSolder = new Material.Builder(GTCEu.id("super_mutated_living_solder"))
                .fluid()
                .color(0xB662FF).iconSet(METALLIC)
                .radioactiveHazard(2)
                .buildAndRegister();
        Grade8PurifiedWater = new Material.Builder(GTCEu.id("grade_8_purified_water"))
                .fluid()
                .color(0x0058CD).iconSet(FLUID)
                .components(Water, 1)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();
        Grade16PurifiedWater = new Material.Builder(GTCEu.id("grade_16_purified_water"))
                .fluid()
                .color(0x0058CD).iconSet(FLUID)
                .components(Water, 1)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        HexafluorideEnrichedNaquadahSolution = new Material.Builder(GTCEu.id("hexafluoride_enriched_naquadah_solution"))
                .fluid()
                .color(0x868D7F)
                .components(NaquadahEnriched, 1, Fluorine, 6)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();
        XenonHexafluoroEnrichedNaquadate = new Material.Builder(GTCEu.id("xenon_hexafluoro_enriched_naquadate"))
                .fluid()
                .color(0x255A55)
                .components(Xenon, 1, NaquadahEnriched, 1, Fluorine, 6)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();
        GoldTrifluoride = new Material.Builder(GTCEu.id("gold_trifluoride"))
                .dust()
                .color(0xE8C478)
                .iconSet(BRIGHT)
                .components(Gold, 1, Fluorine, 3)
                .buildAndRegister();
        XenoauricFluoroantimonicAcid = new Material.Builder(GTCEu.id("xenoauric_fluoroantimonic_acid"))
                .fluid(FluidStorageKeys.LIQUID, new FluidBuilder().attribute(FluidAttributes.ACID))
                .color(0xE0BD74)
                .components(Xenon, 1, Gold, 1, Antimony, 1, Fluorine, 6)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();
        GoldChloride = new Material.Builder(GTCEu.id("gold_chloride"))
                .fluid()
                .color(0xCCCC66)
                .components(Gold, 2, Chlorine, 6)
                .buildAndRegister();
        BromineTrifluoride = new Material.Builder(GTCEu.id("bromine_trifluoride"))
                .fluid()
                .color(0xA88E57)
                .components(Bromine, 1, Fluorine, 3)
                .buildAndRegister();
        HexafluorideNaquadriaSolution = new Material.Builder(GTCEu.id("hexafluoride_naquadria_solution"))
                .fluid()
                .color(0x25C213)
                .components(Naquadria, 1, Fluorine, 6)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();
        RadonDifluoride = new Material.Builder(GTCEu.id("radon_difluoride"))
                .fluid()
                .color(0x8B7EFF)
                .components(Radon, 1, Fluorine, 2)
                .buildAndRegister();
        RadonNaquadriaOctafluoride = new Material.Builder(GTCEu.id("radon_naquadria_octafluoride"))
                .fluid()
                .color(0x85F378)
                .components(Radon, 1, Naquadria, 1, Fluorine, 8)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();
        CaesiumFluoride = new Material.Builder(GTCEu.id("caesium_fluoride"))
                .fluid()
                .color(0xFF7A5F)
                .components(Caesium, 1, Fluorine, 1)
                .buildAndRegister();
        XenonTrioxide = new Material.Builder(GTCEu.id("xenon_trioxide"))
                .fluid()
                .color(0x359FC3)
                .components(Xenon, 1, Oxygen, 3)
                .buildAndRegister();
        CaesiumXenontrioxideFluoride = new Material.Builder(GTCEu.id("caesium_xenontrioxide_fluoride"))
                .fluid()
                .color(0x5067D7)
                .flags(DISABLE_DECOMPOSITION)
                .components(Caesium, 1, Xenon, 1, Oxygen, 3, Fluorine, 1)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();
        NaquadriaCaesiumXenonnonfluoride = new Material.Builder(GTCEu.id("naquadria_caesium_xenonnonfluoride"))
                .fluid()
                .color(0x54C248)
                .components(Naquadria, 1, Caesium, 1, Xenon, 1, Fluorine, 9)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();
        RadonTrioxide = new Material.Builder(GTCEu.id("radon_trioxide"))
                .fluid()
                .color(0x9A6DD7)
                .components(Radon, 1, Oxygen, 3)
                .buildAndRegister();
        NaquadriaCaesiumfluoride = new Material.Builder(GTCEu.id("naquadria_caesiumfluoride"))
                .fluid()
                .color(0xAAEB69)
                .components(Naquadria, 1, Fluorine, 3, Caesium, 1)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister()
                .setFormula("*Nq*F2CsF", true);
        NitrosoniumOctafluoroxenate = new Material.Builder(GTCEu.id("nitrosonium_octafluoroxenate"))
                .fluid()
                .color(0x953D9F)
                .components(NitrogenDioxide, 2, Xenon, 1, Fluorine, 8)
                .buildAndRegister()
                .setFormula("(NO2)2XeF8", true);
        NitrylFluoride = new Material.Builder(GTCEu.id("nitryl_fluoride"))
                .fluid()
                .color(0x8B7EFF)
                .components(Nitrogen, 1, Oxygen, 2, Fluorine, 1)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();
        AcidicNaquadriaCaesiumfluoride = new Material.Builder(GTCEu.id("acidic_naquadria_caesiumfluoride"))
                .fluid()
                .color(0x75EB00)
                .components(Naquadria, 1, Fluorine, 3, Caesium, 1, Sulfur, 2, Oxygen, 8)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister()
                .setFormula("*Nq*F2CsF(SO4)2", true);
    }
}
