package tech.demur.dchcamera.database;

import androidx.annotation.NonNull;
import androidx.room.Entity;
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
}