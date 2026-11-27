package com.racecraft.races.impl;

import com.racecraft.managers.RaceManager;
import com.racecraft.races.RaceMastery;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.List;

public class HoRace implements RaceMastery {
    @Override public String getId() { return "ho"; }
    @Override public String getDisplayName() { return "Tộc Hổ"; }
    @Override public String getTitlePrefix() { return "Bạch Hổ"; }
    @Override public NamedTextColor getThemeColor() { return NamedTextColor.WHITE; }
    @Override public Material getIcon() { return Material.BEEF; }
    @Override public double getBonusAttackDamage() { return 3.0; }
    @Override public double getBonusMoveSpeed() { return 0.018; }
    @Override public double getBonusAttackSpeed() { return 0.50; }
    @Override public double getBonusJumpStrength() { return 0.10; }
    @Override public double getBonusArmor() { return -2.0; }

    @Override
    public List<String> getLore() {
        return List.of(
                "Chỉ số: +18% Tốc chạy, +3.0 Sát thương, +15% Tốc đánh, nhảy cao.",
                "Nội tại: Giảm 50% sát thương rơi; đánh khi chạy nước rút gây +25% sát thương.",
                "Nhược điểm: Giáp tự nhiên giảm 2.0 điểm."
        );
    }

    @Override
    public void onAttackEntity(Player attacker, LivingEntity target, EntityDamageByEntityEvent event, RaceManager ctx) {
        if (attacker.isSprinting()) {
            event.setDamage(event.getDamage() * 1.25);
        }
    }

    @Override
    public void onIncomingDamage(Player victim, EntityDamageEvent event, RaceManager ctx) {
        if (event.getCause() == EntityDamageEvent.DamageCause.FALL) {
            event.setDamage(event.getDamage() * 0.50);
        }
    }
}
