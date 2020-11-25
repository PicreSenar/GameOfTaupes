package fr.vraken.gameoftaupes.EventsClasses;

import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageModifier;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Team;

import fr.vraken.gameoftaupes.GameOfTaupes;

public class PlayerGameEventsClass implements Listener
{
	public boolean pvp = false;
	public boolean playerDeath = false;
	
	GameOfTaupes plugin;
	World gotworld;
	Long shieldcooldown;
	
	ItemStack playerSkull = new ItemStack(Material.LEGACY_SKULL);
	
	public PlayerGameEventsClass(GameOfTaupes gameoftaupes)
	{
		plugin = gameoftaupes;
		gotworld = Bukkit.getWorld(plugin.getConfig().getString("world"));
		shieldcooldown = plugin.getConfig().getInt("options.shieldcooldown") * 20L;
	}

	@EventHandler
	public void CancelRegen(EntityRegainHealthEvent e)
	{
		Entity en = e.getEntity();
		if (e.getRegainReason().equals(EntityRegainHealthEvent.RegainReason.SATIATED) 
				&&  en.getWorld() == gotworld)
			e.setCancelled(true);
	}

	@EventHandler
	public void CancelPvpAndDeath(EntityDamageByEntityEvent e)
	{
		if(playerDeath)
			return;
		
		if(!(e.getEntity() instanceof Player))
			return;

		Player player = (Player) e.getEntity();

		if(player.hasPotionEffect(PotionEffectType.DAMAGE_RESISTANCE)
				|| !plugin.playersAlive.contains(player.getUniqueId()))
			return;

		Entity damager = e.getDamager();
		if(!pvp)
		{
			if(damager instanceof Player) 
			{
				e.setCancelled(true);
				return;
			}
			if(damager instanceof Arrow)
			{
				Arrow arrow = (Arrow) damager;
				if ((arrow.getShooter() instanceof Player))
				{
					e.setCancelled(true);
					return;
				}
			}
		}
		
		if(damager instanceof Player)
		{
			Player pdamager = (Player) damager;
			
			if (pdamager.getInventory().getItemInMainHand().getType() == Material.GOLDEN_SWORD) 
			{
				pdamager.sendMessage(ChatColor.DARK_RED + "Cette epee ne peut pas etre utilisee en pvp");
				player.setFireTicks(0);
				e.setCancelled(true);
			}		
			else if (pdamager.getInventory().getItemInMainHand().getEnchantments().containsKey(Enchantment.FIRE_ASPECT))
			{
				pdamager.getInventory().getItemInMainHand().removeEnchantment(Enchantment.FIRE_ASPECT);
				pdamager.sendMessage(ChatColor.DARK_RED + "Le fire aspect est desactive");
				player.setFireTicks(0);
			}
		}

		boolean lethal = (player.getHealth() - e.getFinalDamage()) < 1;
		if(!lethal)
			return;

		e.setCancelled(true);
		player.setHealth(1.0);
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
	
	@EventHandler
	public void OnShieldBlock(EntityDamageByEntityEvent e)
	{		
		if (!pvp || !(e.getEntity() instanceof Player) || !(e.getDamager() instanceof Player))
			return;

		Player player = (Player) e.getEntity();
		Player damager=	(Player) e.getDamager();

		if(plugin.getConfig().getInt("options.shieldcooldown") <= 0
				|| player.getInventory().getItemInOffHand().getType() != Material.SHIELD) 
			return;
		
		if(e.getDamage() + e.getDamage(DamageModifier.BLOCKING) > 0.0)
			return;

		PlayerInventory Inventory = player.getInventory();
		ItemStack shield = player.getInventory().getItemInOffHand();
						
		player.getInventory().setItemInOffHand(new ItemStack(Material.AIR));
		
		Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, 
				new Runnable() 
				{
					@Override
					public void run() 
					{
						player.getInventory().setItemInOffHand(shield);	
					}
				}, shieldcooldown);
	}
	
    @EventHandler
    public void OnPlayerTeleport(PlayerTeleportEvent e) 
    {
        Player player = e.getPlayer();
        
        if (player.getWorld() != gotworld) 
        	return;
 
        if (e.getCause() == TeleportCause.ENDER_PEARL) 
        {
            e.setCancelled(true); 
            player.teleport(e.getTo());
            
            return;
        }
        
        if (e.getCause() != TeleportCause.CHORUS_FRUIT)
        	return;

        e.setCancelled(true);

        Integer playerX = player.getLocation().getBlockX();
        Integer playerZ = player.getLocation().getBlockZ();

        Block blockground = player.getWorld().getHighestBlockAt(playerX,playerZ);

        blockground.setType(Material.PURPUR_PILLAR);

        Integer surfaceY = blockground.getLocation().getBlockY();

        Location destinationTP = new Location(player.getWorld(), playerX, surfaceY + 1, playerZ);

        player.teleport(destinationTP);

        if (player.getHealth() > 16) 
        	player.setHealth(16);

        player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 300, 1));
        player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 300, 10));
        player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 300, 10));
    }

}