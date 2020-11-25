package fr.vraken.gameoftaupes.EventsClasses;

import java.util.Iterator;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;

import fr.vraken.gameoftaupes.GameOfTaupes;

public class EnchantingEventsClass implements Listener
{
	GameOfTaupes plugin;
	World gotworld;

	public EnchantingEventsClass(GameOfTaupes gameoftaupes)
	{
		plugin = gameoftaupes;
		gotworld = Bukkit.getWorld(plugin.getConfig().getString("world"));
	}

	@EventHandler
	public void OnClickOnDisallowedEnchants(InventoryClickEvent e)
	{
		if (e.getCurrentItem() == null)
			return;
		
		Player p = (Player) e.getWhoClicked();
		
		if (e.getView().getTitle().equals("Repair & Name") && e.getSlot() == 2) 
		{
			Map<Enchantment, Integer> enchants = e.getCurrentItem().getEnchantments();
			
			Iterator<Map.Entry<Enchantment, Integer>> iterator = enchants.entrySet().iterator();
			
			if(p.getWorld() != gotworld)
				return;
			
		    while (iterator.hasNext()) 
		    {
		        Map.Entry<Enchantment, Integer> entry = iterator.next();
		        
		        Enchantment enchantment = entry.getKey();
		        Integer enchantlevel = entry.getValue();
		        
		        Integer maxlevelenchant = plugin.getConfig().getInt("Disallowed enchants." + enchantment.getName());
		        
		        if (maxlevelenchant != 0 && enchantlevel > maxlevelenchant) 
		        { 		
		        	p.sendMessage("" +  enchantment.getName() + " - niveau " + enchantlevel 
		        			+ " : Cet enchantement est desactive (niveau max : " + maxlevelenchant + ")");
		        	e.setCancelled(true);
		        	break;
		        }
		    }
		}
	}

    @EventHandler
	public void OnEnchant(EnchantItemEvent e) 
    {
		Player player = e.getEnchanter();
		
		Map<Enchantment, Integer> enchants = e.getEnchantsToAdd();
		
		Iterator<Map.Entry<Enchantment, Integer>> iterator = enchants.entrySet().iterator();
		
		if(player.getWorld() != gotworld)
			return;

		while (iterator.hasNext()) 
		{	    	
			Map.Entry<Enchantment, Integer> entry = iterator.next();

			Enchantment enchantment = entry.getKey();
			Integer enchantlevel = entry.getValue();

			Integer maxlevelenchant = plugin.getConfig().getInt("Disallowed enchants." + enchantment.getName());

			if (maxlevelenchant != 0 && enchantlevel > maxlevelenchant) 
			{    	
				player.sendMessage(""+ enchantment.getName()+" - niveau " + enchantlevel 
						+ " : Cet enchantement est desactive (niveau max : " + maxlevelenchant + ")");
				e.setCancelled(true);
				return;
			}
		}
	}
}
