package com.racecraft.races.impl;

import com.racecraft.managers.RaceManager;
import com.racecraft.races.RaceMastery;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class XuongDenRace implements RaceMastery {
    @Override public String getId() { return "xuongden"; }
    @Override public String getDisplayName() { return "Tộc Xương Đen"; }
    @Override public String getTitlePrefix() { return "U Minh"; }
    @Override public NamedTextColor getThemeColor() { return NamedTextColor.DARK_GRAY; }
    @Override public Material getIcon() { return Material.WITHER_SKELETON_SKULL; }
    @Override public double getBonusAttackDamage() { return 2.5; }
    @Override public double getBonusAttackSpeed() { return 0.40; }

    @Override
    public List<String> getLore() {
        return List.of(
                "Chỉ số: +2.5 Sát thương (+1.5 khi cầm Kiếm), +10% Tốc đánh.",
                "Nội tại: Miễn nhiễm Lửa, Lava, Héo Úa; 30% gây Héo Úa khi đánh.",
                "Nhược điểm: Nhận thêm 25% sát thương từ vũ khí có phù phép Trừng Phạt."
        );
    }

    @Override
    public void tickPassive(Player player, RaceManager ctx) {
        player.setFireTicks(0);
        if (player.hasPotionEffect(PotionEffectType.WITHER)) {
            player.removePotionEffect(PotionEffectType.WITHER);
        }
    }

    @Override
    public void onAttackEntity(Player attacker, LivingEntity target, EntityDamageByEntityEvent event, RaceManager ctx) {
        if (attacker.getInventory().getItemInMainHand().getType().name().endsWith("_SWORD")) {
            event.setDamage(event.getDamage() + 1.5);
        }
        if (ThreadLocalRandom.current().nextDouble() < 0.30) {
            target.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 60, 0));
        }
    }

    @Override
    public void onIncomingDamage(Player victim, EntityDamageEvent event, RaceManager ctx) {
        EntityDamageEvent.DamageCause c = event.getCause();
        if (c == EntityDamageEvent.DamageCause.FIRE || c == EntityDamageEvent.DamageCause.FIRE_TICK
                || c == EntityDamageEvent.DamageCause.LAVA || c == EntityDamageEvent.DamageCause.WITHER) {
            event.setCancelled(true);
        }
    }
}
