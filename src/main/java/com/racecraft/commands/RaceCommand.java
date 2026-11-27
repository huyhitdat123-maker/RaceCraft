package com.racecraft.commands;

import com.racecraft.gui.RaceGui;
import com.racecraft.managers.RaceManager;
import com.racecraft.races.RaceMastery;
import com.racecraft.races.RaceRegistry;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Collectors;

public class RaceCommand implements CommandExecutor, TabCompleter {

    private final RaceRegistry registry;
    private final RaceManager raceManager;
    private final RaceGui raceGui;

    public RaceCommand(RaceRegistry registry, RaceManager raceManager, RaceGui raceGui) {
        this.registry = registry;
        this.raceManager = raceManager;
        this.raceGui = raceGui;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        String cmd = command.getName().toLowerCase();

        if (cmd.equals("givetrung")) {
            if (!sender.hasPermission("racecraft.admin")) {
                sender.sendMessage(Component.text("Bạn không có quyền dùng lệnh này!", NamedTextColor.RED));
                return true;
            }
            if (args.length < 1) {
                sender.sendMessage(Component.text("Dùng: /givetrung <tên_người_chơi> [số_lượng]", NamedTextColor.RED));
                return true;
            }
            Player target = Bukkit.getPlayerExact(args[0]);
            if (target == null) {
                sender.sendMessage(Component.text("Không tìm thấy người chơi này!", NamedTextColor.RED));
                return true;
            }
            int amount = 1;
            if (args.length >= 2) {
                try {
                    amount = Math.max(1, Integer.parseInt(args[1]));
                } catch (NumberFormatException ignored) {
                }
            }
            target.getInventory().addItem(raceManager.createBloodlineEgg(amount))
                    .values().forEach(rem -> target.getWorld().dropItemNaturally(target.getLocation(), rem));
            sender.sendMessage(Component.text("Đã cấp " + amount + " Trứng Huyết Mạch cho " + target.getName() + ".", NamedTextColor.GREEN));
            return true;
        }

        if (cmd.equals("toc") && args.length >= 3 && args[0].equalsIgnoreCase("set")) {
            if (!sender.hasPermission("racecraft.admin")) {
                sender.sendMessage(Component.text("Bạn không có quyền dùng lệnh này!", NamedTextColor.RED));
                return true;
            }
            Player target = Bukkit.getPlayerExact(args[1]);
            if (target == null) {
                sender.sendMessage(Component.text("Không tìm thấy người chơi!", NamedTextColor.RED));
                return true;
            }
            RaceMastery race = registry.get(args[2]);
            if (race == null) {
                sender.sendMessage(Component.text("Không tồn tại mã tộc: " + args[2], NamedTextColor.RED));
                return true;
            }
            raceManager.setPlayerRaceAdmin(target, race.getId());
            sender.sendMessage(Component.text("Đã chuyển " + target.getName() + " sang " + race.getDisplayName() + ".", NamedTextColor.GREEN));
            return true;
        }

        if (!(sender instanceof Player player)) {
            sender.sendMessage("Lenh nay chi danh cho nguoi choi!");
            return true;
        }

        switch (cmd) {
            case "toc" -> raceGui.openMenu(player);
            case "tiepnhanhuyetmach" -> raceManager.acceptBloodline(player);
            case "tubohuyetmach" -> raceManager.abandonBloodline(player);
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        String cmd = command.getName().toLowerCase();
        if (cmd.equals("givetrung")) {
            if (args.length == 1) {
                return Bukkit.getOnlinePlayers().stream().map(Player::getName)
                        .filter(n -> n.toLowerCase().startsWith(args[0].toLowerCase())).collect(Collectors.toList());
            }
            if (args.length == 2) return List.of("1", "2");
        }
        if (cmd.equals("toc") && sender.hasPermission("racecraft.admin")) {
            if (args.length == 1) return List.of("set");
            if (args.length == 2 && args[0].equalsIgnoreCase("set")) {
                return Bukkit.getOnlinePlayers().stream().map(Player::getName)
                        .filter(n -> n.toLowerCase().startsWith(args[1].toLowerCase())).collect(Collectors.toList());
            }
            if (args.length == 3 && args[0].equalsIgnoreCase("set")) {
                return registry.getAll().stream().map(RaceMastery::getId)
                        .filter(id -> id.startsWith(args[2].toLowerCase())).collect(Collectors.toList());
            }
        }
        return List.of();
    }
}
