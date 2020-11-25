package fr.vraken.gameoftaupes.EventsClasses;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import fr.vraken.gameoftaupes.GameOfTaupes;

public class AutoSmeltingEventsClass implements Listener
{
	GameOfTaupes plugin;
	World gotworld;

	public AutoSmeltingEventsClass(GameOfTaupes gameoftaupes)
	{
		plugin = gameoftaupes;
		gotworld = Bukkit.getWorld(plugin.getConfig().getString("world"));
	}
	
	@EventHandler
	public void OnPlayerMineRessource(BlockBreakEvent e)
	{
		if(!plugin.getConfig().getBoolean("options.autosmelting"))
			return;
		
		if (e.getBlock().getWorld() != gotworld) 
			return;
		
		if (e.getBlock().getType() == Material.IRON_ORE)
		{
			e.getBlock().setType(Material.AIR);
			e.getBlock().getWorld().dropItemNaturally(e.getBlock().getLocation(), new ItemStack(Material.IRON_INGOT, 1));
			
	    	ExperienceOrb orb = e.getBlock().getWorld().spawn(e.getBlock().getLocation(), ExperienceOrb.class);
	    	orb.setExperience(1);
		}
		else if (e.getBlock().getType() == Material.GOLD_ORE)
		{
			e.getBlock().setType(Material.AIR);
			e.getBlock().getWorld().dropItemNaturally(e.getBlock().getLocation(), new ItemStack(Material.GOLD_INGOT, 1));
			
			ExperienceOrb orb = e.getBlock().getWorld().spawn(e.getBlock().getLocation(), ExperienceOrb.class);
	    	orb.setExperience(2);
		}
	}
}
