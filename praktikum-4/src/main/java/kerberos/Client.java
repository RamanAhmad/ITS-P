package kerberos;

/* Simulation einer Kerberos-Session mit Zugriff auf einen Fileserver
 /* Client-Klasse
 */

import java.util.*;

public class Client extends Object {

    private KDC myKDC; // Konstruktor-Parameter
    private Server myFileserver;

    private String currentUser; // Speicherung bei Login nötig
    private Ticket tgsTicket = null; // Speicherung bei Login nötig
    private Ticket serverTicket = null; // Speicherung bei Login nötig
    private long tgsSessionKey; // K(C,TGS) // Speicherung bei Login nötig

    // Konstruktor
    public Client(KDC kdc, Server server) {
        myKDC = kdc;
        myFileserver = server;

    }

    public boolean login(String userName, char[] password) {
        // default-Rueckgabe
        boolean ret = false;
        long nonce1 = generateNonce();

        // Beim KDC anmelden und TGS-Ticket holen
        currentUser = userName;
        TicketResponse tgsResponse = myKDC.requestTGSTicket(userName, "myTGS",
                nonce1);

        System.out.println("Client hat TGS-Antwort bekommen: ");

        if (tgsResponse != null) {
            // TGS-Response entschlüsseln
            if (tgsResponse.decrypt(generateSimpleKeyFromPassword(password))) {
                tgsResponse.print();
            } else {
                System.err.println("Passwort ist falsch!");
                // Antwort ungueltig machen
                tgsResponse = null;
            }
        } else {
            System.out.println("TGS-Ticket konnte nicht ausgestellt werden!");
        }

        // TGS-Response auswerten
        if (tgsResponse != null && tgsResponse.getNonce() == nonce1) {
            // Alles korrekt, Entschlüsselung ok
            ret = true;
            // TGS-Session key und TGS-Ticket speichern
            tgsSessionKey = tgsResponse.getSessionKey();
            tgsTicket = tgsResponse.getResponseTicket();
        }

        Arrays.fill(password, ' ');
        return ret;
    }

    public boolean showFile(Server fileServer, String filePath) {
		boolean serviceOK = false;
		long currentTime = (new Date()).getTime();

		// Login prüfen: tgs-Ticket vorhanden?
		if (tgsTicket == null) {
			System.out.println("Keine gültige Anmeldung! Bitte Login vornehmen!");
			return false;
		}

		// Serverticket holen
		if (serverTicket  == null) {
			long nonce2 = generateNonce();
			// Authentifikation erzeugen und verschlüsseln
			Auth tgsAuth = new Auth(currentUser, currentTime);
			tgsAuth.encrypt(tgsSessionKey);
			// Neues Serverticket beim TGS anfordern
			TicketResponse srvResponse = myKDC.requestServerTicket(tgsTicket,
					tgsAuth, myFileserver.getName(), nonce2);

			System.out.println("Client hat Antwort mit Serverticket bekommen: ");
			if (srvResponse != null) {
				// TGS-Response entschlüsseln
				if (srvResponse.decrypt(tgsSessionKey)) {
					srvResponse.print();
				} else {
					System.err.println("TGS Session Key ist ungültig!");
					srvResponse = null;
				}
			} else {
				System.out.println("Serverticket konnte nicht ausgestellt werden!");
			}
			// TGS-Response auswerten
			if (srvResponse != null && srvResponse.getNonce() == nonce2) {
				// Entschlüsselung ok
				// Server-Session key und Server-Ticket speichern
				tgsSessionKey = srvResponse.getSessionKey();
				tgsTicket = srvResponse.getResponseTicket();
			} else {
				System.out.println("Serverticket nicht korrekt!!");
				return false;
			}
		}

		// Authentifikation erzeugen und verschlüsseln
		Auth srvAuth = new Auth(currentUser, currentTime);
		srvAuth.encrypt(tgsSessionKey);
		// Service beim Server anfordern
		serviceOK = myFileserver.requestService(tgsTicket, srvAuth,
				"showFile", filePath);

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
