package com.accacio.dataExtractor;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

/**
 * Hello world!
 *
 */
public class App 
{
	
	// static String jarDirectory = System.getProperty("user.dir");
	static String jarDirectory = "H:\\downloads\\BiaTXT";

	static String userHome = System.getProperty("user.home");
	static String filePath = jarDirectory + File.separator + "DadosExtraidos" + File.separator;
//		static String fileTXTPath = System.getProperty("user.home") + File.separator + "DadosExtraidos" + File.separator + "extracted_results.txt";
	static String fileTXTPath = "H:\\downloads\\BiaTXT\\DadosExtraidos" + File.separator + "extracted_results.txt";

    public static void main( String[] args )
    {
        System.out.println( "Hello World!" );

		System.out.println("filePath" + filePath);
		System.out.println("fileTXTPath" + fileTXTPath);
		// Specify the folder containing the text files
		String folderPath = jarDirectory; // Use the JAR directory as the folder path

		// Call the method to process all files in the folder
		processFilesInFolder(folderPath);
    }
    
	private static void processFilesInFolder(String folderPath) {
		File folder = new File(folderPath);

		// Check if the provided path is a directory
		if (folder.isDirectory()) {
			File[] files = folder.listFiles();

			if (files != null) {
				for (File file : files) {
					if (file.isFile() && file.getName().endsWith(".txt")) {
						System.out.println("Processing file: " + file.getName());
						leituraArquivoTexto(file.getAbsolutePath(), file.getName());
					}
				}
			} else {
				System.err.println("No files found in the specified folder.");
			}
		} else {
			System.err.println("Invalid folder path: " + folderPath);
		}
	}

