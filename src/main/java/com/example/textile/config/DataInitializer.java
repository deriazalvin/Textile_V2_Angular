package com.example.textile.config;

import com.example.textile.entity.AppUser;
import com.example.textile.entity.Client;
import com.example.textile.entity.PrioriteCommande;
import com.example.textile.entity.Produit;
import com.example.textile.repository.AppUserRepository;
import com.example.textile.repository.ClientRepository;
import com.example.textile.repository.ProduitRepository;
import com.example.textile.security.AppRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.util.List;

@Configuration
@ConditionalOnProperty(name = "textile.demo-data", havingValue = "true", matchIfMissing = true)
public class DataInitializer {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    @Bean
    CommandLineRunner initData(AppUserRepository appUserRepository,
                               ClientRepository clientRepository,
                               ProduitRepository produitRepository,
                               PasswordEncoder passwordEncoder) {
        return args -> {
            creerUtilisateurs(appUserRepository, passwordEncoder);
            creerProduits(produitRepository);
            creerClients(clientRepository);
        };
    }

    private void creerUtilisateurs(AppUserRepository repository, PasswordEncoder encoder) {
        if (repository.count() > 0) {
            return;
        }
        repository.saveAll(List.of(
                new AppUser("admin", encoder.encode("admin123"), "Administrateur", AppRole.ROLE_ADMIN),
                new AppUser("manager", encoder.encode("manager123"), "Chef d'atelier", AppRole.ROLE_MANAGER),
                new AppUser("operateur", encoder.encode("operateur123"), "Operatrice atelier",
                        AppRole.ROLE_OPERATOR),
                new AppUser("consultation", encoder.encode("user123"), "Service commercial",
                        AppRole.ROLE_USER)));
        log.info("Comptes par defaut crees : admin/admin123, manager/manager123, "
                + "operateur/operateur123, consultation/user123");
    }

    private void creerProduits(ProduitRepository repository) {
        if (repository.count() > 0) {
            return;
        }
        repository.saveAll(List.of(
                new Produit("CHEM-001", "Chemise homme cintree", "Chemises", "Coton 100%",
                        new BigDecimal("45.00")),
                new Produit("CHEM-002", "Chemise femme imprimée", "Chemises", "Viscose",
                        new BigDecimal("42.50")),
                new Produit("ROB-001", "Robe droite mi-saison", "Robes", "Polyester",
                        new BigDecimal("68.00")),
                new Produit("PANT-001", "Pantalon ville coupe droite", "Pantalons", "Twill coton",
                        new BigDecimal("52.00")),
                new Produit("JAC-001", "Veste matelsee", "Vestes", "Nylon / polyester",
                        new BigDecimal("95.00")),
                new Produit("TSH-001", "T-shirt col rond", "T-shirts", "Jersey coton",
                        new BigDecimal("15.00"))));
    }

    private void creerClients(ClientRepository repository) {
        if (repository.count() > 0) {
            return;
        }
        Client c1 = new Client("Textil Est", "+216 71 100 200", "contact@textil-est.tn",
                "15 rue de l'Industrie", "Sousse");
        Client c2 = new Client("Mode Center", "+216 72 300 400", "achats@modecenter.tn",
                "Zone industrielle Mghira", "Tunis");
        Client c3 = new Client("Boutique Centrale", "+216 73 500 600", "boutique.centrale@gmail.com",
                "8 avenue Habib Bourguiba", "Sfax");
        repository.saveAll(List.of(c1, c2, c3));
    }

    /** Priorite par defaut utilisee par les commandes de demonstration. */
    public static final PrioriteCommande PRIORITE_DEFAUT = PrioriteCommande.NORMALE;
}