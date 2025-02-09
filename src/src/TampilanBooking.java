/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package src;

import javax.swing.JPanel;
import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
import java.text.SimpleDateFormat;
import java.util.Arrays;

public class TampilanBooking extends javax.swing.JFrame {

    private int userId;
    private String username;

    public void setUserInfo(int userId, String username) {
        this.userId = userId;
        this.username = username;
        if (bNama != null) {
            bNama.setText(username);
        }
    }

    public TampilanBooking() {
        setTitle("Manajemen Lapangan");
        setSize(800, 600);

        initComponents();

        setupCards();

        pack();
        setLocationRelativeTo(null);
        setExtendedState(javax.swing.JFrame.MAXIMIZED_BOTH);
        // Tambahan agar benar-benar full screen
        setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
    }

    private ArrayList<Lapangan> loadDataFromDatabase() {
        ArrayList<Lapangan> daftarLapangan = new ArrayList<>();

        try (Connection conn = new Koneksi().getConnection(); PreparedStatement ps = conn.prepareStatement("SELECT * FROM lapangan"); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int idLapangan = rs.getInt("id_lapangan"); // Ambil idLapangan dari database
                String nama = rs.getString("nama_lapangan");
                String tipe = rs.getString("tipe_lapangan");
                String status = rs.getString("status");
                double hargaPerJam = rs.getDouble("hargaPerJam"); // Ambil hargaPerJam dari database

                // Tentukan tipe lapangan berdasarkan nilai tipe_lapangan
                Lapangan lapangan = tipe.equalsIgnoreCase("Rumput Sintetis (Indoor)")
                        ? new Indoor(idLapangan, nama, status, hargaPerJam)
                        : new Outdoor(idLapangan, nama, status, hargaPerJam);

                daftarLapangan.add(lapangan); // Tambahkan ke daftar
            }
        } catch (SQLException ex) {
            System.err.println("Error loading data: " + ex.getMessage());
        }

