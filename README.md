# RCLL Mqtt Bridge 
This is a tool to wrap the communication of the [rcll-refbox](https://github.com/robocup-logistics/rcll-refbox) with a team over json via mqtt. The advantage for this is that a new team does not need to implement the protocol.

### Building
The build is done via gradle, run `./gradlew build`

### Running
If you are using the [rcll-get-started](https://github.com/robocup-logistics/rcll-get-started) repositor to start the refbox, there is build in support for this via the environment, please take a look there.
If you want to directly start it you can use a command like:<br />
` java -jar mqtt-bridge-0.1-all.jar  -b tcp://localhost:1883 -k randomkey -r localhost -t GRIPS`<br />
, note that all the arguments are required. Note that if you miss an argument it will print out a usage message.

### Features

Implemented:
- Messages Refbox to Public
- Messages Refbox to Private
- Prepare Machines

#### Messages Refbox to Public/Private
Best is you use the [mqtt explorer](http://mqtt-explorer.com/) to take a look what is published where. But there are 2 large groups (public/private) which publish the messages that are received on the according refbox peer. See images below.

TODO INSERT IMAGES

#### Prepare Machines
The bridge is also listening to the following topics:
Team is the value of the configure team name, i.e with the above starting command the name would be `GRIPS`. This applyes to all machine types below.
##### Base Station
Topic: `<TEAM>/prepare/BS/<SIDE>`
Payloads:
- `Red`
- `Silver`
- `Black`

 `SIDE` is which side of the station to move the base to, it can either be `input` or `output`.

TODO insert GIF EXAMPLE

##### Cap Station
Topic: `<TEAM>/prepare/<CAP_STATION>`
Payloads:
- `RetrieveCap`
- `MountCap`

  `CAP_STATION` is which station to use, it is either `CS1` or `CS2`.
- 
TODO insert GIF EXAMPLE

##### Ring Station
Topic: `<TEAM>/prepare/<RING_STATION>`
Payloads:
- `Blue`
- `Green`
- `Orange`
- `Yellow`

`RING_STATION` is which station to use, it is either `RS1` or `RS2`.

TODO insert GIF EXAMPLE

##### Delivery Station
Topic: `<TEAM>/prepare/DS`
Payloads:
- `<ORDER_ID>`

`ORDER_ID` is the id of the delivered order.

### Missing Features

- Publishing Robot Beacon Signals