import java.io.*;

public class FirewallConfigurator {

    public static void configurePort(int port) {
        String os = System.getProperty("os.name").toLowerCase();
        try {
            if (os.contains("win")) {
                String commandIn = "netsh advfirewall firewall add rule name=\"AllowPort\" protocol=TCP dir=in localport=" + port + " action=allow";
                String commandOut = "netsh advfirewall firewall add rule name=\"AllowPort\" protocol=TCP dir=out localport=" + port + " action=allow";

                executeCommand(commandIn);
                executeCommand(commandOut);
                System.out.println("Port " + port + " opened for both incoming and outgoing traffic on Windows.");
            } else {
                System.out.println("Unsupported operating system.");
            }
        } catch (IOException e) {
            System.err.println("Error executing command: " + e.getMessage());
        }
    }

    private static void executeCommand(String command) throws IOException {
        Process process = Runtime.getRuntime().exec(command);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        }

        try {
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                System.err.println("Command failed with exit code " + exitCode);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
