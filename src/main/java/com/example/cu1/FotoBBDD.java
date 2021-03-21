package com.example.cu1;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

// Clase encargada de crear la tabla y columnas en la BBDD de Room sobre SQL
// Asi como de tener las funciones para acceder a cada dato
@Entity(tableName = "tabla_foto")
public class FotoBBDD {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "id")
    protected int did;

    @NonNull
    @ColumnInfo(name = "usuario")
    protected String dusuario;

    @NonNull
    @ColumnInfo(name = "foto")
    private String bytesFoto;


    @ColumnInfo(name = "latitud")
    protected double dLatitud;


    @ColumnInfo(name = "longitud")
    protected double dLongitud;


    @ColumnInfo(name = "x")
    protected float dX;


    @ColumnInfo(name = "y")
    protected float dY;


    @ColumnInfo(name = "z")
    protected float dZ;

    public FotoBBDD(int did, @NonNull String dusuario, @NonNull String bytesFoto, double dLatitud, double dLongitud, float dX, float dY, float dZ) {
        this.did = did;
        this.dusuario = dusuario;
        this.bytesFoto = bytesFoto;
        this.dLatitud = dLatitud;
        this.dLongitud = dLongitud;
        this.dX = dX;
        this.dY = dY;
        this.dZ = dZ;
    }

    public int getid() {
        return did;
    }

    @NonNull
    public String getUsuario() {
        return dusuario;
    }

    @NonNull
    public String getBytesFoto() {
        return bytesFoto;
    }

    public double getLatitud() {
        return this.dLatitud;
    }

    public double getLongitud() {
        return dLongitud;
    }

    public float getX() {
        return dX;
    }

    public float getY() {
        return dY;
    }

    public float getZ() {
        return dZ;
    }
}
