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
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

public class MaCaRongRace implements RaceMastery {
    @Override public String getId() { return "macarong"; }
    @Override public String getDisplayName() { return "Tộc Ma Cà Rồng"; }
    @Override public String getTitlePrefix() { return "Huyết Tộc"; }
    @Override public NamedTextColor getThemeColor() { return NamedTextColor.RED; }
    @Override public Material getIcon() { return Material.REDSTONE; }
    @Override public double getBonusAttackDamage() { return 2.5; }
    @Override public double getBonusAttackSpeed() { return 0.50; }

    @Override
    public List<String> getLore() {
        return List.of(
                "Chỉ số: +2.5 Sát thương, +15% Tốc đánh; ban đêm chạy cực nhanh.",
                "Nội tại: Đòn đánh cận chiến hút 20% sát thương thành máu & nhìn xuyên đêm.",
                "Nhược điểm: Đứng dưới nắng ban ngày không đội mũ sẽ bị bốc cháy!"
        );
    }

    private boolean isNightOrDark(Player player) {
        long time = player.getWorld().getTime();
        return (time >= 13000 && time <= 23000) || player.getLocation().getBlock().getLightLevel() <= 6;
    }

    @Override
    public void tickPassive(Player player, RaceManager ctx) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 240, 0, true, false));
        if (isNightOrDark(player)) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 40, 1, true, false));
            return;
        }
        ItemStack helmet = player.getInventory().getHelmet();
        if ((helmet == null || helmet.getType().isAir()) && player.getLocation().getBlock().getLightFromSky() >= 13) {
            player.setFireTicks(40);
        }
    }

    @Override
    public void onAttackEntity(Player attacker, LivingEntity target, EntityDamageByEntityEvent event, RaceManager ctx) {
        if (event.getDamager() == attacker) {
            double heal = event.getFinalDamage() * 0.20;
            AttributeInstance attr = attacker.getAttribute(Attribute.MAX_HEALTH);
            double max = attr != null ? attr.getValue() : 20.0;
            attacker.setHealth(Math.min(max, attacker.getHealth() + heal));
        }
    }
}
