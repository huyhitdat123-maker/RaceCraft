package com.racecraft.races.impl;

import com.racecraft.managers.RaceManager;
import com.racecraft.races.RaceMastery;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

public class NguoiLunRace implements RaceMastery {
    @Override public String getId() { return "nguoilun"; }
    @Override public String getDisplayName() { return "Tộc Người Lùn"; }
    @Override public String getTitlePrefix() { return "Địa Tinh"; }
    @Override public NamedTextColor getThemeColor() { return NamedTextColor.GOLD; }
    @Override public Material getIcon() { return Material.DIAMOND_PICKAXE; }
    @Override public double getBonusArmor() { return 4.0; }
    @Override public double getBonusKnockbackResistance() { return 0.35; }
    @Override public double getBonusMoveSpeed() { return -0.01; }
    @Override public double getBonusJumpStrength() { return -0.06; }

    @Override
    public List<String> getLore() {
        return List.of(
                "Chỉ số: +4 Giáp, +35% Kháng đẩy lùi, +2.0 sát thương khi cầm Cúp/Rìu.",
                "Nội tại: Dưới lòng đất (Y < 50) nhận Đào Nhanh, Nhìn Đêm & +20% XP.",
                "Nhược điểm: Chạy chậm 10% và nhảy thấp hơn bình thường."
        );
    }

    @Override
    public void tickPassive(Player player, RaceManager ctx) {
        if (player.getLocation().getBlockY() < 50) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.HASTE, 40, 1, true, false));
            player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 240, 0, true, false));
        }
    }

    @Override
    public void onAttackEntity(Player attacker, LivingEntity target, EntityDamageByEntityEvent event, RaceManager ctx) {
        String weapon = attacker.getInventory().getItemInMainHand().getType().name();
        if (weapon.endsWith("_PICKAXE") || weapon.endsWith("_AXE")) {
            event.setDamage(event.getDamage() + 2.0);
        }
    }

    @Override
    public void onExpChange(Player player, PlayerExpChangeEvent event, RaceManager ctx) {
        if (player.getLocation().getBlockY() < 50 && event.getAmount() > 0) {
            event.setAmount((int) Math.ceil(event.getAmount() * 1.20));
        }
    }
}
