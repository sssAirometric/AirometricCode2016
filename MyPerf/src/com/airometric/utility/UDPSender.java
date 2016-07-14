package com.airometric.utility;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;

/**
 * UDPSender is an implementation of the Sender interface, using UDP as the
 * transport protocol. The object is bound to a specified receiver host and port
 * when created, and is able to send the contents of a file to this receiver.
 * 
 */
public class UDPSender implements Sender {
	private File theFile;
	private FileInputStream fileReader;
	private DatagramSocket s;
	private int fileLength, currentPos, bytesRead, toPort;
	private byte[] msg, buffer;
	private String toHost, initReply;
	private InetAddress toAddress;

	/**
	 * Class constructor. Creates a new UDPSender object capable of sending a
	 * file to the specified address and port.
	 * 
	 * @param address
	 *            the address of the receiving host
	 * @param port
	 *            the listening port on the receiving host
	 */
	public UDPSender(InetAddress address, int port) throws IOException {
		toPort = port;
		toAddress = address;
		msg = new byte[8192];
		buffer = new byte[8192];
		s = new DatagramSocket();
		s.connect(toAddress, toPort);
	}

	/**
	 * Sends a file to the bound host. Reads the contents of the specified file,
	 * and sends it via UDP to the host and port specified at when the object
	 * was created.
	 * 
	 * @param theFile
	 *            the file to send
	 */
	public void sendFile(File theFile) throws IOException {
		// Init stuff
		fileReader = new FileInputStream(theFile);
		fileLength = fileReader.available();

		log(" -- Filename: " + theFile.getName());
		log(" -- Bytes to send: " + fileLength);

		s.setSoTimeout(1000);

		// 1. Send the filename to the receiver
		send((theFile.getName() + "::" + fileLength).getBytes());
		log(" -- Filename written : " + theFile.getName());

		// 2. Wait for a reply from the receiver
		DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
		s.receive(reply);
		// initReply = (new String(reply.getData(), 0, reply.getLength()));

		// 3. Send the content of the file
		if (new String(reply.getData(), 0, reply.getLength()).equals("OK")) {
			log("  -- Got OK from receiver - sending the file ");

			while (currentPos < fileLength) {
				// System.out.println("Will read at pos: "+currentPos);
				bytesRead = fileReader.read(msg);
				send(msg);
				// System.out.println("Bytes read: "+bytesRead);
				currentPos = currentPos + bytesRead;
			}
			log("  -- File transfer complete...");
		} else {
			log("Recieved something other than OK... exiting");
		}
	}

	void log(String msg) {
		L.debug(msg);
	}

	private void send(byte[] message) throws IOException {
		boolean isSent = false;
		while (true) {
			try {
				DatagramPacket packet = new DatagramPacket(message,
						message.length);
				s.send(packet);
				isSent = true;
				continue;
			} catch (SocketTimeoutException e) {
				throw e;
			}

		}
	}
}
