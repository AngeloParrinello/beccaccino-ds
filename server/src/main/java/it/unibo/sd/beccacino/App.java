package it.unibo.sd.beccacino;

public class App {
    public String getGreeting() {
        return "Hello World!";
    }

    public static void main(String[] args) {
        Listener listener = new Listener("listener");
        listener.start();
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Sender sender = new Sender("sender", "test message");
        sender.start();
        try {
            listener.join();
        } catch(InterruptedException e) {
            e.printStackTrace();
        }
        try {
            sender.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
