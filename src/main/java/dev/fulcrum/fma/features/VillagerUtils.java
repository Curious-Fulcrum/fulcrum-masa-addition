package dev.fulcrum.fma.features;

import dev.fulcrum.fma.mixin.accessor.VillagerAccessor;
import fi.dy.masa.malilib.gui.GuiBase;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import org.apache.commons.lang3.tuple.Pair;

public abstract class VillagerUtils {

    private static final String TRADE_PREPARED = GuiBase.TXT_GREEN + "OK";

    public static boolean tradable(Villager villager) {
        var profession = villager.getVillagerData().profession();
        return !(profession.is(VillagerProfession.NONE) || profession.is(VillagerProfession.NITWIT));
    }

    // restock

    /// copy from [MasaGadget](https://github.com/plusls/MasaGadget/blob/main/src/main/java/com/plusls/MasaGadget/impl/feature/entityInfo/VillagerNextRestockTimeInfo.java)
    public static String getRestockInfo(Villager villager, MerchantOffers offers, Pair<Entity, CompoundTag> villagerData) {
        long nextRestockTime;
        long nextWorkTime;
        var level = villager.level();
        long timeOfDay = level.getDayTime() % 24000;

        if (timeOfDay >= 2000 && timeOfDay <= 9000) {
            nextWorkTime = 0;
        } else {
            nextWorkTime = timeOfDay < 2000 ? 2000 - timeOfDay : 24000 - timeOfDay + 2000;
        }


        int numberOfRestocksToday = -1;
        long lastRestockGameTime = -1;

        if (villagerData != null) {
            var nbt = villagerData.getRight();
            if (nbt != null && !nbt.isEmpty()) {
                if (nbt.contains("RestocksToday"))
                    numberOfRestocksToday = nbt.getIntOr("RestocksToday", -1);
                if (nbt.contains("LastRestock"))
                    lastRestockGameTime = nbt.getIntOr("LastRestock", -1);
            } else {
                var entity = villagerData.getLeft();
                if (entity instanceof Villager serverVillager) {
                    numberOfRestocksToday = ((VillagerAccessor) serverVillager).getNumberOfRestocksToday();
                    lastRestockGameTime = ((VillagerAccessor) serverVillager).getLastRestockGameTime();
                }
            }
        }

        if (numberOfRestocksToday == 0) {
            nextRestockTime = 0;
        } else if (numberOfRestocksToday < 2) {
            nextRestockTime = Math.max(lastRestockGameTime + 2400 - level.getGameTime(), 0);
        } else {
            nextRestockTime = 0x7fffffffffffffffL;
        }

        nextRestockTime = Math.min(nextRestockTime, Math.max(lastRestockGameTime + 12000L - level.getGameTime(), 0));

        if (needsRestock(offers)) {
            if (timeOfDay + nextRestockTime > 8000) {
                nextRestockTime = 24000 - timeOfDay + 2000;
            } else {
                nextRestockTime = Math.max(nextRestockTime, nextWorkTime);
            }
        } else {
            nextRestockTime = 0;
        }

        if (nextRestockTime == 0) {
            return TRADE_PREPARED;
        }

        return nextRestockTime / 20 + "s";
    }

    private static boolean needsRestock(MerchantOffers offers) {
        return offers.stream().anyMatch(MerchantOffer::isOutOfStock);
    }
}
