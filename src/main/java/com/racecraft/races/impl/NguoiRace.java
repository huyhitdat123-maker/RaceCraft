package com.racecraft.races.impl;

import com.racecraft.managers.RaceManager;
import com.racecraft.races.RaceMastery;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;

import java.util.List;

public class NguoiRace implements RaceMastery {
    @Override public String getId() { return "nguoi"; }
    @Override public String getDisplayName() { return "Tộc Người"; }
    @Override public String getTitlePrefix() { return "Nhân Tộc"; }
    @Override public NamedTextColor getThemeColor() { return NamedTextColor.GREEN; }
    @Override public Material getIcon() { return Material.GOLDEN_APPLE; }
    @Override public double getBonusMaxHealth() { return 2.0; }
    @Override public double getBonusAttackDamage() { return 1.0; }
    @Override public double getBonusArmor() { return 2.0; }
    @Override public double getBonusMoveSpeed() { return 0.005; }

    @Override
    public List<String> getLore() {
        return List.of(
                "Chỉ số toàn diện: +1 Tim, +1.0 Sát thương, +2 Giáp, +5% Tốc chạy.",
                "Nội tại: Nhận thêm +25% XP từ mọi nguồn, ăn thức ăn no lâu hơn.",
                "Nhược điểm: Không có chỉ số đơn lẻ nào bùng nổ đột biến."
        );
    }

    @Override
    public void onExpChange(Player player, PlayerExpChangeEvent event, RaceManager ctx) {
        if (event.getAmount() > 0) {
            event.setAmount((int) Math.ceil(event.getAmount() * 1.25));
        }
    }

    @Override
    public void onConsumeFood(Player player, PlayerItemConsumeEvent event, RaceManager ctx) {
        if (event.getItem().getType().isEdible()) {
            player.setSaturation(Math.min(20f, player.getSaturation() + 3.0f));
        }
    }
}
