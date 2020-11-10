package fr.vraken.gameoftaupes;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class FilesManager
{
	public File configf, teamf, bossf, deathf, minigamef;
	private FileConfiguration config, team, boss, death, minigame;
	GameOfTaupes plugin;

	public FilesManager(GameOfTaupes plugin) throws IOException, InvalidConfigurationException
	{
		this.plugin = plugin;
		createFiles();
		addConfigDefault();
		addTeamDefault();
		addBossDefault();
		addMinigameDefault();
	}

	public FileConfiguration getTeamConfig() 
	{
		return this.team;
	}

	public FileConfiguration getBossConfig() 
	{
		return this.boss;
	}

	public FileConfiguration getDeathConfig() 
	{
		return this.death;
	}

	public FileConfiguration getMinigameConfig() 
	{
		return this.minigame;
	}

	private void createFiles() throws IOException 
	{
		configf = new File(plugin.getDataFolder(), "config.yml");
		teamf = new File(plugin.getDataFolder(), "team.yml");
		bossf = new File(plugin.getDataFolder(), "boss.yml");
		deathf = new File(plugin.getDataFolder(), "death.yml");
		minigamef = new File(plugin.getDataFolder(), "minigame.yml");

		if (!configf.exists()) 
		{
			configf.createNewFile();
		}
		if (!teamf.exists()) 
		{
			teamf.createNewFile();
		}
		if (!bossf.exists()) 
		{
			bossf.createNewFile();
		}
		if (!deathf.exists()) 
		{
			deathf.createNewFile();
		}
		if (!minigamef.exists()) 
		{
			minigamef.createNewFile();
		}

		config = new YamlConfiguration();
		team = new YamlConfiguration();
		boss = new YamlConfiguration();
		death = new YamlConfiguration();
		minigame = new YamlConfiguration();
	}

	public void addTeamDefault() throws IOException, InvalidConfigurationException
	{
		team.load(teamf);

		team.addDefault("rose.name", "rose");
		team.addDefault("rose.X", Integer.valueOf(500));
		team.addDefault("rose.Y", Integer.valueOf(250));
		team.addDefault("rose.Z", Integer.valueOf(500));
		team.addDefault("rose.meetupX", Integer.valueOf(500));
		team.addDefault("rose.meetupY", Integer.valueOf(250));
		team.addDefault("rose.meetupZ", Integer.valueOf(500));
		team.addDefault("cyan.name", "cyan");
		team.addDefault("cyan.X", Integer.valueOf(500));
		team.addDefault("cyan.Y", Integer.valueOf(250));
		team.addDefault("cyan.Z", Integer.valueOf(-500));
		team.addDefault("cyan.meetupX", Integer.valueOf(500));
		team.addDefault("cyan.meetupY", Integer.valueOf(250));
		team.addDefault("cyan.meetupZ", Integer.valueOf(-500));
		team.addDefault("jaune.name", "jaune");
		team.addDefault("jaune.X", Integer.valueOf(-500));
		team.addDefault("jaune.Y", Integer.valueOf(250));
		team.addDefault("jaune.Z", Integer.valueOf(500));
		team.addDefault("jaune.meetupX", Integer.valueOf(-500));
		team.addDefault("jaune.meetupY", Integer.valueOf(250));
		team.addDefault("jaune.meetupZ", Integer.valueOf(500));
		team.addDefault("violette.name", "violette");
		team.addDefault("violette.X", Integer.valueOf(-500));
		team.addDefault("violette.Y", Integer.valueOf(250));
		team.addDefault("violette.Z", Integer.valueOf(-500));
		team.addDefault("violette.meetupX", Integer.valueOf(-500));
		team.addDefault("violette.meetupY", Integer.valueOf(250));
		team.addDefault("violette.meetupZ", Integer.valueOf(-500));
		team.addDefault("verte.name", "verte");
		team.addDefault("verte.X", Integer.valueOf(0));
		team.addDefault("verte.Y", Integer.valueOf(250));
		team.addDefault("verte.Z", Integer.valueOf(0));
		team.addDefault("verte.meetupX", Integer.valueOf(0));
		team.addDefault("verte.meetupY", Integer.valueOf(250));
		team.addDefault("verte.meetupZ", Integer.valueOf(0));
		team.addDefault("grise.name", "grise");
		team.addDefault("grise.X", Integer.valueOf(0));
		team.addDefault("grise.Y", Integer.valueOf(250));
		team.addDefault("grise.Z", Integer.valueOf(0));
		team.addDefault("grise.meetupX", Integer.valueOf(0));
		team.addDefault("grise.meetupY", Integer.valueOf(250));
		team.addDefault("grise.meetupZ", Integer.valueOf(0));

		team.options().copyDefaults(true);
		team.save(teamf);
	}

	public void addBossDefault() throws FileNotFoundException, IOException, InvalidConfigurationException
	{
		boss.load(bossf);

		boss.addDefault("boss.active", Boolean.valueOf(false));		
		boss.addDefault("boss.1", "Gothmog");
		boss.addDefault("boss.2", "Lurtz");
		boss.addDefault("boss.3", "le berzerker");
		boss.addDefault("boss.4", "Arachne");
		boss.addDefault("boss.5", "les gobelins");
		boss.addDefault("boss.6", "Saroumane");

		boss.addDefault("temple1.X", Integer.valueOf(250));
		boss.addDefault("temple1.Y", Integer.valueOf(70));
		boss.addDefault("temple1.Z", Integer.valueOf(250));
		boss.addDefault("temple2.X", Integer.valueOf(250));
		boss.addDefault("temple2.Y", Integer.valueOf(70));
		boss.addDefault("temple2.Z", Integer.valueOf(250));
		boss.addDefault("temple3.X", Integer.valueOf(250));
		boss.addDefault("temple3.Y", Integer.valueOf(70));
		boss.addDefault("temple3.Z", Integer.valueOf(250));
		boss.addDefault("temple4.X", Integer.valueOf(250));
		boss.addDefault("temple4.Y", Integer.valueOf(70));
		boss.addDefault("temple4.Z", Integer.valueOf(250));
		boss.addDefault("temple5.X", Integer.valueOf(250));
		boss.addDefault("temple5.Y", Integer.valueOf(70));
		boss.addDefault("temple5.Z", Integer.valueOf(250));
		boss.addDefault("temple6.X", Integer.valueOf(250));
		boss.addDefault("temple6.Y", Integer.valueOf(70));
		boss.addDefault("temple6.Z", Integer.valueOf(250));

		boss.options().copyDefaults(true);
		boss.save(bossf);
	}

	public void addConfigDefault() throws FileNotFoundException, IOException, InvalidConfigurationException
	{
		config.load(configf);

		plugin.getConfig().addDefault("worldborder.size", Integer.valueOf(1500));
		plugin.getConfig().addDefault("worldborder.finalsize", Integer.valueOf(100));
		plugin.getConfig().addDefault("worldborder.retractafter", Integer.valueOf(30));
		plugin.getConfig().addDefault("worldborder.episodestorestract", Integer.valueOf(1));
		plugin.getConfig().addDefault("worldborder.finalretract", Integer.valueOf(70));
		plugin.getConfig().addDefault("potions.allowglowstone", Boolean.valueOf(false));
		plugin.getConfig().addDefault("lobby.world", "lobby");
		plugin.getConfig().addDefault("lobby.X", Integer.valueOf(0));
		plugin.getConfig().addDefault("lobby.Y", Integer.valueOf(100));
		plugin.getConfig().addDefault("lobby.Z", Integer.valueOf(0));
		plugin.getConfig().addDefault("lobby.respawnX", Integer.valueOf(0));
		plugin.getConfig().addDefault("lobby.respawnY", Integer.valueOf(100));
		plugin.getConfig().addDefault("lobby.respawnZ", Integer.valueOf(0));
		plugin.getConfig().addDefault("lobby.meetupX", Integer.valueOf(0));
		plugin.getConfig().addDefault("lobby.meetupY", Integer.valueOf(100));
		plugin.getConfig().addDefault("lobby.meetupZ", Integer.valueOf(0));
		plugin.getConfig().addDefault("chest.random", Boolean.valueOf(false));
		plugin.getConfig().addDefault("chest.timer", Integer.valueOf(10));
		plugin.getConfig().addDefault("chest.X", Integer.valueOf(0));
		plugin.getConfig().addDefault("chest.Y", Integer.valueOf(62));
		plugin.getConfig().addDefault("chest.Z", Integer.valueOf(0));
		plugin.getConfig().addDefault("potions.regeneration", Boolean.valueOf(false));
		plugin.getConfig().addDefault("potions.strength", Boolean.valueOf(false));
		plugin.getConfig().addDefault("world", "world");
		plugin.getConfig().addDefault("world_nether", "world_nether");
		plugin.getConfig().addDefault("options.taupesperteam", Integer.valueOf(1));
		plugin.getConfig().addDefault("options.taupesteams", Integer.valueOf(1));
		plugin.getConfig().addDefault("options.nodamagetime", Integer.valueOf(20));
		plugin.getConfig().addDefault("options.timecycle", Boolean.valueOf(false));
		plugin.getConfig().addDefault("options.minplayers", Integer.valueOf(20));
		plugin.getConfig().addDefault("options.pvptime", Integer.valueOf(20));
		plugin.getConfig().addDefault("options.cooldown", Boolean.valueOf(false));
		plugin.getConfig().addDefault("options.playersperteam", Integer.valueOf(4));
		plugin.getConfig().addDefault("options.settaupesafter", Integer.valueOf(10));
		plugin.getConfig().addDefault("options.forcereveal", Integer.valueOf(50));
		plugin.getConfig().addDefault("options.supertaupe", Boolean.valueOf(false));
		plugin.getConfig().addDefault("options.setsupertaupesafter", Integer.valueOf(11));
		plugin.getConfig().addDefault("options.superreveal", Integer.valueOf(60));
		plugin.getConfig().addDefault("options.autosmelting", Boolean.valueOf(false));
		plugin.getConfig().addDefault("options.fastcooking", Boolean.valueOf(false));
		plugin.getConfig().addDefault("options.cookingmultiplier", Integer.valueOf(1));
		plugin.getConfig().addDefault("options.haste", Boolean.valueOf(false));
		plugin.getConfig().addDefault("options.saturation", Boolean.valueOf(false));
		plugin.getConfig().addDefault("options.meetupteamtp", Boolean.valueOf(false));
		plugin.getConfig().addDefault("options.revealing", Boolean.valueOf(false));
		
		plugin.getConfig().addDefault("options.job.mineur", Boolean.valueOf(false));
		plugin.getConfig().addDefault("options.job.bucheron", Boolean.valueOf(false));
		plugin.getConfig().addDefault("options.job.chasseur", Boolean.valueOf(false));
		
		

		plugin.getConfig().addDefault("duelspawn1.X", Integer.valueOf(0));
		plugin.getConfig().addDefault("duelspawn1.Y", Integer.valueOf(250));
		plugin.getConfig().addDefault("duelspawn1.Z", Integer.valueOf(0));
		plugin.getConfig().addDefault("duelspawn2.X", Integer.valueOf(0));
		plugin.getConfig().addDefault("duelspawn2.Y", Integer.valueOf(250));
		plugin.getConfig().addDefault("duelspawn2.Z", Integer.valueOf(0));
		
		plugin.getConfig().addDefault("Disallowed enchants", String.valueOf(""));

		plugin.getConfig().options().copyDefaults(true);
		plugin.saveConfig();
	}
	
	public void addMinigameDefault() throws IOException, InvalidConfigurationException
	{
		minigame.load(minigamef);

		minigame.addDefault("skywars.bound_min.X", 0);
		minigame.addDefault("skywars.bound_min.Y", 0);
		minigame.addDefault("skywars.bound_min.Z", 0);
		minigame.addDefault("skywars.bound_max.X", 100);
		minigame.addDefault("skywars.bound_max.Y", 100);
		minigame.addDefault("skywars.bound_max.Z", 100);

		minigame.options().copyDefaults(true);
		minigame.save(minigamef);
	}
}
