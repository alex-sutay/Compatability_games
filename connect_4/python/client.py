import socket


PORT = 42000  # default port. TODO add commandline reading to change it


def play(addr, port):
    """
    Connect to a server and begin the game
    :param addr: address to connect to
    :param port: port used to connect
    :return: None
    """
    c_sock = socket.socket()
    c_sock.connect((addr, port))
    print('Connection successful, waiting for the game to start...')
    # game loop
    while True:
        # todo add network error handling
        packet = c_sock.recv(1024).decode()
        cmd = packet.split(' ')[0]
        if cmd == 'MESSAGE':
            print(' '.join(packet.split(' ')[1:]))
            c_sock.send('ACK'.encode())
        elif cmd == 'TURN':
            col = input('Drop a token at: ')
            c_sock.send(f'DROP {col}'.encode())
        elif cmd == 'INVALID':
            print('Invalid column, please choose again.')
            col = input('Drop a token at: ')
            c_sock.send(f'DROP {col}'.encode())
        elif cmd == 'WIN':
            print('Congrats, you win!' if packet.split(' ')[1] == 'T' else 'Oh no, you lose!')
            print(' '.join(packet.split(' ')[2:]))
            break
        elif cmd == 'DRAW':
            print('Oops, no winner!')
            print(' '.join(packet.split(' ')[1:]))
            break
    input('(press enter to exit)')


def main():
    addr = input('Connect to: ')
    play(addr, PORT)


if __name__ == '__main__':
    main()
