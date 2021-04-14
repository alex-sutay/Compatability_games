use std::io;
use std::net::{TcpStream, Shutdown};
use std::io::{Read, Write};

static PORT: i32 = 42000;

fn play(addr: String, port: i32) {
    let mut buffer: [u8; 1024];
    let mut packet: String;
    let mut cmd: String;
    let mut end: usize;
    let mut resp = String::new();
    let mut stream = TcpStream::connect(format!("{}:{}", addr, port))
                                            .expect("Failed to connect");

    println!("Connection successful, waiting for the game to start...");

    loop {
        buffer = [0; 1024]; // Clear the buffer
        stream.read(&mut buffer).expect("Network Error");

        // Convert the received data into a string
        packet = String::from_utf8(buffer.to_vec()).expect("Failed to convert bytes");
        end = packet.find('\0').unwrap_or(packet.len());
        packet = packet.drain(..end).collect();

        let split= packet.split(" ");
        cmd = String::from(split.collect::<Vec<&str>>()[0]);

        match cmd.as_str() {
            "MESSAGE" => {
                println!("{}", &packet[8..]);
                stream.write("ACK".as_bytes()).expect("Network Error");
            }

            "TURN" => {
                println!("Drop a token at: ");
                io::stdin().read_line(&mut resp).expect("Failed to read line");
                println!("DROP {}", resp);
                stream.write(format!("DROP {}", resp).as_bytes()).expect("Network Error");
                resp = String::from("");  //Clear resp
            }

            "INVALID" => {
                println!("Invalid column, please choose again.\nDrop a token at: ");
                io::stdin().read_line(&mut resp).expect("Failed to read line");
                println!("DROP {}", resp);
                stream.write(format!("DROP {}", resp).as_bytes()).expect("Network Error");
                resp = String::from("");  //Clear resp
            }

            "WIN" => {
                let split= packet.split(" ");
                cmd = String::from(split.collect::<Vec<&str>>()[1]);
                if cmd == "T" {
                    println!("Congrats, you win!");
                } else {
                    println!("Oh no, you lose!");
                }
                println!("{}", &packet[6..]);
                break;
            }

            "DRAW" => {
                println!("Oops, no winner!");
                println!("{}", &packet[5..]);
                break;
            }

            _ => println!("UNKNOWN '{}'", packet)
        }
    }

    // Outside game loop
    stream.shutdown(Shutdown::Both).expect("TCP failed to close");
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
