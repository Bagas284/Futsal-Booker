/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package src;

/**
 *
 * @author Bagas Aldianata
 */
public class Indoor extends Lapangan {
    private String tipe_lapangan = "Rumput Sintetis (Indoor)";
    private String fasilitas = "Ruang Ganti";
    

    // Konstruktor menerima idLapangan dan hargaPerJam
    public Indoor(int idLapangan, String nama_lapangan, String status, double hargaPerJam) {
        super(idLapangan, nama_lapangan, status, hargaPerJam);
    }

    public String getTipe_lapangan() {
        return tipe_lapangan;
    }

    public void setTipe_lapangan(String tipe_lapangan) {
        this.tipe_lapangan = tipe_lapangan;
    }

    public String getFasilitas() {
        return fasilitas;
    }

    public void setFasilitas(String fasilitas) {
        this.fasilitas = fasilitas;
    }

    
    @Override
    public void display() {
        super.display(); // Panggil display dari Lapangan
        System.out.println("Tipe lapangan: " + tipe_lapangan);
    }
}
