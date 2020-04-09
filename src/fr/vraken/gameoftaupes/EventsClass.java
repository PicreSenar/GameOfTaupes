package fr.vraken.gameoftaupes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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
import org.bukkit.entity.Entity;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.BrewEvent;
import org.bukkit.event.inventory.FurnaceBurnEvent;
import org.bukkit.event.inventory.FurnaceSmeltEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.BrewerInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.Banner;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Team;


public class EventsClass implements Listener
{
	static GameOfTaupes plugin;
	public static boolean pvp = false;
	ItemStack playerSkull = new ItemStack(Material.SKULL_ITEM, 1, (short)3);

	public EventsClass(GameOfTaupes gameoftaupes)
	{
		plugin = gameoftaupes;
	}

	public static void addItem(Inventory inv, ChatColor ccolor, DyeColor color, String Name, int slot)
	{
		ItemStack team = new ItemStack(Material.BANNER);
		BannerMeta meta = (BannerMeta)team.getItemMeta();
		meta.setDisplayName(ccolor + Name);
		if (color.equals(DyeColor.PINK))
		{
			List<String> lore = new ArrayList<String>();
			for (OfflinePlayer pl : plugin.s.getTeam(plugin.teamf.getString("rose.name")).getPlayers()) {
				lore.add(ChatColor.LIGHT_PURPLE + "- " + pl.getName());
			}
			meta.setLore(lore);
		}
		else if (color.equals(DyeColor.YELLOW))
		{
			List<String> lore = new ArrayList<String>();
			for (OfflinePlayer pl : plugin.s.getTeam(plugin.teamf.getString("jaune.name")).getPlayers()) {
				lore.add(ChatColor.YELLOW + "- " + pl.getName());
			}
			meta.setLore(lore);
		}
		else if (color.equals(DyeColor.PURPLE))
		{
			List<String> lore = new ArrayList<String>();
			for (OfflinePlayer pl : plugin.s.getTeam(plugin.teamf.getString("violette.name")).getPlayers()) {
				lore.add(ChatColor.DARK_PURPLE + "- " + pl.getName());
			}
			meta.setLore(lore);
		}
		else if (color.equals(DyeColor.CYAN))
		{
			List<String> lore = new ArrayList<String>();
			for (OfflinePlayer pl : plugin.s.getTeam(plugin.teamf.getString("cyan.name")).getPlayers()) {
				lore.add(ChatColor.AQUA + "- " + pl.getName());
			}
			meta.setLore(lore);
		}
		else if (color.equals(DyeColor.GREEN))
		{
			List<String> lore = new ArrayList<String>();
			for (OfflinePlayer pl : plugin.s.getTeam(plugin.teamf.getString("verte.name")).getPlayers()) {
				lore.add(ChatColor.GREEN + "- " + pl.getName());
			}
			meta.setLore(lore);
		}
		else if (color.equals(DyeColor.GRAY))
		{
			List<String> lore = new ArrayList<String>();
			for (OfflinePlayer pl : plugin.s.getTeam(plugin.teamf.getString("grise.name")).getPlayers()) {
				lore.add(ChatColor.GRAY + "- " + pl.getName());
			}
			meta.setLore(lore);
		}
		meta.setBaseColor(color);

		team.setItemMeta(meta);
		inv.setItem(slot, team);
	}

	public static void openTeamInv(Player p)
	{
		Inventory inv = Bukkit.createInventory(p, 9, ChatColor.GOLD + 
				"Choisir " + plugin.teamChoiceString);

		addItem(inv, ChatColor.LIGHT_PURPLE, DyeColor.PINK, plugin.teamf.getString("rose.name"), 0);
		addItem(inv, ChatColor.YELLOW, DyeColor.YELLOW, plugin.teamf.getString("jaune.name"), 1);
		addItem(inv, ChatColor.DARK_PURPLE, DyeColor.PURPLE, plugin.teamf.getString("violette.name"), 2);
		addItem(inv, ChatColor.AQUA, DyeColor.CYAN, plugin.teamf.getString("cyan.name"), 3);
		addItem(inv, ChatColor.GREEN, DyeColor.GREEN, plugin.teamf.getString("verte.name"), 4);
		addItem(inv, ChatColor.GRAY, DyeColor.GRAY, plugin.teamf.getString("grise.name"), 5);
		addItem(inv, ChatColor.WHITE, DyeColor.WHITE, "Quitter son equipe", 6);

		p.openInventory(inv);
	}

