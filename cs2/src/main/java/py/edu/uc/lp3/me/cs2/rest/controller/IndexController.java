package py.edu.uc.lp3.me.cs2.rest.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
public class IndexController {

    @GetMapping("/")
    public Map<String, String> index() {
        Map<String, String> respuesta = new LinkedHashMap<>();
        respuesta.put("autor", "Matias Enciso");
        respuesta.put("dominio", "CS2");
        respuesta.put("estado", "API funcionando");
        return respuesta;
    }
}
