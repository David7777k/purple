package jaypasha.funpay.api.commands;

import jaypasha.funpay.Api;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.List;

@Getter
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public abstract class CommandLayer implements Api {

    List<String> commands;

    List<ArgumentLayer> arguments = new ArrayList<>();

    public abstract void execute(List<String> arguments);

}
