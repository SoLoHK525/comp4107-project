package SLC.SLC.DataStore.Dto.CheckIn;

import SLC.SLC.DataStore.Interface.LockerSize;
import SLC.SLC.DataStore.SerializableDto;

/**
 * Server request to reserve a locker
 */
public class ReservationRequestDto extends SerializableDto {
    public LockerSize lockerSize;
}
