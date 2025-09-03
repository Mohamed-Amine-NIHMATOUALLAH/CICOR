package com.example.cicor.database;

import java.sql.Connection;
import java.sql.SQLException;

public class TestConnection {
    public static void main(String[] args) {
        testMySQLConnection();
    }

    public static void testMySQLConnection() {
        System.out.println("=== Début du test de connexion MySQL ===");

        DatabaseManager dbManager = new DatabaseManager();

        try (Connection conn = dbManager.getConnection()) {
            if (conn != null && !conn.isClosed()) {
                System.out.println("✅ Connexion réussie à MySQL!");
                System.out.println("URL: " + DatabaseManager.DB_URL);
                System.out.println("Utilisateur: " + DatabaseManager.USER);

                // Test supplémentaire
                System.out.println("\n🔍 Métadonnées de la connexion:");
                System.out.println("Base de données: " + conn.getCatalog());
                System.out.println("Version MySQL: " + conn.getMetaData().getDatabaseProductVersion());
            } else {
                System.out.println("❌ La connexion a échoué (objet Connection null ou fermé)");
            }
        } catch (SQLException e) {
            System.err.println("\n❌ Erreur de connexion :");
            System.err.println("Message: " + e.getMessage());
            System.err.println("Code d'erreur SQL: " + e.getErrorCode());
            System.err.println("État SQL: " + e.getSQLState());

            // Diagnostic des problèmes courants
            diagnoseConnectionIssue(e);
        }

        System.out.println("=== Fin du test ===");
    }

    private static void diagnoseConnectionIssue(SQLException e) {
        System.err.println("\n🔧 Diagnostic:");

        if (e.getMessage().contains("Access denied")) {
            System.err.println("- Problème d'authentification");
            System.err.println("- Vérifiez le nom d'utilisateur/mot de passe dans DatabaseManager");
            System.err.println("- Testez ces identifiants dans phpMyAdmin");
        }
        else if (e.getMessage().contains("Unknown database")) {
            System.err.println("- La base de données 'cicor_db' n'existe pas");
            System.err.println("- Créez-la dans phpMyAdmin ou modifiez DB_URL");
        }
        else if (e.getMessage().contains("Communications link failure")) {
            System.err.println("- MySQL n'est pas démarré ou inaccessible");
            System.err.println("- Vérifiez que XAMPP est lancé avec MySQL");
            System.err.println("- Vérifiez que le port 3306 n'est pas bloqué");
        }
        else if (e.getMessage().contains("No suitable driver")) {
            System.err.println("- Le driver JDBC MySQL n'est pas trouvé");
            System.err.println("- Vérifiez la dépendance dans pom.xml");
            System.err.println("- Si vous n'utilisez pas Maven, ajoutez manuellement le fichier JAR");
        }
    }
}