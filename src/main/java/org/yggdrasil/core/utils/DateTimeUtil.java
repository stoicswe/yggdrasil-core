package org.yggdrasil.core.utils;

import java.time.ZoneId;
import java.time.ZonedDateTime;

public class DateTimeUtil {

    private static final String _ZONE_ID = "UTC";

    public static ZonedDateTime getCurrentTimestamp() {
        return ZonedDateTime.now().withZoneSameInstant(ZoneId.of(_ZONE_ID));
    }

}
