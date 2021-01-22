package com.powercivs;

import static com.powercivs.claims.ClaimManager.addLandClaim;
import static com.powercivs.claims.ClaimManager.initClaimManager;
import static com.powercivs.entities.EntityManager.getCivByName;
import static com.powercivs.entities.EntityManager.getCivByUUID;
import static com.powercivs.entities.EntityManager.initEntityManager;
import static com.powercivs.entities.EntityManager.registerCivilization;
import static com.powercivs.entities.EntityManager.saveCivs;
import static com.powercivs.entities.PlayerLocation.playerLoc;

import java.io.File;
import java.io.IOException;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;

import com.powercivs.claims.ClaimManager;
import com.powercivs.claims.LandClaim;
import com.powercivs.data.SaveData;
import com.powercivs.entities.Civilization;
import com.powercivs.entities.Roles;

public class PowerCivs extends JavaPlugin implements Listener {

	public static String path;

	private File customConfigFile;
	private FileConfiguration customConfig;

	public FileConfiguration getCustomConfig() {
		return this.customConfig;
	}

	private void createCustomConfig() {
		customConfigFile = new File(getDataFolder(), "config.yml");
		if (!customConfigFile.exists()) {
			customConfigFile.getParentFile().mkdirs();
			saveResource("config.yml", false);
		}

		customConfig = new YamlConfiguration();
		try {
			customConfig.load(customConfigFile);
		} catch (IOException | InvalidConfigurationException e) {
			e.printStackTrace();
		}
	}

	private void folderSetup() {
		if (getDataFolder().exists() != true) {
			getDataFolder().mkdir();
		}

		new File(getDataFolder().getAbsolutePath() + "/Entities").mkdir();
		new File(getDataFolder().getAbsolutePath() + "/Claims").mkdir();

		path = getDataFolder().getPath();
	}

