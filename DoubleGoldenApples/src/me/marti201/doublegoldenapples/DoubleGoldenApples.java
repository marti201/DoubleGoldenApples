package me.marti201.doublegoldenapples;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class DoubleGoldenApples extends JavaPlugin implements Listener, CommandExecutor {

	private final String APPLE_NAME = ChatColor.GOLD + "Double " + ChatColor.YELLOW + "Golden Apple";

	public void onEnable() {
		getServer().getPluginManager().registerEvents(this, this);
		getCommand("givedoubleapple").setExecutor(this);
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		boolean isPlayer = sender instanceof Player;
		if (isPlayer || args.length >= 2) {

			// Check for permissions
			if (isPlayer) {
				if (!sender.hasPermission("doublegoldenapples.give") && !sender.isOp()) {
					sender.sendMessage(ChatColor.RED + "You don't have permission to use this command!");
					return true;
				}
			}

			// Get the amount
			int amount = 1;
			if (args.length >= 1) {
				try {
					amount = Integer.parseInt(args[0]);
				} catch (NumberFormatException ex) {
					sender.sendMessage((isPlayer ? ChatColor.RED : "") + "Usage: /givedoubleapple [amount] [player]");
					return true;
				}
				if (amount <= 0) {
					sender.sendMessage((isPlayer ? ChatColor.RED : "") + "Usage: /givedoubleapple [amount] [player]");
					return true;
				}
			}

			// Get the player who should receive this
			Player player;
			if (args.length >= 2) {
				player = Bukkit.getPlayer(args[1]);
				if (player == null) {
					sender.sendMessage((isPlayer ? ChatColor.RED : "") + "Couldn't find player " + args[1]);
					return true;
				}
			} else {
				player = (Player) sender;
			}

			// Create the apple and give it to the player
			ItemStack is = new ItemStack(Material.GOLDEN_APPLE, amount);
			ItemMeta meta = is.getItemMeta();
			meta.setDisplayName(APPLE_NAME);
			is.setItemMeta(meta);

			player.getInventory().addItem(is);

			sender.sendMessage((isPlayer ? ChatColor.GOLD : "") + "Giving " + amount + " Double Golden Apple"
					+ ((amount > 1) ? "s " : " ") + "to " + player.getName() + ".");

		} else
			sender.sendMessage("Please specify the player");
		return true;
	}

	@EventHandler
	public void onEat(PlayerItemConsumeEvent e) {
		// Check if the event is cancelled
		if (e.isCancelled())
			return;

		// Check if the player is eating a Double Golden Apple
		ItemStack is = e.getItem();

		if (is.getType() != Material.GOLDEN_APPLE)
			return;

		if (!is.hasItemMeta() || !is.getItemMeta().hasDisplayName())
			return;

		if (is.getItemMeta().getDisplayName().equals(APPLE_NAME)) {

			// If everything is correct, give the extended regeneration effect
			// 10 seconds = 4 hearts

			e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 200, 1));
			// Regeneration II for 200 ticks = 10 seconds
		}
	}
}
