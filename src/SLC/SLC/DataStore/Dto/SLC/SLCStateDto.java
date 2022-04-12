package SLC.SLC.DataStore.Dto.SLC;

import SLC.SLC.DataStore.Dto.Common.LockerDto;
import SLC.SLC.DataStore.Interface.Locker;
import SLC.SLC.DataStore.SerializableDto;

import java.util.ArrayList;
import java.util.HashMap;

public class SLCStateDto extends SerializableDto {
    public ArrayList<LockerDto> lockers;
    public HashMap<String, String> checkInPackage;
}
