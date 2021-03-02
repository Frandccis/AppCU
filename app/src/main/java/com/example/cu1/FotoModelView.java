package com.example.cu1;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;

import java.util.List;

public class FotoModelView extends AndroidViewModel {

    private FotoRepository mRepository;

    public FotoModelView (Application application){
        super(application);
        mRepository = new FotoRepository(application);
    }

    public void insert (FotoBBDD foto) {
        mRepository.insert(foto);
    }

    public void delete (FotoBBDD foto){
        mRepository.delete(foto);
    }

    List<FotoBBDD> getFotosBBDD(String usuario) {
        return mRepository.getdAllFotos(usuario);
    }

}
