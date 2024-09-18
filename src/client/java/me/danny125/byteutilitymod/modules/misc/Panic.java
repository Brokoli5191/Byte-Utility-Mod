package me.danny125.byteutilitymod.modules.misc;

import me.danny125.byteutilitymod.Initialize;
import me.danny125.byteutilitymod.modules.Module;

public class Panic extends Module {
    public Panic() {
        super("Panic",0,CATEGORY.MISCELLANEOUS,false);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        for(Module module : Initialize.modules){
            module.toggled = false;
        }
    }
}