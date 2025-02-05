import java.io.*;
import java.net.*;
import java.util.*;

public class Iperfer {
    public static void main(String[] args) {
        // Check if arguments are provided
        if (args.length < 2) {
            System.out.println("Error: missing or additional arguments");
            return;
        }

        // Determine mode
        if (args[0].equals("-c")) {
            if (args.length != 7 || !args[1].equals("-h") || !args[3].equals("-p") || !args[5].equals("-t")) {
                System.out.println("Error: missing or additional arguments");
                return;
            }

            String serverHost = args[2];
            int serverPort = Integer.parseInt(args[4]);
            int time = Integer.parseInt(args[6]);

            // Validate port number
            if (serverPort < 1024 || serverPort > 65535) {
                System.out.println("Error: port number must be in the range 1024 to 65535");
                return;
            }

            runClient(serverHost, serverPort, time);
        } else if (args[0].equals("-s")) {
            if (args.length != 3 || !args[1].equals("-p")) {
                System.out.println("Error: missing or additional arguments");
                return;
            }

            int serverPort = Integer.parseInt(args[2]);

            // Validate port number
            if (serverPort < 1024 || serverPort > 65535) {
                System.out.println("Error: port number must be in the range 1024 to 65535");
                return;
            }

            runServer(serverPort);
        } else {
            System.out.println("Error: missing or additional arguments");
        }
    }

    public static void runClient(String serverHost, int serverPort, int time) {
        try (Socket socket = new Socket(serverHost, serverPort);
             OutputStream out = socket.getOutputStream()) {

            byte[] data = new byte[1000]; // 1000-byte buffer
            long startTime = System.currentTimeMillis();
            long endTime = startTime + (time * 1000);
            int totalBytesSent = 0;

            while (System.currentTimeMillis() < endTime) {
                out.write(data);
                totalBytesSent += data.length;
            }

            // Convert bytes to KB and Mbps
            double totalKB = totalBytesSent / 1000.0;
            double rate = (totalBytesSent * 8.0) / (time * 1000000.0); // Mbps

            System.out.printf("sent=%.0f KB rate=%.3f Mbps%n", totalKB, rate);

        } catch (IOException e) {
            System.out.println("Error: Unable to connect to server");
        }
    }

    public static void runServer(int serverPort) {
        try (ServerSocket serverSocket = new ServerSocket(serverPort);
             Socket clientSocket = serverSocket.accept();
             InputStream in = clientSocket.getInputStream()) {

            byte[] buffer = new byte[1000];
            int bytesRead;
            int totalBytesReceived = 0;
            long startTime = System.currentTimeMillis();

            while ((bytesRead = in.read(buffer)) != -1) {
                totalBytesReceived += bytesRead;
            }

            long endTime = System.currentTimeMillis();
            double duration = (endTime - startTime) / 1000.0; // in seconds

            // Convert bytes to KB and Mbps
            double totalKB = totalBytesReceived / 1000.0;
            double rate = (totalBytesReceived * 8.0) / (duration * 1000000.0); // Mbps

            System.out.printf("received=%.0f KB rate=%.3f Mbps%n", totalKB, rate);

        } catch (IOException e) {
            System.out.println("Error: Unable to start server");
        }
    }
}
