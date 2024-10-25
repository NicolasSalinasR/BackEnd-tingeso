package backend.backend.Service;

import backend.backend.Entity.ClientEntity;
import backend.backend.Repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service

public class ClientService {


    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private HistoryCountService historyCountService;

    // P2: Creates a new client and saves it in the repository
    public ClientEntity createClient(ClientEntity client) {
        return clientRepository.save(client);
    }

    // Retrieves a client from the repository by its ID
    public ClientEntity getClientById(long id) {
        return clientRepository.findById(id);
    }

    /**
     * Retrieves a client entity from the database using the RUT.
     *
     * @param rut The RUT of the client to be retrieved.
     * @return The ClientEntity object if found, otherwise null.
     */
    public ClientEntity getClient(String rut) {
        return clientRepository.findByRut(rut);
    }

    /**
     * P1
     * Calculates the monthly payment (installment) for a simulated loan using the formula for an amortizing loan.
     *
     * @param amount        The loan amount.
     * @param termYears     The term of the loan in years.
     * @param annualInterest The annual interest rate as a decimal (e.g., 0.05 for 5%).
     * @return The monthly payment amount.
     */
    public int simulateLoanAmount(int amount, int termYears, double annualInterest) {
        // Monthly interest rate
        double monthlyRate = annualInterest / 12;
        // Total number of months for the loan term
        int totalMonths = termYears * 12;
        // Calculate the monthly rate + 1 for the amortization formula
        float monthlyRatePlusOne = (float) (monthlyRate + 1);

        System.out.println("yo deberia ser 0.00375 " + monthlyRate);
        System.out.println("yo deberia ser 240 " + totalMonths);

        // Power term for the amortization formula
        float powerTerm = (float) Math.pow(monthlyRatePlusOne, totalMonths);




        // Numerator of the formula
        double numerator = (monthlyRate * powerTerm);
        System.out.println("yo deberia ser: 0.0092079989" +numerator);

        // Denominator of the formula
        double denominator = powerTerm - 1;



        // Return the calculated monthly payment amount
        double D =   (numerator / denominator);

        System.out.println("yo deberia ser: 0.0063264938 " + D);

        double N = amount * D;
        int J = (int) N;
        return J;
    }

    /**
     * P6
     * Calculates the total cost of a loan including insurance and administration fees.
     *
     * @param amount              The loan amount.
     * @param termYears           The loan term in years.
     * @param annualInterest      The annual interest rate.
     * @param lifeInsurance       The life insurance cost as a percentage of the loan amount.
     * @param fireInsurance       The fixed cost of fire insurance.
     * @param adminFee            The administration fee as a percentage of the loan amount.
     * @return The total loan cost over the entire term.
     */
    public int totalCostP6(int amount, int termYears, double annualInterest, double lifeInsurance, double fireInsurance, double adminFee) {
        // Calculate the total term in months
        int termMonths = termYears * 12;
        // Calculate the monthly loan payment
        double monthlyLoan = simulateLoanAmount(amount, termYears, annualInterest);
        // Calculate the life insurance cost (percentage of the loan amount)
        double lifeInsuranceCost = lifeInsurance * amount;
        // Calculate the administration fee
        double fee = adminFee * amount;
        // Total monthly cost including loan payment, fire insurance, and life insurance
        double totalMonthlyCost = monthlyLoan + fireInsurance + lifeInsuranceCost;
        // Return the total cost over the entire loan term plus the administration fee

        double total =  (totalMonthlyCost * termMonths) + fee;

        return (int) total;
    }

    /**
     * R1: Determines if a client is eligible for a loan based on their salary and the loan amount.
     *
     * @param Id The RUT of the client.
     * @param M   The loan amount.
     * @return true if the loan amount does not exceed 35% of the client's salary.
     */
    public boolean R1(long Id, int amount, int termYears, double annualInterest) {
        int M = simulateLoanAmount( amount, termYears,  annualInterest);;


        // Retrieve client by RUT
        ClientEntity client = clientRepository.findById(Id);
        // Get client's salary
        int salary = client.getSalary();

        // Calculate the loan-to-salary percentage
        double result = (M / salary) * 100;

        // Return true if the loan amount exceeds 35% of the salary
        return !(result < 35);
    }

