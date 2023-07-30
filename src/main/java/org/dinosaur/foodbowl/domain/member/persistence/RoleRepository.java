package org.dinosaur.foodbowl.domain.member.persistence;

import org.dinosaur.foodbowl.domain.member.domain.Role;
import org.springframework.data.repository.Repository;

public interface RoleRepository extends Repository<Role, Long> {
}
