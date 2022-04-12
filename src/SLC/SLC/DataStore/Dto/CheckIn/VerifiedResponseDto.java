package SLC.SLC.DataStore.Dto.CheckIn;

import SLC.SLC.DataStore.SerializableDto;

/**
 * Server barcode verification response to slc
 * including slotID that correspond to barcode received in BarcodeVerificationDto
 */

public class VerifiedResponseDto extends SerializableDto {
    public boolean verified;
    public String slotID;
}
