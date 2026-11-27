package com.racecraft.races;

import com.racecraft.managers.RaceManager;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.List;

public interface RaceMastery {

    String getId();

    String getDisplayName();

    String getTitlePrefix();

    NamedTextColor getThemeColor();

    Material getIcon();

    default boolean isRollable() {
        return true;
    }

    List<String> getLore();

    // Các chỉ số cộng/trừ nền cố định (0.0 = giữ nguyên mặc định)
    default double getBonusMaxHealth() {
        return 0.0;
    }

    default double getBonusAttackDamage() {
        return 0.0;
    }

    default double getBonusArmor() {
        return 0.0;
    }

    default double getBonusArmorToughness() {
        return 0.0;
    }

    default double getBonusMoveSpeed() {
        return 0.0;
    }

    default double getBonusAttackSpeed() {
        return 0.0;
    }

    default double getBonusKnockbackResistance() {
        return 0.0;
    }

    default double getBonusEntityReach() {
        return 0.0;
    }

    default double getBonusBlockReach() {
        return 0.0;
    }

    default double getBonusJumpStrength() {
        return 0.0;
    }

    default void tickPassive(Player player, RaceManager ctx) {
    }

    default void onAttackEntity(Player attacker, LivingEntity target, EntityDamageByEntityEvent event, RaceManager ctx) {
    }

    default void onIncomingDamage(Player victim, EntityDamageEvent event, RaceManager ctx) {
    }

    default void onKillEntity(Player killer, LivingEntity victim, EntityDeathEvent event, RaceManager ctx) {
    }

    default void onExpChange(Player player, PlayerExpChangeEvent event, RaceManager ctx) {
    }

    default void onConsumeFood(Player player, PlayerItemConsumeEvent event, RaceManager ctx) {
    }

    default void onTeleport(Player player, PlayerTeleportEvent event, RaceManager ctx) {
    }

    default void onProjectileLaunch(Player player, ProjectileLaunchEvent event, RaceManager ctx) {
    }
}
