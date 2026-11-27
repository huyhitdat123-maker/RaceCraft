package com.racecraft.gui;

import com.racecraft.RaceCraftPlugin;
import com.racecraft.managers.RaceManager;
import com.racecraft.races.RaceMastery;
import com.racecraft.races.RaceRegistry;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class RaceGui implements Listener {

    private static class RaceMenuHolder implements InventoryHolder {
        @Override
        public @NotNull Inventory getInventory() {
            return Bukkit.createInventory(null, 36);
        }
    }

    private final RaceRegistry registry;
    private final RaceManager raceManager;

    public RaceGui(RaceCraftPlugin plugin, RaceRegistry registry, RaceManager raceManager) {
        this.registry = registry;
        this.raceManager = raceManager;
    }

    public void openMenu(Player player) {
        Inventory inv = Bukkit.createInventory(
                new RaceMenuHolder(),
                36,
                Component.text("Huyết Mạch Chủng Tộc (16 Tộc)", NamedTextColor.GOLD, TextDecoration.BOLD)
        );

        ItemStack filler = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta fMeta = filler.getItemMeta();
        if (fMeta != null) {
            fMeta.displayName(Component.empty());
            filler.setItemMeta(fMeta);
        }
        for (int i = 0; i < 36; i++) inv.setItem(i, filler);

        RaceMastery current = raceManager.getPlayerRace(player);

        // Slot 4: Hiển thị trạng thái huyết mạch hiện tại của người chơi
        ItemStack statusItem = new ItemStack(current != null ? current.getIcon() : Material.TURTLE_EGG);
        ItemMeta sMeta = statusItem.getItemMeta();
        if (sMeta != null) {
            if (current != null) {
                sMeta.displayName(Component.text("★ Huyết Mạch Hiện Tại: " + current.getDisplayName(),
                        current.getThemeColor(), TextDecoration.BOLD).decoration(TextDecoration.ITALIC, false));
                List<Component> lore = new ArrayList<>();
                lore.add(Component.text("Danh hiệu: [" + current.getTitlePrefix() + "]", current.getThemeColor()).decoration(TextDecoration.ITALIC, false));
                lore.add(Component.empty());
                for (String line : current.getLore()) {
                    lore.add(Component.text("• " + line, NamedTextColor.AQUA).decoration(TextDecoration.ITALIC, false));
                }
                sMeta.lore(lore);
                sMeta.setEnchantmentGlintOverride(true);
            } else {
                sMeta.displayName(Component.text("Chưa Thức Tỉnh Huyết Mạch", NamedTextColor.YELLOW, TextDecoration.BOLD)
                        .decoration(TextDecoration.ITALIC, false));
                sMeta.lore(List.of(
                        Component.text("Hãy bấm chuột phải vào Trứng Huyết Mạch", NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false),
                        Component.text("để quay ngẫu nhiên 1 trong 16 chủng tộc!", NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false)
                ));
            }
            sMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS);
            statusItem.setItemMeta(sMeta);
        }
        inv.setItem(4, statusItem);

        // Hiển thị danh sách toàn bộ 16 tộc + Phế Tộc ở các hàng bên dưới
        int[] displaySlots = {
                10, 11, 12, 13, 14, 15, 16,
                19, 20, 21, 22, 23, 24, 25,
                29, 31, 33
        };

        int idx = 0;
        for (RaceMastery race : registry.getAll()) {
            if (idx >= displaySlots.length) break;
            boolean isMine = current != null && current.getId().equals(race.getId());

            ItemStack item = new ItemStack(race.getIcon());
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                meta.displayName(Component.text((isMine ? "✔ " : "") + "[" + race.getTitlePrefix() + "] " + race.getDisplayName(),
                        race.getThemeColor(), TextDecoration.BOLD).decoration(TextDecoration.ITALIC, false));

                List<Component> lore = new ArrayList<>();
                for (String line : race.getLore()) {
                    lore.add(Component.text("• " + line, NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false));
                }
                lore.add(Component.empty());
                lore.add(Component.text(isMine ? "✔ Huyết mạch hiện tại của bạn" : (race.isRollable() ? "Có thể quay ra từ Trứng Huyết Mạch" : "Tộc trừng phạt khi từ bỏ 2 lần"),
                        isMine ? NamedTextColor.GREEN : NamedTextColor.DARK_GRAY).decoration(TextDecoration.ITALIC, false));

                meta.lore(lore);
                meta.setEnchantmentGlintOverride(isMine);
                meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS);
                item.setItemMeta(meta);
            }
            inv.setItem(displaySlots[idx++], item);
        }

        player.openInventory(inv);
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (event.getInventory().getHolder() instanceof RaceMenuHolder) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onDrag(InventoryDragEvent event) {
        if (event.getInventory().getHolder() instanceof RaceMenuHolder) {
            event.setCancelled(true);
        }
    }
}
