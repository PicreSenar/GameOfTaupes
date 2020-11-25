package fr.vraken.gameoftaupes;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Team;

import fr.vraken.gameoftaupes.EventsClasses.*;



public class GameOfTaupes extends JavaPlugin 
{
	// Files
	public FilesManager filesManager;
	public FileConfiguration teamf;
	public FileConfiguration deathf;

	public CustomScoreboardManager customScoreboardManager;
	public TasksManager tasksManager;;
	public TeamsManager teamsManager;
	
	// Listeners
	public LobbyEventsClass lobbyEventsClass;
	public AppleDropEventsClass appleDropEventsClass;
	public AutoSmeltingEventsClass autoSmeltingEventsClass;
	public EnchantingEventsClass enchantingEventsClass;
	public FastCookingEventsClass fastCookingEventsClass;
	public PlayerGameEventsClass playerGameEventsClass;
	public PlayerServerEventsClass playerServerEventsClass;
	public PotionEventsClass potionEventsClass;
	public TreasureChestEventsClass treasureChestEventsClass;

	// Players
	public ArrayList<UUID> playersInTeam = new ArrayList<UUID>();
	public ArrayList<UUID> playersAlive = new ArrayList<UUID>();
	public ArrayList<UUID> playersInLobby = new ArrayList<UUID>();
	public ArrayList<UUID> playersSpec = new ArrayList<UUID>();
	public ArrayList<UUID> playersWithJob = new ArrayList<UUID>();

	// Taupes
	public boolean taupessetup;
	public boolean supertaupessetup;
	
	// Chest
	public static ArrayList<ItemStack> tmpKits = new ArrayList<ItemStack>();
	public boolean finalZone = false;

	// Scoreboard
	public int episode;
	public boolean retract = false;
	public boolean finalretract = false;
	public Objective obj;
	public Objective vie;
	public int gameState;
	public String objMinute;
	public String objSecond;
	public String objTxt;
	public String countdownObj;
	public boolean hasChangedGS;
	public int tmpPlayers;
	public int tmpTeams;
	public int tmpBorder;
	public NumberFormat objFormatter;
	public int height = 10;
	public int minuteTot = 0;

	// Gamestates
	public boolean gameStarted = false;
	public boolean gameEnd = false;
	public boolean pvp = false;
	public Location lobbyLocation;
	public Location meetupLocation;
	public Location respawnLocation;
	
	// Announcements
	public int restractEpisode;
	public String teamAnnounceString = "L'equipe ";
	public String teamChoiceString = "son equipe";

	public void onEnable() 
	{
		System.out.println("+-------------VrakenGameOfTaupes--------------+");
		System.out.println("|           Plugin cree par Vraken            |");
		System.out.println("+---------------------------------------------+");

		
		// FILES
		// -----
		try 
		{
			filesManager = new FilesManager(this);
		}
		catch (IOException | InvalidConfigurationException e1) 
		{
			e1.printStackTrace();
		}

		teamf = filesManager.getTeamConfig();
		deathf = filesManager.getDeathConfig();

		getConfig().options().copyDefaults(true);
		teamf.options().copyDefaults(true);
		deathf.options().copyDefaults(true);
		saveConfig();

		
		// SCOREBOARD
		// ----------
		customScoreboardManager = new CustomScoreboardManager(this);
		
		
		// TASKS
		// -----
		tasksManager = new TasksManager(this);
		
		
		// WORLD
		// -----
		Bukkit.createWorld(new WorldCreator(getConfig().getString("lobby.world")));
		Bukkit.createWorld(new WorldCreator(getConfig().getString("world")));

		
		// LISTENER
		// --------
		lobbyEventsClass = new LobbyEventsClass(this);
		appleDropEventsClass = new AppleDropEventsClass(this);
		autoSmeltingEventsClass = new AutoSmeltingEventsClass(this);
		enchantingEventsClass = new EnchantingEventsClass(this);
		fastCookingEventsClass = new FastCookingEventsClass(this);
		playerGameEventsClass = new PlayerGameEventsClass(this);
		playerServerEventsClass = new PlayerServerEventsClass(this);
		potionEventsClass = new PotionEventsClass(this);
		treasureChestEventsClass = new TreasureChestEventsClass(this);
		
		Bukkit.getPluginManager().registerEvents(lobbyEventsClass, this);
				

		// TEAMS
		// -----
		teamsManager = new TeamsManager(this, 
				getConfig().getInt("options.taupesteams"), 
				getConfig().getBoolean("options.supertaupe"));

		teamsManager.UnregisterAll();
		teamsManager.RegisterAll();
		teamsManager.SetSpawnLocations(
				getConfig().get("world").toString(), 
				getConfig().get("lobby.world").toString(),
				getConfig().getBoolean("options.meetupteamtp"));

		
		// RECIPES
		// -------
		AddCustomRecipes();

		
		// LOCATIONS
		// ---------
		lobbyLocation = new Location(Bukkit.getWorld(getConfig().get("lobby.world").toString()),
				getConfig().getInt("lobby.X"), 
				getConfig().getInt("lobby.Y"),
				getConfig().getInt("lobby.Z"));

		meetupLocation = new Location(Bukkit.getWorld(getConfig().get("lobby.world").toString()),
				getConfig().getInt("lobby.meetupX"), 
				getConfig().getInt("lobby.meetupY"),
				getConfig().getInt("lobby.meetupZ"));

		respawnLocation = new Location(Bukkit.getWorld(getConfig().get("lobby.world").toString()),
				getConfig().getInt("lobby.respawnX"), 
				getConfig().getInt("lobby.respawnY"),
				getConfig().getInt("lobby.respawnZ"));
		

		super.onEnable();
	}

