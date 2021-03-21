package com.example.cu1;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

//Consultas para la BBDD de Room
@Dao
public interface FotoDao {
    //Insertar una foto
    @Insert (onConflict = OnConflictStrategy.IGNORE)
    void insert(FotoBBDD fotoBBDD);

    //Borrar una foto
    @Delete
    void delete(FotoBBDD fotoBBDD);

    //Obtener todas las fotos realizadas por un usuario
    @Query("SELECT * FROM tabla_foto WHERE usuario LIKE :usuario")
    List<FotoBBDD> getFotosUsuario(String usuario);

}
