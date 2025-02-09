/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package src;

import java.util.Date;
import java.text.SimpleDateFormat;

/**
 *
 * @author ASUS
 */
public class Booking {

    private int idBooking;
    private Date jadwal;
    private int durasi;
    private double totalHarga;
    private String status;
     private String alasan; 

    // Asosiasi ke Pengguna
    private Pengguna pengguna;

    // Agregasi ke Lapangan
    private Lapangan lapangan;

    // Constructor
    public Booking(int idBooking, Date jadwal, int durasi, double totalHarga, String status, String alasan, Pengguna pengguna, Lapangan lapangan) {
        this.idBooking = idBooking;
        this.jadwal = jadwal;
        this.durasi = durasi;
        this.totalHarga = totalHarga;
        this.status = status;
        this.alasan = alasan; 
        this.pengguna = pengguna;
        this.lapangan = lapangan;
    }

    // Getter dan Setter
    public int getIdBooking() {
        return idBooking;
    }

    public void setIdBooking(int idBooking) {
        this.idBooking = idBooking;
    }

    public Date getJadwal() {
        return jadwal;
    }

    public String getFormattedJadwal() {
        // Format tanggal menjadi "dd-MM-yyyy HH:mm" (atau format lain sesuai kebutuhan)
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        return sdf.format(jadwal);
    }

    public void setJadwal(Date jadwal) {
        this.jadwal = jadwal;
    }

    public int getDurasi() {
        return durasi;
    }

    public void setDurasi(int durasi) {
        this.durasi = durasi;
    }

    public double getTotalHarga() {
        return totalHarga;
    }

    public void setTotalHarga(double totalHarga) {
        this.totalHarga = totalHarga;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    public String getAlasan() {
        return alasan;
    }

    public void setAlasan(String alasan) {
        this.alasan = alasan;
    }

    public Pengguna getPengguna() {
        return pengguna;
    }

    public void setPengguna(Pengguna pengguna) {
        this.pengguna = pengguna;
    }

    public Lapangan getLapangan() {
        return lapangan;
    }

    public void setLapangan(Lapangan lapangan) {
        this.lapangan = lapangan;
    }

    // Display Booking Information
    public void displayBookingInfo() {
        System.out.println("ID Booking: " + idBooking);
        System.out.println("Jadwal: " + jadwal);
        System.out.println("Durasi: " + durasi + " jam");
        System.out.println("Total Harga: " + totalHarga);
        System.out.println("Status: " + status);
        System.out.println("Pengguna: " + (pengguna != null ? pengguna.getNamaPengguna() : "Tidak ada pengguna"));
        System.out.println("Lapangan: " + (lapangan != null ? lapangan.getNama_lapangan() : "Tidak ada lapangan"));
    }
}
