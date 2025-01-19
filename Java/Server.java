import java.io.*;
import java.net.*;
import java.security.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Server {
    public static void main(String[] args) {
        int port = 5002; // Server port
        String fileName = "mydata.txt"; // File to be sent

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            log("Server is listening on port " + port);

            while (true) {
                Socket socket = serverSocket.accept();
                String clientAddress = socket.getInetAddress().getHostAddress();
                int clientPort = socket.getPort();
                log("Connection established with " + clientAddress + ":" + clientPort);

                try (DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
                     FileInputStream fis = new FileInputStream(fileName);
                     BufferedInputStream bis = new BufferedInputStream(fis)) {

                    File file = new File(fileName);
                    if (!file.exists()) {
                        log("File not found: " + fileName);
                        dos.writeUTF("Error: File not found");
                        dos.flush();
                        continue;
                    }

                    // Compute MD5 checksum of the file
                    String checksum = computeMD5Checksum(fileName);
                    log("MD5 checksum of the file: " + checksum);

                    // Send checksum to the client (as UTF)
                    dos.writeUTF(checksum);
                    dos.flush();

                    // Send the file data until EOF
                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = bis.read(buffer)) != -1) {
                        dos.write(buffer, 0, bytesRead);
                    }
                    dos.flush();
                    log("File '" + fileName + "' sent successfully to " + clientAddress + ":" + clientPort);

                } catch (IOException e) {
                    log("Error while sending the file: " + e.getMessage());
                } finally {
                    socket.close();
                }
            }
        } catch (IOException e) {
            log("Server error: " + e.getMessage());
        }
    }

    // Method to compute MD5 checksum
    private static String computeMD5Checksum(String fileName) {
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
