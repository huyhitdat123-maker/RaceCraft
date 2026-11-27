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

public class ChimRace implements RaceMastery {
    @Override public String getId() { return "chim"; }
    @Override public String getDisplayName() { return "Tộc Chim"; }
    @Override public String getTitlePrefix() { return "Ưng Tộc"; }
    @Override public NamedTextColor getThemeColor() { return NamedTextColor.AQUA; }
    @Override public Material getIcon() { return Material.PHANTOM_MEMBRANE; }
    @Override public double getBonusMaxHealth() { return -4.0; }
    @Override public double getBonusMoveSpeed() { return 0.020; }
    @Override public double getBonusJumpStrength() { return 0.14; }

    @Override
    public List<String> getLore() {
        return List.of(
                "Chỉ số: +20% Tốc chạy, nhảy rất cao, miễn nhiễm sát thương rơi.",
                "Nội tại: Ở trên cao (Y > 90) tăng mạnh tốc độ và +20% sát thương.",
                "Nhược điểm: -2 Tim tối đa; xuống hang sâu (Y < 40) bị nhận thêm sát thương."
        );
    }

    @Override
    public void tickPassive(Player player, RaceManager ctx) {
        if (player.getLocation().getBlockY() > 90) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 40, 1, true, false));
        }
    }

    @Override
    public void onAttackEntity(Player attacker, LivingEntity target, EntityDamageByEntityEvent event, RaceManager ctx) {
        if (attacker.getLocation().getBlockY() > 90 || !attacker.isOnGround()) {
            event.setDamage(event.getDamage() * 1.20);
        }
    }

    @Override
    public void onIncomingDamage(Player victim, EntityDamageEvent event, RaceManager ctx) {
        if (event.getCause() == EntityDamageEvent.DamageCause.FALL) {
            event.setCancelled(true);
            return;
        }
        if (victim.getLocation().getBlockY() < 40) {
            event.setDamage(event.getDamage() * 1.20);
        }
    }
}
