package com.example.cu1;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface FotoDao {
    @Insert (onConflict = OnConflictStrategy.IGNORE)
    void insert(FotoBBDD fotoBBDD);

    @Delete
    void delete(FotoBBDD fotoBBDD);

    @Query("SELECT * FROM tabla_foto WHERE usuario LIKE :usuario")
    List<FotoBBDD> getFotosUsuario(String usuario);

}
