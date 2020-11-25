package fr.vraken.gameoftaupes.EventsClasses;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import fr.vraken.gameoftaupes.GameOfTaupes;

public class TreasureChestEventsClass implements Listener
{
	GameOfTaupes plugin;
	World gotworld;
	
	Random random = new Random();
	Integer appledropchance = 100;
	ItemStack apple = new ItemStack(Material.APPLE, 1);

	public TreasureChestEventsClass(GameOfTaupes gameoftaupes)
	{
		plugin = gameoftaupes;
		gotworld = Bukkit.getWorld(plugin.getConfig().getString("world"));
	}
	
	@EventHandler
	public void OnPlayerOpenTreasureChest(PlayerInteractEvent e)
	{
		if (e.getAction() != Action.RIGHT_CLICK_BLOCK 
				|| e.getClickedBlock().getType() != Material.TRAPPED_CHEST)
			return;


		if (e.getClickedBlock() == null 
				|| e.getClickedBlock().getWorld() != gotworld) 
			return;

		if (e.getPlayer().getGameMode() == GameMode.SPECTATOR)
			return;

		Block chestBlock = e.getClickedBlock();
		Location chestLocation = chestBlock.getLocation();
		int x = (int) chestLocation.getX();	
		int y = (int) chestLocation.getY();
		int z = (int) chestLocation.getZ();

		//DELETE FIRE
		Location fireLocation = new Location(gotworld, x, y - 1, z);
		if (gotworld.getBlockAt(fireLocation).getType() == Material.CAMPFIRE) 
			gotworld.getBlockAt(fireLocation).setType(Material.OAK_PLANKS);

		//DELETE HAY BALE
		Location hayLocation = new Location(gotworld, x, y - 2, z);
		if (gotworld.getBlockAt(hayLocation).getType() == Material.HAY_BLOCK) 
			gotworld.getBlockAt(hayLocation).setType(Material.AIR);
	}
}
