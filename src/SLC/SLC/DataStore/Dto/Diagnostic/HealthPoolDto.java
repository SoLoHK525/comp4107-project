package SLC.SLC.DataStore.Dto.Diagnostic;

import SLC.SLC.DataStore.Dto.Common.LockerDto;
import SLC.SLC.DataStore.Dto.Common.LockerStatusDto;
import SLC.SLC.DataStore.Interface.Locker;
import SLC.SLC.DataStore.SerializableDto;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;

/**
 * health pooling
 */
public class HealthPoolDto extends SerializableDto {
    public boolean isOctopusCardReaderOnline;
    public boolean isTouchScreenOnline;
    public boolean isBarcodeReaderOnline;
    public boolean isLockerControllerOnline;
    public int lastUpdate;
    public String lockers;
}
