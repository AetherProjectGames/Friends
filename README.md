# Friends

## 1. Commands and Permissions

When using BungeeCord, make sure you give the following permissions on BungeeCord!
It does not matter if you are OP or have all permissions on spigot! You will not be able to perform those commands because they are handled trough the proxy and therefor you need these permissions there aswell!

| Commands | Description | Permissions |
| --- | --- | --- |
| /friends add (Name) (Message) | Send a request to a certain player with an optional message | Friends.Commands.Add |
| /friends accept (Name) | Accepts the request from a certain player | Friends.Commands.Accept |
| /friends accept | Accepts the last new request | Friends.Commands.Accept |
| /friends deny (Name) | Denies the request from a certain player | Friends.Commands.Deny |
| /friends deny | Denies the last new request | Friends.Commands.Deny |
| /friends remove (Name) | Removes a player from your friends | Friends.Commands.Remove |
| /friends block (Name) (Note) | Blocks a player with a personal note | Friends.Commands.Block |
| /friends unblock (Name) | Removes a player from your list of blocked players | Friends.Commands.Unblock |
| /friends acceptall | Accepts all open requests | Friends.Commands.AcceptAll |
| /friends denyall | Denies all open requests | Friends.Commands.DenyAll |
| /friends jump (Name) | Jumps to a players server/location | Friends.Commands.Jump
| /friends list | Shows a list of your current friends | Friends.Commands.List |
| /friends status | Shows your current status | - |
| /friends status set (Status) | Change your current status to something new | Friends.Commands.Status.Set |
| /friends status show (Name) | Shows you the status of a certain friend | Friends.Commands.Status.Show |
| /friends msg (Name) (Message) | Send a private message to your friend | Friends.Commands.Msg |
| /msg (Name) (Message) | If enabled, another way to send private messages | Friends.Commands.Msg |
| /friends requests | Toggle wether you want to receive new requests or not | Friends.Commands.Options.Requests |
| /friends messages | Toggle wether you want to receive any kind of message from your friends | Friends.Commands.Options.Messages |
| /friends jumping | Toggle wether you want other players to jump to your location | Friends.Commands.Options.Jumping |
| /friends offlinemode | Toggle wether you want to be shown as offline or online | Friends.Commands.Options.Offline |
| /friends version | Shows the current version of friends | Friends.Commands.Version |
| /friends reload | Reloads all configurations | Friends.Commands.Reload |

Additional Permissions:
- Friends.FriendLimit.Extended | Used to raise the limit of friends as defined in the config.yml
- Friends.Status.ChangeLimit.ByPass | Players with this permission are able to change their status at anytime
- Friends.Commands.Msg.FriendByPass | Allows a player to send private messages to someone without the need of beeing friends
- Party.Commands.OpenGUI | Allows the usage of /party to open the PartyGUI ( If enabled in the party.yml )


## 2. Setup

### 2.1 Single-Server:

To use friends on your server, you simply just need to put in your plugins folder and restart your server.
You do not have to change anything else.

MySQL:
If you want to use MySQL, you additionally need to edit the MySQL.yml which was generated inside plugins/FriendsRELOADED.
Fill in your login-informations and restart your server a second time.

Friends should be setup correctly and is ready to use

### 2.2 BungeeCord-Networks:

To install friends on your Network you have to put FriendsRELOADED on every server you want the players to use the Inventory. ( For example: lobbys ).
You do not need to put Friends on servers you do not want players to use the Friends-Inventory.
After you are done, restart your servers.

Navigate into /plugins/FriendsRELOADED of every server and open up the config.yml's
You are searching for an option called 'BungeeMode'. 
Make sure to enable this option.

Navigate back into the FriendsRELAODED folder of every server and open up your MySQL.yml's
Fill in your login-informations and restart your servers a second time.

The next step is to download and install Friends-BungeeAddon on your BungeeCord-Server
Do NOT put the BungeeAddon on your normal Servers, just put it on your Proxy and you are good to go.
Download: https://www.spigotmc.org/resources/friends2-0-mc1-8-1-10-bungeecord-addon.28909/

You have to setup MySQL on your Friends-BungeeAddon aswell.
Navigate into '/plugins/Friends BungeeAddon' and open up the MySQL.yml
Fill in your login-informations and restart your Proxy.

Everything should be setup correctly now and Friends is ready to use.

To verify everything is working as intended type ingame /friends version. You should be prompted with two separate messages telling you the version of FriendsRELOADED aswell as the Version of the Friends-BungeeAddon

Keep in mind that you need all permissions on BungeeCord, it does not matter if you are op or have the perms on spigot!