	public void startGame() 
	{
		gameStarted = true;
		gameState = 0;
		hasChangedGS = false;
		taupessetup = false;
		supertaupessetup = false;
		tmpBorder = getConfig().getInt("worldborder.size");
		pvp = false;

		
		// WORLD SETUP
		// -----------
		String world = getConfig().getString("world");
		Boolean istimecycle = getConfig().getBoolean("options.timecycle");
		Bukkit.getWorld(world).setGameRuleValue("doDaylightCycle", Boolean.valueOf(istimecycle).toString());
		Bukkit.getWorld(world).setGameRuleValue("announceAdvancements",Boolean.valueOf(false).toString());

		Bukkit.getWorld(getConfig().getString("world")).setWeatherDuration(50000);
		Bukkit.getWorld(getConfig().getString("world")).setStorm(false);
		Bukkit.getWorld(getConfig().getString("world")).setThundering(false);
		Bukkit.getWorld(getConfig().get("world").toString()).setTime(5000L);

		
		// TEAMS
		// -----
		teamsManager.ClearTeams();

		teamsManager.SetTaupes(
				getConfig().getInt("options.taupesperteam"), 
				getConfig().getInt("options.taupesteams"));
		teamsManager.SetSuperTaupe();


		int nbTeams = customScoreboardManager.s.getTeams().size() - getConfig().getInt("options.taupesteams");
		
		if (getConfig().getBoolean("options.supertaupe"))
			nbTeams -= getConfig().getInt("options.taupesteams");

		int taupesTot = nbTeams * getConfig().getInt("options.taupesperteam");
		int taupesMin = taupesTot / getConfig().getInt("options.taupesteams");
		int taupesLeft = taupesTot % getConfig().getInt("options.taupesteams");

		for (int i = 0; i < getConfig().getInt("options.taupesperteam"); i++) 
		{
			if (taupesLeft > 0) 
			{
				teamsManager.taupesperteam.put(i, taupesMin);
				taupesLeft--;
				continue;
			}

			teamsManager.taupesperteam.put(i, taupesMin);
		}

		episode += 1;

		// SCOREBOARD INITIALIZATION
		// -------------------------
		objFormatter = new DecimalFormat("00");
		

		// CLEARING INVENTORY AND STATUS OF EVERY PLAYER THEN TELEPORTING HIM TO HIS
		// SPAWN
		// -------------------------------------------------------------------------------
		ClearPlayers();


		getServer().getWorld(getConfig().getString("world")).getWorldBorder()
		.setSize(getConfig().getDouble("worldborder.size"));
		
		UnregisterEventsListener(lobbyEventsClass);
		
		RegisterEventsListeners();
	}

	public void stopGame() 
	{
		for (Player p : Bukkit.getOnlinePlayers()) 
		{
			p.getScoreboard().clearSlot(DisplaySlot.SIDEBAR);

			if (playersInLobby.contains(p.getUniqueId()))
				continue;

			playersInLobby.add(p.getUniqueId());

			p.getInventory().clear();
			p.setGameMode(GameMode.ADVENTURE);
			p.teleport(lobbyLocation);
		}

		gameEnd = true;
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		if (!(sender instanceof Player))
			return false;

		Player player = (Player) sender;

		//METIERS DEBUT GAME
		// ----------------
		if (cmd.getName().equalsIgnoreCase("bucheron") 
				|| cmd.getName().equalsIgnoreCase("mineur") 
				|| cmd.getName().equalsIgnoreCase("chasseur"))
		{
			SetJob(player, cmd.getName());
			return true;
		}

		// TAUPES CHAT
		// -----------
		if (cmd.getName().equalsIgnoreCase("t")) 
		{
			TaupeSendMessage(player, args);			
			return true;
		}

		// TAUPES REVEAL
		// -------------
		if (cmd.getName().equalsIgnoreCase("reveal")) 
		{
			TaupeReveal(player);			
			return true;
		}

		// SUPERTAUPE REVEAL
		// -----------------
		if (cmd.getName().equalsIgnoreCase("superreveal")) 
		{
			SupertaupeReveal(player);			
			return true;
		}

		// TAUPES CLAIM KIT
		// ----------------
		if (cmd.getName().equalsIgnoreCase("claim"))
		{
			TaupeClaimKit(player);			
			return true;
		}

		// ADMIN MEETUP
		// ------------
		if (cmd.getName().equalsIgnoreCase("gotmeetup") && player.isOp() && !gameStarted) 
		{
			lobbyEventsClass.meetUp = true;
			for (Player p : Bukkit.getOnlinePlayers()) 
			{
				p.teleport(meetupLocation);
			}
			return true;
		}

		// ADMIN START
		// -----------
		if (cmd.getName().equalsIgnoreCase("gotstart") && player.isOp() && !gameStarted) 
		{
			startGame();
			return true;
		}

		// ADMIN STOP
		// ----------
		if (cmd.getName().equalsIgnoreCase("gotstop") && player.isOp() && gameStarted) 
		{
			stopGame();
			return true;
		}

		// DEAD PLAYER RETURN TO LOBBY
		// ---------------------------
		if (cmd.getName().equalsIgnoreCase("gotlobby") 
				&& playersSpec.contains(player.getUniqueId())
				&& gameStarted 
				&& !gameEnd) 
		{
			playersSpec.remove(player.getUniqueId());
			playersInLobby.add(player.getUniqueId());
			player.setGameMode(GameMode.ADVENTURE);
			player.teleport(new Location(Bukkit.getWorld(getConfig().getString("lobby.world")),
					getConfig().getInt("lobby.respawnX"), getConfig().getInt("lobby.respawnY"),
					getConfig().getInt("lobby.respawnZ")));
			return true;
		}

		// DEAD PLAYER SPEC
		// ----------------
		if (cmd.getName().equalsIgnoreCase("gotspec") 
				&& playersInLobby.contains(player.getUniqueId())
				&& gameStarted 
				&& !gameEnd) 
		{
			playersSpec.add(player.getUniqueId());
			playersInLobby.remove(player.getUniqueId());
			player.setGameMode(GameMode.SPECTATOR);
			player.teleport(new Location(Bukkit.getWorld(getConfig().getString("world")), 0, 120, 0));
			return true;
		}

		//COMMANDES ADMIN DE TEST
		if (cmd.getName().equalsIgnoreCase("adminGoT") && player.isOp()) 
		{
			switch (args[0]) 
			{
			case "kit":
				ClaimKit(player, true);
				return true;
			}
		}

		return false;
	}


