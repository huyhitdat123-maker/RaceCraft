package com.racecraft.races.impl;

import com.racecraft.managers.RaceManager;
import com.racecraft.races.RaceMastery;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.List;

public class RongRace implements RaceMastery {
    @Override public String getId() { return "rong"; }
    @Override public String getDisplayName() { return "Tộc Rồng"; }
    @Override public String getTitlePrefix() { return "Thanh Long"; }
    @Override public NamedTextColor getThemeColor() { return NamedTextColor.DARK_AQUA; }
    @Override public Material getIcon() { return Material.DRAGON_EGG; }
    @Override public double getBonusMaxHealth() { return 8.0; }
    @Override public double getBonusAttackDamage() { return 3.0; }
    @Override public double getBonusArmor() { return 4.0; }
    @Override public double getBonusMoveSpeed() { return -0.008; }

    @Override
    public List<String> getLore() {
        return List.of(
                "Chỉ số: +4 Tim tối đa, +3.0 Sát thương, +4 Giáp tự nhiên.",
                "Nội tại: Miễn nhiễm Lửa & Lava; +20% sát thương ở The End.",
                "Nhược điểm: Giảm 8% tốc độ di chuyển trên cạn."
        );
    }

    @Override
    public void tickPassive(Player player, RaceManager ctx) {
        player.setFireTicks(0);
    }

    @Override
    public void onAttackEntity(Player attacker, LivingEntity target, EntityDamageByEntityEvent event, RaceManager ctx) {
        if (attacker.getWorld().getEnvironment() == World.Environment.THE_END) {
            event.setDamage(event.getDamage() * 1.20);
        }
    }

    @Override
    public void onIncomingDamage(Player victim, EntityDamageEvent event, RaceManager ctx) {
        if (event.getCause() == EntityDamageEvent.DamageCause.FIRE
                || event.getCause() == EntityDamageEvent.DamageCause.FIRE_TICK
                || event.getCause() == EntityDamageEvent.DamageCause.LAVA) {
            event.setCancelled(true);
        }
    }
}
