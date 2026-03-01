package dev.itsthatnova.fpcameraheight;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import dev.itsthatnova.fpcameraheight.screen.FPCameraHeightScreen;

public class ModMenuIntegration implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> new FPCameraHeightScreen(parent);
    }
}
