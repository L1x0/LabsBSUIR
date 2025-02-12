package by.astakhau.tests;

import by.astakhau.autotransportcompany.FinanceDepartment;
import by.astakhau.autotransportcompany.MarketingDepartment;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class MarketingDepartmentTest {
    @Test
    public void GetterAndSetterTest() {
        MarketingDepartment md = new MarketingDepartment(new ArrayList<>(), new ArrayList<>(), new ArrayList<>());

        md.setPromotions(new ArrayList<>(List.of("1", "2", "3", "4")));
        assertEquals("1", md.getPromotions().get(0));

        md.setAreasOfAnalysis(new ArrayList<>(List.of("physics")));
        assertEquals("physics", md.getAreasOfAnalysis().get(0));
    }

    @Test
    void endMoneyToFinanceDepartmentTest() {
        MarketingDepartment md = new MarketingDepartment(new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
        FinanceDepartment fd = new FinanceDepartment(new ArrayList<>(), 1000);

        md.setFinanceDepartment(fd);

        md.sendMoneyToFinanceDepartment(100);

        assertEquals(1100, fd.getBudget());
    }
}
