package py.edu.uc.lp3.me.cs2.Controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class IndexController {

	@GetMapping("/")
	public Map<String, String> index() {
	   return Map.of(
		 "autor", "Matias Enciso",
		"dominio", "CS2",
		"estado", "API funcionando"
	   );
      }
}
