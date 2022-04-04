package SLC.SLC.DataStore;

import java.io.*;
import java.util.Base64;

public abstract class SerializableDto implements Serializable {
    public void save(File file) throws IOException {
        byte[] bytes = this.serialize();
        FileOutputStream out = new FileOutputStream(file);

        out.write(bytes);
        out.flush();
        out.close();
    }

    public byte[] serialize() throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream out = null;

        try {
            out = new ObjectOutputStream(bos);
            out.writeObject(this);
            out.flush();
        } finally {
            try {
                bos.close();
            } catch (IOException ex) {
                //
            }
        }

        return bos.toByteArray();
    }

    public String toBase64() throws IOException {
        return Base64.getEncoder().encodeToString(this.serialize());
    }

    public static <T extends SerializableDto> T from(byte[] bytes) throws IOException, ClassNotFoundException {
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);

        try (ObjectInput in = new ObjectInputStream(bis)) {
            Object o = in.readObject();

            return (T) o;
        }
    }

    public static <T extends SerializableDto> T from(String input) throws IOException, ClassNotFoundException {
        byte[] bytes = Base64.getDecoder().decode(input);
        return from(bytes);
    }

    public static <T extends SerializableDto> T from(File file) throws IOException, ClassNotFoundException {
        FileInputStream in = new FileInputStream(file);
        long fileSize = file.length();
        byte[] totalBytes = new byte[(int) fileSize];

        in.read(totalBytes);
        in.close();

        return from(totalBytes);
    }
}
