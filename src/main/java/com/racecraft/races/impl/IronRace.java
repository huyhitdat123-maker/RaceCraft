package com.racecraft.races.impl;

import com.racecraft.managers.RaceManager;
import com.racecraft.races.RaceMastery;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.List;

public class IronRace implements RaceMastery {
    @Override public String getId() { return "iron"; }
    @Override public String getDisplayName() { return "Tộc Iron"; }
    @Override public String getTitlePrefix() { return "Thiết Nhân"; }
    @Override public NamedTextColor getThemeColor() { return NamedTextColor.GRAY; }
    @Override public Material getIcon() { return Material.IRON_BLOCK; }
    @Override public double getBonusMaxHealth() { return 10.0; }
    @Override public double getBonusAttackDamage() { return 2.0; }
    @Override public double getBonusArmor() { return 6.0; }
    @Override public double getBonusArmorToughness() { return 2.0; }
    @Override public double getBonusKnockbackResistance() { return 0.60; }
    @Override public double getBonusMoveSpeed() { return -0.015; }
    @Override public double getBonusAttackSpeed() { return -0.40; }

    @Override
    public List<String> getLore() {
        return List.of(
                "Chỉ số: +5 Tim tối đa, +6 Giáp, +2 Độ cứng giáp, +60% Kháng đẩy lùi.",
                "Nội tại: Miễn nhiễm Độc và miễn sát thương rơi dưới 10 block.",
                "Nhược điểm: Giảm 15% tốc chạy, đánh chậm và chìm nhanh dưới nước."
        );
    }

    @Override
    public void tickPassive(Player player, RaceManager ctx) {
        if (player.hasPotionEffect(PotionEffectType.POISON)) {
            player.removePotionEffect(PotionEffectType.POISON);
        }
        if (player.isInWater()) {
            Vector vel = player.getVelocity();
            player.setVelocity(vel.setY(Math.min(vel.getY(), -0.18)));
        }
    }

    @Override
    public void onIncomingDamage(Player victim, EntityDamageEvent event, RaceManager ctx) {
        if (event.getCause() == EntityDamageEvent.DamageCause.FALL && victim.getFallDistance() <= 10.0f) {
            event.setCancelled(true);
        } else if (event.getCause() == EntityDamageEvent.DamageCause.POISON) {
            event.setCancelled(true);
        }
    }
}
