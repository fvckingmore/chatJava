## Chat Simple Java implementando Sockets

----

### Cliente

La clase [Cliente](./src/cliente/Cliente.java) es el metodo principal de ejecucion para la aplicacion de **Cliente**.

Esta solo crea una instacia de [VentanaCliente](#ventana-cliente) 

----

### Servidor

La clase [Servidor](./src/cliente/Servidor.java) es el metodo principal de ejecucion para la aplicacion de **Servidor**.

Esta solo crea una instacia de [VentanaServidor](#ventana-servidor) 

----

### Ventana Cliente

La clase [VentanaCliente](./src/gui/VentanaCliente.java) extiende de `JFrame` para la parte del GUI e implementa `Runnable` para el manejo de hilos. Esto ultimo para mantenerse a la espera de paquetes.

El constructor crear todos los elementos de GUI y finalmente define el hilo principal de la aplicacion cliente. <br>

```java
89		Thread hilo = new Thread(this);
90		hilo.start();
```

La clase privada `EnviarMensaje` implementa `ActionListener` para manejar los eventos del boton de **enviar** al momento de ser pulsado.
Igualmente se encarga del envio de paquetes por parte del cliente.

Se define un nuevo **Socket** para el envio de paquetes.

``` java
105		Socket socket = new Socket(campoIPServidor.getText(), 20000);
```
`campoIpServidor` recibe la direccion ip del host **Servidor**. <br>
`20000` es el puerto con el cual trabajara el **Socket**.

Se define una instancia de [PaqueteEnvio](#paquete-envio) `data` que contendra toda la informacion necesaria del cliente emisor para el servidor como:

- Mensaje
- Nombre de Usuario
- Direccion IP

Se define una instancia `ObjectOutputStream paqueteData` con el fin de crear un objeto de tipo primitivo, para que sea serializado y este pueda ser transmitido por el **Socket**.

``` java 
113		ObjectOutputStream paqueteData = new ObjectOutputStream(socket.getOutputStream());
```

Luego se escribe en `paqueteData` los datos de `data`, se envia la informacion y por ultimo se cierra el **Socket** para evitar el filtrado de informacion.

Se muestra el mensaje enviado en la caja de chat del emisor por motivo esteticos.

``` java 
117		campoMesajes.append("["+ campoNickName.getText()+ "]: "+ campoRedaccion.getText()+ "\n");
```

El metodo `run()` que pertenece a `Runnable` se utiliza para que el cliente este a la espera de paquetes por parte del servidor, y se mantenga actualizado.

Se define una instancia **ServerSocket** que servira de *listener* a los paquetes entrantes, un **Socket** por donde entraran los paquetes y un [PaqueteEnvio](#paquete-envio) que contendra la informacion proveniente del servidor. 

``` java
134		ServerSocket servidorCliente = new ServerSocket(20000);
135		Socket socketCliente;
136		PaqueteEnvio paqueteEntrada;
```
Se crea un ciclo infinito `while(true)` para que el cliente este siempre a la espera de paquetes.

``` java
144		socketCliente = servidorCliente.accept();
```
Acepta los paquetes entrates provenientes del servidor.

Se crea una instancia `ObjectInputStream entrada` para obtener los datos emitidos por el servidor, y estos se almacenan en `paqueteEntrada` para ser utilizados.
>Se utiliza un cast para poder deserializar la informacion del ObjectInputStream

``` java
147		ObjectInputStream entrada = new ObjectInputStream(socketCliente.getInputStream());
148		paqueteEntrada = (PaqueteEnvio) entrada.readObject();

```

El mensaje recibido desglozado se muestra en la ventana del cliente:

``` java
150		campoMesajes.append("["+ paqueteEntrada.getNickName()+ "]: "+ paqueteEntrada.getMensaje()+ "\n");
```

Y se cierra el **Socket** para evitar filtrado de datos:

``` java
151		socketCliente.close();
```
----

### Ventana Servidor

La clase [VentanaServidor](./src/gui/VentanaServidor.java) extiende de `JFrame` para la parte del GUI e implementa `Runnable` para el manejo de hilos. Esto ultimo para mantenerse a la espera de paquetes.

El constructor crear todos los elementos de GUI y finalmente define el hilo principal de la aplicacion servidor. <br>

```java
46		Thread hiloPrincipal = new Thread(this);
47		hiloPrincipal.start();	// Inicio de hilo
```

El metodo `run()` que pertenece a `Runnable` se utiliza para que el servidor este a la espera de paquetes por parte del cliente, y se mantenga actualizado.

Se define una instancia **ServerSocket** que servira de *listener* a los paquetes entrantes, variables `String` temporales para los datos y un [PaqueteEnvio](#paquete-envio) que contendra la informacion proveniente del cliente. 

``` java
56		ServerSocket socketServidor = new ServerSocket(20000);
58		String direccionIP, nickName, mensaje;
61		PaqueteEnvio data;
```
`20000` es el puerto utilizado por el **Socket** para el flujo de datos. <br>

Se crea un ciclo infinito `while(true)` para que el servidor este siempre a la espera de paquetes.

``` java
67		Socket socketEntrada = socketServidor.accept();
```
Acepta los paquetes entrates provenientes del cliente.

Se crea una instancia `ObjectInputStream paqueteData` para obtener los datos emitidos por el cliente, y estos se almacenan en `data` para ser utilizados.
>Se utiliza un cast para poder deserializar la informacion del ObjectInputStream

``` java
70		ObjectInputStream paqueteData = new ObjectInputStream(socketEntrada.getInputStream());
71		data = (PaqueteEnvio) paqueteData.readObject();
```

Asigna los datos a las variables temporales:

``` java
74		direccionIP = data.getDireccionIP();
75		nickName = data.getNickName();
76		mensaje = data.getMensaje(); 
```

El servidor muestra un log de los eventos:

``` java
78		campoMensajes.append("["+ nickName+ "]: "+ mensaje+ " para "+ direccionIP+ "\n" );
```

Se crea un **Socket** para redirigir la informacion al cliente destino, ya que el **Servidor** es un **Puente** entre clientes.

``` java
81		Socket replica = new Socket(direccionIP, 20000);
```

`direccionIP` es la IP del cliente destino. <br>
`20000` es el puerto que utilizara el **Socket**.

Se define una instancia `ObjectOutputStream paqueteReplica` con el fin de crear un objeto de tipo primitivo, para que sea serializado y este pueda ser transmitido por el **Socket**.

``` java 
82		ObjectOutputStream paqueteReplica = new ObjectOutputStream(replica.getOutputStream());
```

Luego se escribe en `paqueteReplica` los datos de `data`, se envia la informacion y por ultimo se cierra el **Socket** para evitar el filtrado de informacion.


``` java
83		((ObjectOutput) paqueteReplica).writeObject(data);
84		replica.close();				
85		paqueteReplica.close();
86		socketEntrada.close();
```



-----
### Paquete Envio

La clase [PaqueteEnvio](./src/paqueteEnvio/PaqueteEnvio.java) se utiliza como contenedor de informacion que sera transmitida por los clientes. <br>

Esta implementa `Serializable` para que los objetos puedan ser codificados en *bytes* y transmitidos por el **Socket** hacia el servidor o los clientes.

Contiene los atributos:

- `direccionIP`: es la IP destino hacia donde se dirige el paquete.
- `nickName`: es el nombre usuario del cliente emisor.
- `mensaje`: es el mensaje enviado por el cliente emisor.

  