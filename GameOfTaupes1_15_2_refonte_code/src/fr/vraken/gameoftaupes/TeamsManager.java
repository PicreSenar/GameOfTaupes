package fr.vraken.gameoftaupes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class TeamsManager 
{
	static GameOfTaupes plugin;

	private FileConfiguration teamf;
	private Scoreboard scoreboard;
	
	private int taupesTeams;
	HashMap<Integer, Integer> taupesperteam = new HashMap<Integer, Integer>();
	HashMap<Integer, Team> taupesteam = new HashMap<Integer, Team>();
	HashMap<Integer, ArrayList<UUID>> taupes = new HashMap<Integer, ArrayList<UUID>>();
	HashMap<Integer, Boolean> isTaupesTeamDead = new HashMap<Integer, Boolean>();
	ArrayList<UUID> aliveTaupes = new ArrayList<UUID>();
	ArrayList<UUID> showedtaupes = new ArrayList<UUID>();
	ArrayList<UUID> claimedtaupes = new ArrayList<UUID>();
	HashMap<Integer, ArrayList<Integer>> claimedkits = new HashMap<Integer, ArrayList<Integer>>();

	private boolean supertaupeActive;
	HashMap<Integer, Team> supertaupesteam = new HashMap<Integer, Team>();
	HashMap<Integer, UUID> supertaupes = new HashMap<Integer, UUID>();
	HashMap<Integer, Boolean> isSupertaupeDead = new HashMap<Integer, Boolean>();
	ArrayList<UUID> aliveSupertaupes = new ArrayList<UUID>();
	ArrayList<UUID> showedsupertaupes = new ArrayList<UUID>();
	
	private String teamAnnounceString = "L'equipe ";
	private String teamChoiceString = "son equipe";

	Location l1;
	Location l2;
	Location l3;
	Location l4;
	Location l5;
	Location l6;
	Location meetupl1;
	Location meetupl2;
	Location meetupl3;
	Location meetupl4;
	Location meetupl5;
	Location meetupl6;
	Team rose;
	Team jaune;
	Team violette;
	Team cyan;
	Team verte;
	Team grise;
	
	public TeamsManager(GameOfTaupes gameoftaupes, int t, boolean st)
	{
		plugin = gameoftaupes;
		scoreboard = plugin.customScoreboardManager.s;
		teamf = plugin.teamf;
		taupesTeams = t;
		supertaupeActive = st;
	}
	
	public void UnregisterAll()
	{
		for (Team team : scoreboard.getTeams())
		{
			team.unregister();
		}
	}

	public void RegisterAll()
	{
		rose = scoreboard.registerNewTeam(teamf.getString("rose.name"));
		rose.setPrefix(ChatColor.LIGHT_PURPLE.toString());
		rose.setSuffix(ChatColor.WHITE.toString());
		rose.setColor(ChatColor.LIGHT_PURPLE);
		
		cyan = scoreboard.registerNewTeam(teamf.getString("cyan.name"));
		cyan.setPrefix(ChatColor.DARK_AQUA.toString());
		cyan.setSuffix(ChatColor.WHITE.toString());
		cyan.setColor(ChatColor.DARK_AQUA);
		
		jaune = scoreboard.registerNewTeam(teamf.getString("jaune.name"));
		jaune.setPrefix(ChatColor.YELLOW.toString());
		jaune.setSuffix(ChatColor.WHITE.toString());
		jaune.setColor(ChatColor.YELLOW);
		
		violette = scoreboard.registerNewTeam(teamf.getString("violette.name"));
		violette.setPrefix(ChatColor.DARK_PURPLE.toString());
		violette.setSuffix(ChatColor.WHITE.toString());
		violette.setColor(ChatColor.DARK_PURPLE);
		
		verte = scoreboard.registerNewTeam(teamf.getString("verte.name"));
		verte.setPrefix(ChatColor.GREEN.toString());
		verte.setSuffix(ChatColor.WHITE.toString());
		verte.setColor(ChatColor.GREEN);
		
		grise = scoreboard.registerNewTeam(teamf.getString("grise.name"));
		grise.setPrefix(ChatColor.GRAY.toString());
		grise.setSuffix(ChatColor.WHITE.toString());
		grise.setColor(ChatColor.GRAY);

		for (int i = 0; i < taupesTeams; ++i) 
		{
			taupesteam.put(i, scoreboard.registerNewTeam("Taupes#" + i));
			taupesteam.get(i).setPrefix(ChatColor.RED.toString());
			taupesteam.get(i).setSuffix(ChatColor.WHITE.toString());
			taupesteam.get(i).setColor(ChatColor.RED);

			supertaupesteam.put(i, scoreboard.registerNewTeam("SuperTaupe#" + i));
			supertaupesteam.get(i).setPrefix(ChatColor.DARK_RED.toString());
			supertaupesteam.get(i).setSuffix(ChatColor.WHITE.toString());
			supertaupesteam.get(i).setColor(ChatColor.DARK_RED);

			taupes.put(i, new ArrayList<UUID>());
			supertaupes.put(i, null);
			taupesperteam.put(i, 0);
			isTaupesTeamDead.put(i, false);
			isSupertaupeDead.put(i, false);
		}
	}
	
	public void SetSpawnLocations(String worldName, String worldLobbyName, boolean meetupteamtp) 
	{
		l1 = Utilities.GetLocationFromFile(teamf, worldName, "rose.");
		l2 = Utilities.GetLocationFromFile(teamf, worldName, "cyan.");
		l3 = Utilities.GetLocationFromFile(teamf, worldName, "jaune.");
		l4 = Utilities.GetLocationFromFile(teamf, worldName, "violette.");
		l5 = Utilities.GetLocationFromFile(teamf, worldName, "verte.");
		l6 = Utilities.GetLocationFromFile(teamf, worldName, "grise.");

		if (!meetupteamtp) 
			return;

		meetupl1 = Utilities.GetLocationFromFile(teamf, worldLobbyName, "rose.meetup");
		meetupl2 = Utilities.GetLocationFromFile(teamf, worldLobbyName, "cyan.meetup");
		meetupl3 = Utilities.GetLocationFromFile(teamf, worldLobbyName, "jaune.meetup");
		meetupl4 = Utilities.GetLocationFromFile(teamf, worldLobbyName, "violette.meetup");
		meetupl5 = Utilities.GetLocationFromFile(teamf, worldLobbyName, "verte.meetup");
		meetupl6 = Utilities.GetLocationFromFile(teamf, worldLobbyName, "grise.meetup");
	}

	public void ClearTeams() 
	{
		for (Team teams : scoreboard.getTeams()) 
		{
			if(teams.getName().contains("Taupes"))
				continue;

			if (teams.getName().contains("SuperTaupe") && !supertaupeActive)
				teams.unregister();
			else if (teams.getSize() == 0 && !teams.getName().contains("SuperTaupe")) 
				teams.unregister();
		}
	}
	
	public void SetTaupes(int taupespteam, int taupesteams) 
	{
		int nbTeams = scoreboard.getTeams().size() - taupesteams;
		
		if (supertaupeActive)
			nbTeams -= taupesteams;

		int taupesTot = nbTeams * taupespteam;
		int taupesMin = taupesTot / taupesteams;
		int taupesLeft = taupesTot % taupesteams;

		for (int i = 0; i < taupespteam; ++i) 
		{
			if (taupesLeft > 0) 
			{
				taupesperteam.put(i, taupesMin);
				taupesLeft--;
				continue;
			}

			taupesperteam.put(i, taupesMin);
		}
		
		
		ArrayList<UUID> tmpplayers = new ArrayList<UUID>();
		ArrayList<UUID> tmptaupes = new ArrayList<UUID>();
		ArrayList<Integer> tmpteams = new ArrayList<Integer>();
		int psize;
		int tsize;
		UUID p;
		Random random = new Random(System.currentTimeMillis());
		
		for (Team team : scoreboard.getTeams()) 
		{
			if (team.getEntries().size() == 0) 
				continue;

			tmpplayers.clear();
			tmptaupes.clear();
			tmpteams.clear();

			for (String playerName : team.getEntries()) 
			{
				tmpplayers.add(Bukkit.getPlayer(playerName).getUniqueId());
			}

			for (int i = 0; i < taupespteam; ++i) 
			{
				while (true) 
				{
					psize = random.nextInt(tmpplayers.size());
					p = tmpplayers.get(psize);
					
					if (!tmptaupes.contains(p))
						break;
				}
				
				while (true) 
				{
					tsize = random.nextInt(taupesteams);
					
					if (!tmpteams.contains(tsize)
							&& taupes.get(tsize).size() < this.taupesperteam.get(tsize)) 
						break;
				}

				tmpteams.add(tsize);
				tmptaupes.add(p);
				
				if (!taupes.containsKey(tsize))
					taupes.put(tsize, new ArrayList<UUID>());

				taupes.get(tsize).add(p);
				aliveTaupes.add(p);
			}
		}
	}

	public void SetSuperTaupe() 
	{
		if (!supertaupeActive)
			return;

		Random random = new Random(System.currentTimeMillis());

		for (int i = 0; i < getConfig().getInt("options.taupesteams"); ++i) 
		{
			int taupeIndex = random.nextInt(taupes.get(i).size());
			UUID spId = taupes.get(i).get(taupeIndex);
			supertaupes.put(i, spId);
			aliveSupertaupes.add(spId);
		}
	}
	
	public void UnregisterTeam() 
	{
		for (Team teams : scoreboard.getTeams()) 
		{
			if(teams.getSize() > 0
					|| teams.getName().contains("Taupes")
					|| teams.getName().contains("SuperTaupe"))
				continue;

			Bukkit.broadcastMessage(
					teamAnnounceString 
					+ teams.getPrefix() + teams.getName()
					+ ChatColor.RESET + " a ete eliminee ! ");
			teams.unregister();
		}
	}

	public void UnregisterTaupeTeam() 
	{
		for (int i = 0; i < plugin.getConfig().getInt("options.taupesteams"); ++i) 
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
					&& supertaupeActive) 
			{
				isSupertaupeDead.put(i, true);
				Bukkit.broadcastMessage(ChatColor.DARK_RED + "La supertaupe #" + i + " a ete eliminee ! ");
				supertaupesteam.get(i).unregister();
			}
		}
	}
}
