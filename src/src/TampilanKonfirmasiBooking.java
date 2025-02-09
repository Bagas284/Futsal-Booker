/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package src;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.table.DefaultTableModel;
import javax.swing.JOptionPane;
import javax.swing.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.io.File;
import java.io.IOException;
import java.awt.Desktop;
import java.nio.file.Paths;

public class TampilanKonfirmasiBooking extends javax.swing.JFrame {

  private final ArrayList<Booking> daftarBooking;
    private JPanel cardsPanel;

    public TampilanKonfirmasiBooking() {
        daftarBooking = new ArrayList<>();
        setTitle("Manajemen Lapangan");

        initComponents(); // Pastikan inisialisasi GUI dipanggil dulu
        setupMainPanel();
        loadDataFromDatabase();
        populateCards();
        pack();
        setLocationRelativeTo(null);
        setExtendedState(javax.swing.JFrame.MAXIMIZED_BOTH);
        // Tambahan agar benar-benar full screen
        setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
    }

    private void setupMainPanel() {
        if (main == null) {
            throw new IllegalStateException("Panel utama (main) belum diinisialisasi.");
        }
        main.removeAll();
        main.setLayout(new BorderLayout());

        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(Color.WHITE);
        JLabel titleLabel = new JLabel("Status Booking");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titlePanel.add(titleLabel);

        cardsPanel = new JPanel();
        cardsPanel.setLayout(new BoxLayout(cardsPanel, BoxLayout.Y_AXIS));
        cardsPanel.setBackground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(cardsPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        main.add(titlePanel, BorderLayout.NORTH);
        main.add(scrollPane, BorderLayout.CENTER);
        main.revalidate();
        main.repaint();
    }

    private void loadDataFromDatabase() {
        daftarBooking.clear();
        try (Connection conn = new Koneksi().getConnection(); PreparedStatement ps = conn.prepareStatement(
                "SELECT b.id_booking, b.jadwal, b.durasi, b.total_harga, b.status AS status_booking, b.alasan, "
                + "p.idPengguna, p.namaPengguna, p.email, p.password, p.role, "
                + "l.id_lapangan, l.nama_lapangan, l.status AS status_lapangan, l.hargaPerJam "
                + "FROM booking b "
                + "JOIN pengguna p ON b.id_pengguna = p.idPengguna "
                + "JOIN lapangan l ON b.id_lapangan = l.id_lapangan "
                + "ORDER BY b.id_booking DESC")) { // Urutkan hanya berdasarkan id_booking secara eksplisit

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                createAndAddBooking(rs);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading data: " + ex.getMessage());
        }
    }

    private void createAndAddBooking(ResultSet rs) throws SQLException {
        int idBooking = rs.getInt("id_booking");
        Date jadwal = rs.getTimestamp("jadwal");
        int durasi = rs.getInt("durasi");
        double totalHarga = rs.getDouble("total_harga");
        String status = rs.getString("status_booking");
        String alasan = rs.getString("alasan");

        Pengguna pengguna = new Pengguna(
                rs.getInt("idPengguna"),
                rs.getString("namaPengguna"),
                rs.getString("email"),
                rs.getString("password"),
                rs.getString("role")
        );

        Lapangan lapangan = new Lapangan(
                rs.getInt("id_lapangan"),
                rs.getString("nama_lapangan"),
                rs.getString("status_lapangan"),
                rs.getDouble("hargaPerJam")
        );

        daftarBooking.add(new Booking(idBooking, jadwal, durasi, totalHarga, status, alasan, pengguna, lapangan));
    }

    private void populateCards() {
        cardsPanel.removeAll();

        if (daftarBooking.isEmpty()) {
            JLabel emptyLabel = new JLabel("Tidak ada booking yang tersedia");
            emptyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            cardsPanel.add(emptyLabel);
        } else {
            for (Booking booking : daftarBooking) {
                JPanel card = createBookingCard(booking);
                cardsPanel.add(card);
                cardsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
            }
        }

        cardsPanel.revalidate();
        cardsPanel.repaint();
    }

    private JPanel createBookingCard(Booking booking) {
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout());
        card.setBackground(Color.WHITE);

        // Check status for card color
        if ("Bookingan dibatalkan, Silahkan Reschedule".equals(booking.getStatus())) {
            card.setBackground(new Color(255, 230, 230));
        }

        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        card.setPreferredSize(new Dimension(450, 300));

        // Header Panel
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.X_AXIS));
        headerPanel.setBackground(card.getBackground());

        JLabel bookingIdLabel = new JLabel("Booking #" + booking.getIdBooking());
        bookingIdLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));

        JLabel statusLabel = new JLabel(booking.getStatus());
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        statusLabel.setForeground(getStatusColor(booking.getStatus()));

        headerPanel.add(bookingIdLabel);
        headerPanel.add(Box.createHorizontalGlue());
        headerPanel.add(statusLabel);

        // Info Panel
        JPanel infoPanel = new JPanel(new GridLayout(6, 2, 10, 10));
        infoPanel.setBackground(card.getBackground());
        infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        addInfoField(infoPanel, "Pengguna:", booking.getPengguna().getNamaPengguna());
        addInfoField(infoPanel, "Lapangan:", booking.getLapangan().getNama_lapangan());
        addInfoField(infoPanel, "Jadwal:", booking.getJadwal().toString());
        addInfoField(infoPanel, "Durasi:", booking.getDurasi() + " Jam");
        addInfoField(infoPanel, "Total Harga:", String.format("Rp %.2f", booking.getTotalHarga()));

        if ("Bookingan dibatalkan, Silahkan Reschedule".equals(booking.getStatus())) {
            addInfoField(infoPanel, "Alasan Pembatalan:", booking.getAlasan());
        }

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(card.getBackground());

        if ("Selesai".equals(booking.getStatus())
                || "Bookingan dibatalkan, Silahkan Reschedule".equals(booking.getStatus())) {
            // No buttons for these statuses
        } else if ("Booking Berhasil".equals(booking.getStatus())) {
            JButton selesaiButton = new JButton("Selesai");
            selesaiButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            selesaiButton.setBackground(new Color(0, 102, 204));
            selesaiButton.setForeground(Color.WHITE);
            selesaiButton.setFocusPainted(false);
            selesaiButton.setPreferredSize(new Dimension(150, 30));
            selesaiButton.addActionListener(e -> {
                int result = JOptionPane.showConfirmDialog(
                        null,
                        "Apakah Anda yakin booking ini telah selesai?",
                        "Selesaikan Booking",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE
                );

                if (result == JOptionPane.YES_OPTION) {
                    booking.setStatus("Selesai");
                    boolean updateSuccess = updateBookingStatusInDatabase(booking.getIdBooking(), "Selesai", null);

                    if (updateSuccess) {
                        JOptionPane.showMessageDialog(
                                null,
                                "Status booking telah diubah menjadi 'Selesai'.",
                                "Sukses",
                                JOptionPane.INFORMATION_MESSAGE
                        );
                        refreshBookingCard(card, booking);
                    }
                }
            });
            buttonPanel.add(selesaiButton);
        } else if ("Telah Dikonfirmasi".equals(booking.getStatus())) {

            // Acc Booking button
            JButton accBookingButton = new JButton("Acc Booking");
            accBookingButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            accBookingButton.setBackground(new Color(0, 102, 204));
            accBookingButton.setForeground(Color.WHITE);
            accBookingButton.setFocusPainted(false);
            accBookingButton.setPreferredSize(new Dimension(150, 30));
            accBookingButton.addActionListener(e -> {
                int result = JOptionPane.showConfirmDialog(
                        null,
                        "Apakah Anda yakin booking ini berhasil?",
                        "Acc Booking",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE
                );
                if (result == JOptionPane.YES_OPTION) {
                    booking.setStatus("Booking Berhasil");
                    boolean updateSuccess = updateBookingStatusInDatabase(booking.getIdBooking(), "Booking Berhasil", null);
                    if (updateSuccess) {
                        JOptionPane.showMessageDialog(
                                null,
                                "Status booking telah diubah menjadi 'Booking Berhasil'.",
                                "Sukses",
                                JOptionPane.INFORMATION_MESSAGE
                        );
                        refreshBookingCard(card, booking);
                    }
                }
            });
            buttonPanel.add(accBookingButton);

            // View Payment button
            JButton viewPaymentButton = new JButton("Lihat Bukti Pembayaran");
            viewPaymentButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            viewPaymentButton.setBackground(new Color(0, 204, 0));
            viewPaymentButton.setForeground(Color.WHITE);
            viewPaymentButton.setFocusPainted(false);
            viewPaymentButton.setPreferredSize(new Dimension(150, 30));
            viewPaymentButton.addActionListener(e -> {
                String fullPath = getPaymentProofPath(booking.getIdBooking());
                if (fullPath != null) {
                    try {
                        File file = new File(fullPath);
                        if (file.exists()) {
                            Desktop.getDesktop().open(file);
                        } else {
                            JOptionPane.showMessageDialog(null,
                                    "File tidak ditemukan di:\n" + file.getAbsolutePath(),
                                    "Error",
                                    JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(null,
                                "Error membuka file: " + ex.getMessage() + "\nPath: " + fullPath,
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(null,
                            "Bukti pembayaran tidak ditemukan untuk booking ini",
                            "Warning",
                            JOptionPane.WARNING_MESSAGE);
                }
            });
            buttonPanel.add(viewPaymentButton);
        } else {
            // Default state buttons (Konfirmasi and Batalkan)
            JButton confirmButton = new JButton("Konfirmasi");
            confirmButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            confirmButton.setBackground(new Color(0, 128, 0));
            confirmButton.setForeground(Color.WHITE);
            confirmButton.setFocusPainted(false);
            confirmButton.setPreferredSize(new Dimension(150, 30));
            confirmButton.addActionListener(e -> {
                int result = JOptionPane.showConfirmDialog(
                        null,
                        "Apakah Anda yakin ingin mengkonfirmasi booking ini?",
                        "Konfirmasi Booking",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE
                );

                if (result == JOptionPane.YES_OPTION) {
                    booking.setStatus("Telah Dikonfirmasi");
                    boolean updateSuccess = updateBookingStatusInDatabase(booking.getIdBooking(), "Telah Dikonfirmasi", null);

                    if (updateSuccess) {
                        JOptionPane.showMessageDialog(
                                null,
                                "Booking berhasil dikonfirmasi",
                                "Sukses",
                                JOptionPane.INFORMATION_MESSAGE
                        );
                        refreshBookingCard(card, booking);
                    }
                }
            });

            JButton cancelButton = new JButton("Batalkan Bookingan");
            cancelButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            cancelButton.setBackground(new Color(255, 0, 0));
            cancelButton.setForeground(Color.WHITE);
            cancelButton.setFocusPainted(false);
            cancelButton.setPreferredSize(new Dimension(150, 30));
            cancelButton.addActionListener(e -> {
                int result = JOptionPane.showConfirmDialog(
                        null,
                        "Apakah Anda yakin ingin membatalkan booking ini?",
                        "Batalkan Booking",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE
                );
                if (result == JOptionPane.YES_OPTION) {
                    String alasanBatal = JOptionPane.showInputDialog(
                            null,
                            "Masukkan alasan pembatalan:",
                            "Alasan Pembatalan",
                            JOptionPane.QUESTION_MESSAGE
                    );

                    if (alasanBatal != null && !alasanBatal.trim().isEmpty()) {
                        String status = "Bookingan dibatalkan, Silahkan Reschedule";
                        booking.setStatus(status);
                        booking.setAlasan(alasanBatal);

                        boolean updateSuccess = updateBookingStatusInDatabase(
                                booking.getIdBooking(),
                                status,
                                alasanBatal
                        );

                        if (updateSuccess) {
                            JOptionPane.showMessageDialog(
                                    null,
                                    "Booking berhasil dibatalkan",
                                    "Sukses",
                                    JOptionPane.INFORMATION_MESSAGE
                            );
                            refreshBookingCard(card, booking);
                        } else {
                            JOptionPane.showMessageDialog(
                                    null,
                                    "Terjadi kesalahan saat memperbarui booking",
                                    "Kesalahan",
                                    JOptionPane.ERROR_MESSAGE
                            );
                        }
                    } else {
                        JOptionPane.showMessageDialog(
                                null,
                                "Alasan pembatalan harus diisi",
                                "Peringatan",
                                JOptionPane.WARNING_MESSAGE
                        );
                    }
                }
            });

            buttonPanel.add(cancelButton);
            buttonPanel.add(confirmButton);
        }

        card.add(headerPanel, BorderLayout.NORTH);
        card.add(infoPanel, BorderLayout.CENTER);
        card.add(buttonPanel, BorderLayout.SOUTH);

        return card;
    }

// Function to add info field
    private void addInfoField(JPanel panel, String label, String value) {
        JLabel fieldLabel = new JLabel(label);
        fieldLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        fieldLabel.setPreferredSize(new Dimension(100, 20)); // Set fixed width for labels

        JLabel fieldValue = new JLabel(value);
        fieldValue.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        // Enable text wrapping for long values
        fieldValue.setPreferredSize(new Dimension(300, 20));

        // Add tool tip for long text
        if (value.length() > 30) {
            fieldValue.setToolTipText(value);
        }

        panel.add(fieldLabel);
        panel.add(fieldValue);
    }

    private Color getStatusColor(String status) {
        return switch (status.toLowerCase()) {
            case "pending" ->
                new Color(255, 165, 0);  // Orange
            case "confirmed", "telah dikonfirmasi" ->
                new Color(0, 128, 0);  // Green
            case "cancelled" ->
                new Color(255, 0, 0);  // Red
            default ->
                new Color(128, 128, 128);  // Gray
        };
    }

// Function to refresh the booking card
    private void refreshBookingCard(JPanel card, Booking booking) {
        // Clear existing components and recreate the card
        card.removeAll();
        JPanel newCard = createBookingCard(booking);
        card.add(newCard, BorderLayout.CENTER);
        card.revalidate();
        card.repaint();
    }

// Function to update booking status in the database
    private boolean updateBookingStatusInDatabase(int bookingId, String newStatus, String alasan) {
        // Query untuk memperbarui status dan alasan
        String query = "UPDATE booking SET status = ?, alasan = ? WHERE id_booking = ?";

        // Instance dari kelas Koneksi
        Koneksi koneksi = new Koneksi();

        try (Connection connection = koneksi.getConnection(); PreparedStatement statement = connection.prepareStatement(query)) {

            // Set nilai parameter
            statement.setString(1, newStatus); // Set kolom status
            statement.setString(2, alasan);   // Set kolom alasan
            statement.setInt(3, bookingId);   // Set ID booking

            // Eksekusi query
            int rowsAffected = statement.executeUpdate();

            // Return true jika update berhasil
            return rowsAffected > 0;

        } catch (SQLException e) {
            // Tampilkan pesan error jika terjadi exception
            JOptionPane.showMessageDialog(
                    null,
                    "Error updating booking status and reason: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
            // Return false jika terjadi error
            return false;
        }
    }

    private String getPaymentProofPath(int idBooking) {
        try (Connection conn = new Koneksi().getConnection()) {
            String sql = """
            SELECT p.bukti_bayar 
            FROM pembayaran p 
            WHERE p.id_booking = ?
        """;

            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, idBooking);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String buktiFileName = rs.getString("bukti_bayar");
                if (buktiFileName != null) {
                    String currentPath = System.getProperty("user.dir");
                    return Paths.get(currentPath, "src", buktiFileName).toString();
                }
            }
            return null;
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error mengambil data pembayaran: " + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
            return null;
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
        bKonfirmasi2 = new javax.swing.JButton();
        plihatPembayaran = new javax.swing.JPanel();
        bLihatPembayaran = new javax.swing.JButton();
        main = new javax.swing.JPanel();

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
            .addComponent(bTambahLapangan, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 189, Short.MAX_VALUE)
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
                    .addComponent(plihatPembayaran, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(pDaftarLapangan, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pTambahLapangan, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(GambarBola, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pKeluar, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pKonfirmasi, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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

        javax.swing.GroupLayout mainLayout = new javax.swing.GroupLayout(main);
        main.setLayout(mainLayout);
        mainLayout.setHorizontalGroup(
            mainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 626, Short.MAX_VALUE)
        );
        mainLayout.setVerticalGroup(
            mainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 524, Short.MAX_VALUE)
        );

        getContentPane().add(main, java.awt.BorderLayout.CENTER);

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
            java.util.logging.Logger.getLogger(TampilanKonfirmasiBooking.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(TampilanKonfirmasiBooking.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(TampilanKonfirmasiBooking.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(TampilanKonfirmasiBooking.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new TampilanKonfirmasiBooking().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel GambarBola;
    private javax.swing.JButton bDaftarLapangan;
    private javax.swing.JButton bKonfirmasi;
    private javax.swing.JButton bKonfirmasi2;
    private javax.swing.JButton bLihatPembayaran;
    private javax.swing.JButton bTambahLapangan;
    private javax.swing.JPanel main;
    private javax.swing.JPanel menu;
    private javax.swing.JPanel pDaftarLapangan;
    private javax.swing.JPanel pKeluar;
    private javax.swing.JPanel pKonfirmasi;
    private javax.swing.JPanel pTambahLapangan;
    private javax.swing.JPanel plihatPembayaran;
    // End of variables declaration//GEN-END:variables
}
