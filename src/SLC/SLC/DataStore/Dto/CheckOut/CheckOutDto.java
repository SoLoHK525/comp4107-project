package SLC.SLC.DataStore.Dto.CheckOut;

import SLC.SLC.DataStore.SerializableDto;

public class CheckOutDto extends SerializableDto {
    public String access_code;
    public String octupusCardNumber;
    public float amount;
    public int timestamp;
}
