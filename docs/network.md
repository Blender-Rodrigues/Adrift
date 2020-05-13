# Network

## Establishing initial connection

[Client]
*  `ClientNetwork` initializes `ConnectionToServer` on startup
* `ConnectionToServer` crates UDP `DatagramSocket` and TCP `Socket`
* sends a `TcpRegistrationRequest` with client UUID and UDP port nr to server
* waits for a response for 30 seconds while retrying every 3 seconds

[Server]
* `ServerNetwork` initializes and runs `Registrar` on a separate thread on startup
* `Registrar` accepts a new TCP connections on `ServerSocket`
* creates 4 threads, 2 for TCP and 2 for UDP with a new `DatagramSocket`
* responds to request with `TcpRegistrationResponse` containing UDP port nr

[Client]
* `ConnectionToServer` binds the received UDP port with its socket output stream
* sends `UdpRegistrationRequest` to server to allow inbound traffic through the firewall
* waits for a response for 30 seconds while retrying every 3 seconds

[Server]
* UDP `Listener` Marks the client connection as finalized and adds it to a `Set` shared by other threads
* sends a `UdpRegistrationResponse`

[Client]
* `ConnectionToServer` sends a TCP `CreatePlayer` message to the server
* waits for a response for 30 seconds while retrying every 3 seconds

[Server]
* `PlayerJoinHandler` adds the player to the world and gives it a position
* sends a TCP `CreateEntity` message to all other clients
* sends a TCP `LoadWorld` message to the joining client containing all the current entities in the world

[Client]
 * `ConnectionToServer` starts up 4 threads for the TCP and UDP connections
 * `ClientNetwork` adds all entities to local world and corrects the player position
