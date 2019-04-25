/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fak;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author Ghobed
 */
public class Fak {

    final String COMMA_DELIMITER = ",";

    //untuk mengambil data file kata-kata pengecualian 
    private List<String> generateListExclusion() {
        List<String> records = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader("exclusion.csv"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(COMMA_DELIMITER);
                records.addAll(Arrays.asList(values));
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Fak.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Fak.class.getName()).log(Level.SEVERE, null, ex);
        }

        return records;
    }
    
    //untuk mengambil data file kata-kata negatif
    private List<String> generateListNegative() {
        List<String> records = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader("ne_words.csv"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(COMMA_DELIMITER);
                records.addAll(Arrays.asList(values));
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Fak.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Fak.class.getName()).log(Level.SEVERE, null, ex);
        }

        return records;
    }
    
    //true or false pengecualian 
    private boolean isExcluded(String word, List<String> list){
        return list.stream().anyMatch((s) -> (word.equals(s)));
    }

    private String stem(String words, List<String> list) {
        String results = "";
        String[] listWord = words.split(" ");

        for (String s : listWord) {
            
            if(!isExcluded(s, list)){
                results += " "+s;
            }

        }

        return results;
    }

      
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        Fak tp = new Fak();
        List<String> listExclusion = tp.generateListExclusion();
        List<String> listNegative = tp.generateListNegative();

        File folder = new File(".");
        File[] listOfFiles = folder.listFiles();
        String filepath = "ghobed.csv";

        String kalimat = "";

        BufferedReader reader;

        for (File listOfFile : listOfFiles) {
            if (listOfFile.isFile()) {
                String fileName = listOfFile.getName();
                if (fileName.equals("a_negatif5.txt")) {
                    try {
                        File f = new File(fileName);
                        reader = new BufferedReader(new FileReader(f));

                        String line = reader.readLine();
                        while (line != null) {
                            String result = line.replaceAll("[^\\dA-Za-z ]", "").toLowerCase();
                            kalimat += " " + result;

                            line = reader.readLine();

                        }

                        reader.close();
                    } catch (FileNotFoundException ex) {
                        Logger.getLogger(Fak.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (IOException ex) {
                        Logger.getLogger(Fak.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
           
        //List<String> Positif =  
        kalimat = kalimat.trim();
        kalimat = tp.stem(kalimat, listExclusion);
        String output = null;
        for(int b = 0; b < listNegative.size(); b++){
            
            String simpan = listNegative.get(b);
          
            if (kalimat.contains(simpan)){
                output="-";
                System.out.println(output);
            
            } else {
                output="+";
            }
        }
        
      
        
        

        StringTokenizer st = new StringTokenizer(kalimat);

        List<String> listKata = new ArrayList<>();
        int[] frekuensi = new int[st.countTokens()];
        List<String> listKataSemua = new ArrayList<>();

        while (st.hasMoreTokens()) {
            String s = st.nextToken().toLowerCase();
            listKataSemua.add(s);
        }

        int isVisited = -1;

        for (int i = 0; i < listKataSemua.size(); i++) {

            String s = listKataSemua.get(i);
            int counter = 1;

            for (int j = i + 1; j < listKataSemua.size(); j++) {
                String s2 = listKataSemua.get(j);

                if (s.equals(s2)) {
                    counter++;
                    frekuensi[j] = isVisited;
                }
            }

            if (frekuensi[i] != isVisited) {
                frekuensi[i] = counter;
            }

        }

        try{
            FileWriter fw = new FileWriter(filepath,true);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter pw = new PrintWriter(bw);
            
            for (int i = 0; i < frekuensi.length; i++) {
                if (frekuensi[i] != isVisited) {
                      pw.print(listKataSemua.get(i) + ",");
                }
            }
            pw.println("class");
            for (int i = 0; i < frekuensi.length; i++) {
                if (frekuensi[i] != isVisited) {
                    pw.print(frekuensi[i] + ",");
                }
            }
            pw.println(output);
            pw.flush();
            pw.close();

            JOptionPane.showMessageDialog(null,"Record Saved");
        }
        catch(Exception E){
            JOptionPane.showMessageDialog(null, "Record Not Saved");
        }
       
    }

}
