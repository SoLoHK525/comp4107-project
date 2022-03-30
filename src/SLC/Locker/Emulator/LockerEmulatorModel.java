package SLC.Locker.Emulator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeMap;

public class LockerEmulatorModel {
    private TreeMap<String, Boolean> lockerStatusMap;         // <LockerID, hasLocked>
    public LockerEmulatorModel() {
        lockerStatusMap = new TreeMap<>();

        int numLocker = 16;
        for(int i = 0; i < numLocker; i++) {
            String fakeID = String.format("%04d", i);
            System.out.println(fakeID);
            lockerStatusMap.put(fakeID, true);
        }
    }

    public ArrayList<String> GetAllLockerID() {
        return new ArrayList<>(lockerStatusMap.keySet());
    }
}


