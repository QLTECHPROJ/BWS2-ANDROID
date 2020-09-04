package com.qltech.bws.RoomDataBase;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.io.Serializable;
import java.util.Date;

@Entity(tableName = "item_table")
public class DownloadAudioDetails implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private int uid;
    @ColumnInfo(name = "ProductID")
    private String ProductID;
    @ColumnInfo(name = "ProductName")
    private String ProductName;

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getProductID() {
        return ProductID;
    }

    public void setProductID(String productID) {
        ProductID = productID;
    }

    public String getProductName() {
        return ProductName;
    }

    public void setProductName(String productName) {
        ProductName = productName;
    }
}
