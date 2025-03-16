import java.net.Socket;
import java.net.ServerSocket;
import java.io.PrintWriter;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.util.concurrent.*;

class ChatServer implements Runnable {
    private final static int PORT = 8000;
    private final static int MAX_CLIENTS = 5;
    private final static ExecutorService executor = Executors.newFixedThreadPool(MAX_CLIENTS);
    private final static CopyOnWriteArrayList<PrintWriter> clients = new CopyOnWriteArrayList<>();
    private final static BlockingQueue<String> messageQueue = new LinkedBlockingQueue<>();
    private final static ExecutorService broadcaster = Executors.newCachedThreadPool();

    private final Socket clientSocket;
    private String clientName = "";
    private PrintWriter socketWriter;

    private ChatServer(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    public void run() {
        BufferedReader socketReader = null;
        try {
            socketWriter = new PrintWriter(clientSocket.getOutputStream(), true);
            socketReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            // Add client to list
            clients.add(socketWriter);

            // First message is the client's name
            clientName = socketReader.readLine();
            System.out.println(clientName + " has joined the chat.");
            messageQueue.add(clientName + " has joined the chat.");

            String inputLine;
            while ((inputLine = socketReader.readLine()) != null) {
                String message = clientName + ": " + inputLine;
                System.out.println("Queueing: " + message);
                messageQueue.add(message);
            }

            // Client disconnects
            System.out.println(clientName + " has left the chat.");
            messageQueue.add(clientName + " has left the chat.");

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        } finally {
            try {
                // Remove client on disconnect
                clients.remove(socketWriter);
                if (socketWriter != null) socketWriter.close();
                if (socketReader != null) socketReader.close();
                if (clientSocket != null) clientSocket.close();
            } catch (Exception exception) {
                System.out.println("Cleanup error: " + exception.getMessage());
            }
        }
    }

    // Message Dispatcher: Ensures messages are sent in the correct order
    private static void startMessageDispatcher() {
        new Thread(() -> {
            while (true) {
                try {
                    String message = messageQueue.take(); // Blocks until a message is available
                    broadcast(message);
                } catch (InterruptedException ignored) {
                }
            }
        }).start();
    }

    // Broadcast messages in parallel to all clients
    private static void broadcast(String message) {
        for (PrintWriter client : clients) {
            broadcaster.execute(() -> client.println(message));
        }
    }

    public static void main(String[] args) {
        System.out.println("ChatServer started.");
        startMessageDispatcher(); // Start message queue processor

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Listening on port " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                executor.execute(new ChatServer(clientSocket));
            }
        } catch (Exception e) {
            System.out.println("Server error: " + e.getMessage());
        }
    }
}
