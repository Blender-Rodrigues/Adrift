# Adrift

## Overview

Real time 2D shooter game inspired by Liero video game.
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

Build and run jar.

In menu:
* keyboard arrow kes to navigate
* `Enter` key to select a game mode
* `Esc` to close the game

In game: 
* use `A` and `D` keys to move sideways, 
* `W` to jump, 
* numbers `1`, `2`, `3` to select active equipment and 
* mouse cursor to aim and left click to shoot,
* `Esc` to return to menu

## Optionally the jar accepts either of the following set of arguments

* client [server ip, defaults to 127.0.0.1] [server tcp port, defaults to 8880] [player name, defaults to Unknown]
* server [server tcp port, defaults to 8880]

## Documentation

* [Technical documentation](docs/technical.md)
* [Development guidelines](docs/development.md)
