package fr.vraken.gameoftaupes;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
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
import org.bukkit.entity.SplashPotion;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;



public class GameOfTaupes extends JavaPlugin 
{
	// Files
	FilesManager filesManager;
	FileConfiguration teamf;
	FileConfiguration deathf;
	FileConfiguration minigamef;

	CustomScoreboardManager customScoreboardManager;
	TasksManager tasksManager;;
	TeamsManager teamsManager;


	// Players
	ArrayList<UUID> playersInTeam = new ArrayList<UUID>();
	ArrayList<UUID> playersAlive = new ArrayList<UUID>();
	ArrayList<UUID> playersInLobby = new ArrayList<UUID>();
	ArrayList<UUID> playersSpec = new ArrayList<UUID>();
	ArrayList<UUID> playersWithJob = new ArrayList<UUID>();

	// Taupes
	boolean taupessetup;
	boolean supertaupessetup;
	

	// Chest
	static ArrayList<ItemStack> tmpKits = new ArrayList<ItemStack>();
	boolean finalZone = false;

	
	// Scoreboard
	int episode;
	boolean retract = false;
	boolean finalretract = false;
	Objective obj;
	Objective vie;
	int gameState;
	String objMinute;
	String objSecond;
	String objTxt;
	String countdownObj;
	boolean hasChangedGS;
	int tmpPlayers;
	int tmpTeams;
	int tmpBorder;
	NumberFormat objFormatter;
	int height = 10;
	int minuteTot = 0;


	// Gamestates
	boolean meetUp = false;
	boolean gameStarted = false;
	boolean gameEnd = false;
	boolean pvp = false;
	Location lobbyLocation;
	Location meetupLocation;
	Location respawnLocation;
	
	
	// Announcements
	int restractEpisode;
	String teamAnnounceString = "L'equipe ";
	String teamChoiceString = "son equipe";

	public void onEnable() 
	{
		System.out.println("+-------------VrakenGameOfTaupes--------------+");
		System.out.println("|           Plugin cree par Vraken            |");
		System.out.println("+---------------------------------------------+");

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
		minigamef = filesManager.getMinigameConfig();

		getConfig().options().copyDefaults(true);
		teamf.options().copyDefaults(true);
		deathf.options().copyDefaults(true);
		saveConfig();

		customScoreboardManager = new CustomScoreboardManager(this);
		tasksManager = new TasksManager(this);
		teamsManager = new TeamsManager(this, 
				getConfig().getInt("options.taupesteams"), 
				getConfig().getBoolean("options.supertaupe"));

		Bukkit.createWorld(new WorldCreator(getConfig().getString("lobby.world")));
		Bukkit.createWorld(new WorldCreator(getConfig().getString("world")));

		Bukkit.getPluginManager().registerEvents(new EventsClass(this), this);

		teamsManager.UnregisterAll();
		teamsManager.RegisterAll();
		teamsManager.SetSpawnLocations(
				getConfig().get("world").toString(), 
				getConfig().get("lobby.world").toString(),
				getConfig().getBoolean("options.meetupteamtp"));

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

		String world = getConfig().getString("world");
		Boolean istimecycle = getConfig().getBoolean("options.timecycle");
		Bukkit.getWorld(world).setGameRuleValue("doDaylightCycle", Boolean.valueOf(istimecycle).toString());
		Bukkit.getWorld(world).setGameRuleValue("announceAdvancements",Boolean.valueOf(false).toString());

		Bukkit.getWorld(getConfig().getString("world")).setWeatherDuration(50000);
		Bukkit.getWorld(getConfig().getString("world")).setStorm(false);
		Bukkit.getWorld(getConfig().getString("world")).setThundering(false);
		Bukkit.getWorld(getConfig().get("world").toString()).setTime(5000L);

		teamsManager.ClearTeams();

		teamsManager.SetTaupes(
				getConfig().getInt("options.taupesperteam"), 
				getConfig().getInt("options.taupesteams"));
		teamsManager.SetSuperTaupe();


		int nbTeams = s.getTeams().size() - getConfig().getInt("options.taupesteams");
		
		if (getConfig().getBoolean("options.supertaupe"))
			nbTeams -= getConfig().getInt("options.taupesteams");

		int taupesTot = nbTeams * getConfig().getInt("options.taupesperteam");
		int taupesMin = taupesTot / getConfig().getInt("options.taupesteams");
		int taupesLeft = taupesTot % getConfig().getInt("options.taupesteams");

		for (int i = 0; i < getConfig().getInt("options.taupesperteam"); i++) {
			if (taupesLeft > 0) {
				taupesperteam.put(i, taupesMin);
				taupesLeft--;
				continue;
			}

			taupesperteam.put(i, taupesMin);
		}

		episode += 1;

		// SCOREBOARD INITIALIZATION
		// -------------------------
		objFormatter = new DecimalFormat("00");

		// TAUPES SETTING
		// --------------
		setTaupes();

		// SUPERTAUPE SETTING
		// ------------------
		setSuperTaupe();

		// CLEARING INVENTORY AND STATUS OF EVERY PLAYER THEN TELEPORTING HIM TO HIS
		// SPAWN
		// -------------------------------------------------------------------------------
		ClearPlayers();


		getServer().getWorld(getConfig().getString("world")).getWorldBorder()
		.setSize(getConfig().getDouble("worldborder.size"));

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
			meetUp = true;
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
			String teamName = s.getEntryTeam(p.getName()).getName();
			
			if (teamName.equals(teamf.getString("rose.name")))
				tp = l1;
			else if (teamName.equals(teamf.getString("jaune.name")))
				tp = l2;
			else if (teamName.equals(teamf.getString("violette.name")))
				tp = l3;
			else if (teamName.equals(teamf.getString("cyan.name")))
				tp = l4;
			else if (teamName.equals(teamf.getString("verte.name")))
				tp = l5;
			else
				tp = l6;
			
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
		for (Team teams : s.getTeams()) 
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
		for (Team team : s.getTeams()) 
		{
			if(team.getName().contains("aupe"))
				continue;

			teamsAlive++;
			if (teamsAlive > 1)
				return;

			lastTeam = team;
		}

		for (int i = 0; i < taupes.size(); ++i) 
		{
			for (UUID uid : taupes.get(i)) 
			{
				if(!playersAlive.contains(uid) || supertaupes.get(i) == uid)
					continue;

				teamsAlive++;
				if (teamsAlive > 1)
					return;

				lastTeam = taupesteam.get(i);
				break;
			}

			if (!aliveSupertaupes.contains(supertaupes.get(i)))
				continue;	

			teamsAlive++;
			if (teamsAlive > 1)
				return;

			lastTeam = supertaupesteam.get(i);
		}

		if(teamsAlive > 1)
			return;

		ForceReveal(false);
		SuperReveal(false);
		announceWinner(lastTeam);
	}

