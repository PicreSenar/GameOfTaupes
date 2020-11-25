package fr.vraken.gameoftaupes.EventsClasses;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.inventory.ItemStack;

import fr.vraken.gameoftaupes.GameOfTaupes;

public class AppleDropEventsClass implements Listener
{
	GameOfTaupes plugin;
	World gotworld;
	
	Random random = new Random();
	Integer appledropchance = 100;
	ItemStack apple = new ItemStack(Material.APPLE, 1);

	public AppleDropEventsClass(GameOfTaupes gameoftaupes)
	{
		plugin = gameoftaupes;
		gotworld = Bukkit.getWorld(plugin.getConfig().getString("world"));
	}
	
	@EventHandler
    public void OnLeavesDecay(LeavesDecayEvent e) 
    {
    	World world = e.getBlock().getWorld();
    	Location blockpos = e.getBlock().getLocation();

    	if (world != gotworld) 
    		return;

    	if (random.nextInt(appledropchance + 1) == appledropchance) 
    	{
    		world.dropItem(blockpos, apple);
    		ExperienceOrb orb = world.spawn(blockpos, ExperienceOrb.class);
    		orb.setExperience(10);
    		
    		e.setCancelled(true);
    	}
    }
}
