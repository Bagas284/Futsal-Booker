/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package src;

import java.sql.SQLException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import javax.swing.JOptionPane;
/**
 *
 * @author Bagas Aldianata
 */
public class TampilanTambahLapangan extends javax.swing.JFrame {

    /**
     * Creates new form TampilanManageLapangan
     */
    public TampilanTambahLapangan() {
        setTitle("Manajemen Lapangan");
        setSize(800, 600);
        
        initComponents();
        pack();
        setLocationRelativeTo(null);
         setExtendedState(javax.swing.JFrame.MAXIMIZED_BOTH);
        // Tambahan agar benar-benar full screen
        setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
    }
    
   private void simpanData() {
    if (namaLapangan.getText().trim().isEmpty()) {
        JOptionPane.showMessageDialog(this, "Nama lapangan tidak boleh kosong!", "Peringatan", JOptionPane.WARNING_MESSAGE);
        return;
    }

    String kueriInsert = "INSERT INTO lapangan (nama_lapangan, tipe_lapangan, hargaPerJam, status) VALUES (?, ?, ?, ?)";
    int rowAffect = 0;

    try (Connection kon = new Koneksi().getConnection(); 
         PreparedStatement ps = kon.prepareStatement(kueriInsert)) {

        // Ambil data dari form
        String namaLapanganValue = namaLapangan.getText().trim();
        String tipeLapanganValue = tipeLapangan.getSelectedItem().toString();
        String statusValue = status.getSelectedItem().toString();
        int hargaValue = Integer.parseInt(hargaPerJam.getText());

        // Cek tipe lapangan dan inisialisasi harga
        if (tipeLapanganValue.equalsIgnoreCase("Indoor")) {
            Indoor indoor = new Indoor(0, namaLapanganValue, statusValue, hargaValue); // Gunakan ID default (0)
            ps.setString(1, indoor.getNama_lapangan());
            ps.setString(2, indoor.getTipe_lapangan());
            ps.setInt(3, (int) indoor.getHargaPerJam());
        } else {
            Outdoor outdoor = new Outdoor(0, namaLapanganValue, statusValue, hargaValue); // Gunakan ID default (0)
            ps.setString(1, outdoor.getNama_lapangan());
            ps.setString(2, outdoor.getTipe_lapangan());
            ps.setInt(3, (int) outdoor.getHargaPerJam());
        }

        ps.setString(4, statusValue);
        // Eksekusi query
        rowAffect = ps.executeUpdate();

        if (rowAffect > 0) {
            JOptionPane.showMessageDialog(this, "Data berhasil ditambahkan!", "Berhasil", JOptionPane.INFORMATION_MESSAGE);
        }

    } catch (SQLException ex) {
        JOptionPane.showMessageDialog(this, "Terjadi kesalahan: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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
        java.awt.GridBagConstraints gridBagConstraints;

        menu = new javax.swing.JPanel();
        GambarBola = new javax.swing.JLabel();
        pDaftarLapangan = new javax.swing.JPanel();
        bDaftarLapangan = new javax.swing.JButton();
        pTambahLapangan = new javax.swing.JPanel();
        bTambahLapangan = new javax.swing.JButton();
        pKonfirmasi = new javax.swing.JPanel();
        bKonfirmasi = new javax.swing.JButton();
        pKeluar = new javax.swing.JPanel();
        bKonfirmasi2 = new javax.swing.JButton();
        plihatPembayaran = new javax.swing.JPanel();
        bLihatPembayaran = new javax.swing.JButton();
        main = new javax.swing.JPanel();
        TtambahLapangan = new javax.swing.JLabel();
        TnamaLapangan = new javax.swing.JLabel();
        namaLapangan = new javax.swing.JTextField();
        TtipeLapangan = new javax.swing.JLabel();
        tipeLapangan = new javax.swing.JComboBox<>();
        Tstatus = new javax.swing.JLabel();
        status = new javax.swing.JComboBox<>();
        jPanel2 = new javax.swing.JPanel();
        bSimpan = new javax.swing.JButton();
        ThargaPerJam = new javax.swing.JLabel();
        hargaPerJam = new javax.swing.JTextField();

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

        javax.swing.GroupLayout pTambahLapanganLayout = new javax.swing.GroupLayout(pTambahLapangan);
        pTambahLapangan.setLayout(pTambahLapanganLayout);
        pTambahLapanganLayout.setHorizontalGroup(
            pTambahLapanganLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(bTambahLapangan, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 187, Short.MAX_VALUE)
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

        bKonfirmasi2.setBackground(new java.awt.Color(255, 0, 51));
        bKonfirmasi2.setFont(new java.awt.Font("Segoe UI Historic", 1, 18)); // NOI18N
        bKonfirmasi2.setForeground(new java.awt.Color(255, 255, 255));
        bKonfirmasi2.setText("Keluar");
        bKonfirmasi2.setContentAreaFilled(false);
        bKonfirmasi2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bKonfirmasi2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pKeluarLayout = new javax.swing.GroupLayout(pKeluar);
        pKeluar.setLayout(pKeluarLayout);
        pKeluarLayout.setHorizontalGroup(
            pKeluarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(bKonfirmasi2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        pKeluarLayout.setVerticalGroup(
            pKeluarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pKeluarLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(bKonfirmasi2)
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
                    .addComponent(GambarBola, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pKeluar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(menuLayout.createSequentialGroup()
                        .addGroup(menuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(plihatPembayaran, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                            .addComponent(pKonfirmasi, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(pTambahLapangan, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(pDaftarLapangan, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(0, 1, Short.MAX_VALUE)))
                .addContainerGap())
        );
        menuLayout.setVerticalGroup(
            menuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, menuLayout.createSequentialGroup()
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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 101, Short.MAX_VALUE)
                .addComponent(pKeluar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        menuLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {pKeluar, pKonfirmasi, pTambahLapangan});

        getContentPane().add(menu, java.awt.BorderLayout.LINE_START);

        main.setBackground(new java.awt.Color(255, 255, 255));

        TtambahLapangan.setFont(new java.awt.Font("Segoe UI Emoji", 1, 36)); // NOI18N
        TtambahLapangan.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        TtambahLapangan.setText("Tambah Lapangan");

        TnamaLapangan.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        TnamaLapangan.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        TnamaLapangan.setText("Nama Lapangan");

        namaLapangan.setPreferredSize(new java.awt.Dimension(172, 32));

        TtipeLapangan.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        TtipeLapangan.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        TtipeLapangan.setText("Tipe Lapangan");

        tipeLapangan.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Indoor", "Outdoor" }));

        Tstatus.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        Tstatus.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        Tstatus.setText("Status");

        status.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Tersedia", "Tidak Tersedia" }));
        status.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                statusActionPerformed(evt);
            }
        });

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setLayout(new java.awt.GridBagLayout());

        bSimpan.setBackground(new java.awt.Color(255, 0, 51));
        bSimpan.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        bSimpan.setText("Simpan");
        bSimpan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bSimpanActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.ipadx = 354;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(28, 84, 30, 84);
        jPanel2.add(bSimpan, gridBagConstraints);

        ThargaPerJam.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        ThargaPerJam.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        ThargaPerJam.setText("Harga perjam");

        hargaPerJam.setPreferredSize(new java.awt.Dimension(172, 32));

        javax.swing.GroupLayout mainLayout = new javax.swing.GroupLayout(main);
        main.setLayout(mainLayout);
        mainLayout.setHorizontalGroup(
            mainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(TtambahLapangan, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, mainLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(mainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, 628, Short.MAX_VALUE)
                    .addGroup(mainLayout.createSequentialGroup()
                        .addGroup(mainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(ThargaPerJam, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, mainLayout.createSequentialGroup()
                                .addGap(10, 10, 10)
                                .addGroup(mainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(Tstatus, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(TtipeLapangan, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(TnamaLapangan, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 282, Short.MAX_VALUE))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(mainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(namaLapangan, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(tipeLapangan, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(status, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(hargaPerJam, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );
        mainLayout.setVerticalGroup(
            mainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainLayout.createSequentialGroup()
                .addGap(59, 59, 59)
                .addComponent(TtambahLapangan, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(28, 28, 28)
                .addGroup(mainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(TnamaLapangan, javax.swing.GroupLayout.DEFAULT_SIZE, 38, Short.MAX_VALUE)
                    .addComponent(namaLapangan, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(28, 28, 28)
                .addGroup(mainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(TtipeLapangan, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tipeLapangan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(28, 28, 28)
                .addGroup(mainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(Tstatus, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(status, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(mainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(ThargaPerJam, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(hargaPerJam, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(27, 27, 27)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(94, Short.MAX_VALUE))
        );

        mainLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {TnamaLapangan, namaLapangan});

        mainLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {TtipeLapangan, tipeLapangan});

        mainLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {Tstatus, status});

        getContentPane().add(main, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void bSimpanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bSimpanActionPerformed
        simpanData();
    }//GEN-LAST:event_bSimpanActionPerformed

    private void bDaftarLapanganActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bDaftarLapanganActionPerformed
        TampilanDaftarLapangan tdl = new TampilanDaftarLapangan();
        tdl.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_bDaftarLapanganActionPerformed

    private void bKonfirmasiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bKonfirmasiActionPerformed
        TampilanKonfirmasiBooking tkb = new TampilanKonfirmasiBooking();
        tkb.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_bKonfirmasiActionPerformed

    private void bKonfirmasi2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bKonfirmasi2ActionPerformed
        Login login = new Login();
        login.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_bKonfirmasi2ActionPerformed

    private void bLihatPembayaranActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bLihatPembayaranActionPerformed
        TampilanLihatPembayaran tlb = new TampilanLihatPembayaran();
        tlb.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_bLihatPembayaranActionPerformed

    private void statusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_statusActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_statusActionPerformed

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
            java.util.logging.Logger.getLogger(TampilanTambahLapangan.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(TampilanTambahLapangan.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(TampilanTambahLapangan.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(TampilanTambahLapangan.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new TampilanTambahLapangan().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel GambarBola;
    private javax.swing.JLabel ThargaPerJam;
    private javax.swing.JLabel TnamaLapangan;
    private javax.swing.JLabel Tstatus;
    private javax.swing.JLabel TtambahLapangan;
    private javax.swing.JLabel TtipeLapangan;
    private javax.swing.JButton bDaftarLapangan;
    private javax.swing.JButton bKonfirmasi;
    private javax.swing.JButton bKonfirmasi2;
    private javax.swing.JButton bLihatPembayaran;
    private javax.swing.JButton bSimpan;
    private javax.swing.JButton bTambahLapangan;
    private javax.swing.JTextField hargaPerJam;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel main;
    private javax.swing.JPanel menu;
    private javax.swing.JTextField namaLapangan;
    private javax.swing.JPanel pDaftarLapangan;
    private javax.swing.JPanel pKeluar;
    private javax.swing.JPanel pKonfirmasi;
    private javax.swing.JPanel pTambahLapangan;
    private javax.swing.JPanel plihatPembayaran;
    private javax.swing.JComboBox<String> status;
    private javax.swing.JComboBox<String> tipeLapangan;
    // End of variables declaration//GEN-END:variables
}
