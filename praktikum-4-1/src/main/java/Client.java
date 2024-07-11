package main.java;

/* Simulation einer Kerberos-Session mit Zugriff auf einen Fileserver
 /* Client-Klasse
 */

import java.util.*;

public class Client extends Object {

	private KDC myKDC; // Konstruktor-Parameter

	private String currentUser; // Speicherung bei Login nötig
	private Ticket tgsTicket = null; // Speicherung bei Login nötig
	private long tgsSessionKey; // K(C,TGS) // Speicherung bei Login nötig

	// Konstruktor
	public Client(KDC kdc) {
		myKDC = kdc;
	}

	public boolean login(String userName, char[] password) {

		System.out.println("---------------LOGIN START---------------");
		// TGS Ticket anfordern
		long nonce1 = generateNonce();
		TicketResponse tgsTicketResponse = myKDC.requestTGSTicket(userName, "myTGS", nonce1);
		if (tgsTicketResponse == null) {
			System.out.println("Fehler beim Anfordern des TGS-Tickets für Benutzer " + userName);
			return false;
		}

		// TGS Ticket entschlüsseln und prüfen
		long pwKey = generateSimpleKeyFromPassword(password);
		if (!tgsTicketResponse.decrypt(pwKey)) {
			System.out.println("Fehler beim Entschlüsseln des TGS-Tickets für Benutzer " + userName);
			return false;
		}
		if (tgsTicketResponse.getNonce() != nonce1) {
			System.out.println("TGS Ticket Nonce nicht korrekt");
			return false;
		}

		// Ticket printen und Werte speichern
		System.out.println("---------------LOGIN: TicketResponse---------------");
		tgsTicketResponse.print();
		System.out.println();
		tgsTicket = tgsTicketResponse.getResponseTicket();
		currentUser = userName;
		tgsSessionKey = tgsTicketResponse.getSessionKey();


		System.out.println("---------------LOGIN END---------------");
		return true;
	}

	public boolean showFile(Server fileServer, String filePath) {
		System.out.println("---------------SHOW-FILE START---------------");
		boolean serviceOK = false;
		long currentTime = (new Date()).getTime();

		// Login prüfen: tgs-Ticket vorhanden?
		if (tgsTicket == null) {
			System.out.println("Keine gültige Anmeldung! Bitte Login vornehmen!");
			return false;
		}

		long nonce2 = generateNonce();
		// Authentifikation erzeugen und verschlüsseln
		Auth clientAuth = new Auth(currentUser,currentTime);
		clientAuth.encrypt(tgsSessionKey);

		// Neues Serverticket beim TGS anfordern
		TicketResponse srvTicketResponse = myKDC.requestServerTicket(tgsTicket,clientAuth,fileServer.getName(),nonce2);
		if (srvTicketResponse != null) {
			// TGS-Response entschlüsseln
			if (!srvTicketResponse.decrypt(tgsSessionKey)) {
				System.out.println("TGS Session Key ist ungültig!");
				srvTicketResponse = null;
			}
		} else {
			System.out.println("Serverticket konnte nicht ausgestellt werden!");
		}
		// TGS-Response auswerten
		if (srvTicketResponse != null && srvTicketResponse.getNonce() == nonce2) {
			System.out.println("---------------SHOW-FILE: SRV Ticket Reponse---------------");
			srvTicketResponse.print();
		} else {
			System.out.println("Serverticket nicht korrekt!!");
			return false;
		}

		// Authentifikation erzeugen und verschlüsseln
		currentTime = (new Date()).getTime();
		Auth srvAuth = new Auth(currentUser,currentTime);
		srvAuth.encrypt(srvTicketResponse.getSessionKey());

		// Service Request starten
		serviceOK = fileServer.requestService(srvTicketResponse.getResponseTicket(),srvAuth,"showFile",filePath);

		System.out.println("---------------SHOW-FILE END---------------");
		return serviceOK;
	}

	/* *********** Hilfsmethoden **************************** */

	private long generateSimpleKeyFromPassword(char[] passwd) {
		// Liefert einen eindeutig aus dem Passwort abgeleiteten Schlüssel
		// zurück, hier simuliert als long-Wert
		long pwKey = 0;
		if (passwd != null) {
			for (int i = 0; i < passwd.length; i++) {
				pwKey = pwKey + passwd[i];
			}
		}
		return pwKey;
	}

	private long generateNonce() {
		// Liefert einen neuen Zufallswert
		long rand = (long) (100000000 * Math.random());
		return rand;
	}
}
