package SLC.SLC.DataStore.Dto.CheckOut;

import SLC.SLC.DataStore.SerializableDto;

public class CheckOutDto extends SerializableDto {
    public String access_code;
    public String octopusCardNumber;
    public double amount;
    public int timestamp;

    public CheckOutDto (String access_code, String octopusCardNumber, double amount, int timestamp) {
        this.access_code = access_code;
        this.octopusCardNumber = octopusCardNumber;
        this.amount = amount;
        this.timestamp = timestamp;
    }
}
