package main.java;

/* Simulation einer Kerberos-Session mit Zugriff auf einen Fileserver
 /* Server-Klasse
 */

import java.util.*;
import java.io.*;

public class Server extends Object {

	private final long fiveMinutesInMillis = 300000; // 5 Minuten in
														// Millisekunden

	private String myName; // Konstruktor-Parameter
	private KDC myKDC; // wird bei KDC-Registrierung gespeichert
	private long myKey; // wird bei KDC-Registrierung gespeichert

	// Konstruktor
	public Server(String name) {
		myName = name;
	}

	public String getName() {
		return myName;
	}

	public void setupService(KDC kdc) {
		// Anmeldung des Servers beim KDC
		myKDC = kdc;
		myKey = myKDC.serverRegistration(myName);
		System.out.println("Server " + myName
				+ " erfolgreich registriert bei KDC " + myKDC.getName()
				+ " mit ServerKey " + myKey);
	}

	public boolean requestService(Ticket srvTicket, Auth srvAuth,
		String command, String parameter) {
		System.out.println("---------------REQUEST SERVICE START---------------");

		// Serverticket prüfen
		if (srvTicket == null) {
			System.out.println("ServerTicket ungültig");
			return false;
		}

		// Serverticket entschlüsseln und prüfen
		srvTicket.decrypt(myKey);
		if (!timeValid(srvTicket.getStartTime(), srvTicket.getEndTime())) {
			System.out.println("Server Ticket abgelaufen");
			return false;
		}
		if (!srvTicket.getServerName().equals(myName)) {
			System.out.println("Server Ticket ungültiger Name");
			return false;
		}

		// Authorization entschlüsseln und prüfen
		srvAuth.decrypt(srvTicket.getSessionKey());
		if (!srvTicket.getClientName().equals(srvAuth.getClientName())) {
            System.out.println("Client Name doesn't match Server Ticket and Auth");
			return false;
        }
		if (!timeFresh(srvAuth.getCurrentTime())) {
			System.out.println("Server Ticket abgelaufen");
			return false;
		}

		System.out.println("---------------REQUEST SERVICE: AUTH---------------");
		srvAuth.print();
		System.out.println();
		System.out.println("---------------REQUEST SERVICE: TICKET---------------");
		srvTicket.print();
		System.out.println();
		System.out.println();

		// showFile ausführen
		if (command.equals("showFile")) {
			showFile(parameter);
		}

		System.out.println("---------------REQUEST SERVICE END---------------");
		return true;
	}

	/* *********** Services **************************** */

	private boolean showFile(String filePath) {
		/*
		 * Angegebene Datei auf der Konsole ausgeben. Rückgabe: Status der
		 * Operation
		 */
		String lineBuf = null;
		File myFile = new File(filePath);
		boolean status = false;

		if (!myFile.exists()) {
			System.out.println("Datei " + filePath + " existiert nicht!");
		} else {
			try {
				// Datei öffnen und zeilenweise lesen
				BufferedReader inFile = new BufferedReader(
						new InputStreamReader(new FileInputStream(myFile)));
				lineBuf = inFile.readLine();
				while (lineBuf != null) {
					System.out.println(lineBuf);
					lineBuf = inFile.readLine();
				}
				inFile.close();
				status = true;
			} catch (IOException ex) {
				System.out.println("Fehler beim Lesen der Datei " + filePath
						+ ex);
			}
		}
		return status;
	}

	/* *********** Hilfsmethoden **************************** */

	private boolean timeValid(long lowerBound, long upperBound) {
		/*
		 * Wenn die aktuelle Zeit innerhalb der übergebenen Zeitgrenzen liegt,
		 * wird true zurückgegeben
		 */

		long currentTime = (new Date()).getTime(); // Anzahl mSek. seit 1.1.1970
		if (currentTime >= lowerBound && currentTime <= upperBound) {
			return true;
		} else {
			System.out.println("-------- Time not valid: " + currentTime
					+ " not in (" + lowerBound + "," + upperBound + ")!");
			return false;
		}
	}

	boolean timeFresh(long testTime) {
		/*
		 * Wenn die übergebene Zeit nicht mehr als 5 Minuten von der aktuellen
		 * Zeit abweicht, wird true zurückgegeben
		 */
		long currentTime = (new Date()).getTime(); // Anzahl mSek. seit 1.1.1970
		if (Math.abs(currentTime - testTime) < fiveMinutesInMillis) {
			return true;
		} else {
			System.out.println("-------- Time not fresh: " + currentTime
					+ " is current, " + testTime + " is old!");
			return false;
		}
	}
}
