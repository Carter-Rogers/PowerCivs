package powercivs;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_13_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;

import net.minecraft.server.v1_13_R2.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_13_R2.PacketPlayOutTitle;
import net.minecraft.server.v1_13_R2.PacketPlayOutTitle.EnumTitleAction;
import powercivs.corporations.Corporation;
import powercivs.corporations.CorporationManager;
import powercivs.nations.Citizen;
import powercivs.nations.CitizenManager;
import powercivs.nations.ClaimManager;
import powercivs.nations.LandClaim;
import powercivs.nations.Nation;
import powercivs.nations.Nation.NationType;
import powercivs.nations.NationManager;
import powercivs.nations.legislation.Policy;
import powercivs.nations.legislation.Policy.PolicyApplies;


public final class PowerCivs extends JavaPlugin implements Listener {

	public static final Nation RiverCliffe = new Nation(NationType.DEMOCRATIC, "RiverCliffe", null);

	public static String path;

	boolean loaded = false;

	public static ArrayList<Player> playersinworld = new ArrayList<>();

	public PacketPlayOutTitle packet = new PacketPlayOutTitle(EnumTitleAction.TITLE,
			ChatSerializer.a("{\"text\":\"Welcome To\"}"), 20, 60, 20);

	public static Scoreboard sb;
	
	int index = 0;
	
