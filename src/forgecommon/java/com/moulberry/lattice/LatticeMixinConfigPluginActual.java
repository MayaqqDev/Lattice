package com.moulberry.lattice;

import net.minecraftforge.fml.loading.LoadingModList;
import net.msrandom.multiplatform.annotations.Actual;

public class LatticeMixinConfigPluginActual {
    @Actual
    public static String getVersion() {
        return LoadingModList.get().getModFileById("minecraft").versionString();
    }
}
