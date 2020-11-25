package fr.vraken.gameoftaupes.EventsClasses;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Furnace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.FurnaceBurnEvent;
import org.bukkit.event.inventory.FurnaceSmeltEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.FurnaceInventory;

import fr.vraken.gameoftaupes.GameOfTaupes;

public class FastCookingEventsClass implements Listener
{
	GameOfTaupes plugin;
	World gotworld;

	public FastCookingEventsClass(GameOfTaupes gameoftaupes)
	{
		plugin = gameoftaupes;
		gotworld = Bukkit.getWorld(plugin.getConfig().getString("world"));
	}
	
	@EventHandler
    public void FurnaceBurnEvent(FurnaceBurnEvent e) 
	{
		if(!plugin.getConfig().getBoolean("options.fastcooking")
				|| e.getBlock().getWorld() != gotworld)
			return;
		
		Furnace furnace = (Furnace) e.getBlock().getState();
		//Integer cookingmult = plugin.getConfig().getInt("options.cookingmultiplier");
		//furnace.setCookTimeTotal(furnace.getCookTimeTotal() / cookingmult);		
		//furnace.update();		
		Short cooktime = (short) plugin.getConfig().getInt("options.cooktime");		
		furnace.setCookTime(cooktime);
    }
 
    @EventHandler
    public void FurnaceSmeltEvent(FurnaceSmeltEvent e) 
    {
    	if(!plugin.getConfig().getBoolean("options.fastcooking")
    			|| e.getBlock().getWorld() != gotworld)
			return;
		
    	Furnace furnace = (Furnace) e.getBlock();
		//Integer cookingmult = plugin.getConfig().getInt("options.cookingmultiplier");
		Short cooktime = (short) plugin.getConfig().getInt("options.cooktime");		
		furnace.setCookTime(cooktime);

		/*new BukkitRunnable() 
		{
			public void run() 
			{
				Furnace furnace = (Furnace) e.getBlock().getState();

				if ((furnace.getCookTimeTotal() > (short) (furnace.getCookTimeTotal() / cookingmult))) 
				{
					furnace.setCookTimeTotal((short) (furnace.getCookTimeTotal() / cookingmult));
					furnace.update();
					cancel();
				} 
				else 
					cancel();
			}
		}.runTaskTimer(plugin, 2L, 1L);*/
    }
 
    @EventHandler
    public void OnBlockPlace(BlockPlaceEvent e) 
    {
    	if (!plugin.getConfig().getBoolean("options.fastcooking"))
    		return;

    	Block block = e.getBlock();
    	if (block.getWorld() != gotworld) 
    		return;

    	if (block.getType() == Material.FURNACE 
    			|| block.getType() == Material.LEGACY_BURNING_FURNACE)
    	{
    		Furnace furnace = (Furnace) block.getState();
    		Short cooktime = (short) plugin.getConfig().getInt("options.cooktime");		
    		furnace.setCookTime(cooktime);
    	}
    }
    
    @EventHandler
    public void onInventoryClick(InventoryClickEvent e)
    {
    	if (!plugin.getConfig().getBoolean("options.fastcooking"))
    		return;

        if(e.getInventory() instanceof FurnaceInventory) 
            return;
    	
        Furnace furnace = (Furnace) (e.getWhoClicked().getTargetBlock(null, 10)).getState();
		Short cooktime = (short) plugin.getConfig().getInt("options.cooktime");	

        if ((e.getSlot() == 0 || e.getSlot() == 1)
        		&& e.getCursor().getType() != Material.AIR
        		&& furnace.getCookTime() > cooktime)
        	furnace.setCookTime(cooktime);
    }
}
