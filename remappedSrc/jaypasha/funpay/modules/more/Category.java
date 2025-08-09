package jaypasha.funpay.modules.more;

/*
 * Create by puzatiy
 * At 03.06.2025
 */

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import net.minecraft.util.Identifier;

@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum Category {

    Combat(Identifier.of("funpay", "images/combat.png")),
    Movement(Identifier.of("funpay", "images/movement.png")),
    Render(Identifier.of("funpay", "images/render.png")),
    Player(Identifier.of("funpay", "images/player.png")),
    Miscellaneous(Identifier.of("funpay", "images/miscellaneous.png"));

    Identifier icon;

}
