package SLC.SLC.DataStore.Dto.Common;

import SLC.SLC.DataStore.Interface.Locker;
import SLC.SLC.DataStore.SerializableDto;

import java.util.ArrayList;

/**
 * Send Locker status to server
 */
public class LockerStatusDto extends SerializableDto {
    public ArrayList<LockerDto> lockers;

}