	private static void leituraArquivoTexto(String folderPath, String fileName) {
		String caminhoArquivo = folderPath;
	    String resultadoPoco = null;
	    String resultadoLongitude = null;
	    String resultadoLatitude = null;
	    String resultadoBap = null;
	    String resultadoMaiorProf = null;
	    String resultadoLACHENBRUCH_BREWER = null;
	    double maiorTemperatura = Double.MIN_VALUE; // Initialize with the smallest possible value
	    double profundidadeAlcancada = Double.MIN_VALUE; // Initialize with the smallest possible value
	    boolean encontrouPoco = false;
	    boolean encontrouLongitude = false;
	    boolean encontrouLatitude = false;
	    boolean encontrouBap = false;
	    boolean encontrouMaiorProf = false;
	    boolean encontrouProfAlcancada = false;

		try (BufferedReader leitor = new BufferedReader(new FileReader(caminhoArquivo))) {
			String linha;

			while ((linha = leitor.readLine()) != null) {

				if (linha.contains(" POÇO           :")) {
					encontrouPoco = true;
					// Extrai o "POÇO"
					resultadoPoco = linha.substring(linha.indexOf("POÇO") + "POÇO".length()).trim();
				}

				// Move the encontrouPoco check outside of the if block
				if (encontrouPoco) {
					resultadoPoco = linha; // Salva o texto que vem após "POÇO"
					encontrouPoco = false; // Reseta a flag após encontrar
				}

		        if (linha.contains("LONGITUDE      :")) {
		            encontrouLongitude = true;
		            // Extrai o texto após "LONGITUDE :"
		            resultadoLongitude = linha.substring(linha.indexOf("LONGITUDE      :") + "LONGITUDE      :".length()).trim();
		        }

		        // Move the encontrouLongitude check outside of the if block
		        if (encontrouLongitude) {
		            resultadoLongitude = linha.substring(linha.indexOf("LONGITUDE      :") + "LONGITUDE      :".length()).trim();
		            encontrouLongitude = false; // Reseta a flag após encontrar
		        }
				if (linha.contains("LATITUDE")) {
					encontrouLatitude = true;
				}

				if (encontrouLatitude) {
					resultadoLatitude = linha; // Salva o texto que vem após "LATITUDE"
					encontrouLatitude = false; // Reseta a flag após encontrar
					if (resultadoLatitude.contains(")")) {
						resultadoLatitude = resultadoLatitude.substring(0, resultadoLatitude.indexOf(")"));
					}
				}

				if (linha.contains("B.A.P")) {
					encontrouBap = true;
					// Extrai o texto até antes de "P.F.SONDADOR" após "B.A.P"
					int indexPF = linha.indexOf("P.F.SONDADOR");
					if (indexPF != -1) {
						resultadoBap = linha.substring(linha.indexOf("B.A.P"), indexPF).trim();
					} else {
						// Se não encontrar "P.F.SONDADOR", pega o texto completo após "B.A.P"
						resultadoBap = linha.substring(linha.indexOf("B.A.P")).trim();
					}
				}

				if (encontrouBap) {
					resultadoBap = linha; // Salva o texto que vem após "B.A.P"
					encontrouBap = false; // Reseta a flag após encontrar
				}

				if (linha.contains("MAIOR PROF.")) {
				    encontrouMaiorProf = true;
				    int indiceInicio = linha.indexOf("MAIOR PROF.") + "MAIOR PROF.".length();
				    int indiceFim = linha.indexOf("INICIO");
				    
				    if (indiceFim != -1) { // Se houver "INICIO" na linha
				        resultadoMaiorProf = linha.substring(indiceInicio, indiceFim).trim();
				    } else { // Se não houver "INICIO" na linha
				        resultadoMaiorProf = linha.substring(indiceInicio).trim();
				    }
				    
				    // Remove o caractere ":" do início do resultado
				    if (resultadoMaiorProf.startsWith(":")) {
				        resultadoMaiorProf = resultadoMaiorProf.substring(1).trim();
				    }
				}


				// Check if the line contains "LACHENBRUCH & BREWER"
				if (linha.contains("LACHENBRUCH & BREWER")) {
					// Extract the information after "LACHENBRUCH & BREWER"
					String infoAfterKeyword = linha
							.substring(linha.indexOf("LACHENBRUCH & BREWER") + "LACHENBRUCH & BREWER".length()).trim();

					// Accumulate the extracted information
					resultadoLACHENBRUCH_BREWER += infoAfterKeyword + "\n";
				}

				if (linha.contains("TEMPERATURA FUNDO POCO:")) {
					// Extract the temperature value after "TEMPERATURA FUNDO POCO::"
					String temperaturaTexto = linha
							.substring(linha.indexOf("TEMPERATURA FUNDO POCO:") + "TEMPERATURA FUNDO POCO:".length())
							.trim();

					try {
						double temperatura = Double.parseDouble(temperaturaTexto);

						// Check if the current temperature is greater than the previous maximum
						if (temperatura > maiorTemperatura) {
							maiorTemperatura = temperatura;
						}
					} catch (NumberFormatException e) {
						// Handle the case where the temperature value is not a valid double
						System.err.println("Error parsing temperature value: " + temperaturaTexto);
					}
				}
				
				if (maiorTemperatura != Double.MIN_VALUE) {
		            double convertido = converterFahrenheitParaCelsius(maiorTemperatura);
		            System.out.println("Maior valor de 'TEMPERATURA FUNDO POCO: em Fahrenheit' = "
		                    + maiorTemperatura);
		            System.out.println("Maior valor de 'TEMPERATURA FUNDO POCO: em Celcius' = " + convertido);

		        } else {
		            System.out.println("Palavra 'TEMPERATURA FUNDO POCO:' não encontrada no arquivo.");
		        }

				if (encontrouPoco) {
					resultadoPoco = linha; // Salva o texto que vem após "POÇO"
					encontrouPoco = false; // Reseta a flag após encontrar
				}

				if (encontrouLatitude) {
					resultadoLatitude = linha; // Salva o texto que vem após "LATITUDE" até o primeiro parêntese
					encontrouLatitude = false; // Reseta a flag após encontrar
					if (resultadoLatitude.contains(")")) {
						resultadoLatitude = resultadoLatitude.substring(0, resultadoLatitude.indexOf(")"));
					}
				}
				
				if (encontrouProfAlcancada) {
	                resultadoMaiorProf = "PROF. ALCANCADA: " + linha.trim(); // Salva o texto que vem após "PROF. ALCANCADA:"
	                encontrouProfAlcancada = false; // Reseta a flag após encontrar
	            }

			}
		} catch (IOException e) {
			e.printStackTrace();
		}

////		printResultsToFileTXT(resultadoPoco, resultadoLongitude, resultadoLatitude, resultadoBap, resultadoMaiorProf,
////				resultadoLACHENBRUCH_BREWER, maiorTemperatura, removeSuffix(fileName, "_"));
////
//		printResults(resultadoPoco, resultadoLongitude, resultadoLatitude, resultadoBap, resultadoMaiorProf,
//				resultadoLACHENBRUCH_BREWER, maiorTemperatura);
//		
		gerarArquivoExcel(removeSuffix(fileName, "_"), filtrarLongitude(resultadoLongitude),
				filtrarLatitude(resultadoLatitude), filtrarBAP(resultadoBap), 
				resultadoMaiorProf, maiorTemperatura);
	}
	
	