	public void announceWinner(Team team) 
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
		for (Team teams : s.getTeams()) 
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
			UUID supertaupe = supertaupes.get(i);

			boolean dead = true;
			int showed = 0;
			for (UUID uid : taupes.get(i)) 
			{
				if (aliveTaupes.contains(uid))
					dead = false;
				if (showedtaupes.contains(uid))
					showed++;
			}

			if (dead 
					&& !isTaupesTeamDead.get(i)
					&& taupes.get(i).size() == showed) 
			{
				isTaupesTeamDead.put(i, true);
				Bukkit.broadcastMessage(ChatColor.RED + "L'equipe des taupes #" + i + " a ete eliminee ! ");
				taupesteam.get(i).unregister();
			}
			
			if (!isSupertaupeDead.get(i) 
					&& showedsupertaupes.contains(supertaupe)
					&& !aliveSupertaupes.contains(supertaupe)
					&& getConfig().getBoolean("options.supertaupe")) 
			{
				isSupertaupeDead.put(i, true);
				Bukkit.broadcastMessage(ChatColor.DARK_RED + "La supertaupe #" + i + " a ete eliminee ! ");
				supertaupesteam.get(i).unregister();
			}
		}
	}

	public void EnablePvp()
	{
		EventsClass.pvp = true;
		pvp = true;
		Bukkit.broadcastMessage(ChatColor.RED + "Le pvp est maintenant actif !");
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
		Bukkit.broadcastMessage(ChatColor.DARK_GREEN
				+ "Un tresor est apparu ! Allez vite le chercher avant que vos adversaires ne s'en emparent ! ");
		//		try {
		//			Bukkit.getPlayer("Spec")
		//					.performCommand("dmarker add chest icon:chest x:" + GameOfTaupes.this.chestLocation.getX() + " y:"
		//							+ GameOfTaupes.this.chestLocation.getY() + " z:" + GameOfTaupes.this.chestLocation.getZ()
		//							+ " world:" + GameOfTaupes.this.chestLocation.getWorld().getName());
		//		} catch (Exception ex) {
		//		}

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

		Boolean inWater = fireBlock.getType() == Material.WATER;

		if(inWater) 
		{

			Material buoyColor = Material.RED_CONCRETE;

			Location buoyLocation= new Location(Bukkit.getWorld(getConfig().get("world").toString()), x, y, z);

			for (Integer i=-1;i<=1;i++) {
				for (Integer j=-1;j<=1;j++) {

					buoyLocation = new Location(Bukkit.getWorld(getConfig().get("world").toString()), x+i, y, z+j);	
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
		for (int i = 0; i < GameOfTaupes.this.taupes.size(); i++) 
		{
			for (UUID uid : GameOfTaupes.this.taupes.get(i)) 
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
		for (int i = 0; i < GameOfTaupes.this.supertaupes.size(); i++) 
		{
			player = Bukkit.getOfflinePlayer(GameOfTaupes.this.supertaupes.get(i));
			
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
		for (int i = 0; i < taupes.size(); ++i) 
		{
			for (UUID taupe : taupes.get(i)) 
			{
				if(showedtaupes.contains(taupe))
					continue;

				++revealed;
				taupesteam.get(i).addEntry(Bukkit.getOfflinePlayer(taupe).getName());
				showedtaupes.add(taupe);
				
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
		for (int i = 0; i < supertaupes.size(); ++i) 
		{
			uid = supertaupes.get(i);
			if (showedsupertaupes.contains(uid)) 
				continue;

			aliveTaupes.remove(uid);
			supertaupesteam.get(i).addEntry(Bukkit.getOfflinePlayer(uid).getName());
			showedsupertaupes.add(uid);
			
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
				if (!taupes.get(i).contains(player.getUniqueId()))
					continue;
				
				taupeteam = i;
				break;
			}
		}
		
		if(taupeteam == -1)
			return;

		if (!claimedkits.containsKey(taupeteam))
			claimedkits.put(taupeteam, new ArrayList<Integer>());

		kitnumber = random.nextInt(8);
		if(force)
			player.sendMessage("kitnumber : "+ kitnumber);

		while (!force) 
		{
			kitnumber = random.nextInt(8);

			if (claimedkits.get(taupeteam).contains(kitnumber)) 
				continue;

			claimedkits.get(taupeteam).add(kitnumber);
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
			this.claimedtaupes.add(player.getUniqueId());
	}

	//COMMANDES 
	
	public void TaupeSendMessage(Player player, String[] args)
	{
		if(!taupessetup)
			return;
		
		String message;
		for (int i = 0; i < getConfig().getInt("options.taupesteams"); ++i) 
		{
			boolean showMessage = 
					(aliveTaupes.contains(player.getUniqueId())
							&& taupes.get(i).contains(player.getUniqueId()))
					|| (aliveSupertaupes.contains(player.getUniqueId())
							&& supertaupes.get(i) == player.getUniqueId()
							&& !showedsupertaupes.contains(player.getUniqueId()));

			if (!showMessage) 
				continue;

			for (UUID taupe : taupes.get(i)) 
			{
				if (showedsupertaupes.contains(taupe) || !aliveTaupes.contains(taupe))
					continue;

				message = StringUtils.join(args, ' ', 0, args.length);

				String content = ChatColor.GOLD 
						+ "(Taupes#" + i + ") " 
						+ ChatColor.RED 
						+ "<" + player.getName();

				if (!s.getEntryTeam(Bukkit.getOfflinePlayer(taupe).getName()).getName().contains("aupe")) 
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
			if (!taupes.get(i).contains(player.getUniqueId())) 
				continue;

			if (this.showedtaupes.contains(player.getUniqueId())) 
				player.sendMessage(ChatColor.RED 
						+ "Vous vous etes deja revele !");
			else 
			{
				PlayerInventory inventory = player.getInventory();
				inventory.addItem(new ItemStack[] { new ItemStack(Material.GOLDEN_APPLE, 1) });

				this.taupesteam.get(i).addEntry(player.getName());
				this.showedtaupes.add(player.getUniqueId());
				
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
		
		if (this.supertaupes.containsValue(player.getUniqueId())) 
		{
			int key = -1;

			for (int i = 0; i < getConfig().getInt("options.taupesteams"); ++i)
			{
				if (supertaupes.get(i) != player.getUniqueId()) 
					continue;

				key = i;
				break;
			}
			
			if(key == -1)
				return;

			if (showedsupertaupes.contains(player.getUniqueId()))
				player.sendMessage(ChatColor.RED + "Vous vous etes deja revele !");
			else if (!showedtaupes.contains(player.getUniqueId())) 
				player.sendMessage(ChatColor.RED + "Vous devez d'abord vous reveler en tant que taupe !");
			else 
			{
				aliveTaupes.remove(player.getUniqueId());
				supertaupesteam.get(key).addEntry(player.getName());
				showedsupertaupes.add(player.getUniqueId());

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
			if(!taupes.get(i).contains(player.getUniqueId()) && supertaupes.get(i) != player.getUniqueId())
				continue;

			if (!claimedtaupes.contains(player.getUniqueId()))
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
		else if (this.pvp) 
		{
			player.sendMessage(ChatColor.DARK_RED+"Trop tard pour choisir un metier");
			return;
		} 
		else if (!getConfig().getBoolean("options.job." + job)) 
		{
			player.sendMessage(ChatColor.DARK_RED+"Metier desactive");
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
