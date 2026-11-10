package py.edu.uc.lp3.me.cs2.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import py.edu.uc.lp3.me.cs2.domain.*;
import py.edu.uc.lp3.me.cs2.repository.ArmaRepository;
import py.edu.uc.lp3.me.cs2.repository.JugadorRepository;

import java.util.List;

@Configuration
public class DataLoaderConfig {

    @Bean
    public CommandLineRunner initDatabase(ArmaRepository armaRepository, JugadorRepository jugadorRepository) {
        return args -> {
            if (armaRepository.count() == 0) {
                Pistola glock = new Pistola("Glock-18", 200, Equipo.TERRORISTA, 28.0, 75.0, 20, 120, 2.2, false, "glock18.png", "pistol_shot.wav");
                glock.setDescripcion("Pistola estándar de los terroristas");

                Pistola usps = new Pistola("USP-S", 200, Equipo.COUNTER_TERRORISTA, 35.0, 85.0, 12, 24, 2.2, true, "usps.png", "silenced_shot.wav");
                usps.setDescripcion("Pistola silenciada antiterrorista");

                Granada he = new Granada("Granada HE", 300, Equipo.TERRORISTA, 98.0, 8.0, 2.0, true, "Explosión fragmentaria", "he_grenade.png", "explosion.wav");
                he.setDescripcion("Granada explosiva de fragmentación");

                Granada flash = new Granada("Granada Flashbang", 200, Equipo.AMBOS, 5.0, 10.0, 1.5, false, "Ceguera temporal", "flashbang.png", "flash_bang.wav");
                flash.setDescripcion("Granada aturdidora no letal");

                RifleAsalto ak47 = new RifleAsalto("AK-47", 2700, Equipo.TERRORISTA, 36.0, 77.0, 30, 90, 2.5, "ak47.png", "rifle_shot.wav");
                ak47.setDescripcion("Rifle de asalto de gran potencia");

                Pistola deagle = new Pistola("Desert Eagle", 700, Equipo.AMBOS, 53.0, 80.0, 7, 35, 2.2, false, "deagle.png", "deagle_shot.wav");
                deagle.setDescripcion("Pistola de alto calibre");

                armaRepository.saveAll(List.of(glock, usps, he, flash, ak47, deagle));
            }

            if (jugadorRepository.count() == 0) {
                Jugador ct = new Jugador("s1mple (CT)", 100.0, Equipo.COUNTER_TERRORISTA,
                        new Blindaje("Kevlar y Casco Militar", 100.0, 50.0));
                Jugador t = new Jugador("Phoenix (T)", 100.0, Equipo.TERRORISTA,
                        new Blindaje("Chaleco Kevlar", 100.0, 50.0));
                jugadorRepository.saveAll(List.of(ct, t));
            }
        };
    }
}
