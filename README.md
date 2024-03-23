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

Setup Phase:

![setup_phase](https://github.com/robocup-logistics/rcll-mqtt-bridge/assets/5959988/540d64c9-a40c-4ad4-aeea-4cb480cd716b)

Production Phase:

![production_phase](https://github.com/robocup-logistics/rcll-mqtt-bridge/assets/5959988/fe0d6584-21e9-4c36-ab40-7bf4ee905494)

#### Robot Beacon Signals

The bridge supports publishing Robot Beacon signals for a team.
Topics: `<TEAM>/beacon/R1` `<TEAM>/beacon/R2` `<TEAM>/beacon/R3`
Payload:
`{
  "name": <ROBOT_NAME>,
  "x": <X_COORDINATE>,
  "y": <Y_COORDINATE>,
  "yaw": <YAW>
}`

![robot_beacon_signal](https://github.com/robocup-logistics/rcll-mqtt-bridge/assets/5959988/10fdc2e1-a0c3-4ac9-ad89-87af63f88690)


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

![prepare_BS](https://github.com/robocup-logistics/rcll-mqtt-bridge/assets/5959988/2c22239b-87c9-4c93-b4c5-d7eacbea3693)

##### Cap Station
Topic: `<TEAM>/prepare/<CAP_STATION>`
Payloads:
- `RetrieveCap`
- `MountCap`

`CAP_STATION` is which station to use, it is either `CS1` or `CS2`.

![prepare_CS](https://github.com/robocup-logistics/rcll-mqtt-bridge/assets/5959988/67e88ed0-6899-4d63-a107-333e48026931)


##### Ring Station
Topic: `<TEAM>/prepare/<RING_STATION>`
Payloads:
- `Blue`
- `Green`
- `Orange`
- `Yellow`

`RING_STATION` is which station to use, it is either `RS1` or `RS2`.

![prepare_RS](https://github.com/robocup-logistics/rcll-mqtt-bridge/assets/5959988/533a1b8f-e295-48d7-8c83-f7aa25a2d7ac)


##### Delivery Station
Topic: `<TEAM>/prepare/DS`
Payloads:
- `<ORDER_ID>`

`ORDER_ID` is the id of the delivered order.

![prepare_DS](https://github.com/robocup-logistics/rcll-mqtt-bridge/assets/5959988/78f53e2c-7ff7-4c9a-80a5-4f140290e017)


### Missing Features

- Publishing Robot Beacon Signals
- Keep track of game state. Like once a Cap Station was prepared remember that this was done, then we could publish a more strong game state which does not need to be kept in the teams planning implementation but can be handled here.
