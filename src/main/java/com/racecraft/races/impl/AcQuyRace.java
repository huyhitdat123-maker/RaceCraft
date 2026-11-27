package com.racecraft.races.impl;

import com.racecraft.managers.RaceManager;
import com.racecraft.races.RaceMastery;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

public class AcQuyRace implements RaceMastery {
    @Override public String getId() { return "acquy"; }
    @Override public String getDisplayName() { return "Tộc Ác Quỷ"; }
    @Override public String getTitlePrefix() { return "Ma Tộc"; }
    @Override public NamedTextColor getThemeColor() { return NamedTextColor.DARK_RED; }
    @Override public Material getIcon() { return Material.BLAZE_POWDER; }
    @Override public double getBonusAttackDamage() { return 3.0; }
    @Override public double getBonusAttackSpeed() { return 0.40; }

    @Override
    public List<String> getLore() {
        return List.of(
                "Chỉ số: +3.0 Sát thương (+1.5 khi cầm Rìu), +10% Tốc đánh.",
                "Nội tại: Miễn nhiễm Lửa; dưới 40% máu kích hoạt Huyết Nộ & hút máu khi hạ địch.",
                "Nhược điểm: Ngâm nước hoặc ở vùng băng tuyết bị làm chậm và yếu đi."
        );
    }

    @Override
    public void tickPassive(Player player, RaceManager ctx) {
        player.setFireTicks(0);
        if (player.isInWater() || player.getLocation().getBlock().getTemperature() < 0.15) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 30, 1, true, false));
            player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 30, 0, true, false));
        }
    }

    @Override
    public void onAttackEntity(Player attacker, LivingEntity target, EntityDamageByEntityEvent event, RaceManager ctx) {
        double dmg = event.getDamage();
        if (attacker.getInventory().getItemInMainHand().getType().name().endsWith("_AXE")) {
            dmg += 1.5;
        }
        AttributeInstance attr = attacker.getAttribute(Attribute.MAX_HEALTH);
        double max = attr != null ? attr.getValue() : 20.0;
        if (attacker.getHealth() <= max * 0.40) {
            dmg *= 1.25;
        }
        event.setDamage(dmg);
    }

    @Override
    public void onKillEntity(Player killer, LivingEntity victim, EntityDeathEvent event, RaceManager ctx) {
        AttributeInstance attr = killer.getAttribute(Attribute.MAX_HEALTH);
        double max = attr != null ? attr.getValue() : 20.0;
        killer.setHealth(Math.min(max, killer.getHealth() + 4.0));
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
