package powercivs.nations;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import powercivs.PowerCivs;

public class ClaimManager {

	public static ArrayList<LandClaim> claims = new ArrayList<LandClaim>();

	public static void initClaims() {
		File directory = new File(PowerCivs.path + "/Claims");

		File[] list = directory.listFiles();

		for (File f : list) {
			if (f.isFile()) {
				try {
					FileInputStream fis = new FileInputStream(f);
					ObjectInputStream ois = new ObjectInputStream(fis);

					LandClaim n = (LandClaim) ois.readObject();
					addClaim(n);
					ois.close();
					fis.close();
				} catch (Exception e) {
					return;
				}
			}
		}
	}

	public static void reload() {
		claims = null;
		claims = new ArrayList<LandClaim>();
		initClaims();
	}

	public static int numOfClaims(String owner) {
		int i = 0;
		for(LandClaim cc : claims) {
			if(cc.getOwner() != null || cc.getOwner() != "?") {
				if(cc.getOwner().equals(owner)) {
					i += 1;
				}else {
					continue;
				}
			}
		}
		return i;
	}
	
	public static void removeClaim(String owner) {
		try {
			if (NationManager.getNation(owner) != null) {
				for (LandClaim lc : claims) {
					if(lc.getOwner().equals(owner)) {
						lc.removeLand();
					}
					
				}
			} else {
				return;
			}

			saveClaims();
		} catch (Exception e) {
			return;
		}
	}

	public static boolean addClaim(LandClaim claim) {
		if (claims.contains(claim))
			return false;
		else {
			claims.add(claim);
			return true;
		}

	}

	public static void forSale(String chX, String chY, boolean tof) {
		getClaim(chX, chY).setForsale(tof);
	}
	
	public static void removeClaim(String chX, String chY) {
		int index = 0;
		for(LandClaim c : claims) {
			if(c.getChX() == chX && c.getChY() == chY)
				index = claims.indexOf(c);
		}
		
		claims.remove(index);
	}
	
	public static LandClaim getClaim(String chX, String chY) {
		for (LandClaim c : claims) {
			if (c.getChX().equals(chX) & c.getChY().equals(chY))
				return c;
			else
				continue;
		}
		return null;
	}

	public static void saveClaims() {
		try {
			for (LandClaim claim : claims) {
				FileOutputStream fout = null;
					if (claim.getPrivateOwner().equals("?") || claim.getPrivateOwner() == null) {

						fout = new FileOutputStream(PowerCivs.path + "/Claims/" + claim.getOwner() + "," + claim.getChX() + ","
								+ claim.getChY() + ".dat");
					} else {
						fout = new FileOutputStream(PowerCivs.path + "/Claims/" + claim.getPrivateOwner() + "," + claim.getChX()
								+ "," + claim.getChY() + ".dat");

					}

					ObjectOutputStream oos = new ObjectOutputStream(fout);
					oos.writeObject(claim);
					oos.close();
					fout.flush();
					fout.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}