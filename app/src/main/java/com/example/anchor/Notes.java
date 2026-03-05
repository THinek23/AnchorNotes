package com.example.anchor;
import java.util.List;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.Dao;

@Entity(tableName = "notes")
public class Notes {
    @PrimaryKey(autoGenerate = true)
    public long nid;
    @ColumnInfo(name="Title")
    public String title;

    @ColumnInfo(name="Content")
    public String content;

//    @ColumnInfo(name="Lat")
//    public double lat;
//
//    @ColumnInfo(name="Long")
//    public double lon;


    public Notes(String title, String content) {
        this.title = title;
        this.content = content;
    }
}
