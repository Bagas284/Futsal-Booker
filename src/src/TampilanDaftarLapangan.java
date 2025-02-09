/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package src;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.swing.table.DefaultTableModel;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import java.awt.GridLayout;

public class TampilanDaftarLapangan extends javax.swing.JFrame {

    private final DefaultTableModel model;
    private final ArrayList<Lapangan> daftarLapangan;

    /**
     * Creates new form TampilanDaftarLapangan
     */
    public TampilanDaftarLapangan() {
        // Initialize collections
        model = new DefaultTableModel();
        daftarLapangan = new ArrayList<>();

        // Setup frame
        setTitle("Manajemen Lapangan");

        initComponents();
        pack();
        setLocationRelativeTo(null);

        // Setup table model
        model.addColumn("Nama");
        model.addColumn("Tipe Lapangan");
        model.addColumn("Harga perjam");
        model.addColumn("Status");
        model.addColumn("Fasilitas");

        tabelLapangan.setModel(model);

        // Load initial data
        loadDataFromDatabase();
        populateTable();
         setExtendedState(javax.swing.JFrame.MAXIMIZED_BOTH);
        // Tambahan agar benar-benar full screen
        setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
    }

    private void loadDataFromDatabase() {
        daftarLapangan.clear();

        try (Connection conn = new Koneksi().getConnection(); PreparedStatement ps = conn.prepareStatement("SELECT * FROM lapangan"); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int idLapangan = rs.getInt("id_lapangan"); // Ambil idLapangan dari database
                String nama = rs.getString("nama_lapangan");
                String tipe = rs.getString("tipe_lapangan");
                String status = rs.getString("status");
                double hargaPerJam = rs.getDouble("hargaPerJam"); 
               

                // Sesuaikan pemanggilan konstruktor untuk menyertakan idLapangan
                Lapangan lapangan = tipe.equalsIgnoreCase("Rumput Sintetis (Indoor)")
                        ? new Indoor(idLapangan, nama, status, hargaPerJam)
                        : new Outdoor(idLapangan, nama, status, hargaPerJam);

                daftarLapangan.add(lapangan);
            }
        } catch (SQLException ex) {
            System.err.println("Error loading data: " + ex.getMessage());
        }
    }

    private void populateTable() {
        model.setRowCount(0);

        for (Lapangan lapangan : daftarLapangan) {
            Object[] rowData = new Object[5];
            rowData[0] = lapangan.getNama_lapangan();
            if (lapangan instanceof Indoor) {
                Indoor indoor = (Indoor) lapangan;
                rowData[1] = indoor.getTipe_lapangan();
                rowData[2] = indoor.getHargaPerJam();
            } else if (lapangan instanceof Outdoor) {
                Outdoor outdoor = (Outdoor) lapangan;
                rowData[1] = outdoor.getTipe_lapangan();
                rowData[2] = outdoor.getHargaPerJam();
            }
            rowData[3] = lapangan.getStatus();
            if (lapangan instanceof Indoor) {
                Indoor indoor = (Indoor) lapangan;
                rowData[4] = indoor.getFasilitas();
            }else {
                 rowData[4] = "-";
            }
            model.addRow(rowData);
        }
    }

    private void delete() {
        int selectedRow = tabelLapangan.getSelectedRow();
        if (selectedRow == -1) {
            javax.swing.JOptionPane.showMessageDialog(this,
                    "Pilih lapangan yang akan dihapus!!",
                    "Warning",
                    javax.swing.JOptionPane.WARNING_MESSAGE);
            return;
        }

        String namaLapangan = (String) model.getValueAt(selectedRow, 0);

        int konfirmasi = javax.swing.JOptionPane.showConfirmDialog(
                this,
                "Yakin ingin menghapus " + namaLapangan + "?",
                "Konfirmasi Hapus",
                javax.swing.JOptionPane.YES_NO_OPTION
        );
        if (konfirmasi == javax.swing.JOptionPane.YES_OPTION) {
            try {
                Connection conn = new Koneksi().getConnection();
                String sql = "DELETE FROM lapangan WHERE nama_lapangan = ?";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setString(1, namaLapangan);
                ps.executeUpdate();

                loadDataFromDatabase();
                populateTable();

                javax.swing.JOptionPane.showMessageDialog(this,
                        "Lapangan berhasil dihapus",
                        "Success",
                        javax.swing.JOptionPane.INFORMATION_MESSAGE);

                ps.close();
                conn.close();

            } catch (SQLException ex) {
                javax.swing.JOptionPane.showMessageDialog(this,
                        "Error menghapus data karena lapangan sedang terbooking",
                        "Error",
                        javax.swing.JOptionPane.ERROR_MESSAGE);
            }
            
        }
    }
    
