package fr.vraken.gameoftaupes.EventsClasses;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import fr.vraken.gameoftaupes.GameOfTaupes;

public class PotionEventsClass implements Listener
{
	GameOfTaupes plugin;
	World gotworld;

	public PotionEventsClass(GameOfTaupes gameoftaupes)
	{
		plugin = gameoftaupes;
		gotworld = Bukkit.getWorld(plugin.getConfig().getString("world"));
	}
    
    @EventHandler
    public void CancelPoisonSplashPotion(PotionSplashEvent e) {

    	Entity entity = e.getEntity();

    	if (entity.getWorld() != gotworld)
    		return;

    	for (PotionEffect pe : e.getPotion().getEffects()) 
    	{
    		if (pe.getType() == PotionEffectType.POISON) 
    			e.setCancelled(true);
    	}
    }
}
