package com.vanzi.buku.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vanzi.buku.model.BukuModel;

@Repository
public interface BukuRepository extends JpaRepository<BukuModel, Long> {

}
