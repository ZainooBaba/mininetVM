import java.io.*;
import java.net.*;
import java.util.Arrays;

public class Iperfer2 {
    public static void main(String[] args) {
        if (args.length == 0) {
            System.err.println("Error: missing or additional arguments");
            System.exit(1);
        }
        
        if (args[0].equals("-c")) {
            // Expected client mode arguments: -c -h <server hostname> -p <server port> -t <time>
            if (args.length != 7) {
                System.err.println("Error: missing or additional arguments");
                System.exit(1);
            }
            String serverHost = args[2];
            int port = Integer.parseInt(args[4]);
            int time = Integer.parseInt(args[6]);
            if (port < 1024 || port > 65535) {
                System.err.println("Error: port number must be in the range 1024 to 65535");
                System.exit(1);
            }
            runClient(serverHost, port, time);
        } else if (args[0].equals("-s")) {
            // Expected server mode arguments: -s -p <listen port>
            if (args.length != 3) {
                System.err.println("Error: missing or additional arguments");
                System.exit(1);
            }
            int port = Integer.parseInt(args[2]);
            if (port < 1024 || port > 65535) {
                System.err.println("Error: port number must be in the range 1024 to 65535");
                System.exit(1);
            }
            runServer(port);
        } else {
            System.err.println("Error: missing or additional arguments");
            System.exit(1);
        }
    }
    
    // Client method: connects to the server and sends data as fast as possible for 'time' seconds.
    public static void runClient(String serverHost, int port, int time) {
        try (Socket socket = new Socket(serverHost, port)) {
            OutputStream out = socket.getOutputStream();
            byte[] buffer = new byte[1000];
            // Fill the buffer with zeros (this is not strictly necessary as new byte[] is zeroed)
            Arrays.fill(buffer, (byte)0);
            
            long totalBytes = 0;
            long endTime = System.currentTimeMillis() + time * 1000L;
            
            while (System.currentTimeMillis() < endTime) {
                out.write(buffer);
                totalBytes += buffer.length;
            }
            // Ensure all data is sent
            out.flush();
            socket.close();
            
            double seconds = time;
            double kilobytes = totalBytes / 1000.0;
            double mbps = (totalBytes * 8) / (seconds * 1000000.0);
            System.out.printf("sent=%.0f KB rate=%.3f Mbps\n", kilobytes, mbps);
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
            System.exit(1);
        }
    }
    
    // Server method: listens for a connection and receives data until the client closes the connection.
    public static void runServer(int port) {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            Socket clientSocket = serverSocket.accept();
            InputStream in = clientSocket.getInputStream();
            byte[] buffer = new byte[1000];
            long totalBytes = 0;
            int bytesRead;
            long startTime = 0, endTime = 0;
            
            // Read until the client closes the connection
            while ((bytesRead = in.read(buffer)) != -1) {
                if (totalBytes == 0) {
                    // Mark start time when the first byte is received
                    startTime = System.currentTimeMillis();
                }
                totalBytes += bytesRead;
                endTime = System.currentTimeMillis();
            }
            clientSocket.close();
            
            double seconds = (endTime - startTime) / 1000.0;
            double kilobytes = totalBytes / 1000.0;
            double mbps = (totalBytes * 8) / (seconds * 1000000.0);
            System.out.printf("received=%.0f KB rate=%.3f Mbps\n", kilobytes, mbps);
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
            System.exit(1);
        }
    }
}
