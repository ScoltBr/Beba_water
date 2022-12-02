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
                if (editpeso.getText().toString().isEmpty()){ //Verificar se o valor do peso foi digitado
                    //Notificação na parte inferior da tela informando que o peso não foi digitado
                    Toast.makeText(MainActivity.this,R.string.toast_informe_peso, Toast.LENGTH_SHORT).show();
                }else if (editIdade.getText().toString().isEmpty()){ //Verificar se a idade foi digitada
                    //Notificação na parte inferior da tela informando que a idade não foi digitado
                    Toast.makeText(MainActivity.this, R.string.toas_infome_idade, Toast.LENGTH_SHORT).show();
                }else{
                    double peso = Double.parseDouble(editpeso.getText().toString()); //Receber e convertendo o Peso informado pelo usuario
                    int idade = Integer.parseInt(editIdade.getText().toString()); //Recebendo e convertendo a Idade informada pelo usuario
                    NumberFormat formatar = NumberFormat.getNumberInstance(new Locale("pt","BR")); //Formantando para o sistema numerico BR
                    formatar.setGroupingUsed(true); //Definir o formato que vai ser usado
                    txt_resultado_ml.setText(formatar.format(calcular_total_ml(peso,idade)) + " ml"); //Exibir na tela o valor de ML
                }
            }
        });

        ic_redefinir_dados.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Exibir na tela uma aba de dialogo para reiniciar
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
                //Coloca um titulo para a tela com base nas Strings declaradas
                alertDialog.setTitle(R.string.dialogo_titulo);
                //Exibe a mensagem declarada nas Strings declaras
                alertDialog.setMessage(R.string.dialogo_desc);
                //Inicia um Button para reiniciar as informações
                alertDialog.setPositiveButton("Ok",(dialogInterface, i) ->{
                    editpeso.setText(""); //Zera as informações do peso da pessoa
                    editIdade.setText(""); //Zera as informações da Idade da pessoa
                    txt_resultado_ml.setText(""); //Zera os ML informados na tela
                    txt_minuto.setText("00");
                    txt_hora.setText("00");
                });
                //Cria um botão para cancelar
                alertDialog.setNegativeButton("Cancelar", (dialogInterface, i) -> {

                });
                //Exibe na tela as informações
                AlertDialog dialog = alertDialog.create();
                dialog.show();
            }
        });

        bt_lembrete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Pela a instancia atual de Horario
                calendar = Calendar.getInstance();
                //Pega a hora atual do celular
                horaAtual = calendar.get(Calendar.HOUR_OF_DAY);
                //Pega os minutos atuais do celular
                minutosAtuais = calendar.get(Calendar.MINUTE);
                //Incia uma tela contendo os horarios
                timePickerDialog = new TimePickerDialog(MainActivity.this, (timePicker,  hourOfDay, minutes) -> {
                    txt_hora.setText(String.format("%02d",hourOfDay)); //Coloca na tela a hora para lembrar
                    txt_minuto.setText(String.format("%02d", minutes)); //Coloca na tela os minutos
                },horaAtual,minutosAtuais, true);
                timePickerDialog.show();
            }
        });

        bt_alarme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!txt_hora.getText().toString().isEmpty() && !txt_minuto.getText().toString().isEmpty()) {
                    Intent intent = new Intent(AlarmClock.ACTION_SET_ALARM); //Ação de usar Componente internos do Celular (Alarme)
                    intent.putExtra(AlarmClock.EXTRA_HOUR, Integer.parseInt(txt_hora.getText().toString())); //Inserir a hora direto no alarme
                    intent.putExtra(AlarmClock.EXTRA_MINUTES, Integer.parseInt(txt_minuto.getText().toString())); //Inserir o minuto direto no alarme
                    intent.putExtra(AlarmClock.EXTRA_MESSAGE, getString(R.string.alarme_mensagem)); //Mensagem definida do Alarme
                    startActivity(intent); //Abre a tela do Alarme do Celular
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