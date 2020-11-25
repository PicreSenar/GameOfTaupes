package fr.vraken.gameoftaupes.EventsClasses;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Furnace;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Witch;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageModifier;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.inventory.BrewEvent;
import org.bukkit.event.inventory.FurnaceBurnEvent;
import org.bukkit.event.inventory.FurnaceSmeltEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.BrewerInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.block.Skull;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Team;

import com.mysql.fabric.Server;
import com.sun.webkit.plugin.Plugin;

import fr.vraken.gameoftaupes.GameOfTaupes;

public class LobbyEventsClass implements Listener 
{
	GameOfTaupes plugin;
	
	public boolean meetUp = false;

	public LobbyEventsClass(GameOfTaupes gameoftaupes)
	{
		plugin = gameoftaupes;
	}

	public void AddBannerItem(Inventory inv, ChatColor ccolor, DyeColor color, String Name, int slot) 
	{
		ItemStack team = new ItemStack(Material.LEGACY_BANNER);
		BannerMeta meta = (BannerMeta) team.getItemMeta();
		meta.setDisplayName(ccolor + Name);

		if (color.equals(DyeColor.BLUE)) 
		{
			team = new ItemStack(Material.BLUE_BANNER);
			meta = (BannerMeta) team.getItemMeta();
			meta.setDisplayName(ccolor + Name);
			List<String> lore = new ArrayList<String>();
			for (OfflinePlayer pl : plugin.customScoreboardManager.s.getTeam(plugin.teamf.getString("blue.name")).getPlayers()) 
			{
				lore.add(ChatColor.BLUE + "- " + pl.getName());
			}
			meta.setLore(lore);
		} 
		else if (color.equals(DyeColor.YELLOW)) 
		{
			team = new ItemStack(Material.YELLOW_BANNER);
			meta = (BannerMeta) team.getItemMeta();
			meta.setDisplayName(ccolor + Name);
			List<String> lore = new ArrayList<String>();
			for (OfflinePlayer pl : plugin.customScoreboardManager.s.getTeam(plugin.teamf.getString("yellow.name")).getPlayers()) 
			{
				lore.add(ChatColor.YELLOW + "- " + pl.getName());
			}
			meta.setLore(lore);
		} 
		else if (color.equals(DyeColor.MAGENTA)) 
		{
			team = new ItemStack(Material.MAGENTA_BANNER);
			meta = (BannerMeta) team.getItemMeta();
			meta.setDisplayName(ccolor + Name);
			List<String> lore = new ArrayList<String>();
			for (OfflinePlayer pl : plugin.customScoreboardManager.s.getTeam(plugin.teamf.getString("dark_purple.name")).getPlayers()) 
			{
				lore.add(ChatColor.DARK_PURPLE + "- " + pl.getName());
			}
			meta.setLore(lore);
		} 
		else if (color.equals(DyeColor.CYAN)) 
		{
			team = new ItemStack(Material.CYAN_BANNER);
			meta = (BannerMeta) team.getItemMeta();
			meta.setDisplayName(ccolor + Name);
			List<String> lore = new ArrayList<String>();
			for (OfflinePlayer pl : plugin.customScoreboardManager.s.getTeam(plugin.teamf.getString("dark_aqua.name")).getPlayers()) 
			{
				lore.add(ChatColor.DARK_AQUA + "- " + pl.getName());
			}
			meta.setLore(lore);
		} 
		else if (color.equals(DyeColor.GREEN))
		{
			team = new ItemStack(Material.GREEN_BANNER);
			meta = (BannerMeta) team.getItemMeta();
			meta.setDisplayName(ccolor + Name);
			List<String> lore = new ArrayList<String>();
			for (OfflinePlayer pl : plugin.customScoreboardManager.s.getTeam(plugin.teamf.getString("dark_green.name")).getPlayers()) 
			{
				lore.add(ChatColor.DARK_GREEN + "- " + pl.getName());
			}
			meta.setLore(lore);
		} 
		else if (color.equals(DyeColor.GRAY)) 
		{
			team = new ItemStack(Material.GRAY_BANNER);
			meta = (BannerMeta) team.getItemMeta();
			meta.setDisplayName(ccolor + Name);
			List<String> lore = new ArrayList<String>();
			for (OfflinePlayer pl : plugin.customScoreboardManager.s.getTeam(plugin.teamf.getString("dark_gray.name")).getPlayers()) 
			{
				lore.add(ChatColor.DARK_GRAY + "- " + pl.getName());
			}
			meta.setLore(lore);
		}
		meta.setBaseColor(color);

		team.setItemMeta(meta);
		inv.setItem(slot, team);
	}

