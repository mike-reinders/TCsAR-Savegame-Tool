## TCs Auto Rewards: Savegame Tool
This Savegame Tool is used to query information from savegame files.
To export and import into or from json format.
This tool also supports merging multiple savegame files into one.

#### Current Supported Savegame Formats:
- PlayerData version 1.12.6 only
- no support for PacksData yet

### How to Use this Tool

##### for [Windows]  
0.) Install Java JRE 8 (Java Runtime Environment 8), download the TCsAR Savegame Tool [here - SavegameTool.zip](https://github.com/mike-reinders/TCsAR-Savegame-Tool/releases) and extract it on your desktop  
1.) press `win` + `R`, type `cmd.exe` into the text field and click `OK`  
2.) or type `cmd.exe` into the search bar and press enter  
3.) type in the command `cd Desktop` to change the current directory to the sub-directory `Desktop`  
4.) Copy all the savegames to your `Desktop` *(mine are for example `Savegame1.sav`, `Savegame2.sav` and `Savegame3.sav`)*  
5.) In cmd now type `SavegameTool.bat merge TCsAR_PlayerData.sav Savegame1.sav Savegame2.sav Savegame3.sav` and press enter  
  
6.) your Desktop now contains the File TCsAR_PlayerData.sav which contains all the data from the Savegames, `Savegame1.sav`, `Savegame2.sav` and `Savegame3.sav`

### How to install Java JRE 8
1.) Download the latest Java JRE 8 version from [here](https://www.oracle.com/java/technologies/javase-jre8-downloads.html)  
2.) for Windows download `Windows x64 | .exe` (64 Bit) or `Windows x86 | .exe` (32 Bit) (requires an Oracle Account)  
3.) execute the downloaded .exe file, eg. `jre-8u241-windows-x64.exe` and follow the installation.  
4.) that's it.

## Features
- Verify file format and data integrity
- Query playerlist and all informations about one or multiple players at once
- Lookup savegame structure with the debug command
- Merge multiple savegame files
- Convert savegames to human friendly json format
- Convert savegames from json format back to sav format

## Planned Features
- Support for PacksData
- Migration method to migrate savegames from old savegames to new formats (and backwards)

## Build Dependencies
[ARK-Savegame-Toolkit](https://github.com/Qowyn/ark-savegame-toolkit "ARK-Savegame-Toolkit")
