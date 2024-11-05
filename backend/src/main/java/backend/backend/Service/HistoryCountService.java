package backend.backend.Service;

import backend.backend.Entity.ClientEntity;
import backend.backend.Entity.HistoryCountEntity;
import backend.backend.Repository.ClientRepository;
import backend.backend.Repository.HistoryCountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service

public class HistoryCountService {
    @Autowired
    HistoryCountRepository historyCountRepository;

    @Autowired
    ClientRepository clientRepository;


    public HistoryCountEntity addHistoryCount(HistoryCountEntity historyCountEntity) {

        if (historyCountEntity == null) {
            throw new IllegalArgumentException("El parámetro historyCount no puede ser null.");
        }
        return historyCountRepository.save(historyCountEntity);
    }

    public HistoryCountEntity getHistoryCount(Long id) {


        if (id == null) {
            throw new IllegalArgumentException("El parámetro id no puede ser null.");
        }
        return historyCountRepository.findById(id).orElse(null);
    }


    public boolean R71 (long clientId, int amount){
        List<HistoryCountEntity> historyCounts = historyCountRepository.findAllByClientid(clientId);
        int sum = 0;
        for (HistoryCountEntity historyCount : historyCounts) {
            int change = historyCount.getChange();
            sum += change;
        }
        double tenPercent = amount * 0.1;
        if (sum >= tenPercent) {
            return true;
        }
        return false;
    }

    public boolean R72 (long clientId){
        List<HistoryCountEntity> historyCounts = historyCountRepository.findAllByClientid(clientId);
        int totalOfMoney = 0;
        for (HistoryCountEntity historyCount : historyCounts) {
            int change = historyCount.getChange();
            totalOfMoney += change;
        }

        double halfOfMoneyAux =  (double) totalOfMoney / 2;
        int halfOfMoney = (int) halfOfMoneyAux;



        List<HistoryCountEntity> newList = filterObjectsLast12Months(historyCounts);
        int sum = 0;
        for (HistoryCountEntity historyCount : newList) {
            int change = historyCount.getChange();
            if (change > halfOfMoney) {
                return false;
            }

            sum += change;

            int comparation = sum - halfOfMoney;
            if (comparation == 0) {
                return false;
            }

        }
        return true;


    }

    public boolean R73(long clientId) {
        // Obtener el historial de transacciones del cliente
        List<HistoryCountEntity> historyCounts = historyCountRepository.findAllByClientid(clientId);

        // Filtrar los objetos de los últimos 12 meses
        List<HistoryCountEntity> newList = filterObjectsLast12Months(historyCounts);

        boolean condition1 = false;
        int totalOfMoney = 0;

        // Calcular el total de dinero de los cambios positivos (depósitos)
        for (HistoryCountEntity historyCount : newList) {
            int change = historyCount.getChange();
            if (change > 0) {
                totalOfMoney += change;
            }
        }

        // Obtener el cliente actual
        Optional<ClientEntity> clientOpt = Optional.ofNullable(clientRepository.findById(clientId)); // Cambiado a Optional

        // Verificar si el cliente está presente
        if (clientOpt.isPresent()) {
            ClientEntity clientActualy = clientOpt.get(); // Obtener el cliente

            // Obtener el salario del cliente actual
            int Money = clientActualy.getSalary();

            // Verificar la primera condición (si el total de dinero es al menos el 5% del salario)
            if (totalOfMoney >= Money * 0.05) {
                condition1 = true;
            }

            // Comprobar si se realizaron depósitos trimestrales
            boolean quarterlyDeposits = hasQuarterlyDeposits(newList);

            // Retornar true si condition1 es true y se realizaron depósitos trimestrales
            return condition1 && quarterlyDeposits;
        } else {
            // Manejar el caso donde el cliente no existe (puedes lanzar una excepción o retornar false)
            return false;
        }
    }