	// UTILITY FUNCTIONS
	// -----------------
	
	public void RegisterEventsListeners()
	{
		Bukkit.getPluginManager().registerEvents(appleDropEventsClass, this);
		Bukkit.getPluginManager().registerEvents(autoSmeltingEventsClass, this);
		Bukkit.getPluginManager().registerEvents(enchantingEventsClass, this);
		Bukkit.getPluginManager().registerEvents(fastCookingEventsClass, this);
		Bukkit.getPluginManager().registerEvents(playerGameEventsClass, this);
		Bukkit.getPluginManager().registerEvents(playerServerEventsClass, this);
		Bukkit.getPluginManager().registerEvents(potionEventsClass, this);
		Bukkit.getPluginManager().registerEvents(treasureChestEventsClass, this);
	}
	
	public void UnregisterEventsListener(Listener listener)
	{
		PlayerJoinEvent.getHandlerList().unregister(listener);
		PlayerQuitEvent.getHandlerList().unregister(listener);
		PlayerRespawnEvent.getHandlerList().unregister(listener);
		PlayerInteractEvent.getHandlerList().unregister(listener);
		InventoryClickEvent.getHandlerList().unregister(listener);
	}
	
	public void AddCustomRecipes()
	{
		NamespacedKey goldenHeadKey = new NamespacedKey(this, "golden_head");
		ShapedRecipe craft2 = new ShapedRecipe(goldenHeadKey, new ItemStack(Material.GOLDEN_APPLE));
		craft2.shape(new String[] { "***", "*x*", "***" });
		craft2.setIngredient('*', Material.GOLD_INGOT);
		craft2.setIngredient('x', Material.PLAYER_HEAD);
		Bukkit.addRecipe(craft2);

		NamespacedKey driedFleshKey = new NamespacedKey(this, "dried_flesh");
		ItemStack driedflesh = new ItemStack(Material.RABBIT_HIDE);
		ItemMeta driedfleshM = driedflesh.getItemMeta();
		driedfleshM.setDisplayName("Dried Flesh");
		driedflesh.setItemMeta(driedfleshM);
		FurnaceRecipe LeatherR = new FurnaceRecipe(driedFleshKey, driedflesh, Material.ROTTEN_FLESH, 0, 100);
		Bukkit.addRecipe(LeatherR);

		NamespacedKey boneDustKey = new NamespacedKey(this, "bone_dust");
		ShapedRecipe craft3 = new ShapedRecipe(boneDustKey, new ItemStack(Material.PAPER, 3));
		craft3.shape(new String[] {"%%%", "%%%"});
		craft3.setIngredient('%', Material.BONE_MEAL);
		Bukkit.addRecipe(craft3);
	}
	
	public void ClearPlayers() 
	{
		playersInLobby.clear();
		playersSpec.clear();

		for (Player p : Bukkit.getOnlinePlayers()) 
		{
			if (!playersInTeam.contains(p.getUniqueId())) 
			{
				p.kickPlayer("Vous n'avez pas choisi d'equipe. Tant pis pour vous !");
				continue;
			}

			playersAlive.add(p.getUniqueId());
			
			Location tp;
			String teamName = customScoreboardManager.s.getEntryTeam(p.getName()).getName();
			
			if (teamName.equals(teamf.getString("blue.name")))
				tp = teamsManager.l1;
			else if (teamName.equals(teamf.getString("yellow.name")))
				tp = teamsManager.l2;
			else if (teamName.equals(teamf.getString("dark_purple.name")))
				tp = teamsManager.l3;
			else if (teamName.equals(teamf.getString("dark_aqua.name")))
				tp = teamsManager.l4;
			else if (teamName.equals(teamf.getString("dark_green.name")))
				tp = teamsManager.l5;
			else
				tp = teamsManager.l6;
			
			Utilities.ClearPlayer(
					p, 
					getConfig().getInt("options.nodamagetime"), 
					getConfig().getBoolean("options.haste"), 
					getConfig().getBoolean("options.saturation"), 
					tp);
		}
	}

	public void ClearTeams() 
	{
		for (Team teams : customScoreboardManager.s.getTeams()) 
		{
			if(teams.getName().contains("Taupes"))
				continue;

			if (teams.getName().contains("SuperTaupe") && !getConfig().getBoolean("options.supertaupe"))
				teams.unregister();
			else if (teams.getSize() == 0 && !teams.getName().contains("SuperTaupe")) 
				teams.unregister();
		}
	}

