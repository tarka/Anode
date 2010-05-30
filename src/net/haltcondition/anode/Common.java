package net.haltcondition.anode;

/**
 * Copyright Steve Smith (tarkasteve@gmail.com): 29/05/2010
 */
public final class Common
{
    static final String INODE_URI_BASE = "https://customer-webtools-api.internode.on.net/api/v1.5/";
    static String usageUri(Service svc) {
        return INODE_URI_BASE + svc.getServiceId() + "/usage";
    }

    static final Long GB = 1000000000L;

    static final String USAGE_ALARM = "net.haltcondition.anode.broadcast.USAGE_ALARM";
    static final String SETTINGS_UPDATE = "net.haltcondition.anode.broadcast.CONFIG_CHANGED";
    static final String USAGE_UPDATE = "net.haltcondition.anode.broadcast.USAGE_UPDATE";
}
