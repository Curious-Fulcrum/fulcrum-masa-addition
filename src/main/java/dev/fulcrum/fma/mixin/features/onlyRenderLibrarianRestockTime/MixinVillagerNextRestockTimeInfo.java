package dev.fulcrum.fma.mixin.features.onlyRenderLibrarianRestockTime;

import com.plusls.MasaGadget.impl.feature.entityInfo.VillagerNextRestockTimeInfo;
import dev.fulcrum.fma.Configs;
import net.minecraft.world.entity.npc.VillagerData;
import net.minecraft.world.entity.npc.VillagerProfession;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(VillagerNextRestockTimeInfo.class)
public abstract class MixinVillagerNextRestockTimeInfo {

    @Redirect(method = "getInfo", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/npc/VillagerData;getProfession()Lnet/minecraft/world/entity/npc/VillagerProfession;"))
    private static VillagerProfession setValidProfession(VillagerData instance) {
        return Configs.onlyRenderLibrarianRestockTime.getBooleanValue() ? instance.getProfession() : VillagerProfession.LIBRARIAN;
    }
}
