package fr.vraken.gameoftaupes;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

/*
 0 : PvP
 1 : Taupes
 2 : Supertaupe
 3 : Shrink
 4 : Reveal
 5 : SuperReveal
 6 : FinalShrink  
 */

public class CustomScoreboardManager 
{	
	static GameOfTaupes plugin;

	int eventIndex;	
	HashMap<Integer, Integer> eventTimers = new HashMap<Integer, Integer>();
	HashMap<Integer, String> eventStrings = new HashMap<Integer, String>();
	ArrayList<Integer> sortedByTimer = new ArrayList<Integer>();

	int episode;
	int minute;
	int second;
	int minuteTot;
	int minuteToObj;
	int tmpPlayers;
	int tmpBorder;
	ScoreboardManager sm;
	Scoreboard s;
	Objective obj;
	Objective vie;
	NumberFormat objFormatter;
	int height = 10;

	public CustomScoreboardManager(GameOfTaupes gameoftaupes)
	{
		plugin = gameoftaupes;
		
		sm = Bukkit.getScoreboardManager();
		s = sm.getMainScoreboard();
		if (s.getObjective("GameOfTaupes") != null)
			s.getObjective("GameOfTaupes").unregister();
		
		obj = s.registerNewObjective("GameOfTaupes", "dummy", "Game Of Taupes");
		obj.setDisplaySlot(DisplaySlot.SIDEBAR);

		if (s.getObjective("Vie") == null) 
		{
			vie = s.registerNewObjective("Health", "health", "Health");
			vie.setDisplaySlot(DisplaySlot.PLAYER_LIST);
		}

		objFormatter = new DecimalFormat("00");

		eventStrings.put(0, "PvP : ");
		eventStrings.put(1, "Taupes : ");
		eventStrings.put(2, "Supertaupe : ");
		eventStrings.put(3, "World Border : ");
		eventStrings.put(4, "Reveal : ");
		eventStrings.put(5, "Super Reveal : ");
		eventStrings.put(6, "Final Shrink : ");

		eventTimers.put(0, plugin.getConfig().getInt("options.pvptime"));
		eventTimers.put(1, plugin.getConfig().getInt("options.settaupesafter"));
		eventTimers.put(2, plugin.getConfig().getInt("options.setsupertaupesafter"));
		eventTimers.put(3, plugin.getConfig().getInt("worldborder.retractafter"));
		eventTimers.put(4, plugin.getConfig().getInt("options.forcereveal"));
		eventTimers.put(5, plugin.getConfig().getInt("options.superreveal"));
		eventTimers.put(6, plugin.getConfig().getInt("worldborder.finalretract"));

		sortedByTimer = new ArrayList<Integer>(SortByValue(eventTimers).keySet());
	}

	public void InitScoreboard()
	{
		eventIndex = 0;	
		minute = 20;
		second = 0;
		minuteTot = -1;
		episode = 1;
		minuteToObj = eventTimers.get(sortedByTimer.get(eventIndex));

		tmpPlayers = plugin.playersAlive.size();
		tmpBorder = (int) plugin.getServer().getWorld(plugin.getConfig().getString("world")).getWorldBorder().getSize();

		s.getObjective(plugin.obj.getDisplayName())
			.getScore(ChatColor.WHITE + "Episode " + plugin.episode)
			.setScore(0);

		s.getObjective(plugin.obj.getDisplayName())
			.getScore("" + ChatColor.WHITE + tmpPlayers + ChatColor.GRAY + " joueurs")
			.setScore(-1);

		s.getObjective(plugin.obj.getDisplayName())
			.getScore(ChatColor.WHITE + "Border : " + tmpBorder + " x " + tmpBorder)
			.setScore(-3);

		s.getObjective(plugin.obj.getDisplayName())
			.getScore(ChatColor.WHITE 
				+ eventStrings.get(sortedByTimer.get(eventIndex)) 
				+ objFormatter.format(minuteToObj)
				+ ":" 
				+ objFormatter.format(second))
			.setScore(-4);

		s.getObjective(plugin.obj.getDisplayName())
			.getScore(objFormatter.format(minute) + ":" + objFormatter.format(second))
			.setScore(-5);
	}

	public void UpdateScoreboard() 
	{
		s.resetScores(objFormatter.format(minute) + ":" + objFormatter.format(second));

		if(eventIndex < sortedByTimer.size())
		{
			s.resetScores(ChatColor.WHITE 
					+ eventStrings.get(sortedByTimer.get(eventIndex)) 
					+ objFormatter.format(minuteToObj)
					+ ":" 
					+ objFormatter.format(second));
		}

		if(--second < 0)
		{
			second = 59;
			--minuteToObj;

			if(--minute < 0)
			{
				minute = 19;
				s.resetScores(ChatColor.WHITE + "Episode " + episode);
				episode++;

				s.getObjective(plugin.obj.getDisplayName())
					.getScore(ChatColor.WHITE + "Episode " + episode)
					.setScore(0);
			}

			if(++minuteTot >= eventTimers.get(sortedByTimer.get(eventIndex)) && eventIndex < sortedByTimer.size() - 1)
				minuteToObj = eventTimers.get(sortedByTimer.get(++eventIndex)) - minuteTot - 1;
		}

		if(tmpBorder != plugin.tmpBorder)
		{
			s.resetScores(ChatColor.WHITE + "Border : " + tmpBorder + " x " + tmpBorder);
			tmpBorder = plugin.tmpBorder;

			s.getObjective(plugin.obj.getDisplayName())
				.getScore(ChatColor.WHITE + "Border : " + tmpBorder + " x " + tmpBorder)
				.setScore(-3);
		}

		if(tmpPlayers != plugin.playersAlive.size())
		{
			s.resetScores("" + ChatColor.WHITE + tmpPlayers + ChatColor.GRAY + " joueurs");
			tmpPlayers = plugin.playersAlive.size();

			s.getObjective(plugin.obj.getDisplayName())
				.getScore("" + ChatColor.WHITE + plugin.playersAlive.size() + ChatColor.GRAY + " joueurs")
				.setScore(-1);
		}

		if(eventIndex < sortedByTimer.size())
		{
			s.getObjective(plugin.obj.getDisplayName())
				.getScore(ChatColor.WHITE 
					+ eventStrings.get(sortedByTimer.get(eventIndex)) 
					+ objFormatter.format(minuteToObj)
					+ ":" 
					+ objFormatter.format(second))
				.setScore(-4);
		}

		s.getObjective(plugin.obj.getDisplayName())
			.getScore(objFormatter.format(minute) + ":" + objFormatter.format(second))
			.setScore(-5);
	}

	public static HashMap<Integer, Integer> SortByValue(HashMap<Integer, Integer> map) 
	{
		return map.entrySet()
				.stream()
				.sorted((Map.Entry.<Integer, Integer>comparingByValue().reversed()))
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
	}


}
