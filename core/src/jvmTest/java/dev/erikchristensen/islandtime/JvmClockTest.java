package dev.erikchristensen.islandtime;

import org.junit.Test;

import static com.google.common.truth.Truth.assertThat;

public class JvmClockTest {
    @Test
    public void system_instant() {
        assertThat(Clock.system().instant()).isGreaterThan(0);
    }

    @Test
    public void system_timeZone() {
        assertThat(Clock.system().getTimeZone()).isNotNull();
    }

//    @Test
//    public void fixedClock_sanityCheck() {
//        FixedClock fixedClock = new FixedClock(5L);
//        assertThat(fixedClock.instant()).isEqualTo(5L);
//        assertThat(fixedClock.getTimeZone()).isEqualTo(TimeZone.UTC);
//
//        fixedClock = new FixedClock(5L, new TimeZone("Etc/UTC"));
//        assertThat(fixedClock.instant()).isEqualTo(5L);
//        assertThat(fixedClock.getTimeZone()).isEqualTo(new TimeZone("Etc/UTC"));
//    }
}