package backend.backend.Controller;


import backend.backend.Entity.ClientEntity;
import backend.backend.Entity.HistoryCountEntity;

import backend.backend.Service.HistoryCountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/HistoryCount")
@CrossOrigin(origins = "*")


public class HistoryCountController {
    @Autowired
    private HistoryCountService historyCountService;

    @PostMapping("/add")
    public HistoryCountEntity addHistoryCount(@RequestBody Map<String, String> body) {
        long clientid = Long.parseLong(body.get("clientid"));
        int change = Integer.parseInt(body.get("change"));
        Timestamp changeDate = Timestamp.valueOf(LocalDateTime.now());

        // Crear la nueva instancia de HistoryCountEntity
        HistoryCountEntity historyCount = new HistoryCountEntity();
        historyCount.setClientid(clientid);
        historyCount.setChange(change);
        historyCount.setChangeDate(changeDate);

        // Guardar la nueva entidad a través del servicio
        return historyCountService.addHistoryCount(historyCount);

    }
    @GetMapping("/{id}")
    public HistoryCountEntity getClienteyId(@PathVariable Long id) {
        return historyCountService.getHistoryCount(id);
    }







    }



