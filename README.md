# The Last Smith - Revived

## Overview

_The Last Smith_ mod Original Minecraft mod repository: https://github.com/0999312/The-Last-Smith

_The Last Smith_ mod will focus on 1.20.1+ versions development in future, while this repository is for the official continue maintenance of The Last Smith mod 1.12.2.

原 Minecraft 模组开源仓库：https://github.com/0999312/The-Last-Smith

《最后的太刀匠人》模组今后将专注于 mc 1.20.1 以上版本的开发, 本仓库为其 1.12.2 版本的官方继续维护。

---

## How to contibute:

This develop environment utilizes [CleanroomMC/TemplateDevEnv](https://github.com/CleanroomMC/TemplateDevEnv), runs on Java 21 and support your latest Java IDE!

Currently utilizies **Gradle 8.12** + **[RetroFuturaGradle](https://github.com/GTNewHorizons/RetroFuturaGradle) 1.4.1** + **Forge 14.23.5.2847**.

With **coremod and mixin support** that is easy to configure.

1. Clone the repository that you have created with this template to your local machine.
2. Make sure IDEA is using Java 21 for Gradle before you sync the project. Verify this by going to IDEA's `Settings > Build, Execution, Deployment > Build Tools > Gradle > Gradle JVM`.
3. Open the project folder in IDEA. When prompted, click "Load Gradle Project" as it detects the `build.gradle`, if you weren't prompted, right-click the project's `build.gradle` in IDEA, select `Link Gradle Project`, after completion, hit `Refresh All` in the gradle tab on the right.
4. Run gradle tasks such as `runClient` and `runServer` in the IDEA gradle tab, or use the auto-imported run configurations like `1. Run Client`.

### Notes:
- Dependencies script in [gradle/scripts/dependencies.gradle](gradle/scripts/dependencies.gradle), explanations are commented in the file.
- Publishing script in [gradle/scripts/publishing.gradle](gradle/scripts/publishing.gradle).
- When writing Mixins on IntelliJ, it is advisable to use latest [MinecraftDev Fork for RetroFuturaGradle](https://github.com/eigenraven/MinecraftDev/releases).