    /**
     * R2: Checks if a client has a bad credit record (Dicom).
     *
     * @param clientId The ID of the client.
     * @return true if the client has a bad credit record, false otherwise.
     */
    public boolean R2(long clientId) {
        // Retrieve client by ID
        ClientEntity client = clientRepository.findById(clientId);
        // Check if the client has a bad credit record (Dicom)
        boolean dicom = client.GetDicom();
        if (!dicom) {
            return false;
        }
        return true;
    }

    /**
     * R3: Checks if a client has been employed for more than one year.
     *
     * @param clientId The ID of the client.
     * @return true if the client has more than one year of employment, false otherwise.
     */
    public boolean R3(long clientId) {
        // Retrieve client by ID
        ClientEntity client = clientRepository.findById(clientId);
        // Check the client's job tenure in years
        int jobTenure = client.getJobTenure();
        if (jobTenure > 1) {
            return true;
        }
        return false;
    }

    /**
     * R4: Determines if the debt-to-income ratio is acceptable.
     *
     * @param clientId The ID of the client.
     * @param debt     The total debt of the client.
     * @param amount   The loan amount.
     * @return true if the debt-to-income ratio is less than 50%.
     */
    public boolean R4(long clientId, int debt, int amount) {
        // Calculate the total debt after adding the loan
        int sum = amount + debt;
        // Retrieve client by ID
        ClientEntity client = clientRepository.findById(clientId);
        // Get the client's salary
        int salary = client.getSalary();

        // Calculate the debt-to-income ratio
        double ratio = salary / sum;

        // Return true if the ratio is acceptable (greater than 50%)
        if (ratio < 0.5) {
            return false;
        }
        return true;
    }

    /**
     * R5: Determines if the loan amount is appropriate based on the property type.
     *
     * @param type    The property type (1 = first house, 2 = second house, etc.).
     * @param cost    The cost of the property.
     * @param loan    The loan amount.
     * @return true if the loan covers a sufficient percentage of the cost for the property type.
     */
    public boolean R5(int type, int cost, int loan) {
        // Type 1: First house (80% loan coverage)
        if (type == 1) {
            double maxLoan = loan * 0.8;
            if (maxLoan < cost) {
                return true;
            }
            return false;
        }
        // Type 2: Second house (70% loan coverage)
        if (type == 2) {
            double maxLoan = loan * 0.7;
            if (maxLoan < cost) {
                return true;
            }
            return false;
        }
        // Type 3: Commercial property (60% loan coverage)
        if (type == 3) {
            double maxLoan = loan * 0.6;
            if (maxLoan < cost) {
                return true;
            }
            return false;
        }
        // Type 4: Remodeling (50% loan coverage)
        if (type == 4) {
            double maxLoan = loan * 0.5;
            if (maxLoan < cost) {
                return true;
            }
            return false;
        }
        return false;
    }

    /**
     * R6: Checks if the client's age is below 70.
     *
     * @param id The ID of the client.
     * @return true if the client is younger than 70, false otherwise.
     */
    public boolean R6(long id) {
        // Retrieve client by ID
        ClientEntity client = clientRepository.findById(id);
        // Get client's age
        int age = client.getAge();
        if (age > 70) {
            return false;
        }

        return true;
    }

    public List<Boolean> Rcomplete (Long ClientId, int type,  int loan, int debt, int amount, int older, int termYears, double annualInterest) {
        List<Boolean> ListReturn = new ArrayList<Boolean>();
        boolean ResultR1 = R1(ClientId,  amount,  termYears, annualInterest);
        boolean ResultR2 = R2(ClientId);
        boolean ResultR3 = R3(ClientId);
        boolean ResultR4 = R4(ClientId,debt,amount);
        boolean ResultR5 = R5(type,amount,loan);
        boolean ResultR6 = R6(ClientId);
        boolean ResultR7 = historyCountService.R7Complete(ClientId,older,amount);
        ListReturn.add(ResultR1);
        ListReturn.add(ResultR2);
        ListReturn.add(ResultR3);
        ListReturn.add(ResultR4);
        ListReturn.add(ResultR5);
        ListReturn.add(ResultR6);
        ListReturn.add(ResultR7);
        return ListReturn;

    }



}


