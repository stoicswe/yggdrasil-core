package org.yggdrasil.core.utils;

import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 * The DateTimeUtil class is used for getting the current date and time in UTC
 * format.
 *
 * @since 0.0.1
 * @author nathanielbunch
 */
public class DateTimeUtil {

    private static final String _ZONE_ID = "UTC";

    public static ZonedDateTime getCurrentTimestamp() {
        return ZonedDateTime.now().withZoneSameInstant(ZoneId.of(_ZONE_ID));
    }

}
