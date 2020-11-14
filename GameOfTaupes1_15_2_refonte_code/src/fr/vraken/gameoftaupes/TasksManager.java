package fr.vraken.gameoftaupes;

import java.util.ArrayList;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class TasksManager 
{
	static GameOfTaupes plugin;	
	private CustomScoreboardManager customScoreboardManager;

	private ArrayList<BukkitTask> tasks = new ArrayList<BukkitTask>();

	public TasksManager(GameOfTaupes gameoftaupes)
	{
		plugin = gameoftaupes;
		customScoreboardManager = gameoftaupes.customScoreboardManager;
	}

	public void LaunchTasks(FileConfiguration config)
	{

		BukkitTask scoreboardTask = new BukkitRunnable() 
		{
			public void run() 
			{
				customScoreboardManager.UpdateScoreboard();
			}
		}.runTaskTimer(plugin, 0L, 20L);

		BukkitTask pvpTask = new BukkitRunnable() 
		{
			public void run() 
			{
				plugin.EnablePvp();
			}
		}.runTaskLater(plugin, 1200 * config.getInt("options.pvptime"));

		BukkitTask taupesAnnouceTask = new BukkitRunnable() 
		{
			public void run() 
			{
				plugin.TaupeAnnouncement();
			}
		}.runTaskLater(plugin, 1200 * config.getInt("options.settaupesafter"));
		
		BukkitTask taupesRevealTask = new BukkitRunnable()
{
			public void run() 
			{
				plugin.ForceReveal(true);
			}
		}.runTaskLater(plugin, 1200 * config.getInt("options.forcereveal"));

		BukkitTask supertaupesAnnouceTask = new BukkitRunnable() 
		{
			public void run() 
			{
				plugin.SupertaupeAnnouncement();
			}
		}.runTaskLater(plugin, 1200 * config.getInt("options.setsupertaupesafter"));
		
		BukkitTask supertaupesRevealTask = new BukkitRunnable() 
		{
			public void run() 
			{
				plugin.SuperReveal(true);
			}
		}.runTaskLater(plugin, 1200 * config.getInt("options.superreveal"));

		BukkitTask borderShrinkTask = new BukkitRunnable() 
		{
			public void run() 
			{
				plugin.BorderShrink();
			}
		}.runTaskLater(plugin, 1200 * config.getInt("worldborder.retractafter"));

		BukkitTask borderFinalShrinkTask = new BukkitRunnable() 
		{
			public void run() 
			{
				plugin.BorderFinalShrink();
			}
		}.runTaskLater(plugin, 1200 * config.getInt("worldborder.finalretract"));

		BukkitTask borderFinalFinalShrinkTask = new BukkitRunnable() 
		{
			public void run() 
			{
				plugin.BorderFinalFinalShrink();
			}

		}.runTaskTimer(plugin, 0, 40);

		tasks.add(scoreboardTask);
		tasks.add(pvpTask);
		tasks.add(taupesAnnouceTask);
		tasks.add(taupesRevealTask);
		tasks.add(supertaupesAnnouceTask);
		tasks.add(supertaupesRevealTask);
		tasks.add(borderShrinkTask);
		tasks.add(borderFinalShrinkTask);
		tasks.add(borderFinalFinalShrinkTask);
		

		if (config.getBoolean("chest.random")) 
		{
			BukkitTask chestTask = new BukkitRunnable() 
			{
				public void run() 
				{
					if (plugin.finalZone) 
						this.cancel();

					plugin.SpawnChest();
				}

			}.runTaskTimer(plugin, 6000, 1200 * config.getInt("chest.timer"));

			tasks.add(chestTask);
		}
	}

	public void CancelAllTasks()
	{
		for (BukkitTask task : tasks)
		{
			if(task.isCancelled())
				continue;

			task.cancel();
		}
	}

}
