package backend.backend.Controller;


import backend.backend.Entity.RequestEntity;
import backend.backend.Service.RequestService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/Request")
@CrossOrigin(origins = "*")

public class RequestController {

    @Autowired
    private RequestService requestService;

    @PostMapping("/create")
    public ResponseEntity<RequestEntity> createRequest(
            @RequestParam("typeOfRequest") String typeOfRequest,
            @RequestParam("stage") int stage,
            @RequestParam("Amount") int Amount,
            @RequestParam("termYears") int termYears,
            @RequestParam("clientId") long clientId,
            @RequestParam("pdfFile") MultipartFile pdfFile) {
        try {
            // Convertir el archivo PDF a un arreglo de bytes
            byte[] pdfData = pdfFile.getBytes();

            // Crear la entidad de solicitud
            RequestEntity request = requestService.createRequest(typeOfRequest, stage, Amount, termYears, clientId, pdfData);

            return new ResponseEntity<>(request, HttpStatus.CREATED);
        } catch (IOException e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/updateStage")
    public ResponseEntity<String> updateStage(@RequestBody Map<String, String> body
          ) {
        long ClientId= Long.parseLong(body.get("Id"));
        int Stage= Integer.parseInt(body.get("Stage"));

        try {
            // Llamar al servicio para actualizar el Stage
            requestService.updateStage(ClientId, Stage);
            return ResponseEntity.ok("Stage updated successfully for Request ID: " + ClientId);
        } catch (EntityNotFoundException e) {
            // Manejar el caso donde la solicitud no se encuentra
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }



    @GetMapping("/get/{id}")
    public ResponseEntity<RequestEntity> getRequestById(@PathVariable long id) {
        try {
            RequestEntity request = requestService.getRequestById(id);
            return new ResponseEntity<>(request, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/getAll/ClientId")
    public ResponseEntity<List<RequestEntity>> getAllByClientId(@RequestBody Map<String, String> body) {
        Long ClientId= Long.parseLong(body.get("ClientId"));
        List<RequestEntity> requests = requestService.GetAllRequestsByClientId(ClientId);
        if (requests.isEmpty()) {
            return ResponseEntity.noContent().build(); // Devuelve un 204 No Content si no hay resultados
        }
        return ResponseEntity.ok(requests); // Devuelve un 200 OK con la lista de solicitudes
    }


    @GetMapping("/getAll")
    public ResponseEntity<List<RequestEntity>> getAll() {
        return ResponseEntity.ok(requestService.GetAllRequests());
    }







}
