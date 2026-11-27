package com.racecraft.races.impl;

import com.racecraft.managers.RaceManager;
import com.racecraft.races.RaceMastery;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class EndermanRace implements RaceMastery {
    @Override public String getId() { return "enderman"; }
    @Override public String getDisplayName() { return "Tộc Enderman"; }
    @Override public String getTitlePrefix() { return "Hư Không"; }
    @Override public NamedTextColor getThemeColor() { return NamedTextColor.DARK_PURPLE; }
    @Override public Material getIcon() { return Material.ENDER_PEARL; }
    @Override public double getBonusMaxHealth() { return -4.0; }
    @Override public double getBonusAttackDamage() { return 1.5; }
    @Override public double getBonusMoveSpeed() { return 0.015; }
    @Override public double getBonusEntityReach() { return 1.0; }
    @Override public double getBonusBlockReach() { return 1.0; }

    @Override
    public List<String> getLore() {
        return List.of(
                "Chỉ số: +1.5 Sát thương, +15% Tốc chạy, +1.0 Tầm với.",
                "Nội tại: Không mất máu khi ném Ender Pearl (25% hoàn ngọc), 15% né đạn.",
                "Nhược điểm: -2 Tim tối đa; chạm nước hoặc mưa bị mất máu!"
        );
    }

    @Override
    public void tickPassive(Player player, RaceManager ctx) {
        if (player.isInWaterOrRain()) {
            player.damage(2.0);
            player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 30, 1, true, false));
        }
    }

    @Override
    public void onTeleport(Player player, PlayerTeleportEvent event, RaceManager ctx) {
        if (event.getCause() == PlayerTeleportEvent.TeleportCause.ENDER_PEARL) {
            event.setCancelled(true);
            player.teleport(event.getTo());
            if (ThreadLocalRandom.current().nextDouble() < 0.25) {
                player.getInventory().addItem(new ItemStack(Material.ENDER_PEARL, 1));
            }
        }
    }

    @Override
    public void onIncomingDamage(Player victim, EntityDamageEvent event, RaceManager ctx) {
        if (event instanceof EntityDamageByEntityEvent byEntity && byEntity.getDamager() instanceof Projectile) {
            if (ThreadLocalRandom.current().nextDouble() < 0.15) {
                event.setCancelled(true);
            }
        }
    }
}
