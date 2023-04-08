# Mod Update Gradle Plugin

A Gradle plugin for Minecraft mods to push updates to the [Mod Update Server](https://github.com/henkelmax/mod-update-server).

## Useful Links

- [Maven Releases](https://maven.maxhenkel.de/#/releases/de/maxhenkel/mod-update/mod-update)
- [Mod Update Server](https://github.com/henkelmax/mod-update-server)

## Example Usage

- Create a text file called `mod_update_api_key.txt` or a environment variable called `MOD_UPDATE_API_KEY` containing your API key
- Make sure you add your `mod_update_api_key.txt` to the `.gitignore`
- Create a file called `changelog.md` containing a list of your changes
- Add the following to your gradle buildscript


``` groovy
pluginManagement {
    repositories {
        maven { url = 'https://maven.maxhenkel.de/repository/public' }
    }
}
```

*build.gradle*
``` groovy
plugins {
    id 'mod-update' version '2.0.0'
}

modUpdate {
    def messages = []
    file('changelog.md').eachLine { String line ->
        if (line.trim().startsWith('-')) {
            messages.add(line.replaceFirst('-', '').trim())
        }
    }
    def apiKeyFile = file('mod_update_api_key.txt')
    if (System.env.MOD_UPDATE_API_KEY != null) {
        apiKey = System.env.MOD_UPDATE_API_KEY;
    } else if (apiKeyFile.exists()) {
        apiKey = apiKeyFile.text
    } else {
        apiKey = ""
    }
    serverURL = <YOUR SERVER URL>
    modID = <YOUR MOD ID>
    gameVersion = <YOUR MODS MINECRAFT VERSION>
    modLoader = <"forge" | "fabric">
    modVersion = <YOUR MOD VERSION>
    releaseType = <YOUR RELEASE TYPE>
    tags = <TRUE IF THIS UPDATE IS RECOMMENDED> ? ['recommended'] : []
    updateMessages = messages
}
```

Running the gradle task `modUpdate` uploads this update to your server.