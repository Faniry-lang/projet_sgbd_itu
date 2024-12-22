import java.io.*;
import java.nio.file.*;
import javax.swing.*;
import java.awt.*;
import javax.swing.border.EmptyBorder;

public class InstallationApp {

    private JFrame window;
    private JPanel panel1, panel2, panel3;
    private JTextField folderPathField, originalPortField, replicaPortField;
    private String folderPath = "C:\\LightSql"; 
    private int originalPort = 3000;  
    private int replicaPort = 3001; 

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new InstallationApp().createAndShowGUI());
    }

    public void createAndShowGUI() {
        window = new JFrame("Installation de LightSql");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setSize(400, 300);
        window.setLocationRelativeTo(null);  // Centrer la fenêtre

        // Créer un padding autour de la fenêtre
        JPanel paddingPanel = new JPanel();
        paddingPanel.setLayout(new BorderLayout());
        paddingPanel.setBorder(new EmptyBorder(20, 20, 20, 20)); // Padding de 20px autour de la fenêtre

        // Premier panneau : Présentation de LightSql
        panel1 = new JPanel();
        panel1.setLayout(new BoxLayout(panel1, BoxLayout.Y_AXIS));
        JLabel introLabel = new JLabel("LightSql");
        introLabel.setFont(new Font("Arial", Font.BOLD, 24));
        JTextArea descriptionText = new JTextArea("LightSql est un système de gestion de base de données conçu avec Java, " +
                                                 "qui utilise des fichiers JSON pour le stockage des données, offrant une solution " +
                                                 "légère et flexible pour gérer des bases de données simples.\n" +
                                                 "Ce programme vous guide à travers l'installation.\n" +
                                                 "Cliquez 'Suivant' pour commencer.");
        descriptionText.setWrapStyleWord(true);
        descriptionText.setLineWrap(true);
        descriptionText.setOpaque(false);
        descriptionText.setEditable(false);

        JButton nextButton1 = new JButton("Suivant");
        nextButton1.addActionListener(e -> showFolderSelection()); // Passer à l'écran suivant

        panel1.add(introLabel);
        panel1.add(descriptionText);
        panel1.add(nextButton1);
        
        // Deuxième panneau : Choisir le dossier d'installation
        panel2 = new JPanel();
        panel2.setLayout(new BoxLayout(panel2, BoxLayout.Y_AXIS));
        folderPathField = new JTextField(folderPath, 20);
        folderPathField.setPreferredSize(new Dimension(250, 30));  // Hauteur réduite pour le champ
        folderPathField.setMaximumSize(new Dimension(250, 30)); // Hauteur réduite pour le champ
        folderPathField.setToolTipText("Chemin d'installation");

        JButton nextButton2 = new JButton("Suivant");
        nextButton2.addActionListener(e -> showPortSelection()); // Passer à l'écran suivant

        panel2.add(new JLabel("Sélectionnez le dossier d'installation:"));
        panel2.add(folderPathField);
        panel2.add(nextButton2);

        // Troisième panneau : Choisir les ports
        panel3 = new JPanel();
        panel3.setLayout(new BoxLayout(panel3, BoxLayout.Y_AXIS));
        originalPortField = new JTextField(String.valueOf(originalPort), 20);
        originalPortField.setPreferredSize(new Dimension(250, 30));  // Hauteur réduite pour le champ
        originalPortField.setMaximumSize(new Dimension(250, 30)); // Hauteur réduite pour le champ
        originalPortField.setToolTipText("Port base originale");

        replicaPortField = new JTextField(String.valueOf(replicaPort), 20);
        replicaPortField.setPreferredSize(new Dimension(250, 30));  // Hauteur réduite pour le champ
        replicaPortField.setMaximumSize(new Dimension(250, 30)); // Hauteur réduite pour le champ
        replicaPortField.setToolTipText("Port base répliquée");

        JButton finishButton = new JButton("Terminer");
        finishButton.addActionListener(e -> finishInstallation()); // Fin de l'installation

        panel3.add(new JLabel("Sélectionnez les ports de base de données:"));
        panel3.add(originalPortField);
        panel3.add(replicaPortField);
        panel3.add(finishButton);

        // Ajouter les panneaux au paddingPanel
        paddingPanel.add(panel1, BorderLayout.CENTER);
        window.setContentPane(paddingPanel);
        
        window.setVisible(true);
    }

    // Fonction pour passer au deuxième panneau (choix du dossier d'installation)
    private void showFolderSelection() {
        window.getContentPane().removeAll();
        window.getContentPane().add(panel2);
        window.revalidate();
        window.repaint();
    }

    // Fonction pour passer au troisième panneau (choix des ports)
    private void showPortSelection() {
        folderPath = folderPathField.getText();
        window.getContentPane().removeAll();
        window.getContentPane().add(panel3);
        window.revalidate();
        window.repaint();
    }

    // Fonction pour terminer l'installation et créer les dossiers/fichiers
    private void finishInstallation() {
        // Récupère les valeurs des ports saisis
        try {
            originalPort = Integer.parseInt(originalPortField.getText());
            replicaPort = Integer.parseInt(replicaPortField.getText());
        } catch (NumberFormatException e) {
            showAlert("Erreur", "Veuillez entrer des numéros valides pour les ports.");
            return; // Ne permet pas de passer à la scène suivante
        }

        // Crée la structure des dossiers et fichiers
        if (!createDirectoriesAndFiles(folderPath)) {
            return; // Si une erreur est survenue dans la création des dossiers/fichiers, ne pas continuer
        }

        FirewallConfigurator.configurePort(originalPort);
        FirewallConfigurator.configurePort(replicaPort);

        // Affiche un message de confirmation
        JOptionPane.showMessageDialog(window, "L'installation est réussie.\nLe programme sera installé dans : " + folderPath +
                "\nPort original : " + originalPort +
                "\nPort répliqué : " + replicaPort);
        window.dispose();  // Ferme la fenêtre après confirmation
    }

    // Fonction pour créer les dossiers et fichiers nécessaires
    private boolean createDirectoriesAndFiles(String path) {
        try {
            // Création des dossiers
            Path rootPath = Paths.get(path);
            Files.createDirectories(rootPath.resolve("conf"));
            Files.createDirectories(rootPath.resolve("db"));
            Files.createDirectories(rootPath.resolve("log"));
            Files.createDirectories(rootPath.resolve("replica"));
            Files.createDirectories(rootPath.resolve("bin"));
            Files.createDirectories(rootPath.resolve("lib"));

            // Création des fichiers
            Files.createFile(rootPath.resolve("conf").resolve("config.txt"));
            Files.createFile(rootPath.resolve("log").resolve("log.txt"));

            // Écriture dans le fichier config.txt
            try (BufferedWriter writer = Files.newBufferedWriter(rootPath.resolve("conf").resolve("config.txt"))) {
                writer.write("# Fichier de configuration des bases\n");
                // Utilise le chemin complet que l'utilisateur a entré pour configurer les bases
                writer.write(originalPort + ",original," + path + "\\db\n");
                writer.write(replicaPort + ",replica," + path + "\\replica\n");
            }

            // Extraire et copier les fichiers lightsql.bat et lightsql.jar
            copyResourceToFile("bin/lsql_server.bat", rootPath.resolve("bin").resolve("lsql_server.bat"));
            copyResourceToFile("bin/lsql_replica.bat", rootPath.resolve("bin").resolve("lsql_replica.bat"));
            copyResourceToFile("bin/lsql_client.bat", rootPath.resolve("bin").resolve("lsql_client.bat"));
            
            copyResourceToFile("lib/lsql_server.jar", rootPath.resolve("lib").resolve("lsql_server.jar"));
            copyResourceToFile("lib/lsql_replica.jar", rootPath.resolve("lib").resolve("lsql_replica.jar"));
            copyResourceToFile("lib/lsql_client.jar", rootPath.resolve("lib").resolve("lsql_client.jar"));



            copyResourceToFile("lib/jackson-core-2.11.1.jar", rootPath.resolve("lib").resolve("jackson-core-2.11.1.jar"));
            copyResourceToFile("lib/jackson-databind-2.11.1.jar", rootPath.resolve("lib").resolve("jackson-databind-2.11.1.jar"));
            copyResourceToFile("lib/jackson-annotations-2.11.1.jar", rootPath.resolve("lib").resolve("jackson-annotations-2.11.1.jar"));

            copyResourceToFile("lib/SGBD-1.0-SNAPSHOT.jar", rootPath.resolve("lib").resolve("SGBD-1.0.SNAPSHOT.jar"));

            return true; // Si tout a réussi
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors de la création des dossiers et fichiers.");
            return false; // Erreur pendant la création des fichiers
        }
    }

    private void copyResourceToFile(String resource, Path destination) {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(resource);
             OutputStream os = Files.newOutputStream(destination)) {
    
            if (is == null) {
                showAlert("Erreur", "Le fichier " + resource + " est manquant.");
                return;
            }
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
        } catch (IOException e) {
            showAlert("Erreur", "Impossible de copier le fichier " + resource + ".");
        }
    }
    
    // Fonction pour afficher une alerte
    private void showAlert(String title, String message) {
        JOptionPane.showMessageDialog(window, message, title, JOptionPane.ERROR_MESSAGE);
    }
}
