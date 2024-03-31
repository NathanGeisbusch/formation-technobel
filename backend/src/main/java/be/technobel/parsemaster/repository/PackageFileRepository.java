package be.technobel.parsemaster.repository;

import be.technobel.parsemaster.entity.PackageFile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PackageFileRepository extends JpaRepository<PackageFile, Long> {
}
