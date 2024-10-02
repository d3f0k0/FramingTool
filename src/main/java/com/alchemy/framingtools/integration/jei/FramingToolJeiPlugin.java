package com.alchemy.framingtools.integration.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.recipe.VanillaRecipeCategoryUid;

@JEIPlugin
public class FramingToolJeiPlugin implements IModPlugin{
    
    @Override
    public void register(IModRegistry registry) {
        registry.addRecipes(ExampleRecipeMaker.getExampleRecipe(), VanillaRecipeCategoryUid.CRAFTING);
        registry.addRecipes(ExampleRecipeMaker.getOptionalExampleRecipe(), VanillaRecipeCategoryUid.CRAFTING);
    }
}
