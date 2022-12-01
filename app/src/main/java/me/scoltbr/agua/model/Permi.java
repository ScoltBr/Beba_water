package me.scoltbr.agua.model;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

public class Permi {

    public static boolean validaPermi(int requestCode,Activity activity, String[] permi){
        if (Build.VERSION.SDK_INT >=23){
            List<String> listaPermi = new ArrayList<String>();
            for (String permissao : permi){
                boolean validaPermissao = ContextCompat.checkSelfPermission(activity, permissao) == PackageManager.PERMISSION_GRANTED;
                if (!validaPermissao) listaPermi.add(permissao);
            }

            String[] novaPermi = new String[listaPermi.size()];
            listaPermi.toArray(novaPermi);
            if (listaPermi.isEmpty()) return  true;

            ActivityCompat.requestPermissions(activity, novaPermi, requestCode);

        }
        return  true;
    }

}
