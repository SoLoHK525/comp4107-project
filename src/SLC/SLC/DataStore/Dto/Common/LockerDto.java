package SLC.SLC.DataStore.Dto.Common;

import SLC.SLC.DataStore.Interface.LockerSize;

public class LockerDto {
    public String slotId;
    public LockerSize size;
    public boolean locked;
    public boolean containPackage;
    public boolean reserved;
    public int lastUpdate; // timestamp
}
