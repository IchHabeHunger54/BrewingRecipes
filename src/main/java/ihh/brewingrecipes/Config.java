package ihh.brewingrecipes;

import net.minecraft.item.Item;
import net.minecraft.potion.Potion;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.ResourceLocationException;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.registries.ForgeRegistries;
import org.antlr.v4.runtime.misc.Triple;

import java.util.ArrayList;
import java.util.List;

public class Config {
    public static ForgeConfigSpec SPEC;
    public static ForgeConfigSpec.BooleanValue CLEAR;
    private static ForgeConfigSpec.ConfigValue<List<String>> RECIPES;
    private static ForgeConfigSpec.ConfigValue<List<String>> CONVERSION_RECIPES;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        CLEAR = builder.comment("Clears the entire brewing recipe registry and adds only the recipes below").define("clear", false);
        RECIPES = builder.comment("Specify new recipes here.").comment("Format is \"input;item;output\", with \"input\" being a potion (e. g. \"minecraft:water\"), \"item\" being an item (e. g. \"minecraft:nether_wart\") and output being another potion (e. g. \"minecraft:awkward\"). Note that this will define recipes for all three potion types (normal, splash and lingering)").define("recipes", new ArrayList<>());
        CONVERSION_RECIPES = builder.comment("Specify new \"potion -> splash potion\", \"potion -> lingering potion\" and \"splash potion -> lingering potion\" (and vice versa) recipes here.").comment("Format is \"from;to;potion;item\", with \"from\" being either \"potion\", \"splash\" or \"lingering\", \"to\" being either \"potion\", \"splash\" or \"lingering\" and \"item\" being the item (e. g. \"minecraft:gunpowder\") used for the conversion. \"from\" and \"to\" must not be the same!").define("conversion_recipes", new ArrayList<>());
        SPEC = builder.build();
    }

    public static List<Triple<Potion, Item, Potion>> getRecipes() {
        List<Triple<Potion, Item, Potion>> l = new ArrayList<>();
        for (String p : RECIPES.get()) {
            String[] s = p.split(";");
            if (s.length != 3) {
                BrewingRecipes.LOGGER.error("Brewing recipe " + p + " needs exactly three entries!");
                continue;
            }
            try {
                Potion in = ForgeRegistries.POTION_TYPES.getValue(new ResourceLocation(s[0]));
                Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(s[1]));
                Potion out = ForgeRegistries.POTION_TYPES.getValue(new ResourceLocation(s[2]));
                if (in == null) BrewingRecipes.LOGGER.error("Invalid potion " + s[0]);
                else if (item == null) BrewingRecipes.LOGGER.error("Invalid item " + s[1]);
                else if (out == null) BrewingRecipes.LOGGER.error("Invalid potion " + s[2]);
                else if (in == out)
                    BrewingRecipes.LOGGER.error("Input potion and output potion must not be the same! Occurs in brewing recipe: " + p);
                else l.add(new Triple<>(in, item, out));
            } catch (ResourceLocationException e) {
                BrewingRecipes.LOGGER.error("Caught resource location exception while trying to build recipe " + p + "! Stacktrace:");
                e.printStackTrace();
            }
        }
        return l;
    }

    public static List<Triple<Item, Item, Item>> getConversionRecipes() {
        List<Triple<Item, Item, Item>> l = new ArrayList<>();
        for (String p : CONVERSION_RECIPES.get()) {
            String[] s = p.split(";");
            if (s.length != 3) {
                BrewingRecipes.LOGGER.error("Brewing recipe " + p + " needs exactly three entries!");
                continue;
            }
            try {
                Item in = ForgeRegistries.ITEMS.getValue(new ResourceLocation(s[0]));
                Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(s[1]));
                Item out = ForgeRegistries.ITEMS.getValue(new ResourceLocation(s[2]));
                if (in == null) BrewingRecipes.LOGGER.error("Invalid item " + s[0]);
                else if (item == null) BrewingRecipes.LOGGER.error("Invalid item " + s[1]);
                else if (out == null) BrewingRecipes.LOGGER.error("Invalid item " + s[2]);
                else if (in == out)
                    BrewingRecipes.LOGGER.error("Input item and output item must not be the same! Occurs in brewing recipe: " + p);
                else l.add(new Triple<>(in, item, out));
            } catch (ResourceLocationException e) {
                BrewingRecipes.LOGGER.error("Caught resource location exception while trying to build recipe " + p + "! Stacktrace:");
                e.printStackTrace();
            }
        }
        return l;
    }
}
