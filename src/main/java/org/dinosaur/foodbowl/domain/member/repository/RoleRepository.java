package org.dinosaur.foodbowl.domain.member.repository;

import java.util.List;
import org.dinosaur.foodbowl.domain.member.entity.Role;
import org.springframework.data.repository.Repository;

public interface RoleRepository extends Repository<Role, Long> {

    List<Role> findAll();
}
