# AI

Server side (and single player mode) control the behavior of `Bot` entities,
Clients only get their gunshot and vector updates. 

Bot behaviour is driven by an adrenaline level which dictates the current executing goal (or general mood)
and how sensory input affects the adrenaline level. Each goal brings a different general objective a bot tries
to achieve. Adrenaline level goes down naturally if no further input is provided.

Bots have 4 sensors: visual, tactile, audio, damage

## Wander
No specific aim, just walks back and forth. Looks towards a close gunshot direction. Easily agitated.
Adrenaline level below 100.
* bumps into wall: adrenaline+
* get damaged: adrenaline+++
* spot a player in line of sight: adrenaline++ 
* hear a gunshot: adrenaline++ (based on distance)

## Look for player
Tries to navigate to last known location it spotted the player, able to jump.
Shoots in a direction it sees a player. Tries to shoot terrain that gets in the way. 
Adrenaline level between 100 and 1500.
* get damaged: adrenaline++
* spot a player in line of sight: adrenaline+
* hear a gunshot: adrenaline+ (based on distance)

## Panic
Tries to move away from gunshot locations. Fires random shots towards close by gunshot directions.
Shoots all living things if it sees them. Able to jump and move faster than normal.
Adrenaline level above 1500.
* spot any living in line of sight: adrenaline+
* hear a gunshot: adrenaline+ (based on distance)
* get damaged: adrenaline+
