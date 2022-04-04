package SLC.SLC.DataStore;

import SLC.SLC.DataStore.Dto.ExampleCollectionDto;
import SLC.SLC.DataStore.Dto.TestObjectDto;

import java.io.File;
import java.io.IOException;

public class Test {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        ExampleCollectionDto collection = new ExampleCollectionDto();

        collection.testObjects.add(new TestObjectDto("test", "1"));
        collection.testObjects.add(new TestObjectDto("test2", "2"));

        System.out.println(TestObjectDto.from(collection.toBase64()));
        File exampleCollectionFile = new File("example_collection.bin");
        collection.save(exampleCollectionFile);

        System.out.println(ExampleCollectionDto.<ExampleCollectionDto>from(exampleCollectionFile));
    }
}
