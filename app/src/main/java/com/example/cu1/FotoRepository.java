package com.example.cu1;

import android.app.Application;

import java.util.List;

public class FotoRepository {
    private FotoDao dFotoDao;


    FotoRepository(Application application){
        FotoBBDDRoom db = FotoBBDDRoom.getDatabase(application);
        dFotoDao = db.fotoDao();
    }

    List<FotoBBDD> getdAllFotos(String usuario){
        return dFotoDao.getFotosUsuario(usuario);
    }

    void insert(FotoBBDD fotoBBDD){
        FotoBBDDRoom.databaseWriteExecutor.execute(() -> {
            dFotoDao.insert(fotoBBDD);
        });
    }

    void delete(FotoBBDD fotoBBDD){
        FotoBBDDRoom.databaseWriteExecutor.execute(() -> {
            dFotoDao.delete(fotoBBDD);
        });
    }

}
