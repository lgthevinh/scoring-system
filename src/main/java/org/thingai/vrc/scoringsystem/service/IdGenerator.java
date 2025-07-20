package org.thingai.vrc.scoringsystem.service;

import org.thingai.vrc.scoringsystem.types.AllianceColor;
import org.thingai.vrc.scoringsystem.types.MatchType;

public class IdGenerator {
    public static int generateMatchId(MatchType matchType, int matchNumber) {
        byte[] idAsBytes = new byte[2];

        idAsBytes[0] = (byte) matchType.toByte();
        idAsBytes[1] = (byte) matchNumber;

        return ((idAsBytes[0] & 0xFF) << 8) | (idAsBytes[1] & 0xFF);
    }

    public static int generateAllianceId(int matchId, AllianceColor color) {
        byte[] idAsBytes = new byte[3];

        idAsBytes[0] = (byte) (matchId >> 8);
        idAsBytes[1] = (byte) (matchId & 0xFF);
        idAsBytes[2] = (byte) color.toByte();

        return ((idAsBytes[0] & 0xFF) << 16) | ((idAsBytes[1] & 0xFF) << 8) | (idAsBytes[2] & 0xFF);
    }


}
