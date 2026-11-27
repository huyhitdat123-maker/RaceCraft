package com.racecraft.races.impl;

import com.racecraft.managers.RaceManager;
import com.racecraft.races.RaceMastery;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

public class TienRungRace implements RaceMastery {
    @Override public String getId() { return "tienrung"; }
    @Override public String getDisplayName() { return "Tộc Tiên Rừng"; }
    @Override public String getTitlePrefix() { return "Tinh Linh"; }
    @Override public NamedTextColor getThemeColor() { return NamedTextColor.GREEN; }
    @Override public Material getIcon() { return Material.BOW; }
    @Override public double getBonusMaxHealth() { return -3.0; }
    @Override public double getBonusMoveSpeed() { return 0.012; }
    @Override public double getBonusJumpStrength() { return 0.08; }

    @Override
    public List<String> getLore() {
        return List.of(
                "Chỉ số: +12% Tốc chạy, nhảy cao hơn, +25% sát thương Cung/Nỏ.",
                "Nội tại: Đứng trên cỏ/lá cây tự hồi máu nhẹ.",
                "Nhược điểm: -1.5 Tim tối đa; mặc giáp Kim Cương/Netherite bị chậm."
        );
    }

    @Override
    public void tickPassive(Player player, RaceManager ctx) {
        String under = player.getLocation().subtract(0, 1, 0).getBlock().getType().name();
        if (under.contains("GRASS") || under.contains("LEAVES") || under.contains("MOSS")) {
            AttributeInstance attr = player.getAttribute(Attribute.MAX_HEALTH);
            double max = attr != null ? attr.getValue() : 17.0;
            player.setHealth(Math.min(max, player.getHealth() + 0.5));
        }

        for (ItemStack armor : player.getInventory().getArmorContents()) {
            if (armor != null && (armor.getType().name().contains("DIAMOND") || armor.getType().name().contains("NETHERITE"))) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 30, 0, true, false));
                break;
            }
        }
    }

    @Override
    public void onAttackEntity(Player attacker, LivingEntity target, EntityDamageByEntityEvent event, RaceManager ctx) {
        if (event.getDamager() instanceof Arrow) {
            event.setDamage(event.getDamage() * 1.25);
        }
    }
}
