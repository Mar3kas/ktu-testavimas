package com.projektas.itprojektas.repository;

import com.projektas.itprojektas.model.Consultant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConsultantRepository extends JpaRepository<Consultant, Integer> {

    @Query(value = "select * from consultants inner join consultant_status on consultants.consultant_status_id = consultant_status.id  where consultant_status.status = 'free'", nativeQuery = true)
    List<Consultant> findFreeConsultants();

    @Query(value = "select * from consultants u where u.username = ?1 LIMIT 1", nativeQuery = true)
    Consultant findByUsername(String username);

    @Query(value = "select * from consultants where id = ?1", nativeQuery = true)
    Consultant findConsultantById(int id);

    @Modifying
    //@Transactional
    @Query(value = "update consultants set consultant_status_id = 2 where id = ?1", nativeQuery = true)
    void occupyConsultant(int id);

    @Modifying
    //@Transactional
    @Query(value = "update consultants set consultant_status_id = 1 where id = ?1", nativeQuery = true)
    void freeConsultant(int id);
}
