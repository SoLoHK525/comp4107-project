COMP4107 Project

Github Repository URL: https://github.com/SoLoHK525/comp4107-project

Entrypoint: SLCEmulatorStarter.java


Achieved Project Goals:
	✓ Checking in of packages by staff of delivery company
	✓ Checking out of packages by ordinary users
	✓ Health poll of hardware devices
	✓ System diagnostic for showing system status (to standard output)



Project Structure

	./src/SLC/
	├──────────────────────────────────────────────────────
	├── BarcodeReaderDriver      # Barcode Emulator
	├── HWHandler                # Default files
	├── Locker.Emulator          # Locker Emulator
	├── OctopusCardReaderDriver  # OctopusCard Reader Emulator
	├── ServerDriver             # Server Emulator
	├── SLC                      # Sub-package used by the SLC
	│   ├── DataStore/           # Data Transfer Objects for transferring and saving Java Objects
	│   │   └── Dto/             # All the data transfer objects used between thread or emulator communications
	│   ├── Handlers.MouseClick/ # Virtualized button handlers for touch screen emulator on SLC
	│   ├── Services/            # SLC Services (Checkout, CheckIn, Diagnostic, etc...)
	│   └── SLC.java             # SLC main instance
	├── TouchDisplayHandler/     # Touch Display Emulator
	├── SLCEmulatorStarter.java  # Run SLC with Emulators
	└── SLCStarter.java          # Base SLC