	public void OpenTeamInv(Player p) 
	{
		Inventory inv = Bukkit.createInventory(p, 9, ChatColor.GOLD + "Choisir " + plugin.teamChoiceString);

		AddBannerItem(inv, ChatColor.BLUE, DyeColor.BLUE, plugin.teamf.getString("blue.name"), 0);
		AddBannerItem(inv, ChatColor.YELLOW, DyeColor.YELLOW, plugin.teamf.getString("yellow.name"), 1);
		AddBannerItem(inv, ChatColor.DARK_PURPLE, DyeColor.PURPLE, plugin.teamf.getString("dark_purple.name"), 2);
		AddBannerItem(inv, ChatColor.AQUA, DyeColor.CYAN, plugin.teamf.getString("dark_aqua.name"), 3);
		AddBannerItem(inv, ChatColor.GREEN, DyeColor.GREEN, plugin.teamf.getString("dark_green.name"), 4);
		AddBannerItem(inv, ChatColor.GRAY, DyeColor.GRAY, plugin.teamf.getString("dark_gray.name"), 5);
		AddBannerItem(inv, ChatColor.WHITE, DyeColor.WHITE, "Quitter son equipe", 6);

		p.openInventory(inv);
	}

	@EventHandler
	public void OnInventoryClick(InventoryClickEvent e)
	{
		if(!meetUp)
			return;
		
		if (e.getCurrentItem() == null)
			return;

		Player p = (Player) e.getWhoClicked();

		if (e.getView().getTitle().equals(ChatColor.GOLD + "Choisir " + plugin.teamChoiceString)) 
		{
			BannerMeta banner = (BannerMeta) e.getCurrentItem().getItemMeta();
			String bname = banner.getDisplayName();

			if (bname.contains(plugin.teamf.getString("blue.name"))) 
			{
				if (plugin.teamsManager.blue.getPlayers().size() < plugin.getConfig().getInt("options.playersperteam")) 
				{
					p.sendMessage(plugin.teamsManager.blue.getPrefix() + "Vous avez rejoint " + plugin.teamf.getString("blue.name"));
					plugin.teamsManager.blue.addPlayer(p);

					if (!plugin.playersInTeam.contains(p.getUniqueId()))
						plugin.playersInTeam.add(p.getUniqueId());
				} 
				else
					p.sendMessage(ChatColor.RED + "Cette equipe est complete !");
			}
			else if (bname.contains(plugin.teamf.getString("dark_aqua.name"))) 
			{
				if (plugin.teamsManager.dark_aqua.getPlayers().size() < plugin.getConfig().getInt("options.playersperteam")) 
				{
					p.sendMessage(plugin.teamsManager.dark_aqua.getPrefix() + "Vous avez rejoint "
							+ plugin.teamf.getString("dark_aqua.name"));
					plugin.teamsManager.dark_aqua.addPlayer(p);

					if (!plugin.playersInTeam.contains(p.getUniqueId()))
						plugin.playersInTeam.add(p.getUniqueId());
				} 
				else
					p.sendMessage(ChatColor.RED + "Cette equipe est complete !");
			}
			else if (bname.contains(plugin.teamf.getString("yellow.name"))) 
			{
				if (plugin.teamsManager.yellow.getPlayers().size() < plugin.getConfig().getInt("options.playersperteam")) 
				{
					p.sendMessage(
							plugin.teamsManager.yellow.getPrefix() + "Vous avez rejoint " + plugin.teamf.getString("yellow.name"));
					plugin.teamsManager.yellow.addPlayer(p);

					if (!plugin.playersInTeam.contains(p.getUniqueId()))
						plugin.playersInTeam.add(p.getUniqueId());
				} 
				else
					p.sendMessage(ChatColor.RED + "Cette equipe est complete !");
			}
			else if (bname.contains(plugin.teamf.getString("dark_purple.name"))) 
			{
				if (plugin.teamsManager.dark_purple.getPlayers().size() < plugin.getConfig().getInt("options.playersperteam")) 
				{
					p.sendMessage(plugin.teamsManager.dark_purple.getPrefix() + "Vous avez rejoint "
							+ plugin.teamf.getString("dark_purple.name"));
					plugin.teamsManager.dark_purple.addPlayer(p);

					if (!plugin.playersInTeam.contains(p.getUniqueId()))
						plugin.playersInTeam.add(p.getUniqueId());
				}
				else
					p.sendMessage(ChatColor.RED + "Cette equipe est complete !");
			}
			else if (bname.contains(plugin.teamf.getString("dark_green.name"))) 
			{
				if (plugin.teamsManager.dark_green.getPlayers().size() < plugin.getConfig().getInt("options.playersperteam")) 
				{
					p.sendMessage(plugin.teamsManager.dark_green.getPrefix() + "Vous avez rejoint "
							+ plugin.teamf.getString("dark_green.name"));
					plugin.teamsManager.dark_green.addPlayer(p);

					if (!plugin.playersInTeam.contains(p.getUniqueId()))
						plugin.playersInTeam.add(p.getUniqueId());
				}
				else
					p.sendMessage(ChatColor.RED + "Cette equipe est complete !");
			}
			else if (bname.contains(plugin.teamf.getString("dark_gray.name"))) 
			{
				if (plugin.teamsManager.dark_gray.getPlayers().size() < plugin.getConfig().getInt("options.playersperteam")) 
				{
					p.sendMessage(plugin.teamsManager.dark_gray.getPrefix() + "Vous avez rejoint "
							+ plugin.teamf.getString("dark_gray.name"));
					plugin.teamsManager.dark_gray.addPlayer(p);

					if (!plugin.playersInTeam.contains(p.getUniqueId()))
						plugin.playersInTeam.add(p.getUniqueId());
				} 
				else
					p.sendMessage(ChatColor.RED + "Cette equipe est complete !");
			}
			else if (bname.contains("Quitter son equipe")) 
			{
				if (plugin.playersInTeam.contains(p.getUniqueId())) 
				{
					plugin.playersInTeam.remove(p.getUniqueId());
					plugin.customScoreboardManager.s.getPlayerTeam(p).removePlayer(p);
					p.sendMessage("Vous avez quitte votre equipe !");
				}
			}
			e.setCancelled(true);
			OpenTeamInv(p);
		}
	}

