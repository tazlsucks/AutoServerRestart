package cc.tazl.serverrestart;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class restart extends JavaPlugin {
    private static final long RESTART_INTERVAL = 5 * 60 * 60 * 20L; // 5 hours in ticks (20 ticks = 1 second)
    private static final long FIVE_MINUTES = 5 * 60 * 20L;
    private static final long ONE_MINUTE = 60 * 20L;
    private static final long TEN_SECONDS = 10 * 20L;

    private long ticksUntilNextRestart;

    @Override
    public void onEnable() {
        scheduleRestart();
        getCommand("restarttime").setExecutor(new RestartTimeCommandExecutor());
    }

    private void scheduleRestart() {
        ticksUntilNextRestart = RESTART_INTERVAL - FIVE_MINUTES;
        new BukkitRunnable() {
            @Override
            public void run() {
                alertPlayers();
                ticksUntilNextRestart = RESTART_INTERVAL - FIVE_MINUTES;
            }
        }.runTaskTimer(this, ticksUntilNextRestart, RESTART_INTERVAL);

        new BukkitRunnable() {
            @Override
            public void run() {
                if (ticksUntilNextRestart > 0) {
                    ticksUntilNextRestart--;
                }
            }
        }.runTaskTimer(this, 0L, 1L);
    }

    private void alertPlayers() {
        // Alert 5 minutes before restart
        new BukkitRunnable() {
            @Override
            public void run() {
                Bukkit.broadcastMessage(ChatColor.RED + "Server will restart in 5 minutes!");
            }
        }.runTaskLater(this, 0L);

        // Alert 1 minute before restart
        new BukkitRunnable() {
            @Override
            public void run() {
                Bukkit.broadcastMessage(ChatColor.RED + "Server will restart in 1 minute!");
            }
        }.runTaskLater(this, FIVE_MINUTES - ONE_MINUTE);

        // Alert 10 seconds before restart
        new BukkitRunnable() {
            @Override
            public void run() {
                Bukkit.broadcastMessage(ChatColor.RED + "Server will restart in 10 seconds!");
            }
        }.runTaskLater(this, FIVE_MINUTES - TEN_SECONDS);

        // Restart the server
        new BukkitRunnable() {
            @Override
            public void run() {
                Bukkit.broadcastMessage(ChatColor.RED + "Server is restarting now!");
                Bukkit.shutdown();
            }
        }.runTaskLater(this, FIVE_MINUTES);
    }


    public class RestartTimeCommandExecutor implements CommandExecutor {
        @Override
        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            if (command.getName().equalsIgnoreCase("restarttime")) {
                long secondsUntilRestart = ticksUntilNextRestart / 20;
                long minutesUntilRestart = secondsUntilRestart / 60;
                long hoursUntilRestart = minutesUntilRestart / 60;

                secondsUntilRestart %= 60;
                minutesUntilRestart %= 60;

                String timeMessage = String.format("Time until next restart: %d hours, %d minutes, %d seconds",
                        hoursUntilRestart, minutesUntilRestart, secondsUntilRestart);

                sender.sendMessage(ChatColor.GREEN + timeMessage);
                return true;
            }
            return false;
        }
    }
}
