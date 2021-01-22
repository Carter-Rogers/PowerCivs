package com.powercivs;

import static com.powercivs.entities.EntityManager.getCivByName;
import static com.powercivs.entities.EntityManager.getCivByUUID;
import static com.powercivs.entities.EntityManager.initEntityManager;
import static com.powercivs.entities.EntityManager.registerCivilization;
import static com.powercivs.entities.EntityManager.saveCivs;

import java.io.File;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import com.powercivs.data.SaveData;
import com.powercivs.entities.Civilization;

public class PowerCivs extends JavaPlugin implements Listener {

	public static String path;

	private void folderSetup() {
		if (getDataFolder().exists() != true) {
			getDataFolder().mkdir();
		}

		new File(getDataFolder().getAbsolutePath() + "/Entities").mkdir();

		path = getDataFolder().getPath();
	}

	@Override
	public void onEnable() {
		folderSetup();

		initEntityManager();

		new SaveData().runTaskTimer(this, 0, 1200); // save data every minute
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Player player = (Player) sender;

		if (cmd.getName().equalsIgnoreCase("about")) {
			Player p = (Player) sender;
			p.sendMessage(path);
			return true;
		}

		if (cmd.getName().equalsIgnoreCase("civ") && args.length > 0) {

			if (cmd.getName().equalsIgnoreCase("civ") && args[0].equalsIgnoreCase("new")) {
				if (args.length <= 1) {
					player.sendMessage(ChatColor.BLUE + "EX: /civ new <name>");
					return true;
				} else {

					@SuppressWarnings("unused")
					String type = args[0]; // default is civ for now.
					String name = args[1];

					Civilization civ = new Civilization(name);
					civ.setLeader(player.getName());

					if (getCivByUUID(civ.getLeader()) == null) {
						if (registerCivilization(civ)) {
							saveCivs();
							return true;
						} else {
							player.sendMessage(ChatColor.RED + "That Civilization Already Exists!");
						}

						return true;
					} else {
						player.sendMessage(ChatColor.RED + " You Are Either The Owner or Belong To Another Civ!");
					}

					return true;
				}
			}

			if (cmd.getName().equalsIgnoreCase("civ") && args[0].equalsIgnoreCase("info")) {
				if (args.length <= 1) { // Check your own civilization stats
					Civilization civ = getCivByUUID(player.getName());

					if (civ == null)
						player.sendMessage(ChatColor.RED + "You Aren't In A Civilization!");
					else {
						player.sendMessage("");
						player.sendMessage(ChatColor.GREEN + "" + civ.getName() + "'s Information:");
						player.sendMessage(ChatColor.GREEN + "Leader: " + ChatColor.BLUE
								+ civ.getLeader());

						player.sendMessage("");

						return true;
					}

					return true;
				} else {
					String name = args[1];

					Civilization civ = getCivByName(name);

					if (civ == null)
						player.sendMessage(ChatColor.RED + "That Civilization Doesn't Exist!");
					else {
						player.sendMessage("");
						player.sendMessage(ChatColor.GREEN + "" + civ.getName() + "'s Information:");
						player.sendMessage(ChatColor.GREEN + "Leader: " + ChatColor.BLUE
								+ civ.getLeader());
						player.sendMessage("");
						return true;
					}

					return true;
				}
			}

			if (cmd.getName().equalsIgnoreCase("civ") && args[0].equalsIgnoreCase("invite")) {
				String invitedPlayer = args[1];

				Civilization civ = getCivByUUID(player.getName());
				
				if(Bukkit.getPlayer(invitedPlayer) == null) {
					player.sendMessage(ChatColor.RED + "Unknown Player!");
					return true;
				}else {
					Civilization otherCiv = getCivByUUID(invitedPlayer);

					if (civ == null) {
						player.sendMessage(ChatColor.RED + "You Aren't In A Civilization!");
						return true;
					} else {
						if (otherCiv == null) {
							if (civ.getLeader().equalsIgnoreCase(player.getName())) {
								boolean invited = civ
										.invitePlayer(invitedPlayer);

								if (!invited) {
									player.sendMessage(ChatColor.RED + "Invite Already Sent!");
								} else {
									player.sendMessage(ChatColor.GREEN + "Civilization Invite Sent Successfully!");
								}

								return true;
							} else {
								player.sendMessage(ChatColor.RED + "You Aren't The Leader of This Civilization!");
								return true;
							}
						} else {
							if (civ.getLeader().equalsIgnoreCase(player.getName())) {
								if (otherCiv.getName().equalsIgnoreCase(civ.getName())) {
									player.sendMessage(ChatColor.RED + "Player Already Belongs To Your Civilization!");
									return true;
								} else {
									boolean invited = civ
											.invitePlayer(invitedPlayer);

									if (!invited) {
										player.sendMessage(ChatColor.RED + "Invite Already Sent!");
									} else {
										player.sendMessage(ChatColor.GREEN + "Civilization Invite Sent Successfully!");
									}

									return true;
								}
							} else {
								player.sendMessage(ChatColor.RED + "You Aren't The Leader of This Civilization!");
								return true;
							}
						}

					}					
				}
				
			}
			
			if(cmd.getName().equalsIgnoreCase("civ") && args[0].equalsIgnoreCase("appoint")) {
				String appointed = args[1];
				
				if(Bukkit.getPlayer(appointed) == null) {
					player.sendMessage(ChatColor.RED + "Player Must Be Online To Appoint!");
				}else {
					Civilization civ = getCivByUUID(player.getName());
					
					if(civ.getLeader().equalsIgnoreCase(player.getName())) {
						return true;
					}
					
				}
				
				
				return true;
			}
			

			if (cmd.getName().equalsIgnoreCase("civ") && args[0].equalsIgnoreCase("accept")
					|| args[0].equalsIgnoreCase("deny")) {
				String civ = args[1];

				Civilization civilization = getCivByName(civ);

				if (civilization == null) {
					return true;
				} else {

					if (civilization.isInvited(player.getName())) {
						if (args[0].equalsIgnoreCase("accept")) {
							civilization.AcceptOrDeny(player.getName(), true);
						} else if (args[0].equalsIgnoreCase("deny")) {
							civilization.AcceptOrDeny(player.getName(), false);
						}
						return true;
					} else {
						player.sendMessage(ChatColor.RED + "You Don't Have A Pending Invite From This Civilization!");
						return true;
					}

				}
			}

			return true;
		}

		if (cmd.getName().equalsIgnoreCase("civ") && args.length == 0) {
			player.sendMessage(ChatColor.RED + "Type /help powercivs for a list of commands for PowerCivs!");
			return true;
		}

		return true;
	}

	@Override
	public void onDisable() {

	}

}
