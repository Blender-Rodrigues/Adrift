# Escape from Eros

## Overview

2D shooter game inspired by Liero video game and loosely themed after The Expanse TV series.
Can be played both in single-player and multiplayer mode.

Goal of the game: shoot other bots and players, get 20 kills to win. 

* Destructible terrain
* Entities drop loot upon death
* 3 Types of different guns
* Health globes and shields
* Players have 10 lives.
* Bots have attitudes and respawn upon death.

## Play the game

Requirements: java 8+

Download jar from [our static website](http://104.248.243.136/).

Run the jar with `java -jar path-to-file`.

Our game server `104.248.243.136:8880` is already configured as the default connection.

## Optionally you can pass the following jar arguments

* client [server ip, defaults to 104.248.243.136] [server tcp port, defaults to 8880] [player name, defaults to Unknown]
* server [server tcp port, defaults to 8880]

## Documentation

* [Technical documentation](docs/technical.md)
* [Development guidelines](docs/development.md)
