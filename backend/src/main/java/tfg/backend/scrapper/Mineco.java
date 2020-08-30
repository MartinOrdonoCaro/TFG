package tfg.backend.scrapper;

import tfg.backend.domain.Dato;
import tfg.backend.domain.Serie;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Mineco {

    public static List<Serie> importSeries() throws IOException {
        byte[] buffer = {(byte) 0xfb};
        String fileZipPath = "src\\main\\java\\tfg\\backend\\resources\\mineco\\bdsiceCompleta.zip";
        ZipInputStream zis = new ZipInputStream(new FileInputStream(fileZipPath), Charset.forName("windows-1252"));
        int numSeries = 0;
        List<Serie> series = new ArrayList<>();
        try{
            ZipEntry zipEntry = zis.getNextEntry();
            while (zipEntry != null) {
                try{
                    StringBuilder fileStringBuilder = new StringBuilder();
                    int len;
                    while ((len = zis.read(buffer)) > 0) {
                        //Construccion del archivo
                        fileStringBuilder.append(new String(buffer, 0, len));
                    }

                    /*** Tratamos el archivo contenido en s ***/
                    Serie serie = new Serie();
                    Set<Dato> datos = new HashSet<>();

                    String fileString = fileStringBuilder.toString();
                    String lines[] = fileString.split("\\r?\\n");
                    serie.setTerritorio("España");

                    int index = fileString.indexOf("COD:");
                    String codigo = fileString.substring(index+5, fileString.indexOf("\n")).trim();

                    index = fileString.indexOf("TIT:");
                    String descripcion = fileString.substring(index+5, fileString.indexOf("\n", index)).trim();

                    index = fileString.indexOf("UNI:");
                    String unidad = fileString.substring(index+5, fileString.indexOf("\n", index)).trim();

                    index = fileString.indexOf("FUE:");
                    String siglasFuente = fileString.substring(index+5, fileString.indexOf("\n", index)).trim();

                    index = fileString.indexOf("INI:");
                    String fechaInicial = fileString.substring(index+5, fileString.indexOf("\n", index)).trim();

                    index = fileString.indexOf("NOB:");
                    List<String> datosString = new LinkedList<>(
                            Arrays.asList(fileString.substring(index+5, fileString.indexOf("PUB:")).trim().split(" +")));

                    index = fileString.indexOf("FRE:");
                    String frecuencia = fileString.substring(index+5, fileString.indexOf("\n", index)).trim();

                    String periodicidad;
                    switch (frecuencia) {
                        case "1":
                            periodicidad = "Anual";
                            break;
                        case "2":
                            periodicidad = "Semestral";
                            break;
                        case "4":
                            periodicidad = "Trimestral";
                            break;
                        case "12":
                            periodicidad = "Mensual";
                            break;
                        default:
                            periodicidad = null;
                            break;
                    }
                    if(periodicidad == null){
                        continue;
                    }

                    Integer anioActual = Integer.valueOf(fechaInicial.substring(0, 4));
                    Integer periodoActual = Integer.valueOf(fechaInicial.substring(5, 6));
                    datosString.remove(0);
                    for(String item:datosString) {
                        Dato dato = new Dato();
                        dato.setAnio(anioActual.toString());
                        dato.setPeriodo(periodicidad.substring(0, 1) + periodoActual);
                        dato.setValor(isNumeric(item) ? Float.valueOf(item.trim()) : null);
                        datos.add(dato);
                        periodoActual++;
                        if(periodoActual > Integer.valueOf(frecuencia)){
                            periodoActual = 1;
                            anioActual++;

                        }
                    }
                    /** Se guardan los datos en la serie **/
                    serie.setSiglasFuente(siglasFuente);
                    serie.setPeriodicidad(periodicidad);
                    serie.setDescripcion(descripcion);
                    serie.setCodigo(codigo);
                    serie.setUnidad(unidad);
                    serie.setDatos(datos);
                    serie.setOrigen("MINECO");
                    series.add(serie);
                    numSeries++;

                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    System.out.println("Excepcion");
                } finally {
                    zipEntry = zis.getNextEntry();

                }
            }
            zis.closeEntry();
            return series;

        } catch (Exception e) {
            return series;
        } finally {
            zis.close();
        }
    }

    private static boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            Double.parseDouble(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

}
