package fr.niware.uhcrun.utils;

public class Orientation {

    public String getOrientation(double x, double z, double yawP) {
        double oppose = z;
        oppose = Math.sqrt(oppose * oppose);
        double adjacent = x;
        adjacent = Math.sqrt(adjacent * adjacent);
        double angle = Math.atan(oppose / adjacent) * 57.29577951308232D;
        int playerOrientation = 0;
        if (0.0D >= x) {
            playerOrientation = getPlayerOrientation(z, angle, playerOrientation, 2, 3, 1);
        } else if (0.0D < x) {
            playerOrientation = getPlayerOrientation(z, angle, playerOrientation, 6, 5, 7);
        }

        double yaw = (yawP - 90.0F) % 360.0F;
        if (yaw < 0.0D) {
            yaw += 360.0D;
        }

        int seeOrientation = getSeeOrientation(yaw);
        int pointOrientation = playerOrientation - seeOrientation;
        return getArrow(pointOrientation);
    }

    private String getArrow(int pointOrientation) {
        String object = "";
        switch (pointOrientation) {
            case -7:
            case 1:
                object = object + "⬈";
                break;
            case -6:
            case 2:
                object = object + "➡";
                break;
            case -5:
            case 3:
                object = object + "⬊";
                break;
            case -4:
            case 4:
                object = object + "⬇";
                break;
            case -3:
            case 5:
                object = object + "⬋";
                break;
            case -2:
            case 6:
                object = object + "⬅";
                break;
            case -1:
            case 7:
                object = object + "⬉";
                break;
            case 0:
                object = object + "⬆";
        }

        return object;
    }

    private int getSeeOrientation(double yaw) {
        int seeOrientation = 0;
        if ((337.5D > yaw || yaw >= 360.0D) && (0.0D > yaw || yaw > 22.5D)) {
            if (22.5D <= yaw && yaw < 67.5D) {
                seeOrientation = 7;
            } else if (112.5D <= yaw && yaw < 157.5D) {
                seeOrientation = 1;
            } else if (157.5D <= yaw && yaw < 202.5D) {
                seeOrientation = 2;
            } else if (202.5D <= yaw && yaw < 247.5D) {
                seeOrientation = 3;
            } else if (247.5D <= yaw && yaw < 292.5D) {
                seeOrientation = 4;
            } else if (292.5D <= yaw && yaw < 337.5D) {
                seeOrientation = 5;
            }
        } else {
            seeOrientation = 6;
        }

        return seeOrientation;
    }

    private int getPlayerOrientation(double zP, double angle, int playerOrientation, int i, int i2, int i3) {
        double z = 0.0D;
        if (z >= zP) {
            if (angle <= 30.0D) {
                playerOrientation = i;
            } else if (angle <= 60.0D) {
                playerOrientation = i2;
            } else {
                playerOrientation = 4;
            }
        } else if (angle <= 30.0D) {
            playerOrientation = i;
        } else if (angle <= 60.0D) {
            playerOrientation = i3;
        }
        return playerOrientation;
    }
}
