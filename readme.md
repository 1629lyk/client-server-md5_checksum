# Client-Server File Transfer with MD5 Verification

This repository contains a simple client-server application demonstrating file transfer over TCP sockets, along with MD5 checksum verification to ensure file integrity.

---

## Features

1. **MD5 Checksum**:  
   - The server computes the file’s MD5 checksum before sending it.  
   - The client computes its own checksum on the received file.  
   - Checksums are compared to verify that the file is received intact.

2. **Socket Programming**:  
   - The server listens on a specified port (e.g., 5002).  
   - The client connects to the server, receives the file, and saves it locally.

3. **Readability & Logging**:  
   - Clear logging on both client and server to track progress and any errors.

---

## How It Works

1. **Server**:  
   - Binds to a TCP socket on a well-known port.  
   - Waits for incoming client connections.  
   - Computes the MD5 hash of the file (`mydata.txt`).  
   - Sends the checksum first, followed by the file contents.

2. **Client**:  
   - Connects to the server’s IP and port.  
   - Reads the MD5 checksum sent by the server.  
   - Receives the file in binary mode and saves it (`mydata_client.txt`).  
   - Computes the MD5 of the received file.  
   - Compares both checksums to confirm successful file transfer.

---
