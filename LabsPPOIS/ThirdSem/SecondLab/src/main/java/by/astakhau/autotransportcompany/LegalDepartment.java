package by.astakhau.autotransportcompany;

import java.util.ArrayList;

public class LegalDepartment extends Department {
    ArrayList<String> claims;

    public LegalDepartment(ArrayList<Employee> employees, ArrayList<String> claims) {
        super(employees);
        this.claims = claims;
    }

    public ArrayList<String> getClaims() {
        return claims;
    }

    public void setClaims(ArrayList<String> claims) {
        this.claims = claims;
    }
}
