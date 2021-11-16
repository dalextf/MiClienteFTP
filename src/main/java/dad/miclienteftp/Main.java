package dad.miclienteftp;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.SocketException;
import java.util.Arrays;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

public class Main {

	public static void main(String [] args) {
		try {
			
			// instanciar el cliente FTP
			FTPClient cliente = new FTPClient();
			
			// conectar con el servidor FTP
			cliente.connect("ftp.rediris.es", 21);
			
			// iniciar sesión anónimo (login)
			cliente.login("", "");
			
			// cambiar el directorio actual en el servidor
			cliente.changeWorkingDirectory("/debian/dists");

			// recuperar el nombre del directorio actual
			String directorioActual = cliente.printWorkingDirectory();
			System.out.println("Directorio actual: " + directorioActual);

			// recuperar un listado de los ficheros y directorios del directorio actual del servidor
			FTPFile [] ficheros = cliente.listFiles();
			
			// recorrer el listado de archivos recuperados
			System.out.format("+------------------------+%n");
			System.out.format("| Archivos del servidor: |%n");
			System.out.format("+------------------------+---------+----------------+-----------------+%n");
			System.out.format("| Nombre                           | Tamaño (bytes) | Tipo            |%n");
			System.out.format("+----------------------------------+----------------+-----------------+%n");
			Arrays.stream(ficheros)
				.forEach(fichero -> {
				    System.out.format("| %-32s | %-14d | %-15s |%n", fichero.getName(), fichero.getSize(), getTipo(fichero));					
				});
			System.out.format("+----------------------------------+----------------+-----------------+%n");
			
			// cambiar el directorio padre en el servidor
			cliente.changeWorkingDirectory("..");

			// descargar un fichero
			File descarga = new File("welcome.msg");
			FileOutputStream flujo = new FileOutputStream(descarga);
			cliente.retrieveFile("welcome.msg", flujo);
			flujo.flush();
			flujo.close();
			
			// desconectar del servidor
			cliente.disconnect();
			
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String getTipo(FTPFile fichero) {
		switch (fichero.getType()) {
		case FTPFile.DIRECTORY_TYPE:
			return "Directorio";
		case FTPFile.FILE_TYPE:
			return "Fichero";
		case FTPFile.SYMBOLIC_LINK_TYPE:
			return "Enlace";
		default:
			return "Desconocido";
		}
	}

}