package gui;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import paqueteEnvio.PaqueteEnvio;

import javax.swing.JButton;
import java.awt.Color;
import java.awt.event.ActionListener;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.awt.event.ActionEvent;
import java.net.*;
import javax.swing.JScrollBar;




public class VentanaCliente extends JFrame implements Runnable {

	private JPanel contenedor;
	private JButton botonEnviar;
	private JTextArea campoRedaccion;
	private JTextArea campoMesajes;
	private JTextArea campoNickName;
	private JTextArea campoIPInvitado;
	private JTextArea campoIPServidor;


	/* Constructor de ventana clientes */
	public VentanaCliente() {
		
		
		/* Creacion del contenedor */
		setTitle("Chat");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 400, 300);
		contenedor = new JPanel();
		contenedor.setBorder(null);
		setContentPane(contenedor);
		contenedor.setLayout(null);
		
		
		/* Boton para enviar mensaje */
		botonEnviar = new JButton("Enviar");
		botonEnviar.setBounds(308, 238, 78, 25);
		EnviarMensaje evento = new EnviarMensaje();
		botonEnviar.addActionListener(evento);
		contenedor.add(botonEnviar);
		campoRedaccion = new JTextArea();
		campoRedaccion.setText("Mensaje....");
		campoRedaccion.setBounds(12, 238, 288, 25);
		contenedor.add(campoRedaccion);
		campoRedaccion.setColumns(10);
		
		
		/* Campo de texto para mensajes recibidos */
		campoMesajes = new JTextArea();
		campoMesajes.setBackground(Color.WHITE);
		campoMesajes.setEditable(false);
		campoMesajes.setBounds(12, 42, 374, 184);
		contenedor.add(campoMesajes);
		campoMesajes.setColumns(10);
		
		/* Campo de texto para nombre de usuario */
		campoNickName = new JTextArea();
		campoNickName.setText("Nickname");
		campoNickName.setBounds(12, 12, 180, 20);
		contenedor.add(campoNickName);
		
		/* Campo de texto para direccion destino */
		campoIPInvitado = new JTextArea();
		campoIPInvitado.setText("IP Invitado");
		campoIPInvitado.setBounds(199, 12, 90, 20);
		contenedor.add(campoIPInvitado);
		
		/* Campo para direccion de servidor */
		campoIPServidor = new JTextArea();
		campoIPServidor.setText("IP Servidor");
		campoIPServidor.setBounds(301, 12, 90, 20);
		contenedor.add(campoIPServidor);
		
		/* Hilo principal del cliente */
		Thread hilo = new Thread(this);
		hilo.start();
		

		
		setVisible(true);
	}
	
	/* Clase para lectura de eventos */
	private class EnviarMensaje implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			
			try {
				/* Socket de transmision de mensajes */
				Socket socket = new Socket(campoIPServidor.getText(), 20000);
				
				/* Creacion y encapsulacion de paquete para envio al servidor */
				PaqueteEnvio data = new PaqueteEnvio();
				data.setNickName(campoNickName.getText());
				data.setDireccionIP(campoIPInvitado.getText());
				data.setMensaje(campoRedaccion.getText());
				
				ObjectOutputStream paqueteData = new ObjectOutputStream(socket.getOutputStream());
				paqueteData.writeObject(data);

				socket.close();
				campoMesajes.append("["+ campoNickName.getText()+ "]: "+ campoRedaccion.getText()+ "\n");
				
			} catch (UnknownHostException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				System.out.println(e1.getMessage());
			}			
		}		
	}

	/* Metodo para mantener el cliente a la espera de mensajes */
	@Override
	public void run() {
		
		try {
		
			/* Socket de servidor para cliente */
			ServerSocket servidorCliente = new ServerSocket(20000);
			Socket socketCliente;
			PaqueteEnvio paqueteEntrada;
			
			/* Bucle para la espera de mensajes */
			while(true) {
				

				
				/* Socket para la esta de paquete */
				socketCliente = servidorCliente.accept();
				
				/* Obtencion de paquete */
				ObjectInputStream entrada = new ObjectInputStream(socketCliente.getInputStream());
				paqueteEntrada = (PaqueteEnvio) entrada.readObject();
				
				campoMesajes.append("["+ paqueteEntrada.getNickName()+ "]: "+ paqueteEntrada.getMensaje()+ "\n");
				socketCliente.close();
				//servidorCliente.close();
				
			}
			
		} catch (IOException | ClassNotFoundException e) {

			e.printStackTrace();
		}
	}
}
