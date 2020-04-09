# AI

Server side (and single player mode) control the behavior of `Bot` entities,
Clients only get their gunshot and vector updates. 

Bot behaviour is driven by an adrenaline level which dictates the current executing goal (or general mood)
and sensory input which affects the adrenaline level. Each goal brings a different general objective a bot tries
to achieve. Adrenaline level goes down naturally if no further input is provided.

Bots have 4 sensors: VISUAL, TACTILE, AUDIO, DAMAGE

_Some features below are not yet implemented but are planned._

## Wander
No specific aim, just walks back and forth. Looks towards a close gunshot direction.
* bumps into wall: adrenaline+
* get damaged: adrenaline+++
* spot a player in line of sight: adrenaline++ 
* hear a gunshot: adrenaline++ (based on distance)

## Look for player
_Tries to navigate to last known location it spotted the player (not yet implemented, moves randomly)._
Shoots in a direction it sees a player.
* get damaged: adrenaline++
* spot a player in line of sight: adrenaline+
* hear a gunshot: adrenaline+ (based on distance)

## Panic
Moves randomly, shoots any living thing it can see. 
50% of the times shoots towards a close gunshot location without verifying a target (may shoot at terrain).
_Tries to move away from gunshot locations (not yet implemented)._
* spot any living in line of sight: adrenaline+
* hear a gunshot: adrenaline++ (based on distance)
* get damaged: adrenaline++
