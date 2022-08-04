package it.unibo.sd.beccacino;

//import com.mongodb.MongoClientURI;
//import com.mongodb.MongoClient;
//import com.mongodb.client.MongoClient;

import com.rabbitmq.client.*;

import java.io.IOException;

// riceve info da una coda
public class Listener extends Agent {

    public Listener(String name) {
        super(name);
    }

    public static void main(String[] args) throws InterruptedException {
        Thread listener = new Listener(args.length > 0 ? args[0] : "nobody");
        listener.start();
        listener.join();
    }

    @Override
    public void run(String myName, Connection connection) throws Exception {
        // si occupa lui di creare una connessione, lo fa lui in automatico dietro le quinte. Avendo una connessione posso
        // creare un channel.
        Channel channel = connection.createChannel();
        boolean state = true;

        // sul canale posso fare tantissime operazioni
        // ridichiaro uno scambio cosa non strettamente necessaria ma che qui si fa per farlo vedere. Gli dico guarda broker
        // esiste uno scambio e si chiama messages ed è di tipo diretto quindi mi aspetto una sola coda attaccata.
        // LA CREAZIONE DELLO SCAMBIO DOVREBBE ESSERE FATTA DAL MITTENTE NON DAL RICEVENTE OVVIAMENTE
        channel.exchangeDeclare("messages", BuiltinExchangeType.TOPIC);
        // dichiaro la coda. Crea una coda il cui nome è determinato dal server e sarà di defulat esclusiva, auto-delete e non
        // durable. Coda eliminata da sola al termine del programma.NOn mi interessa sapere il nome della coda.
        var declaration = channel.queueDeclare();
        // coda già esistente e gli dico qual'è e le proprietà devono essere identiche
            /*channel.queueDeclare(
                    "messages-queue", // queue
                    false, // durable
                    false, // exclusive
                    false, // auto delete
                    null // arguments
            );*/
        // il nome della coda lo genera da solo ma mi serve per poterlo utilizzare.
        String queueName = declaration.getQueue();
        // broker associa alla coda che hai appena creato allo scambio chiamato "messages" . Se il tipo di exchange fosse stato
        // TOPIC in routingKey dovevamo dire a quali messaggi/a quali topic siamo interessati. Tipo se ci interessavano solo
        // i messaggi da Giovanni dovevamo mettere "from.giovanni" assicurandoci che il mittente scrivesse nella sua routing key
        // "from.giovanni". In questo caso si mette in ascolto dei messaggi da chiunque.
        channel.queueBind(queueName, "messages", "from.*");

        System.out.println("Listening for messages...");

        // per mettersi nell'ascolto in pratica devo attivare una callback. Su quale coda mi metto in ascolto e devo fornire un oggett
        // che esponga un metodo che viene chiamato ogni volta che arriva un messaggio sulla coda. In questo caso semplicemente
        // lo stampo. Posso anche dirgli di non mandargli un ack implicitamente ogni volta che ricevo correttamente un messaggio. Ma se
        // levo gli ack senza gestirli bene, il broker non saprà più quale messaggio ha spedito correttamente quindi quando un
        // client si collega re-invierà sempre i messaggi. Il broker leverà i messaggi dalla memoria locale quando verranno ackkati
        // gestendo intelligentemente gli ack posso sfruttare l'assenza di ack per re-inviare i giusti messaggi.
        channel.basicConsume(declaration.getQueue(), new DefaultConsumer(channel) {
            // dentro envelope ci sono tutti i metadati di livello protocollare per gestire sto messaggio
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                var sender = "prova";
                System.out.printf("[%s] %s\n", sender, new String(body));


            /*MongoDatabase db;
            System.out.println("ooooooooooo");
            // MongoClient client = MongoClients.create(System.getenv("MONGODB"));
            MongoClient client = new MongoClient(new MongoClientURI("mongodb://localhost:27017"));
            System.out.println("aaaaaaaaaa");
            db = client.getDatabase("local");
            System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA:"+db.getName());
            db.getCollection("fica").insertOne(new Document());
            System.out.println("BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB"+db.getCollection("fica").countDocuments());*/

            }
        });

        // porgramma va avanti fino a che non viene chiuso lo standard input
        while (state) ;

        // chiudo canale e connessione
        channel.close();
        connection.close();
    }
}
