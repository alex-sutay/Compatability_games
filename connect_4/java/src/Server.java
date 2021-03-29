import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

//todo add comments
public class Server {
    private final ServerSocket sock;

    public Server(int port) throws IOException {
        sock = new ServerSocket(port);
    }

    public void start_server() throws IOException {
        sock.setReuseAddress(true);
        System.out.println("Starting server...");
        Socket[] clients;
        while (true) {
            clients = new Socket[2];
            for (int i=0; i<2; i++) {
                Socket client = sock.accept();
                System.out.println("Connection from: " + client.getInetAddress());
                clients[i] = client;
            }
            System.out.println("Starting a game...");
            gameManager gameThread = new gameManager(clients);
            new Thread(gameThread).start();
        }
    }

    public static void main(String[] args) {
        try {
            Server server = new Server(42000);  //todo add commandline arguments for port
            server.start_server();
        } catch (IOException e) {
            System.out.println("Networking error");
        }
    }

    private static class gameManager implements Runnable {
        private final Socket[] socks;

        public gameManager(Socket[] socks) {
            this.socks = socks;
        }

        public void run() {
            int turn = 0;
            char[] players = {'X', 'O'};
            int turn_offset = 0;  //todo make random
            int cur_player;
            Board board = new Board();
            String msg;
            DataOutputStream[] outputs = new DataOutputStream[2];
            try {
                outputs[0] = new DataOutputStream(socks[0].getOutputStream());
                outputs[1] = new DataOutputStream(socks[1].getOutputStream());
                while (true) {
                    turn += 1;
                    cur_player = (turn + turn_offset) % 2;
                    msg = String.format("MESSAGE Turn %d: Player %c's turn:\n%s", turn, players[cur_player], board.toString());
                    outputs[0].write(msg.getBytes());
                    outputs[1].write(msg.getBytes());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
