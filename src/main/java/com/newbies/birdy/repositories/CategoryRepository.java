package com.newbies.birdy.repositories;

import com.newbies.birdy.entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {

    Category findByIdAndStatus(int id, Boolean status);
}
