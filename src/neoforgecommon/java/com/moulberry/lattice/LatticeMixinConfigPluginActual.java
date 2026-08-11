package com.moulberry.lattice;

import net.msrandom.multiplatform.annotations.Actual;
import net.neoforged.fml.loading.LoadingModList;

public class LatticeMixinConfigPluginActual {
    @Actual
    public static String getVersion() {
        return LoadingModList.get().getModFileById("minecraft").versionString();
    }
}
