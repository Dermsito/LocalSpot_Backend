package com.example.demo.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

/**
 * Test de Arquitectura Hexagonal (ArchUnit).
 * - El dominio NO debe depender de Spring, JPA ni de la capa de infraestructura.
 * - Los controladores solo deben depender de los casos de uso (application).
 * - Los adaptadores de persistencia deben implementar los puertos del dominio.
 */
class HexagonalArchitectureTest {

    private static JavaClasses classes;

    @BeforeAll
    static void init() {
        classes = new ClassFileImporter().importPackages("com.example.demo");
    }

    @Test
    void dominio_noDebeDependerDeSpring() {
        ArchRule rule = noClasses()
                .that().resideInAPackage("..domain..")
                .should().dependOnClassesThat()
                .resideInAPackage("org.springframework..");

        rule.check(classes);
    }

    @Test
    void dominio_noDebeDependerDeJpa() {
        ArchRule rule = noClasses()
                .that().resideInAPackage("..domain..")
                .should().dependOnClassesThat()
                .resideInAPackage("jakarta.persistence..");

        rule.check(classes);
    }

    @Test
    void dominio_noDebeDependerDeInfraestructura() {
        ArchRule rule = noClasses()
                .that().resideInAPackage("..domain..")
                .should().dependOnClassesThat()
                .resideInAPackage("..infrastructure..");

        rule.check(classes);
    }

    @Test
    void application_noDebeDependerDeInfraestructura() {
        ArchRule rule = noClasses()
                .that().resideInAPackage("..application..")
                .should().dependOnClassesThat()
                .resideInAPackage("..infrastructure.adapter..");

        rule.check(classes);
    }

    @Test
    void controladores_debenResidirEnInfrastructureAdapterInWeb() {
        ArchRule rule = classes()
                .that().haveSimpleNameEndingWith("Controller")
                .should().resideInAPackage("..infrastructure.adapter.in.web..");

        rule.check(classes);
    }

    @Test
    void repositoriosJpa_debenResidirEnPersistence() {
        ArchRule rule = classes()
                .that().haveSimpleNameEndingWith("JpaRepository")
                .should().resideInAPackage("..infrastructure.adapter.out.persistence.repository..");

        rule.check(classes);
    }

    @Test
    void noHayDependenciasCiclicasEntreCapas() {
        slices().matching("com.example.demo.(*)..")
                .should().beFreeOfCycles()
                .check(classes);
    }
}
