# LoraLuftdatenForwarder
Bridge software for receiving airborne particulate matter data from TheThingsNetwork and forwarding it to luftdaten.info

# Building the application
Execute the following commands:
* cd LoraLuftdatenForwarder
* cd gradle
* ./gradlew distTar (for Linux)
* gradlew distZip (for Windows)

A compressed distribution archive is now available in LoraLuftdatenForwarder/build/dist
Uncompress this and run:
  bin/LoraluftdatenForwarder
