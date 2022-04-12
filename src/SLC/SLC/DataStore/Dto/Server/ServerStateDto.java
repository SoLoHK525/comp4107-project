package SLC.SLC.DataStore.Dto.Server;

import SLC.SLC.DataStore.Dto.Common.LockerDto;
import SLC.SLC.DataStore.SerializableDto;

import java.util.HashMap;

public class ServerStateDto extends SerializableDto {
    public HashMap<String, LockerDto> barcodeToLockerMap;
    public HashMap<String, String> accessCodeToBarcodeMap;
}
