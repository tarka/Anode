package net.haltcondition.anode;

/**
 * Copyright Steve Smith (tarkasteve@gmail.com): 29/05/2010
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3 as
 * published by the Free Software Foundation..
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
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