	public void CheckVictory() 
	{
		Team lastTeam = null;
		int teamsAlive = 0;
		for (Team team : customScoreboardManager.s.getTeams()) 
		{
			if(team.getName().contains("aupe"))
				continue;

			teamsAlive++;
			if (teamsAlive > 1)
				return;

			lastTeam = team;
		}

		for (int i = 0; i < teamsManager.taupes.size(); ++i) 
		{
			for (UUID uid : teamsManager.taupes.get(i)) 
			{
				if(!playersAlive.contains(uid) || teamsManager.supertaupes.get(i) == uid)
					continue;

				teamsAlive++;
				if (teamsAlive > 1)
					return;

				lastTeam = teamsManager.taupesteam.get(i);
				break;
			}

			if (!teamsManager.aliveSupertaupes.contains(teamsManager.supertaupes.get(i)))
				continue;	

			teamsAlive++;
			if (teamsAlive > 1)
				return;

			lastTeam = teamsManager.supertaupesteam.get(i);
		}

		if(teamsAlive > 1)
			return;

		ForceReveal(false);
		SuperReveal(false);
		AnnounceWinner(lastTeam);
	}

	public void AnnounceWinner(Team team) 
	{
		if (team == null) 
		{
			Bukkit.broadcastMessage("Toutes les equipes ont ete eliminees, personne n'a gagne ! ");
			tasksManager.CancelAllTasks();
			return;
		}

		Bukkit.broadcastMessage(teamAnnounceString 
				+ team.getPrefix() + team.getName() 
				+ ChatColor.RESET + " a gagne ! ");

		tasksManager.CancelAllTasks();

		playersAlive.clear();
	}

	public void UnregisterTeam() 
	{
		for (Team teams : customScoreboardManager.s.getTeams()) 
		{
			if (teams.getSize() != 0 
					|| teams.getName().contains("Taupes")
					|| teams.getName().contains("SuperTaupe"))
				continue;
								
				Bukkit.broadcastMessage(teamAnnounceString 
						+ teams.getPrefix() + teams.getName()
						+ ChatColor.RESET + " a ete eliminee ! ");
				teams.unregister();
		}
	}

	public void UnregisterTaupeTeam() 
	{
		for (int i = 0; i < getConfig().getInt("options.taupesteams"); ++i) 
		{
			UUID supertaupe = teamsManager.supertaupes.get(i);

			boolean dead = true;
			int showed = 0;
			for (UUID uid : teamsManager.taupes.get(i)) 
			{
				if (teamsManager.aliveTaupes.contains(uid))
					dead = false;
				if (teamsManager.showedtaupes.contains(uid))
					showed++;
			}

			if (dead 
					&& !teamsManager.isTaupesTeamDead.get(i)
					&& teamsManager.taupes.get(i).size() == showed) 
			{
				teamsManager.isTaupesTeamDead.put(i, true);
				Bukkit.broadcastMessage(ChatColor.RED + "L'equipe des taupes #" + i + " a ete eliminee ! ");
				teamsManager.taupesteam.get(i).unregister();
			}
			
			if (!teamsManager.isSupertaupeDead.get(i) 
					&& teamsManager.showedsupertaupes.contains(supertaupe)
					&& !teamsManager.aliveSupertaupes.contains(supertaupe)
					&& getConfig().getBoolean("options.supertaupe")) 
			{
				teamsManager.isSupertaupeDead.put(i, true);
				Bukkit.broadcastMessage(ChatColor.DARK_RED + "La supertaupe #" + i + " a ete eliminee ! ");
				teamsManager.supertaupesteam.get(i).unregister();
			}
		}
	}

	public void EnablePvp()
	{
		pvp = true;
		playerGameEventsClass.pvp = true;
		Bukkit.broadcastMessage(ChatColor.RED + "Le pvp est maintenant actif !");
	}
	
	public void EnablePlayerDeath()
	{
		playerGameEventsClass.playerDeath = true;
		Bukkit.broadcastMessage(ChatColor.RED + "Vous pouvez maintenant mourir, attention derriere vous !");
	}
	
	public void BorderShrink()
	{
		retract = true;

		Bukkit.broadcastMessage("La carte est en train de retrecir ! " + "Depechez-vous d'aller entre -"
				+ (int) (getConfig().getDouble("worldborder.finalsize") / 2) + " et "
				+ (int) (getConfig().getDouble("worldborder.finalsize") / 2) + " ! ");

		getServer().getWorld(getConfig().getString("world"))
				   .getWorldBorder()
				   .setSize(getConfig().getDouble("worldborder.finalsize"),	1200 * getConfig().getInt("worldborder.episodestorestract"));
	}
	
	public void BorderFinalShrink()
	{
		finalZone = true;

		Bukkit.broadcastMessage(
				"Retrecissement final de la carte ! Il ne restera bientot plus aucun endroit ou se cacher !");

		getServer().getWorld(getConfig().getString("world"))
				   .getWorldBorder()
				   .setSize(2, 60 * 10);
	}
	
	public void BorderFinalFinalShrink()
	{
		Block block = Bukkit.getWorld(getConfig().get("world").toString()).getBlockAt(0, height, 0);
		block.setType(Material.AIR);
		++height;
	}

