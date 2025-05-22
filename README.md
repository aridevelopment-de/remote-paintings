# RemotePaintings

Ever wanted to override Minecraft paintings but creating a resource pack was too much work? 
Fear not, RemotePaintings is here to help! You can now finally override Minecraft paintings with your own images,
right from the game!

## Examples
<details>
<summary><strong>Example with PNG/JPEG</strong></summary>
<p float="left" align="left">
<img src="https://b.catgirlsare.sexy/Z4UNNEN2C2H0.png" width="600px">
<img src="https://b.catgirlsare.sexy/JqZGLzEY0tPF.png" width="600px">
</p>
</details>

<details>
<summary><strong>Example with GIFs</strong></summary>
<a href="https://youtu.be/U34k-pZf6IY"><img width="600px" src="https://b.catgirlsare.sexy/SijZrVIQvaas.png"></a>
</details>

## Commands
Generally, every command is available via the ``/remotepaintings`` command and should be self-explanatory.

- ``/remotepaintings paintingInfo``: Shows information about the painting being looked at
- ``/remotepaintings info``: Shows information about every painting override
- ``/remotepaintings override <id> <url>``: Override the specified painting id (minecraft identifier!) with the specified URL
- ``/remotepaintings overrideTargeted <url>``: Override the painting being looked at with the specified URL
- ``/remotepaintings unloadPainting <id>``: Unload the specified painting id (minecraft identifier!)
- ``/remotepaintings reloadImages``: Refetch and reload all images

For configs, these commands are available:
- ``/remotepaintings loadConfig <url>``: Loads a json config from any url (raw hastebin for example) and saves the url
- ``/remotepaintings reloadConfig``: Reloads the config from the saved url
- ``/remotepaintings saveConfig``: Uploads the current overrides in a new hastebin and saves the url locally

## Config
The config is being provided by [owo-lib](https://github.com/wisp-forest/owo-lib) and thus reachable via any modern ModMenu.
In the config itself the current remote paintings config url and the hastebin upload server can be specified.

## How it works
After executing the ``override`` command, the texture gets fetched and is being registered in the TextureManager (thus being loaded in RAM).
For GIFs, all frames are being concatenated in a single row next to each other. Mind that this file might get very large if the GIF is too big.

The mod then injects into the painting renderer to avoid rendering the actual painting. Instead, it renders the custom remote painting
on top of it.

This approach is rather simple compared to the first two attempts I made, in which I tried to modify the SpriteAtlas itself.

### Questions, Contributing and Licensing

If you have any questions, feel free to [contact me](https://ari24.dev) or open an issue. I'd be glad to help!  
For contributions, please open a detailed pull request with your changes. Would be nice if you could use [conventional commits](https://www.conventionalcommits.org/en/v1.0.0/).  
This project is licensed under the MIT license.