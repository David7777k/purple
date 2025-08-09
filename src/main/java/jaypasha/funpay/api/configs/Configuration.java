package jaypasha.funpay.api.configs;

import jaypasha.funpay.Api;

import java.util.List;

public interface Configuration extends Api {

    void save(String name);

    void load(String name);

    void remove(String name);

    List<String> asList();

}
