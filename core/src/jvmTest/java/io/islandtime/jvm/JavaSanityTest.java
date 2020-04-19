package io.islandtime.jvm;

import io.islandtime.Date;
import io.islandtime.Month;
import org.junit.Test;

import java.time.LocalDate;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalQueries;

import static com.google.common.truth.Truth.assertThat;
import static io.islandtime.jvm.Conversions.*;

public class JavaSanityTest {
    @Test
    public void convertIslandDateToJavaLocalDate() {
        Date islandDate = new Date(2019, Month.MARCH, 1);
        LocalDate javaDate = toJavaLocalDate(islandDate);

        assertThat(javaDate.getYear()).isEqualTo(islandDate.getYear());
        assertThat(javaDate.getMonthValue()).isEqualTo(islandDate.getMonth().getNumber());
        assertThat(javaDate.getDayOfMonth()).isEqualTo(islandDate.getDayOfMonth());
    }

    @Test
    public void convertJavaLocalDateToIslandDate() {
        LocalDate javaDate = LocalDate.of(2019, 3, 1);
        Date islandDate = toIslandDate(javaDate);

        assertThat(islandDate.getYear()).isEqualTo(javaDate.getYear());
        assertThat(islandDate.getMonth().getNumber()).isEqualTo(javaDate.getMonthValue());
        assertThat(islandDate.getDayOfMonth()).isEqualTo(javaDate.getDayOfMonth());
    }

    @Test
    public void convertIslandDateToJavaTemporalAccessor() {
        Date islandDate = new Date(2020, Month.MAY, 20);
        TemporalAccessor temporalAccessor = toJavaTemporalAccessor(islandDate);
        assertThat(temporalAccessor.query(TemporalQueries.localDate()))
                .isEqualTo(LocalDate.of(2020, java.time.Month.MAY, 20));
    }
}