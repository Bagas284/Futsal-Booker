/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package src;

/**
 *
 * @author Bagas Aldianata
 */
public class Lapangan {
    protected int idLapangan; // Tambahkan atribut idLapangan
    protected String nama_lapangan;
    protected String status;
    protected double hargaPerJam; // Tambahkan atribut hargaPerJam

    public Lapangan(int idLapangan, String nama_lapangan, String status, double hargaPerJam) {
        this.idLapangan = idLapangan; // Inisialisasi idLapangan
        this.nama_lapangan = nama_lapangan;
        this.status = status;
        this.hargaPerJam = hargaPerJam; // Inisialisasi hargaPerJam
    }

    // Getter dan Setter untuk idLapangan
    public int getIdLapangan() {
        return idLapangan;
    }

    public void setIdLapangan(int idLapangan) {
        this.idLapangan = idLapangan;
    }

    public void setNama_lapangan(String nama_lapangan) {
        this.nama_lapangan = nama_lapangan;
    }

    public String getNama_lapangan() {
        return nama_lapangan;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public double getHargaPerJam() {
        return hargaPerJam; // Getter untuk hargaPerJam
    }
    
    public double setHargaPerJam(){
        return hargaPerJam;
    }

    public void display() {
        System.out.println("ID Lapangan: " + idLapangan + 
                           " | Nama lapangan: " + nama_lapangan + 
                           " | Status: " + status + 
                           " | Harga per jam: " + hargaPerJam);
    }
}
