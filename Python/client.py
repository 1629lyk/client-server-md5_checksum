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
    server_ip = "127.0.0.1"  # Add the server's ip address
    port = 5003             # Must match the server's port
    save_file_path = "mydata_client.txt"

    # Create a TCP socket to connect to the server
    client_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    try:
        client_socket.connect((server_ip, port))
        print(f"[Client] Connected to server {server_ip}:{port}")

        # Receive the MD5 checksum from the server 
        server_checksum = ""
        data = b""
        while True:
            chunk = client_socket.recv(1)  # read one byte at a time until newline
            if not chunk:
                break
            data += chunk
            if chunk == b"\n":
                break

        server_checksum = data.decode("utf-8").strip()
        print(f"[Client] MD5 checksum received from server: {server_checksum}")

        # Receive the file data
        with open(save_file_path, "wb") as f:
            file_received = False
            while True:
                chunk = client_socket.recv(4096)
                if not chunk:
                    break
                f.write(chunk)
                file_received = True

        if file_received:
            print(f"[Client] File received and saved as '{save_file_path}'")

            # Compute MD5 after the file is fully closed
            client_checksum = compute_md5(save_file_path)
            print(f"[Client] MD5 checksum of received file: {client_checksum}")

            # Compare with server’s checksum
            if client_checksum == server_checksum:
                print("[Client] Checksum verification successful! File integrity verified.")
            else:
                print("[Client] Checksum verification failed! File may be corrupted.")
        else:
            print("[Client] No file data received from the server.")

    except Exception as e:
        print(f"[Client] Error: {e}")
    finally:
        client_socket.close()

if __name__ == "__main__":
    main()