    public boolean R74(long clientId, int older, int amount) {
        List<HistoryCountEntity> historyCounts = historyCountRepository.findAllByClientid(clientId);
        int sum = 0;
        for (HistoryCountEntity historyCount : historyCounts) {
            int change = historyCount.getChange();
            sum += change;
        }
        if (older >= 2 ){
            if(sum > amount*0.1){
                return true;
            }
            return false;
        }
        if (older < 2){
            if(sum > amount*0.2){
                return true;
            }
            return false;

        }
        return false;
    }
    public boolean R75 (long clientId ) {
        List<HistoryCountEntity> historyCounts = historyCountRepository.findAllByClientid(clientId);
        int sum = 0;
        for (HistoryCountEntity historyCount : historyCounts) {
            int change = historyCount.getChange();
            sum += change;
        }
        List<HistoryCountEntity> newList = filterObjectsLast6Months(historyCounts);
        for(HistoryCountEntity historyCount : newList){
            int change = historyCount.getChange();
            if(change < 0  ){
                change = change * -1;
                if( change >= sum *0.3){
                    return false;
                }
            }
        }
        return true;

    }



    public boolean R7Complete(long clientId, int older, int amount) {
        boolean condition1 = R71(clientId,amount);
        boolean condition2 = R72(clientId);
        boolean condition3 = R73(clientId);
        boolean condition4 = R74(clientId, older, amount);
        boolean condition5 = R75(clientId);
        if (condition1 && condition2 && condition3 && condition4 && condition5) {
            return true;
        }
        return false;
    }














    

    // Función para verificar si se realizaron depósitos trimestrales
    public boolean hasQuarterlyDeposits(List<HistoryCountEntity> transactions) {
        // Filtrar los depósitos (Change > 0) y agrupar por año y trimestre
        Map<Integer, Map<Integer, List<HistoryCountEntity>>> depositsByQuarter = transactions.stream()
                .filter(transaction -> transaction.getChange() > 0)  // Filtrar los depósitos
                .collect(Collectors.groupingBy(transaction -> {
                    LocalDate changeDate = transaction.getChangeDate().toInstant()
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate();
                    return changeDate.getYear(); // Agrupar por año
                }, Collectors.groupingBy(transaction -> {
                    LocalDate changeDate = transaction.getChangeDate().toInstant()
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate();
                    int month = changeDate.getMonthValue();
                    return (month - 1) / 3 + 1;  // Agrupar por trimestre
                })));

        // Verificar si hay al menos un depósito en cada trimestre del año
        return depositsByQuarter.values().stream()
                .allMatch(quarterMap -> quarterMap.size() >= 1);  // Verifica si cada trimestre tiene al menos 1 depósito
    }



















    // Método para filtrar objetos cuya fecha es de los últimos 12 meses
    public static List<HistoryCountEntity> filterObjectsLast12Months(List<HistoryCountEntity> objects) {
        // Obtener la fecha actual
        LocalDate today = LocalDate.now();
        // Calcular la fecha de hace 12 meses
        LocalDate twelveMonthsAgo = today.minus(12, ChronoUnit.MONTHS);

        // Filtrar la lista
        return objects.stream()
                .filter(obj -> {
                    // Convertir el Timestamp a LocalDate si `getChangeDate()` devuelve un Timestamp
                    LocalDate objectDate = obj.getChangeDate().toInstant()
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate();

                    // Comparar la fecha del objeto con la fecha de hace 12 meses
                    return objectDate.isAfter(twelveMonthsAgo);
                })
                .collect(Collectors.toList());
    }



    // Método para filtrar objetos cuya fecha es de los últimos 6 meses
    public static List<HistoryCountEntity> filterObjectsLast6Months(List<HistoryCountEntity> objects) {
        // Obtener la fecha actual
        LocalDate today = LocalDate.now();
        // Calcular la fecha de hace 6 meses
        LocalDate sixMonthsAgo = today.minus(6, ChronoUnit.MONTHS);

        // Filtrar la lista
        return objects.stream()
                .filter(obj -> {
                    // Convertir el Timestamp a LocalDate si `getChangeDate()` devuelve un Timestamp
                    LocalDate objectDate = obj.getChangeDate().toInstant()
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate();

                    // Comparar la fecha del objeto con la fecha de hace 6 meses
                    return objectDate.isAfter(sixMonthsAgo);
                })
                .collect(Collectors.toList());
    }


}
