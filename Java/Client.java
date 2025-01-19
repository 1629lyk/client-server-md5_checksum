import java.io.*;
import java.net.*;
import java.security.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Client {
    public static void main(String[] args) {
        String serverIP = "192.168.1.105";  // Server IP
        int port = 5002;                    // Server port
        String saveFilePath = "mydata_client.txt";
        
        boolean fileReceived = false;
        String serverChecksum = null;  // We'll store the server's checksum here

        try (Socket socket = new Socket(serverIP, port)) {
            log("Connected to server " + serverIP + ":" + port);

            // 1) We use DataInputStream to read the server's checksum & file data
            try (DataInputStream dis = new DataInputStream(socket.getInputStream());
                 FileOutputStream fos = new FileOutputStream(saveFilePath);
                 BufferedOutputStream bos = new BufferedOutputStream(fos)) {

                // 2) Read the checksum first
                serverChecksum = dis.readUTF();
                log("MD5 checksum received from server: " + serverChecksum);

                // 3) Read the file data into mydata_client.txt
                byte[] buffer = new byte[4096];
                int bytesRead;

                while ((bytesRead = dis.read(buffer)) != -1) {
                    bos.write(buffer, 0, bytesRead);
                    fileReceived = true;
                }

                // The try-with-resources block ensures bos is flushed & closed here
            } catch (IOException e) {
                log("Error while receiving the file: " + e.getMessage());
            }
        } catch (UnknownHostException e) {
            log("Unknown server host: " + e.getMessage());
        } catch (IOException e) {
            log("Error connecting to server: " + e.getMessage());
        }

        // FILE IS FULLY WRITTEN AND STREAMS ARE CLOSED AT THIS POINT 
        if (fileReceived && serverChecksum != null) {
            log("File received and saved as '" + saveFilePath + "'");

            // MD5 of the fully written file
            String clientChecksum = computeMD5Checksum(saveFilePath);
            log("MD5 checksum of received file: " + clientChecksum);

            // Compare checksums
            if (serverChecksum.equals(clientChecksum)) {
                log("Checksum verification successful! File integrity verified.");
            } else {
                log("Checksum verification failed! File may be corrupted.");
            }
        } else {
            log("No file data received from the server or no server checksum available.");
        }
    }

    // Method to compute MD5 checksum
    private static String computeMD5Checksum(String fileName) {
        
        // Check the path of the file 
        // File file = new File(fileName);
        // log("Computing MD5 for file at: " + file.getAbsolutePath());
        
        try (FileInputStream fis = new FileInputStream(fileName)) {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] buffer = new byte[4096];
            int bytesRead;

            while ((bytesRead = fis.read(buffer)) != -1) {
                md.update(buffer, 0, bytesRead);
            }

            byte[] digest = md.digest();
            StringBuilder hexString = new StringBuilder();
            for (byte b : digest) {
                hexString.append(String.format("%02x", b));
            }
            return hexString.toString();
        } catch (Exception e) {
            log("Error computing MD5 checksum: " + e.getMessage());
            return null;
        }
    }

    // Logging utility
    private static void log(String message) {
        String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        System.out.println("[" + timeStamp + "] " + message);
    }
}