private void edit() {
    int selectedRow = tabelLapangan.getSelectedRow();
    if (selectedRow == -1) {
        javax.swing.JOptionPane.showMessageDialog(this,
                "Pilih lapangan yang akan diedit!",
                "Warning",
                javax.swing.JOptionPane.WARNING_MESSAGE);
        return;
    }

    // Ambil data lapangan dari tabel
    String namaLapanganLama = (String) model.getValueAt(selectedRow, 0);
    String statusSaatIni = (String) model.getValueAt(selectedRow, 1); // Asumsikan kolom 1 adalah status
    String[] opsiStatus = {"Tersedia", "Tidak Tersedia"};

    // Panel form untuk dialog
    JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 10));
    JLabel lblNamaLapangan = new JLabel("Nama Lapangan:");
    JTextField txtNamaLapangan = new JTextField(namaLapanganLama);
    JLabel lblStatus = new JLabel("Status:");
    JComboBox<String> cmbStatus = new JComboBox<>(opsiStatus);
    cmbStatus.setSelectedItem(statusSaatIni);

    formPanel.add(lblNamaLapangan);
    formPanel.add(txtNamaLapangan);
    formPanel.add(lblStatus);
    formPanel.add(cmbStatus);

    // Dialog konfirmasi
    int result = javax.swing.JOptionPane.showConfirmDialog(this,
            formPanel,
            "Edit Lapangan",
            javax.swing.JOptionPane.OK_CANCEL_OPTION,
            javax.swing.JOptionPane.PLAIN_MESSAGE);

    if (result == javax.swing.JOptionPane.OK_OPTION) {
        String namaLapanganBaru = txtNamaLapangan.getText().trim();
        String statusBaru = (String) cmbStatus.getSelectedItem();

        // Validasi input
        if (namaLapanganBaru.isEmpty()) {
            javax.swing.JOptionPane.showMessageDialog(this,
                    "Nama lapangan tidak boleh kosong!",
                    "Error",
                    javax.swing.JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            Connection conn = new Koneksi().getConnection();
            String sql = "UPDATE lapangan SET nama_lapangan = ?, status = ? WHERE nama_lapangan = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, namaLapanganBaru);
            ps.setString(2, statusBaru);
            ps.setString(3, namaLapanganLama);
            ps.executeUpdate();

            // Memuat ulang data dari database dan memperbarui tabel
            loadDataFromDatabase();
            populateTable();

            javax.swing.JOptionPane.showMessageDialog(this,
                    "Data lapangan berhasil diperbarui!",
                    "Success",
                    javax.swing.JOptionPane.INFORMATION_MESSAGE);

            ps.close();
            conn.close();

        } catch (SQLException ex) {
            javax.swing.JOptionPane.showMessageDialog(this,
                    "Error mengedit data: " + ex.getMessage(),
                    "Error",
                    javax.swing.JOptionPane.ERROR_MESSAGE);
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

        menu = new javax.swing.JPanel();
        GambarBola = new javax.swing.JLabel();
        pDaftarLapangan = new javax.swing.JPanel();
        bDaftarLapangan = new javax.swing.JButton();
        pTambahLapangan = new javax.swing.JPanel();
        bTambahLapangan = new javax.swing.JButton();
        pKonfirmasi = new javax.swing.JPanel();
        bKonfirmasi = new javax.swing.JButton();
        pKeluar = new javax.swing.JPanel();
        bKeluar = new javax.swing.JButton();
        plihatPembayaran = new javax.swing.JPanel();
        bLihatPembayaran = new javax.swing.JButton();
        main = new javax.swing.JPanel();
        TtambahLapangan = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tabelLapangan = new javax.swing.JTable();
        Hapus = new javax.swing.JButton();
        Edit = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        menu.setBackground(new java.awt.Color(255, 0, 51));
        menu.setPreferredSize(new java.awt.Dimension(200, 500));

        GambarBola.setBackground(new java.awt.Color(0, 0, 0));
        GambarBola.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        GambarBola.setIcon(new javax.swing.ImageIcon(getClass().getResource("/src/sports_soccer_24dp_E8EAED_FILL0_wght400_GRAD0_opsz24.png"))); // NOI18N
        GambarBola.setPreferredSize(new java.awt.Dimension(10, 96));

        pDaftarLapangan.setBackground(new java.awt.Color(204, 0, 51));

        bDaftarLapangan.setBackground(new java.awt.Color(255, 0, 51));
        bDaftarLapangan.setFont(new java.awt.Font("Segoe UI Historic", 1, 18)); // NOI18N
        bDaftarLapangan.setForeground(new java.awt.Color(255, 255, 255));
        bDaftarLapangan.setText("Daftar Lapangan");
        bDaftarLapangan.setContentAreaFilled(false);
        bDaftarLapangan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bDaftarLapanganActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pDaftarLapanganLayout = new javax.swing.GroupLayout(pDaftarLapangan);
        pDaftarLapangan.setLayout(pDaftarLapanganLayout);
        pDaftarLapanganLayout.setHorizontalGroup(
            pDaftarLapanganLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(bDaftarLapangan, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        pDaftarLapanganLayout.setVerticalGroup(
            pDaftarLapanganLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pDaftarLapanganLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(bDaftarLapangan)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pTambahLapangan.setBackground(new java.awt.Color(204, 0, 51));

        bTambahLapangan.setBackground(new java.awt.Color(255, 0, 51));
        bTambahLapangan.setFont(new java.awt.Font("Segoe UI Historic", 1, 18)); // NOI18N
        bTambahLapangan.setForeground(new java.awt.Color(255, 255, 255));
        bTambahLapangan.setText("Tambah Lapangan");
        bTambahLapangan.setContentAreaFilled(false);
        bTambahLapangan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bTambahLapanganActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pTambahLapanganLayout = new javax.swing.GroupLayout(pTambahLapangan);
        pTambahLapangan.setLayout(pTambahLapanganLayout);
        pTambahLapanganLayout.setHorizontalGroup(
            pTambahLapanganLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(bTambahLapangan, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        pTambahLapanganLayout.setVerticalGroup(
            pTambahLapanganLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pTambahLapanganLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(bTambahLapangan)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pKonfirmasi.setBackground(new java.awt.Color(204, 0, 51));

        bKonfirmasi.setBackground(new java.awt.Color(255, 0, 51));
        bKonfirmasi.setFont(new java.awt.Font("Segoe UI Historic", 1, 18)); // NOI18N
        bKonfirmasi.setForeground(new java.awt.Color(255, 255, 255));
        bKonfirmasi.setText("Konfirmasi");
        bKonfirmasi.setContentAreaFilled(false);
        bKonfirmasi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bKonfirmasiActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pKonfirmasiLayout = new javax.swing.GroupLayout(pKonfirmasi);
        pKonfirmasi.setLayout(pKonfirmasiLayout);
        pKonfirmasiLayout.setHorizontalGroup(
            pKonfirmasiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(bKonfirmasi, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        pKonfirmasiLayout.setVerticalGroup(
            pKonfirmasiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pKonfirmasiLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(bKonfirmasi)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pKeluar.setBackground(new java.awt.Color(204, 0, 51));

        bKeluar.setBackground(new java.awt.Color(255, 0, 51));
        bKeluar.setFont(new java.awt.Font("Segoe UI Historic", 1, 18)); // NOI18N
        bKeluar.setForeground(new java.awt.Color(255, 255, 255));
        bKeluar.setText("Keluar");
        bKeluar.setContentAreaFilled(false);
        bKeluar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bKeluarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pKeluarLayout = new javax.swing.GroupLayout(pKeluar);
        pKeluar.setLayout(pKeluarLayout);
        pKeluarLayout.setHorizontalGroup(
            pKeluarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(bKeluar, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        pKeluarLayout.setVerticalGroup(
            pKeluarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pKeluarLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(bKeluar)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        plihatPembayaran.setBackground(new java.awt.Color(204, 0, 51));

        bLihatPembayaran.setBackground(new java.awt.Color(255, 0, 51));
        bLihatPembayaran.setFont(new java.awt.Font("Segoe UI Historic", 1, 18)); // NOI18N
        bLihatPembayaran.setForeground(new java.awt.Color(255, 255, 255));
        bLihatPembayaran.setText("Histori Pembayaran");
        bLihatPembayaran.setContentAreaFilled(false);
        bLihatPembayaran.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bLihatPembayaranActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout plihatPembayaranLayout = new javax.swing.GroupLayout(plihatPembayaran);
        plihatPembayaran.setLayout(plihatPembayaranLayout);
        plihatPembayaranLayout.setHorizontalGroup(
            plihatPembayaranLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(bLihatPembayaran, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        plihatPembayaranLayout.setVerticalGroup(
            plihatPembayaranLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(plihatPembayaranLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(bLihatPembayaran)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout menuLayout = new javax.swing.GroupLayout(menu);
        menu.setLayout(menuLayout);
        menuLayout.setHorizontalGroup(
            menuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(menuLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(menuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pKeluar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pKonfirmasi, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pTambahLapangan, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pDaftarLapangan, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(GambarBola, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, menuLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(plihatPembayaran, javax.swing.GroupLayout.PREFERRED_SIZE, 189, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        menuLayout.setVerticalGroup(
            menuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(menuLayout.createSequentialGroup()
                .addGap(38, 38, 38)
                .addComponent(GambarBola, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(43, 43, 43)
                .addComponent(pDaftarLapangan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(pTambahLapangan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(pKonfirmasi, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(plihatPembayaran, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 50, Short.MAX_VALUE)
                .addComponent(pKeluar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        getContentPane().add(menu, java.awt.BorderLayout.LINE_START);

        main.setBackground(new java.awt.Color(255, 255, 255));

        TtambahLapangan.setFont(new java.awt.Font("Segoe UI Emoji", 1, 36)); // NOI18N
        TtambahLapangan.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        TtambahLapangan.setText("Daftar Lapangan");

        tabelLapangan.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        tabelLapangan.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Nama Lapangan", "Tipe lapangan", "Harga perjam", "Jenis", "Fasilitas Tambahan"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tabelLapangan.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        tabelLapangan.setGridColor(new java.awt.Color(0, 0, 0));
        tabelLapangan.setRowHeight(30);
        tabelLapangan.setSelectionBackground(new java.awt.Color(255, 153, 102));
        tabelLapangan.setShowGrid(true);
        tabelLapangan.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(tabelLapangan);
        if (tabelLapangan.getColumnModel().getColumnCount() > 0) {
            tabelLapangan.getColumnModel().getColumn(0).setResizable(false);
            tabelLapangan.getColumnModel().getColumn(1).setResizable(false);
            tabelLapangan.getColumnModel().getColumn(2).setResizable(false);
            tabelLapangan.getColumnModel().getColumn(3).setResizable(false);
            tabelLapangan.getColumnModel().getColumn(4).setResizable(false);
        }

        Hapus.setText("Hapus");
        Hapus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                HapusActionPerformed(evt);
            }
        });

        Edit.setText("Edit");
        Edit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                EditActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout mainLayout = new javax.swing.GroupLayout(main);
        main.setLayout(mainLayout);
        mainLayout.setHorizontalGroup(
            mainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(TtambahLapangan, javax.swing.GroupLayout.DEFAULT_SIZE, 626, Short.MAX_VALUE)
            .addGroup(mainLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(mainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(mainLayout.createSequentialGroup()
                        .addComponent(Edit, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Hapus, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        mainLayout.setVerticalGroup(
            mainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainLayout.createSequentialGroup()
                .addGap(40, 40, 40)
                .addComponent(TtambahLapangan, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(mainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(Hapus)
                    .addComponent(Edit))
                .addContainerGap(204, Short.MAX_VALUE))
        );

        getContentPane().add(main, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void bDaftarLapanganActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bDaftarLapanganActionPerformed
        TampilanDaftarLapangan tdl = new TampilanDaftarLapangan();
        tdl.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_bDaftarLapanganActionPerformed

    private void bTambahLapanganActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bTambahLapanganActionPerformed
        TampilanTambahLapangan ttl = new TampilanTambahLapangan();
        ttl.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_bTambahLapanganActionPerformed

    private void bKonfirmasiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bKonfirmasiActionPerformed
        TampilanKonfirmasiBooking tkb = new TampilanKonfirmasiBooking();
        tkb.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_bKonfirmasiActionPerformed

    private void bKeluarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bKeluarActionPerformed
        Login login = new Login();
        login.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_bKeluarActionPerformed

    private void HapusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_HapusActionPerformed
        delete();
    }//GEN-LAST:event_HapusActionPerformed

    private void bLihatPembayaranActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bLihatPembayaranActionPerformed
        TampilanLihatPembayaran tlb = new TampilanLihatPembayaran();
        tlb.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_bLihatPembayaranActionPerformed

    private void EditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_EditActionPerformed
        edit();
    }//GEN-LAST:event_EditActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(TampilanDaftarLapangan.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(TampilanDaftarLapangan.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(TampilanDaftarLapangan.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(TampilanDaftarLapangan.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new TampilanDaftarLapangan().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton Edit;
    private javax.swing.JLabel GambarBola;
    private javax.swing.JButton Hapus;
    private javax.swing.JLabel TtambahLapangan;
    private javax.swing.JButton bDaftarLapangan;
    private javax.swing.JButton bKeluar;
    private javax.swing.JButton bKonfirmasi;
    private javax.swing.JButton bLihatPembayaran;
    private javax.swing.JButton bTambahLapangan;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPanel main;
    private javax.swing.JPanel menu;
    private javax.swing.JPanel pDaftarLapangan;
    private javax.swing.JPanel pKeluar;
    private javax.swing.JPanel pKonfirmasi;
    private javax.swing.JPanel pTambahLapangan;
    private javax.swing.JPanel plihatPembayaran;
    private javax.swing.JTable tabelLapangan;
    // End of variables declaration//GEN-END:variables
}
