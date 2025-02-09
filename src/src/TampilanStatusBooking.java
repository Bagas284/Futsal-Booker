/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package src;

import javax.swing.JOptionPane;
import javax.swing.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

/**
 *
 * @author Bagas Aldianata
 */
public class TampilanStatusBooking extends javax.swing.JFrame {

private int userId;
private String username;

private final ArrayList<Booking> daftarBooking;
private JPanel cardsPanel;

public void setUserInfo(int userId, String username) {
    this.userId = userId;
    this.username = username;
    if (bNama != null) {
        bNama.setText(username);
    }
    // Reload data for the logged-in user
    loadDataFromDatabase();
    populateCards(null); // Pass null initially
}

public TampilanStatusBooking() {
    daftarBooking = new ArrayList<>();
    setTitle("Manajemen Lapangan");
    initComponents();
    setupMainPanel();
    loadDataFromDatabase();
    populateCards(null); // Pass null initially
    setLocationRelativeTo(null);
    setExtendedState(javax.swing.JFrame.MAXIMIZED_BOTH);
    setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
}

private void setupMainPanel() {
    // Clean and setup main panel
    main.removeAll();
    main.setLayout(new BorderLayout());

    // Create title panel
    JPanel titlePanel = new JPanel();
    titlePanel.setBackground(Color.WHITE);
    JLabel titleLabel = new JLabel("Status Booking");
    titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
    titlePanel.add(titleLabel);

    // Create cards panel
    cardsPanel = new JPanel();
    cardsPanel.setLayout(new BoxLayout(cardsPanel, BoxLayout.Y_AXIS));
    cardsPanel.setBackground(Color.WHITE);

    // Add scroll pane
    JScrollPane scrollPane = new JScrollPane(cardsPanel);
    scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
    scrollPane.setBorder(null);
    scrollPane.getVerticalScrollBar().setUnitIncrement(16);

    // Add components to main panel
    main.add(titlePanel, BorderLayout.NORTH);
    main.add(scrollPane, BorderLayout.CENTER);
    main.revalidate();
    main.repaint();
}

private void loadDataFromDatabase() {
    daftarBooking.clear();
    try (Connection conn = new Koneksi().getConnection(); 
         PreparedStatement ps = conn.prepareStatement(
                "SELECT b.id_booking, b.jadwal, b.durasi, b.total_harga, b.status AS status_booking, b.alasan, "
                + "p.idPengguna, p.namaPengguna, p.email, p.password, p.role, "
                + "l.id_lapangan, l.nama_lapangan, l.status AS status_lapangan, l.hargaPerJam "
                + "FROM booking b "
                + "JOIN pengguna p ON b.id_pengguna = p.idPengguna "
                + "JOIN lapangan l ON b.id_lapangan = l.id_lapangan "
                + "WHERE p.namaPengguna = ? "
                + "ORDER BY b.id_booking DESC")) {
        ps.setString(1, username); // Set the username parameter
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

private void populateCards(Integer updatedBookingId) {
    cardsPanel.removeAll();

    if (daftarBooking.isEmpty()) {
        JLabel emptyLabel = new JLabel("Tidak ada booking yang tersedia");
        emptyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        cardsPanel.add(emptyLabel);
    } else {
        for (Booking booking : daftarBooking) {
            JPanel card = createBookingCard(booking, updatedBookingId);
            cardsPanel.add(card);
            cardsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        }
    }

    cardsPanel.revalidate();
    cardsPanel.repaint();
}

private JPanel createBookingCard(Booking booking, Integer updatedBookingId) {
    JPanel card = new JPanel();
    card.setLayout(new BorderLayout(10, 10));
    card.setBackground(Color.WHITE);
    card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
    ));
    card.setMaximumSize(new Dimension(800, 250));
    card.setPreferredSize(new Dimension(800, 250));

    // Header Panel
    JPanel headerPanel = new JPanel(new BorderLayout(10, 0));
    headerPanel.setBackground(Color.WHITE);

    JLabel bookingIdLabel = new JLabel("Booking #" + booking.getIdBooking());
    bookingIdLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));

    JLabel statusLabel = new JLabel(booking.getStatus());
    statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
    statusLabel.setForeground(getStatusColor(booking.getStatus()));

    headerPanel.add(bookingIdLabel, BorderLayout.WEST);
    headerPanel.add(statusLabel, BorderLayout.EAST);

    // Content Panel
    JPanel contentPanel = new JPanel(new GridBagLayout());
    contentPanel.setBackground(Color.WHITE);
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.insets = new Insets(5, 5, 5, 5);

    // Add basic info fields
    addInfoField(contentPanel, gbc, 0, "Lapangan:", booking.getLapangan().getNama_lapangan());
    addInfoField(contentPanel, gbc, 1, "Jadwal:", booking.getJadwal().toString());
    addInfoField(contentPanel, gbc, 2, "Durasi:", booking.getDurasi() + " Jam");
    addInfoField(contentPanel, gbc, 3, "Total Harga:", String.format("Rp %.2f", booking.getTotalHarga()));

    // Check for cancellation status and add reason if applicable
    if ("Bookingan dibatalkan, Silahkan Reschedule".equals(booking.getStatus())) {
        addInfoField(contentPanel, gbc, 4, "Alasan Pembatalan:", booking.getAlasan());
        gbc.gridy = 5; // Update the grid position for the next button
    }

    // Button Panel (conditionally displayed)
    if (booking.getStatus().equalsIgnoreCase("Telah Dikonfirmasi") 
        && (updatedBookingId == null || !updatedBookingId.equals(booking.getIdBooking()))) {
        JButton bayarButton = new JButton("Bayar DP");
        bayarButton.setBackground(new Color(0, 123, 255));
        bayarButton.setForeground(Color.WHITE);
        bayarButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        bayarButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        bayarButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        bayarButton.addActionListener(e -> showPaymentForm(booking));

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2; // Spanning across columns
        gbc.anchor = GridBagConstraints.EAST;
        contentPanel.add(bayarButton, gbc);
    } else if (booking.getStatus().equalsIgnoreCase("Selesai") 
               || booking.getStatus().equalsIgnoreCase("Booking Berhasil")) {
        JButton cetakInvoiceButton = new JButton("Lihat Invoice");
        cetakInvoiceButton.setBackground(new Color(0, 123, 255));
        cetakInvoiceButton.setForeground(Color.WHITE);
        cetakInvoiceButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        cetakInvoiceButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        cetakInvoiceButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        cetakInvoiceButton.addActionListener(e -> generateInvoice(booking));

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2; // Spanning across columns
        gbc.anchor = GridBagConstraints.EAST;
        contentPanel.add(cetakInvoiceButton, gbc);
    }

    // Add Panels to Card
    card.add(headerPanel, BorderLayout.NORTH);
    card.add(contentPanel, BorderLayout.CENTER);

    return card;
}