	@EventHandler
	public void OnChatEvent(AsyncPlayerChatEvent e)
	{
		
		Team team = e.getPlayer().getScoreboard().getPlayerTeam(e.getPlayer());
		if(team==null) return;
		String format = team.getPrefix() + "<%s> " + ChatColor.WHITE + "%s";
		e.setFormat(format);
	}

	@EventHandler
	public void ChoiceTeam(InventoryClickEvent e)
	{
		if (e.getCurrentItem() == null)
		{
			return;
        }
		
		Player p = (Player)e.getWhoClicked();
		if (e.getInventory().getName().equals(ChatColor.GOLD + "Choisir " + plugin.teamChoiceString) && e.getCurrentItem().getType() == Material.BANNER)
		{
			BannerMeta banner = (BannerMeta)e.getCurrentItem().getItemMeta();
			
			String bname = banner.getDisplayName();
			
			
			//Bukkit.broadcastMessage(bname+" sélectionné");
			
			
			if (bname.contains(plugin.teamf.getString("rose.name"))) 
			{
				
				
				if (plugin.rose.getPlayers().size() < plugin.getConfig().getInt("options.playersperteam"))
				{
					p.sendMessage(plugin.rose.getPrefix() + 
							"Vous avez rejoint " + plugin.teamf.getString("rose.name"));
					plugin.rose.addPlayer(p);
					if(!plugin.playersInTeam.contains(p.getUniqueId()))
					{
						plugin.playersInTeam.add(p.getUniqueId());
					}
				}
				else
				{
					p.sendMessage(ChatColor.RED + "Cette equipe est complete !");
				}
			}
			if (bname.contains(plugin.teamf.getString("cyan.name"))) {
				if (plugin.cyan.getPlayers().size() < plugin.getConfig().getInt("options.playersperteam"))
				{
					p.sendMessage(plugin.cyan.getPrefix() + 
							"Vous avez rejoint " + plugin.teamf.getString("cyan.name"));
					plugin.cyan.addPlayer(p);
					if(!plugin.playersInTeam.contains(p.getUniqueId()))
					{
						plugin.playersInTeam.add(p.getUniqueId());
					}
				}
				else
				{
					p.sendMessage(ChatColor.RED + "Cette equipe est complete !");
				}
			}
			if (bname.contains(plugin.teamf.getString("jaune.name"))) {
				if (plugin.jaune.getPlayers().size() < plugin.getConfig().getInt("options.playersperteam"))
				{
					p.sendMessage(plugin.jaune.getPrefix() + 
							"Vous avez rejoint " + plugin.teamf.getString("jaune.name"));
					plugin.jaune.addPlayer(p);
					if(!plugin.playersInTeam.contains(p.getUniqueId()))
					{
						plugin.playersInTeam.add(p.getUniqueId());
					}
				}
				else
				{
					p.sendMessage(ChatColor.RED + "Cette equipe est complete !");
				}
			}
			if (bname.contains(plugin.teamf.getString("violette.name"))) {
				if (plugin.violette.getPlayers().size() < plugin.getConfig().getInt("options.playersperteam"))
				{
					p.sendMessage(plugin.violette.getPrefix() + 
							"Vous avez rejoint " + plugin.teamf.getString("violette.name"));
					plugin.violette.addPlayer(p);
					if(!plugin.playersInTeam.contains(p.getUniqueId()))
					{
						plugin.playersInTeam.add(p.getUniqueId());
					}
				}
				else
				{
					p.sendMessage(ChatColor.RED + "Cette equipe est complete !");
				}
			}
			if (bname.contains(plugin.teamf.getString("verte.name"))) {
				if (plugin.verte.getPlayers().size() < plugin.getConfig().getInt("options.playersperteam"))
				{
					p.sendMessage(plugin.verte.getPrefix() + 
							"Vous avez rejoint " + plugin.teamf.getString("verte.name"));
					plugin.verte.addPlayer(p);
					if(!plugin.playersInTeam.contains(p.getUniqueId()))
					{
						plugin.playersInTeam.add(p.getUniqueId());
					}
				}
				else
				{
					p.sendMessage(ChatColor.RED + "Cette equipe est complete !");
				}
			}
			if (bname.contains(plugin.teamf.getString("grise.name"))) {
				if (plugin.grise.getPlayers().size() < plugin.getConfig().getInt("options.playersperteam"))
				{
					p.sendMessage(plugin.grise.getPrefix() +
							"Vous avez rejoint " + plugin.teamf.getString("grise.name"));
					plugin.grise.addPlayer(p);
					if(!plugin.playersInTeam.contains(p.getUniqueId()))
					{
						plugin.playersInTeam.add(p.getUniqueId());
					}
				}
				else
				{
					p.sendMessage(ChatColor.RED + "Cette equipe est complete !");
				}
			}
			if (bname.contains("Quitter son equipe")) 
			{				
				if(plugin.playersInTeam.contains(p.getUniqueId()))
				{
					plugin.playersInTeam.remove(p.getUniqueId());
					plugin.s.getPlayerTeam(p).removePlayer(p);
					p.sendMessage("Vous avez quitte votre equipe !");
				}
			}
			e.setCancelled(true);
			openTeamInv(p);
		}
	}

