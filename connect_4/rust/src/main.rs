use std::io;
//use socket2::{Socket, Domain, Type};
//use std::net::SocketAddr;

static PORT: i32 = 42000;

fn play(addr: String, port: i32) {
    println!("Connection successful, waiting for the game to start...");
    let mut packet = String::new();
    let mut cmd = String::new();
    let mut resp = String::new();
    /*
        let socket = Socket::new(Domain::IPV4, Type::STREAM, None)?;
        let address: SocketAddr = addr.parse().unwrap();
        socket.bind(&address.into())?;
        socket.listen(port);*/

    loop {
        io::stdin().read_line(&mut packet).expect("Failed to read line");
        packet = String::from(packet.trim());
        let mut split = packet.split(" ");
        cmd = String::from(split.collect::<Vec<&str>>()[0]);
        match cmd.as_str() {
            "MESSAGE" => {
                println!("{}", packet);
                println!("ACK");  // Change to send
            }

            "TURN" => {
                print!("Drop a token at: ");
                io::stdin().read_line(&mut resp).expect("Failed to read line");
                println!("DROP {}", resp);  // Change to send
            }

            "INVALID" => {
                print!("Invalid column, please choose again.\nDrop a token at: ");
                io::stdin().read_line(&mut resp).expect("Failed to read line");
                println!("DROP {}", resp);  // Change to send
            }

            "WIN" => {
                println!("I'll do this in a sec");
                break;
            }

            "DRAW" => {
                println!("This one too");
                break;
            }

            _ => println!("UNKNOWN '{}'", packet)
        }
        cmd = String::from(""); // clear cmd
        packet = String::from("");  // clear packet
    }

    // Outside game loop
    // Close the socket here
    println!("(press enter to exit)");
    io::stdin().read_line(&mut resp).expect("Failed to read line");

}

fn main() {
    let mut addr = String::new();

    println!("Connect to: ");
    io::stdin().read_line(&mut addr).expect("Failed to read line");
    addr = String::from(addr.trim());  // trim the newline off the end
    play(addr, PORT);
}
