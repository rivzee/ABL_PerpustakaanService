package com.vanzi.buku.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vanzi.buku.model.BukuModel;
import com.vanzi.buku.service.BukuService;

import static net.logstash.logback.argument.StructuredArguments.kv;

@RestController
@RequestMapping("/api/buku")
public class BukuController {

    private static final Logger log = LoggerFactory.getLogger(BukuController.class);

    @Autowired
    private BukuService bukuService;

    @GetMapping
    public List<BukuModel> getAllBuku() {
        log.info("Request received", kv("action", "GET_ALL"));
        List<BukuModel> result = bukuService.getAllBuku();
        log.info("Request completed", kv("action", "GET_ALL"), kv("status", "SUKSES"), kv("count", result.size()));
        return result;
    }

    @GetMapping("/{id}")
    public ResponseEntity<BukuModel> getBukubyId(@PathVariable Long id) {
        log.info("Request received", kv("action", "GET_BY_ID"), kv("bukuId", id));
        BukuModel buku = bukuService.getBukuById(id);
        if (buku != null) {
            log.info("Request completed", 
                kv("action", "GET_BY_ID"), 
                kv("status", "SUCCESS"), 
                kv("bukuId", id),
                kv("judul", buku.getJudul()),
                kv("pengarang", buku.getPengarang()));
            return ResponseEntity.ok(buku);
        } else {
            log.warn("Request completed", kv("action", "GET_BY_ID"), kv("status", "NOT_FOUND"), kv("bukuId", id));
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public BukuModel createBuku(@RequestBody BukuModel buku) {
        log.info("Request received", 
            kv("action", "CREATE"), 
            kv("eventType", "DATA_CHANGE"),
            kv("judul", buku.getJudul()));
        
        BukuModel result = bukuService.createBuku(buku);
        
        // Log detail data yang baru dibuat
        log.info("DATA CREATED - Buku baru ditambahkan", 
            kv("action", "CREATE"), 
            kv("eventType", "DATA_CHANGE"),
            kv("status", "SUCCESS"), 
            kv("bukuId", result.getId()),
            kv("newData_judul", result.getJudul()),
            kv("newData_pengarang", result.getPengarang()),
            kv("newData_penerbit", result.getPenerbit()),
            kv("newData_tahunTerbit", result.getTahun_terbit()));
        
        return result;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBuku(@PathVariable Long id) {
        log.info("Request received", 
            kv("action", "DELETE"), 
            kv("eventType", "DATA_CHANGE"),
            kv("bukuId", id));
        
        // Ambil data sebelum dihapus untuk logging
        BukuModel bukuToDelete = bukuService.getBukuById(id);
        
        if (bukuToDelete != null) {
            // Log detail data yang akan dihapus
            log.info("DATA DELETED - Buku dihapus", 
                kv("action", "DELETE"), 
                kv("eventType", "DATA_CHANGE"),
                kv("status", "SUCCESS"), 
                kv("bukuId", id),
                kv("deletedData_judul", bukuToDelete.getJudul()),
                kv("deletedData_pengarang", bukuToDelete.getPengarang()),
                kv("deletedData_penerbit", bukuToDelete.getPenerbit()),
                kv("deletedData_tahunTerbit", bukuToDelete.getTahun_terbit()));
            
            bukuService.deleteBuku(id);
            return ResponseEntity.noContent().build();
        } else {
            log.warn("DELETE FAILED - Buku tidak ditemukan", 
                kv("action", "DELETE"), 
                kv("eventType", "DATA_CHANGE"),
                kv("status", "NOT_FOUND"), 
                kv("bukuId", id));
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<BukuModel> updateBuku(@PathVariable Long id, @RequestBody BukuModel buku) {
        log.info("Request received", 
            kv("action", "UPDATE"), 
            kv("eventType", "DATA_CHANGE"),
            kv("bukuId", id));
        
        // Ambil data lama sebelum update
        BukuModel oldData = bukuService.getBukuById(id);
        
        if (oldData == null) {
            log.warn("UPDATE FAILED - Buku tidak ditemukan", 
                kv("action", "UPDATE"), 
                kv("eventType", "DATA_CHANGE"),
                kv("status", "NOT_FOUND"), 
                kv("bukuId", id));
            return ResponseEntity.notFound().build();
        }
        
        // Simpan data lama untuk perbandingan
        String oldJudul = oldData.getJudul();
        String oldPengarang = oldData.getPengarang();
        String oldPenerbit = oldData.getPenerbit();
        Integer oldTahunTerbit = oldData.getTahun_terbit();
        
        // Lakukan update
        BukuModel updated = bukuService.updateBuku(id, buku);
        
        if (updated != null) {
            // Bandingkan dan log perubahan spesifik
            List<String> changes = new ArrayList<>();
            
            if (!Objects.equals(oldJudul, updated.getJudul())) {
                changes.add("judul: '" + oldJudul + "' -> '" + updated.getJudul() + "'");
            }
            if (!Objects.equals(oldPengarang, updated.getPengarang())) {
                changes.add("pengarang: '" + oldPengarang + "' -> '" + updated.getPengarang() + "'");
            }
            if (!Objects.equals(oldPenerbit, updated.getPenerbit())) {
                changes.add("penerbit: '" + oldPenerbit + "' -> '" + updated.getPenerbit() + "'");
            }
            if (!Objects.equals(oldTahunTerbit, updated.getTahun_terbit())) {
                changes.add("tahun_terbit: '" + oldTahunTerbit + "' -> '" + updated.getTahun_terbit() + "'");
            }
            
            String changesDescription = changes.isEmpty() ? "Tidak ada perubahan" : String.join(", ", changes);
            
            // Log dengan detail lengkap perubahan
            log.info("DATA UPDATED - Buku diperbarui", 
                kv("action", "UPDATE"), 
                kv("eventType", "DATA_CHANGE"),
                kv("status", "SUCCESS"), 
                kv("bukuId", id),
                kv("changesDescription", changesDescription),
                kv("changesCount", changes.size()),
                kv("oldData_judul", oldJudul),
                kv("oldData_pengarang", oldPengarang),
                kv("oldData_penerbit", oldPenerbit),
                kv("oldData_tahunTerbit", oldTahunTerbit),
                kv("newData_judul", updated.getJudul()),
                kv("newData_pengarang", updated.getPengarang()),
                kv("newData_penerbit", updated.getPenerbit()),
                kv("newData_tahunTerbit", updated.getTahun_terbit()));
            
            return ResponseEntity.ok(updated);
        } else {
            log.warn("UPDATE FAILED - Gagal memperbarui buku", 
                kv("action", "UPDATE"), 
                kv("eventType", "DATA_CHANGE"),
                kv("status", "FAILED"), 
                kv("bukuId", id));
            return ResponseEntity.notFound().build();
        }
    }
}