	public void SpawnChest() 
	{
		Random rdm = new Random();
		int x = rdm.nextInt(tmpBorder) - tmpBorder / 2;
		int z = rdm.nextInt(tmpBorder) - tmpBorder / 2;
		int y = Bukkit.getWorld(getConfig().getString("world")).getHighestBlockYAt(
				new Location(Bukkit.getWorld(getConfig().getString("world")), x, 0, z));

		Location chestLocation = new Location(Bukkit.getWorld(getConfig().get("world").toString()), x, y + 1, z);		
		Location hayLocation = new Location(Bukkit.getWorld(getConfig().get("world").toString()), x, y - 1, z);
		Location fireLocation = new Location(Bukkit.getWorld(getConfig().get("world").toString()), x, y, z);		

		Block chestBlock = Bukkit.getWorld(getConfig().get("world").toString())
				.getBlockAt(chestLocation);
		Block hayBlock = Bukkit.getWorld(getConfig().get("world").toString())
				.getBlockAt(hayLocation);
		Block fireBlock = Bukkit.getWorld(getConfig().get("world").toString())
				.getBlockAt(fireLocation);

		if (fireBlock.getType() == Material.WATER) 
		{
			Material buoyColor = Material.RED_CONCRETE;
			Location buoyLocation = new Location(Bukkit.getWorld(getConfig().get("world").toString()), x, y, z);

			for (Integer i = -1; i <= 1; ++i) 
			{
				for (Integer j = -1; j <= 1; ++j) 
				{
					buoyLocation = new Location(Bukkit.getWorld(getConfig().get("world").toString()), x + i, y, z + j);	
					Bukkit.getWorld(getConfig().get("world").toString()).getBlockAt(buoyLocation).setType(buoyColor);

					buoyColor = (buoyColor == Material.RED_CONCRETE) ? Material.WHITE_CONCRETE : Material.RED_CONCRETE;
				}
			}
		}

		if (chestBlock.getType() != Material.TRAPPED_CHEST) 
		{
			chestBlock.setType(Material.TRAPPED_CHEST);
			hayBlock.setType(Material.HAY_BLOCK);
			fireBlock.setType(Material.CAMPFIRE);
		}


		org.bukkit.block.Chest chest = (org.bukkit.block.Chest) chestBlock.getState();
		Inventory inv = chest.getInventory();
		inv.clear();
		ArrayList<Integer> kits = new ArrayList<Integer>();

		int chestPosition = 12;
		int chestKit;
		ItemStack item = new ItemStack(Material.DIAMOND, 8);

		for (int i = 0; i < 2; ++i) 
		{
			while (true) 
			{
				chestKit = rdm.nextInt(9);
				if (!kits.contains(chestKit)) 
				{
					kits.add(chestKit);
					break;
				}
			}

			switch (chestKit) 
			{
			case 0:
				inv.setItem(chestPosition++, item);
				break;
			case 1:
				item.setAmount(32);
				item.setType(Material.IRON_INGOT);
				inv.setItem(chestPosition++, item);
				break;
			case 2:
				item.setAmount(3);
				item.setType(Material.GOLDEN_APPLE);
				inv.setItem(chestPosition++, item);
				break;
			case 3:
				item.setAmount(10);
				item.setType(Material.EXPERIENCE_BOTTLE);
				inv.setItem(chestPosition++, item);
				break;
			case 4:
				item.setAmount(1);
				item.setType(Material.ENCHANTING_TABLE);
				inv.setItem(chestPosition++, item);
				item.setAmount(32);
				item.setType(Material.LAPIS_ORE);
				inv.setItem(chestPosition++, item);
				break;
			case 5:
				item.setAmount(3);
				item.setType(Material.GOLDEN_APPLE);
				inv.setItem(chestPosition++, item);
				break;
			case 6:
				item.setAmount(1);
				item.setType(Material.SPLASH_POTION);
				PotionMeta potionmeta = (PotionMeta) item.getItemMeta();
				potionmeta.setBasePotionData(new PotionData(PotionType.SPEED, false, false));
				item.setItemMeta(potionmeta);
				inv.setItem(chestPosition++, item);
				inv.setItem(chestPosition++, item);
				break;
			case 7:
				item.setAmount(1);
				item.setType(Material.SPLASH_POTION);
				PotionMeta potionmeta2 = (PotionMeta) item.getItemMeta();
				potionmeta2.setBasePotionData(new PotionData(PotionType.INSTANT_DAMAGE, false, false));
				item.setItemMeta(potionmeta2);
				inv.setItem(chestPosition++, item);
				break;
			case 8:
				item.setAmount(1);
				item.setType(Material.BOW);
				inv.setItem(chestPosition++, item);
				item.setAmount(32);
				item.setType(Material.ARROW);
				inv.setItem(chestPosition++, item);
				break;
			}
		}
	}

	public void TaupeAnnouncement() 
	{
		OfflinePlayer taupe;
		for (int i = 0; i < teamsManager.taupes.size(); i++) 
		{
			for (UUID uid : teamsManager.taupes.get(i)) 
			{
				taupe = Bukkit.getOfflinePlayer(uid);

				if (!taupe.isOnline()) 
					continue;

				taupe.getPlayer().sendMessage(ChatColor.RED 
						+ "-------Annonce IMPORTANTE------");
				taupe.getPlayer().sendMessage(ChatColor.GOLD 
						+ "Vous etes une taupe de votre equipe !");
				taupe.getPlayer().sendMessage(ChatColor.GOLD 
						+ "Pour parler avec les autres taupes, executez la commande /t < message>");
				taupe.getPlayer().sendMessage(ChatColor.GOLD
						+ "Si vous voulez devoiler votre vraie identite, executez la commande /reveal");
				taupe.getPlayer().sendMessage(ChatColor.GOLD 
						+ "Pour obtenir votre kit de taupe, executez la commande /claim");
				taupe.getPlayer().sendMessage(ChatColor.GOLD 
						+ "Votre but : " 
						+ ChatColor.DARK_RED
						+ "Tuer les membres de votre \"equipe\"");
				taupe.getPlayer().sendMessage(ChatColor.RED 
						+ "-------------------------------");
				
				taupe.getPlayer().sendTitle("Vous etes une taupe !", "Ne le dites a personne !", 10, 70, 20);
			}
		}
		
		taupessetup = true;
	}

