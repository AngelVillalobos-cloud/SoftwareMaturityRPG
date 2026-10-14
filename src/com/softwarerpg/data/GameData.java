package com.softwarerpg.data;

import com.softwarerpg.model.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Banco de datos de misiones, enemigos y equipo inicial según el temario visto en clase:
 * CMMI (Niveles 1 a 5), MoProSoft (DIR, GER, OPE), PSP (Bitácoras, LOC, defectos 10-100) y TSP (Roles y trabajo en equipo).
 */
public class GameData {

    public static List<TeamMember> createInitialTeam() {
        List<TeamMember> team = new ArrayList<>();
        team.add(new TeamMember("Ana (Líder)", TSPRole.LIDER_EQUIPO, PSPLevel.PSP1));
        team.add(new TeamMember("Carlos (Dev)", TSPRole.GESTOR_DESARROLLO, PSPLevel.PSP1));
        team.add(new TeamMember("Elena (Plan)", TSPRole.GESTOR_PLANIFICACION, PSPLevel.PSP0_1));
        team.add(new TeamMember("Roberto (QA)", TSPRole.GESTOR_CALIDAD, PSPLevel.PSP2));
        team.add(new TeamMember("Sofia (Req/Config)", TSPRole.GESTOR_REQUERIMIENTOS, PSPLevel.PSP0_1));
        return team;
    }

    public static List<Mission> createMissions() {
        List<Mission> missions = new ArrayList<>();

        // NIVEL 1 CMMI - INICIAL (Caos, ad-hoc, sobrecostos)
        Enemy boss1 = new Enemy(
            "El Caos Inicial de Medianoche",
            "Manifestación del Nivel 1 CMMI: Falta de Procesos y Desesperación",
            120, 18, 2, DefectType.SINTAXIS, 150, 200,
            "¡En esta empresa no hay reglas! ¡El código compila de milagro y la entrega era para ayer!",
            true
        );
        missions.add(new Mission(
            "MIS-01",
            "Misión 1: La Pesadilla Ad-Hoc del Novato",
            "Profesor de Computación",
            "Desarrollar un algoritmo sin bitácoras ni planificación. El proyecto sobrepasa el presupuesto y tiempo por falta de proceso probado.",
            54, 120, 1, boss1, 150, 250, 60
        ));

        // NIVEL 2 CMMI - GESTIONADO (Requisitos, monitoreo, aseguramiento QA)
        Enemy boss2 = new Enemy(
            "El Monstruo de Requisitos Ambulantes",
            "Pesadilla del Nivel 2: Ambigüedad y Alcance Descontrolado",
            220, 26, 5, DefectType.ASIGNACION, 280, 400,
            "¡Cada día cambiaré los requisitos de tu cliente! ¡Tus planes documentados arderán bajo estrés!",
            true
        );
        missions.add(new Mission(
            "MIS-02",
            "Misión 2: Domando la Gestión de Requisitos",
            "Financiera MoProSoft",
            "Establecer acuerdos claros con las partes interesadas, planificar el proyecto y mantener la disciplina de proceso en momentos de estrés.",
            120, 240, 2, boss2, 300, 450, 120
        ));

        // NIVEL 3 CMMI - DEFINIDO (Estandarización organizacional y proactividad)
        Enemy boss3 = new Enemy(
            "El Coloso de Procesos No Estandarizados",
            "Amenaza del Nivel 3: Falta de Métodos y Herramientas Comunes",
            340, 35, 9, DefectType.CONSTRUCCION_PAQUETES, 450, 650,
            "¡Cada desarrollador usa sus propias reglas! ¡Nadie sigue las normas ni procedimientos estándar!",
            true
        );
        missions.add(new Mission(
            "MIS-03",
            "Misión 3: El Bastión de los Procesos Estandarizados",
            "Secretaría de Economía (Norma NMX-I-059)",
            "Unificar las normas, procedimientos y herramientas en toda la organización para actuar de forma proactiva y prevenir riesgos futuros.",
            280, 360, 3, boss3, 500, 700, 200
        ));

        // NIVEL 4 CMMI - CUANTITATIVAMENTE GESTIONADO (Control estadístico y variación especial)
        Enemy boss4 = new Enemy(
            "La Hidra de Variación Especial",
            "Bestia del Nivel 4: Ruido Estadístico y Causas Especiales",
            480, 44, 14, DefectType.FUNCION, 650, 950,
            "¿Crees que puedes predecir mis ataques con números? ¡Mis defectos lógicos rompen tus distribuciones estadísticas!",
            true
        );
        missions.add(new Mission(
            "MIS-04",
            "Misión 4: Laboratorio de Control Estadístico",
            "Instituto Médico de Alta Previsibilidad",
            "Implementar control cuantitativo con técnicas estadísticas para medir subprocesos y eliminar fuentes de causas especiales de variación.",
            450, 500, 4, boss4, 750, 1100, 320
        ));

        // NIVEL 5 CMMI - OPTIMIZACIÓN (Mejora continua, causas comunes, innovación tecnológica)
        Enemy boss5 = new Enemy(
            "La Entropía del Código Espagueti",
            "El Último Desafío del Nivel 5: La Inercia y Causas Comunes de Variación",
            650, 55, 18, DefectType.SISTEMA, 1000, 1800,
            "¡El software envejece y decae! ¡Ninguna mejora continua puede salvarte de la entropía definitiva!",
            true
        );
        missions.add(new Mission(
            "MIS-05",
            "Misión 5: La Cima de la Optimización e Innovación",
            "Consorcio Global de Excelencia en Software",
            "Alcanzar el pináculo de la ingeniería: cultura de mejora continua, comprensión de causas comunes de variación e innovación tecnológica ágil.",
            750, 720, 5, boss5, 1200, 2000, 500
        ));

        return missions;
    }

    public static Enemy createRandomBug(int stage) {
        switch (stage) {
            case 1:
                return new Enemy("Bug de Sintaxis", "Errata en código", 45, 12, 1,
                        DefectType.SINTAXIS, 40, 50, "¡Olvidaste un punto y coma en la línea 42!", false);
            case 2:
                return new Enemy("Error de Asignación", "Confusión de tipos y ámbito", 80, 18, 3,
                        DefectType.ASIGNACION, 75, 90, "¡Declaraste dos variables con el mismo identificador!", false);
            case 3:
                return new Enemy("Fallo de Interfaz", "Parámetro inválido en llamada RPC", 125, 25, 6,
                        DefectType.INTERFAZ, 120, 140, "¡El formato de la respuesta del servicio externo no coincide!", false);
            case 4:
                return new Enemy("Puntero Nulo y Bucle Infinito", "Defecto de Función / Lógica", 180, 32, 10,
                        DefectType.FUNCION, 180, 220, "¡NullPointerException y bucle sin condición de parada!", false);
            case 5:
            default:
                return new Enemy("Fuga de Memoria y Deadlock", "Defecto de Sistema y Concurrencia", 240, 40, 14,
                        DefectType.SISTEMA, 250, 300, "¡Hilos bloqueados permanentemente por recursos compartidos!", false);
        }
    }
}

