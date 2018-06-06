package m13;

import java.io.*;
import java.net.*;

/**
 * Simple Server for the Predators and Preys project.
 * 
 * <p>
 * The server first waits for a single TCP connection from a client, then will
 * read two integer values from the client, in binary format. The first integer
 * correspond to the size (in bytes) of extended prey data, the second to the
 * size of extended predator data. Extended creature data is additional
 * information transmitted after the mandatory (X,Y) position of the creature.
 * E.g: if your client sends an additional double value for each prey (e.g
 * direction), then the first integer sent at the beginning of the connection
 * must be 8 (eight bytes for the additional double value). If your client sends
 * an additional double value plus an int (e.g age) for each predator, then the
 * second integer sent at the beginning of the connection must be 12 (eight
 * bytes for a double plus four bytes for an int).
 * <P>
 * The server will then wait for messages from the client and respond to it in
 * an infinite loop, until the connection is broken by the client. A message is
 * typically sent by a client after each cycle of simulation.
 * <p>
 * The expected format of each message sent by the client is the following :
 * four integer values followed by two lists of creature data (one list for the
 * exiting preys, the second for the exiting predators). A prey or a predator is
 * leaving the square simulation if one or both of its coordinates are outside
 * the normalized square.
 * <p>
 * The four integer values are in this order: the number of remaining preys in
 * the client square simulation, the number of remaining predators (these two
 * values are just for statistical interest, they are currently printed by the
 * server on its console), the number of exiting preys, and the number of
 * exiting predators.
 * <p>
 * Each list of creature data may be empty (if the corresponding number of
 * exiting creatures is zero). Creature data is composed of two double values
 * followed by extended additional data: the two double values are the X and Y
 * coordinates (double values), the additional data might be anything defined by
 * the client, it will not be interpreted by the server, but retransmitted
 * without modification (hence the server parameters that define the size of
 * creatures' extended additional data).
 * 
 * 
 * 
 * @author F.Frances
 *
 */
public class SimpleServer {
	private Socket socket;
	private DataInputStream input;
	private DataOutputStream output;

	/**
	 * Creates a SimpleServer.
	 * 
	 * @param port
	 *            the TCP port on which the server will be waiting for a connection.
	 * @throws IOException
	 *             if an error occurs when opening or closing the server socket, or
	 *             when waiting for a connection, or when working with the streams
	 *             associated with the accepted connection (creation of the input
	 *             and output stream).
	 * 
	 */
	public SimpleServer(int port) throws IOException {
		ServerSocket listenSocket = new ServerSocket(port);
		this.socket = listenSocket.accept();
		this.input = new DataInputStream(socket.getInputStream());
		this.output = new DataOutputStream(socket.getOutputStream());
		listenSocket.close();
	}

	private void echoList(int length, int extDataSize) throws IOException {
		for (int i = 0; i < length; i++) {
			double x = input.readDouble();
			double y = input.readDouble();
			output.writeDouble(x - Math.floor(x));
			output.writeDouble(y - Math.floor(y));
			for (int n = 0; n < extDataSize; n++)
				output.write(input.read());
		}
	}

	/**
	 * Runs the server: the server will first read two parameters from the client,
	 * then cyclically exchange data with it. Please read the specification of the
	 * data exchanges in the provided protocol definition. This simple server will
	 * have the creatures re-enter the client simulation, by adjusting their
	 * coordinates as if the normalized square was mapped to a torus.
	 */
	public void run() {
		try {
			int extPreyLength = input.readInt();
			int extPredLength = input.readInt();

			while (true) {
				int preys = input.readInt(), preds = input.readInt();
				System.out.print("\rRemaining Preys|Predators: " + preys + ":" + preds);
				int exitPreys = input.readInt(), exitPreds = input.readInt();
				// exiting Preys and Predators are re-entered back
				output.writeInt(exitPreys);
				output.writeInt(exitPreds);
				echoList(exitPreys, extPreyLength); // list of exiting preys
				echoList(exitPreds, extPredLength); // list of exiting predators
				output.flush();
			}
		} catch (IOException e) {
			System.out.println();
			System.err.println("Server terminated.");
		}
	}

	private static void usage(String msg) {
		System.err.println(msg);
		System.err.println();
		System.err.println("Usage: java fr.isae.project2017.SimpleServer [<port>]");
		System.err.println();
		System.err.println("where <port> is a decimal value for the TCP port of the server (default: 6789).");
		System.exit(1);
	}

	/**
	 * Sample main program. This creates a SimpleServer and runs it. Run this
	 * program and optionally provide the TCP port number as a command line argument
	 * (port 6789 will be used if no argument is provided), e.g:
	 * <p>
	 * java fr.isae.project2017.SimpleServer 6502
	 * <p>
	 * 
	 * @param args
	 *            the arguments on the command line. Should be either an empty array
	 *            of strings, or an array of a single string giving the decimal
	 *            representation of the port number. If the array is empty, default
	 *            value of 6789 will be used for the TCP port.
	 */
	public static void main(String[] args) {
		if (args.length > 1)
			usage("Bad number of arguments");
		SimpleServer server = null;
		try {
			int port = args.length == 1 ? Integer.parseInt(args[0]) : 6789;
			server = new SimpleServer(port);
		} catch (IOException e) {
			usage(e.getMessage());
		}
		server.run();
	}
}