	public void SupertaupeAnnouncement() 
	{
		if (!getConfig().getBoolean("options.supertaupe"))
			return;

		OfflinePlayer player;
		for (int i = 0; i < teamsManager.supertaupes.size(); i++) 
		{
			player = Bukkit.getOfflinePlayer(teamsManager.supertaupes.get(i));
			
			if (player.isOnline())
				continue;

			player.getPlayer().sendMessage(ChatColor.RED 
					+ "-------Annonce IMPORTANTE------");
			player.getPlayer().sendMessage(ChatColor.GOLD 
					+ "Vous etes la supertaupe !");
			player.getPlayer().sendMessage(ChatColor.GOLD
					+ "Si vous voulez devoiler votre vraie identite executez la commande /superreveal");
			player.getPlayer().sendMessage(ChatColor.GOLD 
					+ "Votre but : " 
					+ ChatColor.DARK_RED 
					+ "Tuer tous les autres joueurs !");
			player.getPlayer().sendMessage(ChatColor.RED 
					+ "-------------------------------");
			
			player.getPlayer().sendTitle("Vous etes la supertaupe !", "Ne le dites a personne !", 10, 70, 20);
		}
		
		supertaupessetup = true;
	}

	public void ForceReveal(boolean check) 
	{
		int revealed = 0;
		for (int i = 0; i < teamsManager.taupes.size(); ++i) 
		{
			for (UUID taupe : teamsManager.taupes.get(i)) 
			{
				if(teamsManager.showedtaupes.contains(taupe))
					continue;

				++revealed;
				teamsManager.taupesteam.get(i).addEntry(Bukkit.getOfflinePlayer(taupe).getName());
				teamsManager.showedtaupes.add(taupe);
				
				Bukkit.broadcastMessage(ChatColor.RED 
						+ Bukkit.getOfflinePlayer(taupe).getName()
						+ " a revele qu'il etait une taupe !");
			}
		}

		if(revealed > 0)
		{
			for (Player online : Bukkit.getOnlinePlayers())
			{
				online.playSound(online.getLocation(), Sound.ENTITY_GHAST_SCREAM, 10.0F, -10.0F);
			}
		}

		taupessetup = true;

		UnregisterTeam();
		UnregisterTaupeTeam();

		if (check)
			CheckVictory();
	}

	public void SuperReveal(boolean check) 
	{
		UUID uid;
		int revealed = 0;
		for (int i = 0; i < teamsManager.supertaupes.size(); ++i) 
		{
			uid = teamsManager.supertaupes.get(i);
			if (teamsManager.showedsupertaupes.contains(uid)) 
				continue;

			teamsManager.aliveTaupes.remove(uid);
			teamsManager.supertaupesteam.get(i).addEntry(Bukkit.getOfflinePlayer(uid).getName());
			teamsManager.showedsupertaupes.add(uid);
			
			Bukkit.broadcastMessage(ChatColor.DARK_RED 
					+ Bukkit.getOfflinePlayer(uid).getName()
					+ " a revele qu'il etait une supertaupe !");
		}

		if(revealed > 0)
		{
			for (Player online : Bukkit.getOnlinePlayers()) 
			{
				online.playSound(online.getLocation(), Sound.ENTITY_GHAST_SCREAM, 10.0F, -10.0F);
				online.playSound(online.getLocation(), Sound.ENTITY_GHAST_SCREAM, 10.0F, -10.0F);
			}
		}

		supertaupessetup = true;

		UnregisterTeam();
		UnregisterTaupeTeam();

		if (check)
			CheckVictory();
	}