// Function to add info field
private void addInfoField(JPanel panel, GridBagConstraints gbc, int row, String label, String value) {
    gbc.gridx = 0;
    gbc.gridy = row;
    gbc.weightx = 1.0;
    JLabel fieldLabel = new JLabel(label);
    fieldLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    panel.add(fieldLabel, gbc);

    gbc.gridx = 1;
    JLabel fieldValue = new JLabel(value);
    fieldValue.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    panel.add(fieldValue, gbc);
}

// Method to generate and download the invoice as a PDF
private void generateInvoice(Booking booking) {
    // Create a new JFrame for the invoice
    JFrame invoiceFrame = new JFrame("Invoice for Booking #" + booking.getIdBooking());
    invoiceFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    invoiceFrame.setSize(450, 350);
    invoiceFrame.setLayout(new BorderLayout(10, 10));

    // Create a panel to hold the invoice details
    JPanel invoicePanel = new JPanel();
    invoicePanel.setLayout(new GridBagLayout());
    invoicePanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15)); // Add padding
    invoicePanel.setBackground(Color.WHITE);

    // Add invoice details to the panel
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.insets = new Insets(5, 5, 5, 5);

    addInvoiceDetail(invoicePanel, gbc, 0, "Booking ID:", String.valueOf(booking.getIdBooking()));
    addInvoiceDetail(invoicePanel, gbc, 1, "Nama Pengguna:", booking.getPengguna().getNamaPengguna());
    addInvoiceDetail(invoicePanel, gbc, 2, "Lapangan:", booking.getLapangan().getNama_lapangan());
    addInvoiceDetail(invoicePanel, gbc, 3, "Jadwal:", booking.getJadwal().toString());
    addInvoiceDetail(invoicePanel, gbc, 4, "Durasi:", booking.getDurasi() + " Jam");

    double totalHarga = booking.getTotalHarga();
    double sisaBayar = totalHarga * 0.5;
    addInvoiceDetail(invoicePanel, gbc, 5, "Sisa Bayar:", String.format("Rp %.2f", sisaBayar));

    // Add a header label
    JLabel headerLabel = new JLabel("Invoice Details (Harap di screenshoot)", SwingConstants.CENTER);
    headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
    headerLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

    // Add close button
    JButton closeButton = new JButton("Close");
    closeButton.setBackground(new Color(220, 53, 69));
    closeButton.setForeground(Color.WHITE);
    closeButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
    closeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
    closeButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
    closeButton.addActionListener(e -> invoiceFrame.dispose());

    // Add components to the frame
    invoiceFrame.add(headerLabel, BorderLayout.NORTH);
    invoiceFrame.add(invoicePanel, BorderLayout.CENTER);

    JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
    footerPanel.setBackground(Color.WHITE);
    footerPanel.add(closeButton);
    invoiceFrame.add(footerPanel, BorderLayout.SOUTH);

    // Set frame visibility
    invoiceFrame.setVisible(true);
}

