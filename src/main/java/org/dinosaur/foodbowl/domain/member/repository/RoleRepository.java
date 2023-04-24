package org.dinosaur.foodbowl.domain.member.repository;

import org.dinosaur.foodbowl.domain.member.entity.Role;
import org.springframework.data.repository.Repository;

import java.util.List;

public interface RoleRepository extends Repository<Role, Long> {

    List<Role> findAll();
}
