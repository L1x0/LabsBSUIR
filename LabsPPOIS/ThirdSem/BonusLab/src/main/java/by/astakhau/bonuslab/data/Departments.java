package by.astakhau.bonuslab.data;

public enum Departments {
    MarketingDepartment,
    LogisticsDepartment,
    FinanceDepartment,
    TechnicalDepartment;

    public String toStringValue() {
        return this.name();
    }
}
