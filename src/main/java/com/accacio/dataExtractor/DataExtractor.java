package com.accacio.dataExtractor;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

public class DataExtractor {

    private static String jarDirectory = "H:\\downloads\\BiaTXT";
    private static String filePath = jarDirectory + File.separator + "DadosExtraidos" + File.separator;
    private static String fileTXTPath = "H:\\downloads\\BiaTXT\\DadosExtraidos" + File.separator + "extracted_results.txt";

    public static void main(String[] args) {
        System.out.println("Hello World!");
        System.out.println("filePath: " + filePath);
        System.out.println("fileTXTPath: " + fileTXTPath);

        String folderPath = jarDirectory;
        processFilesInFolder(folderPath);
    }

    private static void processFilesInFolder(String folderPath) {
        File folder = new File(folderPath);
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
    	    String resultadoMaiorProf = "";
    	    String resultadoMaiorProfPopula = null;	    
    	    String resultadoLACHENBRUCH_BREWER = null;
    	    Double maiorTemperatura = Double.MIN_VALUE; // Initialize with the smallest possible value
    	    Double profundidadeAlcancada = Double.MIN_VALUE; // Initialize with the smallest possible value
    	    boolean encontrouPoco = false;
    	    boolean encontrouLongitude = false;
    	    boolean encontrouLatitude = false;
    	    boolean encontrouBap = false;
    	    boolean encontrouMaiorProf = false;
    	    boolean encontrouProfAlcancada = false;
            boolean novaSecao = true;	    
            String temperaturaTexto = "";
            boolean temTemperatura = false;
            double maiorValor = Double.MIN_VALUE; // Inicializa o maior valor com o menor valor possível de um double
            String resultadoMaiorValor = ""; // Inicializa a string do resultado com uma string vazia
            String resultadoMaiorProf2 = "";
            boolean encontrouMaiorProf2 = false;
            boolean isLACHENBRUCH = false;

    	    
    		try (BufferedReader leitor = new BufferedReader(new FileReader(caminhoArquivo))) {
    			String linha;

    			List<String> profundidadeArray = new ArrayList<>();
    			List<String> temperaturaFundodeArray = new ArrayList<>();		
    		
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

    				  if (linha.contains("------------------------------------------------------------------------------------------------------------------------------------")) {
    			            novaSecao = true;
    			        } 
    				  if(novaSecao) {
    						if (linha.contains("PROF. ALCANCADA")) {
    						    encontrouMaiorProf = true;
    						    int indiceInicio = linha.indexOf("PROF. ALCANCADA") + "PROF. ALCANCADA".length();
    						    int indiceFim = linha.indexOf("(");
    		
    						    if (indiceFim != -1) { // Se houver "(" na linha
    						        resultadoMaiorProf = linha.substring(indiceInicio, indiceFim).trim();
    						        resultadoMaiorProf = remove2Pontos(resultadoMaiorProf);
    						    } else { // Se não houver "(" na linha
    						        resultadoMaiorProf = remove2Pontos(resultadoMaiorProf);
    						        resultadoMaiorProf = linha.substring(indiceInicio).trim();
    						    }
    						    if(resultadoMaiorProf.isBlank() || resultadoMaiorProf.isEmpty()) {
    						    	resultadoMaiorProf = "0";
    						    }
    						    resultadoMaiorProfPopula = resultadoMaiorProf;
    					    	temTemperatura = true;	
    						    
    						}
    		
    						if (linha.contains("TEMPERATURA FUNDO POCO:")) {
    							// Extract the temperature value after "TEMPERATURA FUNDO POCO::"
    							temperaturaTexto = linha
    									.substring(linha.indexOf("TEMPERATURA FUNDO POCO:") + "TEMPERATURA FUNDO POCO:".length())
    									.trim();
    							if (temperaturaTexto != null && !temperaturaTexto.isEmpty()) {
    								try {
    									double temperatura = Double.parseDouble(temperaturaTexto);
    			
//    									temperaturaFundodeArray.add(temperaturaTexto);
    									System.out.println("TEMPERATURA FINAL" + temperaturaFundodeArray);
    									
    									// Check if the current temperature is greater than the previous maximum
    									if (temperatura > maiorTemperatura) {
    										maiorTemperatura = temperatura;
    									}
    								} catch (NumberFormatException e) {
    									// Handle the case where the temperature value is not a valid double
    									System.err.println("Error parsing temperature value: " + temperaturaTexto);
    								}
    							}else {
    								temperaturaFundodeArray.add("0");
    							}
    							
    						}
    						if(resultadoMaiorProfPopula != null && temTemperatura) {
    							profundidadeArray.add(resultadoMaiorProf);
    							if(temperaturaTexto.isBlank() || temperaturaTexto.isEmpty()) {
    								temperaturaFundodeArray.add("0");
    							}else {
    								temperaturaFundodeArray.add(temperaturaTexto);
    							}
    						}
    						temTemperatura = false;
    						
    			}
    			
    				  //TODO Verificar se a regra do LacheBREUNCH é a mesma da temperatura normalse for aplicar a regra de maior profundidade
    				  if (linha.contains("LACHENBRUCH & BREWER")) {
    					    // Extrai as informações após "LACHENBRUCH & BREWER"
    					    String infoAfterKeyword = linha.substring(linha.indexOf("LACHENBRUCH & BREWER") + "LACHENBRUCH & BREWER".length()).trim();

    					    // Divide a linha em partes usando espaços em branco como delimitador
    					    String[] partes = infoAfterKeyword.split("\\s+");

    					    // Obtém o valor da terceira parte (o valor numérico)
    					    if (partes.length >= 3) {
    					        try {
    					            double valor = Double.parseDouble(partes[0]); // Obtém o valor numérico

    					            // Verifica se o valor atual é maior que o maior valor registrado até agora
    					            if (valor > maiorValor) {
    					                maiorValor = valor; // Atualiza o maior valor
    					                resultadoMaiorValor = infoAfterKeyword; // Atualiza a string do resultado
    					                System.out.println(resultadoMaiorValor);
    					                
    					                resultadoMaiorProfPopula = removeLastSpecialCharacter(partes[1].toString());
    					            }
    					        } catch (NumberFormatException e) {
    					            // Lida com o caso em que o valor numérico não pode ser convertido para double
    					            System.err.println("Erro ao converter o valor para double: " + partes[2]);
    					        }
    					    }
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
//    	                profundidadeArray.add(resultadoMaiorProf);
    	                encontrouProfAlcancada = false; // Reseta a flag após encontrar
    	            }

    			}
    			
    			if(!profundidadeArray.isEmpty() || !temperaturaFundodeArray.isEmpty()) {
    				maiorTemperatura = maiorProfundidadeComTemperaturaFundoPoco(profundidadeArray, temperaturaFundodeArray);
    			}else {
    				maiorTemperatura = maiorValor;
    			}
    			if(resultadoMaiorProf.isEmpty()) {
    				resultadoMaiorProf = remove2Pontos(resultadoMaiorProf2);
    				isLACHENBRUCH = true;
    			}
    			
    		} catch (IOException e) {
    			e.printStackTrace();
    		}
    		
    		
//    		gerarArquivoExcel(removeSuffix(fileName, "_"), filtrarLongitude(resultadoLongitude),
//    				filtrarLatitude(resultadoLatitude), filtrarBAP(resultadoBap), 
//    				resultadoMaiorProfPopula, maiorTemperatura, isLACHENBRUCH );
    	}

    private static double converterFahrenheitParaCelsius(double temperaturaFahrenheit) {
        return (temperaturaFahrenheit - 32) * 5 / 9;
    }

    private static String removeSuffix(String str, String suffix) {
        int index = str.indexOf(suffix);
        if (index != -1) {
            return str.substring(0, index);
        }
        return str;
    }

    private static String removeLastSpecialCharacter(String str) {
        return str.replaceAll("[^a-zA-Z0-9]+$", "");
    }

    private static String filtrarLatitude(String linha) {
        String latitude = null;
        if (linha.contains("LATITUDE")) {
            int indiceInicio = linha.indexOf("LATITUDE") + "LATITUDE".length();
            int indiceFim = linha.indexOf("(");
            String textoLatitude;
            if (indiceFim != -1) {
                textoLatitude = linha.substring(indiceInicio, indiceFim).trim();
            } else {
                textoLatitude = linha.substring(indiceInicio).trim();
            }
            latitude = textoLatitude.split(":")[1].trim();
        }
        return latitude;
    }

    private static String filtrarLongitude(String resultadoLongitude) {
        String[] resultadoLongitudeSplit;
        resultadoLongitudeSplit = resultadoLongitude.split("\\s+");
        return resultadoLongitudeSplit[0];
    }

    private static String filtrarBAP(String resultadoBap) {
        String[] resultadoBAPSplit;
        resultadoBAPSplit = resultadoBap.split("\\s+");
        return resultadoBAPSplit[3];
    }

    public static int buscarMaiorProfundidade(List<String> profundidadeArray) {
        int posicaoMaior = 0;
        for (int i = 1; i < profundidadeArray.size(); i++) {
            if (Double.parseDouble(profundidadeArray.get(i)) >= Double.parseDouble(profundidadeArray.get(posicaoMaior))) {
                posicaoMaior = i;
            }
        }
        return posicaoMaior;
    }

    public static Double maiorProfundidadeComTemperaturaFundoPoco(List<String> profundidadeArray, List<String> temperaturaFundodeArray) {
        int posicaoMaior = 0;
        for (int i = 1; i < profundidadeArray.size(); i++) {
            if (Double.parseDouble(profundidadeArray.get(i)) >= Double.parseDouble(profundidadeArray.get(posicaoMaior)) 
                    && Double.parseDouble(temperaturaFundodeArray.get(i)) > 0) {
                posicaoMaior = i;
            }
        }
        return Double.parseDouble(profundidadeArray.get(posicaoMaior));
    }

    public static String remove2Pontos(String resultadoMaiorProf) {
		// Remove o caractere ":" do início do resultado
	    if (resultadoMaiorProf.startsWith(":")) {
	        resultadoMaiorProf = resultadoMaiorProf.substring(1).trim();
//	        profundidadeArray.add(resultadoMaiorProf);
	    }
	 return resultadoMaiorProf;   
	}
}
