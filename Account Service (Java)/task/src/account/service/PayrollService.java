package account.service;

import account.exceptions.CustomExceptions;
import account.model.Payroll;
import account.model.User;
import account.model.dto.PayrollCreateDTO;
import account.model.dto.PayrollGetDTO;
import account.repository.PayrollRepository;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.*;
import java.util.stream.Stream;

import static account.util.PayrollUtils.periodInputFormatter;

@Service
public class PayrollService {

    private final AuthService authService;
    private final PayrollRepository payrollRepository;
    private final ModelMapper modelMapper;

    private final Comparator<Payroll> payrollPeriodComparator = (o1, o2) -> {
        try {
            Date d1 = periodInputFormatter.parse(o1.getPeriod());
            Date d2 = periodInputFormatter.parse(o2.getPeriod());
            return d1.compareTo(d2);
        } catch (ParseException e) {
            throw new RuntimeException();
        }
    };

    public PayrollService(AuthService authService, PayrollRepository payrollRepository, ModelMapper modelMapper) {
        this.authService = authService;
        this.payrollRepository = payrollRepository;
        this.modelMapper = modelMapper;
    }

    @Transactional
    public void addPayrolls(List<PayrollCreateDTO> payrolls) {
        payrolls.forEach(payrollDTO -> {
            String empEmail = payrollDTO.getEmployee();
            User user = authService.findUser(empEmail);
            Payroll payroll = modelMapper.map(payrollDTO, Payroll.class);
            payroll.setEmployee(user);
            payrollRepository.save(payroll);
        });
    }

    @Transactional
    public void updatePayroll(PayrollCreateDTO dto) {
        // check if employee exists
        String empEmail = dto.getEmployee();
        User user = authService.findUser(empEmail);
        Payroll payroll = payrollRepository.findByEmployeeIdAndPeriod(user.getId(), dto.getPeriod())
                .orElseThrow(() -> new CustomExceptions.PayrollNotFoundException("Payroll not found - email: %s, period: %s".formatted(empEmail, dto.getPeriod())));
        payroll.setSalary(dto.getSalary());
        payrollRepository.save(payroll);
    }

    @Transactional
    public List<PayrollGetDTO> getPayrollsForCurrentUser(String period) {
        String empEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = authService.findUser(empEmail);
        Stream<Payroll> payrolls =
                period == null
                        ? payrollRepository.findAllByEmployeeEmail(empEmail)
                        : payrollRepository.findAllByEmployeeEmailAndPeriod(empEmail, period);
        // Convert each Payroll entity to DTO and update names. Sort in descending order using custom comparator
        return payrolls
                .sorted(payrollPeriodComparator.reversed())
                .map(payroll -> {
                        PayrollGetDTO dto = modelMapper.map(payroll, PayrollGetDTO.class);
                        dto.setName(user.getName());
                        dto.setLastname(user.getLastname());
                        return dto;
                    })
                .toList();
    }
}
