package com.powercivs.claims;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import com.powercivs.PowerCivs;

public class ClaimManager {

	public static ArrayList<LandClaim> claims = new ArrayList<LandClaim>();
	
	public static void initClaimManager() {
		File directory = new File(PowerCivs.path + "/Claims");
		
		File[] list = directory.listFiles();
		
		for(File file : list) {
			if(file.isFile()) {
				try {
					FileInputStream fis = new FileInputStream(file);
					ObjectInputStream ois = new ObjectInputStream(fis);
					
					LandClaim lc = (LandClaim) ois.readObject();
					claims.add(lc);
					
					
					ois.close();
					fis.close();
				}catch(Exception e) {
					e.printStackTrace();
				}
			}
		}
		
	}
	
	public static LandClaim getLandClaim(String chX, String chY) {
		for(LandClaim lc : claims) {
			if(lc.chX.equalsIgnoreCase(chX) && lc.chY.equalsIgnoreCase(chY)) {
				return lc;
			}else {
				continue;
			}
		}
		return null;
	}
	
	public static boolean addLandClaim(String chX, String chY, String owner) {
		LandClaim lc = getLandClaim(chX, chY);
		
		if(lc == null) {
			lc = new LandClaim();
			lc.chX = chX;
			lc.chY = chY;
			lc.owner = owner;
			claims.add(lc);
			return true;
		}else {
			return false;
		}
		
	}
	
	public static void saveClaims() {
		try {
			for(LandClaim lc : claims) {
				
				FileOutputStream fout = new FileOutputStream(PowerCivs.path + "/Claims/" + lc.getOwner() + "(" + lc.chX + ")" + lc.chY + ".dat");
				ObjectOutputStream oos = new ObjectOutputStream(fout);
				
				oos.writeObject(lc);
				oos.close();
				fout.flush();
				fout.close();
				
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
}
