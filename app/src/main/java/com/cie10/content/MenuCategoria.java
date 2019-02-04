package com.cie10.content;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jose_Segura on 15/12/16.
 */

public class MenuCategoria {

    public static List<contentItem> ITEMS = new ArrayList<>();

    static {
        addItem(new contentItem("I", "A00-B99","Enfermedades infecciosas y parasitarias"));
        addItem(new contentItem("II", "C00-D49","Neoplasias"));
        addItem(new contentItem("III", "D50-D89","Enfermedades de la sangre y de los Órganos hematopoyéticos."));
        addItem(new contentItem("IV", "E00-E90","Enfermedades endocrinas, nutricionales y metabólicas"));
        addItem(new contentItem("V", "F00-F99","Trastornos mentales y del comportamiento"));
        addItem(new contentItem("VI", "G00-G99","Enfermedades del sistema nervioso"));
        addItem(new contentItem("VII", "H00-H59","Enfermedades del ojo y sus anexos"));
        addItem(new contentItem("VIII", "H60-H99","Enfermedades del oÌdo y de la apófisis mastoides"));
        addItem(new contentItem("IX", "I00-I99","Enfermedades del sistema circulatorio"));
        addItem(new contentItem("X", "J00-J99","Enfermedades del sistema respiratorio"));
        addItem(new contentItem("XI", "K00-K99","Enfermedades del aparato digestivo"));
        addItem(new contentItem("XII", "L00-L99","Enfermedades de la piel y el tejido subcutáneo"));
        addItem(new contentItem("XIII", "M00-M99","Enfermedades del sistema osteomuscular y del tejido conectivo"));
        addItem(new contentItem("XIV", "N00-N99","Enfermedades del aparato genitourinario"));
        addItem(new contentItem("XV", "O00-O99","Embarazo, parto y puerperio"));
        addItem(new contentItem("XVI", "P00-P99","Ciertas afecciones originadas en el periodo perinatal"));
        addItem(new contentItem("XVII", "Q00-Q99","Malformaciones congénitas, deformidades y anomalías cromosómicas"));
        addItem(new contentItem("XVIII", "R00-R98","Síntomas, signos y hallazgos anormales clínicos y de laboratorio"));
        addItem(new contentItem("XIX", "S00-T98","Traumatismos, envenenamientos."));
        addItem(new contentItem("XX", "V01-Y98","Causas extremas de morbilidad y de mortalidad"));
        addItem(new contentItem("XXI", "Z00-Z99","Factores que influyen en el estado de salud."));

    }

    private static void addItem(contentItem item) {
        ITEMS.add(item);
    }

    public static class contentItem {
        public String capitulo;
        public String codigos;
        public String titulo;

        private contentItem(String capitulo, String codigos, String titulo) {
            this.capitulo = capitulo;
            this.codigos = codigos;
            this.titulo = titulo;
        }

        @Override
        public String toString() {
            return titulo;
        }
    }

}
