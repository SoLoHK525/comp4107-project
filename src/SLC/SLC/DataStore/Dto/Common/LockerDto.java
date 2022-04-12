package SLC.SLC.DataStore.Dto.Common;

import SLC.SLC.DataStore.Interface.LockerSize;
import SLC.SLC.DataStore.SerializableDto;

public class LockerDto extends SerializableDto {
    public String slotId;
    public LockerSize size;
    public boolean locked;
    public boolean containPackage;
    public boolean reserved;
    public int lastUpdate; // timestamp
}
