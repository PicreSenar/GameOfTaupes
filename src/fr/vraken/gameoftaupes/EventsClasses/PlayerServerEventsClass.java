package fr.vraken.gameoftaupes.EventsClasses;

import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Team;

import fr.vraken.gameoftaupes.GameOfTaupes;

public class PlayerServerEventsClass implements Listener
{
	GameOfTaupes plugin;
	World gotworld;
	
	Long shieldcooldown;

	public PlayerServerEventsClass(GameOfTaupes gameoftaupes)
	{
		plugin = gameoftaupes;
		gotworld = Bukkit.getWorld(plugin.getConfig().getString("world"));
	}

	@EventHandler
	public void OnChatEvent(AsyncPlayerChatEvent e)
	{
		Team team = e.getPlayer().getScoreboard().getPlayerTeam(e.getPlayer());
		if (team == null) 
			return;
		
		String format = team.getPrefix() + "<%s> " + ChatColor.WHITE + "%s";
		e.setFormat(format);
	}
	
	@EventHandler
	public void OnPlayerJoin(PlayerJoinEvent e)
	{
		Player p = e.getPlayer();

		if (!plugin.gameEnd)
		{		
			if (!plugin.playersAlive.contains(p.getUniqueId()))
			{
				p.setGameMode(GameMode.SPECTATOR);
				p.teleport(new Location(
						gotworld, 
						0, 120, 0));					
				
				if(!plugin.playersSpec.contains(p.getUniqueId()))
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
			plugin.playersInLobby.remove(e.getPlayer().getUniqueId());
		else if(plugin.playersSpec.contains(e.getPlayer().getUniqueId()))
			plugin.playersSpec.remove(e.getPlayer().getUniqueId());
	}
	
	@EventHandler
	public void OnPlayerRespawn(PlayerRespawnEvent e)
	{
		Player p = e.getPlayer();
		if(!plugin.gameEnd)
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
						gotworld, 
						0, 120, 0));
				
				if(!plugin.playersSpec.contains(p.getUniqueId()))
					plugin.playersSpec.add(p.getUniqueId());
				
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
				plugin.playersInLobby.add(p.getUniqueId());
		}
	}
	
	@EventHandler
	public void PlayerDeathInGame(PlayerDeathEvent e)
	{
		Player player = e.getEntity();
		
		String deathmsg = "Ep" + plugin.episode + "-" + plugin.customScoreboardManager.minute 
				+ ":" + plugin.objSecond + " " + e.getDeathMessage();

		if (plugin.playersAlive.contains(player.getUniqueId()))
		{	    	    
			plugin.deathf.addDefault(player.getName(), deathmsg);
			plugin.deathf.options().copyDefaults(true);
			try 
			{
				plugin.deathf.save(plugin.filesManager.deathf);
			}
			catch (IOException e1) {}

			plugin.playersAlive.remove(player.getUniqueId());
		
			Team team = player.getScoreboard().getPlayerTeam(player);
			team.removePlayer(player);

			e.setDeathMessage(team.getPrefix() 
					+ e.getEntity().getName() 
					+ " est mort !");

			for (Player pl : Bukkit.getOnlinePlayers()) 
			{
				pl.playSound(pl.getLocation(), Sound.ENTITY_WITHER_DEATH, 10.0F, 10.0F);
			}

			for(int i = 0; i < plugin.teamsManager.taupes.size(); i++)
			{
				if (plugin.teamsManager.taupes.get(i).contains(player.getUniqueId())) 
					plugin.teamsManager.aliveTaupes.remove(player.getUniqueId());
			}

			for(int i = 0; i < plugin.teamsManager.supertaupes.size(); i++)
			{
				if (plugin.teamsManager.supertaupes.get(i) == player.getUniqueId())
					plugin.teamsManager.aliveSupertaupes.remove(player.getUniqueId());
			}

			new BukkitRunnable()
			{
				public void run()
				{	
					plugin.UnregisterTeam();
					plugin.UnregisterTaupeTeam();
					plugin.CheckVictory();
				}
			}.runTaskLater(plugin, 60);	
			
			
			ItemStack head = new ItemStack(Material.PLAYER_HEAD);
			SkullMeta headmeta= (SkullMeta) head.getItemMeta();
			
			headmeta.setDisplayName(player.getDisplayName());
			headmeta.setOwningPlayer(player);
			
			head.setItemMeta(headmeta);
				
			e.getDrops().add(head);
			e.getDrops().add(new ItemStack(Material.GOLDEN_APPLE));
		}
	}
	
}