        return daftarLapangan;
    }

    private void setupCards() {
        // Panel utama untuk menampung semua komponen
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        // Tambahkan label "Daftar Lapangan" di atas
        JLabel titleLabel = new JLabel("Daftar Lapangan", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 50));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Load data dari database
        ArrayList<Lapangan> daftarLapangan = loadDataFromDatabase();

        // Panel untuk menampung semua cards
        JPanel cardContainer = new JPanel();
        cardContainer.setLayout(new java.awt.GridLayout(0, 2, 10, 10));
        // Ukuran card yang lebih kecil
        int cardWidth = 300;
        int cardHeight = 200;
        // Format untuk menghilangkan .0
        DecimalFormat decimalFormat = new DecimalFormat("#,###");
        // Iterasi daftarLapangan untuk membuat card
        for (Lapangan lapangan : daftarLapangan) {
            JPanel card = new JPanel();
            card.setPreferredSize(new java.awt.Dimension(cardWidth, cardHeight));
            card.setBackground(new java.awt.Color(255, 255, 255));
            card.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(50, 50, 50), 2));

            // Ganti GridLayout dengan BoxLayout untuk kontrol yang lebih baik
            card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

            // Panel untuk konten
            JPanel contentPanel = new JPanel();
            contentPanel.setLayout(new GridLayout(5, 1, 5, 5));
            contentPanel.setBackground(Color.WHITE);

            JLabel nameLabel = new JLabel(lapangan.getNama_lapangan());
            nameLabel.setFont(new Font("Arial", Font.BOLD, 20));
            nameLabel.setHorizontalAlignment(SwingConstants.CENTER);
            contentPanel.add(nameLabel);

            JLabel typeLabel = new JLabel();
            if (lapangan instanceof Indoor) {
                Indoor indoor = (Indoor) lapangan;
                typeLabel.setText("Tipe : " + indoor.getTipe_lapangan());
            } else if (lapangan instanceof Outdoor) {
                Outdoor outdoor = (Outdoor) lapangan;
                typeLabel.setText("Tipe lapangan: " + outdoor.getTipe_lapangan());
            } else {
                typeLabel.setText("Tipe : Tidak Diketahui");
            }
            typeLabel.setFont(new Font("Arial", Font.PLAIN, 18));
            contentPanel.add(typeLabel);

            JLabel statusLabel = new JLabel("Status: " + lapangan.getStatus());
            statusLabel.setFont(new Font("Arial", Font.PLAIN, 18));
            contentPanel.add(statusLabel);

            String formattedPrice = "Rp " + decimalFormat.format(lapangan.getHargaPerJam());
            JLabel priceLabel = new JLabel("Harga per Jam: " + formattedPrice);
            priceLabel.setFont(new Font("Arial", Font.PLAIN, 18));
            contentPanel.add(priceLabel);
            
            JLabel fasilitas = new JLabel();
            if (lapangan instanceof Indoor) {
                Indoor indoor = (Indoor) lapangan;
                fasilitas.setText("Fasilitas : " + indoor.getFasilitas());
            } else{
                fasilitas.setText("Fasilitas : - ");
            }
            fasilitas.setFont(new Font("Arial", Font.PLAIN, 18));
            contentPanel.add(fasilitas);
            
            card.add(contentPanel);

            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            buttonPanel.setBackground(Color.WHITE);

            JButton bookingButton = new JButton("Booking");
            bookingButton.setPreferredSize(new Dimension(150, 40));
            bookingButton.setBackground(new java.awt.Color(255, 0, 51));
            bookingButton.setForeground(new java.awt.Color(255, 255, 255));

            bookingButton.setPreferredSize(new java.awt.Dimension(200, 50));

            if ("Tidak Tersedia".equals(lapangan.getStatus())) {
                bookingButton.setEnabled(false);
                bookingButton.setBackground(new java.awt.Color(169, 169, 169));
            } else {
                bookingButton.addActionListener(e -> {
                    showBookingForm(lapangan);
                });
            }

            buttonPanel.add(bookingButton);
            card.add(buttonPanel);

            // Tambahkan card ke container
            cardContainer.add(card);
        }

        // Wrap cardContainer dengan JScrollPane
        JScrollPane scrollPane = new JScrollPane(cardContainer);
        scrollPane.setPreferredSize(new java.awt.Dimension(640, 400));
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        // Tambahkan scrollPane ke mainPanel bagian CENTER
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Set layout manager untuk main
        main.setLayout(new BorderLayout());
        main.add(mainPanel, BorderLayout.CENTER);

        // Refresh tampilan
        main.revalidate();
        main.repaint();
    }

    // Menampilkan form booking
    private void showBookingForm(Lapangan lapangan) {
        JPanel bookingPanel = new JPanel();
        bookingPanel.setLayout(new GridLayout(0, 2, 5, 5));

        int panelWidth = 400;  // Lebar panel
        int panelHeight = 300; // Tinggi panel
        bookingPanel.setPreferredSize(new Dimension(panelWidth, panelHeight));

        // Display field information
        bookingPanel.add(new JLabel("Nama Lapangan: "));
        bookingPanel.add(new JLabel(lapangan.getNama_lapangan()));
        bookingPanel.add(new JLabel("Tipe Lapangan: "));
        bookingPanel.add(new JLabel(lapangan instanceof Indoor ? "Rumput Sintetis" : "Rumput Non Sintetis"));
        bookingPanel.add(new JLabel("Status: "));
        bookingPanel.add(new JLabel(lapangan.getStatus()));
        bookingPanel.add(new JLabel("Harga per Jam: "));
        DecimalFormat decimalFormat = new DecimalFormat("#,###");
        String formattedPrice = "Rp " + decimalFormat.format(lapangan.getHargaPerJam());
        bookingPanel.add(new JLabel(formattedPrice));

        // Combo boxes for date selection
        bookingPanel.add(new JLabel("Tahun: "));
        Integer[] years = {2025, 2026, 2027};
        JComboBox<Integer> yearComboBox = new JComboBox<>(years);
        bookingPanel.add(yearComboBox);

        bookingPanel.add(new JLabel("Bulan: "));
        Integer[] months = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12};
        JComboBox<Integer> monthComboBox = new JComboBox<>(months);
        bookingPanel.add(monthComboBox);

        bookingPanel.add(new JLabel("Tanggal: "));
        JComboBox<Integer> dayComboBox = new JComboBox<>();
        bookingPanel.add(dayComboBox);

        // Combo box for time selection with custom renderer
        bookingPanel.add(new JLabel("Jam: "));
        DefaultComboBoxModel<TimeSlot> timeModel = new DefaultComboBoxModel<>();
        JComboBox<TimeSlot> hourComboBox = new JComboBox<>(timeModel);

        // Custom renderer for available/unavailable time slots
        hourComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                    int index, boolean isSelected, boolean cellHasFocus) {
                Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof TimeSlot) {
                    TimeSlot timeSlot = (TimeSlot) value;
                    setText(timeSlot.getDisplayTime());
                    setEnabled(timeSlot.isAvailable());
                    if (!timeSlot.isAvailable()) {
                        setBackground(Color.LIGHT_GRAY);
                        setForeground(Color.GRAY);
                    } else {
                        setBackground(Color.WHITE);
                        setForeground(Color.BLACK);
                    }
                }
                return c;
            }
        });
        bookingPanel.add(hourComboBox);

        // Spinner for duration
        bookingPanel.add(new JLabel("Durasi (jam): "));
        JSpinner durasiSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 12, 1));
        bookingPanel.add(durasiSpinner);

        // Update combo boxes when date changes
        ActionListener dateChangeListener = e -> {
            if (yearComboBox.getSelectedItem() != null
                    && monthComboBox.getSelectedItem() != null
                    && dayComboBox.getSelectedItem() != null) {

                updateTimeSlots(lapangan, yearComboBox, monthComboBox, dayComboBox, timeModel);
            }
        };

        // Add listeners
        yearComboBox.addActionListener(dateChangeListener);
        monthComboBox.addActionListener(dateChangeListener);
        dayComboBox.addActionListener(dateChangeListener);

        // Initial update of days
        updateDayComboBox(yearComboBox, monthComboBox, dayComboBox);

        // Submit button
        JButton submitButton = new JButton("Booking");
        submitButton.addActionListener(e -> {
            TimeSlot selectedTime = (TimeSlot) hourComboBox.getSelectedItem();
            if (selectedTime == null || !selectedTime.isAvailable()) {
                JOptionPane.showMessageDialog(null, "Silakan pilih jam yang tersedia",
                        "Peringatan", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int durasi = (int) durasiSpinner.getValue();
            Date jadwal = selectedTime.getDate();

            // Directly save the booking since we're not checking for conflicts
            double totalHarga = lapangan.getHargaPerJam() * durasi;
            saveBooking(lapangan, jadwal, durasi, totalHarga);
            JOptionPane.showMessageDialog(null, "Booking berhasil! Total harga: Rp "
                    + decimalFormat.format(totalHarga));
        });

        bookingPanel.add(new JLabel());
        bookingPanel.add(submitButton);

        JOptionPane.showConfirmDialog(this, bookingPanel, "Form Booking",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
    }

// Class untuk menyimpan informasi time slot
    private static class TimeSlot {

        private final Date date;
        private final boolean available;
        private final String displayTime;

        public TimeSlot(Date date, boolean available) {
            this.date = date;
            this.available = available;
            SimpleDateFormat sdf = new SimpleDateFormat("HH:00");
            this.displayTime = sdf.format(date);
        }

        public Date getDate() {
            return date;
        }

        public boolean isAvailable() {
            return available;
        }

        public String getDisplayTime() {
            return displayTime;
        }
    }

// Method for checking availability of a date with improved logic
    private boolean isDateAvailable(Lapangan lapangan, Date jadwal, int durasi) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(jadwal);
        calendar.add(Calendar.HOUR_OF_DAY, durasi);
        Date endJadwal = calendar.getTime();

        try (Connection conn = new Koneksi().getConnection(); PreparedStatement ps = conn.prepareStatement(
                "SELECT COUNT(*) FROM booking WHERE id_lapangan = ? AND status = 'Booking Berhasil' AND "
                + "((jadwal BETWEEN ? AND ?) OR (? BETWEEN jadwal AND DATE_ADD(jadwal, INTERVAL durasi HOUR)) OR "
                + "(DATE_ADD(jadwal, INTERVAL durasi HOUR) > ? AND jadwal < ?))")) {

            ps.setInt(1, lapangan.getIdLapangan());
            ps.setTimestamp(2, new Timestamp(jadwal.getTime()));
            ps.setTimestamp(3, new Timestamp(endJadwal.getTime()));
            ps.setTimestamp(4, new Timestamp(jadwal.getTime()));
            ps.setTimestamp(5, new Timestamp(jadwal.getTime()));
            ps.setTimestamp(6, new Timestamp(endJadwal.getTime()));

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) == 0; // Return true if no existing bookings overlap
            }
        } catch (SQLException ex) {
            System.err.println("Error checking availability: " + ex.getMessage());
        }
        return false; // Default to unavailable if there's an error
    }

