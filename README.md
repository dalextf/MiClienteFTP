# Mi Cliente FTP

Implementar un sencillo cliente FTP que permite explorar el sistema de ficheros de un servidor y descargar archivos. 

## La librería `apache.commons.net`

Esta librería open source desarrollada por el proyecto Apache contiene clases para acceder a determinados servicios de red. Entre las clases de esta librería encontramos **FTPClient**; esta clase nos permite conectar a un servidor FTP, explorar su contenido, subir y descargar archivos, etc. 

A continuación se expone un trozo de código con un ejemplo de uso del cliente FTP: 

```java
package dad.miclienteftp;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.SocketException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

public class Main {
	
	@SuppressWarnings("serial")
	public static final Map<Integer, String> FILE_TYPE = new HashMap<Integer, String>() {{
		put(FTPFile.DIRECTORY_TYPE, "Directorio");
		put(FTPFile.FILE_TYPE, "Fichero");
		put(FTPFile.SYMBOLIC_LINK_TYPE, "Enlace");
	}};

	public static void main(String [] args) {
		try {
			
			// instanciar el cliente FTP
			FTPClient cliente = new FTPClient();
			
			// conectar con el servidor FTP
			cliente.connect("ftp.rediris.es", 21);
			
			// iniciar sesión anónima (login)
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
			System.out.format("+------------------------+-----------------+----------------+-----------------+%n");
			System.out.format("| Nombre                                   | Tamaño (bytes) | Tipo            |%n");
			System.out.format("+------------------------------------------+----------------+-----------------+%n");
			Arrays.stream(ficheros)
				.forEach(fichero -> {
				    System.out.format("| %-40s | %-14d | %-15s |%n", fichero.getName(), fichero.getSize(), FILE_TYPE.get(fichero.getType()));
				});
			System.out.format("+------------------------------------------+----------------+-----------------+%n");
			
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

}
```

## Apartados

### 1. Conectar al servidor FTP

Abrir un cuadro de diálogo donde se piden los datos para conectar e iniciar la conexión con el servidor FTP usando la clase `FTPClient`: 

![](docs/001.png)

**Este cuadro de diálogo se abrirá cuando seleccionemos la opción "Conectar" del menú "Servidor" (ver más adelante).** 

![](docs/002.png)

Si se pulsa **Cancelar** no se conectará con el servidor. 

Si se pulsa **Conectar**, se establecerá la conexión con el servidor indicado y se iniciará la sesión. 

En caso de **éxito** se deberá mostrar lo siguiente, se deberá deshabilitar la opción "Conectar" del menú "Servidor" y habilitar "Desconectar", y cerrar el diálogo de conexión: 

![](docs/003.png)

En caso de **error**, se indicará también al usuario y no se cerrará el cuadro de diálogo: 

![](docs/004.png)

### 2. Desconectar del servidor FTP

Implementar la funcionalidad la opción "Desconectar" del menú.  

![](docs/005.png)

Se deberá mostrar el siguiente mensaje, así como habilitar la opción "Conectar" del menú "Servidor" y deshabilitar la opción "Desconectar": 

![](docs/006.png)

### 3. Listar sistema de ficheros remoto

Nada más establecer conexión con el servidor, se deberá rellenar la tabla del formulario principal del cliente FTP con los ficheros y directorios del servidor FTP (ver código inicial para saber cómo recuperar los ficheros del servidor): 

#### Cliente desconectado. No se lista nada.

![](docs/007.png)

#### Cliente conectado. Se lista el contenido del directorio raíz "/".

![](docs/008.png)

Al conectar se deberá mostrar en la etiqueta encima de la tabla el directorio actual en el que nos encontramos dentro del servidor FTP, y rellenar la tabla con el contenido de este directorio.

### 4. Explorar sistema de ficheros remoto

Al hacer doble clic sobre un directorio de la tabla se deberá entrar en él, actualizando la ruta que hay encima de la tabla, indicando así que hemos cambiado de directorio y rellenando la tabla con el contenido del nuevo directorio: 

![](docs/009.png)

Debemos implementar el evento [`onMouseClicked`](https://openjfx.io/javadoc/11/javafx.graphics/javafx/scene/Node.html#setOnMouseClicked()) de la tabla, de un modo similar al siguiente:

```java
@FXML
private void onTablaMouseClicked(MouseEvent e) {
    // si se ha pulsado dos veces y hay un elemento seleccionado en la tabla
    if (e.getClickCount() == 2 && tabla.getSelectionModel().getSelectedItem() != null) {
        // TODO implementar aquí la acción del doble click
    }	
}
```

### 5. Descargar  

Seleccionar un fichero de la lista, pulsar el botón "Descargar": 

![](docs/010.png)

A continuación se mostrará un cuadro de diálogo donde se elegirá el destino local del fichero remoto seleccionado: 

![](docs/011.png)

Al pulsar "Guardar" se descargará el archivo a nuestro ordenador desde el servidor FTP, mostrando un mensaje de éxito: "El fichero ha sido descargado". 

## Calificación

Cada apartado se calificará en función del grado de cumplimiento de lo especificado para dicho apartado.  

|**Apartados** |**Puntuación** |
| - | - |
|1. Conectar |20 |
|2. Desconectar |15 |
|3. Listar sistema de ficheros remoto |25 |
|4. Explorar sistema de ficheros remoto |30 |
|5. Descargar un fichero |10 |
|**Total** |**100** |
