package com.company;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws SQLException {
        Connection conn = DriverManager.getConnection("jdbc:mysql://192.168.1.53:3306/personale", "root", "root"); //connessione al db
        //rs.getMetaData(); avere informazioni sulla tabella --> soprattutto se la tabella è sconosciuta

        Statement stmt = conn.createStatement();

        Scanner t=new Scanner((System.in));
        System.out.println("Inserire nome tabella");
        String nome=t.nextLine();

        try{
            stmt.execute("create table "+nome+" (codice int primary key, denominazione varchar(20), prezzo double, scadenza datetime);");// read
        }
        catch(Exception e){
            System.out.println("Tabella già presente");
        }

        int n=0;
        while(n!=8){
            System.out.println("COMANDI: ");
            System.out.println("1 --> inserisci nuova tupla");
            System.out.println("2 --> visualizzare tutti prodotti");
            System.out.println("3 --> visualizzare prodotti con scadenza in: ");
            System.out.println("4 --> visualizzare prodotti con scadenza in questo mese");
            System.out.println("5 --> cancellare prodotti scaduti");
            System.out.println("6 --> visualizzare prodotto più costoso");
            System.out.println("7 --> cambiare prezzo prodotto");
            System.out.println("8 --> FINE");

            n=t.nextInt();
            switch (n){
                case 1:
                    t.nextLine();
                    System.out.println("Inserisci nuovo codice");
                    String cod=t.nextLine();
                    System.out.println("Inserisci denominazione");
                    String den=t.nextLine();
                    System.out.println("Inserisci prezzo");
                    double prezzo=Double.parseDouble(t.nextLine());
                    System.out.println("Inserisci scadenza yyyy-MM-dd");
                    String scadenza=t.nextLine();

                    System.out.println(cod);
                    System.out.println(den);
                    System.out.println(prezzo);
                    System.out.println(scadenza);
                    System.out.println("Aggiunto "+insertProdotti(nome,conn,cod,den,prezzo,scadenza)+" articolo");
                    break;

                case 2:
                    visualTab(conn,nome);
                    break;

                case 3:
                    t.nextLine();
                    System.out.println("Inserisci una data yyyy-MM-dd");
                    String data=t.nextLine();

                    selezionaData(conn,nome,data);
                    break;

                case 4:
                    Calendar c4 = Calendar.getInstance();
                    c4.set(Calendar.DAY_OF_MONTH, c4.getActualMaximum(Calendar.DAY_OF_MONTH));
                    SimpleDateFormat sdf4 = new SimpleDateFormat("yyyy-MM-dd");
                    String data4=sdf4.format(c4.getTime());

                    selezionaData(conn,nome,data4);
                    break;

                case 5:
                    Calendar c5 = Calendar.getInstance();
                    c5.set(Calendar.DAY_OF_MONTH, c5.getActualMaximum(Calendar.DAY_OF_MONTH));
                    SimpleDateFormat sdf5 = new SimpleDateFormat("yyyy-MM-dd");
                    String data5=sdf5.format(c5.getTime());

                    Date dataDelete=java.sql.Date.valueOf(data5);

                    Statement stmt5 = conn.createStatement();
                    stmt5.execute("DELETE from "+nome+" where scadenza<="+dataDelete);// read
                    stmt5.close();
                    break;

                case 6:
                    double prezzoMax=0.0,temp=0.0;
                    Statement stmt6 = conn.createStatement();
                    ResultSet rs6 = stmt6.executeQuery("SELECT * from "+nome);// read
                    while (rs6.next()) { //riga record tupla
                        temp=Double.parseDouble(rs6.getString(3));
                        if(temp>prezzoMax){
                            prezzoMax=temp;
                        }
                    }
                    stmt6.close();

                    Statement stmt6_2 = conn.createStatement();
                    ResultSet rs6_2=stmt6_2.executeQuery("SELECT * from "+nome+" where prezzo="+prezzoMax);
                    System.out.println("Articolo più costoso:");
                    while(rs6_2.next()){
                        System.out.println(rs6_2.getString(1)+" "+rs6_2.getString(2)+" "+rs6_2.getString(3)+" "+rs6_2.getString(4));
                    }
                    stmt6_2.close();
                    break;

                case 7:
                    t.nextLine();
                    System.out.println("Inserisci nome prodotto");
                    String nomeProdotto=t.nextLine();

                    double prezzo7=0;
                    Statement stmt7 = conn.createStatement();
                    ResultSet rs7 = stmt7.executeQuery("SELECT * from "+nome);// read
                    while (rs7.next()) { //riga record tupla
                        if(rs7.getString(2).equals(nomeProdotto)){
                            System.out.println("Inserisci nuovo prezzo");
                            prezzo7=Double.parseDouble(t.nextLine());
                        }
                    }
                    stmt7.close();

                    Statement stmt7_2 = conn.createStatement();
                    if(prezzo7!=0) {
                        stmt7_2.executeUpdate("UPDATE " + nome + " SET prezzo=" + prezzo7+" WHERE denominazione='"+nomeProdotto+"'");
                        System.out.println("Prezzo cambiato con successo");
                    }
                    else{
                        System.out.println("Prodotto non disponibile");
                    }
                    stmt7_2.close();
                    break;
            }
        }
    }

    public static void selezionaData(Connection conn, String nome, String dataScadenza) throws SQLException {
        Date data=java.sql.Date.valueOf(dataScadenza);
        Statement stmtData = conn.createStatement();
        ResultSet rsData = stmtData.executeQuery("SELECT * from "+nome+" where scadenza<="+data);// read
        while (rsData.next()) { //riga record tupla
            System.out.println(rsData.getString(1)+" "+rsData.getString(2)+" "+rsData.getString(3)+" "+rsData.getString(4));
        }
        stmtData.close();
    }

    public static int insertProdotti(String nome, Connection c, String cod, String descr, double prezzo, String scadenza) throws SQLException {
        int r;
        PreparedStatement ps=c.prepareStatement("INSERT INTO "+ nome +" values (?,?,?,?)");

        ps.setString(1, cod);
        ps.setString(2, descr);
        ps.setDouble(3, prezzo);
        ps.setDate(4, java.sql.Date.valueOf(scadenza));
        r = ps.executeUpdate();
        return r;
    }

    public static void visualTab(Connection conn, String nome) throws SQLException {
        Statement stmt=conn.createStatement();

        ResultSet rs=stmt.executeQuery("SELECT * from "+nome);

        ResultSetMetaData md=rs.getMetaData();
        int nc = md.getColumnCount();
        for (int i = 1; i <= nc; i++) {
            System.out.print(md.getColumnName(i) + " ");
        }
        System.out.println();
        while (rs.next()) {
            for (int i = 1; i <= nc; i++) {
                System.out.print(rs.getString(i) + " ");
            }
            System.out.println();
        }
        stmt.close();
    }

}
