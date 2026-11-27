package com.racecraft.races.impl;

import com.racecraft.races.RaceMastery;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;

import java.util.List;

public class PheTocRace implements RaceMastery {

    public static final String ID = "phetoc";

    @Override public String getId() { return ID; }
    @Override public String getDisplayName() { return "Phế Tộc"; }
    @Override public String getTitlePrefix() { return "Phế Tộc"; }
    @Override public NamedTextColor getThemeColor() { return NamedTextColor.DARK_GRAY; }
    @Override public Material getIcon() { return Material.BONE; }
    @Override public boolean isRollable() { return false; }

    // Giới hạn máu -4 HP (trừ 2 tim) và +2.0 sát thương cơ bản, không có ưu/nhược điểm
    @Override public double getBonusMaxHealth() { return -4.0; }
    @Override public double getBonusAttackDamage() { return 2.0; }

    @Override
    public List<String> getLore() {
        return List.of(
                "Hậu quả khi từ bỏ huyết mạch của bản thân 2 lần.",
                "Chỉ số cố định: -4 Máu tối đa (-2 tim), +2.0 Sát thương.",
                "Đặc tính: Không có bất kỳ nội tại ưu điểm hay nhược điểm nào."
        );
    }
}
