package SLC.SLC.DataStore.Dto.CheckIn;

import SLC.SLC.DataStore.Interface.Locker;
import SLC.SLC.DataStore.SerializableDto;

/**
 * SLC response of reserving a locker
 */
public class ReservedResponseDto extends SerializableDto {
    public Locker reservedLocker;
}