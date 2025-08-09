package jaypasha.funpay.api.commands.list.gps;

import jaypasha.funpay.api.commands.ArgumentLayer;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class AddArgumentLayer extends ArgumentLayer {

    public AddArgumentLayer() {
        super("add", 0);
    }

    @Override
    public void execute(List<String> arguments) {
        if (arguments.size() < 4) {
            print("Не так не получится братан");
            return;
        }

        if (GpsRepository.getGps().stream().anyMatch(e -> e.getName().equalsIgnoreCase(arguments.getFirst()))) {
            print("Такая метка уже существует.");
            return;
        }

        GpsRepository.WayPoint wayPoint = getWayPoint(arguments);
        GpsRepository.getGps().add(wayPoint);
        print("Успешно добавлена метка " + wayPoint.getName() + " с координатами " + wayPoint.getX() + ", " + wayPoint.getY() + ", " + wayPoint.getZ());
    }

    private static GpsRepository.@NotNull WayPoint getWayPoint(List<String> arguments) {
        int x, y, z;

        if (isInteger(arguments.get(1)) && isInteger(arguments.get(2)) && isInteger(arguments.get(3))) {
            x = Integer.parseInt(arguments.get(1));
            y = Integer.parseInt(arguments.get(2));
            z = Integer.parseInt(arguments.get(3));
        } else {
            x = (int) mc.player.getX();
            y = (int) mc.player.getY();
            z = (int) mc.player.getZ();
        }

        return new GpsRepository.WayPoint(arguments.getFirst(), x, y, z);
    }

    public static boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
