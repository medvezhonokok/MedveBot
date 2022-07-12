package com.medvebot.features;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * @author : medvezhonokok
 * @mailto : nocap239@gmail.com
 * @created : 24.06.2022, пятница
 **/
public class Movies {
    private static final Map<String, String> horrors = new HashMap<>();
    private static final Map<String, String> drams = new HashMap<>();
    private static final Map<String, String> detectives = new HashMap<>();
    private static final Map<String, String> comedies = new HashMap<>();
    private static final Map<String, String> actionMovies = new HashMap<>();

    public Movies() {
        detectives.put("\nОстров проклятых (2009): \n", "-http://mi.4-kfilm.cyou/1068-ostrov-prokljatyh-2009-smotret-onlajn-4k-u19.html");
        detectives.put("\nПриключения Шерлока Холмса и доктора Ватсона: Собака Баскервилей (1981) :\n", "-http://mi.4-kfilm.cyou/969-prikljuchenija-sherloka-holmsa-i-doktora-vatsona-sobaka-baskervilej-1981-smotret-onlajn-4k-u19.html");
        detectives.put("\nПрестиж (2006): \n", "-http://mi.4-kfilm.cyou/815-prestizh-2006-smotret-onlajn-4k-u19.html");
        detectives.put("\nМолчание ягнят (1990): \n", "-http://mi.4-kfilm.cyou/751-molchanie-jagnjat-1990-smotret-onlajn-4k-u19.html");
        detectives.put("\nРеинкарнация (2018):\n", "-http://mi.4-kfilm.cyou/346-reinkarnacija-2018-smotret-onlajn-4k-u23.html");

        comedies.put("\nБольшой Лебовски (1998): \n", "-http://mi.4-kfilm.cyou/651-bolshoj-lebovski-1998-smotret-onlajn-4k-u23.html");
        comedies.put("\nШоу Трумана (1998): \n", "-http://mi.4-kfilm.cyou/1472-shou-trumana-1998-smotret-onlajn-4k-u19.html");
        comedies.put("\nОчень плохая училка (2011): \n", "-http://mi.4-kfilm.cyou/1155-ochen-plohaja-uchilka-2011-smotret-onlajn-4k-u19.html");
        comedies.put("\nПоездка в Америку (1988): \n", "-http://mi.4-kfilm.cyou/889-poezdka-v-ameriku-1988-smotret-onlajn-4k-u19.html");
        comedies.put("\nОднажды в… Голливуде (2019): \n", "-http://mi.4-kfilm.cyou/482-odnazhdyvgollivude-2019-smotret-onlajn-4k4kc.html");

        drams.put("\nЗеленая миля (1999): \n", "-http://mi.4-kfilm.cyou/1502-zelenaja-milja-1999-smotret-onlajn-4k-u19.html");
        drams.put("\nФокус (2014): \n", "-http://mi.4-kfilm.cyou/271-fokus-2014-smotret-onlajn-4k-u19.html");
        drams.put("\nВолк с Уолл-стрит (2013): \n", "-http://mi.4-kfilm.cyou/1426-volk-s-uoll-strit-2013-smotret-onlajn-4k-u19.html");
        drams.put("\nЧерный ящик (2021): \n", "-http://mi.4-kfilm.cyou/1480-chernyj-jaschik-2021-smotret-onlajn-4k-u19.html");
        drams.put("\nПобег из Шоушенка (1994): \n", "-http://mi.4-kfilm.cyou/992-pobeg-iz-shoushenka-1994-smotret-onlajn-4k-u23.html");

        horrors.put("\nАмериканский оборотень в Лондоне (1981): \n", "-http://mi.4-kfilm.cyou/939-amerikanskij-oboroten-v-londone-1981-smotret-onlajn-4k-u19.html");
        horrors.put("\nВойна миров Z (2013):\n", "-http://mi.4-kfilm.cyou/678-vojna-mirov-z-2013-smotret-onlajn-4k-u19.html");
        horrors.put("\nЗаклятие (2013):\n", "-http://mi.4-kfilm.cyou/857-zakljatie-2013-smotret-onlajn-4k-u19.html");
        horrors.put("\nКристина (1983):\n", " -http://mi.4-kfilm.cyou/1064-kristina-1983-smotret-onlajn-4k-u19.html");
        horrors.put("\nЗлое (2021): \n", "-http://mi.4-kfilm.cyou/1303-zloe-2021-smotret-onlajn-4k-u23.html");

        actionMovies.put("\nАдреналин (2006): \n", "-http://mi.4-kfilm.cyou/299-adrenalin-2006-smotret-onlajn-4k-u23.html");
        actionMovies.put("\nКрепкий орешек 2 (1990): \n", "-http://mi.4-kfilm.cyou/458-krepkij-oreshek-2-1990-smotret-onlajn-4k-u23.html");
        actionMovies.put("\nСкорая (2022): \n", "-http://mi.4-kfilm.cyou/1518-skoraja-2022-smotret-onlajn-v-4k.html");
        actionMovies.put("\nРэмбо 3 (1988): \n", "-http://mi.4-kfilm.cyou/1053-rjembo-3-1988-smotret-onlajn-4k-u23.html");
        actionMovies.put("\nНе время умирать (2021): \n", "-http://mi.4-kfilm.cyou/1348-ne-vremja-umirat-2021-smotret-onlajn-4k-u23.html");
    }

    public static String suggestMovies(String string) {
        string = string.toLowerCase(Locale.ROOT);
        String ans = "";

        string = string.contains("ужасы") ? "ужасы" : string;
        string = string.contains("комедии") ? "комедии" : string;
        string = string.contains("драмы") ? "драмы" : string;
        string = string.contains("детективы") ? "детективы" : string;
        string = string.contains("боевики") ? "боевики" : string;

        switch (string) {
            case "ужасы":
                ans = FOR(ans, horrors);
                break;
            case "комедии":
                ans = FOR(ans, comedies);
                break;
            case "драмы":
                ans = FOR(ans, drams);
                break;
            case "детективы":
                ans = FOR(ans, detectives);
                break;
            case "боевики":
                ans = FOR(ans, actionMovies);
                break;
            default:
                ans = null;
                break;
        }

        return ans;
    }

    private static String FOR(final String ans, final Map<String, String> typeOfMovie) {
        StringBuilder ansBuilder = new StringBuilder(ans);

        for (Map.Entry<String, String> nameAndURL : typeOfMovie.entrySet()) {
            ansBuilder.append(nameAndURL.getKey()).append(nameAndURL.getValue());
        }

        return ansBuilder.toString();
    }
}
