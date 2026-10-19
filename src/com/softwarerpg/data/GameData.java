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

    /**
     * Campaña de 10 misiones: dos por nivel de madurez CMMI. La primera de cada nivel es un
     * reto intermedio; la segunda es el jefe del nivel y al vencerla la organización asciende
     * al siguiente nivel CMMI. Las estadísticas se ajustaron con simulación (CampaignBalanceTest).
     */
    public static List<Mission> createMissions() {
        List<Mission> missions = new ArrayList<>();

        // ---------------- NIVEL 1 CMMI - INICIAL (Caos, ad-hoc, sobrecostos)
        missions.add(new Mission("MIS-01A",
            "Misión 1: El Proyecto sin Control de Versiones",
            "Papelería La Esquina",
            "Un sistema de inventario donde cada quien guarda su copia del código. Nadie sabe cuál versión es la buena.",
            40, 90, 1,
            new Enemy("El Fantasma del Copiar-Pegar",
                "Nivel 1 CMMI: código duplicado y sin versiones",
                80, 14, 1, DefectType.CONSTRUCCION_PAQUETES, 70, 60,
                "¿Cuál es la versión final? ¿final_v2? ¿final_v2_ahora_si? ¡Jajaja!", false),
            70, 60, 40,
            "💡 Pista Táctica: Este enemigo ataca con código duplicado. Usa 'Codificar' para hacer daño base o una 'Checklist PSP' si tu salud baja para estabilizar el proyecto."));

        missions.add(new Mission("MIS-01",
            "Misión 2: La Pesadilla Ad-Hoc del Novato",
            "Profesor de Computación",
            "Desarrollar un algoritmo sin bitácoras ni planificación. El proyecto sobrepasa el presupuesto y tiempo por falta de proceso probado.",
            54, 120, 1,
            new Enemy("El Caos Inicial de Medianoche",
                "Manifestación del Nivel 1 CMMI: Falta de Procesos y Desesperación",
                120, 18, 2, DefectType.SINTAXIS, 100, 90,
                "¡En esta empresa no hay reglas! ¡El código compila de milagro y la entrega era para ayer!", true),
            100, 90, 60,
            "💡 Pista Táctica [JEFE CMMI-1]: Es caótico y ad-hoc. Primero cúrate con 'Checklist PSP' (PSP 2) para mitigar sus golpes; luego activa Sinergia TSP con el 'Líder del Equipo' para recuperar MP y rematar con 'Codificar'."));

        // ---------------- NIVEL 2 CMMI - GESTIONADO (Requisitos, planes, monitoreo, QA)
        missions.add(new Mission("MIS-02A",
            "Misión 3: El Plan que Nadie Sigue",
            "Agencia de Viajes Rumbo",
            "El proyecto tiene un plan bonito en un cajón, pero nadie da seguimiento al avance ni revisa la calidad de lo entregado.",
            90, 180, 2,
            new Enemy("El Cronograma Fantasma",
                "Nivel 2 CMMI: planes sin seguimiento ni aseguramiento de calidad",
                260, 22, 4, DefectType.DOCUMENTACION, 130, 110,
                "¿Seguimiento semanal? Ese diagrama de Gantt se hizo una vez y nunca más se abrió.", false),
            130, 110, 80,
            "💡 Pista Táctica: Los planes abandonados generan desviaciones. Usa la Sinergia TSP del 'Gestor de Planificación' para potenciar tu siguiente ataque (+40% daño) o 'Estimación LOC (PSP 1)' para recuperar disciplina."));

        missions.add(new Mission("MIS-02",
            "Misión 4: Domando la Gestión de Requisitos",
            "Financiera MoProSoft",
            "Establecer acuerdos claros con las partes interesadas, planificar el proyecto y mantener la disciplina de proceso en momentos de estrés.",
            120, 240, 2,
            new Enemy("El Monstruo de Requisitos Ambulantes",
                "Pesadilla del Nivel 2: Ambigüedad y Alcance Descontrolado",
                291, 25, 5, DefectType.ASIGNACION, 170, 140,
                "¡Cada día cambiaré los requisitos de tu cliente! ¡Tus planes documentados arderán bajo estrés!", true),
            170, 140, 120,
            "💡 Pista Táctica [JEFE CMMI-2]: ¡Requisitos cambiantes! Usa de inmediato la Sinergia TSP con el 'Administrador de Requerimientos' para fijar la línea base: ¡inmuniza 3 codificaciones contra defectos y reduce su daño a la mitad!"));

        // ---------------- NIVEL 3 CMMI - DEFINIDO (Estandarización organizacional)
        missions.add(new Mission("MIS-03A",
            "Misión 5: La Torre de Babel de Herramientas",
            "Universidad Tecnológica del Norte",
            "Tres equipos, tres lenguajes, tres formas de probar. Integrar sus módulos es una batalla en cada entrega.",
            220, 300, 3,
            new Enemy("El Integrador Caótico",
                "Nivel 3 CMMI: cada equipo con sus propias herramientas",
                330, 29, 7, DefectType.INTERFAZ, 210, 170,
                "¡Tu módulo espera JSON y el mío manda XML! ¡Y nadie documentó la interfaz!", false),
            210, 170, 150,
            "💡 Pista Táctica: Incompatibilidad de interfaces (Tipo 50). Pide apoyo al 'Gestor de Desarrollo (TSP)' para asestar un golpe de arquitectura limpia (45+ daño) y neutralizar sus llamadas defectuosas."));

        missions.add(new Mission("MIS-03",
            "Misión 6: El Bastión de los Procesos Estandarizados",
            "Secretaría de Economía (Norma NMX-I-059)",
            "Unificar las normas, procedimientos y herramientas en toda la organización para actuar de forma proactiva y prevenir riesgos futuros.",
            280, 360, 3,
            new Enemy("El Coloso de Procesos No Estandarizados",
                "Amenaza del Nivel 3: Falta de Métodos y Herramientas Comunes",
                281, 32, 8, DefectType.CONSTRUCCION_PAQUETES, 260, 210,
                "¡Cada desarrollador usa sus propias reglas! ¡Nadie sigue las normas ni procedimientos estándar!", true),
            260, 210, 200,
            "💡 Pista Táctica [JEFE CMMI-3]: La falta de normas estándar lo vuelve vulnerable a la revisión formal. Invoca la Sinergia del 'Gestor de Calidad (TSP)' para ejecutar una inspección por pares (+20 HP y 25 daño directo)."));

        // ---------------- NIVEL 4 CMMI - CUANTITATIVAMENTE GESTIONADO (Control estadístico)
        missions.add(new Mission("MIS-04A",
            "Misión 7: Métricas sin Sentido",
            "Aseguradora Prudencia",
            "La empresa mide todo pero no analiza nada: los promedios esconden los problemas y nadie distingue el ruido de las causas especiales.",
            380, 440, 4,
            new Enemy("El Promedio Engañoso",
                "Nivel 4 CMMI: datos sin análisis estadístico",
                480, 40, 11, DefectType.DATOS, 320, 250,
                "En promedio todo va bien... ¡aunque la mitad de los proyectos se incendie!", false),
            320, 250, 260,
            "💡 Pista Táctica: Datos mal interpretados. Usa 'Estimación LOC (PSP 1)' para desarmar su ruido numérico y luego castígalo con 'Codificar' bajo concentración."));

        missions.add(new Mission("MIS-04",
            "Misión 8: Laboratorio de Control Estadístico",
            "Instituto Médico de Alta Previsibilidad",
            "Implementar control cuantitativo con técnicas estadísticas para medir subprocesos y eliminar fuentes de causas especiales de variación.",
            450, 500, 4,
            new Enemy("La Hidra de Variación Especial",
                "Bestia del Nivel 4: Ruido Estadístico y Causas Especiales",
                467, 42, 14, DefectType.FUNCION, 390, 300,
                "¿Crees que puedes predecir mis ataques con números? ¡Mis defectos lógicos rompen tus distribuciones estadísticas!", true),
            390, 300, 320,
            "💡 Pista Táctica [JEFE CMMI-4]: ¡Causas especiales de variación! ¡Usa 'Control Estadístico (CMMI-4)'! Este movimiento erradica sus desviaciones y causa hasta 100 de daño devastador basado en el Yield de tu proceso."));

        // ---------------- NIVEL 5 CMMI - OPTIMIZACIÓN (Mejora continua e innovación)
        missions.add(new Mission("MIS-05A",
            "Misión 9: La Deuda Técnica Silenciosa",
            "Banco Nacional Digital",
            "Un sistema legado que funciona \"de milagro\": cada mejora rompe algo y nadie se atreve a tocar el núcleo.",
            650, 640, 5,
            new Enemy("El Monolito Legado",
                "Nivel 5 CMMI: sistemas que nadie se atreve a cambiar",
                566, 48, 15, DefectType.ENTORNO, 460, 350,
                "¡Llevo 20 años funcionando! ¿Pruebas automatizadas? ¿Para qué?", false),
            460, 350, 400,
            "💡 Pista Táctica: La resistencia al cambio se vence con paciencia. Alterna 'Checklists PSP' y motivación del 'Líder TSP' para sostener la salud del proyecto ante su desgaste prolongado."));

        missions.add(new Mission("MIS-05",
            "Misión 10: La Cima de la Optimización e Innovación",
            "Consorcio Global de Excelencia en Software",
            "Alcanzar el pináculo de la ingeniería: cultura de mejora continua, comprensión de causas comunes de variación e innovación tecnológica ágil.",
            750, 720, 5,
            new Enemy("La Entropía del Código Espagueti",
                "El Último Desafío del Nivel 5: La Inercia y Causas Comunes de Variación",
                533, 52, 16, DefectType.SISTEMA, 550, 450,
                "¡El software envejece y decae! ¡Ninguna mejora continua puede salvarte de la entropía definitiva!", true),
            550, 450, 500,
            "💡 Pista Táctica [JEFE FINAL CMMI-5]: El rey de las causas comunes de variación. ¡Activa 'Mejora Continua (CMMI-5)'! Regenerarás +50 HP iniciales y +15 HP por 3 turnos para superar cualquier crisis."));

        return missions;
    }

    /**
     * Bug de práctica escalado al nivel del héroe: si el héroe supera el nivel de la etapa,
     * el bug gana +15% de vida y ataque por nivel de diferencia (farmear no se vuelve trivial).
     */
    public static Enemy createRandomBug(int stage, int heroLevel) {
        Enemy base = createRandomBug(stage);
        double f = 1 + 0.15 * Math.max(0, heroLevel - stage);
        return new Enemy(base.getName(), base.getSubtitle(), (int) Math.round(base.getMaxHp() * f),
                (int) Math.round(base.getAttackPower() * f), base.getDefense(), base.getAssociatedDefect(),
                base.getXpReward(), base.getGoldReward(), base.getIntroQuote(), false);
    }

    public static Enemy createRandomBug(int stage) {
        switch (stage) {
            case 1:
                return new Enemy("Bug de Sintaxis", "Errata en código", 45, 12, 1,
                        DefectType.SINTAXIS, 20, 20, "¡Olvidaste un punto y coma en la línea 42!", false);
            case 2:
                return new Enemy("Error de Asignación", "Confusión de tipos y ámbito", 80, 18, 3,
                        DefectType.ASIGNACION, 30, 30, "¡Declaraste dos variables con el mismo identificador!", false);
            case 3:
                return new Enemy("Fallo de Interfaz", "Parámetro inválido en llamada RPC", 125, 25, 6,
                        DefectType.INTERFAZ, 45, 40, "¡El formato de la respuesta del servicio externo no coincide!", false);
            case 4:
                return new Enemy("Puntero Nulo y Bucle Infinito", "Defecto de Función / Lógica", 180, 32, 10,
                        DefectType.FUNCION, 60, 55, "¡NullPointerException y bucle sin condición de parada!", false);
            case 5:
            default:
                return new Enemy("Fuga de Memoria y Deadlock", "Defecto de Sistema y Concurrencia", 240, 40, 14,
                        DefectType.SISTEMA, 80, 70, "¡Hilos bloqueados permanentemente por recursos compartidos!", false);
        }
    }
}

