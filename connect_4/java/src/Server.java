import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;

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
        private final char[] players = {'X', 'O'};

        public gameManager(Socket[] socks) {
            this.socks = socks;
        }

        public void run() {
            try { // Everything is in a try catch loop for the IO exceptions. todo add proper error handling
                // initialize variables
                int turn = 0;
                int turn_offset = 0;  //todo make random
                int cur_player;
                int col;
                byte[] resp = new byte[1024];
                Board board = new Board();
                String msg;
                char winner;
                DataOutputStream[] outputs = new DataOutputStream[2];
                DataInputStream[] inputs = new DataInputStream[2];
                outputs[0] = new DataOutputStream(socks[0].getOutputStream());
                outputs[1] = new DataOutputStream(socks[1].getOutputStream());
                inputs[0] = new DataInputStream(socks[0].getInputStream());
                inputs[1] = new DataInputStream(socks[1].getInputStream());

                //Start the game
                msg = String.format("MESSAGE Welcome to connect 4! You are player %c", players[0]);
                outputs[0].write(msg.getBytes());
                inputs[0].read(resp);  //wait for an ack
                resp = new byte[1024]; // flush resp
                msg = String.format("MESSAGE Welcome to connect 4! You are player %c", players[1]);
                outputs[1].write(msg.getBytes());
                inputs[1].read(resp);  //wait for an ack
                resp = new byte[1024]; // flush resp

                while (true) {
                    turn += 1;
                    cur_player = (turn + turn_offset) % 2;
                    msg = String.format("MESSAGE Turn %d: Player %c's turn:\n%s", turn, players[cur_player], board.toString());
                    outputs[0].write(msg.getBytes());
                    inputs[0].read(resp);  //wait for an ack
                    resp = new byte[1024]; // flush resp
                    outputs[1].write(msg.getBytes());
                    inputs[1].read(resp);  //wait for an ack
                    resp = new byte[1024]; // flush resp
                    outputs[cur_player].write("TURN".getBytes());

                    while (true) { //Drop the token
                        try {
                            inputs[cur_player].read(resp);
                            msg = new String(resp);
                            msg = msg.substring(0, msg.indexOf('\0'));  // Trim the null values off the end to make things better later
                            resp = new byte[1024]; // flush resp
                            col = Integer.parseInt(msg.split(" ")[1]);
                            if (board.drop(col, players[cur_player])) {
                                break;
                            } else {
                                throw new NumberFormatException();
                            }
                        } catch (NumberFormatException e) {
                            outputs[cur_player].write("INVALID".getBytes());
                        }
                    }

                    winner = board.win_check();
                    if (winner == players[0]) {
                        msg = String.format("WIN T %s", board.toString());
                        outputs[0].write(msg.getBytes());
                        msg = String.format("WIN F %s", board.toString());
                        outputs[1].write(msg.getBytes());
                        break;
                    } else if (winner == players[1]) {
                        msg = String.format("WIN T %s", board.toString());
                        outputs[1].write(msg.getBytes());
                        msg = String.format("WIN F %s", board.toString());
                        outputs[0].write(msg.getBytes());
                        break;
                    }
                }
                // This is just outside the game loop, cleanup the sockets
                socks[0].close();
                socks[1].close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
