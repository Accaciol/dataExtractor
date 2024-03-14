package com.accacio.dataExtractor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

public class ExcelGenerator {

    private static String filePath = "H:\\downloads\\BiaTXT\\DadosExtraidos" + File.separator + "extracted_results.xls";

    public static void main(String[] args) {
        // Example usage:
        generateExcelFile("example_file.txt", "-23.4567", "45.6789", "BAP_123", "1234.56", "78.9", "X");
    }

    public static void generateExcelFile(String fileName, String longitude, String latitude, String bap,
            String maiorProfundidade, String temperaturaFahrenheit, String isLachenbruch) {
        Workbook workbook = new HSSFWorkbook();
        Sheet sheet = workbook.createSheet("Dados Extraídos");

        // Create header row
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("Nome do Arquivo");
        headerRow.createCell(1).setCellValue("Longitude");
        headerRow.createCell(2).setCellValue("Latitude");
        headerRow.createCell(3).setCellValue("B.A.P");
        headerRow.createCell(4).setCellValue("Maior Profundidade Alcançada");
        headerRow.createCell(5).setCellValue("Maior Temp. do Fundo do Poço (Fahrenheit)");
        headerRow.createCell(6).setCellValue("Maior Temp. do Fundo do Poço (celcius)");
        headerRow.createCell(7).setCellValue("isLACHENBRUCH/MAIOR PROF");

        // Create a data row with the received parameters
        Row dataRow = sheet.createRow(1);
        dataRow.createCell(0).setCellValue(fileName);
        dataRow.createCell(1).setCellValue(longitude);
        dataRow.createCell(2).setCellValue(latitude);
        dataRow.createCell(3).setCellValue(bap);
        dataRow.createCell(4).setCellValue(maiorProfundidade);
        dataRow.createCell(5).setCellValue(Double.parseDouble(temperaturaFahrenheit));
        dataRow.createCell(6).setCellValue(converterFahrenheitParaCelsius(Double.parseDouble(temperaturaFahrenheit)));
        dataRow.createCell(7).setCellValue(isLachenbruch);

        // Write the workbook to a file
        try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
            workbook.write(fileOut);
            System.out.println("Excel file generated successfully at: " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error generating Excel file: " + e.getMessage());
        }
    }

    private static double converterFahrenheitParaCelsius(double temperaturaFahrenheit) {
        return (temperaturaFahrenheit - 32) * 5 / 9;
    }
}