	@EventHandler
	public void OnPlayerJoin(PlayerJoinEvent e) 
	{
		Player p = e.getPlayer();
		
		if (meetUp) 
		{
			if (plugin.playersInTeam.contains(p.getUniqueId()) && plugin.teamf.getBoolean("options.meetupteamtp")) 
			{
				if(plugin.customScoreboardManager.s.getEntryTeam(p.getCustomName()).getDisplayName() == plugin.teamsManager.blue.getDisplayName())
					p.teleport(plugin.teamsManager.meetupl1);
				else if(plugin.customScoreboardManager.s.getEntryTeam(p.getCustomName()).getDisplayName() == plugin.teamsManager.yellow.getDisplayName())
					p.teleport(plugin.teamsManager.meetupl2);
				else if(plugin.customScoreboardManager.s.getEntryTeam(p.getCustomName()).getDisplayName() == plugin.teamsManager.dark_purple.getDisplayName())
					p.teleport(plugin.teamsManager.meetupl3);
				else if(plugin.customScoreboardManager.s.getEntryTeam(p.getCustomName()).getDisplayName() == plugin.teamsManager.dark_aqua.getDisplayName())
					p.teleport(plugin.teamsManager.meetupl4);
				else if(plugin.customScoreboardManager.s.getEntryTeam(p.getCustomName()).getDisplayName() == plugin.teamsManager.dark_green.getDisplayName())
					p.teleport(plugin.teamsManager.meetupl5);
				else if(plugin.customScoreboardManager.s.getEntryTeam(p.getCustomName()).getDisplayName() == plugin.teamsManager.dark_gray.getDisplayName())
					p.teleport(plugin.teamsManager.meetupl6);
				else
					p.teleport(plugin.meetupLocation);
			}
			else
				p.teleport(plugin.meetupLocation);
			
			p.getInventory().clear();
			p.getInventory().setItem(0, new ItemStack(Material.LEGACY_BANNER, 1));
			ItemMeta meta1 = p.getInventory().getItem(0).getItemMeta();
			meta1.setDisplayName(ChatColor.GOLD + "Choisir " + plugin.teamChoiceString);
			p.getInventory().getItem(0).setItemMeta(meta1);
		} 
		else 
		{
			p.teleport(plugin.lobbyLocation);
			p.getInventory().clear();
		}

		p.setGameMode(GameMode.ADVENTURE);

		e.setJoinMessage(ChatColor.BLUE + p.getName() + ChatColor.YELLOW + " a rejoint la partie  " + ChatColor.GRAY
				+ "(" + ChatColor.YELLOW + Bukkit.getOnlinePlayers().size() + "/" + Bukkit.getMaxPlayers()
				+ ChatColor.GRAY + ")");

		plugin.playersInLobby.add(p.getUniqueId());
	}

