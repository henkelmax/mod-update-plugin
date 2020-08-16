# Forge Update Gradle Plugin ![GitHub Workflow Status](https://img.shields.io/github/workflow/status/henkelmax/forge-update-plugin/Build) ![GitHub issues](https://img.shields.io/github/issues-raw/henkelmax/forge-update-plugin) ![GitHub release (latest by date)](https://img.shields.io/github/v/release/henkelmax/forge-update-plugin?include_prereleases)

A Gradle plugin for Forge mods to push updates to the [Mod Update Server](https://github.com/henkelmax/mod-update-server).

## Example Usage

- Create a text file called `forge_update_api_key.txt` or a environment variable called `FORGE_UPDATE_API_KEY` containing your API key
- Make sure you add your `forge_update_api_key.txt` to the `.gitignore`
- Create a file called `changelog.md` containing a list of your changes
- Add the following to your gradle buildscript

*build.gradle*
``` groovy
buildscript {
    repositories {
        ...
        maven { url = 'https://maven.maxhenkel.de/repository/public' }
    }
    dependencies {
        ...
        classpath group: 'de.maxhenkel.forge-update', name: 'forge-update', version: '1.0.2'
    }
}
...
apply plugin: 'forge-update'
...
forgeUpdate {
    def messages = []
    file('changelog.md').eachLine { String line ->
        if (line.trim().startsWith('-')) {
            messages.add(line.replaceFirst('-', '').trim())
        }
    }
    def apiKeyFile = file('forge_update_api_key.txt')
    if (System.env.FORGE_UPDATE_API_KEY != null) {
        apiKey = System.env.FORGE_UPDATE_API_KEY;
    } else if (apiKeyFile.exists()) {
        apiKey = apiKeyFile.text
    } else {
        apiKey = ""
    }
    serverURL = <YOUR SERVER URL>
    modID = <YOUR MOD ID>
    gameVersion = <YOUR MODS MINECRAFT VERSION>
    modVersion = <YOUR MOD VERSION>
    releaseType = <YOUR RELEASE TYPE>
    tags = <TRUE IF THIS UPDATE IS RECOMMENDED> ? ['recommended'] : []
    updateMessages = messages
}
```

Running the gradle task `forgeUpdate` uploads this update to your server.