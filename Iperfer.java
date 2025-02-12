import java.io.*;
import java.net.*;
import java.util.*;

public class Iperfer {
    public static void main(String[] args) {

        //All possible parameters
        String hostName = null;
        Integer port = null;
        Integer time = null;

        char mode = '\0';

        for (int i = 0; i < args.length; i++){

            //checks for serv/client
            if (args[i].equals("-s") || args[i].equals("-c")){
                if (mode != '\0'){
                    System.out.println("Error: missing or additional arguments");
                    return;
                }
                mode = args[i].charAt(1);
            }

            //checks for host name
            else if (args[i].equals("-h")) {
                i++;
                if (hostName != null || i >= args.length){
                    System.out.println("Error: missing or additional arguments");
                    return;
                }
                hostName = args[i];
            }

            //checks for time
            else if (args[i].equals("-t")) {
                i++;
                if (time != null || i >= args.length){
                    System.out.println("Error: missing or additional arguments");
                    return;
                }
                time = Integer.parseInt(args[i]);
            }

            //checks for port number
            else if(args[i].equals("-p")){
                i++;
                if (port != null || i >= args.length){
                    System.out.println("Error: missing or additional arguments");
                    return;
                }

                port = Integer.parseInt(args[i]);

                //checks if server port is in bounds
                if (port.intValue() < 1024 || port.intValue() > 65535) {
                    System.out.println("Error: port number must be in the range 1024 to 65535");
                    return;
                }
            }

            //If no statemnt catches the param then it is additional
            else {
                System.out.println("Error: missing or additional arguments");
                return;
            }
        }

        //runs server code
        if (mode == 's'){
            if (hostName != null || time != null || port == null){
                System.out.println("Error: missing or additional arguments");
                return;
            }
            runServer(port.intValue());
        }

        //runs client code
        else if (mode == 'c'){
            if (hostName == null || time == null || port == null){
                System.out.println("Error: missing or additional arguments");
                return;
            }
            runClient(hostName, port.intValue(), time.intValue());
        }

        //no mode spesified
        else{
        System.out.println("Error: missing or additional arguments");
        }
    }

    public static void runClient(String hostName, int port, int time) {
        try {
            //creates new socket and data
            Socket socket = new Socket(hostName, port);
            byte[] data = new byte[1000];

            //sends data
            long endTime = System.currentTimeMillis() + (time * 1000);
            double KB = 0;
            while (System.currentTimeMillis() < endTime) {
                socket.getOutputStream().write(data);
                KB += 1;
            }

            //calculates and displays rate
            double Mbps = (KB * 8) / (time * 1000.0);
            System.out.printf("sent=%.0f KB rate=%.3f Mbps%n", KB, Mbps);

        } catch (Exception e) {
            System.out.println("client error happened");
        }
    }

    public static void runServer(int port) {
        try {
            //creates new socket to read from
            Socket socket = new ServerSocket(port).accept();

            //intilizes vars
            byte[] buffer = new byte[1000];
            int bytesRead;
            int bytes = 0;
            long start = System.currentTimeMillis();

            //reads data
            while ((bytesRead = socket.getInputStream().read(buffer)) != -1) {
                bytes += bytesRead;
            }

            //calculates and displays rate
            double time = (System.currentTimeMillis() - start) / 1000.0;
            double KB = bytes / 1000.0;
            double Mbps = (KB * 8) / (time * 1000.0);
            System.out.printf("received=%.0f KB rate=%.3f Mbps%n", KB, Mbps);

        } catch (Exception e) {
            System.out.println("Server Error Happened");
        }
    }
}
