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
	GameOfTaupes plugin;

	private FileConfiguration teamf;
	private Scoreboard scoreboard;
	
	private int taupesTeams;
	public HashMap<Integer, Integer> taupesperteam = new HashMap<Integer, Integer>();
	public HashMap<Integer, Team> taupesteam = new HashMap<Integer, Team>();
	public HashMap<Integer, ArrayList<UUID>> taupes = new HashMap<Integer, ArrayList<UUID>>();
	public HashMap<Integer, Boolean> isTaupesTeamDead = new HashMap<Integer, Boolean>();
	public ArrayList<UUID> aliveTaupes = new ArrayList<UUID>();
	public ArrayList<UUID> showedtaupes = new ArrayList<UUID>();
	public ArrayList<UUID> claimedtaupes = new ArrayList<UUID>();
	public HashMap<Integer, ArrayList<Integer>> claimedkits = new HashMap<Integer, ArrayList<Integer>>();

	private boolean supertaupeActive;
	public HashMap<Integer, Team> supertaupesteam = new HashMap<Integer, Team>();
	public HashMap<Integer, UUID> supertaupes = new HashMap<Integer, UUID>();
	public HashMap<Integer, Boolean> isSupertaupeDead = new HashMap<Integer, Boolean>();
	public ArrayList<UUID> aliveSupertaupes = new ArrayList<UUID>();
	public ArrayList<UUID> showedsupertaupes = new ArrayList<UUID>();
	
	public String teamAnnounceString = "L'equipe ";
	public String teamChoiceString = "son equipe";

	public Location l1;
	public Location l2;
	public Location l3;
	public Location l4;
	public Location l5;
	public Location l6;
	public Location meetupl1;
	public Location meetupl2;
	public Location meetupl3;
	public Location meetupl4;
	public Location meetupl5;
	public Location meetupl6;
	public Team blue;
	public Team yellow;
	public Team dark_purple;
	public Team dark_aqua;
	public Team dark_green;
	public Team dark_gray;
	
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
		blue = scoreboard.registerNewTeam(teamf.getString("blue.name"));
		blue.setPrefix(ChatColor.BLUE.toString());
		blue.setSuffix(ChatColor.WHITE.toString());
		blue.setColor(ChatColor.BLUE);
		
		dark_aqua = scoreboard.registerNewTeam(teamf.getString("dark_aqua.name"));
		dark_aqua.setPrefix(ChatColor.DARK_AQUA.toString());
		dark_aqua.setSuffix(ChatColor.WHITE.toString());
		dark_aqua.setColor(ChatColor.DARK_AQUA);
		
		yellow = scoreboard.registerNewTeam(teamf.getString("yellow.name"));
		yellow.setPrefix(ChatColor.YELLOW.toString());
		yellow.setSuffix(ChatColor.WHITE.toString());
		yellow.setColor(ChatColor.YELLOW);
		
		dark_purple = scoreboard.registerNewTeam(teamf.getString("dark_purple.name"));
		dark_purple.setPrefix(ChatColor.DARK_PURPLE.toString());
		dark_purple.setSuffix(ChatColor.WHITE.toString());
		dark_purple.setColor(ChatColor.DARK_PURPLE);
		
		dark_green = scoreboard.registerNewTeam(teamf.getString("dark_green.name"));
		dark_green.setPrefix(ChatColor.DARK_GREEN.toString());
		dark_green.setSuffix(ChatColor.WHITE.toString());
		dark_green.setColor(ChatColor.DARK_GREEN);
		
		dark_gray = scoreboard.registerNewTeam(teamf.getString("dark_gray.name"));
		dark_gray.setPrefix(ChatColor.DARK_GRAY.toString());
		dark_gray.setSuffix(ChatColor.WHITE.toString());
		dark_gray.setColor(ChatColor.DARK_GRAY);

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
		l1 = Utilities.GetLocationFromFile(teamf, worldName, "blue.");
		l2 = Utilities.GetLocationFromFile(teamf, worldName, "dark_aqua.");
		l3 = Utilities.GetLocationFromFile(teamf, worldName, "yellow.");
		l4 = Utilities.GetLocationFromFile(teamf, worldName, "dark_purple.");
		l5 = Utilities.GetLocationFromFile(teamf, worldName, "dark_green.");
		l6 = Utilities.GetLocationFromFile(teamf, worldName, "dark_gray.");

		if (!meetupteamtp) 
			return;

		meetupl1 = Utilities.GetLocationFromFile(teamf, worldLobbyName, "blue.meetup");
		meetupl2 = Utilities.GetLocationFromFile(teamf, worldLobbyName, "dark_aqua.meetup");
		meetupl3 = Utilities.GetLocationFromFile(teamf, worldLobbyName, "yellow.meetup");
		meetupl4 = Utilities.GetLocationFromFile(teamf, worldLobbyName, "dark_purple.meetup");
		meetupl5 = Utilities.GetLocationFromFile(teamf, worldLobbyName, "dark_green.meetup");
		meetupl6 = Utilities.GetLocationFromFile(teamf, worldLobbyName, "dark_gray.meetup");
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

		for (int i = 0; i < plugin.getConfig().getInt("options.taupesteams"); ++i) 
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
