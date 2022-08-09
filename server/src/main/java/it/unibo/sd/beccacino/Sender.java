package it.unibo.sd.beccacino;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Connection;

public class Sender extends Agent {

    private final String message;

    public Sender(String name, String message) {
        super(name);
        this.message = message;
    }

    public static void main(String[] args) throws InterruptedException {
        Thread sender = new Sender(
                args.length > 0 ? args[0] : "nobody",
                args.length > 1 ? args[1] : "hello"
        );
        sender.start();
        sender.join();
    }

    @Override
    public void run(String myName, Connection connection) throws Exception {
        // data una connessione creo un canale
        var channel = connection.createChannel();

        // dichiaro lo scambio NOME E TIPO devono essere lo stesso del ricevente. OBBLIGATORIO.
        channel.exchangeDeclare("messages", BuiltinExchangeType.TOPIC);
        // invio un SINGOLO messaggio su uno scambio precedetemente dichiarato specificando la routing key. In questo semplice
        // caso la routing key serve solo a identificare il mittente del messaggio. Scambia messaggi che sono array di byte
        // se ho una stringa o altro devo convertula in array di byte

        channel.basicPublish("messages", "from." + myName, null, message.getBytes());
        // fino a questo momento però non memorizziamo i messaggi dichiariamo solo gli scambi. Che succede se il Listener non è
        // in ascolto? Che ci perdiamo il messaggio! Bisogna quindi dichiarare una coda. Quindi bisogna essere sicuri che
        // quando inviamo un messaggio la coda ci sia già. Quello che potrei fare è fare in modo che tramite un configuratore
        // posso creare una coda
        System.out.printf("[%s] Sent message %s\n", myName, message);
        channel.close();
        connection.close();
    }
}
