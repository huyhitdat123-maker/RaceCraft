package com.racecraft.races.impl;

import com.racecraft.managers.RaceManager;
import com.racecraft.races.RaceMastery;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

public class PhuongHoangRace implements RaceMastery {
    @Override public String getId() { return "phuonghoang"; }
    @Override public String getDisplayName() { return "Tộc Phượng Hoàng"; }
    @Override public String getTitlePrefix() { return "Chu Tước"; }
    @Override public NamedTextColor getThemeColor() { return NamedTextColor.RED; }
    @Override public Material getIcon() { return Material.BLAZE_ROD; }
    @Override public double getBonusMaxHealth() { return 4.0; }
    @Override public double getBonusAttackDamage() { return 2.0; }

    @Override
    public List<String> getLore() {
        return List.of(
                "Chỉ số: +2 Tim tối đa, +2.0 Sát thương cơ bản.",
                "Nội tại: Miễn nhiễm Lửa/Lava; Niết Bàn Tái Sinh chặn đòn chết (hồi 3 phút).",
                "Nhược điểm: Ngâm nước hoặc dính mưa bị giảm tốc độ & suy yếu."
        );
    }

    @Override
    public void tickPassive(Player player, RaceManager ctx) {
        player.setFireTicks(0);
        if (player.isInWaterOrRain()) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 30, 0, true, false));
            player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 30, 0, true, false));
        }
    }

    @Override
    public void onIncomingDamage(Player victim, EntityDamageEvent event, RaceManager ctx) {
        EntityDamageEvent.DamageCause c = event.getCause();
        if (c == EntityDamageEvent.DamageCause.FIRE || c == EntityDamageEvent.DamageCause.FIRE_TICK || c == EntityDamageEvent.DamageCause.LAVA) {
            event.setCancelled(true);
            return;
        }

        if (victim.getHealth() - event.getFinalDamage() <= 0.5 && ctx.isPhoenixReady(victim)) {
            event.setCancelled(true);
            ctx.triggerPhoenixCooldown(victim, 180);

            AttributeInstance attr = victim.getAttribute(Attribute.MAX_HEALTH);
            double max = attr != null ? attr.getValue() : 24.0;
            victim.setHealth(max * 0.50);
            victim.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 100, 1));
            victim.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 80, 1));

            victim.getWorld().spawnParticle(Particle.FLAME, victim.getLocation().add(0, 1, 0), 45, 0.7, 0.8, 0.7, 0.08);
            RaceManager.playSoundSafe(victim, "item.totem.use", 1.1f);
            victim.sendMessage(Component.text("🔥 Niết Bàn Tái Sinh! Phượng Hoàng hồi sinh từ tro tàn (hồi chiêu 3 phút).",
                    NamedTextColor.GOLD, TextDecoration.BOLD));
        }
    }
}