	@EventHandler
	public void OnPlayerDisconnect(PlayerQuitEvent e) 
	{
		if(plugin.gameStarted || plugin.gameEnd)
			return;
		
		if (plugin.playersInLobby.contains(e.getPlayer().getUniqueId()))
			plugin.playersInLobby.remove(e.getPlayer().getUniqueId());
	}

	@EventHandler
	public void OnPlayerRespawn(PlayerRespawnEvent e) 
	{
		Player p = e.getPlayer();

		if (meetUp)
			e.setRespawnLocation(plugin.meetupLocation);
		else
			e.setRespawnLocation(plugin.respawnLocation);

		p.setGameMode(GameMode.ADVENTURE);

		if (!plugin.playersInLobby.contains(p.getUniqueId()))
			plugin.playersInLobby.add(p.getUniqueId());

		if (!meetUp)
			return;

		p.getInventory().clear();
		p.getInventory().setItem(0, new ItemStack(Material.LEGACY_BANNER, 1));
		ItemMeta meta1 = p.getInventory().getItem(0).getItemMeta();
		meta1.setDisplayName(ChatColor.GOLD + "Choisir " + plugin.teamChoiceString);
		p.getInventory().getItem(0).setItemMeta(meta1);
	}

	@EventHandler
	public void OnPlayerInteractWithBanner(PlayerInteractEvent e) 
	{
		Player p = e.getPlayer();
		Action a = e.getAction();
		if ((a.equals(Action.RIGHT_CLICK_AIR) || a.equals(Action.RIGHT_CLICK_BLOCK))
				&& p.getItemInHand().getType() == Material.BLACK_BANNER 
				&& p.getItemInHand().getItemMeta().getDisplayName().equals(ChatColor.GOLD + "Choisir " + plugin.teamChoiceString)) 
		{
			e.setCancelled(true);
			OpenTeamInv(p);
		}
	}
}