	public void ClaimKit(Player player, boolean force) 
	{
		Random random = new Random();
		int kitnumber;
		int taupeteam = -1;

		if(!force) 
		{
			for (int i = 0; i < getConfig().getInt("options.taupesteams"); ++i) 
			{
				if (!teamsManager.taupes.get(i).contains(player.getUniqueId()))
					continue;
				
				taupeteam = i;
				break;
			}
		}
		
		if(taupeteam == -1)
			return;

		if (!teamsManager.claimedkits.containsKey(taupeteam))
			teamsManager.claimedkits.put(taupeteam, new ArrayList<Integer>());

		kitnumber = random.nextInt(8);
		if(force)
			player.sendMessage("kitnumber : "+ kitnumber);

		while (!force) 
		{
			kitnumber = random.nextInt(8);

			if (teamsManager.claimedkits.get(taupeteam).contains(kitnumber)) 
				continue;

			teamsManager.claimedkits.get(taupeteam).add(kitnumber);
			break;
		}

		ItemStack kit = new ItemStack(Material.GOLDEN_APPLE, 4);
		Location loc = player.getLocation();
		loc.add(player.getEyeLocation().getDirection().normalize());

		switch (kitnumber) 
		{
		case (0):		
			kit.setAmount(5);
			kit.setType(Material.TNT);
			player.getWorld().dropItemNaturally(loc, kit);
			kit.setAmount(1);
			kit.setType(Material.FLINT_AND_STEEL);
			player.getWorld().dropItemNaturally(loc, kit);
			
			player.sendMessage(ChatColor.RED + "Vous recevez le kit terroriste ! Ca va peter !");
			break;
		case (1):
			kit.setAmount(5);
			kit.setType(Material.BLAZE_SPAWN_EGG);
			player.getWorld().dropItemNaturally(loc, kit);
			
			player.sendMessage(ChatColor.RED + "Vous recevez le kit invocateur ! Ne sont-ils pas mignons ?");
			break;
		case (2):
			kit.setAmount(32);
			kit.setType(Material.ARROW);
			player.getWorld().dropItemNaturally(loc, kit);
			kit.setAmount(1);
			kit.setType(Material.BOW);
			kit.addEnchantment(Enchantment.ARROW_DAMAGE, 2);
			player.getWorld().dropItemNaturally(loc, kit);
			
			player.sendMessage(ChatColor.RED + "Vous recevez le kit Robin des bois ! Volez aux riches pour donner au pauvre (vous) !");
			break;
		case (3):
			kit.setAmount(1);
			kit.setType(Material.SPLASH_POTION);
			PotionMeta meta=(PotionMeta) kit.getItemMeta();
			meta.setBasePotionData(new PotionData(PotionType.INVISIBILITY, true, false));
			kit.setItemMeta(meta);
			player.getWorld().dropItemNaturally(loc, kit);
			meta.setBasePotionData(new PotionData(PotionType.SPEED, true, false));
			kit.setItemMeta(meta);
			player.getWorld().dropItemNaturally(loc, kit);
			meta.setBasePotionData(new PotionData(PotionType.INSTANT_DAMAGE, false, true));
			kit.setItemMeta(meta);
			player.getWorld().dropItemNaturally(loc, kit);
			meta.setBasePotionData(new PotionData(PotionType.INSTANT_HEAL, false, true));
			kit.setItemMeta(meta);
			player.getWorld().dropItemNaturally(loc, kit);

			player.sendMessage(ChatColor.RED + "Vous recevez le kit alchimiste ! Un p'tit verre ?");
			break;
		case (4):
			kit.setAmount(1);
			kit.setType(Material.DIAMOND_PICKAXE);
			kit.addEnchantment(Enchantment.DIG_SPEED, 3);
			kit.addEnchantment(Enchantment.DURABILITY, 3);
			kit.addEnchantment(Enchantment.LOOT_BONUS_BLOCKS, 3);
			player.getWorld().dropItemNaturally(loc, kit);
			
			player.sendMessage(ChatColor.RED + "Vous recevez le kit taupe-taupe ! Creusez plus vite que la lumiere!");
			break;
		case (5):
			kit.setAmount(1);
			kit.setType(Material.DIAMOND_CHESTPLATE);
			kit.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 3);
			player.getWorld().dropItemNaturally(loc, kit);
			
			player.sendMessage(ChatColor.RED + "Vous recevez le kit tank ! Et t'es combien dans ton armure ?");
			break;
		case (6):
			kit.setAmount(8);
			kit.setType(Material.ENDER_PEARL);
			player.getWorld().dropItemNaturally(loc, kit);
			kit.setAmount(1);
			kit.setType(Material.DIAMOND_BOOTS);
			kit.addEnchantment(Enchantment.PROTECTION_FALL, 4);
			kit.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
			player.getWorld().dropItemNaturally(loc, kit);
			
			player.sendMessage(ChatColor.RED + "Vous recevez le kit teleport ! La, vous me voyez. La vous me voyez plus.");
			break;
		case(7):
			kit.setAmount(1);
			kit.setType(Material.DIAMOND_SWORD);
			kit.addEnchantment(Enchantment.DAMAGE_ALL, 3);
			player.getWorld().dropItemNaturally(loc, kit);
			
			player.sendMessage(ChatColor.RED + "Vous recevez le kit bourreau ! Ca va couper cherie !");
			break;
		default:
			player.getWorld().dropItemNaturally(loc, kit);
			break;
		}

