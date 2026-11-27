package com.racecraft.races.impl;

import com.racecraft.managers.RaceManager;
import com.racecraft.races.RaceMastery;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

public class KyLanRace implements RaceMastery {
    @Override public String getId() { return "kylan"; }
    @Override public String getDisplayName() { return "Tộc Kỳ Lân"; }
    @Override public String getTitlePrefix() { return "Kỳ Lân"; }
    @Override public NamedTextColor getThemeColor() { return NamedTextColor.GOLD; }
    @Override public Material getIcon() { return Material.AMETHYST_SHARD; }
    @Override public double getBonusMaxHealth() { return 4.0; }
    @Override public double getBonusArmor() { return 3.0; }
    @Override public double getBonusMoveSpeed() { return 0.012; }
    @Override public double getBonusAttackDamage() { return 0.5; }

    @Override
    public List<String> getLore() {
        return List.of(
                "Chỉ số: +2 Tim tối đa, +3 Giáp, +12% Tốc chạy, +15% XP.",
                "Nội tại: Miễn nhiễm Độc & Héo Úa; đứng nơi có ánh sáng tự hồi máu nhẹ.",
                "Nhược điểm: Sát thương cơ bản thấp và suy yếu nhẹ trong Nether."
        );
    }

    @Override
    public void tickPassive(Player player, RaceManager ctx) {
        if (player.hasPotionEffect(PotionEffectType.POISON)) player.removePotionEffect(PotionEffectType.POISON);
        if (player.hasPotionEffect(PotionEffectType.WITHER)) player.removePotionEffect(PotionEffectType.WITHER);

        if (player.getLocation().getBlock().getLightLevel() >= 11) {
            AttributeInstance attr = player.getAttribute(Attribute.MAX_HEALTH);
            double max = attr != null ? attr.getValue() : 24.0;
            player.setHealth(Math.min(max, player.getHealth() + 0.5));
        }
    }

    @Override
    public void onAttackEntity(Player attacker, LivingEntity target, EntityDamageByEntityEvent event, RaceManager ctx) {
        if (attacker.getWorld().getEnvironment() == World.Environment.NETHER) {
            event.setDamage(event.getDamage() * 0.85);
        }
    }

    @Override
    public void onExpChange(Player player, PlayerExpChangeEvent event, RaceManager ctx) {
        if (event.getAmount() > 0) {
            event.setAmount((int) Math.ceil(event.getAmount() * 1.15));
        }
    }

    @Override
    public void onIncomingDamage(Player victim, EntityDamageEvent event, RaceManager ctx) {
        if (event.getCause() == EntityDamageEvent.DamageCause.POISON || event.getCause() == EntityDamageEvent.DamageCause.WITHER) {
            event.setCancelled(true);
        }
    }
}
