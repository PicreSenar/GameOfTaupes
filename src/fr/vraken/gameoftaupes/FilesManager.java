package fr.vraken.gameoftaupes;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class FilesManager
{
	public File configf, teamf, deathf;
	private FileConfiguration config, team, death;
	GameOfTaupes plugin;

	public FilesManager(GameOfTaupes plugin) throws IOException, InvalidConfigurationException
	{
		this.plugin = plugin;
		CreateFiles();
		AddConfigDefault();
		AddTeamDefault();
	}

	public FileConfiguration getTeamConfig() 
	{
		return this.team;
	}

	public FileConfiguration getDeathConfig() 
	{
		return this.death;
	}

	private void CreateFiles() throws IOException 
	{
		configf = new File(plugin.getDataFolder(), "config.yml");
		teamf = new File(plugin.getDataFolder(), "team.yml");
		deathf = new File(plugin.getDataFolder(), "death.yml");

		if (!configf.exists()) 
		{
			configf.createNewFile();
		}
		if (!teamf.exists()) 
		{
			teamf.createNewFile();
		}
		if (!deathf.exists()) 
		{
			deathf.createNewFile();
		}

		config = new YamlConfiguration();
		team = new YamlConfiguration();
		death = new YamlConfiguration();
	}

	public void AddTeamDefault() throws IOException, InvalidConfigurationException
	{
		team.load(teamf);

		team.addDefault("blue.name", "blue");
		team.addDefault("blue.X", Integer.valueOf(500));
		team.addDefault("blue.Y", Integer.valueOf(250));
		team.addDefault("blue.Z", Integer.valueOf(500));
		team.addDefault("blue.meetupX", Integer.valueOf(500));
		team.addDefault("blue.meetupY", Integer.valueOf(250));
		team.addDefault("blue.meetupZ", Integer.valueOf(500));
		team.addDefault("dark_aqua.name", "dark_aqua");
		team.addDefault("dark_aqua.X", Integer.valueOf(500));
		team.addDefault("dark_aqua.Y", Integer.valueOf(250));
		team.addDefault("dark_aqua.Z", Integer.valueOf(-500));
		team.addDefault("dark_aqua.meetupX", Integer.valueOf(500));
		team.addDefault("dark_aqua.meetupY", Integer.valueOf(250));
		team.addDefault("dark_aqua.meetupZ", Integer.valueOf(-500));
		team.addDefault("yellow.name", "yellow");
		team.addDefault("yellow.X", Integer.valueOf(-500));
		team.addDefault("yellow.Y", Integer.valueOf(250));
		team.addDefault("yellow.Z", Integer.valueOf(500));
		team.addDefault("yellow.meetupX", Integer.valueOf(-500));
		team.addDefault("yellow.meetupY", Integer.valueOf(250));
		team.addDefault("yellow.meetupZ", Integer.valueOf(500));
		team.addDefault("dark_purple.name", "dark_purple");
		team.addDefault("dark_purple.X", Integer.valueOf(-500));
		team.addDefault("dark_purple.Y", Integer.valueOf(250));
		team.addDefault("dark_purple.Z", Integer.valueOf(-500));
		team.addDefault("dark_purple.meetupX", Integer.valueOf(-500));
		team.addDefault("dark_purple.meetupY", Integer.valueOf(250));
		team.addDefault("dark_purple.meetupZ", Integer.valueOf(-500));
		team.addDefault("dark_green.name", "dark_green");
		team.addDefault("dark_green.X", Integer.valueOf(0));
		team.addDefault("dark_green.Y", Integer.valueOf(250));
		team.addDefault("dark_green.Z", Integer.valueOf(0));
		team.addDefault("dark_green.meetupX", Integer.valueOf(0));
		team.addDefault("dark_green.meetupY", Integer.valueOf(250));
		team.addDefault("dark_green.meetupZ", Integer.valueOf(0));
		team.addDefault("dark_gray.name", "dark_gray");
		team.addDefault("dark_gray.X", Integer.valueOf(0));
		team.addDefault("dark_gray.Y", Integer.valueOf(250));
		team.addDefault("dark_gray.Z", Integer.valueOf(0));
		team.addDefault("dark_gray.meetupX", Integer.valueOf(0));
		team.addDefault("dark_gray.meetupY", Integer.valueOf(250));
		team.addDefault("dark_gray.meetupZ", Integer.valueOf(0));

		team.options().copyDefaults(true);
		team.save(teamf);
	}

	public void AddConfigDefault() throws FileNotFoundException, IOException, InvalidConfigurationException
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
		plugin.getConfig().addDefault("chest.timer", Integer.valueOf(10));
		plugin.getConfig().addDefault("potions.regeneration", Boolean.valueOf(false));
		plugin.getConfig().addDefault("potions.strength", Boolean.valueOf(false));
		plugin.getConfig().addDefault("world", "world");
		plugin.getConfig().addDefault("world_nether", "world_nether");
		plugin.getConfig().addDefault("options.taupesperteam", Integer.valueOf(1));
		plugin.getConfig().addDefault("options.taupesteams", Integer.valueOf(1));
		plugin.getConfig().addDefault("options.nodamagetime", Integer.valueOf(20));
		plugin.getConfig().addDefault("options.timecycle", Boolean.valueOf(false));
		plugin.getConfig().addDefault("options.minplayers", Integer.valueOf(20));
		plugin.getConfig().addDefault("options.pvptime", Integer.valueOf(10));
		plugin.getConfig().addDefault("options.nodeathtime", Integer.valueOf(20));
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
		plugin.getConfig().addDefault("options.cooktime", Integer.valueOf(100));
		plugin.getConfig().addDefault("options.haste", Boolean.valueOf(false));
		plugin.getConfig().addDefault("options.saturation", Boolean.valueOf(false));
		plugin.getConfig().addDefault("options.meetupteamtp", Boolean.valueOf(false));
		plugin.getConfig().addDefault("options.revealing", Boolean.valueOf(false));
		
		plugin.getConfig().addDefault("Disallowed enchants", String.valueOf(""));

		plugin.getConfig().options().copyDefaults(true);
		plugin.saveConfig();
	}
}