	private static String filtrarResultadeMP(String resultadoMaiorProf) {
	        // Monta o padrão regex para encontrar o valor entre "PROF. ALCANCADA:" e "("
	        Pattern padrao = Pattern.compile("PROF\\. ALCANCADA:\\s*(-?\\d+\\.\\d+)\\s*\\(");
	        Matcher matcher = padrao.matcher(resultadoMaiorProf);
	        
	        // Verifica se o padrão é encontrado no texto
	        if (matcher.find()) {
	            // Extrai o valor correspondente ao padrão
	            String valor = matcher.group(1);
	            return valor;
	        } else {
	            return ""; // Retorna vazio se o padrão não for encontrado
	        }
	    }

	private static String filtrarBAP(String resultadoBap) {
		String[] resultadoBAPSplit;
		resultadoBAPSplit = resultadoBap.split("\\s+");
	    return resultadoBAPSplit[3]; // Retorna o primeiro elemento do array
	}

	private static String filtrarLongitude(String resultadoLongitude) {
	    String[] resultadoLongitudeSplit;
	    
	    // Dividir a string pelo espaço em branco e pegar o primeiro elemento
	    resultadoLongitudeSplit = resultadoLongitude.split("\\s+");
	    return resultadoLongitudeSplit[0]; // Retorna o primeiro elemento do array
	}