		if (!force) 
			teamsManager.claimedtaupes.add(player.getUniqueId());
	}

	// COMMANDES 
	// ---------
	
	public void TaupeSendMessage(Player player, String[] args)
	{
		if(!taupessetup)
			return;
		
		String message;
		for (int i = 0; i < getConfig().getInt("options.taupesteams"); ++i) 
		{
			boolean showMessage = 
					(teamsManager.aliveTaupes.contains(player.getUniqueId())
							&& teamsManager.taupes.get(i).contains(player.getUniqueId()))
					|| (teamsManager.aliveSupertaupes.contains(player.getUniqueId())
							&& teamsManager.supertaupes.get(i) == player.getUniqueId()
							&& !teamsManager.showedsupertaupes.contains(player.getUniqueId()));

			if (!showMessage) 
				continue;

			for (UUID taupe : teamsManager.taupes.get(i)) 
			{
				if (teamsManager.showedsupertaupes.contains(taupe) || !teamsManager.aliveTaupes.contains(taupe))
					continue;

				message = StringUtils.join(args, ' ', 0, args.length);

				String content = ChatColor.GOLD 
						+ "(Taupes#" + i + ") " 
						+ ChatColor.RED 
						+ "<" + player.getName();

				if (!customScoreboardManager.s.getEntryTeam(Bukkit.getOfflinePlayer(taupe).getName()).getName().contains("aupe")) 
					content += "(" + player.getScoreboard().getEntryTeam(player.getName()).getName() + ")";

				content += "> " + ChatColor.WHITE + message;

				Bukkit.getPlayer(taupe).sendMessage(content);
			}
			return;
		}
		
		player.sendMessage(ChatColor.RED + "Vous n'etes pas une taupe !");
	}

	public void TaupeReveal(Player player)
	{
		if(!taupessetup)
			return;

		for (int i = 0; i < getConfig().getInt("options.taupesteams"); ++i) 
		{
			if (!teamsManager.taupes.get(i).contains(player.getUniqueId())) 
				continue;

			if (teamsManager.showedtaupes.contains(player.getUniqueId())) 
				player.sendMessage(ChatColor.RED 
						+ "Vous vous etes deja revele !");
			else 
			{
				PlayerInventory inventory = player.getInventory();
				inventory.addItem(new ItemStack[] { new ItemStack(Material.GOLDEN_APPLE, 1) });

				teamsManager.taupesteam.get(i).addEntry(player.getName());
				teamsManager.showedtaupes.add(player.getUniqueId());
				
				for (Player online : Bukkit.getOnlinePlayers())
					online.playSound(online.getLocation(), Sound.ENTITY_GHAST_SCREAM, 10.0F, -10.0F);
				
				Bukkit.broadcastMessage(ChatColor.RED 
						+ player.getName() 
						+ " a revele qu'il etait une taupe !");

				UnregisterTeam();
				UnregisterTaupeTeam();
				CheckVictory();
			}
			
			return;
		}

		player.sendMessage(ChatColor.RED + "Vous n'etes pas une taupe !");
		return;
	}
	
	public void SupertaupeReveal(Player player)
	{
		if(!supertaupessetup)
			return;
		
		if (teamsManager.supertaupes.containsValue(player.getUniqueId())) 
		{
			int key = -1;

			for (int i = 0; i < getConfig().getInt("options.taupesteams"); ++i)
			{
				if (teamsManager.supertaupes.get(i) != player.getUniqueId()) 
					continue;

				key = i;
				break;
			}
			
			if(key == -1)
				return;

			if (teamsManager.showedsupertaupes.contains(player.getUniqueId()))
				player.sendMessage(ChatColor.RED + "Vous vous etes deja revele !");
			else if (!teamsManager.showedtaupes.contains(player.getUniqueId())) 
				player.sendMessage(ChatColor.RED + "Vous devez d'abord vous reveler en tant que taupe !");
			else 
			{
				teamsManager.aliveTaupes.remove(player.getUniqueId());
				teamsManager.supertaupesteam.get(key).addEntry(player.getName());
				teamsManager.showedsupertaupes.add(player.getUniqueId());

				PlayerInventory inventory = player.getInventory();
				inventory.addItem(new ItemStack[] { new ItemStack(Material.GOLDEN_APPLE, 2) });


				for (Player online : Bukkit.getOnlinePlayers()) 
				{
					online.playSound(online.getLocation(), Sound.ENTITY_GHAST_SCREAM, 10.0F, -10.0F);
					online.playSound(online.getLocation(), Sound.ENTITY_GHAST_SCREAM, 10.0F, -10.0F);
				}
				
				Bukkit.broadcastMessage(ChatColor.DARK_RED 
						+ player.getName() 
						+ " a revele qu'il etait une supertaupe !");

				UnregisterTeam();
				UnregisterTaupeTeam();
				CheckVictory();
			}
			return;
		}
		player.sendMessage(ChatColor.RED + "Vous n'etes pas la supertaupe !");
		return;
	}
	
	public void TaupeClaimKit(Player player)
	{
		if(!taupessetup)
			return;
		
		for (int i = 0; i < this.getConfig().getInt("options.taupesteams"); ++i) 
		{
			if(!teamsManager.taupes.get(i).contains(player.getUniqueId()) && teamsManager.supertaupes.get(i) != player.getUniqueId())
				continue;

			if (!teamsManager.claimedtaupes.contains(player.getUniqueId()))
				ClaimKit(player, false);
			else
				player.sendMessage(ChatColor.RED + "Vous avez deja claim votre kit de taupe !");

			return;
		}

		player.sendMessage(ChatColor.RED + "Vous n'etes pas une taupe !");
		return;
	}
	
	public void SetJob(Player player, String job) 
	{
		if (!gameStarted || gameEnd) 
			return;
	
		if (this.playersWithJob.contains(player.getUniqueId()))
		{
			player.sendMessage(ChatColor.DARK_RED + "Vous avez deja un metier !");
			return;
		}
		else if (pvp) 
		{
			player.sendMessage(ChatColor.DARK_RED + "Trop tard pour choisir un metier");
			return;
		} 
		else if (!getConfig().getBoolean("options.job." + job)) 
		{
			player.sendMessage(ChatColor.DARK_RED + "Metier desactive");
			return;
		}
		else
		{
			player.sendMessage(ChatColor.GREEN + "Vous avez choisi le metier de " + job);
			playersWithJob.add(player.getUniqueId());

			Location ploc = player.getLocation();
			World world = player.getWorld();

			ItemStack givenItem;

			switch(job) 
			{
			case "bucheron":
				givenItem = new ItemStack(Material.STONE_AXE, 1);
				givenItem.addEnchantment(Enchantment.DURABILITY, 1);
				givenItem.addEnchantment(Enchantment.DIG_SPEED, 5);
				givenItem.addEnchantment(Enchantment.VANISHING_CURSE, 1);

				world.dropItem(ploc, givenItem);
				return;
			case "mineur":
				givenItem = new ItemStack(Material.STONE_PICKAXE, 1);
				givenItem.addEnchantment(Enchantment.DURABILITY, 1);
				givenItem.addEnchantment(Enchantment.DIG_SPEED, 3);
				givenItem.addEnchantment(Enchantment.VANISHING_CURSE, 1);

				world.dropItem(ploc, givenItem);

				givenItem = new ItemStack(Material.GOLDEN_SHOVEL, 1);
				givenItem.addEnchantment(Enchantment.DURABILITY, 1);
				givenItem.addEnchantment(Enchantment.DIG_SPEED, 3);
				givenItem.addEnchantment(Enchantment.VANISHING_CURSE, 1);

				world.dropItem(ploc, givenItem);

				givenItem = new ItemStack(Material.TORCH, 32);

				world.dropItem(ploc, givenItem);

				return;
			case "chasseur":
				givenItem = new ItemStack(Material.GOLDEN_SWORD, 1);
				givenItem.addEnchantment(Enchantment.LOOT_BONUS_MOBS, 2);
				givenItem.addEnchantment(Enchantment.DAMAGE_ALL, 5);
				givenItem.addEnchantment(Enchantment.FIRE_ASPECT, 2);
				givenItem.addEnchantment(Enchantment.VANISHING_CURSE, 1);
				givenItem.addEnchantment(Enchantment.DURABILITY, 1);

				world.dropItem(ploc, givenItem);

				return;
			}
		}
	}
}
