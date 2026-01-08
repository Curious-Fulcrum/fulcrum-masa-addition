package dev.fulcrum.fma.mixin.features.villagerRestockTime;

import dev.fulcrum.fma.config.Configs;
import dev.fulcrum.fma.features.VillagerUtils;
import fi.dy.masa.minihud.renderer.OverlayRendererVillagerInfo;
import fi.dy.masa.minihud.util.EntityUtils;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.tuple.Pair;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;

@Mixin(OverlayRendererVillagerInfo.class)
public abstract class MixinOverlayRendererVillagerInfo {

    @Shadow
    protected abstract void renderAtEntity(List<String> texts, Entity entity, Entity targetEntity);

    @Shadow
    protected abstract MerchantOffers getTrades(Level world, Villager villager);

    @Shadow
    protected abstract Pair<Entity, CompoundTag> getVillagerData(Level world, int entityId);

    @Unique
    private final Int2ObjectArrayMap<String> renderCache = new Int2ObjectArrayMap<>();

    @Inject(
            method = "update",
            at = @At(
                    value = "FIELD",
                    target = "Lfi/dy/masa/minihud/config/Configs$Generic;VILLAGER_OFFER_ENCHANTMENT_BOOKS:Lfi/dy/masa/malilib/config/options/ConfigBoolean;",
                    opcode = Opcodes.GETSTATIC
            ),
            locals = LocalCapture.CAPTURE_FAILSOFT
    )
    private void renderRestockTime(Vec3 cameraPos, Entity entity, Minecraft mc, ProfilerFiller profiler,
                                   CallbackInfo ci, AABB box, Level world) {
        if (world == null || !Configs.villagerRestockTime.getBooleanValue()) return;
        var villagers = EntityUtils.getEntitiesByClass(mc, Villager.class, box, VillagerUtils::tradable);
        renderCache.clear();
        for (var villager : villagers) {
            var offers = this.getTrades(world, villager);
            if (offers == null || offers.isEmpty()) continue;
            int id = villager.getId();
            String restockInfo = VillagerUtils.getRestockInfo(villager, offers, this.getVillagerData(world, id));
            if (fi.dy.masa.minihud.config.Configs.Generic.VILLAGER_OFFER_ENCHANTMENT_BOOKS.getBooleanValue()
                    && villager.getVillagerData().profession().is(VillagerProfession.LIBRARIAN))
                renderCache.put(id, restockInfo);
            else
                this.renderAtEntity(List.of(restockInfo), entity, villager);
        }
    }

    @ModifyArg(
            method = "update",
            at = @At(
                    value = "INVOKE",
                    target = "Lfi/dy/masa/minihud/renderer/OverlayRendererVillagerInfo;renderAtEntity(Ljava/util/List;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/entity/Entity;)V",
                    ordinal = 0
            )
    )
    private List<String> compatLibrarian(List<String> texts, Entity entity, Entity target) {
        if (Configs.villagerRestockTime.getBooleanValue()) {
            String cache = renderCache.get(target.getId());
            if (cache != null)    texts.add(cache);
        }
        return texts;
    }
}
