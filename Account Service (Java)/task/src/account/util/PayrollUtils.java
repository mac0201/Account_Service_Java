package account.util;

import java.text.SimpleDateFormat;

public class PayrollUtils {

    // create date formatters for input and output

    public static final SimpleDateFormat periodInputFormatter = new SimpleDateFormat("MM-yyyy");
    public static final SimpleDateFormat periodOutputFormatter = new SimpleDateFormat("MM-yyyy");
    public static final String PAYROLL_REGEX = "^((0[1-9])|(1[0-2]))-(\\d{4})$";
    public static final String PAYROLL_REGEX_ERROR = "";
    // create period format regex


}
