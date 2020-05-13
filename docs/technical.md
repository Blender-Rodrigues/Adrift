# Documentation

## Used technologies

* Java 8, Gradle
* [Lightweight Java Game Library](https://www.lwjgl.org/) - Graphics / UI input
* [Google Guice](https://github.com/google/guice) - Dependency injection
* [Apache Log4j](https://logging.apache.org/log4j/2.x/) - Logging
* [Junit](https://junit.org/junit5/), [Mockito](https://site.mockito.org/), [AssertJ](https://assertj.github.io/) - Testing
* [Docker](https://www.docker.com/) - Deployment

## Project structure

Package names specified relative to root `ee.taltech.iti0200`.

### Foundation

* `menu` - outer wrapper loop for client 
* `di` - setting up modules and dependencies for selected game mode
* `application` - initialization of modules per selected game mode, registering event handlers, main game loop and timer, cleanup on termination  

### Modules

Each game mode loads its own set of modules, for brevity S for Server, C for Client, P for Single-Player.

* `ai` - bot behaviour, creation and re-spawning (_S/P_)
* `graphics` - rendering world, gui and entities (_C/P_)
* `input` - capturing player keyboard and mouse input (_C/P_)
* `network` - network thread management, syncing game and input events between the server and connected client(s) (_S/C_)
* `physics` - motion, gravity, collisions, bounce and slide (_S/C/P_)

### Domain

Package `domain` defines the world, its layout, both living and non-living entities and equipment.
Contains event bus, game events and listeners.

## Networking

During a multiplayer game the Server side is keeping the game state, clients send their user input updates and received
both game events and position updates for other moving bodies in the server world (including other players).

#### Overview

Initial connection establishing is described on a separate [page](network.md)

* Server has 1 TCP port which is shared by a general `Registrar` thread + 2x per client `Listener` & `Sender` threads.
* Server has a separate UDP port for each client which is shared by separate `Listener` & `Sender` threads.
* Client has 1 TCP port shared by separate `Listener` & `Sender` threads.
* Client has 1 UDP port shared by separate `Listener` & `Sender` threads.

#### Events and game loop

* At the start of a game loop, messages from the network threads are removed from their incoming queues and forwarded to event bus.
* Event listeners process those messages and stop their propagation.
* During the game loop the rest of the modules (namely `ai`, `input` and `physics`) fire additional events.
At the end of a game loop messages that are still alive and have an external `Receiver` are converted to network messages
and passed on to appropriate outbox threads.
* At this point the client side also includes player vector updates and the server side movable body updates to the outbox.
* Main thread on each side then sleeps to maintain a stable tick rate and outbound network threads send off the messages in their outboxes.

## AI

The `ai` module tries to respawn new bots to world spawn locations to supersede the player count in the world.

Bot behaviour is driven by an adrenaline level which dictates the current executing goal (or general mood)
and how sensory input affects the adrenaline level. Each goal brings a different general objective a bot tries
to achieve. Adrenaline level goes down naturally if no further input is provided.

Bots have 4 sensors: visual, tactile, audio, damage

Bots moods:

* wander - no specific goal, easily agitated by damage, spotting a player and noise
* look for player - tries to navigate towards last seen player location or a close by gunshot
* panic - tries to move away for gunshot locations, also aggressive towards other bots

(see [here](ai.md) for longer description)
