import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private static final int PORT = 42000;
    Scanner in;

    public Client(Scanner in) {
        this.in = in;
    }

    public static void main(String[] args) {

        System.out.println("5".split(" ")[0].equals("5"));

        // initialize variables
        Scanner in = new Scanner(System.in);
        System.out.print("Connect to: ");
        String servername= in.nextLine();

        Client this_client = new Client(in);
        this_client.play(servername, PORT);

    }

    public void play(String addr, int port) {
        try {
            // initialize variables
            Socket c_sock = new Socket(addr, port);
            byte[] resp = new byte[1024];
            String msg;
            String cmd;
            boolean cont = true;
            DataInputStream server_in = new DataInputStream(c_sock.getInputStream());
            DataOutputStream server_out = new DataOutputStream(c_sock.getOutputStream());

            System.out.println("Connection successful, waiting for game to start...");

            while (cont) {
                // Read the message from the server
                server_in.read(resp);
                msg = new String(resp);
                msg = msg.substring(0, msg.indexOf('\0'));  // Trim the null values off the end to make things better later
                resp = new byte[1024]; // flush resp
                cmd = msg.split(" ")[0];
                switch (cmd) {
                    case "MESSAGE":
                        System.out.println(msg.substring(8));
                        server_out.write("ACK".getBytes());
                        break;
                    case "INVALID":
                        System.out.println("Invalid column, please choose again.");
                    case "TURN":
                        System.out.print("Drop a token at: ");
                        msg = in.nextLine();
                        server_out.write(("DROP " + msg).getBytes());
                        break;
                    case "WIN":
                        if (msg.charAt(4) == 'T') {
                            System.out.println("Congrats, you win!");
                        } else {
                            System.out.println("Oh no, you lose!");
                        }
                        System.out.println(msg.substring(6));
                        cont = false;
                        break;
                    case "DRAW":
                        System.out.println("Oops, no winner!");
                        System.out.println(msg.substring(5));
                        cont = false;
                        break;
                    default:
                        System.out.println("UNKNOWN: '"+ msg + "'");
                }
            }

            c_sock.close();
            System.out.println("(Press enter to exit)");
            in.nextLine();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
