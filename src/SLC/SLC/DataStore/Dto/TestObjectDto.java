package SLC.SLC.DataStore.Dto;

import SLC.SLC.DataStore.SerializableDto;

public class TestObjectDto extends SerializableDto {
    public String name;
    public String id;

    public TestObjectDto(String name, String id) {
        this.name = name;
        this.id = id;
    }
}
