package tech.demur.dchcamera.database;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "recordings")
public class Recording {
    @PrimaryKey(autoGenerate = true)
    public int id;
    @NonNull
    public String name;
    public int duration;
    @NonNull
    public Long created;

    public Recording(int id, @NonNull String name, int duration, @NonNull Long created) {
        this.id = id;
        this.name = name;
        this.duration = duration;
        this.created = created;
    }

    @Ignore
    public Recording(@NonNull String name, int duration, @NonNull Long created) {
        this.name = name;
        this.duration = duration;
        this.created = created;
    }
}