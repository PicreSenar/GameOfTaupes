package fr.vraken.gameoftaupes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Skeleton.SkeletonType;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class BossManager 
{
	GameOfTaupes plugin;
	
	private ArrayList<Location> shrinesLocation = new ArrayList<Location>();
	private ArrayList<Integer> activatedShrines = new ArrayList<Integer>();
	private Map<Integer, Integer> bossLocations = new HashMap<Integer, Integer>();
	public Map<Integer, Integer> aliveBoss = new HashMap<Integer, Integer>();
	public ArrayList<Integer> gobelins = new ArrayList<Integer>();
	public ArrayList<Integer> blazes = new ArrayList<Integer>();
	
	public ArrayList<Integer> spawnedBoss = new ArrayList<Integer>();

	public BossManager(GameOfTaupes gameoftaupes)
	{
		plugin = gameoftaupes;
		initializeShrineLocations();
	}
	
	public ArrayList<Location> getShrinesLocation()
	{
		return this.shrinesLocation;
	}
	
	public ArrayList<Integer> getActivatedShrines()
	{
		return this.activatedShrines;
	}
	
	public Map<Integer, Integer> getBossLocations()
	{
		return this.bossLocations;
	}
	
	private void initializeShrineLocations()
	{
		this.shrinesLocation.add(new Location(Bukkit.getWorld(this.plugin.getConfig().get("world").toString()), this.plugin.bossf.getInt("temple1.X"), this.plugin.bossf.getInt("temple1.Y"), this.plugin.bossf.getInt("temple1.Z")));
		this.shrinesLocation.add(new Location(Bukkit.getWorld(this.plugin.getConfig().get("world").toString()), this.plugin.bossf.getInt("temple2.X"), this.plugin.bossf.getInt("temple2.Y"), this.plugin.bossf.getInt("temple2.Z")));
		this.shrinesLocation.add(new Location(Bukkit.getWorld(this.plugin.getConfig().get("world").toString()), this.plugin.bossf.getInt("temple3.X"), this.plugin.bossf.getInt("temple3.Y"), this.plugin.bossf.getInt("temple3.Z")));
		this.shrinesLocation.add(new Location(Bukkit.getWorld(this.plugin.getConfig().get("world").toString()), this.plugin.bossf.getInt("temple4.X"), this.plugin.bossf.getInt("temple4.Y"), this.plugin.bossf.getInt("temple4.Z")));
		this.shrinesLocation.add(new Location(Bukkit.getWorld(this.plugin.getConfig().get("world").toString()), this.plugin.bossf.getInt("temple5.X"), this.plugin.bossf.getInt("temple5.Y"), this.plugin.bossf.getInt("temple5.Z")));
		this.shrinesLocation.add(new Location(Bukkit.getWorld(this.plugin.getConfig().get("world").toString()), this.plugin.bossf.getInt("temple6.X"), this.plugin.bossf.getInt("temple6.Y"), this.plugin.bossf.getInt("temple6.Z")));
	}
	
	public void activateShrine(int level)
	{
		Random rdm = new Random();
		int index = 0;
		do
		{
			index = rdm.nextInt(6);
			if(!activatedShrines.contains(index))
			{
				activatedShrines.add(index);
				break;
			}
			
		} while(true);
		
		Bukkit.broadcastMessage(ChatColor.DARK_RED + "Un autel a �t� activ� par une �nergie mystique ! Utilisez votre boussole pour en trouver la source ! ");
	    Location loc = shrinesLocation.get(index);
	    try 
		{
			Bukkit.getPlayer("Spec").performCommand("dmarker add " + plugin.bossf.getString("boss." + level) + " icon:skull x:" + loc.getX() + " y:" + loc.getY() + " z:" + loc.getZ() + " world:" + loc.getWorld().getName());
		}
		catch(Exception ex) {}
	    
		bossLocations.put(index, level);
		aliveBoss.put(level, index);
	}
	
	public void summonBoss(int index)
	{
		int level =  bossLocations.get(index);
		bossLocations.remove(index);
		switch(level)
		{
		case 1:			
			spawnZombie(shrinesLocation.get(index));
			break;
		case 2:
			spawnSkeleton(shrinesLocation.get(index));
			break;
		case 3:
			spawnCreeper(shrinesLocation.get(index));
			break;
		case 4:
			spawnSpider(shrinesLocation.get(index));
			break;
		case 5:
			spawnGobelins(shrinesLocation.get(index));
			break;
		case 6:
			spawnWitch(shrinesLocation.get(index));
			break;
		}
	}
	
	private void spawnZombie(Location loc)
	{
		Entity gzombie = this.plugin.getServer().getWorld(this.plugin.getConfig().getString("world")).spawnEntity(loc, EntityType.ZOMBIE);
	
		gzombie.setCustomName(this.plugin.bossf.getString("boss.1"));
		gzombie.setCustomNameVisible(true);
	
		LivingEntity livingzombie = (LivingEntity)gzombie;
		livingzombie.setHealth(20.0f);
		Zombie z = (Zombie) gzombie;
		z.setBaby(false);
		z.setRemoveWhenFarAway(false);
		
		livingzombie.getEquipment().setBoots(new ItemStack(Material.GOLDEN_BOOTS, 1));
		livingzombie.getEquipment().setChestplate(new ItemStack(Material.GOLDEN_CHESTPLATE, 1));
		livingzombie.getEquipment().setHelmet(new ItemStack(Material.GOLDEN_HELMET, 1));
		livingzombie.getEquipment().setLeggings(new ItemStack(Material.GOLDEN_LEGGINGS, 1));
		livingzombie.getEquipment().setItemInHand(new ItemStack(Material.GOLDEN_AXE, 1));
		
		livingzombie.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 100000, 1));
		
		this.spawnedBoss.add(livingzombie.getEntityId());
	}
	
	private void spawnSkeleton(Location loc)
	{
		Entity sskeleton = this.plugin.getServer().getWorld(this.plugin.getConfig().getString("world")).spawnEntity(loc, EntityType.SKELETON);

		sskeleton.setCustomName(this.plugin.bossf.getString("boss.2"));
		sskeleton.setCustomNameVisible(true);

		LivingEntity livingskeleton = (LivingEntity)sskeleton;
		livingskeleton.setHealth(20.0f);
		@SuppressWarnings("deprecation")
		Skeleton s = (Skeleton) sskeleton;
		s.setSkeletonType(SkeletonType.NORMAL);
		s.setRemoveWhenFarAway(false);

		livingskeleton.getEquipment().setBoots(new ItemStack(Material.CHAINMAIL_BOOTS, 1));
		livingskeleton.getEquipment().setChestplate(new ItemStack(Material.CHAINMAIL_CHESTPLATE, 1));
		livingskeleton.getEquipment().setHelmet(new ItemStack(Material.CHAINMAIL_HELMET, 1));
		livingskeleton.getEquipment().setLeggings(new ItemStack(Material.CHAINMAIL_LEGGINGS, 1));
		livingskeleton.getEquipment().setItemInHand(new ItemStack(Material.BOW, 1));
		
		this.spawnedBoss.add(livingskeleton.getEntityId());
	}
	
	private void spawnCreeper(Location loc)
	{
		Entity ccreeper = this.plugin.getServer().getWorld(this.plugin.getConfig().getString("world")).spawnEntity(loc, EntityType.CREEPER);

		ccreeper.setCustomName(this.plugin.bossf.getString("boss.3"));
		ccreeper.setCustomNameVisible(true);

		LivingEntity livingcreeper = (LivingEntity)ccreeper;
		livingcreeper.setHealth(20.0f);
		Creeper c = (Creeper) ccreeper;
		c.setPowered(true);
		c.setRemoveWhenFarAway(false);
		
		this.spawnedBoss.add(livingcreeper.getEntityId());
	}
	
	private void spawnSpider(Location loc)
	{
		Entity spider = this.plugin.getServer().getWorld(this.plugin.getConfig().getString("world")).spawnEntity(loc, EntityType.SPIDER);

		spider.setCustomName(this.plugin.bossf.getString("boss.4"));
		spider.setCustomNameVisible(true);

		LivingEntity livingspider = (LivingEntity)spider;
		livingspider.setHealth(16.0f);
		livingspider.setRemoveWhenFarAway(false);
		
		this.spawnedBoss.add(livingspider.getEntityId());
	}
	
	private void spawnGobelins(Location loc)
	{
		for(int i = 0; i < 5; i++)
		{
			Entity gzombie = this.plugin.getServer().getWorld(this.plugin.getConfig().getString("world")).spawnEntity(loc, EntityType.ZOMBIE);
		
			gzombie.setCustomName("Gobelin");
			gzombie.setCustomNameVisible(true);
			gobelins.add(gzombie.getEntityId());
		
			LivingEntity livingzombie = (LivingEntity)gzombie;
			livingzombie.setHealth(20.0f);
			Zombie z = (Zombie) gzombie;
			z.setBaby(true);
			z.setRemoveWhenFarAway(false);
			
			livingzombie.getEquipment().setBoots(new ItemStack(Material.LEATHER_BOOTS, 1));
			livingzombie.getEquipment().setChestplate(new ItemStack(Material.LEATHER_CHESTPLATE, 1));
			livingzombie.getEquipment().setHelmet(new ItemStack(Material.LEATHER_HELMET, 1));
			livingzombie.getEquipment().setLeggings(new ItemStack(Material.LEATHER_LEGGINGS, 1));
			livingzombie.getEquipment().setItemInHand(new ItemStack(Material.STONE_SWORD, 1));
			
			this.spawnedBoss.add(livingzombie.getEntityId());
		}
	}
	
	private void spawnWitch(Location loc)
	{
		Entity witch = this.plugin.getServer().getWorld(this.plugin.getConfig().getString("world")).spawnEntity(loc, EntityType.WITCH);

		witch.setCustomName(this.plugin.bossf.getString("boss.6"));
		witch.setCustomNameVisible(true);

		LivingEntity livingwitch = (LivingEntity)witch;
		livingwitch.setHealth(20.0f);
		livingwitch.setRemoveWhenFarAway(false);
		
		livingwitch.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 2400000, 10));
		
		this.spawnedBoss.add(livingwitch.getEntityId());
		
		Location loc1 = loc.clone();
		Location loc2 = loc.clone();
		Location loc3 = loc.clone();
		Location loc4 = loc.clone();
		loc1.add(3,0,3);
		loc2.add(3,0,-3);
		loc3.add(-3,0,-3);
		loc4.add(-3,0,3);
		
		Entity blaze1 = this.plugin.getServer().getWorld(this.plugin.getConfig().getString("world")).spawnEntity(loc1, EntityType.BLAZE);
		Entity blaze2 = this.plugin.getServer().getWorld(this.plugin.getConfig().getString("world")).spawnEntity(loc2, EntityType.BLAZE);
		Entity blaze3 = this.plugin.getServer().getWorld(this.plugin.getConfig().getString("world")).spawnEntity(loc3, EntityType.BLAZE);
		Entity blaze4 = this.plugin.getServer().getWorld(this.plugin.getConfig().getString("world")).spawnEntity(loc4, EntityType.BLAZE);
		
		blaze1.setCustomName("Fire minion");
		blaze2.setCustomName("Fire minion");
		blaze3.setCustomName("Fire minion");
		blaze4.setCustomName("Fire minion");
		
		this.blazes.add(blaze1.getEntityId());
		this.blazes.add(blaze2.getEntityId());
		this.blazes.add(blaze3.getEntityId());
		this.blazes.add(blaze4.getEntityId());
		
		LivingEntity bl1 = (LivingEntity) blaze1;
		LivingEntity bl2 = (LivingEntity) blaze2;
		LivingEntity bl3 = (LivingEntity) blaze3;
		LivingEntity bl4 = (LivingEntity) blaze4;
		
		bl1.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 2400000, 10));
		bl2.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 2400000, 10));
		bl3.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 2400000, 10));
		bl4.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 2400000, 10));
		
		this.spawnedBoss.add(bl1.getEntityId());
		this.spawnedBoss.add(bl2.getEntityId());
		this.spawnedBoss.add(bl3.getEntityId());
		this.spawnedBoss.add(bl4.getEntityId());
		
		bl1.setRemoveWhenFarAway(false);
		bl2.setRemoveWhenFarAway(false);
		bl3.setRemoveWhenFarAway(false);
		bl4.setRemoveWhenFarAway(false);
	}
}
