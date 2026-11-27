package com.racecraft.races.impl;

import com.racecraft.managers.RaceManager;
import com.racecraft.races.RaceMastery;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;
import java.util.Set;

public class ThienSuRace implements RaceMastery {

    private static final Set<EntityType> UNDEAD = Set.of(
            EntityType.ZOMBIE, EntityType.ZOMBIE_VILLAGER, EntityType.HUSK, EntityType.DROWNED,
            EntityType.SKELETON, EntityType.STRAY, EntityType.WITHER_SKELETON, EntityType.WITHER, EntityType.PHANTOM
    );

    @Override public String getId() { return "thiensu"; }
    @Override public String getDisplayName() { return "Tộc Thiên Sứ"; }
    @Override public String getTitlePrefix() { return "Thiên Sứ"; }
    @Override public NamedTextColor getThemeColor() { return NamedTextColor.YELLOW; }
    @Override public Material getIcon() { return Material.FEATHER; }
    @Override public double getBonusMaxHealth() { return 4.0; }
    @Override public double getBonusAttackDamage() { return -1.0; }

    @Override
    public List<String> getLore() {
        return List.of(
                "Chỉ số: +2 Tim tối đa, giảm 20% sát thương từ quái Bất Tử.",
                "Nội tại: Miễn nhiễm sát thương rơi; máu thấp tự kích hoạt Hồi Phục & Kháng Cự.",
                "Nhược điểm: Sát thương cận chiến cơ bản giảm 1.0."
        );
    }

    @Override
    public void onIncomingDamage(Player victim, EntityDamageEvent event, RaceManager ctx) {
        if (event.getCause() == EntityDamageEvent.DamageCause.FALL) {
            event.setCancelled(true);
            return;
        }
        if (event instanceof EntityDamageByEntityEvent byEntity && UNDEAD.contains(byEntity.getDamager().getType())) {
            event.setDamage(event.getDamage() * 0.80);
        }
        AttributeInstance attr = victim.getAttribute(Attribute.MAX_HEALTH);
        double max = attr != null ? attr.getValue() : 24.0;
        if (victim.getHealth() - event.getFinalDamage() <= max * 0.25) {
            victim.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 80, 1));
            victim.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 80, 1));
        }
    }
}
