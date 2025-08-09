package jaypasha.funpay.api.commands.list.gps;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.List;

public class GpsRepository {

    @Getter
    private static final List<WayPoint> gps = new ArrayList<>();

    @Getter
    @RequiredArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    public static class WayPoint {
        String name;
        int x, y, z;
    }
}
