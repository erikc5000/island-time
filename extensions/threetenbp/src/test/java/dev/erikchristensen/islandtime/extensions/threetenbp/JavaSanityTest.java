package dev.erikchristensen.islandtime.extensions.threetenbp;

import dev.erikchristensen.islandtime.Month;
import dev.erikchristensen.islandtime.date.Date;
import org.junit.Test;
import org.threeten.bp.LocalDate;

import static com.google.common.truth.Truth.assertThat;

public class JavaSanityTest {
    @Test
    public void convertIslandDateToJavaLocalDate() {
        Date islandDate = new Date(2019, Month.MARCH, 1);
        LocalDate javaDate = IslandTime.convertToJava(islandDate);

        assertThat(javaDate.getYear()).isEqualTo(islandDate.getYear());
        assertThat(javaDate.getMonthValue()).isEqualTo(islandDate.getMonth().getNumber());
        assertThat(javaDate.getDayOfMonth()).isEqualTo(islandDate.getDayOfMonth());
    }

    @Test
    public void convertJavaLocalDateToIslandDate() {
        LocalDate javaDate = LocalDate.of(2019, 3, 1);
        Date islandDate = IslandTime.convertFromJava(javaDate);

        assertThat(islandDate.getYear()).isEqualTo(javaDate.getYear());
        assertThat(islandDate.getMonth().getNumber()).isEqualTo(javaDate.getMonthValue());
        assertThat(islandDate.getDayOfMonth()).isEqualTo(javaDate.getDayOfMonth());
    }
}