	@Override
	public void onEnable() {
		
		Bukkit.getPluginManager().registerEvents(this, this);

		getDataFolder().mkdir();
		new File(getDataFolder().getAbsolutePath() + "/Players").mkdir();
		new File(getDataFolder().getAbsolutePath() + "/Corporations").mkdir();
		new File(getDataFolder().getAbsolutePath() + "/Nations").mkdir();
		new File(getDataFolder().getAbsolutePath() + "/Claims").mkdir();

		path = getDataFolder().getPath();

		getLogger().info("PowerCivs has been enabled! Let the game of power begin!");

		/*
		 * Reload Code
		 */

		@SuppressWarnings("unused")
		int id = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
			public void run() {
				Bukkit.getServer().broadcastMessage(ChatColor.GOLD
						+ "Remember To Register Your Corporation Or You May Be Arrested Or Have A Bounty Put On You! Remember Registering Is Optional Though!");
				index += 1;
				
				for (Corporation corp : CorporationManager.corporations) {
					corp.takeTax();
					corp.payEmployees();
				}
				
				if(index == 5) {
					for(Citizen c : CitizenManager.citizens) {
						Nation n = NationManager.getNationByCit(c.getDisplayName());
						
						if(n != null) {
							int balance = c.getMoney(); //all of player's money
							
							double takeAmount = balance * n.flat_tax.getRate();
							
							c.addMoney((int)-takeAmount);
							n.addCapital(takeAmount);
							NationManager.saveNations();
							CitizenManager.saveCitizen();
						}
						
					}
					
					CitizenManager.takeTax();
					
					Bukkit.getServer().broadcastMessage(ChatColor.GREEN + "Taxes Have Been Collected From All Citizens!");
					index = 0;
				}

				NationManager.saveNations();
				CorporationManager.saveCorporations();
				CitizenManager.saveCitizen();
				ClaimManager.saveClaims();

				if (loaded)
					return;
				if (!loaded) {
					NationManager.initNations();
					CorporationManager.initCorporations();
					CitizenManager.initCitizens();
					ClaimManager.initClaims();
					try {

						loaded = true;
					} catch (Exception e) {
						return;
					}
				}
				
				for (Player p : Bukkit.getOnlinePlayers()) {
					if(CitizenManager.getCitizen(p.getDisplayName()) != null)
						return;
					else {
						CitizenManager.addCitizen(new Citizen(p.getDisplayName()));
					}
				}

			}

		}, 0, 600);

	}

	@Override
	public void onDisable() {

	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("c") || cmd.getName().equalsIgnoreCase("corporation")) {
			String command = args[0];

			if (command.equalsIgnoreCase("info")) {
				Player p = (Player) sender;

				Corporation c = CorporationManager.getInfo(args[1]); // args[1] is name of corporation

				p.sendMessage("");
				p.sendMessage(ChatColor.GREEN + "        Corporation Info");
				p.sendMessage("");
				p.sendMessage(ChatColor.GOLD + "NAME: " + ChatColor.DARK_GREEN + c.getName());
				
				DecimalFormat df = new DecimalFormat();
				df.setMaximumFractionDigits(2);
				
				p.sendMessage(ChatColor.GOLD + "CAPITAL: " + ChatColor.DARK_GREEN + "$" + df.format(c.getCapital()));
				p.sendMessage(ChatColor.GOLD + "HOME NATION: " + ChatColor.DARK_GREEN + c.getNation());
				p.sendMessage("");

				return true;
			}

			if (command.equalsIgnoreCase("remove")) {
				
				double left = CorporationManager.getCorporation(((Player)sender)).getCapital();
				
				Corporation c = CorporationManager.getCorporation((Player)sender);
				
				CitizenManager.getCitizen(Bukkit.getPlayer(c.CEO).getDisplayName()).addMoney((int)left);
				
				CorporationManager.removeCorporation(((Player) sender).getUniqueId(), args[1]);
				return true;
			}
			
			if(command.equalsIgnoreCase("hire")) {
				String player = args[1];
				
				Corporation c = CorporationManager.getCorporation((Player)sender);
				
				if(c != null) {
					if(c.CEO.equals(((Player)sender).getUniqueId()))
						c.addEmployee(player);					
				}
				
				
				return true;
			}
			
			if(command.equalsIgnoreCase("payrate")) {
				String amount = args[1];
				
				Corporation c = CorporationManager.getCorporation((Player)sender);
				
				if(c != null) {
					if(c.CEO.equals(((Player)sender).getUniqueId())) {
						c.employeePercentage = Integer.parseInt(amount);
						Bukkit.broadcastMessage(c.getName() + " Has Set Employee Pay Rate To " + c.employeePercentage + "%");
					}
				}
				
				return true;
				
			}

			return true;
		}

		if (cmd.getName().equalsIgnoreCase("register")) {
			String type = args[0];
			String name = args[1];
			String ty;
			try {
				ty = args[2];
			} catch (Exception e) {
				ty = "dem";
			}

			if (type.equalsIgnoreCase("n") || type.equalsIgnoreCase("nation")) {
				Player p = (Player) sender;

				NationType ntype = NationType.DEMOCRATIC;

				switch (ty) {
				case "dem":
					ntype = NationType.DEMOCRATIC;
					break;
				case "democratic":
					ntype = NationType.DEMOCRATIC;
					break;
				}

				if (NationManager.registerNation(new Nation(ntype, name, p))) {
					Bukkit.broadcastMessage("Welcome " + name);
					saveNations();
				}

				return true;
			}

			if (type.equalsIgnoreCase("c") || type.equalsIgnoreCase("corporation")) {
				Corporation c = new Corporation(name, ((Player)sender).getUniqueId());
				CorporationManager.registerCorporation(c);
				return true;
			}

		}

		if (cmd.getName().equalsIgnoreCase("list")) {
			Player p = (Player) sender;

			if (args[0].equalsIgnoreCase("nation") || args[0].equalsIgnoreCase("nations")) {

				for (Nation n : NationManager.nations) {
					Bukkit.broadcastMessage(n.getNationName());
				}

				return true;
			} else if (args[0].equalsIgnoreCase("corporations")) {
				for (Corporation c : CorporationManager.corporations) {
					p.sendMessage(c.getName());
				}

				return true;
			} else if (args[0].equalsIgnoreCase("citizens")) {
				StringBuilder sb = new StringBuilder();

				for (Citizen c : CitizenManager.citizens) {
					sb.append(c.getDisplayName() + "$" + c.getMoney() + ", ");
				}

				p.sendMessage(sb.toString());

				sb = null;

				return true;
			} else if (args[0].equalsIgnoreCase("claims")) {
				StringBuilder sb = new StringBuilder();

				for (LandClaim lc : ClaimManager.claims) {
					sb.append(lc.getOwner() + "(" + lc.XandY() + ")");
				}

				p.sendMessage(sb.toString());
				return true;
			}
			return true;
		}

		if (cmd.getName().equalsIgnoreCase("saven")) {
			saveNations();
			return true;
		}

		if (cmd.getName().equalsIgnoreCase("taxes")) {
			for (Corporation c : CorporationManager.corporations) {
				c.takeTax();
			}

			Bukkit.broadcastMessage("Taxes Have Been Collected");

			return true;
		}

		/*
		 * Policy Commands
		 * 
		 */

		if (cmd.getName().equalsIgnoreCase("claim")) {
			String nation = args[0];

			Nation n = NationManager.getNation(nation);

			if (n != null) {
				if (n.getUUID() == null) {
					Chunk cc = ((Player) sender).getWorld().getChunkAt(((Player) sender).getLocation());
					String cX = "" + cc.getX();
					String cY = "" + cc.getZ();
					
					if(ClaimManager.getClaim(cX, cY) == null) {
						@SuppressWarnings("unused")
						LandClaim lc = new LandClaim(cX, cY, n);
						Bukkit.broadcastMessage(ChatColor.GREEN + " Has Just Made A Land Claim At Chunk: " + cX + ", " + cY + "!");
						ClaimManager.saveClaims();	
						return true;
					}else {
						((Player)sender).sendMessage(ChatColor.RED + " You Cannot Claim This Area!");
						return true;
					}
				}
				if (n.getUUID().equals(((Player) sender).getUniqueId())) {
					
					Chunk cc = ((Player) sender).getWorld().getChunkAt(((Player) sender).getLocation());
					String cX = "" + cc.getX();
					String cY = "" + cc.getZ();

					if(ClaimManager.getClaim(cX, cY) == null) { 
						@SuppressWarnings("unused")
						LandClaim lc = new LandClaim(cX, cY, n);
						Bukkit.broadcastMessage(
								ChatColor.GREEN + " Has Just Made A Land Claim At Chunk: " + cX + ", " + cY + "!");
						ClaimManager.saveClaims();
						return true;
					}else {
						((Player)sender).sendMessage(ChatColor.RED + " You Cannot Claim This Area!");
						return true;
					}
				}
				return true;
			}
			return true;
		}

		if (cmd.getName().equalsIgnoreCase("n") || cmd.getName().equalsIgnoreCase("nation")) {

			String command = args[0];

			if (command.equalsIgnoreCase("remove")) {
				Nation n = NationManager.getNation(args[1]);

				if (n != null) {
					
					if (n.getMayor().equalsIgnoreCase(((Player) sender).getName())) {
						Bukkit.broadcastMessage(ChatColor.BLUE + n.getNationName() + " Has Fallen! RIP");
					
						File directory = new File(PowerCivs.path + "/Claims");

						File[] list = directory.listFiles();

						for (File ff : list) {
							if (ff.isFile()) {
								if(ff.getAbsolutePath().contains(n.getNationName()))
									ff.delete();
							}
						}
					
						File f = new File(path + "/" + n.getNationName() + ".dat");
						File f1 = new File(path + "/" + n.getNationName() + "_bank.dat");
						f.delete();
						f1.delete();
						
						CitizenManager.getCitizen(n.getMayor()).addMoney((int)n.getTreasury());
						
						NationManager.removeNation(n.getNationName());
						ClaimManager.removeClaim(n.getNationName());
						ClaimManager.reload();
						return true;
					} else if (!n.getMayor().equalsIgnoreCase(((Player) sender).getName())) {
						Bukkit.getPlayer(((Player) sender).getUniqueId())
								.sendMessage(ChatColor.RED + "You Cannot Remove That Nation!");
						return true;
					}
					return true;
				}
			}

			if (command.equalsIgnoreCase("info")) {
				Player p = (Player) sender;

				try {

					Nation n = NationManager.getNation(args[1]);

					if (n != null) {
						p.sendMessage(ChatColor.GREEN + "        Nation Info        ");
						p.sendMessage("");
						p.sendMessage(ChatColor.GOLD + "NAME: " + ChatColor.DARK_GREEN + n.getNationName());
						p.sendMessage(ChatColor.GOLD + "GOVERNMENT TYPE: " + ChatColor.DARK_GREEN + n.getType());
				
						
						DecimalFormat df = new DecimalFormat();
						df.setMaximumFractionDigits(2);
						
						p.sendMessage(ChatColor.GOLD + "TREASURY: " + ChatColor.DARK_GREEN + "$" + df.format(n.getTreasury()));
						p.sendMessage(ChatColor.GOLD + "CORPORATE TAX RATE: " + ChatColor.DARK_GREEN
								+ n.getCorporateRate() + "%");
						
						StringBuilder sbb = new StringBuilder();
						if(n.policies.size() <= 0) {}
						else {
							for(Policy pp : n.policies) {
								sbb.append(ChatColor.GREEN + pp.getPolicyName() + ": " + pp.value + "% ,");
							}
						}
						p.sendMessage(ChatColor.GOLD + "OTHER POLICIES: " + sbb.toString());
						
						p.sendMessage(ChatColor.GOLD + "MAYOR: " + ChatColor.DARK_GREEN + n.getMayor());
						
						StringBuilder sb = new StringBuilder();
						
						for(String s : n.enemies) {
							sb.append(s + ", ");
						}
						
						p.sendMessage(ChatColor.GOLD + "WARS: " + ChatColor.RED + sb.toString());
						return true;
					} else {
						p.sendMessage(ChatColor.RED + "No Such Nation!");
						return true;
					}
				} catch (Exception e) {
					return true;
				}

			}
			
			if(command.equalsIgnoreCase("add")) {
				String name = args[1];
				
				if(Bukkit.getPlayer(name) != null) {
					Player p = ((Player)sender);
					Nation n = NationManager.getNationByCit(p.getDisplayName());
					if(n != null) {
						CitizenManager.getCitizen(name).addRequest(n.getNationName());	
						p.sendMessage("Citizen Requested To Join!");
						return true;
					}else {
						p.sendMessage("Failed!");
						return true;
					}
				}else if(name.equalsIgnoreCase("policy") || name.equalsIgnoreCase("p")) {
					
					String pName = args[2];
					double value = Double.valueOf(args[3]);
					String applies = args[4];
					
					Nation n = NationManager.getNationByCit(((Player)sender).getDisplayName());
					
					if(n.getUUID().equals(((Player)sender).getUniqueId())) {
						
						if(applies.equalsIgnoreCase("citizens") || applies.equalsIgnoreCase("c")) {
							Policy p = new Policy(pName, value, PolicyApplies.CITIZENS);
						
							if(n.policies.contains(p))
								((Player)sender).sendMessage("Policy Exists!!!");
							else {
								n.policies.add(p);
								n.broadcastToCitizens("Your Nation Just Enacted A New Policy: " + p.getPolicyName() + "! It's Value is " + p.value + "! It Applies To " + p.appliesTo().getApplies());
							}
							
							return true;
						}
						return true;
					}		
				}
				return true;
			}

			if (command.equalsIgnoreCase("ct")) {
				
				double value = Double.parseDouble(args[1]);

				Nation n = NationManager.playerBelongs(((Player) sender));

				if(n != null) {					
					if (n.getMayor().equals(((Player) sender).getName())) {
						n.setCorporateRate(value);
						Bukkit.broadcastMessage(ChatColor.GREEN + "The Mayor Of " + n.getNationName()
						+ " Has Set The Corporate Tax Rate To " + ChatColor.RED + n.getCorporateRate() + "%!");
					}
				}
				
				return true;
			}

			if (command.equalsIgnoreCase("rn")) {

				String name = args[1];

				NationManager.removeName(name, ((Player) sender).getDisplayName());

				return true;
			}
			
			if(command.equalsIgnoreCase("rp")) {
				String policyname = args[1];
				
				Nation n = NationManager.playerBelongs(((Player) sender));
				
				if(n != null) {
					if(n.getMayor().equals(((Player)sender).getName())) {
						n.removePolicy(policyname);
						n.broadcastToCitizens("Your Nation Just Repealed The Policy: " + policyname + "!");
					}
				}
				
				return true;
			}
			
			if(command.equalsIgnoreCase("war")) {
				String name = args[1];
				
				Nation n = NationManager.playerBelongs(((Player)sender));
				
				if(n != null) {
					
					
					if(n.getMayor().equals(((Player)sender).getName())) {
						if(NationManager.getNation(name)!=null) {
							
							if(n.getTreasury() >= 250)
								NationManager.setWar(n, NationManager.getNation(name));
							else
								((Player)sender).sendMessage(ChatColor.RED + "Not Enough Funds To Do That!");
							return true;
						}
						return true;
					}
					return true;
				}
				
				return true;
			}
			
			if(command.equalsIgnoreCase("warend")) {
				String name = args[1];
				
				Nation n = NationManager.playerBelongs(((Player)sender));
				
				if(n != null) {
					
					
					if(n.getMayor().equals(((Player)sender).getName())) {
						if(NationManager.getNation(name)!=null) {
								NationManager.endWar(n, NationManager.getNation(name));
							return true;
						}
						return true;
					}
					return true;
				}
				
				return true;
			}
			
			if(command.equals("deposit")) {
				int amount = Integer.valueOf(args[1]);

				Citizen cc = CitizenManager.getCitizen(((Player)sender).getDisplayName());
				
				Nation n = NationManager.getNationByCit(cc.getDisplayName());
				
				Bukkit.broadcastMessage(n.getNationName());
				
				if(n != null) {
					Citizen c = CitizenManager.getCitizen(((Player)sender).getDisplayName());
					
					
					if(c.getMoney() >= amount) {
						n.addCapital(amount);
						c.addMoney(-amount);
						((Player)sender).sendMessage(ChatColor.GREEN + "Successfully Deposited $" + amount + "!");
						return true;
					}else {
						((Player)sender).sendMessage(ChatColor.RED + "Not Enough Money To Do That!");
						return true;
					}
					
					
				}
				
			}
 
			return true;
		}

		if(cmd.getName().equalsIgnoreCase("bal")) {
			
			Citizen c = CitizenManager.getCitizen(((Player)sender).getDisplayName());
			
			((Player)sender).sendMessage(ChatColor.GREEN + "Balance: $" + c.getMoney());
			
			return true;
		}
		
		if(cmd.getName().equalsIgnoreCase("accept")) {
			String name = args[0];
			
			Citizen c = CitizenManager.getCitizen(((Player)sender).getDisplayName());
			
			for(String s : c.requestingNations) {
				if(s.equals(name)) {
					Nation n = NationManager.getNation(name);
					if(n != null) {
						n.addCitizen(c);
						return true;
					}
				}else {
					continue;
				}
				((Player)sender).sendMessage(ChatColor.RED + "No Nation By That Name Has Requested You To Join!");
				return true;
			}
			
			return true;
		}
		
		return false;
	}

	public static void saveNations() {
		try {
			CorporationManager.saveCorporations();
			NationManager.saveNations();
		} catch (Exception e) {
		}
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent e) {
		Player p = (Player) e.getPlayer();
		Block b = (Block) e.getBlock();

		Chunk c = p.getWorld().getChunkAt(b);

		LandClaim cc = ClaimManager.getClaim("" + c.getX(), "" + c.getZ());

		Nation n = NationManager.getNationByCit(p.getDisplayName());

		if (cc == null) {
			return;
		} else {
			if(n==null)
				e.setCancelled(true);
			
			if (cc.getOwner().equals(n.getNationName()) || NationManager.getNation(cc.getOwner()).isEnemy(n.getNationName())) {
			} else {
				e.setCancelled(true);
			}
		}

	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent e) {
		Player p = (Player) e.getPlayer();
		Block b = (Block) e.getBlock();

		Chunk c = p.getWorld().getChunkAt(b);
		LandClaim cc = ClaimManager.getClaim("" + c.getX(), "" + c.getZ());

		Nation n = NationManager.getNationByCit(p.getDisplayName());

		if (cc == null) {
			return;
		} else {
			if (cc.getOwner().equals(n.getNationName()) || NationManager.getNation(cc.getOwner()).isEnemy(n.getNationName())) {
			} else {
				e.setCancelled(true);
			}
		}

	}

	String lastLoc = "NULL";

	@EventHandler
	public void onMove(PlayerMoveEvent e) {
		Player p = (Player) e.getPlayer();

		Chunk c = p.getWorld().getChunkAt(p.getLocation());
		LandClaim cc = ClaimManager.getClaim("" + c.getX(), "" + c.getZ());

		if (cc == null) {
			if (lastLoc != "NULL") {
				PacketPlayOutTitle packet = new PacketPlayOutTitle(EnumTitleAction.TITLE,
						ChatSerializer.a("{\"text\":\"" + ChatColor.GREEN + "Leaving " + lastLoc + "\"}"), 20, 60, 20);
				((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);
			}

			lastLoc = "NULL";
			return;
		} else {

			if (!lastLoc.equals(cc.getOwner())) {
				PacketPlayOutTitle packet = new PacketPlayOutTitle(EnumTitleAction.TITLE,
						ChatSerializer.a("{\"text\":\"" + ChatColor.GREEN + "Entering " + cc.getOwner() + "\"}"), 20,
						60, 20);
				((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);
				lastLoc = cc.getOwner();
			}

		}

	}

	@EventHandler
	public void onItemDrop(PlayerDropItemEvent e) {
		Player p = (Player) e.getPlayer();
		
		Chunk c = p.getWorld().getChunkAt(p.getLocation());
		LandClaim cc = ClaimManager.getClaim("" + c.getX(), "" + c.getZ());

		Nation n = NationManager.getNationByCit(p.getDisplayName());
		
		if (cc == null) {
			return;
		} else {
			try {
				if (cc.getOwner().equals(n.getNationName()) || NationManager.getNation(cc.getOwner()).isEnemy(n.getNationName())) {
				} else {
					e.setCancelled(true);
				}
			}catch(Exception ee) {
				e.setCancelled(true);
			}
			
		}

	}
	
	@EventHandler
	public void onPlayerUse(PlayerInteractEvent e) {
		Player p = (Player) e.getPlayer();
		
		Chunk c = p.getWorld().getChunkAt(p.getLocation());
		LandClaim cc = ClaimManager.getClaim("" + c.getX(), "" + c.getZ());

		Nation n = NationManager.getNationByCit(p.getDisplayName());
		
		if (cc == null) {
			return;
		} else {
			try {
				if (cc.getOwner().equals(n.getNationName()) || NationManager.getNation(cc.getOwner()).isEnemy(n.getNationName())) {
				} else {
					e.setCancelled(true);
				}
			}catch(Exception ee) {
				e.setCancelled(true);
			}
			
		}
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Citizen c = new Citizen(event.getPlayer().getDisplayName());
		CitizenManager.addCitizen(c);
		
		CitizenManager.saveCitizen();
	}
	
	
}