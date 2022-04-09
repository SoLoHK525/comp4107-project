package SLC.SLC.DataStore.Dto.CheckIn;

import SLC.SLC.DataStore.SerializableDto;

/**
 * Message for check in
 */
public class CheckInDto extends SerializableDto {
    public String access_code;
    public String barcode;
    public int timestamp;
}
