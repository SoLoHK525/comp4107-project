package SLC.SLC.DataStore.Dto.Diagnostic;

import SLC.SLC.DataStore.Interface.Locker;
import SLC.SLC.DataStore.SerializableDto;

import java.util.ArrayList;

/**
 * health pooling
 */
public class HealthPoolDto extends SerializableDto {
    public boolean isOctopusCardReaderOnline;
    public boolean isTouchScreenOnline;
    public boolean isBarcodeReaderOnline;
    public boolean isLockerControllerOnline;
    public ArrayList<Locker> lockers;
}
