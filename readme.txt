COMP4107 Project

Github Repository URL: https://github.com/SoLoHK525/comp4107-project

Entrypoint: SLCEmulatorStarter.java


=========== How to build 

(1) JAR as build Target:
	1. Open this project using IntelliJ IDEA
	2. Click on File -> Project Structure -> Artifacts
		-> Add JAR (From module with dependencies)
		-> Select [SLCEmulatorStarter] as the Main Class
	3. Click on Build -> Build Artifact...
	
	The output jar will be located inside /out/artifacts/<name_of_artifacts>/<name_of_artifacts>.jar
	
(2) Run in development build:
	1. Open this project using IntelliJ IDEA
	2. Create a new "Run Configuration"
		-> Add Application
		-> Select SLC.SLCEmulatorStarter as the Main Class
	3. Run the Configuration
	
	
	
=========== How to run
1. Create a ./etc/SLC.cfg within the current working directory (where the jar or the project files are)
2. The sample configuration file can be found in ./etc/SLC.cfg.example
3. Run the program
	1. JAR:
		- java -jar <jar_file_name>.jar
	2. Development Build
		- Click run within IntelliJ IDEA

=========== How to stop
	Supposedly you click the "Close" button in any of the SLC's window could stop the program.
	However, due to the multi-threaded design, you might need to manually kill the java process.


=========== Project Structure

	./src/SLC/
	│
	├── BarcodeReaderDriver                 # Barcode Emulator
	├── HWHandler                           # Default files
	├── Locker.Emulator                     # Locker Emulator
	├── OctopusCardReaderDriver             # OctopusCard Reader Emulator
	├── ServerDriver                        # Server Emulator
	├── SLC                                 # Sub-package used by the SLC
	│   ├── DataStore/                      # Data Transfer Objects for transferring and saving Java Objects
	│   │   ├── Dto/                        # All the data transfer objects used between thread or emulator communications
	│   │   └── SerializableDto.java        # All the data transfer objects used between thread or emulator communications
	│   ├── Handlers.MouseClick/            # Virtualized button handlers for touch screen emulator on SLC
	│   ├── Services/                       # SLC Services (Checkout, CheckIn, Diagnostic, etc...)
	│   └── SLC.java                        # SLC main instance
	├── TouchDisplayHandler/                # Touch Display Emulator
	├── SLCEmulatorStarter.java             # Run SLC with Emulators
	└── SLCStarter.java                     # Base SLC


Achieved Project Goals:
	✓ Checking in of packages by staff of delivery company
	✓ Checking out of packages by ordinary users
	✓ Health poll of hardware devices
	✓ System diagnostic for showing system status (to standard output)