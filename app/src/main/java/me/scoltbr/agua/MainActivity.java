package me.scoltbr.agua;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.AlarmClock;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Locale;

import me.scoltbr.agua.model.Permi;

public class MainActivity extends AppCompatActivity {

    private int ML_JOVEM = 40;
    private int ML_ADULTO = 35;
    private int ML_IDOSO = 30;
    private int ML_MAIS_DE_66_ANOS = 25;

    private double resultado_mel = 0;
    private double resultado_total_ml=0;

    private EditText editpeso;
    private EditText editIdade;
    private Button bt_calcular;
    private TextView txt_resultado_ml;
    private ImageButton ic_redefinir_dados;
    private Button bt_lembrete;
    private Button bt_alarme;
    private TextView txt_hora;
    private TextView txt_minuto;

    Calendar calendar;
    TimePickerDialog timePickerDialog;

    int horaAtual = 0;
    int minutosAtuais = 0;

    private String[] permiNecessarias = new String[]{Manifest.permission.SET_ALARM};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        IniciarComponentes();
        Permi.validaPermi(1,this,permiNecessarias);

        bt_calcular.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editpeso.getText().toString().isEmpty()){
                    Toast.makeText(MainActivity.this,R.string.toast_informe_peso, Toast.LENGTH_SHORT).show();
                }else if (editIdade.getText().toString().isEmpty()){
                    Toast.makeText(MainActivity.this, R.string.toas_infome_idade, Toast.LENGTH_SHORT).show();
                }else{
                    double peso = Double.parseDouble(editpeso.getText().toString());
                    int idade = Integer.parseInt(editIdade.getText().toString());
                    NumberFormat formatar = NumberFormat.getNumberInstance(new Locale("pt","BR"));
                    formatar.setGroupingUsed(false);
                    txt_resultado_ml.setText(formatar.format(calcular_total_ml(peso,idade)) + " ml");
                }
            }
        });

        ic_redefinir_dados.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
                alertDialog.setTitle(R.string.dialogo_titulo);
                alertDialog.setMessage(R.string.dialogo_desc);
                alertDialog.setPositiveButton("Ok",(dialogInterface, i) ->{
                    editpeso.setText("");
                    editIdade.setText("");
                    txt_resultado_ml.setText("");
                });
                alertDialog.setNegativeButton("Cancelar", (dialogInterface, i) -> {

                });
                AlertDialog dialog = alertDialog.create();
                dialog.show();
            }
        });

        bt_lembrete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calendar = Calendar.getInstance();
                horaAtual = calendar.get(Calendar.HOUR_OF_DAY);
                minutosAtuais = calendar.get(Calendar.MINUTE);
                timePickerDialog = new TimePickerDialog(MainActivity.this, (timePicker,  hourOfDay, minutes) -> {
                    txt_hora.setText(String.format("%02d",hourOfDay));
                    txt_minuto.setText(String.format("%02d", minutes));
                },horaAtual,minutosAtuais, true);
                timePickerDialog.show();
            }
        });

        bt_alarme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!txt_hora.getText().toString().isEmpty() && !txt_minuto.getText().toString().isEmpty()) {
                    Intent intent = new Intent(AlarmClock.ACTION_SET_ALARM);
                    intent.putExtra(AlarmClock.EXTRA_HOUR, Integer.parseInt(txt_hora.getText().toString()));
                    intent.putExtra(AlarmClock.EXTRA_MINUTES, Integer.parseInt(txt_minuto.getText().toString()));
                    intent.putExtra(AlarmClock.EXTRA_MESSAGE, getString(R.string.alarme_mensagem));
                    startActivity(intent);
                    if (intent.resolveActivity(getPackageManager()) != null){
                        startActivity(intent);
                    }else {
                        Toast.makeText(MainActivity.this, "Não Suportado", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(MainActivity.this, "Coloque o horario", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    public double calcular_total_ml(double peso, int idade){
        if (idade <= 17){
            resultado_mel = peso * ML_JOVEM;
            resultado_total_ml = resultado_mel;
            return resultado_total_ml;
        }else if (idade <= 55) {
            resultado_mel = peso * ML_ADULTO;
            resultado_total_ml = resultado_mel;
            return resultado_total_ml;

        }else if (idade <= 65){
            resultado_mel = peso * ML_IDOSO;
            resultado_total_ml = resultado_mel;
            return resultado_total_ml;
        }else {
            resultado_mel = peso * ML_MAIS_DE_66_ANOS;
            resultado_total_ml = resultado_mel;
            return resultado_total_ml;
        }
    }

    private void IniciarComponentes(){
        editpeso = findViewById(R.id.edit_peso);
        editIdade = findViewById(R.id.edit_idade);
        bt_calcular = findViewById(R.id.bt_calcular);
        txt_resultado_ml = findViewById(R.id.txt_resultado_ml);
        ic_redefinir_dados = findViewById(R.id.ic_redefinir_dados);
        bt_alarme = findViewById(R.id.bt_definir_alarme);
        bt_lembrete = findViewById(R.id.bt_definir_lembrete);
        txt_hora = findViewById(R.id.txt_hora);
        txt_minuto = findViewById(R.id.text_minutos);
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResult){
        super.onRequestPermissionsResult(requestCode,permissions,grantResult);
        for ( int resultado : grantResult){
            if (resultado == PackageManager.PERMISSION_DENIED){
                alertaValidaPermissao();
            }
        }
    }

    private void alertaValidaPermissao(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("PERMISSÃO NEGADA");
        builder.setMessage("Para utilizar esse app, é necessário aceitar as permissões");
        builder.setPositiveButton("CONFIRMAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

}