package com.zondy.mapgis.utilities;

import com.zondy.mapgis.geometry.Geometry;

import java.util.Map;

/**
 * @author cxy
 * @date 2020/05/19
 */
public final class Check {
    private static final double MIN_DOUBLE_PRECISION_VALUE = 1.0E-4D;

    public Check() {
    }

    public static void throwIfNull(Object value, String name) {
        if (value == null) {
            throw new IllegalArgumentException(String.format("Parameter %s must not be null", name));
        }
    }

    public static void throwIfNullOrEmpty(String value, String name) {
        throwIfNull(value, name);
        if (value.isEmpty()) {
            throw new IllegalArgumentException(String.format("Parameter %s must not be empty", name));
        }
    }

    public static void throwIfNullOrEmpty(Iterable<?> value, String name) {
        throwIfNull(value, name);
        if (!value.iterator().hasNext()) {
            throw new IllegalArgumentException(String.format("Parameter %s must not be empty", name));
        }
    }

    public static void throwIfNullOrEmpty(Map<?, ?> value, String name) {
        throwIfNull(value, name);
        if (value.isEmpty()) {
            throw new IllegalArgumentException(String.format("Parameter %s must not be empty", name));
        }
    }

    public static void throwIfNullOrEmpty(byte[] value, String name) {
        throwIfNull(value, name);
        if (value.length == 0) {
            throw new IllegalArgumentException(String.format("Parameter %s must not be empty", name));
        }
    }

    public static void throwIfNullOrEmpty(double[] value, String name) {
        throwIfNull(value, name);
        if (value.length == 0) {
            throw new IllegalArgumentException(String.format("Parameter %s must not be empty", name));
        }
    }

    public static void throwIfNullOrEmpty(Geometry value, String name) {
        throwIfNull(value, name);
        if (value.isEmpty()) {
            throw new IllegalArgumentException(String.format("Parameter %s must not be empty", name));
        }
    }

    public static void throwIfMissingFileExtension(String filePath, String fileExtension, String parameterName) {
        throwIfNullOrEmpty(filePath, parameterName);
        if (!filePath.toLowerCase().endsWith(fileExtension)) {
            throw new IllegalArgumentException(parameterName + " must have file extension '" + fileExtension + "'");
        }
    }

    public static void throwIfNegative(double value, String name) {
        if (value < 0.0D) {
            throw new IllegalArgumentException(String.format("Parameter %s must be 0 or greater", name));
        }
    }

    public static void throwIfZeroOrNegative(double value, String name) {
        if (value <= 0.0D) {
            throw new IllegalArgumentException(String.format("Parameter %s must be greater than 0", name));
        }
    }

    public static void throwIfNegative(int value, String name) {
        if (value < 0) {
            throw new IllegalArgumentException(String.format("Parameter %s must be 0 or greater", name));
        }
    }

    public static void throwIfNegative(float value, String name) {
        if (value < 0.0F) {
            throw new IllegalArgumentException(String.format("Parameter %s must be 0 or greater", name));
        }
    }

    public static void throwIfNotInRange(double value, String name, double min, double max) {
        if (value < min) {
            throw new IllegalArgumentException(String.format(String.format("Parameter %1$s must not be less than %2$s", name, Math.abs(min) < 1.0E-4D ? "%.6E" : "%f"), min));
        } else if (value > max) {
            throw new IllegalArgumentException(String.format(String.format("Parameter %1$s must not be greater than %2$s", name, Math.abs(max) < 1.0E-4D ? "%.6E" : "%f"), max));
        }
    }

    public static void throwIfNotInRange(long value, String name, long min, long max) {
        if (value < min || value > max) {
            throw new IllegalArgumentException(String.format("Parameter %s is out of bounds", name));
        }
    }

    public static void throwIfIndexOutOfBounds(double value, String name, double min, double max) {
        if (value < min || value > max) {
            throw new IndexOutOfBoundsException(String.format("Parameter %s is out of bounds", name));
        }
    }

    public static void throwIfThreadIsInterrupted(Thread thread, String message) throws InterruptedException {
        if (thread.isInterrupted()) {
            throw new InterruptedException(message);
        }
    }

    public static void throwIfSmaller(long value, String name, long lowerLimit) {
        if (value < lowerLimit) {
            throw new IllegalArgumentException(String.format(String.format("Parameter %1$s must not be less than %2$s", name, "%d"), lowerLimit));
        }
    }

    public static void throwIfSmaller(double value, String name, double lowerLimit) {
        if (value < lowerLimit) {
            throw new IllegalArgumentException(String.format(String.format("Parameter %1$s must not be less than %2$s", name, Math.abs(lowerLimit) < 1.0E-4D ? "%.6E" : "%f"), lowerLimit));
        }
    }

    public static void throwIfLengthOfArrayIsInvalid(double[] value, String name, int validLength) {
        if (value.length != validLength) {
            throw new IllegalArgumentException(String.format(String.format("Parameter %1$s length must equal %2$s", name, "%d"), validLength));
        }
    }

    public static void throwIfNotValidUtilityNetworkAttributeDataType(Object value, String name) {
        if (!(value instanceof Integer) && !(value instanceof Float) && !(value instanceof Double) && !(value instanceof Boolean)) {
            throw new IllegalArgumentException(String.format("Parameter %1$s must be a valid network attribute data type", name));
        }
    }
}
