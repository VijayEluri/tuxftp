package de.tuxftp.sockets.socketMessages;

import java.io.IOException;
import java.util.StringTokenizer;

import de.tuxftp.debugging.PassivModeException;
import de.tuxftp.sockets.MessageSocket;


/**
 * @author Tobias Letschka
 * this class handle modes for ...
 *
 */
public class ServerDataAnswer {

	private int port  = 0;
	private String serverIP = "";
	private MessageSocket msgSocket;
	
	public ServerDataAnswer(MessageSocket msgSocket) {
		this.msgSocket = msgSocket;
	}


	public int getRETURN_PORT() {
		return port;
	}

	public String getRETURN_IP() {
		return serverIP;
	}


//	/**
//	 * non static because every object needs an extra datasocket
//	 * 
//	 * @return
//	 */
//	public FtpDataSocket setPasvMode() {
//		msgSocket.output().println("PASV"); // send command PASV
//		ftpServMsg.readPasvAnswer(); // read answer from server
//		FtpDataSocket data = new FtpDataSocket(ftpServMsg.getRETURN_IP(),
//				ftpServMsg.getRETURN_PORT()); // start data socket
//		// through reading get ip and port
//		return data;
//	}
	/*
	 * read the server answer and find transfer ip and port
	 */
	public void readPasvAnswer() throws PassivModeException {
		while (true) {									// endless loop
			try {										// if any byte is available (start)
				int counter;
				if ((counter = msgSocket.getSocketInput().available()) != 0) {
					int buffersize = msgSocket.getSocketInput().available();
					char buffer[] = new char[buffersize];
					// read bytes till 0 byte are available (end)
					while ((counter = msgSocket.getSocketInput().available()) != 0) {
						int count = 0;
						count = msgSocket.input().read(buffer, 0, buffersize);
						System.out.println("< Server: ");
						String fromServer = new String(buffer, 0, count);
						System.out.println(fromServer);
						
						analysePASVstring(fromServer);
					}
					try {
						Thread.sleep(100);
						break;
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}

	}

	private void analysePASVstring(String input) throws PassivModeException {
		String substring = input.substring(0, 3);

	if (substring.equals("227") == true) { // if found message who starts
												// with 227...

			System.out.println("Pasmod gefunden");
			System.out.println("zu untersuchtender String: ");
			System.out.println(input);
			System.out.println("Ende");

			// analyse string server answer for pasv-mode
			// 227 Entering Passive Mode (195,71,9,196,177,13)

			StringTokenizer leftside = new StringTokenizer(input, "(");
			if (leftside.hasMoreTokens()) {
				String leftsubstring = leftside.nextToken();
				// System.out.println(leftsubstring);
				StringTokenizer rightside = new StringTokenizer(input, ")");
				if (rightside.hasMoreTokens()) {
					String rightsubstring = rightside.nextToken();
					// System.out.println(rightsubstring);
					String tmp = rightsubstring.replaceAll(leftsubstring, "");
					String ipPort;
					ipPort = tmp.substring(1); // cut first character
					System.out.println(ipPort);
					String ip[] = ipPort.split(",", 6);
					serverIP = ip[0] + "." + ip[1] + "." + ip[2] + "." + ip[3];
					String highBit = ip[4];
					String lowBit = ip[5];

					port = Integer.parseInt(highBit) * 256
							+ Integer.parseInt(lowBit);

					System.out.println("Die Ip ist " + serverIP);
					System.out.println("Port: höchstwertiger Bit: " + highBit
							+ " Port:niederwertiger bit: " + lowBit);
					System.out.println("Berechneter Port: " + port);

				}
			}

		} else {
			throw new PassivModeException("Exception in ServerAnswerData at PassivMode calulation ");
		}
	}

}
