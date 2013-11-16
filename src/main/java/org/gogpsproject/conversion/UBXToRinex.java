/*
 * Copyright (c) 2011 Eugenio Realini, Mirko Reguzzoni, Cryms sagl - Switzerland. All Rights Reserved.
 *
 * This file is part of goGPS Project (goGPS).
 *
 * goGPS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 *
 * goGPS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with goGPS.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package org.gogpsproject.conversion;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;

import org.gogpsproject.Observations;
import org.gogpsproject.ObservationsProducer;
import org.gogpsproject.parser.ublox.UBXFileReader;
import org.gogpsproject.producer.rinex.RinexV2Producer;

/**
 * @author Lorenzo Patocchi, cryms.com
 *
 * Converts UBX binary file to RINEX
 *
 */
public class UBXToRinex {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		if(args.length<1){
			System.out.println("UBXToRinex <ubx file> [y|n] (y=master n=rover=default)");
			return;
		}

		Calendar c = Calendar.getInstance();
		int yy = c.get(Calendar.YEAR)-2000;
		int p=0;
		String inFile = args[p++];
		String outFile = inFile.indexOf(".dat")>0?inFile.substring(0, inFile.indexOf(".dat"))+"."+yy+"o":inFile+"."+yy+"o";

		System.out.println("in :"+inFile);
		System.out.println("out:"+outFile);

		ObservationsProducer masterIn = new UBXFileReader(new File(inFile));
		try {
			masterIn.init();
		} catch (Exception e) {
			e.printStackTrace();
		}

//		ObservationsBuffer masterIn = new ObservationsBuffer();
//		try {
//			masterIn.readFromLog(inFile,false);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}

		System.out.println("RINEX");
		RinexV2Producer rp = new RinexV2Producer(outFile, args!=null&&args.length>=p+1&&args[p++].startsWith("y"));
		rp.setDefinedPosition(masterIn.getDefinedPosition());

		Observations o = masterIn.getNextObservations();
		while(o!=null){
			rp.addObservations(o);
			o = masterIn.getNextObservations();
		}
		rp.streamClosed();
		System.out.println("END");

	}

}
