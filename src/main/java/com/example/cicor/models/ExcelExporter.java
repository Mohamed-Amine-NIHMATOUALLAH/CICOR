package com.example.cicor.models;

import com.example.cicor.database.ArticleDAO;
import com.example.cicor.database.CategoryDAO;
import com.example.cicor.models.Cardboard;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ExcelExporter {

    public static void exportCardboardToExcel(Cardboard cardboard, String filePath) throws IOException {
        ArticleDAO articleDAO = new ArticleDAO();
        CategoryDAO categoryDAO = new CategoryDAO();
        List<Article> articles = articleDAO.getAllArticlesOfCardboard(cardboard.getCartonNumber());

        // Créer un nouveau workbook Excel
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Carton_" + cardboard.getCartonNumber());

        // Style pour les en-têtes
        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);

        // Remplir les informations du carton
        int rowNum = 0;
        Row row = sheet.createRow(rowNum++);
        createLabelCell(row, 0, "Article :", headerStyle);
        createValueCell(row, 1, cardboard.getCategoryName());

        row = sheet.createRow(rowNum++);
        createLabelCell(row, 0, "Numéro de carton:", headerStyle);
        createValueCell(row, 1, cardboard.getCartonNumber());

        row = sheet.createRow(rowNum++);
        createLabelCell(row, 0, "Date de fabrication:", headerStyle);
        createValueCell(row, 1, cardboard.getManufactureDate());

        row = sheet.createRow(rowNum++);
        createLabelCell(row, 0, "Quantité:", headerStyle);
        createValueCell(row, 1, String.valueOf(cardboard.getQuantity()));

        // Ligne vide
        rowNum++;

        // En-têtes des articles
        row = sheet.createRow(rowNum++);
        createHeaderCell(row, 0, "Numéro de série produit (adresse MAC Bluetooth)", headerStyle);
        createHeaderCell(row, 1, "Version matérielle", headerStyle);
        createHeaderCell(row, 2, "Version logicielle", headerStyle);
        createHeaderCell(row, 3, "Remarques", headerStyle);

        // Remplir les articles
        for (Article article : articles) {
            row = sheet.createRow(rowNum++);
            createValueCell(row, 0, article.getMacAddress());
            createValueCell(row, 1, article.getHardwareVersion());
            createValueCell(row, 2, article.getSoftwareVersion());
            createValueCell(row, 3, article.getRemark());
        }
        // Auto-size les colonnes
        for (int i = 0; i < 4; i++) {
            sheet.autoSizeColumn(i);
        }

        // Générer un nom de fichier avec la date/heure
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
            workbook.write(outputStream);
        }
        workbook.close();
    }

    private static void createLabelCell(Row row, int column, String value, CellStyle style) {
        Cell cell = row.createCell(column);
        cell.setCellValue(value);
        if (style != null) {
            cell.setCellStyle(style);
        }
    }

    private static void createValueCell(Row row, int column, String value) {
        row.createCell(column).setCellValue(value);
    }

    private static void createHeaderCell(Row row, int column, String value, CellStyle style) {
        Cell cell = row.createCell(column);
        cell.setCellValue(value);
        cell.setCellStyle(style);
    }
}