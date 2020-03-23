package gui;

import java.awt.Color;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import paqueteEnvio.PaqueteEnvio;


public class VentanaServidor extends JFrame implements Runnable {

	private JPanel contenedor;	// Contenedor JFrame
	private JTextArea campoMensajes;	//Area de texto para logs

	
	/* Constructor de ventana */
	public VentanaServidor() {
		
		/* Definicion de contenedor de widgets */
		setTitle("Servidor");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 400, 300);
		contenedor = new JPanel();
		contenedor.setBorder(null);
		setContentPane(contenedor);
		contenedor.setLayout(null);
		
		/* Definicion de area de texto para logs */
		campoMensajes = new JTextArea();
		campoMensajes.setBackground(Color.WHITE);
		campoMensajes.setEditable(false);
		campoMensajes.setBounds(12, 12, 374, 251);
		contenedor.add(campoMensajes);
		campoMensajes.setColumns(10);
		setVisible(true);
		
		/* Hilo prncipal de ejecucion */
		Thread hiloPrincipal = new Thread(this);
		hiloPrincipal.start();	// Inicio de hilo
	}
	
	/* Metodo para mantener al servidor a la espera de mensajes */
	@Override
	public void run() {
		
		try {
			
			ServerSocket socketServidor = new ServerSocket(20000);	// Socket de servidor
			
			String direccionIP, nickName, mensaje;	// Variables para almacenar datos de entrada

			/* Objeto tipo "Paquete" para recepcion de los datos */
			PaqueteEnvio data; 
			

			/* Bucle infinito para la espera de peticiones */
			while(true) {
								
				Socket socketEntrada = socketServidor.accept();	// Socket para recibir los datos
				
				/* Obtencion de datos desde el objeto recibido */
				ObjectInputStream paqueteData = new ObjectInputStream(socketEntrada.getInputStream());
				data = (PaqueteEnvio) paqueteData.readObject();
				
				/* Asignacion de datos a las variables temporales */
				direccionIP = data.getDireccionIP();
				nickName = data.getNickName();
				mensaje = data.getMensaje();
				
				campoMensajes.append("["+ nickName+ "]: "+ mensaje+ " para "+ direccionIP+ "\n" );	//Log de servidor
				
				
				Socket replica = new Socket(direccionIP, 20000);
				ObjectOutputStream paqueteReplica = new ObjectOutputStream(replica.getOutputStream());
				((ObjectOutput) paqueteReplica).writeObject(data);
				replica.close();
				
				
				paqueteReplica.close();
				socketEntrada.close();
				//socketServidor.close();
			}
			
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		
		
	}

}