// Method for updating time slots based on selected date with improved logic
    private void updateTimeSlots(Lapangan lapangan, JComboBox<Integer> yearComboBox,
            JComboBox<Integer> monthComboBox, JComboBox<Integer> dayComboBox,
            DefaultComboBoxModel<TimeSlot> timeModel) {

        int year = (int) yearComboBox.getSelectedItem();
        int month = (int) monthComboBox.getSelectedItem();
        int day = (int) dayComboBox.getSelectedItem();

        timeModel.removeAllElements();
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, day, 0, 0, 0);

        // Array to store hour availability (0-23)
        boolean[] hourAvailability = new boolean[24];
        Arrays.fill(hourAvailability, true); // Assume all hours are available

        try (Connection conn = new Koneksi().getConnection()) {
            String sql = "SELECT jadwal, durasi FROM booking WHERE id_lapangan = ? AND DATE(jadwal) = ? AND status = 'Booking Berhasil'";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, lapangan.getIdLapangan());
                ps.setDate(2, new java.sql.Date(calendar.getTimeInMillis()));

                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    Timestamp bookingTime = rs.getTimestamp("jadwal");
                    int durasi = rs.getInt("durasi");

                    Calendar bookingCal = Calendar.getInstance();
                    bookingCal.setTimeInMillis(bookingTime.getTime());
                    int startHour = bookingCal.get(Calendar.HOUR_OF_DAY);

                    // Mark hours within the booking duration as unavailable
                    for (int i = 0; i < durasi; i++) {
                        if (startHour + i < 24) {
                            hourAvailability[startHour + i] = false;
                        }
                    }
                }
            }
        } catch (SQLException ex) {
            System.err.println("Error checking time slots: " + ex.getMessage());
        }

        // Add available hours to the combo box
        for (int i = 0; i < 24; i++) {
            calendar.set(Calendar.HOUR_OF_DAY, i);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);

            boolean isAvailable = hourAvailability[i];
            timeModel.addElement(new TimeSlot(calendar.getTime(), isAvailable));
        }
    }

