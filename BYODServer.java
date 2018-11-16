import java.io.BufferedReader;
import java.io.*;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashMap;
import javax.net.ServerSocketFactory;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.swing.JOptionPane;
import javax.swing.text.AbstractDocument.LeafElement;

public class BYODServer {

	public SSLServerSocket serverSocket;
	public static HashMap<String, String> usuarios= new HashMap<String,String>();
	
	// Constructor del Servidor
	public BYODServer() throws Exception {
		// SSLServerSocketFactory para construir los ServerSockets
		SSLServerSocketFactory socketFactory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();	
		// Creación de un objeto SSLServerSocket escuchando peticiones en el puerto 7070
		serverSocket = (SSLServerSocket) socketFactory.createServerSocket(7070);
		// Hablitamos los CipherSuites que sabemos que son seguros y que se pueden utilizar para el keyStore creado
		String ciphers[] = {"TLS_DHE_DSS_WITH_AES_128_GCM_SHA256","TLS_DHE_DSS_WITH_AES_256_GCM_SHA384","TLS_DHE_RSA_WITH_AES_128_GCM_SHA256","TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA384"};
		serverSocket.setEnabledCipherSuites(ciphers);
	} 
	
	// Almacena los usuarios con sus contraseñas que están en el fichero que se pasa por parámetro
	public static void almacenaUsuarios(String archivo) throws FileNotFoundException, IOException {
		String cadena;
		FileReader f = new FileReader(archivo);
		BufferedReader b = new BufferedReader(f);
		while((cadena = b.readLine())!=null) {
			usuarios.put(cadena.split(",")[0], cadena.split(",")[1]);
		}
		b.close();
	}
	// Escribe en el fichero el mensaje del cliente en caso de que las comprobaciones hayan sido satisfactorias
	public static void escribirEnFichero(String mensaje){
		FileWriter fichero = null;
        PrintWriter pw = null;
        try
        {
            fichero = new FileWriter("C:/Users/josea/Desktop/prueba.txt",true);
            pw = new PrintWriter(fichero);
                pw.println(mensaje);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
           try {
           if (null != fichero)
              fichero.close();
           } catch (Exception e2) {
              e2.printStackTrace();
           }
        }
	}
	// Ejecución del servidor para escuchar peticiones de los clientes
	private void runServer(){
		while (true) {
			// Espera las peticiones del cliente para comprobar mensaje
			try {
				System.err.println( "Esperando conexiones de clientes...");
				Socket socket = ( Socket ) serverSocket.accept();
				// Abre un BufferedReader para leer los datos del cliente
				BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				String respuesta = input.readLine();
				String [] respuestaSeparada=respuesta.split(",");	
				// Abre un PrintWriter para enviar datos al cliente
				PrintWriter output = new PrintWriter(new OutputStreamWriter(socket.getOutputStream() ) );
				// Lectura y comprobación de errores del mensaje enviado por el cliente, dependiendo del caso se le enviará un mensaje distinto
				if(respuestaSeparada.length==3){
					String user = respuestaSeparada[0];
					String pass = respuestaSeparada[1]; 					
					if(usuarios.containsKey(user)){
						if(usuarios.get(user).equals(pass)){	
							output.println("Mensaje almacenado correctamente" );
							escribirEnFichero(respuestaSeparada[2]);
						}else{
							output.println("Contraseña incorrecta" );	
						}
					}else{
						output.println("El usuario indicado no exite" );	
					}
				}else{
					output.println("El mensaje enviado no contiene los 3 campos requeridos");	
				}
				output.flush();
				output.close();
				input.close();
				socket.close();
			}
			catch ( IOException ioException ) { ioException.printStackTrace(); }
		} 
	} 
	//Programa principal
	public static void main( String args[] ) throws Exception
	{
		almacenaUsuarios("C:/Users/josea/Desktop/pruebaDMS.txt");
		BYODServer server = new BYODServer();
		server.runServer();
	}
}

