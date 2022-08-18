package identity;

import lombok.NonNull;

import java.io.Serializable;
import java.util.UUID;

public interface UniquelyIdentifiable<T extends Serializable> {

    static final String UID_FIELD_ID = "uid";

    static long generate(@NonNull String value) {
        return Math.abs(UUID.nameUUIDFromBytes(value.getBytes()).getMostSignificantBits());
    }

    T getUid();

    void setUid(T uid);
}
