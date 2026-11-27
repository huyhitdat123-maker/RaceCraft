package com.racecraft.races.impl;

import com.racecraft.managers.RaceManager;
import com.racecraft.races.RaceMastery;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

public class VikingRace implements RaceMastery {
    @Override public String getId() { return "viking"; }
    @Override public String getDisplayName() { return "Tộc Viking"; }
    @Override public String getTitlePrefix() { return "Chiến Thần"; }
    @Override public NamedTextColor getThemeColor() { return NamedTextColor.GOLD; }
    @Override public Material getIcon() { return Material.IRON_AXE; }
    @Override public double getBonusMaxHealth() { return 6.0; }
    @Override public double getBonusAttackDamage() { return 2.5; }
    @Override public double getBonusKnockbackResistance() { return 0.40; }

    @Override
    public List<String> getLore() {
        return List.of(
                "Chỉ số: +3 Tim tối đa, +2.5 Sát thương (+2.5 khi cầm Rìu), +40% Kháng đẩy lùi.",
                "Nội tại: Miễn nhiễm đóng băng; hạ gục mục tiêu nhận Sức Mạnh & Tốc Độ 5s.",
                "Nhược điểm: Giảm 30% sát thương khi bắn Cung hoặc Nỏ."
        );
    }

    @Override
    public void tickPassive(Player player, RaceManager ctx) {
        player.setFreezeTicks(0);
    }

    @Override
    public void onAttackEntity(Player attacker, LivingEntity target, EntityDamageByEntityEvent event, RaceManager ctx) {
        if (event.getDamager() instanceof Arrow) {
            event.setDamage(event.getDamage() * 0.70);
            return;
        }
        if (attacker.getInventory().getItemInMainHand().getType().name().endsWith("_AXE")) {
            event.setDamage(event.getDamage() + 2.5);
        }
    }

    @Override
    public void onKillEntity(Player killer, LivingEntity victim, EntityDeathEvent event, RaceManager ctx) {
        killer.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, 100, 0));
        killer.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 100, 0));
    }
}
