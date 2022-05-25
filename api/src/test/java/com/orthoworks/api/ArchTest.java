package com.orthoworks.api;

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
            .importPackages("com.orthoworks.api");

        noClasses()
            .that()
            .resideInAnyPackage("com.orthoworks.api.service..")
            .or()
            .resideInAnyPackage("com.orthoworks.api.repository..")
            .should()
            .dependOnClassesThat()
            .resideInAnyPackage("..com.orthoworks.api.web..")
            .because("Services and repositories should not depend on web layer")
            .check(importedClasses);
    }
}
