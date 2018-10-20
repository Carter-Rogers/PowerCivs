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
	
	public static void removeClaim(String owner) {
		try {			
			for(LandClaim lc : claims) {
				if(lc.getOwner().equals(owner)) {
					claims.remove(lc);
					continue;
				}
			}
		}catch(Exception e) {
			return;
		}
	}
	
	public static boolean addClaim(LandClaim claim) {
		if(claims.contains(claim))
			return false;
		else {
			claims.add(claim);
			return true;
		}
			
	}

	public static LandClaim getClaim(String chX, String chY) {
		for(LandClaim c : claims) {
			if(c.chX.equals(chX) & c.chY.equals(chY))
				return c;
			else
				continue;
		}
		return null;
	}
	
	public static void saveClaims() {
		try {
			for (LandClaim claim: claims) {
				FileOutputStream fout = new FileOutputStream(PowerCivs.path + "/Claims/" + claim.getOwner() + ","+claim.chX + ","+claim.chY + ".dat");
				ObjectOutputStream oos = new ObjectOutputStream(fout);
				oos.writeObject(claim);
				fout.flush();
				fout.close();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
