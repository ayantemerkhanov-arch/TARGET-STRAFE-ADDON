package com.example.addon;

import com.example.addon.modules.TargetStrafe;
import meteordevelopment.meteorclient.addons.MeteorAddon;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Modules;

public class AddonTemplate extends MeteorAddon {
    public static final Category CATEGORY = new Category("HvH");

    @Override
    public void onInitialize() {
        Modules.get().add(new TargetStrafe());
    }

    @Override
    public void onRegisterCategories() {
        Modules.registerCategory(CATEGORY);
    }

    @Override
    public String getPackage() {
        return "com.example.addon";
    }
}