// Update day combo box based on selected year and month
    private void updateDayComboBox(JComboBox<Integer> yearComboBox, JComboBox<Integer> monthComboBox, JComboBox<Integer> dayComboBox) {
        int year = (int) yearComboBox.getSelectedItem();
        int month = (int) monthComboBox.getSelectedItem();
        dayComboBox.removeAllItems();

        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, 1);
        int daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        for (int i = 1; i <= daysInMonth; i++) {
            dayComboBox.addItem(i);
        }
    }

    // Simpan data booking ke database
    private void saveBooking(Lapangan lapangan, Date jadwal, int durasi, double totalHarga) {
        try (Connection conn = new Koneksi().getConnection()) {
            String sql = "INSERT INTO booking (id_lapangan, id_pengguna, nama_pengguna, jadwal, durasi, total_harga) "
                    + "VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, lapangan.getIdLapangan());
                ps.setInt(2, userId); // Gunakan ID pengguna yang login
                ps.setString(3, username); // Gunakan nama pengguna yang login
                ps.setTimestamp(4, new Timestamp(jadwal.getTime()));
                ps.setInt(5, durasi);
                ps.setDouble(6, totalHarga);

                ps.executeUpdate();
            }
        } catch (SQLException ex) {
            System.err.println("Error saving booking: " + ex.getMessage());
        }
    }

    // Modifikasi constructor untuk memanggil metode setupCards
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
                    .addComponent(pNama, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
            java.util.logging.Logger.getLogger(TampilanBooking.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(TampilanBooking.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(TampilanBooking.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(TampilanBooking.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new TampilanBooking().setVisible(true);
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
