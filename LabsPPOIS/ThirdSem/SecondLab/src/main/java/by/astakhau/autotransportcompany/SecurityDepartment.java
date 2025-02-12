package by.astakhau.autotransportcompany;

import java.util.ArrayList;

public class SecurityDepartment extends Department {
    private ArrayList<String> logOfReflectedAttacks;
    private ArrayList<String> instruments;

    public SecurityDepartment(ArrayList<Employee> employees, ArrayList<String> logOfReflectedAttacks, ArrayList<String> instruments) {
        super(employees);
        this.logOfReflectedAttacks = logOfReflectedAttacks;
        this.instruments = instruments;
    }

    public ArrayList<String> getLogOfReflectedAttacks() {
        return logOfReflectedAttacks;
    }

    public void setLogOfReflectedAttacks(ArrayList<String> logOfReflectedAttacks) {
        this.logOfReflectedAttacks = logOfReflectedAttacks;
    }

    public ArrayList<String> getInstruments() {
        return instruments;
    }

    public void setInstruments(ArrayList<String> instruments) {
        this.instruments = instruments;
    }
}
