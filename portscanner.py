import socket


def scan_ports(target_host, start_port, end_port):
    try:
        # Resolve target hostname to IP address
        target_ip = socket.gethostbyname(target_host)

        # Scan ports within the specified range
        for port in range(start_port, end_port + 1):
            # Create a socket object
            sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
            sock.settimeout(1)  # Set timeout to 1 second

            # Attempt to connect to the target port
            result = sock.connect_ex((target_ip, port))

            # Check if the port is open or closed
            if result == 0:
                print(f"Port {port}: Open")
            else:
                print(f"Port {port}: Closed")

            sock.close()

    except socket.gaierror:
        print("Hostname could not be resolved.")

    except socket.error:
        print("Could not connect to the server.")


# Get target information from user input
def get_target_info():
    target_host = input("Enter the target hostname or IP address: ")
    start_port = int(input("Enter the starting port number: "))
    end_port = int(input("Enter the ending port number: "))
    return target_host, start_port, end_port


if __name__ == "__main__":
    target_host, start_port, end_port = get_target_info()
    scan_ports(target_host, start_port, end_port)
