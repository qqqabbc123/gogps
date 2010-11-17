/*
 * Copyright (c) 2010, Cryms.com . All Rights Reserved.
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

package org.cryms.gogps.parser.ublox;

import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Vector;

import org.gogpsproject.Observations;

public class SerialConnection implements EventListener{


	private InputStream inputStream;
	private OutputStream outputStream;
	private boolean connected = false;
	//private Thread reader;
	@SuppressWarnings("restriction")
	private SerialPort serialPort;
	//private boolean end = false;
//	private int divider;
	//private int[] tempBytes;
//	int numTempBytes = 0, numTotBytes = 0;
	//private int id;
	private UBXSerialReader ubxReader;

	public SerialConnection() {


	}

	public boolean connect(String portName) {
		return connect(portName, 9600);
	}

	@SuppressWarnings("restriction")
	public boolean connect(String portName, int speed) {
		CommPortIdentifier portIdentifier;
		// OutputMessage msg = new OutputMessage();
		MsgConfiguration msgcfg;
		//OutputMessage clear = new OutputMessage();
		boolean conn = false;
		try {
			portIdentifier = CommPortIdentifier.getPortIdentifier(portName);
			if (portIdentifier.isCurrentlyOwned()) {
				System.out.println("Error: Port is currently in use");
			} else {
				serialPort = (SerialPort) portIdentifier.open("Serial", 2000);
				serialPort.setSerialPortParams(speed, SerialPort.DATABITS_8,
						SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);

				inputStream = serialPort.getInputStream();
				outputStream = serialPort.getOutputStream();

				ubxReader = new UBXSerialReader(inputStream,outputStream, this);
				ubxReader.start();
				
				connected = true;
				System.out.println("Connection on " + portName + " established");
				conn = true;
				
			}
		} catch (NoSuchPortException e) {
			System.out.println("The connection could not be made");
			e.printStackTrace();
		} catch (PortInUseException e) {
			System.out.println("The connection could not be made");
			e.printStackTrace();
		} catch (UnsupportedCommOperationException e) {
			System.out.println("The connection could not be made");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("The connection could not be made");
			e.printStackTrace();
		}
		return conn;
	}

	// serial reader

	public boolean disconnect() {
		boolean disconn = true;
		
		ubxReader.stop();
		//end = true;
//		try {
//			reader.join();
//		} catch (InterruptedException e1) {
//			e1.printStackTrace();
//			disconn = false;
//		}
		try {
			outputStream.close();
			inputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
			disconn = false;
		}
		serialPort.close();
		connected = false;
		System.out.println("Connection disconnected");
		return disconn;
	}

	// Close connection

	public Vector<String> getPortList() {
		Enumeration<CommPortIdentifier> portList;
		Vector<String> portVect = new Vector<String>();
		portList = CommPortIdentifier.getPortIdentifiers();

		CommPortIdentifier portId;
		while (portList.hasMoreElements()) {
			portId = portList.nextElement();
			if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL) {
				portVect.add(portId.getName());
			}
		}
		System.out.println("Found the following ports:");
		for (int i = 0; i < portVect.size(); i++) {
			System.out.println(portVect.elementAt(i));
		}

		return portVect;
	}

	public boolean isConnected() {
		return connected;
	}

	/* (non-Javadoc)
	 * @see org.cryms.gogps.parser.ublox.EventListener#addObservations(org.gogpsproject.Observations)
	 */
	@Override
	public void addObservations(Observations o) {
		System.out.println("# "+o.getGpsSize()+" GPS time "+o.getRefTime().getMsec());
		
	}

	/* (non-Javadoc)
	 * @see org.cryms.gogps.parser.ublox.EventListener#streamClosed()
	 */
	@Override
	public void streamClosed() {
		disconnect();
	}

//	public boolean writeSerial(String message) {
//		boolean success = false;
//		if (isConnected()) {
//			try {
//				outputStream.write(message.getBytes());
//				success = true;
//			} catch (IOException e) {
//				disconnect();
//			}
//		} else {
//			System.out.println("Debug : " + id + " No port is connected.");
//		}
//		return success;
//	}

}
