package com.moulberry.lattice;

import net.msrandom.multiplatform.annotations.Expect;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class LatticeMixinConfigPlugin implements IMixinConfigPlugin {

    private String mixinPackage = null;
    private static String version = "";

    // Trust me.
    private static final Map<String, Integer> versionsRanked = Map.ofEntries(
        Map.entry("1.20.1", 0),
        Map.entry("1.20.2", 1),
        Map.entry("1.20.4", 2),
        Map.entry("1.20.6", 3),
        Map.entry("1.21.1", 4),
        Map.entry("1.21.3", 5),
        Map.entry("1.21.4", 6),
        Map.entry("1.21.5", 7),
        Map.entry("1.21.6", 8),
        Map.entry("1.21.9", 9),
        Map.entry("1.21.11", 10)
    );

    private static final Map<String, List<String>> versionedMixinMap = Map.ofEntries(
        Map.entry("multiversion.MixinDrawString", List.of("1.20.1", "1.20.6", "1.21.6")),
        Map.entry("multiversion.MixinMouseScrolled", List.of("1.20.1", "1.20.2")),
        Map.entry("multiversion.MixinNewMultiLineEditBox", List.of("1.20.1", "1.20.6", "1.21.6")),
        Map.entry("multiversion.MixinNewCycleButton", List.of("1.20.1", "1.20.6", "1.21.11")),
        Map.entry("multiversion.MixinOffsetZ", List.of("1.20.1", "1.20.6", "1.21.6")),
        Map.entry("multiversion.MixinGetWindowHandle", List.of("1.20.1", "1.20.6", "1.21.9")),
        Map.entry("MixinDropdownWidget", List.of("1.20.1", "1.20.2", "1.20.4", "1.20.6")),
        Map.entry("MixinLatticeConfigScreen", List.of("1.20.1", "1.20.2", "1.20.6", "1.21.6")),
        Map.entry("MixinWidgetWithText", List.of("1.20.1", "1.20.2")),
        Map.entry("GuiEventListenerMixins", List.of("1.20.1", "1.20.6", "1.21.9")),
        Map.entry("MixinDropdownWidgetEntry", List.of("1.21.9")),
        Map.entry("MixinButtonRenderContent", List.of("1.21.11"))
    );

    @Expect
    public static String getVersion();

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        Objects.requireNonNull(this.mixinPackage);

        if (mixinClassName.startsWith(this.mixinPackage + ".")) {
            version = getVersion();

            String mixinName = mixinClassName.substring(this.mixinPackage.length() + 1);
            mixinName = mixinName.split("\\$")[0];
            List<String> versionsForMixin = versionedMixinMap.get(mixinName);

            if (versionsForMixin == null) {
                throw new RuntimeException("Missing versions for " + mixinName);
            }

            Integer latestApplicableVersion = null;
            String applyVersionName = null;
            boolean found = false;

            for (String mixinVersionName : versionsForMixin) {
                int mixinVersion = versionsRanked.get(mixinVersionName);
                var idVersion = "v" + mixinVersionName.replaceAll("\\.", "");

                if (this.mixinPackage.endsWith("." + idVersion)) {
                    found = true;
                }

                int currentVersionOrdinal = versionsRanked.get(version);

                if (mixinVersion <= currentVersionOrdinal && (latestApplicableVersion == null || currentVersionOrdinal > latestApplicableVersion)) {
                    latestApplicableVersion = mixinVersion;
                    applyVersionName = idVersion;
                }
            }

            if (!found) {
                throw new RuntimeException("Mixin " + mixinClassName + " isn't included in versioned map");
            }
            if (applyVersionName == null) {
                return false;
            }

            return this.mixinPackage.endsWith("." + applyVersionName);
        } else {
            throw new RuntimeException("Unexpected mixin package: " + mixinClassName);
        }
    }

    @Override
    public void onLoad(String mixinPackage) {
        this.mixinPackage = mixinPackage;
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }

}
