/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package interface_avec_tab;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Samir
 */
public class Accueil extends javax.swing.JFrame {

    /**
     * Creates new form
     */
    CardLayout card;

    public Accueil() {
        initComponents();
        Dimension d = new Dimension(1140, 670);
        setSize(d);
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(dim.width / 2 - this.getSize().width / 2, dim.height / 2 - this.getSize().height / 2);
        card = (CardLayout) (Tabs.getLayout());
        location_button.setForeground(Color.GRAY);
        reservation_button.setForeground(Color.GRAY);
        client_button.setForeground(Color.WHITE);
        facture_button.setForeground(Color.GRAY);
        evenement_button.setForeground(Color.GRAY);
        rembourcer.setForeground(Color.GRAY);
        Select(liste_clients, "SELECT * FROM client");

    }

    public Connection connect() {
        //Effectuer une connection avec la BD
        Connection conn = null;
        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection("jdbc:sqlite:Hotel_la_Gazelle.db");

        } catch (ClassNotFoundException | SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), " error", JOptionPane.ERROR_MESSAGE);
        }
        return conn;
    }

    /**
     *
     */
    public void ajouter_client() {
        Connection conn = null;
        PreparedStatement ps;

        try {
            //connect to database
            conn = connect();
            //change date format
            SimpleDateFormat Date_Format = new SimpleDateFormat("yyyy-MM-dd");

            if ("".equals(Nom_client.getText()) || "".equals(prenom_client.getText())
                    || "".equals(Num_id.getText()) || "".equals(profession_client.getText()) || "".equals(Nationalite.getText())) {
                JOptionPane.showMessageDialog(this, "Information non valid", " error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (date_naissance_client.getDate() == null) {
                JOptionPane.showMessageDialog(this, "Date de naissance non valid", " error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            //query creation
            ps = conn.prepareStatement("INSERT INTO client values (null,'" + Nom_client.getText() + "','" + prenom_client.getText()
                    + "','" + profession_client.getText() + "','" + type_id.getSelectedItem() + "','" + Num_id.getText()
                    + "','" + Date_Format.format(date_naissance_client.getDate()) + "','" + Nationalite.getText() + "')");
            //execute query
            int rs = ps.executeUpdate();

            //client added message
            JOptionPane.showMessageDialog(this, "Client Ajouter");

            Select(liste_clients, "SELECT * FROM client");
            //Reset
            Nom_client.setText("");
            prenom_client.setText("");
            profession_client.setText("");
            date_naissance_client.setDate(null);
            Num_id.setText("");
            Nationalite.setText("");

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), " error", JOptionPane.ERROR_MESSAGE);

        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), " error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void supprimerClient() {
        int column = 0;
        int row = liste_clients.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Client non selectionné", " error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String value = liste_clients.getModel().getValueAt(row, column).toString();

        String query = "delete from client where Num_client='" + value + "'";
        Connection conn = connect();
        PreparedStatement statement;
        try {
            statement = conn.prepareStatement(query);
            int i = statement.executeUpdate();

            JOptionPane.showMessageDialog(this, "client supprimé");
            Select(liste_clients, "SELECT * FROM client");

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), " error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void Select(JTable jTable1, String sql) {
        Connection conn;
        conn = connect();
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {

            // get columns info
            ResultSetMetaData rsmd = rs.getMetaData();
            int columnCount = rsmd.getColumnCount();

            // for changing column and row model
            DefaultTableModel tm = (DefaultTableModel) jTable1.getModel();

            // clear existing columns
            tm.setColumnCount(0);

            // add specified columns to table
            for (int i = 1; i <= columnCount; i++) {
                tm.addColumn(rsmd.getColumnName(i));
            }

            // clear existing rows
            tm.setRowCount(0);

            // add rows to table
            while (rs.next()) {
                String[] a = new String[columnCount];
                for (int i = 0; i < columnCount; i++) {
                    a[i] = rs.getString(i + 1);
                }
                tm.addRow(a);
            }
            tm.fireTableDataChanged();
            conn.close();
            stmt.close();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex, ex.getMessage(), WIDTH, null);
        }
    }

    public void get_rooms() {
        Connection conn = null;
        PreparedStatement ps;

        int column = 0;
        int row = liste_clients_reseervation.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Client non selectionné", " error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String value = liste_clients_reseervation.getModel().getValueAt(row, column).toString();
        //changer le format de la date
        SimpleDateFormat Date_Format = new SimpleDateFormat("yyyy-MM-dd");
        if (date_arrive.getDate() == null) {
            JOptionPane.showMessageDialog(this, "Date de arrivé non valid", " error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (date_depart.getDate() == null) {
            JOptionPane.showMessageDialog(this, "Date de arrivé non valid", " error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        //chambre disponible a une date donnée
        String sql = "select Num_chambre,prix_chambre from chambre where"
                + " Num_chambre NOT IN (select Num_chambre from reservation where"
                + "  Date_debut between '" + Date_Format.format(date_arrive.getDate())
                + "'and '" + Date_Format.format(date_depart.getDate()) + "' "
                + "or Date_fin between '" + Date_Format.format(date_arrive.getDate())
                + "'and '" + Date_Format.format(date_depart.getDate()) + "')";
        try {
            conn = connect();

            ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            //activer button
            ajouter_reservation.setEnabled(true);

            if (rs == null) {
                JOptionPane.showMessageDialog(this, "pas de chambre disponible");
                return;
            }
            //list chambre disponible
            Select(list_chambre, sql);
            ajouter_reservation.setEnabled(true);

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), " error", JOptionPane.ERROR_MESSAGE);
        } finally {
            try {
                if (conn != null) {

                    conn.close();
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), " error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void ajouterReservation() {
        Connection conn = null;
        PreparedStatement ps;

        int column = 0;
        int row = liste_clients_reseervation.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Client non selectionné", " error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String value = liste_clients_reseervation.getModel().getValueAt(row, column).toString();

        row = list_chambre.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Chambre non selectionné", " error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String value1 = list_chambre.getModel().getValueAt(row, column).toString();

        SimpleDateFormat Date_Format = new SimpleDateFormat("yyyy-MM-dd");

        String etat_payement = "";
        if (jRadioButton1.isSelected()) {
            etat_payement = "payé";
        } else {
            etat_payement = "non payé";
        }
        String sql = "INSERT INTO reservation values (null,'"
                + Date_Format.format(date_arrive.getDate()) + "','" + Date_Format.format(date_depart.getDate()) + "',"
                + "'non_confirmé','" + modep.getSelectedItem() + "','" + value
                + "','" + value1 + "','" + etat_payement + "')";

        try {
            conn = connect();

            ps = conn.prepareStatement(sql);
            //execute query
            int rs = ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Reservation Ajouter");
            ps.close();
            //chambre disponible Aujoud'hui
            sql = "select Num_chambre,prix_chambre from chambre where"
                    + " Num_chambre NOT IN (select Num_chambre from reservation where"
                    + "  Date_debut between Date('now')"
                    + " and Date('now') "
                    + "or Date_fin between Date('now')"
                    + " and  Date('now'))";
            Select(list_chambre, sql);
            date_arrive.setCalendar(null);
            date_depart.setCalendar(null);
            Num_client.setText("");
            Select(table_reservation, "SELECT * FROM reservation");
            ajouter_reservation.setEnabled(false);

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), " error", JOptionPane.ERROR_MESSAGE);
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), " error", JOptionPane.ERROR_MESSAGE);

            }
        }
    }

    public void confirmerReservation() {
        int column = 0;
        int row = table_reservation.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Reservation non selectionné", " error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String value = table_reservation.getModel().getValueAt(row, column).toString();

        String query = "Update reservation set etat_reservation='confirmé' where Num_reservation=" + value + ""
                + " and etat_reservation ='non_confirmé'";

        try {
            Connection conn = connect();

            PreparedStatement statement = conn.prepareStatement(query);
            int i = statement.executeUpdate();
            if (i > 0) {
                JOptionPane.showMessageDialog(this, "Reservation confirmé");
            } else {
                JOptionPane.showMessageDialog(this, "Reservation ne peut pas être confirmé");
            }
            Select(table_reservation, "SELECT * FROM reservation");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), " error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void annulerReservation() {
        int column = 0;
        int row = table_reservation.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Reservation non selectionné", " error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String value = table_reservation.getModel().getValueAt(row, column).toString();

        String query = "Update reservation set etat_reservation='annulé' where Num_reservation='" + value + "'"
                + " and etat_reservation !='annulé' and etat_reservation !='terminé'";

        try {
            Connection conn = connect();

            PreparedStatement statement = conn.prepareStatement(query);
            int i = statement.executeUpdate();
            if (i > 0) {
                JOptionPane.showMessageDialog(this, "Reservation annulé");
            } else {
                JOptionPane.showMessageDialog(this, "Reservation ne peut pas être annulé");
            }

            Select(table_reservation, "SELECT * FROM reservation");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), " error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void get_salles() {
        Connection conn = null;
        PreparedStatement ps;

        SimpleDateFormat Date_Format = new SimpleDateFormat("yyyy-MM-dd");

        if (date_debut_location.getDate() == null) {
            JOptionPane.showMessageDialog(this, "Date de arrivé non valid", " error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (date_fin_location.getDate() == null) {
            JOptionPane.showMessageDialog(this, "Date de depart non valid", " error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        //requete salle disponible
        String sql = "select Num_salle,prix_salle from salle where"
                + " Num_salle NOT IN (select Num_salle from louer where"
                + " Date_debut_location between '" + Date_Format.format(date_debut_location.getDate()) + "' and '"
                + Date_Format.format(date_fin_location.getDate()) + "' "
                + "or Date_fin_location between '" + Date_Format.format(date_debut_location.getDate())
                + "'and '" + Date_Format.format(date_fin_location.getDate())
                + "' and (etat_location!='annulé'))";

        try {
            conn = connect();
            ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            if (rs == null) {
                JOptionPane.showMessageDialog(this, "pas de salle disponible");
            } else {

                Select(liste_salle_disponible, sql);
                ajouter_location.setEnabled(true);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), " error", JOptionPane.ERROR_MESSAGE);
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), " error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void addLocation() {
        Connection conn = null;
        PreparedStatement ps;

        int column = 0;
        int row = liste_client_location.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Client non selectionné", " error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String value = liste_client_location.getModel().getValueAt(row, column).toString();

        row = liste_salle_disponible.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Chambre non selectionné", " error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String value1 = liste_salle_disponible.getModel().getValueAt(row, column).toString();

        try {
            conn = connect();
            SimpleDateFormat Date_Format = new SimpleDateFormat("yyyy-MM-dd");

            Date_Format.format(date_debut_location.getDate());
            ps = conn.prepareStatement("INSERT INTO louer values ("
                    + value
                    + "," + value1 + ",'"
                    + Date_Format.format(date_debut_location.getDate()) + "','"
                    + Date_Format.format(date_fin_location.getDate()) + "','non_confirmé')");

            //execute query
            int rs = ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Location Ajouter");
            date_debut_location.setCalendar(null);
            date_fin_location.setCalendar(null);
            num_client_location.setText("");
            Select(liste_locations, "select * from louer");
            //liste salle disponible Ajourd'hui
            String sql = "select Num_salle,prix_salle from salle where"
                    + " Num_salle NOT IN (select Num_salle from louer where"
                    + " Date_debut_location between Date('now') and Date('now') "
                    + "or Date_fin_location between Date('now')"
                    + " and Date('now') and (etat_location!='annulé'))";
            Select(liste_salle_disponible, sql);
            ps.close();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), " error", JOptionPane.ERROR_MESSAGE);
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), " error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void annulerLocation() {
        int column = 0;
        int row = liste_locations.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Location non selectionné", " error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String value = liste_locations.getModel().getValueAt(row, column).toString();
        String value2 = liste_locations.getModel().getValueAt(row, 1).toString();

        String query = "Update louer set etat_location='annulé' where Num_client='" + value + "'"
                + "and Num_salle='" + value2 + "' and etat_location !='annulé' and etat_location !='terminé'";

        try {
            Connection conn = connect();

            PreparedStatement statement = conn.prepareStatement(query);
            int i = statement.executeUpdate();
            if (i > 0) {
                JOptionPane.showMessageDialog(this, "Location annulé");
            } else {
                JOptionPane.showMessageDialog(this, "Location ne peut pas être annulé");
            }

            Select(liste_locations, "SELECT * FROM louer");
            //liste salle disponible Ajourd'hui
            String sql = "select Num_salle,prix_salle from salle where"
                    + " Num_salle NOT IN (select Num_salle from louer where"
                    + " Date_debut_location between Date('now') and Date('now') "
                    + "or Date_fin_location between Date('now')"
                    + " and Date('now') and (etat_location!='annulé'))";
            Select(liste_salle_disponible, sql);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), " error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void get_location_evenement() {

        Connection conn = null;
        PreparedStatement ps;
        ResultSet rs2;

        int column = 0;
        int row = lise_client_evenement.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Client non selectionné", " error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String value = lise_client_evenement.getModel().getValueAt(row, column).toString();

        //requete location d'un client
        String sql = "select Num_salle, Num_client, Date_debut_location, Date_fin_location from louer"
                + " where Num_client='" + value + "'";

        try {
            conn = connect();

            ps = conn.prepareStatement(sql);
            rs2 = ps.executeQuery();

            if (!rs2.next()) {
                Select(liste_salle_louer, sql);
                JOptionPane.showMessageDialog(this, "Client n'a aucune location", " error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            Select(liste_salle_louer, sql);
            ps.close();
            //activer button 
            ajouter_evenement.setEnabled(true);

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), " error", JOptionPane.ERROR_MESSAGE);
        } finally {
            try {
                if (conn != null) {

                    conn.close();
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), " error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void ajouterEvenement() {
        Connection conn = null;
        PreparedStatement ps;

        int column = 0;
        int row = lise_client_evenement.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Client non selectionné", " error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String value = lise_client_evenement.getModel().getValueAt(row, column).toString();

        row = liste_salle_louer.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Salle non selectionné", " error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String value1 = liste_salle_louer.getModel().getValueAt(row, column).toString();

        try {
            conn = connect();
            SimpleDateFormat Date_Format = new SimpleDateFormat("yyyy-MM-dd");
            if ("".equals(nom_evenement.getText())) {
                JOptionPane.showMessageDialog(this, "Nom d'evenement non valid", " error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            Date_Format.format(date_debut_evenement.getDate());
            ps = conn.prepareStatement("INSERT INTO evenement values (null,'" + nom_evenement.getText() + "','"
                    + Date_Format.format(date_debut_evenement.getDate()) + "','"
                    + Date_Format.format(date_fin_evenement.getDate()) + "',"
                    + value1 + ")");
            //execute query
            int r = ps.executeUpdate();
            if (r > 0) {
                JOptionPane.showMessageDialog(this, "Evenement Ajouter");
            }
            date_debut_evenement.setCalendar(null);
            date_fin_evenement.setCalendar(null);
            nom_evenement.setText("");
            num_client_evenement.setText("");
            Select(list_evnement, "select * from evenement");
            //Select(liste_locations_evenement, "select * from louer");
            DefaultTableModel tm = (DefaultTableModel) liste_salle_louer.getModel();
            //tm.setColumnCount(0);
            tm.setRowCount(0);

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), " error", JOptionPane.ERROR_MESSAGE);
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), " error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void get_reservation_facturation() {
        jLabel29.setText("Liste des reservations");
        Connection conn;
        facture_area.setText("");
        SimpleDateFormat Date_Format = new SimpleDateFormat("yyyy-MM-dd");

        conn = connect();
        int column = 0;
        int row = client_facturation.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Reservation non selectionné", " error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String value = client_facturation.getModel().getValueAt(row, column).toString();
        //verfier date arrive c null
        if (date_depart_facturation.getDate() == null) {
            JOptionPane.showMessageDialog(this, "Date de depart non valid", " error", JOptionPane.ERROR_MESSAGE);
            buttonGroup1.clearSelection();
            return;
        }
        //requete reservations
        String sql = "Select * from reservation where Num_client= '" + value
                + "' and Date_fin >= '" + Date_Format.format(date_depart_facturation.getDate()) + "'"
                + " and Date_debut <= '" + Date_Format.format(date_depart_facturation.getDate()) + "'"
                + " and etat_reservation = 'confirmé' or etat_reservation = 'terminé'";

        Select(liste_reservation_location, sql);
        int rc = liste_reservation_location.getRowCount();
        if (rc == 0) {
            JOptionPane.showMessageDialog(this, "Client n'a aucune reservation", " error", JOptionPane.ERROR_MESSAGE);
        }

        //activer button
        facturer.setEnabled(true);
        try {
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), " error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void get_location_facturation() {
        jLabel29.setText("Liste des locations");
        facture_area.setText("");
        SimpleDateFormat Date_Format = new SimpleDateFormat("yyyy-MM-dd");

        int column = 0;
        int row = client_facturation.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Location non selectionné", " error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String value = client_facturation.getModel().getValueAt(row, column).toString();

        //verfier date arrive c null
        if (date_depart_facturation.getDate() == null) {
            JOptionPane.showMessageDialog(this, "Date de depart non valid", " error", JOptionPane.ERROR_MESSAGE);
            buttonGroup1.clearSelection();
            return;
        }
        String sql = "Select * from louer where Num_client= '" + value
                + "' and Date_fin_location >= '" + Date_Format.format(date_depart_facturation.getDate()) + "'"
                + " and Date_debut_location <= '" + Date_Format.format(date_depart_facturation.getDate()) + "'"
                + " and etat_location = 'confirmé'";
        liste_reservation_location.setModel(new DefaultTableModel());
        Select(liste_reservation_location, sql);
        int rc = liste_reservation_location.getRowCount();
        if (rc == 0) {
            JOptionPane.showMessageDialog(this, "Client n'a aucune location", " error", JOptionPane.ERROR_MESSAGE);
        }
        //activer button
        facturer.setEnabled(true);
    }

    public void facture_reservation() throws IOException, ParseException {
        int column = 0;
        int row = liste_reservation_location.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Reservation non selectionné", " error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String value = liste_reservation_location.getModel().getValueAt(row, column).toString();

        String num_reservation = value;
        Connection conn = null;
        PreparedStatement ps;
        SimpleDateFormat Date_Format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            conn = connect();

            ps = conn.prepareStatement("select Num_facture, Montant_total, date_facture from facture_reservation where"
                    + " Num_reservation=" + num_reservation);
            ResultSet rs = ps.executeQuery();
            int Montant = rs.getInt("Montant_total");
            int Num_facture = rs.getInt("Num_facture");
            String date = rs.getString("date_facture");
            String f = "\n---------------------------------------------------------------------------------\n"
                    + "                                 Facture Hotel la Gazelle                             \n"
                    + "\n                                   Reservation Chambre                 \n"
                    + "----------------------------------------------------------------------------------";
            //nombre de jours

            ps = conn.prepareStatement("SELECT julianday(Date_fin) - julianday(Date_debut) as days FROM reservation where"
                    + " Num_reservation = " + num_reservation + "");
            rs = ps.executeQuery();
            int d = rs.getInt("days") + 1;
            int prix_chambre = Montant / d;

            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

            String d3 = Date_Format.format(date_depart_facturation.getDate());

            Date d1 = format.parse(d3);
            Date d2 = format.parse(date);

            long diff = d2.getTime() - d1.getTime();

            long diffDays = diff / (24 * 60 * 60 * 1000);

            d = d - (int) diffDays;
            Montant = d * prix_chambre;
            //nom prenom num id client
            ps = conn.prepareStatement("select Nom, Prenom, Num_id from client where"
                    + " Num_client=(select Num_client from reservation where Num_reservation=" + num_reservation + ")");
            rs = ps.executeQuery();
            String Nom = rs.getString("Nom");
            String Prenom = rs.getString("Prenom");
            String num_id_var = rs.getString("Num_id");

            f = f + "\n     Nom: " + rs.getString("Nom") + "\n     Prenom:" + rs.getString("Prenom")
                    + "\n     Numero piéce d'identite:" + rs.getString("Num_id");

            // montant appel
            ps = conn.prepareStatement("select Sum(prix_appel) as montant_appel from appel "
                    + " where Num_reservation= " + num_reservation);
            rs = ps.executeQuery();
            int appel = rs.getInt("montant_appel");
            //montant consommation
            ps = conn.prepareStatement("select Sum(prix_consommation) as montant_consomation from consommation "
                    + " where Num_reservation= " + num_reservation);
            rs = ps.executeQuery();
            int consommation = rs.getInt("montant_consomation");
            //Num_chambre
            ps = conn.prepareStatement("select Num_chambre from reservation where"
                    + " Num_reservation= " + num_reservation);
            rs = ps.executeQuery();
            String Num_chambre = rs.getString("Num_chambre");

            f = f + "\n     Numero de Chambre: " + Num_chambre + "\n     Nombre de jours: " + d;
            f = f + "\n---------------------------------------------------------------------------------"
                    + "\n        Montant appels: " + appel
                    + "\n        Montant consommations: " + consommation
                    + "\n        Moantant_totale= " + Montant + "\n";

            ps = conn.prepareStatement("delete from reservation where Num_reservation = " + num_reservation);
            //execute query
            int r = ps.executeUpdate();
            if (r > 0) {
                //Creating PDF document object 
                try {

                    String text = f;
                    Document document = new Document(PageSize.A5);
                    PdfWriter.getInstance(document, new FileOutputStream("Facture/Facture_"
                            + Num_facture + "_Reservation_Chambre_" + Num_chambre + "_R_"
                            + num_reservation
                            + "_" + Nom + "_" + Prenom + "_" + num_id_var + "_" + Date_Format.format(date_depart_facturation.getDate())
                            + ".pdf"));
                    document.open();
                    document.add(new Paragraph(text));

                    Font font = new Font(Font.FontFamily.TIMES_ROMAN, 10.0f, Font.BOLD, BaseColor.LIGHT_GRAY);
                    Chunk c = new Chunk("\nFacture_"
                            + Num_facture + "_Reservation_Chambre_" + Num_chambre + "_R_"
                            + num_reservation
                            + "_" + Nom + "_" + Prenom + "_" + num_id_var + "_" + Date_Format.format(date_depart_facturation.getDate()),
                            font);

                    Paragraph p1 = new Paragraph(c);
                    p1.setAlignment(Paragraph.ALIGN_CENTER);
                    facture_area.setText(f);
                    document.add(p1);
                    JOptionPane.showMessageDialog(this, "Facture établit");

                    nom.setText("");

                    date_depart_facturation.setDate(new Date());

                    document.close();
                } catch (DocumentException | FileNotFoundException e) {
                }

            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), " error", JOptionPane.ERROR_MESSAGE);
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), " error", JOptionPane.ERROR_MESSAGE);
            }
        }

    }

    public void facture_location() {
        int column = 0;
        int row = liste_reservation_location.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Location non selectionné", " error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String value = liste_reservation_location.getModel().getValueAt(row, column).toString();

        int column1 = 1;

        String value1 = liste_reservation_location.getModel().getValueAt(row, column1).toString();

        String num_client_var = value;
        String num_salle_var = value1;
        Connection conn = null;
        PreparedStatement ps;
        //change date format
        SimpleDateFormat Date_Format = new SimpleDateFormat("yyyy-MM-dd");

        try {
            conn = connect();
            //Montant totale
            ps = conn.prepareStatement("select Num_facture, Montant_total from facture_location where"
                    + " Num_client="
                    + num_client_var + " and Num_salle=" + num_salle_var + ""
                    + " and date_facture <= '" + Date_Format.format(date_depart_facturation.getDate()) + "'");
            ResultSet rs = ps.executeQuery();
            int Montant = rs.getInt("Montant_total");
            int Num_facture = rs.getInt("Num_facture");
            String f = "\n---------------------------------------------------------------------------------\n"
                    + "                                     Facture Hotel la Gazelle                          \n"
                    + "\n                                       Location Salle                    \n"
                    + "----------------------------------------------------------------------------------";
            //nombre de jours
            ps = conn.prepareStatement("SELECT julianday(Date_fin_location) - julianday(Date_debut_location) as days FROM louer where"
                    + " Num_salle = '" + num_salle_var
                    + "' and Num_client='"
                    + num_client_var
                    + "' and Date_fin_location >='"
                    + Date_Format.format(date_depart_facturation.getDate()) + "'");
            rs = ps.executeQuery();
            int d = rs.getInt("days") + 1;
            //nom prenom num id client
            ps = conn.prepareStatement("select Nom, Prenom, Num_id from client where"
                    + " Num_client=" + num_client_var);
            rs = ps.executeQuery();
            String Nom = rs.getString("Nom");
            String Prenom = rs.getString("Prenom");
            String Num_id_var = rs.getString("Num_id");
            f = f + "\n     Nom: " + Nom + "\n     Prenom:" + Prenom
                    + "\n     Numero piéce d'identite:" + Num_id_var;
            // montant prestation
            ps = conn.prepareStatement("select Sum(prix_prestation) as montant_prestation from prestation "
                    + " where Num_evenement= (select Num_evenement from evenement where"
                    + " Num_salle=" + num_salle_var + ")");
            rs = ps.executeQuery();
            int prestation = rs.getInt("montant_prestation");

            String Num_salle = num_salle_var;
            f = f + "\n     Numero de salle: " + Num_salle + "\n     Nombre de jours: " + d;
            f = f + "\n---------------------------------------------------------------------------------"
                    + "\n        Montant prestations: " + prestation
                    + "\n        Moantant_totale= " + Montant;

            ps = conn.prepareStatement("delete from louer where Num_salle = " + num_salle_var
                    + " and Num_client = " + num_client_var);
            //execute query
            int r = ps.executeUpdate();
            if (r > 0) {
                try {

                    String text = f;
                    Document document = new Document(PageSize.A5);
                    PdfWriter.getInstance(document, new FileOutputStream("Facture/Facture_0" + Num_facture + "_location_ salle_"
                            + num_client_var
                            + "_" + Nom + "_" + Prenom + "_" + Num_id_var + "_" + Date_Format.format(date_depart_facturation.getDate())
                            + ".pdf"));
                    document.open();
                    document.add(new Paragraph(text));

                    Font font = new Font(Font.FontFamily.TIMES_ROMAN, 10.0f, Font.BOLD, BaseColor.LIGHT_GRAY);
                    Chunk c = new Chunk("Facture_0" + Num_facture + "_location_ salle_"
                            + num_client_var
                            + "_" + Nom + "_" + Prenom + "_" + Num_id_var + "_" + Date_Format.format(date_depart_facturation.getDate()),
                            font);

                    Paragraph p1 = new Paragraph(c);
                    p1.setAlignment(Paragraph.ALIGN_CENTER);
                    document.add(p1);
                    facture_area.setText(f);
                    JOptionPane.showMessageDialog(this, "Facture établit");

                    nom.setText("");

                    date_depart_facturation.setDate(new Date());

                    document.close();
                } catch (DocumentException | FileNotFoundException e) {
                    JOptionPane.showMessageDialog(this, e.getMessage(), " error", JOptionPane.ERROR_MESSAGE);

                }
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), " error", JOptionPane.ERROR_MESSAGE);
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), " error", JOptionPane.ERROR_MESSAGE);
            }
        }

    }

    public void Reset_reservation_tab() {

        date_arrive.setCalendar(null);
        date_depart.setCalendar(null);
        Num_client.setText("");
        ajouter_reservation.setEnabled(false);
        //liste client
        Select(liste_clients_reseervation, "SELECT Num_client, Nom, Prenom, Num_id FROM client");
        //liste reservation
        Select(table_reservation, "SELECT * FROM reservation");

        location_button.setForeground(Color.GRAY);
        reservation_button.setForeground(Color.WHITE);
        client_button.setForeground(Color.GRAY);
        facture_button.setForeground(Color.GRAY);
        evenement_button.setForeground(Color.GRAY);
        rembourcer.setForeground(Color.GRAY);

        //chambre disponible Aujoud'hui
        String sql = "select Num_chambre,prix_chambre from chambre where"
                + " Num_chambre NOT IN (select Num_chambre from reservation where"
                + "  Date_debut between Date('now')"
                + " and Date('now') "
                + "or Date_fin between Date('now')"
                + " and  Date('now'))";
        Select(list_chambre, sql);
    }

    public void Reset_location_tab() {
        date_debut_location.setCalendar(null);
        date_fin_location.setCalendar(null);
        num_client_location.setText("");
        //change location button foreground to white
        location_button.setForeground(Color.WHITE);
        reservation_button.setForeground(Color.GRAY);
        client_button.setForeground(Color.GRAY);
        facture_button.setForeground(Color.GRAY);
        evenement_button.setForeground(Color.GRAY);
        rembourcer.setForeground(Color.GRAY);

        //liste location
        Select(liste_locations, "select * from louer");
        //liste clients
        Select(liste_client_location, "select Num_client,Nom,Prenom, Num_id from client");
        //liste salle disponible Ajourd'hui
        String sql = "select Num_salle,prix_salle from salle where"
                + " Num_salle NOT IN (select Num_salle from louer where"
                + " Date_debut_location between Date('now') and Date('now') "
                + "or Date_fin_location between Date('now')"
                + " and Date('now') and (etat_location!='annulé'))";
        Select(liste_salle_disponible, sql);
    }

    public void Reset_client_tab() {
        //reset
        Nom_client.setText("");
        prenom_client.setText("");
        profession_client.setText("");
        date_naissance_client.setDate(null);
        Num_id.setText("");
        Nationalite.setText("");
        //client button foreground white
        location_button.setForeground(Color.GRAY);
        reservation_button.setForeground(Color.GRAY);
        client_button.setForeground(Color.WHITE);
        facture_button.setForeground(Color.GRAY);
        evenement_button.setForeground(Color.GRAY);
        rembourcer.setForeground(Color.GRAY);

        //liste clients
        Select(liste_clients, "SELECT * FROM client");
    }

    public void Reset_facture_tab() {
        nom.requestFocus();
        //resert
        java.util.Date mydate = new java.util.Date();
        date_depart_facturation.setDate(mydate);

        nom.setText("");
        //vider la table

        //empty text area
        facture_area.setText("");
        //facture button foreground white
        location_button.setForeground(Color.GRAY);
        reservation_button.setForeground(Color.GRAY);
        client_button.setForeground(Color.GRAY);
        facture_button.setForeground(Color.WHITE);
        evenement_button.setForeground(Color.GRAY);
        rembourcer.setForeground(Color.GRAY);
        DefaultTableModel tm = (DefaultTableModel) liste_reservation_location.getModel();
        tm.setColumnCount(0);
        tm.setRowCount(0);

        //liste clients
        Select(client_facturation, "SELECT Num_client, Nom, Prenom, Num_id FROM client");
    }

    public void Reset_evenement_tab() {
        //reset
        nom_evenement.setText("");
        num_client_evenement.setText("");
        DefaultTableModel tm = (DefaultTableModel) liste_salle_louer.getModel();
        tm.setRowCount(0);

        //evenement button foreground white
        location_button.setForeground(Color.GRAY);
        reservation_button.setForeground(Color.GRAY);
        client_button.setForeground(Color.GRAY);
        facture_button.setForeground(Color.GRAY);
        evenement_button.setForeground(Color.WHITE);
        rembourcer.setForeground(Color.GRAY);
        //liste evenements
        Select(list_evnement, "select * from evenement");
        //liste locations
        //Select(liste_locations_evenement, "select * from louer");
        //Liste_clients
        Select(lise_client_evenement, "SELECT Num_client, Nom, Prenom, Num_id FROM client");
        num_client_evenement.requestFocus();

    }

    public void Reset_rembourcement_tab() {
        //evenement button foreground white
        location_button.setForeground(Color.GRAY);
        reservation_button.setForeground(Color.GRAY);
        client_button.setForeground(Color.GRAY);
        facture_button.setForeground(Color.GRAY);
        evenement_button.setForeground(Color.GRAY);
        rembourcer.setForeground(Color.WHITE);

        String sql = "Select r.Num_reservation, Nom, Prenom, Num_id, Montant_total from client c, reservation r, facture_reservation f"
                + " where c.Num_client=r.Num_client and r.Num_reservation = f.Num_reservation and etat_payement = 'payé'";
        Select(rembourcement, sql);
        card.show(Tabs, "card7");
    }

    public void get_reservation_client() {
        int column = 0;
        int row = liste_clients_reseervation.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Client non selectionné", " error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String value = liste_clients_reseervation.getModel().getValueAt(row, column).toString();
        String sql = "Select * from reservation where Num_client=" + value;
        Select(table_reservation, sql);

    }

    public void get_location_client() {
        int column = 0;
        int row = liste_client_location.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Client non selectionné", " error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String value = liste_client_location.getModel().getValueAt(row, column).toString();
        String sql = "select * from louer where Num_client = " + value;
        Select(liste_locations, sql);

    }

    public void get_location_client_evenement() {
        int column = 0;
        int row = liste_client_location.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Client non selectionné", " error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String value = liste_client_location.getModel().getValueAt(row, column).toString();
        String sql = "select * from louer where Num_client = " + value;
        Select(liste_salle_louer, sql);

    }

    public void supprimerClients(JTable entryTable) {
        if (entryTable.getRowCount() > 0) {
            if (entryTable.getSelectedRowCount() > 0) {
                int selectedRow[] = entryTable.getSelectedRows();
                for (int i : selectedRow) {
                    String value = entryTable.getModel().getValueAt(i, 0).toString();
                    String query = "delete from client where Num_client='" + value + "'";
                    Connection conn = connect();
                    PreparedStatement statement;
                    try {
                        statement = conn.prepareStatement(query);
                        statement.executeUpdate();

                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(this, ex.getMessage(), " error", JOptionPane.ERROR_MESSAGE);
                    }
                }
                JOptionPane.showMessageDialog(this, "client supprimé");
                Select(entryTable, "SELECT * FROM client");
            }
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        buttonGroup2 = new javax.swing.ButtonGroup();
        buttonGroup3 = new javax.swing.ButtonGroup();
        buttonGroup4 = new javax.swing.ButtonGroup();
        buttonGroup5 = new javax.swing.ButtonGroup();
        buttonGroup6 = new javax.swing.ButtonGroup();
        buttonGroup7 = new javax.swing.ButtonGroup();
        buttonGroup8 = new javax.swing.ButtonGroup();
        jSplitPane1 = new javax.swing.JSplitPane();
        Tabs = new javax.swing.JPanel();
        client = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        liste_clients = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        date_naissance_client = new com.toedter.calendar.JDateChooser();
        jLabel5 = new javax.swing.JLabel();
        prenom_client = new javax.swing.JTextField();
        type_id = new javax.swing.JComboBox<>();
        ajouter_client = new javax.swing.JButton();
        Nationalite = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        profession_client = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        Num_id = new javax.swing.JTextField();
        Nom_client = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        supprimer_client = new javax.swing.JButton();
        jLabel42 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        deconnexion = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        ordre_nom = new javax.swing.JRadioButton();
        ordre_defaut = new javax.swing.JRadioButton();
        jButton4 = new javax.swing.JButton();
        reservation = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        list_chambre = new javax.swing.JTable();
        jLabel14 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        liste_clients_reseervation = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        date_depart = new com.toedter.calendar.JDateChooser();
        jLabel10 = new javax.swing.JLabel();
        date_arrive = new com.toedter.calendar.JDateChooser();
        jLabel13 = new javax.swing.JLabel();
        ajouter_reservation = new javax.swing.JButton();
        modep = new javax.swing.JComboBox<>();
        chambre_disponible = new javax.swing.JButton();
        Num_client = new javax.swing.JTextField();
        jLabel25 = new javax.swing.JLabel();
        jRadioButton1 = new javax.swing.JRadioButton();
        jRadioButton2 = new javax.swing.JRadioButton();
        nom_rb = new javax.swing.JRadioButton();
        Numero_id = new javax.swing.JRadioButton();
        jPanel12 = new javax.swing.JPanel();
        confirmer_reservation = new javax.swing.JButton();
        jLabel15 = new javax.swing.JLabel();
        annuler_reservation = new javax.swing.JButton();
        jScrollPane4 = new javax.swing.JScrollPane();
        table_reservation = new javax.swing.JTable();
        location = new javax.swing.JPanel();
        jScrollPane7 = new javax.swing.JScrollPane();
        liste_locations = new javax.swing.JTable();
        jPanel9 = new javax.swing.JPanel();
        jScrollPane5 = new javax.swing.JScrollPane();
        liste_client_location = new javax.swing.JTable();
        jLabel22 = new javax.swing.JLabel();
        jScrollPane6 = new javax.swing.JScrollPane();
        liste_salle_disponible = new javax.swing.JTable();
        jLabel23 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        annuler_location = new javax.swing.JButton();
        jPanel8 = new javax.swing.JPanel();
        jLabel17 = new javax.swing.JLabel();
        salle_disponible = new javax.swing.JButton();
        date_fin_location = new com.toedter.calendar.JDateChooser();
        jLabel18 = new javax.swing.JLabel();
        date_debut_location = new com.toedter.calendar.JDateChooser();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        ajouter_location = new javax.swing.JButton();
        num_client_location = new javax.swing.JTextField();
        nom_rb_location = new javax.swing.JRadioButton();
        jRadioButton4 = new javax.swing.JRadioButton();
        evenement = new javax.swing.JPanel();
        jPanel10 = new javax.swing.JPanel();
        jLabel26 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        date_fin_evenement = new com.toedter.calendar.JDateChooser();
        jLabel28 = new javax.swing.JLabel();
        date_debut_evenement = new com.toedter.calendar.JDateChooser();
        jLabel30 = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        ajouter_evenement = new javax.swing.JButton();
        nom_evenement = new javax.swing.JTextField();
        num_client_evenement = new javax.swing.JTextField();
        jRadioButton3 = new javax.swing.JRadioButton();
        jRadioButton5 = new javax.swing.JRadioButton();
        jScrollPane8 = new javax.swing.JScrollPane();
        lise_client_evenement = new javax.swing.JTable();
        jScrollPane9 = new javax.swing.JScrollPane();
        liste_salle_louer = new javax.swing.JTable();
        jScrollPane10 = new javax.swing.JScrollPane();
        list_evnement = new javax.swing.JTable();
        jLabel32 = new javax.swing.JLabel();
        jLabel33 = new javax.swing.JLabel();
        jLabel34 = new javax.swing.JLabel();
        facture = new javax.swing.JPanel();
        jPanel11 = new javax.swing.JPanel();
        jLabel36 = new javax.swing.JLabel();
        nom = new javax.swing.JTextField();
        jLabel40 = new javax.swing.JLabel();
        date_depart_facturation = new com.toedter.calendar.JDateChooser();
        jLabel41 = new javax.swing.JLabel();
        reservation_rb = new javax.swing.JRadioButton();
        location_rb = new javax.swing.JRadioButton();
        jLabel35 = new javax.swing.JLabel();
        nom_rb_f = new javax.swing.JRadioButton();
        jRadioButton7 = new javax.swing.JRadioButton();
        facturer = new javax.swing.JButton();
        jScrollPane11 = new javax.swing.JScrollPane();
        liste_reservation_location = new javax.swing.JTable();
        jScrollPane13 = new javax.swing.JScrollPane();
        facture_area = new javax.swing.JTextArea();
        jScrollPane12 = new javax.swing.JScrollPane();
        client_facturation = new javax.swing.JTable();
        jLabel11 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jLabel43 = new javax.swing.JLabel();
        jScrollPane14 = new javax.swing.JScrollPane();
        rembourcement = new javax.swing.JTable();
        jButton1 = new javax.swing.JButton();
        jTextField1 = new javax.swing.JTextField();
        jRadioButton6 = new javax.swing.JRadioButton();
        jRadioButton8 = new javax.swing.JRadioButton();
        jLabel37 = new javax.swing.JLabel();
        TopButtons = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        reservation_button = new javax.swing.JButton();
        location_button = new javax.swing.JButton();
        client_button = new javax.swing.JButton();
        facture_button = new javax.swing.JButton();
        evenement_button = new javax.swing.JButton();
        rembourcer = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Accueil");

        jSplitPane1.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

        Tabs.setBackground(new java.awt.Color(102, 102, 102));
        Tabs.setLayout(new java.awt.CardLayout());

        client.setBackground(new java.awt.Color(255, 153, 102));

        liste_clients.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null}
            },
            new String [] {
                "Numero", "Nom", "Prenom", "null", "null", "null", "null", "null"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(liste_clients);

        jPanel1.setOpaque(false);

        jLabel2.setText("Nom");

        jLabel1.setText("Prenom");

        jLabel5.setText("Numero piéce d'identité");

        type_id.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Carte d identité", "Passport", "Permis de conduite", " " }));

        ajouter_client.setText("Ajouter client");
        ajouter_client.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ajouter_clientActionPerformed(evt);
            }
        });

        jLabel7.setText("Nationalité");

        jLabel4.setText("Type piéce d'idnetité");

        jLabel3.setText("Profession");

        jLabel6.setText("Date de Naissance");

        supprimer_client.setText("Supprimer client");
        supprimer_client.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                supprimer_clientActionPerformed(evt);
            }
        });

        jLabel42.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel42.setForeground(new java.awt.Color(51, 51, 51));
        jLabel42.setText("Client");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addComponent(jLabel4)
                            .addComponent(jLabel1)
                            .addComponent(jLabel3)
                            .addComponent(jLabel5)
                            .addComponent(jLabel6)
                            .addComponent(jLabel7))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(supprimer_client, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(ajouter_client, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(Nationalite)
                            .addComponent(profession_client)
                            .addComponent(prenom_client)
                            .addComponent(Nom_client)
                            .addComponent(date_naissance_client, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 146, Short.MAX_VALUE)
                            .addComponent(Num_id)
                            .addComponent(type_id, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(33, 33, 33))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel42)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel42)
                .addGap(23, 23, 23)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(Nom_client, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(prenom_client, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(profession_client, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(type_id, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(Num_id, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(date_naissance_client, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel7)
                    .addComponent(Nationalite, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ajouter_client)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(supprimer_client)
                .addContainerGap(75, Short.MAX_VALUE))
        );

        jLabel16.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel16.setForeground(new java.awt.Color(51, 51, 51));
        jLabel16.setText("Liste des clients");

        deconnexion.setText("Déconnexion");
        deconnexion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deconnexionActionPerformed(evt);
            }
        });

        jButton2.setText("Client non actif");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton3.setText("Client en Cours");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        buttonGroup3.add(ordre_nom);
        ordre_nom.setText("Ordre par Nom");

        buttonGroup3.add(ordre_defaut);
        ordre_defaut.setSelected(true);
        ordre_defaut.setText("Ordre par defaut");

        jButton4.setText("Tous les Clients");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout clientLayout = new javax.swing.GroupLayout(client);
        client.setLayout(clientLayout);
        clientLayout.setHorizontalGroup(
            clientLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, clientLayout.createSequentialGroup()
                .addGroup(clientLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, clientLayout.createSequentialGroup()
                        .addGap(376, 376, 376)
                        .addComponent(ordre_defaut)
                        .addGap(18, 18, 18)
                        .addComponent(ordre_nom)
                        .addGap(18, 18, 18)
                        .addComponent(jButton4)
                        .addGap(27, 27, 27)
                        .addComponent(jButton3)
                        .addGap(18, 18, 18)
                        .addComponent(jButton2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(deconnexion))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, clientLayout.createSequentialGroup()
                        .addGap(45, 45, 45)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(clientLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel16)
                            .addComponent(jScrollPane1))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        clientLayout.setVerticalGroup(
            clientLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(clientLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(clientLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(clientLayout.createSequentialGroup()
                        .addComponent(jLabel16)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 375, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(38, 38, 38)
                .addGroup(clientLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ordre_defaut)
                    .addComponent(ordre_nom)
                    .addComponent(jButton4)
                    .addComponent(jButton3)
                    .addComponent(jButton2)
                    .addComponent(deconnexion))
                .addContainerGap(329, Short.MAX_VALUE))
        );

        Tabs.add(client, "client");
        client.getAccessibleContext().setAccessibleParent(client);

        reservation.setPreferredSize(new java.awt.Dimension(500, 395));

        jPanel6.setBackground(new java.awt.Color(255, 153, 102));

        jPanel5.setOpaque(false);

        jLabel12.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel12.setText("Chambres disponibles");

        list_chambre.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null}
            },
            new String [] {
                "Num_chambre", "prix"
            }
        ));
        jScrollPane2.setViewportView(list_chambre);

        jLabel14.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel14.setText("Liste des Clients");

        liste_clients_reseervation.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null}
            },
            new String [] {
                "Numero", "Nom", "Title 3", "Title 4", "Title 5", "Title 6", "Title 7", "Prenom"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, true, true, true, true, true, true
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        liste_clients_reseervation.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                liste_clients_reseervationMouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(liste_clients_reseervation);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 549, Short.MAX_VALUE)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel12)
                            .addComponent(jLabel14))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jScrollPane3))
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel12)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(31, Short.MAX_VALUE))
        );

        jPanel2.setOpaque(false);

        jLabel9.setText("Date depart");

        jLabel8.setText("Mode payement");

        jLabel10.setText("Date arrivé");

        jLabel13.setText("Client");

        ajouter_reservation.setText("Ajouer reservation");
        ajouter_reservation.setEnabled(false);
        ajouter_reservation.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ajouter_reservationActionPerformed(evt);
            }
        });

        modep.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Cash ", "Chèque ", "Cate bancaire", " " }));

        chambre_disponible.setText("chambre disponible");
        chambre_disponible.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chambre_disponibleActionPerformed(evt);
            }
        });

        Num_client.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                Num_clientCaretUpdate(evt);
            }
        });
        Num_client.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Num_clientActionPerformed(evt);
            }
        });

        jLabel25.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel25.setText("Reservation");

        buttonGroup2.add(jRadioButton1);
        jRadioButton1.setText("Payé");

        buttonGroup2.add(jRadioButton2);
        jRadioButton2.setSelected(true);
        jRadioButton2.setText("Non payé");

        buttonGroup4.add(nom_rb);
        nom_rb.setText("Nom");
        nom_rb.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nom_rbActionPerformed(evt);
            }
        });

        buttonGroup4.add(Numero_id);
        Numero_id.setSelected(true);
        Numero_id.setText("Numero id");
        Numero_id.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Numero_idActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(ajouter_reservation))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jLabel8, javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(jLabel9))
                                    .addComponent(jLabel10)
                                    .addComponent(jLabel13))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addComponent(jRadioButton1)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(jRadioButton2))
                                    .addComponent(date_arrive, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(date_depart, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(modep, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(chambre_disponible, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addComponent(Num_client, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(nom_rb)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(Numero_id, javax.swing.GroupLayout.DEFAULT_SIZE, 82, Short.MAX_VALUE))))
                            .addComponent(jLabel25))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jLabel25)
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel13)
                            .addComponent(Num_client, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(nom_rb)
                            .addComponent(Numero_id))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(date_arrive, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel10))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(date_depart, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(modep, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8))
                .addGap(18, 18, 18)
                .addComponent(chambre_disponible)
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jRadioButton1)
                    .addComponent(jRadioButton2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 33, Short.MAX_VALUE)
                .addComponent(ajouter_reservation)
                .addContainerGap())
        );

        jPanel12.setOpaque(false);

        confirmer_reservation.setText("Confirmer reservation");
        confirmer_reservation.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                confirmer_reservationActionPerformed(evt);
            }
        });

        jLabel15.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel15.setText("Liste des reservations");

        annuler_reservation.setText("Annuler reservation");
        annuler_reservation.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                annuler_reservationActionPerformed(evt);
            }
        });

        table_reservation.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4", "Title 5", "Title 6", "Title 7", "null"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane4.setViewportView(table_reservation);

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addGap(32, 32, 32)
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel12Layout.createSequentialGroup()
                        .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 892, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(confirmer_reservation, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(annuler_reservation, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(43, 43, 43))
                    .addGroup(jPanel12Layout.createSequentialGroup()
                        .addComponent(jLabel15)
                        .addGap(0, 0, Short.MAX_VALUE))))
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel15)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel12Layout.createSequentialGroup()
                        .addComponent(confirmer_reservation)
                        .addGap(18, 18, 18)
                        .addComponent(annuler_reservation))
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, 1090, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                        .addGap(34, 34, 34)
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(26, 26, 26)
                .addComponent(jPanel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(100, 100, 100))
        );

        javax.swing.GroupLayout reservationLayout = new javax.swing.GroupLayout(reservation);
        reservation.setLayout(reservationLayout);
        reservationLayout.setHorizontalGroup(
            reservationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel6, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        reservationLayout.setVerticalGroup(
            reservationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, reservationLayout.createSequentialGroup()
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0))
        );

        Tabs.add(reservation, "reservation");
        reservation.getAccessibleContext().setAccessibleParent(reservation);

        location.setBackground(new java.awt.Color(255, 153, 102));
        location.setName("location"); // NOI18N

        liste_locations.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4", "null"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane7.setViewportView(liste_locations);

        jPanel9.setOpaque(false);

        liste_client_location.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Num_client", "Nom", "Prenom"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        liste_client_location.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                liste_client_locationMouseClicked(evt);
            }
        });
        jScrollPane5.setViewportView(liste_client_location);

        jLabel22.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel22.setText("Liste des clients");

        liste_salle_disponible.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Num_salle", "prix_salle"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane6.setViewportView(liste_salle_disponible);

        jLabel23.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel23.setText("Salles disponible");

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel23)
                    .addComponent(jLabel22)
                    .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 548, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane6))
                .addGap(0, 53, Short.MAX_VALUE))
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addGap(4, 4, 4)
                .addComponent(jLabel22)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(8, 8, 8)
                .addComponent(jLabel23)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jLabel24.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel24.setText("Liste locations");

        annuler_location.setText("Annuler location");
        annuler_location.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                annuler_locationActionPerformed(evt);
            }
        });

        jPanel8.setOpaque(false);
        jPanel8.setPreferredSize(new java.awt.Dimension(391, 303));

        jLabel17.setText("Date depart");

        salle_disponible.setText("Salle disponible");
        salle_disponible.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                salle_disponibleActionPerformed(evt);
            }
        });

        jLabel18.setText("Date arrivé");

        jLabel20.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel20.setText("Location");

        jLabel21.setText("Client");

        ajouter_location.setText("Ajouer location");
        ajouter_location.setEnabled(false);
        ajouter_location.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ajouter_locationActionPerformed(evt);
            }
        });

        num_client_location.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                num_client_locationCaretUpdate(evt);
            }
        });

        buttonGroup5.add(nom_rb_location);
        nom_rb_location.setText("Nom");
        nom_rb_location.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nom_rb_locationActionPerformed(evt);
            }
        });

        buttonGroup5.add(jRadioButton4);
        jRadioButton4.setSelected(true);
        jRadioButton4.setText("Numero id");
        jRadioButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButton4ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addGap(21, 21, 21)
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel8Layout.createSequentialGroup()
                                .addComponent(jLabel20)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(jPanel8Layout.createSequentialGroup()
                                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel17)
                                    .addComponent(jLabel18)
                                    .addComponent(jLabel21))
                                .addGap(14, 14, 14)
                                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(date_debut_location, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(date_fin_location, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addGroup(jPanel8Layout.createSequentialGroup()
                                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(salle_disponible)
                                            .addGroup(jPanel8Layout.createSequentialGroup()
                                                .addComponent(num_client_location, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(nom_rb_location)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addComponent(jRadioButton4)))
                                        .addGap(0, 19, Short.MAX_VALUE))))))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel8Layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(ajouter_location)))
                .addContainerGap())
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addComponent(jLabel20)
                .addGap(10, 10, 10)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel21)
                            .addComponent(num_client_location, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(nom_rb_location)
                            .addComponent(jRadioButton4))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(date_debut_location, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel18))
                .addGap(18, 18, 18)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(date_fin_location, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel17, javax.swing.GroupLayout.Alignment.TRAILING))
                .addGap(18, 18, 18)
                .addComponent(salle_disponible)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 66, Short.MAX_VALUE)
                .addComponent(ajouter_location)
                .addGap(71, 71, 71))
        );

        javax.swing.GroupLayout locationLayout = new javax.swing.GroupLayout(location);
        location.setLayout(locationLayout);
        locationLayout.setHorizontalGroup(
            locationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(locationLayout.createSequentialGroup()
                .addGap(38, 38, 38)
                .addGroup(locationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel24)
                    .addGroup(locationLayout.createSequentialGroup()
                        .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 892, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(annuler_location, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(87, Short.MAX_VALUE))
            .addGroup(locationLayout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, 385, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(34, 34, 34))
        );
        locationLayout.setVerticalGroup(
            locationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(locationLayout.createSequentialGroup()
                .addGroup(locationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(locationLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, 323, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addComponent(jLabel24)
                .addGap(38, 38, 38)
                .addGroup(locationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(annuler_location))
                .addContainerGap(285, Short.MAX_VALUE))
        );

        Tabs.add(location, "location");
        location.getAccessibleContext().setAccessibleParent(location);

        evenement.setBackground(new java.awt.Color(255, 153, 102));

        jPanel10.setOpaque(false);

        jLabel26.setText("Date fin");

        jLabel27.setText("Nom evenement");

        jLabel28.setText("Date debut");

        jLabel30.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel30.setText("Evenement");

        jLabel31.setText("Client");

        ajouter_evenement.setText("Ajouer evenement");
        ajouter_evenement.setEnabled(false);
        ajouter_evenement.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ajouter_evenementActionPerformed(evt);
            }
        });

        num_client_evenement.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                num_client_evenementCaretUpdate(evt);
            }
        });

        buttonGroup6.add(jRadioButton3);
        jRadioButton3.setText("Nom");
        jRadioButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButton3ActionPerformed(evt);
            }
        });

        buttonGroup6.add(jRadioButton5);
        jRadioButton5.setSelected(true);
        jRadioButton5.setText("Numero id");
        jRadioButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButton5ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel27)
                            .addComponent(jLabel28)
                            .addComponent(jLabel26)
                            .addComponent(jLabel31))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(date_debut_evenement, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(date_fin_evenement, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(nom_evenement)
                            .addComponent(jLabel30)
                            .addGroup(jPanel10Layout.createSequentialGroup()
                                .addComponent(num_client_evenement, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jRadioButton3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jRadioButton5))))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel10Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(ajouter_evenement)))
                .addContainerGap())
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel30)
                .addGap(7, 7, 7)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel31)
                    .addComponent(num_client_evenement, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jRadioButton3)
                    .addComponent(jRadioButton5))
                .addGap(38, 38, 38)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(date_debut_evenement, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel28))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(date_fin_evenement, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel26, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(nom_evenement, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel27))
                .addGap(29, 29, 29)
                .addComponent(ajouter_evenement)
                .addContainerGap(59, Short.MAX_VALUE))
        );

        lise_client_evenement.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        lise_client_evenement.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lise_client_evenementMouseClicked(evt);
            }
        });
        jScrollPane8.setViewportView(lise_client_evenement);

        liste_salle_louer.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Num_salle", "Num_client", "Date_debut_location", "Date_fin_location"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane9.setViewportView(liste_salle_louer);

        list_evnement.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4", "Title 5"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane10.setViewportView(list_evnement);

        jLabel32.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel32.setText("Liste Clients");

        jLabel33.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel33.setText("Liste des Salles louer");

        jLabel34.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel34.setText("Liste des evènements");

        javax.swing.GroupLayout evenementLayout = new javax.swing.GroupLayout(evenement);
        evenement.setLayout(evenementLayout);
        evenementLayout.setHorizontalGroup(
            evenementLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(evenementLayout.createSequentialGroup()
                .addGroup(evenementLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(evenementLayout.createSequentialGroup()
                        .addGap(24, 24, 24)
                        .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(135, 135, 135)
                        .addGroup(evenementLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel33)
                            .addComponent(jLabel32)
                            .addComponent(jScrollPane8, javax.swing.GroupLayout.DEFAULT_SIZE, 483, Short.MAX_VALUE)
                            .addComponent(jScrollPane9)))
                    .addGroup(evenementLayout.createSequentialGroup()
                        .addGap(49, 49, 49)
                        .addGroup(evenementLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel34)
                            .addComponent(jScrollPane10, javax.swing.GroupLayout.PREFERRED_SIZE, 958, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(143, Short.MAX_VALUE))
        );
        evenementLayout.setVerticalGroup(
            evenementLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(evenementLayout.createSequentialGroup()
                .addGap(13, 13, 13)
                .addGroup(evenementLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(evenementLayout.createSequentialGroup()
                        .addComponent(jLabel32)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane8, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel33)
                        .addGap(4, 4, 4)
                        .addComponent(jScrollPane9, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(evenementLayout.createSequentialGroup()
                        .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(63, 63, 63)
                        .addComponent(jLabel34)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane10, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(309, Short.MAX_VALUE))
        );

        Tabs.add(evenement, "evenement");

        facture.setBackground(new java.awt.Color(255, 153, 102));

        jPanel11.setOpaque(false);

        jLabel36.setText("Date ");

        nom.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                nomCaretUpdate(evt);
            }
        });

        jLabel40.setText("Client");

        jLabel41.setText("Type facture");

        buttonGroup1.add(reservation_rb);
        reservation_rb.setText("Reservation");
        reservation_rb.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                reservation_rbActionPerformed(evt);
            }
        });

        buttonGroup1.add(location_rb);
        location_rb.setText("Location");
        location_rb.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                location_rbActionPerformed(evt);
            }
        });

        jLabel35.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel35.setText("Facturation");

        buttonGroup7.add(nom_rb_f);
        nom_rb_f.setText("Nom");
        nom_rb_f.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nom_rb_fActionPerformed(evt);
            }
        });

        buttonGroup7.add(jRadioButton7);
        jRadioButton7.setSelected(true);
        jRadioButton7.setText("Numero id");
        jRadioButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButton7ActionPerformed(evt);
            }
        });

        facturer.setText("Facturer");
        facturer.setEnabled(false);
        facturer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                facturerActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addGap(24, 24, 24)
                        .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel11Layout.createSequentialGroup()
                                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel41)
                                    .addComponent(jLabel36)
                                    .addComponent(jLabel40))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(location_rb)
                                    .addComponent(reservation_rb)
                                    .addGroup(jPanel11Layout.createSequentialGroup()
                                        .addComponent(nom, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(nom_rb_f)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jRadioButton7))
                                    .addComponent(date_depart_facturation, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                            .addComponent(jLabel35))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel11Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(facturer)))
                .addContainerGap())
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addComponent(jLabel35)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(nom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel40)
                            .addComponent(nom_rb_f)
                            .addComponent(jRadioButton7))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(date_depart_facturation, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel36))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(reservation_rb)
                            .addComponent(jLabel41))
                        .addGap(31, 31, 31))
                    .addComponent(location_rb))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 21, Short.MAX_VALUE)
                .addComponent(facturer)
                .addGap(36, 36, 36))
        );

        liste_reservation_location.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "", "", "", "", "", "", ""
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane11.setViewportView(liste_reservation_location);

        facture_area.setColumns(20);
        facture_area.setRows(5);
        jScrollPane13.setViewportView(facture_area);

        client_facturation.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4", "null", "null", "null"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        client_facturation.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                client_facturationMouseClicked(evt);
            }
        });
        jScrollPane12.setViewportView(client_facturation);

        jLabel11.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel11.setText("Liste des clients");

        jLabel19.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel19.setText("Facture");

        jLabel29.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel29.setText("Liste des reservations");

        javax.swing.GroupLayout factureLayout = new javax.swing.GroupLayout(facture);
        facture.setLayout(factureLayout);
        factureLayout.setHorizontalGroup(
            factureLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, factureLayout.createSequentialGroup()
                .addGroup(factureLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, factureLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(factureLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel29))
                        .addGroup(factureLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(factureLayout.createSequentialGroup()
                                .addGap(399, 399, 399)
                                .addComponent(jLabel19)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, factureLayout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 46, Short.MAX_VALUE)
                                .addGroup(factureLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel11)
                                    .addComponent(jScrollPane12, javax.swing.GroupLayout.PREFERRED_SIZE, 684, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                    .addGroup(factureLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jScrollPane11, javax.swing.GroupLayout.PREFERRED_SIZE, 706, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(44, 44, 44)
                        .addComponent(jScrollPane13, javax.swing.GroupLayout.PREFERRED_SIZE, 310, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(45, 45, 45))
        );
        factureLayout.setVerticalGroup(
            factureLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(factureLayout.createSequentialGroup()
                .addComponent(jLabel11)
                .addGap(7, 7, 7)
                .addGroup(factureLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(factureLayout.createSequentialGroup()
                        .addComponent(jScrollPane12, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel19)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane13, javax.swing.GroupLayout.PREFERRED_SIZE, 281, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(factureLayout.createSequentialGroup()
                        .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel29)
                        .addGap(18, 18, Short.MAX_VALUE)
                        .addComponent(jScrollPane11, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(342, 342, 342))
        );

        Tabs.add(facture, "facture");
        facture.getAccessibleContext().setAccessibleParent(facture);

        jPanel4.setBackground(new java.awt.Color(255, 153, 102));

        jPanel3.setOpaque(false);

        jLabel43.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel43.setText("Rembourcement");

        rembourcement.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jScrollPane14.setViewportView(rembourcement);

        jButton1.setText("Rembourcer");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jTextField1.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                jTextField1CaretUpdate(evt);
            }
        });

        buttonGroup8.add(jRadioButton6);
        jRadioButton6.setText("Nom");

        buttonGroup8.add(jRadioButton8);
        jRadioButton8.setSelected(true);
        jRadioButton8.setText("Numero id");

        jLabel37.setText("Client");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addComponent(jLabel43))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(2, 2, 2)
                        .addComponent(jScrollPane14, javax.swing.GroupLayout.PREFERRED_SIZE, 746, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jButton1))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel37)
                        .addGap(12, 12, 12)
                        .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jRadioButton6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jRadioButton8)))
                .addContainerGap(50, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel43)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jRadioButton6)
                    .addComponent(jRadioButton8)
                    .addComponent(jLabel37))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 10, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addComponent(jButton1)
                        .addGap(287, 287, 287))
                    .addComponent(jScrollPane14, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 330, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap(164, Short.MAX_VALUE)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(79, 79, 79))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(42, 42, 42)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(356, Short.MAX_VALUE))
        );

        Tabs.add(jPanel4, "card7");

        jSplitPane1.setRightComponent(Tabs);

        TopButtons.setBackground(new java.awt.Color(51, 51, 51));

        jPanel7.setOpaque(false);

        reservation_button.setBackground(new java.awt.Color(51, 51, 51));
        reservation_button.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        reservation_button.setForeground(new java.awt.Color(255, 255, 255));
        reservation_button.setText("Reservation");
        reservation_button.setBorderPainted(false);
        reservation_button.setContentAreaFilled(false);
        reservation_button.setFocusPainted(false);
        reservation_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                reservation_buttonActionPerformed(evt);
            }
        });

        location_button.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        location_button.setForeground(new java.awt.Color(255, 255, 255));
        location_button.setText("Location");
        location_button.setBorderPainted(false);
        location_button.setContentAreaFilled(false);
        location_button.setFocusPainted(false);
        location_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                location_buttonActionPerformed(evt);
            }
        });

        client_button.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        client_button.setForeground(new java.awt.Color(255, 255, 255));
        client_button.setText("Clients");
        client_button.setBorderPainted(false);
        client_button.setContentAreaFilled(false);
        client_button.setFocusPainted(false);
        client_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                client_buttonActionPerformed(evt);
            }
        });

        facture_button.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        facture_button.setForeground(new java.awt.Color(255, 255, 255));
        facture_button.setText("Factures");
        facture_button.setBorderPainted(false);
        facture_button.setContentAreaFilled(false);
        facture_button.setFocusPainted(false);
        facture_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                facture_buttonActionPerformed(evt);
            }
        });

        evenement_button.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        evenement_button.setForeground(new java.awt.Color(255, 255, 255));
        evenement_button.setText("Evènements");
        evenement_button.setBorderPainted(false);
        evenement_button.setContentAreaFilled(false);
        evenement_button.setFocusCycleRoot(true);
        evenement_button.setFocusPainted(false);
        evenement_button.setFocusable(false);
        evenement_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                evenement_buttonActionPerformed(evt);
            }
        });

        rembourcer.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        rembourcer.setForeground(new java.awt.Color(255, 255, 255));
        rembourcer.setText("Rembourcement");
        rembourcer.setBorderPainted(false);
        rembourcer.setContentAreaFilled(false);
        rembourcer.setFocusPainted(false);
        rembourcer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rembourcerActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addComponent(client_button, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(51, 51, 51)
                .addComponent(reservation_button)
                .addGap(45, 45, 45)
                .addComponent(location_button, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(61, 61, 61)
                .addComponent(evenement_button)
                .addGap(58, 58, 58)
                .addComponent(facture_button, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 51, Short.MAX_VALUE)
                .addComponent(rembourcer)
                .addGap(34, 34, 34))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(client_button)
                    .addComponent(reservation_button)
                    .addComponent(location_button)
                    .addComponent(facture_button)
                    .addComponent(evenement_button)
                    .addComponent(rembourcer))
                .addContainerGap())
        );

        javax.swing.GroupLayout TopButtonsLayout = new javax.swing.GroupLayout(TopButtons);
        TopButtons.setLayout(TopButtonsLayout);
        TopButtonsLayout.setHorizontalGroup(
            TopButtonsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, TopButtonsLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(87, 87, 87))
        );
        TopButtonsLayout.setVerticalGroup(
            TopButtonsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(TopButtonsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jSplitPane1.setLeftComponent(TopButtons);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane1, javax.swing.GroupLayout.Alignment.TRAILING)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane1, javax.swing.GroupLayout.Alignment.TRAILING)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void reservation_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_reservation_buttonActionPerformed
        Reset_reservation_tab();
        //change card
        card.show(Tabs, "reservation");
        Num_client.requestFocus();
    }//GEN-LAST:event_reservation_buttonActionPerformed

    private void location_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_location_buttonActionPerformed
        Reset_location_tab();
        //change tab
        card.show(Tabs, "location");
        num_client_location.requestFocus();
    }//GEN-LAST:event_location_buttonActionPerformed

    private void client_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_client_buttonActionPerformed
        Reset_client_tab();
        //change tab
        card.show(Tabs, "client");
    }//GEN-LAST:event_client_buttonActionPerformed

    private void ajouter_clientActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ajouter_clientActionPerformed
        ajouter_client();
    }//GEN-LAST:event_ajouter_clientActionPerformed

    private void ajouter_reservationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ajouter_reservationActionPerformed
        ajouterReservation();
    }//GEN-LAST:event_ajouter_reservationActionPerformed

    private void chambre_disponibleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chambre_disponibleActionPerformed
        get_rooms();
    }//GEN-LAST:event_chambre_disponibleActionPerformed

    private void confirmer_reservationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_confirmer_reservationActionPerformed
        confirmerReservation();
    }//GEN-LAST:event_confirmer_reservationActionPerformed

    private void annuler_reservationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_annuler_reservationActionPerformed
        annulerReservation();
    }//GEN-LAST:event_annuler_reservationActionPerformed

    private void supprimer_clientActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_supprimer_clientActionPerformed
        supprimerClients(liste_clients);
    }//GEN-LAST:event_supprimer_clientActionPerformed

    private void salle_disponibleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_salle_disponibleActionPerformed
        get_salles();
    }//GEN-LAST:event_salle_disponibleActionPerformed

    private void ajouter_locationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ajouter_locationActionPerformed
        addLocation();
    }//GEN-LAST:event_ajouter_locationActionPerformed

    private void evenement_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_evenement_buttonActionPerformed
        Reset_evenement_tab();
        card.show(Tabs, "evenement");
        num_client_evenement.requestFocus();

    }//GEN-LAST:event_evenement_buttonActionPerformed

    private void annuler_locationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_annuler_locationActionPerformed
        annulerLocation();
    }//GEN-LAST:event_annuler_locationActionPerformed

    private void facturerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_facturerActionPerformed
        if (reservation_rb.isSelected()) {
            try {
                facture_reservation();
            } catch (IOException | ParseException ex) {
                Logger.getLogger(Accueil.class.getName()).log(Level.SEVERE, null, ex);
            }
            buttonGroup1.clearSelection();
        } else if (location_rb.isSelected()) {
            facture_location();
            buttonGroup1.clearSelection();
        } else {
            JOptionPane.showMessageDialog(this, "Type Facture non seléctionner", " error", JOptionPane.ERROR_MESSAGE);
        }

        DefaultTableModel tm = (DefaultTableModel) liste_reservation_location.getModel();
        tm.setColumnCount(0);
        tm.setRowCount(0);
    }//GEN-LAST:event_facturerActionPerformed

    private void reservation_rbActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_reservation_rbActionPerformed
        get_reservation_facturation();
    }//GEN-LAST:event_reservation_rbActionPerformed

    private void location_rbActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_location_rbActionPerformed
        get_location_facturation();

    }//GEN-LAST:event_location_rbActionPerformed

    private void deconnexionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deconnexionActionPerformed
        dispose();
        Login l = new Login();
        l.setVisible(true);
    }//GEN-LAST:event_deconnexionActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        int column = 0;
        int row = rembourcement.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Location non selectionné", " error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String value = rembourcement.getModel().getValueAt(row, column).toString();

        Connection conn = connect();
        PreparedStatement ps;
        try {
            ps = conn.prepareStatement("delete from reservation where Num_reservation = " + value);
            //execute query
            int r = ps.executeUpdate();
            if (r > 0) {
                JOptionPane.showMessageDialog(this, "Client rembourceé");
                String sql = "Select r.Num_reservation, Nom, Prenom, Num_id, Montant_total from client c, reservation r, facture_reservation f"
                        + " where c.Num_client=r.Num_client and r.Num_reservation = f.Num_reservation and etat_payement = 'payé'";
                Select(rembourcement, sql);
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), " error", JOptionPane.ERROR_MESSAGE);
        }

    }//GEN-LAST:event_jButton1ActionPerformed

    private void facture_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_facture_buttonActionPerformed
        Reset_facture_tab();
        card.show(Tabs, "facture");
    }//GEN-LAST:event_facture_buttonActionPerformed

    private void rembourcerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rembourcerActionPerformed
        Reset_rembourcement_tab();

    }//GEN-LAST:event_rembourcerActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        String sql = "";
        if (ordre_defaut.isSelected()) {
            sql = "select * from client where Num_client not in (select Num_client from reservation)"
                    + " and Num_client not in(select Num_client from louer)";
        } else if (ordre_nom.isSelected()) {
            sql = "select * from client where Num_client not in (select Num_client from reservation)"
                    + " and Num_client not in(select Num_client from louer)"
                    + "Order by Nom ASC;";
        }

        Select(liste_clients, sql);
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        String sql = "";
        if (ordre_defaut.isSelected()) {
            sql = "select * from client where Num_client  in (select Num_client from reservation)"
                    + " or Num_client  in(select Num_client from louer)";
        } else if (ordre_nom.isSelected()) {
            sql = "select * from client where Num_client  in (select Num_client from reservation)"
                    + " or Num_client  in(select Num_client from louer)"
                    + "Order by Nom DESC;";
        }

        Select(liste_clients, sql);
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        String sql = "";
        if (ordre_defaut.isSelected()) {
            sql = "select * from client";
        } else if (ordre_nom.isSelected()) {
            sql = "select * from client"
                    + " Order by Nom DESC;";
        }
        Select(liste_clients, sql);
    }//GEN-LAST:event_jButton4ActionPerformed

    private void Num_clientCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_Num_clientCaretUpdate
        Num_client.requestFocus();
        if (nom_rb.isSelected()) {
            Select(liste_clients_reseervation, "SELECT Num_client, Nom, Prenom, Num_id FROM client"
                    + " where Nom LIKE '" + Num_client.getText() + "%';");
        } else {
            Select(liste_clients_reseervation, "SELECT Num_client, Nom, Prenom, Num_id FROM client"
                    + " where Num_id LIKE '" + Num_client.getText() + "%';");
        }
    }//GEN-LAST:event_Num_clientCaretUpdate

    private void Num_clientActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Num_clientActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_Num_clientActionPerformed

    private void liste_clients_reseervationMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_liste_clients_reseervationMouseClicked
        get_reservation_client();
    }//GEN-LAST:event_liste_clients_reseervationMouseClicked

    private void num_client_locationCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_num_client_locationCaretUpdate
        num_client_location.requestFocus();
        if (nom_rb_location.isSelected()) {
            Select(liste_client_location, "SELECT Num_client, Nom, Prenom, Num_id FROM client"
                    + " where Nom LIKE '" + num_client_location.getText() + "%';");
        } else {
            Select(liste_client_location, "SELECT Num_client, Nom, Prenom, Num_id FROM client"
                    + " where Num_id LIKE '" + num_client_location.getText() + "%';");
        }
    }//GEN-LAST:event_num_client_locationCaretUpdate

    private void nom_rb_locationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nom_rb_locationActionPerformed
        // TODO add your handling code here:
        num_client_location.requestFocus();
    }//GEN-LAST:event_nom_rb_locationActionPerformed

    private void nom_rbActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nom_rbActionPerformed
        Num_client.requestFocus();
    }//GEN-LAST:event_nom_rbActionPerformed

    private void Numero_idActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Numero_idActionPerformed
        Num_client.requestFocus();

    }//GEN-LAST:event_Numero_idActionPerformed

    private void jRadioButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton4ActionPerformed
        num_client_location.requestFocus();

    }//GEN-LAST:event_jRadioButton4ActionPerformed

    private void jRadioButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton7ActionPerformed
        // TODO add your handling code here:
        nom.requestFocus();
    }//GEN-LAST:event_jRadioButton7ActionPerformed

    private void nom_rb_fActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nom_rb_fActionPerformed
        // TODO add your handling code here:
        nom.requestFocus();

    }//GEN-LAST:event_nom_rb_fActionPerformed

    private void nomCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_nomCaretUpdate
        // TODO add your handling code here:
        if (nom_rb_f.isSelected()) {
            Select(client_facturation, "SELECT Num_client, Nom, Prenom, Num_id FROM client"
                    + " where Nom LIKE '" + nom.getText() + "%';");
        } else {
            Select(client_facturation, "SELECT Num_client, Nom, Prenom, Num_id FROM client"
                    + " where Num_id LIKE '" + nom.getText() + "%';");
        }
    }//GEN-LAST:event_nomCaretUpdate

    private void client_facturationMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_client_facturationMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_client_facturationMouseClicked

    private void liste_client_locationMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_liste_client_locationMouseClicked
        get_location_client();
    }//GEN-LAST:event_liste_client_locationMouseClicked

    private void jRadioButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton5ActionPerformed
        // TODO add your handling code here:
        num_client_evenement.requestFocus();
    }//GEN-LAST:event_jRadioButton5ActionPerformed

    private void jRadioButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton3ActionPerformed
        // TODO add your handling code here:
        num_client_evenement.requestFocus();
    }//GEN-LAST:event_jRadioButton3ActionPerformed

    private void num_client_evenementCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_num_client_evenementCaretUpdate
        if (jRadioButton3.isSelected()) {
            Select(lise_client_evenement, "SELECT Num_client, Nom, Prenom, Num_id FROM client"
                    + " where Nom LIKE '" + num_client_evenement.getText() + "%';");
        } else {
            Select(lise_client_evenement, "SELECT Num_client, Nom, Prenom, Num_id FROM client"
                    + " where Num_id LIKE '" + num_client_evenement.getText() + "%';");
        }
    }//GEN-LAST:event_num_client_evenementCaretUpdate

    private void ajouter_evenementActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ajouter_evenementActionPerformed
        ajouterEvenement();
    }//GEN-LAST:event_ajouter_evenementActionPerformed

    private void lise_client_evenementMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lise_client_evenementMouseClicked
        get_location_evenement();
    }//GEN-LAST:event_lise_client_evenementMouseClicked

    private void jTextField1CaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_jTextField1CaretUpdate
        // TODO add your handling code here:
        if (jRadioButton7.isSelected()) {
            Select(rembourcement, "Select r.Num_reservation, Nom, Prenom, Num_id, Montant_total from client c, reservation r, facture_reservation f"
                    + "                         where c.Num_client=r.Num_client and r.Num_reservation = f.Num_reservation and etat_payement = 'payé'"
                    + " and Nom LIKE '" + jTextField1.getText() + "%';");
        } else {
            Select(rembourcement, "Select r.Num_reservation, Nom, Prenom, Num_id, Montant_total from client c, reservation r, facture_reservation f"
                    + "                         where c.Num_client=r.Num_client and r.Num_reservation = f.Num_reservation and etat_payement = 'payé'"
                    + " and Num_id LIKE '" + jTextField1.getText() + "%';");
        }
    }//GEN-LAST:event_jTextField1CaretUpdate


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField Nationalite;
    private javax.swing.JTextField Nom_client;
    private javax.swing.JTextField Num_client;
    private javax.swing.JTextField Num_id;
    private javax.swing.JRadioButton Numero_id;
    private javax.swing.JPanel Tabs;
    private javax.swing.JPanel TopButtons;
    private javax.swing.JButton ajouter_client;
    private javax.swing.JButton ajouter_evenement;
    private javax.swing.JButton ajouter_location;
    private javax.swing.JButton ajouter_reservation;
    private javax.swing.JButton annuler_location;
    private javax.swing.JButton annuler_reservation;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.ButtonGroup buttonGroup2;
    private javax.swing.ButtonGroup buttonGroup3;
    private javax.swing.ButtonGroup buttonGroup4;
    private javax.swing.ButtonGroup buttonGroup5;
    private javax.swing.ButtonGroup buttonGroup6;
    private javax.swing.ButtonGroup buttonGroup7;
    private javax.swing.ButtonGroup buttonGroup8;
    private javax.swing.JButton chambre_disponible;
    private javax.swing.JPanel client;
    private javax.swing.JButton client_button;
    private javax.swing.JTable client_facturation;
    private javax.swing.JButton confirmer_reservation;
    private com.toedter.calendar.JDateChooser date_arrive;
    private com.toedter.calendar.JDateChooser date_debut_evenement;
    private com.toedter.calendar.JDateChooser date_debut_location;
    private com.toedter.calendar.JDateChooser date_depart;
    private com.toedter.calendar.JDateChooser date_depart_facturation;
    private com.toedter.calendar.JDateChooser date_fin_evenement;
    private com.toedter.calendar.JDateChooser date_fin_location;
    private com.toedter.calendar.JDateChooser date_naissance_client;
    private javax.swing.JButton deconnexion;
    private javax.swing.JPanel evenement;
    private javax.swing.JButton evenement_button;
    private javax.swing.JPanel facture;
    private javax.swing.JTextArea facture_area;
    private javax.swing.JButton facture_button;
    private javax.swing.JButton facturer;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel43;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JRadioButton jRadioButton1;
    private javax.swing.JRadioButton jRadioButton2;
    private javax.swing.JRadioButton jRadioButton3;
    private javax.swing.JRadioButton jRadioButton4;
    private javax.swing.JRadioButton jRadioButton5;
    private javax.swing.JRadioButton jRadioButton6;
    private javax.swing.JRadioButton jRadioButton7;
    private javax.swing.JRadioButton jRadioButton8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane10;
    private javax.swing.JScrollPane jScrollPane11;
    private javax.swing.JScrollPane jScrollPane12;
    private javax.swing.JScrollPane jScrollPane13;
    private javax.swing.JScrollPane jScrollPane14;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JScrollPane jScrollPane9;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTable lise_client_evenement;
    private javax.swing.JTable list_chambre;
    private javax.swing.JTable list_evnement;
    private javax.swing.JTable liste_client_location;
    private javax.swing.JTable liste_clients;
    private javax.swing.JTable liste_clients_reseervation;
    private javax.swing.JTable liste_locations;
    private javax.swing.JTable liste_reservation_location;
    private javax.swing.JTable liste_salle_disponible;
    private javax.swing.JTable liste_salle_louer;
    private javax.swing.JPanel location;
    private javax.swing.JButton location_button;
    private javax.swing.JRadioButton location_rb;
    private javax.swing.JComboBox<String> modep;
    private javax.swing.JTextField nom;
    private javax.swing.JTextField nom_evenement;
    private javax.swing.JRadioButton nom_rb;
    private javax.swing.JRadioButton nom_rb_f;
    private javax.swing.JRadioButton nom_rb_location;
    private javax.swing.JTextField num_client_evenement;
    private javax.swing.JTextField num_client_location;
    private javax.swing.JRadioButton ordre_defaut;
    private javax.swing.JRadioButton ordre_nom;
    private javax.swing.JTextField prenom_client;
    private javax.swing.JTextField profession_client;
    private javax.swing.JTable rembourcement;
    private javax.swing.JButton rembourcer;
    private javax.swing.JPanel reservation;
    private javax.swing.JButton reservation_button;
    private javax.swing.JRadioButton reservation_rb;
    private javax.swing.JButton salle_disponible;
    private javax.swing.JButton supprimer_client;
    private javax.swing.JTable table_reservation;
    private javax.swing.JComboBox<String> type_id;
    // End of variables declaration//GEN-END:variables
}
