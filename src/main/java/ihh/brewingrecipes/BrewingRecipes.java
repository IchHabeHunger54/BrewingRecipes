package ihh.brewingrecipes;

import net.minecraft.item.Item;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.potion.Potion;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.fml.DeferredWorkQueue;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.antlr.v4.runtime.misc.Triple;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(BrewingRecipes.MODID)
public class BrewingRecipes {
    public static final String MODID = "brewingrecipes";
    public static final Logger LOGGER = LogManager.getLogger();

    public BrewingRecipes() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(EventPriority.LOWEST, this::finish);
    }

    private void finish(final FMLCommonSetupEvent e) {
        DeferredWorkQueue.runLater(() -> {
            if (Config.CLEAR.get()) {
                PotionReflection.clear();
                LOGGER.info("Removed all brewing recipes");
            }
            for (Triple<Potion, Item, Potion> t : Config.getRecipes()) {
                PotionReflection.addTypeConversion(t.a, t.b, t.c);
                LOGGER.info("Successfully added brewing recipe that takes " + t.a.getRegistryName() + " and item " + t.b.getRegistryName() + " and outputs " + t.c.getRegistryName());
            }
            for (Triple<Item, Item, Item> t : Config.getConversionRecipes()) {
                PotionReflection.addItemConversion(t.a, t.b, t.c);
                BrewingRecipeRegistry.addRecipe(Ingredient.fromItems())
                LOGGER.info("Successfully added brewing conversion recipe that takes " + t.a.getRegistryName() + " and " + t.b.getRegistryName() + " and outputs " + t.c.getRegistryName());
            }
        });
    }
}
