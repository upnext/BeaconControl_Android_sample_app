/*
 * Copyright (c) 2016, Upnext Technologies Sp. z o.o.
 * All rights reserved.
 *
 * This source code is licensed under the BSD 3-Clause License found in the
 * LICENSE.txt file in the root directory of this source tree.
 */

package io.upnext.beaconcontrol.app.beacons;

import java.util.Comparator;

import io.upnext.beaconcontrol.sdk.Beacon;
import io.upnext.beaconcontrol.sdk.Beacon.Proximity;

public class BeaconComparator implements Comparator<Beacon> {
    @Override
    public int compare(Beacon b1, Beacon b2) {
        final Proximity p1 = b1.currentProximity;
        final Proximity p2 = b2.currentProximity;
        if (p1 == p2) {
            if (p1 != Proximity.OUT_OF_RANGE) {
                return (int) Math.signum(b1.distance - b2.distance);
            } else {
                return 0;
            }
        }
        if ((p1 == Proximity.IMMEDIATE) ||
                (p1 == Proximity.NEAR && p2 != Proximity.IMMEDIATE) ||
                (p1 == Proximity.FAR && p2 != Proximity.IMMEDIATE && p2 != Proximity.NEAR)) {
            return -1;
        }
        return 1;
    }

    public static boolean areEqual(Beacon b1, Beacon b2) {
        return b1 != null && b2 != null && b1.id.equals(b2.id);
    }
}
