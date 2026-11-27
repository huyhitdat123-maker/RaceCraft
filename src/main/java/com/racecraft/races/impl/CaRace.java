package com.racecraft.races.impl;

import com.racecraft.managers.RaceManager;
import com.racecraft.races.RaceMastery;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

public class CaRace implements RaceMastery {
    @Override public String getId() { return "ca"; }
    @Override public String getDisplayName() { return "Tộc Cá"; }
    @Override public String getTitlePrefix() { return "Thủy Tộc"; }
    @Override public NamedTextColor getThemeColor() { return NamedTextColor.AQUA; }
    @Override public Material getIcon() { return Material.TRIDENT; }
    @Override public double getBonusMoveSpeed() { return -0.01; }

    @Override
    public List<String> getLore() {
        return List.of(
                "Nội tại: Thở vô hạn dưới nước, bơi nhanh, nhìn rõ đáy biển.",
                "Chiến đấu: +20% sát thương Đinh Ba; dưới nước/mưa tăng sức mạnh & hồi máu.",
                "Nhược điểm: Chạy trên cạn chậm 10%, yếu đi trong Nether."
        );
    }

    @Override
    public void tickPassive(Player player, RaceManager ctx) {
        player.setRemainingAir(player.getMaximumAir());
        if (player.isInWaterOrRain()) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.DOLPHINS_GRACE, 40, 1, true, false));
            player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 240, 0, true, false));
            player.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, 40, 0, true, false));
            player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 40, 0, true, false));
        }
    }

    @Override
    public void onAttackEntity(Player attacker, LivingEntity target, EntityDamageByEntityEvent event, RaceManager ctx) {
        if (attacker.getInventory().getItemInMainHand().getType() == Material.TRIDENT) {
            event.setDamage(event.getDamage() * 1.20);
        }
        if (attacker.getWorld().getEnvironment() == World.Environment.NETHER) {
            event.setDamage(event.getDamage() * 0.80);
        }
    }
}
