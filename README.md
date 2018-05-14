# RPGPets

Yet another pet plugin, but this one actually makes them useful. See the overview
[here](https://www.spigotmc.org/resources/rpgpets.42653/)
(Requires [SpigotMC](https://www.spigotmc.org) account)

## Development Environment

Compilation requires some dependencies in your local maven repository,
namely Spigot (Run BuildTools, multiple versions required!), Feudal (Premium resource), Vault, Essentials, Towny, WorldGuard and ProtocolLib.
For the currently required version see [build.gradle](build.gradle).

A test server can be installed into `testserver/` using the following commands:

    $ mkdir -p testserver; gradle shadow installServer install
