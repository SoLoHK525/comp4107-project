package SLC.Locker.Emulator;

import java.util.*;

public class LockerEmulatorModel {
    private TreeMap<String, Boolean> lockerStatusMap;         // <LockerID, hasLocked>

    public LockerEmulatorModel() {
        lockerStatusMap = new TreeMap<>();

        int numLocker = 16;
        for (int i = 0; i < numLocker; i++) {
            String fakeID = String.format("%04d", i);
            lockerStatusMap.put(fakeID, true);
        }
    }

    public ArrayList<String> GetAllLockerID() {
        return new ArrayList<>(lockerStatusMap.keySet());
    }

    public void UpdateStatusByID(String id, boolean isLock) {
        lockerStatusMap.replace(id, isLock);
    }

    public boolean GetLockStatusByID(String id) {
        return lockerStatusMap.get(id);
    }
}


