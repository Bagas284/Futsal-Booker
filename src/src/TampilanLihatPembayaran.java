/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package src;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.nio.file.Path;
import java.nio.file.Paths;

public class TampilanLihatPembayaran extends javax.swing.JFrame {

    private JPanel mainPanel;
    private JScrollPane scrollPane;
    private final String UPLOAD_FOLDER = "src/upload/"; // Path relatif ke folder upload

    public TampilanLihatPembayaran() {
        initComponents();
        setupAdditionalComponents();
        setTitle("Lihat Pembayaran");
        setLocationRelativeTo(null);
        loadPembayaran();
        setExtendedState(javax.swing.JFrame.MAXIMIZED_BOTH);
        // Tambahan agar benar-benar full screen
        setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
    }

    private void setupAdditionalComponents() {
        Container contentPane = getContentPane();
        mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        scrollPane = new JScrollPane(mainPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        contentPane.add(scrollPane, BorderLayout.CENTER);
    }

    private String getAbsoluteUploadPath(String fileName) {
        try {
            // Mendapatkan pFath absolut dari project
            String currentPath = System.getProperty("user.dir");
            // Gabungkan dengan path ke folder upload dan nama file
            // Path ke folder upload harus sesuai dengan struktur project
            return Paths.get(currentPath, "src", fileName).toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void loadPembayaran() {
        mainPanel.removeAll();

        try (Connection conn = new Koneksi().getConnection()) {
            String sql = """
                SELECT 
                    p.id_pembayaran, p.jumlah_bayar, p.bukti_bayar, p.tanggal_bayar,
                    b.id_booking, b.jadwal, b.durasi, b.total_harga, b.status,
                    pg.namaPengguna,
                    l.nama_lapangan
                FROM pembayaran p 
                JOIN booking b ON p.id_booking = b.id_booking
                JOIN pengguna pg ON b.id_pengguna = pg.idPengguna
                JOIN lapangan l ON b.id_lapangan = l.id_lapangan
                ORDER BY p.tanggal_bayar DESC
            """;

            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                // Mendapatkan nama file dari database
                String buktiFileName = rs.getString("bukti_bayar");
                // Mendapatkan path lengkap file
                String fullPath = buktiFileName != null ? getAbsoluteUploadPath(buktiFileName) : null;

                JPanel cardPanel = createPaymentCard(
                        rs.getInt("id_pembayaran"),
                        rs.getDouble("jumlah_bayar"),
                        fullPath, // Menggunakan path lengkap
                        rs.getTimestamp("tanggal_bayar"),
                        rs.getInt("id_booking"),
                        rs.getTimestamp("jadwal"),
                        rs.getInt("durasi"),
                        rs.getString("status"),
                        rs.getString("namaPengguna"),
                        rs.getString("nama_lapangan"),
                        buktiFileName // Nama file original untuk display
                );

                mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
                mainPanel.add(cardPanel);
            }

            mainPanel.revalidate();
            mainPanel.repaint();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error loading payment data: " + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private JPanel createPaymentCard(int idPembayaran, double jumlahBayar,
            String fullPath, Timestamp tanggalBayar, int idBooking,
            Timestamp jadwal, int durasi, String status,
            String namaPengguna, String namaLapangan,
            String originalFileName) {

        JPanel card = new JPanel();
        card.setLayout(new GridBagLayout());
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Left Panel for Payment Details
        JPanel detailsPanel = new JPanel(new GridLayout(8, 1, 5, 5));
        detailsPanel.add(new JLabel("ID Pembayaran: " + idPembayaran));
        detailsPanel.add(new JLabel("Jumlah Bayar: Rp " + String.format("%,.2f", jumlahBayar)));
        detailsPanel.add(new JLabel("Tanggal Bayar: " + tanggalBayar.toString()));
        detailsPanel.add(new JLabel("ID Booking: " + idBooking));
        detailsPanel.add(new JLabel("Jadwal: " + jadwal.toString()));
        detailsPanel.add(new JLabel("Pengguna: " + namaPengguna));
        detailsPanel.add(new JLabel("Lapangan: " + namaLapangan));
        detailsPanel.add(new JLabel("Bukti Bayar: " + (originalFileName != null ? originalFileName : "Tidak ada")));

        // Right Panel for Status and Button
        JPanel actionPanel = new JPanel(new GridLayout(2, 1, 5, 5));

        JLabel statusLabel = new JLabel("Status: " + status);
        statusLabel.setFont(statusLabel.getFont().deriveFont(Font.BOLD));
        actionPanel.add(statusLabel);

        if (fullPath != null && !fullPath.isEmpty()) {
            JButton viewButton = new JButton("Lihat Bukti Pembayaran");
            viewButton.addActionListener((ActionEvent e) -> {
                try {
                    File file = new File(fullPath);
                    System.out.println("Mencoba membuka file: " + file.getAbsolutePath());
                    if (file.exists()) {
                        Desktop.getDesktop().open(file);
                    } else {
                        JOptionPane.showMessageDialog(this,
                                "File tidak ditemukan di:\n" + file.getAbsolutePath(),
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this,
                            "Error membuka file: " + ex.getMessage() + "\nPath: " + fullPath,
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            });
            actionPanel.add(viewButton);
        }
        gbc.weightx = 0.7;
        card.add(detailsPanel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.3;
        card.add(actionPanel, gbc);

        return card;
    }

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
                    .addComponent(GambarBola, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(menuLayout.createSequentialGroup()
                        .addGroup(menuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(pKeluar, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(plihatPembayaran, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                            .addComponent(pKonfirmasi, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(pTambahLapangan, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(pDaftarLapangan, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(0, 1, Short.MAX_VALUE)))
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

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void bDaftarLapanganActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bDaftarLapanganActionPerformed
        TampilanDaftarLapangan tdl = new TampilanDaftarLapangan(); // Membuat instance TampilanDaftarLapangan
        tdl.setVisible(true); // Menampilkan frame TampilanDaftarLapangan
        this.dispose(); // Menutup frame saat ini untuk menghindari banyak frame terbuka
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

    private void bLihatPembayaranActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bLihatPembayaranActionPerformed
        TampilanLihatPembayaran tlb = new TampilanLihatPembayaran();
        tlb.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_bLihatPembayaranActionPerformed

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
            java.util.logging.Logger.getLogger(TampilanLihatPembayaran.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(TampilanLihatPembayaran.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(TampilanLihatPembayaran.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(TampilanLihatPembayaran.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new TampilanLihatPembayaran().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel GambarBola;
    private javax.swing.JButton bDaftarLapangan;
    private javax.swing.JButton bKeluar;
    private javax.swing.JButton bKonfirmasi;
    private javax.swing.JButton bLihatPembayaran;
    private javax.swing.JButton bTambahLapangan;
    private javax.swing.JPanel menu;
    private javax.swing.JPanel pDaftarLapangan;
    private javax.swing.JPanel pKeluar;
    private javax.swing.JPanel pKonfirmasi;
    private javax.swing.JPanel pTambahLapangan;
    private javax.swing.JPanel plihatPembayaran;
    // End of variables declaration//GEN-END:variables
}
