package account.model.dto;

import account.exceptions.CustomExceptions;
import account.util.PayrollUtils;
import lombok.Data;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Month;
import java.util.Calendar;
import java.util.Date;

import static account.util.PayrollUtils.*;

@Data
public class PayrollGetDTO {
    private String name;
    private String lastname;
    private String period;
    private String salary;

    public void setSalary(long salary) {
        this.salary = formatSalary(salary);
    }

    public void setPeriod(String period) {
        this.period = formatPeriod(period);
    }

    private String formatSalary(long salary) {
        long dollars = salary / 100; // extract dollars amount
        long cents = salary % 100; // extract cents amount
        return "%s%s".formatted(dollars + " dollar(s)", cents > 0 ? " " + cents + " cent(s)" : "");
    }

    // Accept period in MM-YYYY format and return Month-YYYY, e.g. 03-2021 -> March-2021
    private String formatPeriod(String period) {
        try {
            return periodInputFormatter.format(
                    periodOutputFormatter.parse(period));
        } catch (ParseException ex) {
            throw new CustomExceptions.RequestValidationException();
        }
    }
}
