package com.example.cicor.database;

import com.example.cicor.models.Article;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ArticleDAO {
    private static DatabaseManager dbManager = null;

    public ArticleDAO() {
        this.dbManager = new DatabaseManager();
    }

    public List<Article> getAllArticlesOfCardboard(String cartonNumber) {
        List<Article> articles = new ArrayList<>();
        String sql = """
        SELECT a.*, c.hardware_ver, c.software_ver 
        FROM Article a
        JOIN Cardboard cb ON a.carton_number = cb.carton_number
        JOIN categories c ON cb.category_id = c.id
        WHERE a.carton_number = ?""";

        try (Connection conn = dbManager.getConnection();
             var stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, cartonNumber);
            var rs = stmt.executeQuery();
            while (rs.next()) {
                Article article = new Article();
                article.setCategoryId(rs.getInt("category_id"));
                article.setcartonNumber(rs.getString("carton_number"));
                article.setMacAddress(rs.getString("MacAddress"));
                article.setRemark(rs.getString("Remark"));
                // Ajoutez ces propriétés à votre classe Article
                article.setHardwareVersion(rs.getString("hardware_ver"));
                article.setSoftwareVersion(rs.getString("software_ver"));
                articles.add(article);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return articles;
    }

    public static List<String> getAllMacAddress() {
        List<String> mac = new ArrayList<>();
        String maqsql = "SELECT MacAddress FROM Article";
        try (Connection conn = dbManager.getConnection();
             var stmt = conn.createStatement();
             var rs = stmt.executeQuery(maqsql)) {
            while (rs.next()) {
                String Mac = rs.getString("MacAddress");
                mac.add(Mac);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return mac;
    }

    public void insertArticle(Article article) {
        String sql = "INSERT INTO Article (MacAddress, carton_number, category_id) VALUES (?, ?, ?)";
        try (Connection conn = dbManager.getConnection();
             var pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, article.getMacAddress());
            pstmt.setString(2, article.getcartonNumber());
            pstmt.setInt(3, article.getCategoryId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error inserting Article: " + e.getMessage(), e);
        }
    }

    public void deleteArticle(String macAddress) {
        String sql = "DELETE FROM Article WHERE MacAddress = ?";
        try (Connection conn = dbManager.getConnection();
             var pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, macAddress);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting Article: " + e.getMessage(), e);
        }
    }
    public void AddORUpdateRemark(String macAddress, String remark) {
        String sql = "UPDATE Article SET Remark = ? WHERE MacAddress = ?";
        try (Connection conn = dbManager.getConnection();
             var pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, remark);
            pstmt.setString(2, macAddress);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating Remark: " + e.getMessage(), e);
        }
    }
}