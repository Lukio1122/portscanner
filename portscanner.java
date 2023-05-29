import java.io.IOException;
import java.net.Socket;

public class PortScanner {
    public static void scanPorts(String targetHost, int startPort, int endPort) {
        try {
            // Resolve target hostname to IP address
            String targetIp = java.net.InetAddress.getByName(targetHost).getHostAddress();

            // Scan ports within the specified range
            for (int port = startPort; port <= endPort; port++) {
                try (Socket socket = new Socket(targetIp, port)) {
                    System.out.println("Port " + port + ": Open");
                } catch (IOException e) {
                    System.out.println("Port " + port + ": Closed");
                }
            }
        } catch (java.net.UnknownHostException e) {
            System.out.println("Hostname could not be resolved.");
        }
    }

    public static void main(String[] args) {
        java.util.Scanner scanner = new java.util.Scanner(System.in);
        
        System.out.print("Enter the target hostname or IP address: ");
        String targetHost = scanner.nextLine();
        
        System.out.print("Enter the starting port number: ");
        int startPort = scanner.nextInt();
        
        System.out.print("Enter the ending port number: ");
        int endPort = scanner.nextInt();
        
        scanPorts(targetHost, startPort, endPort);
    }
}
