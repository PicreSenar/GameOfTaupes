package fr.vraken.gameoftaupes;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Utilities 
{
	public static Location GetLocationFromFile(FileConfiguration file, String worldName, String teamName)
	{
		Location loc = new Location(Bukkit.getWorld(worldName), 
				file.getInt("" + teamName + "X"),
				file.getInt("" + teamName + "Y"), 
				file.getInt("" + teamName + "Z"));
		
		return loc;
	}
	
	public static void ClearPlayer(Player player, int nodamageDuration, boolean haste, boolean saturation, Location spawn) 
	{
		player.getInventory().clear();
		player.getInventory().setHelmet(null);
		player.getInventory().setChestplate(null);
		player.getInventory().setLeggings(null);
		player.getInventory().setBoots(null);
		player.setExp(0.0f);
		player.setLevel(0);

		for (PotionEffect potion : player.getActivePotionEffects()) 
		{
			player.removePotionEffect(potion.getType());
		}

		player.setGameMode(GameMode.SURVIVAL);
		player.setHealth(20.0D);
		player.setFoodLevel(40);
		
		player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20 * nodamageDuration, 4));

		if (haste)
			player.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, Integer.MAX_VALUE, 2, true, false));

		if (saturation)
			player.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, Integer.MAX_VALUE, 2, true, false));

		player.getInventory().setItemInMainHand(new ItemStack(Material.GOLDEN_APPLE, 2));

		//DON D'UNE CORDE SORTIE
		ItemStack item = new ItemStack(Material.CHORUS_FRUIT,1);
		ItemMeta itemM = item.getItemMeta();
		itemM.setDisplayName(ChatColor.YELLOW + "Corde sortie");
		itemM.setLore(Arrays.asList("N'utiliser qu'en cas d'urgence",
				"Permet de se teleporter a la surface",
				"Effets secondaires : Perte de vie, nausees"));		
		item.setItemMeta(itemM);

		player.getInventory().setItem(9, item);
	}
}
