/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package src;

/**
 *
 * @author ASUS
 */
import java.util.Date;

public class Pembayaran {
    private int idPembayaran;
    private Booking booking; // Komposisi: Pembayaran memiliki Booking
    private double jumlahBayar;
    private String buktiBayar;
    private final Date tanggalBayar;

    // Constructor
    public Pembayaran(int idPembayaran, Booking booking, double jumlahBayar, String buktiBayar) {
        this.idPembayaran = idPembayaran;
        this.booking = booking; // Menginisialisasi Booking
        this.jumlahBayar = jumlahBayar;
        this.buktiBayar = buktiBayar;
        this.tanggalBayar = new Date(); // Set tanggal bayar ke waktu saat ini
    }

    // Getter dan Setter
    public int getIdPembayaran() {
        return idPembayaran;
    }

    public void setIdPembayaran(int idPembayaran) {
        this.idPembayaran = idPembayaran;
    }

    public Booking getBooking() {
        return booking;
    }

    public void setBooking(Booking booking) {
        this.booking = booking;
    }

    public double getJumlahBayar() {
        return jumlahBayar;
    }

    public void setJumlahBayar(double jumlahBayar) {
        this.jumlahBayar = jumlahBayar;
    }

    public String getBuktiBayar() {
        return buktiBayar;
    }

    public void setBuktiBayar(String buktiBayar) {
        this.buktiBayar = buktiBayar;
    }

    public Date getTanggalBayar() {
        return tanggalBayar;
    }

    // Display Payment Information
//    public void displayPaymentInfo() {
//        System.out.println("ID Pembayaran: " + idPembayaran);
//        System.out.println("ID Booking: " + (booking != null ? booking.getIdBooking() : "Tidak ada booking"));
//        System.out.println("Jumlah Bayar: " + jumlahBayar);
//        System.out.println("Bukti Bayar: " + buktiBayar);
//        System.out.println("Tanggal Bayar: " + tanggalBayar);
//    }
}


