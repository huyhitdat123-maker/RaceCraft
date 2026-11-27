package com.racecraft.managers;

import com.racecraft.RaceCraftPlugin;
import com.racecraft.races.RaceMastery;
import com.racecraft.races.RaceRegistry;
import com.racecraft.races.impl.PheTocRace;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class RaceManager implements Listener {

    private final RaceCraftPlugin plugin;
    private final RaceRegistry registry;
    private final NamespacedKey bloodEggKey;
    private final NamespacedKey attrModKey;

    private final File dataFile;
    private final YamlConfiguration dataConfig;

    private final Map<UUID, String> playerRaces = new HashMap<>();
    private final Map<UUID, String> pendingRaces = new HashMap<>();
    private final Map<UUID, Set<String>> rejectedRaces = new HashMap<>();
    private final Map<UUID, Integer> rejectCounts = new HashMap<>();
    private final Map<UUID, Long> phoenixCooldownUntil = new HashMap<>();

    public RaceManager(RaceCraftPlugin plugin, RaceRegistry registry) {
        this.plugin = plugin;
        this.registry = registry;
        this.bloodEggKey = new NamespacedKey(plugin, "rc_bloodline_egg");
        this.attrModKey = new NamespacedKey(plugin, "rc_race_modifier");
        this.dataFile = new File(plugin.getDataFolder(), "races_data.yml");
        this.dataConfig = YamlConfiguration.loadConfiguration(dataFile);

        for (Player online : Bukkit.getOnlinePlayers()) {
            loadPlayerData(online);
        }
    }

    public RaceCraftPlugin getPlugin() {
        return plugin;
    }

    public void saveData() {
        try {
            dataConfig.save(dataFile);
        } catch (IOException e) {
            plugin.getLogger().warning("Loi luu races_data.yml: " + e.getMessage());
        }
    }

    public boolean isPhoenixReady(Player player) {
        return System.currentTimeMillis() >= phoenixCooldownUntil.getOrDefault(player.getUniqueId(), 0L);
    }

    public void triggerPhoenixCooldown(Player player, int seconds) {
        phoenixCooldownUntil.put(player.getUniqueId(), System.currentTimeMillis() + (seconds * 1000L));
    }

    private void loadPlayerData(Player player) {
        UUID uuid = player.getUniqueId();
        String basePath = "players." + uuid;

        String savedRace = dataConfig.getString(basePath + ".race");
        if (savedRace != null && registry.get(savedRace) != null) {
            playerRaces.put(uuid, savedRace.toLowerCase());
        }

        String savedPending = dataConfig.getString(basePath + ".pending");
        if (savedPending != null && registry.get(savedPending) != null) {
            pendingRaces.put(uuid, savedPending.toLowerCase());
        }

        rejectedRaces.put(uuid, new HashSet<>(dataConfig.getStringList(basePath + ".rejected")));
        rejectCounts.put(uuid, dataConfig.getInt(basePath + ".reject_count", 0));

        // Cấp 2 Trứng Huyết Mạch duy nhất 1 lần khi vào server lần đầu
        if (!dataConfig.getBoolean(basePath + ".starter_given", false)) {
            dataConfig.set(basePath + ".starter_given", true);
            saveData();

            int count = Math.max(1, plugin.getConfig().getInt("starter-eggs", 2));
            player.getInventory().addItem(createBloodlineEgg(count))
                    .values().forEach(rem -> player.getWorld().dropItemNaturally(player.getLocation(), rem));

            player.sendMessage(Component.text("✦ Huyết mạch thức tỉnh! Bạn nhận được ", NamedTextColor.GOLD)
                    .append(Component.text(count + " Trứng Huyết Mạch", NamedTextColor.YELLOW, TextDecoration.BOLD))
                    .append(Component.text(". Chuột phải để quay chủng tộc (từ bỏ 2 lần sẽ thành Phế Tộc)!", NamedTextColor.GOLD)));
        } else if (pendingRaces.containsKey(uuid)) {
            RaceMastery pending = registry.get(pendingRaces.get(uuid));
            if (pending != null) {
                sendRacePrompt(player, pending, rejectCounts.getOrDefault(uuid, 0));
            }
        }

        applyRaceAttributesAndTitle(player);
    }

    public ItemStack createBloodlineEgg(int amount) {
        ItemStack egg = new ItemStack(Material.TURTLE_EGG, Math.max(1, amount));
        ItemMeta meta = egg.getItemMeta();
        if (meta != null) {
            meta.displayName(Component.text("✦ Trứng Thức Tỉnh Huyết Mạch ✦", NamedTextColor.LIGHT_PURPLE, TextDecoration.BOLD)
                    .decoration(TextDecoration.ITALIC, false));
            meta.lore(List.of(
                    Component.text("Chuột Phải để thức tỉnh ngẫu nhiên 1 trong 16 Chủng Tộc.", NamedTextColor.AQUA).decoration(TextDecoration.ITALIC, false),
                    Component.text("Dùng /tiepnhanhuyetmach để nhận hoặc /tubohuyetmach để bỏ.", NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false),
                    Component.empty(),
                    Component.text("⚠ Cảnh báo: Từ bỏ huyết mạch 2 lần sẽ vĩnh viễn", NamedTextColor.RED).decoration(TextDecoration.ITALIC, false),
                    Component.text("biến thành Phế Tộc (-4 HP tối đa, +2 sát thương)!", NamedTextColor.RED).decoration(TextDecoration.ITALIC, false)
            ));
            meta.setEnchantmentGlintOverride(true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            meta.getPersistentDataContainer().set(bloodEggKey, PersistentDataType.BYTE, (byte) 1);
            egg.setItemMeta(meta);
        }
        return egg;
    }

    public boolean isBloodlineEgg(ItemStack item) {
        if (item == null || item.getType() != Material.TURTLE_EGG || !item.hasItemMeta()) return false;
        return item.getItemMeta().getPersistentDataContainer().has(bloodEggKey, PersistentDataType.BYTE);
    }

    public RaceMastery getPlayerRace(Player player) {
        String id = playerRaces.get(player.getUniqueId());
        return id != null ? registry.get(id) : null;
    }

    // Dùng cho Admin (/toc set <tên> <tộc>)
    public void setPlayerRaceAdmin(Player target, String raceId) {
        RaceMastery race = registry.get(raceId);
        if (race == null) return;

        UUID uuid = target.getUniqueId();
        pendingRaces.remove(uuid);
        playerRaces.put(uuid, race.getId());

        String basePath = "players." + uuid;
        dataConfig.set(basePath + ".pending", null);
        dataConfig.set(basePath + ".race", race.getId());
        saveData();

        applyRaceAttributesAndTitle(target);
    }

    // =========================================================================
    // QUAY TRỨNG HUYẾT MẠCH & XỬ LÝ /tiepnhanhuyetmach - /tubohuyetmach
    // =========================================================================

    @EventHandler
    public void onUseBloodlineEgg(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;
        if (!event.getAction().isRightClick()) return;

        Player player = event.getPlayer();
        ItemStack hand = player.getInventory().getItemInMainHand();
        if (!isBloodlineEgg(hand)) return;

        event.setCancelled(true);
        UUID uuid = player.getUniqueId();

        if (playerRaces.containsKey(uuid)) {
            RaceMastery cur = getPlayerRace(player);
            player.sendMessage(Component.text("Huyết mạch của bạn đã định hình là " + (cur != null ? cur.getDisplayName() : "") + " rồi!", NamedTextColor.RED));
            return;
        }

        if (pendingRaces.containsKey(uuid)) {
            RaceMastery pending = registry.get(pendingRaces.get(uuid));
            player.sendMessage(Component.text("Bạn đang có huyết mạch " + (pending != null ? pending.getDisplayName() : "")
                    + " chờ quyết định! Hãy dùng /tiepnhanhuyetmach hoặc /tubohuyetmach trước.", NamedTextColor.YELLOW));
            return;
        }

        Set<String> rejected = rejectedRaces.computeIfAbsent(uuid, k -> new HashSet<>());
        List<RaceMastery> pool = new ArrayList<>();
        for (RaceMastery r : registry.getRollableRaces()) {
            if (!rejected.contains(r.getId())) {
                pool.add(r);
            }
        }

        if (pool.isEmpty()) {
            player.sendMessage(Component.text("Không còn chủng tộc nào khả dụng để thức tỉnh!", NamedTextColor.RED));
            return;
        }

        // Tiêu hao ngay 1 quả trứng
        if (hand.getAmount() > 1) {
            hand.setAmount(hand.getAmount() - 1);
        } else {
            player.getInventory().setItemInMainHand(null);
        }

        RaceMastery rolled = pool.get(ThreadLocalRandom.current().nextInt(pool.size()));
        pendingRaces.put(uuid, rolled.getId());
        dataConfig.set("players." + uuid + ".pending", rolled.getId());
        saveData();

        playSoundSafe(player, "entity.player.levelup", 1.15f);
        int currentRejects = rejectCounts.getOrDefault(uuid, 0);
        sendRacePrompt(player, rolled, currentRejects);
    }

    private void sendRacePrompt(Player player, RaceMastery race, int currentRejects) {
        player.sendMessage(Component.empty());
        player.sendMessage(Component.text("✦ Huyết mạch cổ xưa trỗi dậy: ", NamedTextColor.GRAY)
                .append(Component.text("[" + race.getTitlePrefix() + "]", race.getThemeColor(), TextDecoration.BOLD)));
        player.sendMessage(Component.text("Bạn có muốn trở thành ", NamedTextColor.GOLD)
                .append(Component.text(race.getDisplayName(), race.getThemeColor(), TextDecoration.BOLD))
                .append(Component.text(" không?", NamedTextColor.GOLD)));

        for (String line : race.getLore()) {
            player.sendMessage(Component.text("  • " + line, NamedTextColor.AQUA));
        }

        // Nếu đã từng từ bỏ 1 lần thì hiện cảnh báo đỏ rực trước lần quyết định thứ 2
        if (currentRejects >= 1) {
            player.sendMessage(Component.text("⚠ CẢNH BÁO: Bạn đã từ bỏ huyết mạch 1 lần. Nếu tiếp tục từ bỏ lần thứ 2, bạn sẽ vĩnh viễn trở thành PHẾ TỘC!",
                    NamedTextColor.RED, TextDecoration.BOLD));
        }

        Component acceptBtn = Component.text("[✔ TIẾP NHẬN (/tiepnhanhuyetmach)]", NamedTextColor.GREEN, TextDecoration.BOLD)
                .clickEvent(ClickEvent.runCommand("/tiepnhanhuyetmach"))
                .hoverEvent(HoverEvent.showText(Component.text("Bấm để tiếp nhận huyết mạch " + race.getDisplayName() + " và danh hiệu [" + race.getTitlePrefix() + "]")));

        Component declineBtn = Component.text("[✖ TỪ BỎ (/tubohuyetmach)]", NamedTextColor.RED, TextDecoration.BOLD)
                .clickEvent(ClickEvent.runCommand("/tubohuyetmach"))
                .hoverEvent(HoverEvent.showText(Component.text(currentRejects >= 1
                        ? "NGUY HIỂM: Từ bỏ lần 2 sẽ biến bạn thành PHẾ TỘC!"
                        : "Từ bỏ huyết mạch này (1/2 lần)")));

        player.sendMessage(acceptBtn.append(Component.text("   ")).append(declineBtn));
        player.sendMessage(Component.empty());
    }

    public void acceptBloodline(Player player) {
        UUID uuid = player.getUniqueId();
        String pendingId = pendingRaces.remove(uuid);
        if (pendingId == null) {
            player.sendMessage(Component.text("Bạn không có huyết mạch nào đang chờ tiếp nhận!", NamedTextColor.RED));
            return;
        }

        RaceMastery race = registry.get(pendingId);
        if (race == null) return;

        playerRaces.put(uuid, race.getId());
        String basePath = "players." + uuid;
        dataConfig.set(basePath + ".pending", null);
        dataConfig.set(basePath + ".race", race.getId());
        saveData();

        applyRaceAttributesAndTitle(player);
        playSoundSafe(player, "ui.toast.challenge_complete", 1.1f);

        player.sendMessage(Component.text("★ Chúc mừng! Bạn đã chính thức trở thành ", NamedTextColor.GREEN)
                .append(Component.text(race.getDisplayName(), race.getThemeColor(), TextDecoration.BOLD))
                .append(Component.text(" với danh hiệu ", NamedTextColor.GREEN))
                .append(Component.text("[" + race.getTitlePrefix() + "]", race.getThemeColor(), TextDecoration.BOLD))
                .append(Component.text("!", NamedTextColor.GREEN)));
    }

    public void abandonBloodline(Player player) {
        UUID uuid = player.getUniqueId();
        String pendingId = pendingRaces.remove(uuid);
        if (pendingId == null) {
            player.sendMessage(Component.text("Bạn không có huyết mạch nào đang chờ quyết định!", NamedTextColor.RED));
            return;
        }

        RaceMastery abandoned = registry.get(pendingId);
        Set<String> rejected = rejectedRaces.computeIfAbsent(uuid, k -> new HashSet<>());
        rejected.add(pendingId);

        int newRejectCount = rejectCounts.getOrDefault(uuid, 0) + 1;
        rejectCounts.put(uuid, newRejectCount);

        String basePath = "players." + uuid;
        dataConfig.set(basePath + ".pending", null);
        dataConfig.set(basePath + ".rejected", new ArrayList<>(rejected));
        dataConfig.set(basePath + ".reject_count", newRejectCount);

        String raceName = abandoned != null ? abandoned.getDisplayName() : pendingId;

        if (newRejectCount >= 2) {
            // Rơi vào Phế Tộc vĩnh viễn + Thông báo toàn server
            playerRaces.put(uuid, PheTocRace.ID);
            dataConfig.set(basePath + ".race", PheTocRace.ID);
            saveData();

            applyRaceAttributesAndTitle(player);
            playSoundSafe(player, "entity.wither.spawn", 0.9f);

            Component broadcastMsg = Component.text("☠ ", NamedTextColor.DARK_RED, TextDecoration.BOLD)
                    .append(Component.text(player.getName(), NamedTextColor.RED, TextDecoration.BOLD))
                    .append(Component.text(" đã từ bỏ huyết mạch của mình lần thứ 2 và vĩnh viễn trở thành ", NamedTextColor.GRAY))
                    .append(Component.text("[Phế Tộc]", NamedTextColor.DARK_GRAY, TextDecoration.BOLD))
                    .append(Component.text("!", NamedTextColor.GRAY));
            Bukkit.broadcast(broadcastMsg);
        } else {
            saveData();
            playSoundSafe(player, "entity.item.break", 0.9f);
            player.sendMessage(Component.text("✖ Bạn đã từ bỏ huyết mạch ", NamedTextColor.RED)
                    .append(Component.text(raceName, NamedTextColor.DARK_RED, TextDecoration.BOLD))
                    .append(Component.text(" (1/2 lần). Cảnh báo: Nếu từ bỏ thêm 1 lần nữa bạn sẽ trở thành Phế Tộc!", NamedTextColor.RED)));
        }
    }

    // =========================================================================
    // ÁP DỤNG CHỈ SỐ NỀN & DANH HIỆU (CHAT + TABLIST + TRÊN ĐẦU NHÂN VẬT)
    // =========================================================================

    public void applyRaceAttributesAndTitle(Player player) {
        clearAllRaceModifiers(player);

        RaceMastery race = getPlayerRace(player);
        if (race == null) {
            updatePlayerTitleDisplay(player, null);
            return;
        }

        addModifierIfNonZero(player, Attribute.MAX_HEALTH, race.getBonusMaxHealth());
        addModifierIfNonZero(player, Attribute.ATTACK_DAMAGE, race.getBonusAttackDamage());
        addModifierIfNonZero(player, Attribute.ARMOR, race.getBonusArmor());
        addModifierIfNonZero(player, Attribute.ARMOR_TOUGHNESS, race.getBonusArmorToughness());
        addModifierIfNonZero(player, Attribute.MOVEMENT_SPEED, race.getBonusMoveSpeed());
        addModifierIfNonZero(player, Attribute.ATTACK_SPEED, race.getBonusAttackSpeed());
        addModifierIfNonZero(player, Attribute.KNOCKBACK_RESISTANCE, race.getBonusKnockbackResistance());
        addModifierIfNonZero(player, Attribute.ENTITY_INTERACTION_RANGE, race.getBonusEntityReach());
        addModifierIfNonZero(player, Attribute.BLOCK_INTERACTION_RANGE, race.getBonusBlockReach());
        addModifierIfNonZero(player, Attribute.JUMP_STRENGTH, race.getBonusJumpStrength());

        AttributeInstance maxHp = player.getAttribute(Attribute.MAX_HEALTH);
        if (maxHp != null && player.getHealth() > maxHp.getValue()) {
            player.setHealth(maxHp.getValue());
        }

        updatePlayerTitleDisplay(player, race);
    }

    private void clearAllRaceModifiers(Player player) {
        Attribute[] tracked = {
                Attribute.MAX_HEALTH, Attribute.ATTACK_DAMAGE, Attribute.ARMOR, Attribute.ARMOR_TOUGHNESS,
                Attribute.MOVEMENT_SPEED, Attribute.ATTACK_SPEED, Attribute.KNOCKBACK_RESISTANCE,
                Attribute.ENTITY_INTERACTION_RANGE, Attribute.BLOCK_INTERACTION_RANGE, Attribute.JUMP_STRENGTH
        };
        for (Attribute attr : tracked) {
            AttributeInstance inst = player.getAttribute(attr);
            if (inst != null) {
                inst.removeModifier(attrModKey);
            }
        }
    }

    private void addModifierIfNonZero(Player player, Attribute attr, double amount) {
        if (Math.abs(amount) < 0.0001) return;
        AttributeInstance inst = player.getAttribute(attr);
        if (inst != null) {
            inst.addModifier(new AttributeModifier(attrModKey, amount, AttributeModifier.Operation.ADD_NUMBER));
        }
    }

    public void updatePlayerTitleDisplay(Player player, RaceMastery race) {
        Scoreboard sb = Bukkit.getScoreboardManager().getMainScoreboard();
        String teamName = "rc_" + player.getName();
        if (teamName.length() > 16) teamName = teamName.substring(0, 16);

        Team team = sb.getTeam(teamName);
        if (race == null) {
            if (team != null) team.unregister();
            player.playerListName(Component.text(player.getName()));
            return;
        }

        Component prefix = Component.text("[" + race.getTitlePrefix() + "] ", race.getThemeColor(), TextDecoration.BOLD);

        if (plugin.getConfig().getBoolean("enable-tablist-prefix", true)) {
            player.playerListName(prefix.append(Component.text(player.getName(), NamedTextColor.WHITE).decoration(TextDecoration.BOLD, false)));
        }

        if (team == null) {
            team = sb.registerNewTeam(teamName);
        }
        team.prefix(prefix);
        team.color(NamedTextColor.WHITE);
        if (!team.hasEntry(player.getName())) {
            team.addEntry(player.getName());
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onChat(AsyncChatEvent event) {
        if (!plugin.getConfig().getBoolean("enable-chat-prefix", true)) return;

        Player player = event.getPlayer();
        RaceMastery race = getPlayerRace(player);
        if (race == null) return;

        Component prefix = Component.text("[" + race.getTitlePrefix() + "] ", race.getThemeColor(), TextDecoration.BOLD);
        event.renderer((source, sourceDisplayName, message, viewer) ->
                prefix.append(Component.text(source.getName(), NamedTextColor.WHITE).decoration(TextDecoration.BOLD, false))
                        .append(Component.text(": ", NamedTextColor.GRAY).decoration(TextDecoration.BOLD, false))
                        .append(message.color(NamedTextColor.WHITE).decoration(TextDecoration.BOLD, false))
        );
    }

    // =========================================================================
    // VÒNG LẶP NỘI TẠI & CÁC SỰ KIỆN CHIẾN ĐẤU / MÔI TRƯỜNG
    // =========================================================================

    public void tickPassives() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            RaceMastery race = getPlayerRace(player);
            if (race != null) {
                race.tickPassive(player, this);
            }
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        loadPlayerData(event.getPlayer());
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (event.getPlayer().isOnline()) {
                applyRaceAttributesAndTitle(event.getPlayer());
            }
        }, 1L);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (isBloodlineEgg(event.getItemInHand())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onAttack(EntityDamageByEntityEvent event) {
        Player attacker = null;
        if (event.getDamager() instanceof Player p) {
            attacker = p;
        } else if (event.getDamager() instanceof Projectile proj && proj.getShooter() instanceof Player p) {
            attacker = p;
        }

        if (attacker != null && event.getEntity() instanceof LivingEntity target) {
            RaceMastery race = getPlayerRace(attacker);
            if (race != null) {
                race.onAttackEntity(attacker, target, event, this);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onIncomingDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player victim)) return;
        RaceMastery race = getPlayerRace(victim);
        if (race != null) {
            race.onIncomingDamage(victim, event, this);
        }
    }

    @EventHandler
    public void onKill(EntityDeathEvent event) {
        Player killer = event.getEntity().getKiller();
        if (killer == null) return;
        RaceMastery race = getPlayerRace(killer);
        if (race != null) {
            race.onKillEntity(killer, event.getEntity(), event, this);
        }
    }

    @EventHandler
    public void onExp(PlayerExpChangeEvent event) {
        RaceMastery race = getPlayerRace(event.getPlayer());
        if (race != null) {
            race.onExpChange(event.getPlayer(), event, this);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onEat(PlayerItemConsumeEvent event) {
        RaceMastery race = getPlayerRace(event.getPlayer());
        if (race != null) {
            race.onConsumeFood(event.getPlayer(), event, this);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onTeleport(PlayerTeleportEvent event) {
        RaceMastery race = getPlayerRace(event.getPlayer());
        if (race != null) {
            race.onTeleport(event.getPlayer(), event, this);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onProjectileLaunch(ProjectileLaunchEvent event) {
        if (event.getEntity().getShooter() instanceof Player player) {
            RaceMastery race = getPlayerRace(player);
            if (race != null) {
                race.onProjectileLaunch(player, event, this);
            }
        }
    }

    public static void playSoundSafe(Player player, String key, float pitch) {
        Sound sound = Registry.SOUNDS.get(NamespacedKey.minecraft(key));
        if (sound != null) {
            player.playSound(player.getLocation(), sound, 1.0f, pitch);
        }
    }
}
