package org.yggdrasil.node.network.messages.util;

import org.apache.commons.lang3.ArrayUtils;

public class DataUtil {

    public static byte[] appendBytes(byte[] base, byte[] extension) {
        return ArrayUtils.addAll(base, extension);
    }

}
