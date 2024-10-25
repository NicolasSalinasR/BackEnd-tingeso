package backend.backend.Controller;


import backend.backend.Entity.ClientEntity;
import backend.backend.Service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/Client")
@CrossOrigin(origins = "*")

public class ClientController {

    @Autowired
    private ClientService clientService;

    @GetMapping("/{id}")
    public ClientEntity getClienteyId(@PathVariable Long id) {
        return clientService.getClientById(id);
    }

    @PostMapping("/add")
    public ClientEntity addClient(@RequestBody Map<String, String> body) {
        String rut = body.get("rut");
        String email = body.get("email");
        String password = body.get("password");
        String firstName = body.get("firstName");
        String lastName = body.get("lastname");
        int age = Integer.parseInt(body.get("age"));
        int salary = Integer.parseInt(body.get("salary"));
        int JobTenure = 0;
        try {
            String JobTenure1 = body.get("jobTenure");
            if (JobTenure1 == null || JobTenure1.isEmpty()) {
            }else {
                JobTenure = Integer.parseInt(JobTenure1);
            }
        } catch (NumberFormatException e) {
            throw new RuntimeException(e);
        }

        //  int JobTenure = Integer.parseInt(body.get("JobTenure"));
        boolean dicom = Boolean.parseBoolean(body.get("dicom"));
        ClientEntity client = new ClientEntity(rut, password, email, firstName, lastName, age, salary, JobTenure, dicom);

        return clientService.createClient(client);
    }

    @PostMapping("/simulateLoanAmount")
    public ResponseEntity<Integer> simulateLoanAmount(@RequestBody Map<String, String> body) {
        try {
            // Verificar que los parámetros están presentes
            if (!body.containsKey("amount") || !body.containsKey("termYears") || !body.containsKey("annualInterest")) {
                return ResponseEntity.badRequest().body(null); // Retorna un código 400 si falta algún parámetro
            }

            // Parsear los valores desde el JSON
            int amount = Integer.parseInt(body.get("amount"));
            int termYears = Integer.parseInt(body.get("termYears"));
            double annualInterest = Double.parseDouble(body.get("annualInterest"));

            // Llamar al servicio para calcular el préstamo
            int result = clientService.simulateLoanAmount(amount, termYears, annualInterest);
            return ResponseEntity.ok(result);

        } catch (NumberFormatException e) {
            // Manejar la excepción si los parámetros no son válidos
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PostMapping("/totalCost")
    public ResponseEntity<Integer> totalCost(@RequestBody Map<String, String> body) {
        try {
            // Parsear los valores desde el JSON
            int amount = Integer.parseInt(body.get("amount"));
            int termYears = Integer.parseInt(body.get("termYears"));
            double annualInterest = Double.parseDouble(body.get("annualInterest"));
            double seguroDegrabacion = Double.parseDouble(body.get("seguroDegrabacion"));
            double seguroIncendio = Double.parseDouble(body.get("seguroIncendio"));
            double comision = Double.parseDouble(body.get("comision"));

            // Llamar al servicio para calcular el costo total
            int result = clientService.totalCostP6(amount, termYears, annualInterest, seguroDegrabacion, seguroIncendio, comision);
            return ResponseEntity.ok(result);

        } catch (NumberFormatException e) {
            // Manejar la excepción si los parámetros no son válidos
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PostMapping("/P4")
    public ResponseEntity<List<Boolean>> P4(@RequestBody Map<String, String> body) {
        try {
            // Extraer parámetros del cuerpo de la solicitud
            Long clientId = Long.parseLong(body.get("clientId"));

            int type = Integer.parseInt(body.get("type"));
            int loan = Integer.parseInt(body.get("loan"));
            int debt = Integer.parseInt(body.get("debt"));
            int amount = Integer.parseInt(body.get("amount"));
            int older = Integer.parseInt(body.get("older"));
            int termYears = Integer.parseInt(body.get("termYears"));
            double annualInterest = Double.parseDouble(body.get("annualInterest"));

            // Llamar al servicio con los parámetros
            List<Boolean> result = clientService.Rcomplete(clientId, type, loan, debt, amount, older, termYears, annualInterest);
            return ResponseEntity.ok(result);
        } catch (NumberFormatException e) {
            // Manejar la excepción si los parámetros no son válidos
            return ResponseEntity.badRequest().body(null);
        }
    }



}