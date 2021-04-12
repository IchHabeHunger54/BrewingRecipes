package ihh.brewingrecipes;

import net.minecraft.item.Item;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionBrewing;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.registries.ForgeRegistryEntry;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

//Modified variant of https://github.com/Vazkii/Quark/blob/master/src/main/java/vazkii/quark/base/util/PotionReflection.java
@SuppressWarnings({"unchecked", "rawtypes"})
public class PotionReflection {
    private static final MethodHandle MIX_PREDICATE_CONSTRUCTOR, GET_POTION_TYPE_CONVERSIONS, GET_POTION_ITEM_CONVERSIONS;

    static {
        try {
            Class<?> mixPredicate = Class.forName("net.minecraft.potion.PotionBrewing$MixPredicate");
            MethodType type = MethodType.methodType(Void.TYPE, ForgeRegistryEntry.class, Ingredient.class, ForgeRegistryEntry.class);
            Constructor<?> constructor = mixPredicate.getConstructor(type.parameterArray());
            constructor.setAccessible(true);
            MIX_PREDICATE_CONSTRUCTOR = MethodHandles.lookup().unreflectConstructor(constructor).asType(type.changeReturnType(Object.class));
            Field typeConversions = ObfuscationReflectionHelper.findField(PotionBrewing.class, "field_185213_a"); // POTION_TYPE_CONVERSIONS
            GET_POTION_TYPE_CONVERSIONS = MethodHandles.lookup().unreflectGetter(typeConversions).asType(MethodType.methodType(List.class));
            Field itemConversions = ObfuscationReflectionHelper.findField(PotionBrewing.class, "field_185214_b"); // POTION_ITEM_CONVERSIONS
            GET_POTION_ITEM_CONVERSIONS = MethodHandles.lookup().unreflectGetter(itemConversions).asType(MethodType.methodType(List.class));
        } catch (IllegalAccessException | ClassNotFoundException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public static void addTypeConversion(Potion input, Item reagent, Potion output) {
        try {
            Object mixPredicate = MIX_PREDICATE_CONSTRUCTOR.invokeExact((ForgeRegistryEntry) input, Ingredient.fromItems(reagent), (ForgeRegistryEntry) output);
            List<Object> itemConversions = (List) GET_POTION_TYPE_CONVERSIONS.invokeExact();
            itemConversions.add(mixPredicate);
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    public static void addItemConversion(Item input, Item reagent, Item output) {
        try {
            Object mixPredicate = MIX_PREDICATE_CONSTRUCTOR.invokeExact((ForgeRegistryEntry) input, Ingredient.fromItems(reagent), (ForgeRegistryEntry) output);
            List<Object> typeConversions = (List) GET_POTION_ITEM_CONVERSIONS.invokeExact();
            typeConversions.add(mixPredicate);
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    public static void clear() {
        PotionBrewing.POTION_TYPE_CONVERSIONS.clear();
        PotionBrewing.POTION_ITEM_CONVERSIONS.clear();
        try {
            Field field = BrewingRecipeRegistry.class.getDeclaredField("recipes");
            field.setAccessible(true);
            field.set(null, new ArrayList<>());
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }
}