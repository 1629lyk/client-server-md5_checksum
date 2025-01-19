import socket
import hashlib
import os

def compute_md5(file_path):
    """Compute MD5 checksum of the specified file."""
    md5_hash = hashlib.md5()
    with open(file_path, "rb") as f:
        for chunk in iter(lambda: f.read(4096), b""):
            md5_hash.update(chunk)
    return md5_hash.hexdigest()

def main():
    host = ""          # Listen on all available interfaces
    port = 5003        # Server port
    file_name = "mydata.txt"

    # Create a TCP socket
    server_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    server_socket.bind((host, port))
    server_socket.listen(1)
    print(f"[Server] Listening on port {port}...")

    while True:
        conn, addr = server_socket.accept()
        client_ip, client_port = addr
        print(f"[Server] Connection established with {client_ip}:{client_port}")

        try:
            if not os.path.exists(file_name):
                print(f"[Server] File not found: {file_name}")
                conn.sendall(b"Error: File not found")
                conn.close()
                continue

            # Compute MD5 checksum
            server_checksum = compute_md5(file_name)
            print(f"[Server] MD5 of {file_name}: {server_checksum}")

            # Send the MD5 checksum first (in UTF-8)
            conn.sendall((server_checksum + "\n").encode("utf-8"))

            # Send the file contents
            with open(file_name, "rb") as f:
                while True:
                    data = f.read(4096)
                    if not data:
                        break
                    conn.sendall(data)

            print(f"[Server] File '{file_name}' sent successfully to {client_ip}:{client_port}")
        except Exception as e:
            print(f"[Server] Error: {e}")
        finally:
            conn.close()

if __name__ == "__main__":
    main()
