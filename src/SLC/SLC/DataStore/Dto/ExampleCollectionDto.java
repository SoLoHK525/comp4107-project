package SLC.SLC.DataStore.Dto;

import SLC.SLC.DataStore.SerializableDto;

import java.util.ArrayList;
import java.util.Arrays;

public class ExampleCollectionDto extends SerializableDto {
    public ArrayList<TestObjectDto> testObjects = new ArrayList<>();

    @Override
    public String toString() {
        String buffer = "";

        for(TestObjectDto obj : testObjects) {
            buffer += obj.name + "\n";
        }

        return buffer;
    }
}