private void addInvoiceDetail(JPanel panel, GridBagConstraints gbc, int row, String label, String value) {
    JLabel fieldLabel = new JLabel(label);
    fieldLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));

    JLabel fieldValue = new JLabel(value);
    fieldValue.setFont(new Font("Segoe UI", Font.BOLD, 14));

    gbc.gridx = 0;
    gbc.gridy = row;
    gbc.anchor = GridBagConstraints.WEST;
    panel.add(fieldLabel, gbc);

    gbc.gridx = 1;
    gbc.anchor = GridBagConstraints.EAST;
    panel.add(fieldValue, gbc);
}

private void showPaymentForm(Booking booking) {
    JDialog paymentDialog = new JDialog(this, "Form Pembayaran DP", true);
    paymentDialog.setLayout(new BorderLayout());
    paymentDialog.setSize(400, 300);
    paymentDialog.setLocationRelativeTo(this);

    JPanel formPanel = new JPanel(new GridBagLayout());
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.gridwidth = 1;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.insets = new Insets(5, 5, 5, 5);

    // Form components
    JLabel lblTransferBank = new JLabel("Transfer Bank (BCA): 048-223-1771");
    JLabel lblEWallet = new JLabel("E-Wallet (DANA): 081935186124");

    JLabel lblJumlahDP = new JLabel("Jumlah DP (50%):");
    double jumlahDPValue = booking.getTotalHarga() * 0.5;
    JTextField txtJumlahDP = new JTextField(String.format("%.2f", jumlahDPValue));
    txtJumlahDP.setEditable(false);

    JLabel lblMetodePembayaran = new JLabel("Metode Pembayaran:");
    String[] metodePembayaran = {"Transfer Bank", "E-Wallet"};
    JComboBox<String> cmbMetodePembayaran = new JComboBox<>(metodePembayaran);

    JLabel lblBuktiPembayaran = new JLabel("Bukti Pembayaran:");
    JTextField txtBuktiPembayaran = new JTextField(20);
    JButton btnPilihFile = new JButton("Pilih File");

    // Add components to form
    gbc.gridx = 0;
    gbc.gridy = 0;
    formPanel.add(lblTransferBank, gbc);  // Label Transfer Bank
    gbc.gridx = 0;
    gbc.gridy = 1;
    formPanel.add(lblEWallet, gbc); // Label E-Wallet

    gbc.gridx = 0;
    gbc.gridy = 2;
    formPanel.add(lblJumlahDP, gbc);
    gbc.gridx = 1;
    formPanel.add(txtJumlahDP, gbc);

    gbc.gridx = 0;
    gbc.gridy = 3;
    formPanel.add(lblMetodePembayaran, gbc);
    gbc.gridx = 1;
    formPanel.add(cmbMetodePembayaran, gbc);

    gbc.gridx = 0;
    gbc.gridy = 4;
    formPanel.add(lblBuktiPembayaran, gbc);
    gbc.gridx = 1;
    formPanel.add(txtBuktiPembayaran, gbc);
    gbc.gridx = 2;
    formPanel.add(btnPilihFile, gbc);

    // Button panel
    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    JButton btnSubmit = new JButton("Submit");
    JButton btnCancel = new JButton("Cancel");

    buttonPanel.add(btnSubmit);
    buttonPanel.add(btnCancel);

    // File chooser action
    btnPilihFile.addActionListener(e -> {
        JFileChooser fileChooser = new JFileChooser();
        if (fileChooser.showOpenDialog(paymentDialog) == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();

            // Tentukan lokasi folder upload
            File uploadDir = new File("src/upload");
            if (!uploadDir.exists()) {
                uploadDir.mkdirs(); // Create folder if it doesn't exist
            }

            // Copy file to upload folder
            File destinationFile = new File(uploadDir, selectedFile.getName());
            try {
                Files.copy(selectedFile.toPath(), destinationFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                txtBuktiPembayaran.setText(destinationFile.getAbsolutePath());
                JOptionPane.showMessageDialog(paymentDialog, "File berhasil diunggah!");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(paymentDialog, "Gagal menyimpan file: " + ex.getMessage());
            }
        }
    });

    // Submit action
    btnSubmit.addActionListener(e -> {
        if (txtBuktiPembayaran.getText().isEmpty()) {
            JOptionPane.showMessageDialog(paymentDialog, "Mohon upload bukti pembayaran!");
            return;
        }

        // Path relatif untuk disimpan ke database
        String relativePath = "upload/" + new File(txtBuktiPembayaran.getText()).getName();

        // Create a new Pembayaran object
        Pembayaran pembayaran = new Pembayaran(0, booking, jumlahDPValue, relativePath);

        // Save payment to database
        savePembayaran(pembayaran, booking.getIdBooking());
        paymentDialog.dispose();
    });

    btnCancel.addActionListener(e -> paymentDialog.dispose());

    paymentDialog.add(formPanel, BorderLayout.CENTER);
    paymentDialog.add(buttonPanel, BorderLayout.SOUTH);
    paymentDialog.setVisible(true);
}

private Color getStatusColor(String status) {
        return switch (status.toLowerCase()) {
            case "menunggu konfirmasi" ->
                new Color(255, 165, 0);  // Orange
            case "confirmed" ->
                new Color(0, 128, 0);  // Green
            case "cancelled" ->
                new Color(255, 0, 0);  // Red
            default ->
                new Color(128, 128, 128);  // Gray
        };
    }

private void savePembayaran(Pembayaran pembayaran, int bookingId) {
    try (Connection conn = new Koneksi().getConnection()) {
        // Insert into Pembayaran table
        String sqlPembayaran = "INSERT INTO pembayaran (id_booking, jumlah_bayar, bukti_bayar, tanggal_bayar) "
                + "VALUES (?, ?, ?, NOW())";

        PreparedStatement psPembayaran = conn.prepareStatement(sqlPembayaran);
        psPembayaran.setInt(1, pembayaran.getBooking().getIdBooking());
        psPembayaran.setDouble(2, pembayaran.getJumlahBayar());
        psPembayaran.setString(3, pembayaran.getBuktiBayar());

        psPembayaran.executeUpdate();

        JOptionPane.showMessageDialog(this, "Pembayaran berhasil disimpan!");

        // Reload data from database and refresh cards
        loadDataFromDatabase();
        populateCards(bookingId); // Pass updated booking ID
    } catch (SQLException ex) {
        JOptionPane.showMessageDialog(this, "Error saving payment: " + ex.getMessage());
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
        pStatusBooking = new javax.swing.JPanel();
        bStatusBooking = new javax.swing.JButton();
        pKeluar = new javax.swing.JPanel();
        bKonfirmasi2 = new javax.swing.JButton();
        pNama = new javax.swing.JPanel();
        bNama = new javax.swing.JButton();
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

        pStatusBooking.setBackground(new java.awt.Color(204, 0, 51));

        bStatusBooking.setBackground(new java.awt.Color(255, 0, 51));
        bStatusBooking.setFont(new java.awt.Font("Segoe UI Historic", 1, 18)); // NOI18N
        bStatusBooking.setForeground(new java.awt.Color(255, 255, 255));
        bStatusBooking.setText("Status Booking");
        bStatusBooking.setContentAreaFilled(false);
        bStatusBooking.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bStatusBookingActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pStatusBookingLayout = new javax.swing.GroupLayout(pStatusBooking);
        pStatusBooking.setLayout(pStatusBookingLayout);
        pStatusBookingLayout.setHorizontalGroup(
            pStatusBookingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(bStatusBooking, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 188, Short.MAX_VALUE)
        );
        pStatusBookingLayout.setVerticalGroup(
            pStatusBookingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pStatusBookingLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(bStatusBooking)
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

        pNama.setBackground(new java.awt.Color(204, 0, 51));

        bNama.setBackground(new java.awt.Color(255, 0, 51));
        bNama.setFont(new java.awt.Font("Segoe UI Historic", 1, 18)); // NOI18N
        bNama.setForeground(new java.awt.Color(255, 255, 255));
        bNama.setText("Nama");
        bNama.setContentAreaFilled(false);
        bNama.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bNamaActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pNamaLayout = new javax.swing.GroupLayout(pNama);
        pNama.setLayout(pNamaLayout);
        pNamaLayout.setHorizontalGroup(
            pNamaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(bNama, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        pNamaLayout.setVerticalGroup(
            pNamaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pNamaLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(bNama)
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
                    .addComponent(pDaftarLapangan, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pStatusBooking, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pKeluar, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pNama, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        menuLayout.setVerticalGroup(
            menuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(menuLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pNama, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(GambarBola, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(43, 43, 43)
                .addComponent(pDaftarLapangan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(pStatusBooking, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 156, Short.MAX_VALUE)
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
        TampilanBooking booking = new TampilanBooking();
        booking.setUserInfo(this.userId, this.username); // Kirim informasi pengguna
        booking.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_bDaftarLapanganActionPerformed

    private void bStatusBookingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bStatusBookingActionPerformed
        TampilanStatusBooking statusBooking = new TampilanStatusBooking();
        statusBooking.setUserInfo(this.userId, this.username); // Mengirimkan informasi pengguna
        statusBooking.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_bStatusBookingActionPerformed

    private void bKonfirmasi2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bKonfirmasi2ActionPerformed
        Login login = new Login();
        login.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_bKonfirmasi2ActionPerformed

    private void bNamaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bNamaActionPerformed
        if (username != null && !username.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Logged in as: " + username);
        } else {
            JOptionPane.showMessageDialog(this, "User information not available!");
        }
    }//GEN-LAST:event_bNamaActionPerformed

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
            java.util.logging.Logger.getLogger(TampilanStatusBooking.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(TampilanStatusBooking.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(TampilanStatusBooking.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(TampilanStatusBooking.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new TampilanStatusBooking().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel GambarBola;
    private javax.swing.JButton bDaftarLapangan;
    private javax.swing.JButton bKonfirmasi2;
    private javax.swing.JButton bNama;
    private javax.swing.JButton bStatusBooking;
    private javax.swing.JPanel main;
    private javax.swing.JPanel menu;
    private javax.swing.JPanel pDaftarLapangan;
    private javax.swing.JPanel pKeluar;
    private javax.swing.JPanel pNama;
    private javax.swing.JPanel pStatusBooking;
    // End of variables declaration//GEN-END:variables
}