	@Override
	public void onEnable() {
		createCustomConfig();
		
		Bukkit.getPluginManager().registerEvents(this, this);

		folderSetup();

		initEntityManager();
		initClaimManager();

		Bukkit.getLogger().info("Test:" + customConfig.getInt("save-interval"));
		
		new SaveData().runTaskTimer(this, 0, customConfig.getInt("save-interval") * 1200); 
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Player player = (Player) sender;

		if (cmd.getName().equalsIgnoreCase("about")) {
			player.sendMessage(ChatColor.RED + "Type /help powercivs for a list of commands for PowerCivs!");
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
						player.sendMessage(ChatColor.GREEN + "Leader: " + ChatColor.BLUE + civ.getLeader());

						for (Entry<String, String> cabinet : civ.cabinet_roles.entrySet()) {
							player.sendMessage(
									ChatColor.GREEN + cabinet.getValue() + ":" + ChatColor.BLUE + cabinet.getKey());
						}

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
						player.sendMessage(ChatColor.GREEN + "Leader: " + ChatColor.BLUE + civ.getLeader());

						for (Entry<String, String> cabinet : civ.cabinet_roles.entrySet()) {
							player.sendMessage(
									ChatColor.GREEN + cabinet.getValue() + ":" + ChatColor.BLUE + cabinet.getKey());
						}

						player.sendMessage("");
						return true;
					}

					return true;
				}
			}

			if (cmd.getName().equalsIgnoreCase("civ") && args[0].equalsIgnoreCase("invite")) {
				String invitedPlayer = args[1];

				Civilization civ = getCivByUUID(player.getName());

				if (Bukkit.getPlayer(invitedPlayer) == null) {
					player.sendMessage(ChatColor.RED + "Unknown Player!");
					return true;
				} else {
					Civilization otherCiv = getCivByUUID(invitedPlayer);

					if (civ == null) {
						player.sendMessage(ChatColor.RED + "You Aren't In A Civilization!");
						return true;
					} else {
						if (otherCiv == null) {
							if (civ.getLeader().equalsIgnoreCase(player.getName())) {
								boolean invited = civ.invitePlayer(invitedPlayer);

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
									boolean invited = civ.invitePlayer(invitedPlayer);

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

			if (cmd.getName().equalsIgnoreCase("civ") && args[0].equalsIgnoreCase("appoint")) {

				if (args.length < 3) {
					player.sendMessage(ChatColor.RED + "Type /help powercivs for a list of commands for PowerCivs!");
					return true;
				} else {
					String appointed = args[1];
					String role = args[2];

					if (Bukkit.getPlayer(appointed) == null) {
						player.sendMessage(ChatColor.RED + "Player Must Be Online To Appoint!");
					} else {
						Civilization civ = getCivByUUID(player.getName());

						if (civ == null) {
							player.sendMessage(ChatColor.RED + "You Aren't In A Civilization!");
							return true;
						} else {
							if (civ.getLeader().equalsIgnoreCase(player.getName())) {

								if (appointed.equalsIgnoreCase(player.getName())) {
									player.sendMessage(ChatColor.RED + "Player Cannot Be You!");
									return true;
								} else {
									if (getCivByUUID(appointed) == null) {
										player.sendMessage(ChatColor.RED + "Player Isn't In Your Nation!");
										return true;
									} else if (getCivByUUID(appointed).getName().equalsIgnoreCase(civ.getName())) {

										switch (role) {
										case "sot":
											civ.appointCitizen(appointed, Roles.SOT);
											return true;
										case "sod":
											civ.appointCitizen(appointed, Roles.SOD);
											return true;
										case "sos":
											civ.appointCitizen(appointed, Roles.SOS);
											return true;
										default:
											civ.appointCitizen(appointed, Roles.SOT);
											return true;
										}

									} else {
										player.sendMessage(ChatColor.RED + "Player Isn't In Your Nation!");
									}
								}

							} else {
								player.sendMessage(ChatColor.RED + "You Aren't The Leader of This Civilization!");

								return true;
							}
						}

					}
				}

				return true;
			}

			if (cmd.getName().equalsIgnoreCase("civ") && args[0].equalsIgnoreCase("claim")) {

				Civilization civ = getCivByUUID(player.getName());

				if (civ == null) {
					player.sendMessage(ChatColor.RED + "You Aren't In A Civilization!");
					return true;
				} else {
					if (civ.getLeader().equalsIgnoreCase(player.getName())) {

						String chunkX = "" + player.getLocation().getChunk().getX();
						String chunkZ = "" + player.getLocation().getChunk().getZ();

						boolean claimed = addLandClaim(chunkX, chunkZ, civ.getName());

						if (claimed) {
							player.sendMessage(ChatColor.GREEN + "Added Claim!");
							return true;
						} else {
							player.sendMessage(ChatColor.RED + "Already Claimed By " + ChatColor.GREEN
									+ ClaimManager.getLandClaim(chunkX, chunkZ).getOwner());
							return true;
						}

					} else {
						player.sendMessage(ChatColor.RED + "You Aren't The Leader of This Civilization!");
						return true;
					}
				}

			}

			if (cmd.getName().equalsIgnoreCase("civ") && args[0].equalsIgnoreCase("accept")
					|| args[0].equalsIgnoreCase("deny")) {
				
				if(args.length <= 1) {
					player.sendMessage(ChatColor.BLUE + "EX: /civ <accept/deny> <nation>");
					return true;
				}else {	
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
			}

			if (cmd.getName().equalsIgnoreCase("civ") && args[0].equalsIgnoreCase("leave")) {
				Civilization civ = getCivByUUID(player.getName());

				if (civ == null) {
					player.sendMessage(ChatColor.RED + "You Aren't In A Civilization!");
					return true;
				} else {

					if (player.getName().equalsIgnoreCase(civ.getLeader())) {
						player.sendMessage(ChatColor.RED + "You Must Appoint A New Leader First!");
					} else {

						for (Entry<String, String> entry : civ.cabinet_roles.entrySet()) {
							if (entry.getKey().equalsIgnoreCase(player.getName())) {
								civ.cabinet_roles.remove(player.getName(), entry.getValue());
							}
						}

						civ.removeCitizen(player.getName());
						player.sendMessage(ChatColor.YELLOW + "Left Civilization Successfully!");
					}

					return true;
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

	@EventHandler
	public void onMove(PlayerMoveEvent e) {
		Player p = (Player) e.getPlayer();

		Chunk c = p.getLocation().getChunk();
		LandClaim lc = ClaimManager.getLandClaim("" + c.getX(), "" + c.getZ());

		if (!playerLoc.containsKey(p.getName())) {
			if (lc == null) {
				playerLoc.put(p.getName(), "?");
			} else if (lc != null) {
				playerLoc.put(p.getName(), lc.getOwner());
			}
		} else {
			if (lc == null && !playerLoc.get(p.getName()).equalsIgnoreCase("?")) { // player has left claimed area and
																					// entered wild
				p.sendTitle(ChatColor.BLUE + "Bye!", ChatColor.GREEN + "You are now entering the Wild.", 2, 40, 2);
				playerLoc.put(p.getName(), "?");
			}

			if (lc != null && playerLoc.get(p.getName()).equalsIgnoreCase("?")) { // player has left wild and entered
																					// claimed area
				p.sendTitle(ChatColor.BLUE + "Entering Territory", ChatColor.GREEN + "Owned By " + lc.getOwner(), 2, 40,
						2);
				playerLoc.put(p.getName(), lc.getOwner());
				return;
			}

			if (lc == null && playerLoc.get(p.getName()).equalsIgnoreCase("?")) {
				return;
			}

		}

	}

}