	@EventHandler
	public void CancelRegen(EntityRegainHealthEvent e)
	{
		Entity en=e.getEntity();
		if (e.getRegainReason().equals(EntityRegainHealthEvent.RegainReason.SATIATED) &&  en.getWorld()==Bukkit.getWorld(plugin.getConfig().getString("world"))) {
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e)
	{
		Player p = e.getPlayer();

		if (!plugin.gameStarted)
		{
			if(plugin.meetUp)
			{
				if(plugin.playersInTeam.contains(p.getUniqueId()) && plugin.teamf.getBoolean("options.meetupteamtp"))
				{
					if(plugin.s.getPlayerTeam(p) == plugin.rose)
					{
						p.teleport(plugin.meetupl1);
					}
					else if(plugin.s.getPlayerTeam(p) == plugin.jaune)
					{
						p.teleport(plugin.meetupl2);
					}
					else if(plugin.s.getPlayerTeam(p) == plugin.violette)
					{
						p.teleport(plugin.meetupl3);
					}
					else if(plugin.s.getPlayerTeam(p) == plugin.cyan)
					{
						p.teleport(plugin.meetupl4);
					}
					else if(plugin.s.getPlayerTeam(p) == plugin.verte)
					{
						p.teleport(plugin.meetupl5);
					}
					else if(plugin.s.getPlayerTeam(p) == plugin.grise)
					{
						p.teleport(plugin.meetupl6);
					}
				}
				else
				{
					p.teleport(plugin.meetupLocation);
				}
			}
			else
			{
				p.teleport(plugin.lobbyLocation);
			}
			
			p.setGameMode(GameMode.ADVENTURE);
			
			p.getInventory().setItem(0, new ItemStack(Material.BANNER, 1));
			ItemMeta meta1 = p.getInventory().getItem(0).getItemMeta();
			meta1.setDisplayName(ChatColor.GOLD + "Choisir " + plugin.teamChoiceString);
			p.getInventory().getItem(0).setItemMeta(meta1);

			e.setJoinMessage(ChatColor.BLUE + p.getName() + 
					ChatColor.YELLOW + " a rejoint la partie  " + 
					ChatColor.GRAY + "(" + 
					ChatColor.YELLOW + Bukkit.getOnlinePlayers().size() + "/" + 
					Bukkit.getMaxPlayers() + 
					ChatColor.GRAY + ")");

			plugin.playersInLobby.add(p.getUniqueId());
		}
		else if (!plugin.gameEnd)
		{		
			if (!plugin.playersAlive.contains(p.getUniqueId()))
			{
				p.setGameMode(GameMode.SPECTATOR);
				p.teleport(new Location(
						Bukkit.getWorld(plugin.getConfig().getString("world")), 
						0, 120, 0));					
				
				plugin.playersSpec.add(p.getUniqueId());
				
				e.setJoinMessage(ChatColor.GRAY + ChatColor.ITALIC.toString() + 
						p.getName() + " a rejoint la partie  ");
			}
		}
		else if (plugin.gameEnd)
		{
			p.teleport(plugin.respawnLocation);
			p.setGameMode(GameMode.ADVENTURE);

			e.setJoinMessage(ChatColor.BLUE + p.getName() + 
					ChatColor.YELLOW + " a rejoint la partie  " + 
					ChatColor.GRAY + "(" + 
					ChatColor.YELLOW + Bukkit.getOnlinePlayers().size() + "/" + Bukkit.getMaxPlayers() + 
					ChatColor.GRAY + ")");

			plugin.playersInLobby.add(p.getUniqueId());
		}
	}
	
	@EventHandler
	public void OnPlayerDisconnect(PlayerQuitEvent e)
	{
		if(plugin.playersInLobby.contains(e.getPlayer().getUniqueId()))
		{
			plugin.playersInLobby.remove(e.getPlayer().getUniqueId());
		}
		else if(plugin.playersSpec.contains(e.getPlayer().getUniqueId()))
		{
			plugin.playersSpec.remove(e.getPlayer().getUniqueId());
		}
	}
	
	@EventHandler
	public void OnPlayerRespawn(PlayerRespawnEvent e)
	{
		Player p = e.getPlayer();
		if(!plugin.gameStarted)
		{
			if(plugin.meetUp)
			{
				e.setRespawnLocation(plugin.meetupLocation);
			}
			else 
			{
				e.setRespawnLocation(plugin.respawnLocation);
			}
			p.setGameMode(GameMode.ADVENTURE);
			
			p.getInventory().setItem(0, new ItemStack(Material.BANNER, 1));
			ItemMeta meta1 = p.getInventory().getItem(0).getItemMeta();
			meta1.setDisplayName(ChatColor.GOLD + "Choisir " + plugin.teamChoiceString);
			p.getInventory().getItem(0).setItemMeta(meta1);

			if(!plugin.playersInLobby.contains(p.getUniqueId()))
			{
				plugin.playersInLobby.add(p.getUniqueId());
			}
		}
		else if(!plugin.gameEnd)
		{
			if(plugin.playersInLobby.contains(p.getUniqueId()))
			{
				p.setGameMode(GameMode.ADVENTURE);
				e.setRespawnLocation(plugin.respawnLocation);
			}
			else
			{
				p.setGameMode(GameMode.SPECTATOR);
				e.setRespawnLocation(new Location(
						Bukkit.getWorld(plugin.getConfig().getString("world")), 
						0, 120, 0));
				
				if(!plugin.playersSpec.contains(p.getUniqueId()))
				{
					plugin.playersSpec.add(p.getUniqueId());
				}
				
				p.sendTitle(
						"Pensez a vous mute sur Mumble !", 
						"Par fairplay, assurez-vous que les joueurs en vie ne puissent pas vous entendre !");
			}
		}
		else if(plugin.gameEnd)
		{
			e.setRespawnLocation(plugin.respawnLocation);
			p.setGameMode(GameMode.ADVENTURE);
			
			if(!plugin.playersInLobby.contains(p.getUniqueId()))
			{
				plugin.playersInLobby.add(p.getUniqueId());
			}
		}
	}

	@EventHandler
	public void Options(PlayerInteractEvent e)
	{
		Player p = e.getPlayer();
		Action a = e.getAction();
		if ((a.equals(Action.RIGHT_CLICK_AIR)
				|| a.equals(Action.RIGHT_CLICK_BLOCK))
				&& p.getItemInHand().getType() == Material.BANNER
				&& p.getItemInHand().getItemMeta().getDisplayName().equals(ChatColor.GOLD + "Choisir " + plugin.teamChoiceString))
		{
			e.setCancelled(true);      
			openTeamInv(p);
		}
	}
	
	@EventHandler
	public void PlayerImmunityBeforeTaupes(EntityDamageEvent e)
	{
		if(!plugin.gameStarted || plugin.gameEnd)
		{
			return;
		}
		
		if(plugin.taupessetup)
		{
			return;
		}

		if(e.getEntity() instanceof Player)
		{
			Player player = (Player)e.getEntity();
			if(plugin.playersInLobby.contains(player.getUniqueId()))
			{
				return;
			}
			if(!player.hasPotionEffect(PotionEffectType.DAMAGE_RESISTANCE))
			{
				boolean lethal = (player.getHealth() - e.getFinalDamage()) < 1;
				if(lethal)
				{
					e.setCancelled(true);
					player.setHealth(1.0);
				}
			}
		}
	}
	
	@EventHandler
	public void PlayerDeathInGame(PlayerDeathEvent e)
	{
		Player player = e.getEntity();
		
		String deathmsg ="Ep"+plugin.episode+"-"+plugin.minute+":"+plugin.objSecond+" "+e.getDeathMessage();

		if(plugin.playersAlive.contains(player.getUniqueId()))
		{	    	    
			plugin.deathf.addDefault(player.getName(), deathmsg);
			plugin.deathf.options().copyDefaults(true);
			try 
			{
				plugin.deathf.save(plugin.filesManager.deathf);
			}
			catch (IOException e1) {}

			//alive.remove(player.getUniqueId());  
			plugin.playersAlive.remove(player.getUniqueId());

			e.getDrops().add(new ItemStack(Material.SKULL_ITEM));
			e.getDrops().add(new ItemStack(Material.GOLDEN_APPLE));

			Team team = player.getScoreboard().getPlayerTeam(player);
			team.removePlayer(player);

			e.setDeathMessage(team.getPrefix() 
					+ e.getEntity().getName() 
					+ " est mort !");

			for (Player pl : Bukkit.getOnlinePlayers()) 
			{
				pl.playSound(pl.getLocation(), Sound.ENTITY_WITHER_DEATH, 10.0F, 10.0F);
			}

			for(int i = 0; i < plugin.taupes.size(); i++)
			{
				if (plugin.taupes.get(i).contains(player.getUniqueId())) 
				{
					plugin.aliveTaupes.remove(player.getUniqueId());
				}
			}

			for(int i = 0; i < plugin.supertaupes.size(); i++)
			{
				if (plugin.supertaupes.get(i) == player.getUniqueId()) 
				{
					plugin.aliveSupertaupes.remove(player.getUniqueId());
				}
			}

			new BukkitRunnable()
			{
				public void run()
				{	
					plugin.unregisterTeam();
					plugin.unregisterTaupeTeam();
					plugin.checkVictory();
					
					try
					{
						Bukkit.getPlayer("Spec").performCommand("dynmap hide " + player.getName());
						Bukkit.getPlayer("Spec").performCommand("tp " + player.getName() + " 0 500 0");
					}
					catch(Exception ex) {}
				}
			}.runTaskLater(plugin, 60);	
		}
		/*
		else if(plugin.duelInProgress)
		{
			String victor;
			String loser;
			if(plugin.provoked == player.getUniqueId())
			{
				victor = Bukkit.getPlayer(plugin.provoker).getName();
				loser = Bukkit.getPlayer(plugin.provoked).getName();
			}
			else
			{
				victor = Bukkit.getPlayer(plugin.provoked).getName();
				loser = Bukkit.getPlayer(plugin.provoker).getName();
			}
			for (Player pl : Bukkit.getOnlinePlayers()) 
			{
				if(!plugin.playersAlive.contains(pl.getUniqueId()))
				{
					pl.sendMessage(victor + " a remporte son duel contre " + loser + " !");
				}
			}
			plugin.duelInProgress = false;
			Bukkit.getPlayer(plugin.provoked).setGameMode(GameMode.SPECTATOR);
			Bukkit.getPlayer(plugin.provoker).setGameMode(GameMode.SPECTATOR);
			plugin.provoked = null;
			plugin.provoker = null;
		}*/
	}
	
	@EventHandler
	public void BrewCancel(BrewEvent e)
	{
					
		BrewerInventory bi = e.getContents();
		
		Block block=e.getBlock();
		
		if (block.getWorld()!=Bukkit.getWorld(plugin.getConfig().getString("world"))) return;
		
		
		if ((bi.getIngredient().getType().equals(Material.GLOWSTONE_DUST)) && 
				(!plugin.getConfig().getBoolean("potions.allowglowstone")))
		{
			for (HumanEntity player : bi.getViewers()) {
				player.sendMessage(ChatColor.RED + 
						"Les potions de niveau 2 sont interdites !");
			}
			e.setCancelled(true);
		}
		else if ((bi.getIngredient().getType().equals(Material.BLAZE_POWDER)) && 
				(!plugin.getConfig().getBoolean("potions.strength")))
		{
			e.setCancelled(true);
			for (HumanEntity player : bi.getViewers()) {
				player.sendMessage(ChatColor.RED + 
						"Les potions de force sont interdites !");
			}
		}
		else if ((bi.getIngredient().getType().equals(Material.GHAST_TEAR)) && 
				(!plugin.getConfig().getBoolean("potions.regeneration")))
		{
			e.setCancelled(true);
			for (HumanEntity player : bi.getViewers()) {
				player.sendMessage(ChatColor.RED + 
						"Les potions de regeneration sont interdites !");
			}
		}
	}

	/*
	@EventHandler
	public void RespawTp(PlayerRespawnEvent e)
	{
		final Player p = e.getPlayer();
		new BukkitRunnable()
		{
			public void run()
			{
				p.teleport(new Location(Bukkit.getWorld(EventsClass.plugin.getConfig().get("lobby.world").toString()), EventsClass.plugin.getConfig().getInt("lobby.X"), EventsClass.plugin.getConfig().getInt("lobby.Y"), EventsClass.plugin.getConfig().getInt("lobby.Z")));p.setGameMode(GameMode.SPECTATOR);
			}
		}.runTaskLater(plugin, 4L);
	}*/
	
//	@EventHandler
//	public void CancelDropInLobby(PlayerDropItemEvent e)
//	{
//		Player p = e.getPlayer();
//		if (p.getWorld().equals(Bukkit.getWorld(plugin.getConfig().get("lobby.world").toString()))) 
//		{
//			Location loc = p.getLocation();
//			if(loc.getX() < plugin.minigamef.getInt("skywars.bound_min.X") 
//					|| loc.getX() > plugin.minigamef.getInt("skywars.bound_max.X")
//					|| loc.getY() < plugin.minigamef.getInt("skywars.bound_min.Y")
//					|| loc.getY() > plugin.minigamef.getInt("skywars.bound_max.Y")
//					|| loc.getZ() < plugin.minigamef.getInt("skywars.bound_min.Z")
//					|| loc.getZ() > plugin.minigamef.getInt("skywars.bound_max.Z"))
//			{
//				e.setCancelled(true);
//			}
//		}
//	}
	
	/*@EventHandler
	public void CancelPVP(EntityDamageEvent e)
	{
		if (e.getEntity().getWorld().equals(Bukkit.getWorld(plugin.getConfig().get("lobby.world").toString()))) 
		{
			if(!plugin.duelInProgress)
			{
				e.setCancelled(true);
			}
		}
	}*/
	
	@EventHandler
	public void CancelPVPInGame(EntityDamageByEntityEvent e)
	{		
		if (	e.getDamager() instanceof Player && 
				e.getEntity() instanceof Player)
		{
			
		Player damager=	(Player) e.getDamager();
		Player player = (Player)e.getEntity();
			
			if (!pvp )		
			{
				
				if(plugin.playersInLobby.contains(player.getUniqueId()))
				{
					return;
				}
				e.setCancelled(true);
			}
			else if(damager.getInventory().getItemInMainHand().getType()==Material.GOLD_SWORD) {
				
				damager.sendMessage(ChatColor.DARK_RED+"Cette épée ne peut pas être utilisée en pvp");
				player.setFireTicks(0);
				e.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void OnPlayerOpenTreasureChest(PlayerInteractEvent e)
	{
		
		
		if(e.getPlayer().getWorld()!=Bukkit.getWorld(plugin.getConfig().getString("world"))) return;
		
		if(e.getAction() == Action.RIGHT_CLICK_BLOCK && e.getClickedBlock().getType() == Material.TRAPPED_CHEST)
		{
			if(e.getClickedBlock().getLocation().distance(plugin.chestLocation) <= 10.0f)
			{
				try
				{
					Bukkit.getPlayer("Spec").performCommand("dmarker delete chest");
				}
				catch(Exception ex) {}
				
				
				if(plugin.getConfig().getBoolean("chest.random"))
				{
					Block redstoneBlock = e.getClickedBlock();
					Location redstoneLocation = redstoneBlock.getLocation();
					int x = (int)redstoneBlock.getLocation().getX();	
					int y = 120;
					int z = (int)redstoneBlock.getLocation().getZ();

					redstoneBlock.setType(Material.AIR);
					redstoneLocation.setY(y);
					Bukkit.getWorld(plugin.getConfig().get("world").toString()).getBlockAt(redstoneLocation).setType(Material.AIR);
					
					redstoneLocation.setY(y + 1);
					Bukkit.getWorld(plugin.getConfig().get("world").toString()).getBlockAt(redstoneLocation).setType(Material.AIR);
					redstoneLocation.setX(x + 1);
					redstoneLocation.setY(y + 1);
					redstoneLocation.setZ(z);
					Bukkit.getWorld(plugin.getConfig().get("world").toString()).getBlockAt(redstoneLocation).setType(Material.AIR);
					redstoneLocation.setX(x - 1);
					redstoneLocation.setY(y + 1);
					redstoneLocation.setZ(z);
					Bukkit.getWorld(plugin.getConfig().get("world").toString()).getBlockAt(redstoneLocation).setType(Material.AIR);
					redstoneLocation.setX(x);
					redstoneLocation.setY(y + 1);
					redstoneLocation.setZ(z + 1);
					Bukkit.getWorld(plugin.getConfig().get("world").toString()).getBlockAt(redstoneLocation).setType(Material.AIR);
					redstoneLocation.setX(x);
					redstoneLocation.setY(y + 1);
					redstoneLocation.setZ(z - 1);
					Bukkit.getWorld(plugin.getConfig().get("world").toString()).getBlockAt(redstoneLocation).setType(Material.AIR);

					redstoneLocation.setX(x);
					redstoneLocation.setY(y + 2);
					redstoneLocation.setZ(z);
					Bukkit.getWorld(plugin.getConfig().get("world").toString()).getBlockAt(redstoneLocation).setType(Material.AIR);
					redstoneLocation.setX(x + 1);
					redstoneLocation.setY(y + 2);
					redstoneLocation.setZ(z);
					Bukkit.getWorld(plugin.getConfig().get("world").toString()).getBlockAt(redstoneLocation).setType(Material.AIR);
					redstoneLocation.setX(x - 1);
					redstoneLocation.setY(y + 2);
					redstoneLocation.setZ(z);
					Bukkit.getWorld(plugin.getConfig().get("world").toString()).getBlockAt(redstoneLocation).setType(Material.AIR);
					redstoneLocation.setX(x);
					redstoneLocation.setY(y + 2);
					redstoneLocation.setZ(z + 1);
					Bukkit.getWorld(plugin.getConfig().get("world").toString()).getBlockAt(redstoneLocation).setType(Material.AIR);
					redstoneLocation.setX(x);
					redstoneLocation.setY(y + 2);
					redstoneLocation.setZ(z - 1);
					Bukkit.getWorld(plugin.getConfig().get("world").toString()).getBlockAt(redstoneLocation).setType(Material.AIR);
					redstoneLocation.setX(x + 2);
					redstoneLocation.setY(y + 2);
					redstoneLocation.setZ(z);
					Bukkit.getWorld(plugin.getConfig().get("world").toString()).getBlockAt(redstoneLocation).setType(Material.AIR);
					redstoneLocation.setX(x - 2);
					redstoneLocation.setY(y + 2);
					redstoneLocation.setZ(z);
					Bukkit.getWorld(plugin.getConfig().get("world").toString()).getBlockAt(redstoneLocation).setType(Material.AIR);
					redstoneLocation.setX(x);
					redstoneLocation.setY(y + 2);
					redstoneLocation.setZ(z + 2);
					Bukkit.getWorld(plugin.getConfig().get("world").toString()).getBlockAt(redstoneLocation).setType(Material.AIR);
					redstoneLocation.setX(x);
					redstoneLocation.setY(y + 2);
					redstoneLocation.setZ(z - 2);
					Bukkit.getWorld(plugin.getConfig().get("world").toString()).getBlockAt(redstoneLocation).setType(Material.AIR);
					redstoneLocation.setX(x + 1);
					redstoneLocation.setY(y + 2);
					redstoneLocation.setZ(z + 1);
					Bukkit.getWorld(plugin.getConfig().get("world").toString()).getBlockAt(redstoneLocation).setType(Material.AIR);
					redstoneLocation.setX(x + 1);
					redstoneLocation.setY(y + 2);
					redstoneLocation.setZ(z - 1);
					Bukkit.getWorld(plugin.getConfig().get("world").toString()).getBlockAt(redstoneLocation).setType(Material.AIR);
					redstoneLocation.setX(x - 1);
					redstoneLocation.setY(y + 2);
					redstoneLocation.setZ(z + 1);
					Bukkit.getWorld(plugin.getConfig().get("world").toString()).getBlockAt(redstoneLocation).setType(Material.AIR);
					redstoneLocation.setX(x - 1);
					redstoneLocation.setY(y + 2);
					redstoneLocation.setZ(z - 1);
					Bukkit.getWorld(plugin.getConfig().get("world").toString()).getBlockAt(redstoneLocation).setType(Material.AIR);
					
					redstoneLocation.setX(x);
					redstoneLocation.setY(y + 3);
					redstoneLocation.setZ(z);
					Bukkit.getWorld(plugin.getConfig().get("world").toString()).getBlockAt(redstoneLocation).setType(Material.AIR);
					redstoneLocation.setY(y + 4);
					Bukkit.getWorld(plugin.getConfig().get("world").toString()).getBlockAt(redstoneLocation).setType(Material.AIR);
					redstoneLocation.setY(y + 5);
					Bukkit.getWorld(plugin.getConfig().get("world").toString()).getBlockAt(redstoneLocation).setType(Material.AIR);
					redstoneLocation.setY(y + 6);
					Bukkit.getWorld(plugin.getConfig().get("world").toString()).getBlockAt(redstoneLocation).setType(Material.AIR);
				}
			}
		}
	}
	
	@EventHandler
	public void OnPlayerMineRessource(BlockBreakEvent e)
	{
		if(!plugin.getConfig().getBoolean("options.autosmelting"))
			return;
		
		if (e.getBlock().getWorld()!=Bukkit.getWorld(plugin.getConfig().getString("world"))) return;
		
		if(e.getBlock().getType() == Material.IRON_ORE)
		{
			e.getBlock().setType(Material.AIR);
			e.getBlock().getWorld().dropItemNaturally(e.getBlock().getLocation(), new ItemStack(Material.IRON_INGOT, 1));
			
	    	ExperienceOrb orb = e.getBlock().getWorld().spawn(e.getBlock().getLocation(), ExperienceOrb.class);
	    	orb.setExperience(1);
		}
		else if(e.getBlock().getType() == Material.GOLD_ORE)
		{
			e.getBlock().setType(Material.AIR);
			e.getBlock().getWorld().dropItemNaturally(e.getBlock().getLocation(), new ItemStack(Material.GOLD_INGOT, 1));
			
			ExperienceOrb orb = e.getBlock().getWorld().spawn(e.getBlock().getLocation(), ExperienceOrb.class);
	    	orb.setExperience(2);
		}
	}
	
	@EventHandler
    public void FurnaceBurnEvent(FurnaceBurnEvent e) 
	{
		if(!plugin.getConfig().getBoolean("options.fastcooking"))
		{
			return;
		}
		
        Furnace furnace = (Furnace) e.getBlock().getState();
        furnace.setCookTime((short)100);
    }
 
    @EventHandler
    public void FurnaceSmeltEvent(FurnaceSmeltEvent e) 
    {
		if(!plugin.getConfig().getBoolean("options.fastcooking"))
		{
			return;
		}
		
        Furnace furnace = (Furnace) e.getBlock().getState();
        furnace.setCookTime((short)100);
    }
 
    @EventHandler
    public void OnInventoryClick(BlockPlaceEvent e) 
    {
		
    	
    	
    	if(!plugin.getConfig().getBoolean("options.fastcooking"))
		{
			return;
		}
		
    	Block block = e.getBlock();
    	if (block.getWorld()!=Bukkit.getWorld(plugin.getConfig().getString("world"))) return;
    	
    	if(block.getType() == Material.FURNACE || block.getType() == Material.BURNING_FURNACE)
    	{
    		Furnace furnace = (Furnace) block.getState();
            furnace.setCookTime((short)100);
    	}
    }
    
    
    @EventHandler
    public void OnLeaveDecay(LeavesDecayEvent e) 
    {
    	
    
    
    World world =e.getBlock().getWorld();
    Location blockpos=e.getBlock().getLocation();
    Integer dropchance = 50;
    Random random = new Random();
    ItemStack drop = new ItemStack(Material.APPLE,1);
    
    if (world!=Bukkit.getWorld(plugin.getConfig().getString("world"))) return;
   
    if (random.nextInt((dropchance - 0) + 1) + 0==dropchance) {
    	
    	world.dropItem(blockpos, drop);
    	ExperienceOrb orb = world.spawn(blockpos, ExperienceOrb.class);
    	orb.setExperience(3);
    	
    }
    
    	
    	return;
    }
    
    
    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        
        if (player.getWorld()!=Bukkit.getWorld(plugin.getConfig().getString("world"))) return;
 
        if (event.getCause() == TeleportCause.ENDER_PEARL) {
            event.setCancelled(true);
 
            player.teleport(event.getTo());
        }
        
        if (event.getCause() == TeleportCause.CHORUS_FRUIT) {
            
        	event.setCancelled(true);
        	
        	Integer playerX=player.getLocation().getBlockX();
        	Integer playerZ=player.getLocation().getBlockZ();
        	
        	Block blockground=player.getWorld().getHighestBlockAt(playerX,playerZ);
        	
        	blockground.setType(Material.PURPUR_PILLAR);
        	
        	Integer surfaceY=blockground.getLocation().getBlockY();
        	
        	Location destinationTP =new Location(player.getWorld(),playerX,surfaceY+1,playerZ);
 
            player.teleport(destinationTP);
            
            if (player.getHealth()>2) player.setHealth(2);
            player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 600, 2));
        }
        
    }
    
}