	private static void printResults(String resultadoPoco, String resultadoLongitude, String resultadoLatitude,
	        String resultadoBap, String resultadoMaiorProf, String resultadoLACHENBRUCH_BREWER,
	        double maiorTemperaturaFundoPoco) {
	    // Check if any relevant information is found in the file
	    if (resultadoPoco != null || resultadoLongitude != null || resultadoLatitude != null || resultadoBap != null
	            || resultadoMaiorProf != null || resultadoLACHENBRUCH_BREWER != null
	            || maiorTemperaturaFundoPoco != Double.MIN_VALUE) {
	        System.out.println(" ");
	        System.out.println(" ");
	        System.out.println("-------------------------INICIO---------------------------");
	        if (resultadoPoco != null) {
	            System.out.println(" " + resultadoPoco);
	        } else {
	            System.out.println("Palavra 'POÇO' não encontrada no arquivo.");
	        }

	        if (resultadoLongitude != null) {
	            System.out.println("'LONGITUDE      :' " + resultadoLongitude);
	        } else {
	            System.out.println("Palavra 'LONGITUDE      :' não encontrada no arquivo.");
	        }

	        if (resultadoLatitude != null) {
	            System.out.println(resultadoLatitude);
	        } else {
	            System.out.println("Palavra 'LATITUDE' não encontrada no arquivo.");
	        }

	        if (resultadoBap != null) {
	            System.out.println(" 'B.A.P': " + resultadoBap);
	        } else {
	            System.out.println("Palavra 'B.A.P' não encontrada no arquivo.");
	        }

	        if (resultadoMaiorProf != null) {
	            System.out.println(" 'MAIOR PROF.': " + resultadoMaiorProf);
	        } else {
	            System.out.println("Palavra 'MAIOR PROF.' não encontrada no arquivo.");
	        }

	        if (maiorTemperaturaFundoPoco != Double.MIN_VALUE) {
	            double convertido = converterFahrenheitParaCelsius(maiorTemperaturaFundoPoco);
	            System.out.println("Maior valor de 'TEMPERATURA FUNDO POCO: em Fahrenheit' = "
	                    + maiorTemperaturaFundoPoco);
	            System.out.println("Maior valor de 'TEMPERATURA FUNDO POCO: em Celcius' = " + convertido);

	        } else {
	            System.out.println("Palavra 'TEMPERATURA FUNDO POCO:' não encontrada no arquivo.");
	        }
	        if (resultadoLACHENBRUCH_BREWER != null && !resultadoLACHENBRUCH_BREWER.isEmpty()) {
	            double convertido = converterFahrenheitParaCelsius(maiorTemperaturaFundoPoco);
	            System.out.println(
	                    "Informações após 'LACHENBRUCH & BREWER' em Fahrenheit': \n" + resultadoLACHENBRUCH_BREWER);
	            System.out.println("Informações após 'LACHENBRUCH & BREWER' em Celcius': \n\n" + +convertido);
	        } else {
	            System.out.println("\n Palavra 'LACHENBRUCH & BREWER' não encontrada no arquivo.");
	        }
	        System.out.println(" ");
	        System.out.println(" ");
	        System.out.println("-------------------------FIM---------------------------");
	    } else {
	        System.out.println("Nenhuma informação relevante encontrada no arquivo.");
	    }
	}
	private static void printResultsToFileTXT(String resultadoPoco, String resultadoLongitude, String resultadoLatitude,
			String resultadoBap, String resultadoMaiorProf, String resultadoLACHENBRUCH_BREWER,
			double maiorTemperaturaFundoPoco, String fileName) {

		try {
			// Verifique se a pasta DadosExtraidos existe; se não, crie-a
			File directory = new File(filePath);
			if (!directory.exists()) {
				directory.mkdir();
				System.out.println("Caminho criado.");
			} else {
				System.out.println("Caminho já existe.");
			}
			// Caminho do arquivo completo, incluindo o nome do arquivo
			String fullFilePath = filePath + "extracted_results.txt";
//			    Path filePath = Paths.get(folderPath);

//		        String fileName = filePath.getFileName().toString(); // Extract the file name from the path

			// Cria o BufferedWriter para o arquivo
			try (BufferedWriter writer = new BufferedWriter(new FileWriter(fullFilePath, true))) {
				writer.write("------------------------------------------------------------------------------\n");
				writer.write("------------------------------------------------------------------------------\n");
				writer.write("-------------------------INICIO DO "+fileName+"---------------------------\n\n");
				writeResultToFile(writer, "NOME DO ARQUIVO:", fileName);
				writeResultToFile(writer, " 'LONGITUDE' :", resultadoLongitude);
				writeResultToFile(writer, "", resultadoLatitude + ")");
				writeResultToFile(writer, " 'B.A.P' :", resultadoBap);
				writeResultToFile(writer, " 'MAIOR PROF. :", resultadoMaiorProf);

				if (maiorTemperaturaFundoPoco != Double.MIN_VALUE) {
					double convertido = converterFahrenheitParaCelsius(maiorTemperaturaFundoPoco);
					writer.write("Maior valor de 'TEMPERATURA FUNDO POCO:': " + maiorTemperaturaFundoPoco
							+ "  Fahrenheit \n");
					writer.write("Maior valor de 'TEMPERATURA FUNDO POCO:': " + convertido + " Celcius \n");

				} else {
					writer.write("Palavra 'TEMPERATURA FUNDO POCO:' não encontrada no arquivo.\n");
				}

				if (resultadoLACHENBRUCH_BREWER != null && !resultadoLACHENBRUCH_BREWER.isEmpty()) {
					writer.write("Informações após 'LACHENBRUCH & BREWER':\n" + resultadoLACHENBRUCH_BREWER);
//			            writer.write("Informações após 'LACHENBRUCH & BREWER':\n" + resultadoLACHENBRUCH_BREWER);
				} else {
					writer.write("Palavra 'LACHENBRUCH & BREWER' não encontrada no arquivo.\n");
				}
				writer.write("-------------------------FIM DO "+fileName+"---------------------------\n");
				writer.write("------------------------------------------------------------------------------\n");
				writer.write("------------------------------------------------------------------------------\n\n\n\n");
			}
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void gerarArquivoExcel(String fileName, String resultadoLongitude, String resultadoLatitude,
	        String resultadoBap, String resultadoMaiorProf, double maiorTemperaturaFundoPoco) {
	    String fullFilePath = filePath + "extracted_results.xls";
	    Workbook workbook;
	    Sheet sheet;
	    File file = new File(fullFilePath);

	    // If the file already exists, open it and get the existing workbook
	    if (file.exists()) {
	        try (FileInputStream fis = new FileInputStream(file)) {
	            workbook = new HSSFWorkbook(fis);
	            sheet = workbook.getSheetAt(0); // Assuming only one sheet
	        } catch (IOException e) {
	            e.printStackTrace();
	            return;
	        }
	    } else {
	        workbook = new HSSFWorkbook();
	        sheet = workbook.createSheet("Dados Extraídos");

	        // Create header row
	        Row headerRow = sheet.createRow(0);
	        headerRow.createCell(0).setCellValue("Nome do Arquivo");
	        headerRow.createCell(1).setCellValue("Longitude");
	        headerRow.createCell(2).setCellValue("Latitude");
	        headerRow.createCell(3).setCellValue("B.A.P");
	        headerRow.createCell(4).setCellValue("Maior Profundidade Alcançada");
	        headerRow.createCell(5).setCellValue("Maior Temperatura do Fundo do Poço (Fahrenheit)");
	    }

	    int lastRowNum = sheet.getLastRowNum();

	    // Create a new row for the next result
	    Row dataRow = sheet.createRow(lastRowNum + 1);

	    dataRow.createCell(0).setCellValue(fileName);
	    dataRow.createCell(1).setCellValue(resultadoLongitude);
	    dataRow.createCell(2).setCellValue(resultadoLatitude);
	    dataRow.createCell(3).setCellValue(resultadoBap);
	    dataRow.createCell(4).setCellValue(resultadoMaiorProf);
	    dataRow.createCell(5).setCellValue(maiorTemperaturaFundoPoco);

	    try (FileOutputStream fileOut = new FileOutputStream(fullFilePath)) {
	        workbook.write(fileOut);
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}

	
	// Adicione o método removeSuffix() à classe
	private static String removeSuffix(String str, String suffix) {
	    int index = str.indexOf(suffix);
	    if (index != -1) {
	        return str.substring(0, index);
	    }
	    return str;
	}
	
	private static String filtrarLatitude(String linha) {
	    String latitude = null;
	    // Verificar se a linha contém "LATITUDE"
	    if (linha.contains("LATITUDE")) {
	        // Extrair o texto após "LATITUDE"
	        int indiceInicio = linha.indexOf("LATITUDE") + "LATITUDE".length();
	        int indiceFim = linha.indexOf("(");
	        String textoLatitude;
	        if (indiceFim != -1) { // Se houver "(" na linha
	            textoLatitude = linha.substring(indiceInicio, indiceFim).trim();
	        } else { // Se não houver "(" na linha
	            textoLatitude = linha.substring(indiceInicio).trim();
	        }
	        // Extrair apenas o valor numérico da latitude
	        latitude = textoLatitude.split(":")[1].trim();
	    }
	    return latitude;
	}


	
	private static double converterFahrenheitParaCelsius(double temperaturaFahrenheit) {
		return (temperaturaFahrenheit - 32) * 5 / 9;
	}

	private static void writeResultToFile(BufferedWriter writer, String description, String result) throws IOException {
		if (result != null) {
			writer.write(description + " " + result + "\n");
		} else {
			writer.write(description + " não encontrada no arquivo.\n");
		}
	}
	

}
