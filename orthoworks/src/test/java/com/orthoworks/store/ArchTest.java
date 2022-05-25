package com.orthoworks.store;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import org.junit.jupiter.api.Test;

class ArchTest {

    @Test
    void servicesAndRepositoriesShouldNotDependOnWebLayer() {
        JavaClasses importedClasses = new ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages("com.orthoworks.store");

        noClasses()
            .that()
            .resideInAnyPackage("com.orthoworks.store.service..")
            .or()
            .resideInAnyPackage("com.orthoworks.store.repository..")
            .should()
            .dependOnClassesThat()
            .resideInAnyPackage("..com.orthoworks.store.web..")
            .because("Services and repositories should not depend on web layer")
            .check(importedClasses);
    }
}
