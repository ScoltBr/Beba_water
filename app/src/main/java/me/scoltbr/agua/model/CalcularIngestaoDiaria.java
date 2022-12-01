package me.scoltbr.agua.model;

public class CalcularIngestaoDiaria {

    private int ML_JOVEM = 40;
    private int ML_ADULTO = 35;
    private int ML_IDOSO = 30;
    private int ML_MAIS_DE_66_ANOS = 25;

    private double resultado_mel = 0;
    private double resultado_total_ml=0;

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

}
