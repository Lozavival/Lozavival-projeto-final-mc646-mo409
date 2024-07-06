package org.isf.medicalstock.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.graphwalker.core.condition.EdgeCoverage;
import org.graphwalker.core.condition.ReachedVertex;
import org.graphwalker.core.condition.TimeDuration;
import org.graphwalker.core.condition.VertexCoverage;
import org.graphwalker.core.generator.AStarPath;
import org.graphwalker.core.generator.RandomPath;
import org.graphwalker.core.machine.ExecutionContext;
import org.graphwalker.core.model.Edge;
import org.graphwalker.core.model.Vertex;
import org.graphwalker.java.test.TestBuilder;
import org.graphwalker.java.test.TestExecutor;
import org.isf.medicals.model.Medical;
import org.isf.medicalstock.model.Lot;
import org.isf.medicalstock.model.Movement;
import org.isf.supplier.model.Supplier;
import org.isf.utils.exception.OHException;
import org.isf.utils.exception.OHServiceException;
import org.isf.ward.model.Ward;
import org.isf.ward.test.TestWardType;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class GraphWalkerTest extends TestsBase implements StockMovement {
    public final static Path MODEL_PATH = Paths.get("org/isf/medicalstock/StockMovement.json");

    private static final int criticalLevel = 7;
    private static int stock;
    private static int inqty;
    private static int outqty;
    private static int opNum;
    private static Medical medical;
    private static Lot lot;
    private static Ward ward;
    private static Supplier supplier;

    @Before
    public void setUp() throws OHException {
        super.setUp();
        medical = _setupTestMedical(true);
        medical.setMinqty(criticalLevel);
        lot = _setupTestLot(medical, currentDate, currentDate, "lotNo", BigDecimal.ONE, Integer.MAX_VALUE);
        ward = _setupTestWard(TestWardType.INTERNAL_MEDICINE);
        supplier = _setupTestSupplier();
        stock = 0;
        inqty = 0;
        outqty = 0;
        opNum = 0;
    }

    private void _charge(int quantity) throws OHServiceException, OHException {
        Movement movement = _setupTestMovement(medical, true, ward, supplier, lot, currentDate, quantity, "refNo" + opNum);
        ArrayList<Movement> movements = new ArrayList<>(1);
        movements.add(movement);

        movStockInsertingManager.newMultipleChargingMovements(movements, "refNo" + opNum++);
    }

    private void _discharge(int quantity) throws OHServiceException, OHException {
        Movement movement = _setupTestMovement(medical, false, ward, supplier, lot, currentDate, quantity, "refNo" + opNum);
        ArrayList<Movement> movements = new ArrayList<>(1);
        movements.add(movement);

        movStockInsertingManager.newMultipleDischargingMovements(movements, "refNo" + opNum++);
    }

    @Override
    public void v_BellowCriticalLevel() {
        assertEquals(inqty, medical.getInqty(), 0.1);
        assertEquals(outqty, medical.getOutqty(), 0.1);
        assertEquals(stock, medical.getTotalQuantity(), 0.1);
        assertTrue(0 < medical.getTotalQuantity() && medical.getTotalQuantity() <= criticalLevel);
    }

    @Override
    public void e_chargeInvalid() {
        int quantity = -1;
        try {
            _charge(quantity);
        } catch (Exception e) {
            System.out.println("e_chargeInvalid: Ocorreu exceção " + e);
        }
    }

    @Override
    public void e_dischargeInvalid() {
        int quantity = stock + 1;
        try {
            _discharge(quantity);
        } catch (Exception e) {
            System.out.println("e_dischargeInvalid: Ocorreu exceção " + e);
        }
    }

    @Override
    public void e_charge() {
        int quantity = 3;
        try {
            _charge(quantity);
            inqty += quantity;
            stock += quantity;
        } catch (Exception e) {
            System.out.println("e_charge: Ocorreu exceção " + e);
        }
    }

    @Override
    public void e_dischargeToEmpty() {
        int quantity = stock;
        try {
            _discharge(quantity);
            outqty += quantity;
            stock -= quantity;
        } catch (Exception e) {
            System.out.println("e_dischargeToEmpty: Ocorreu exceção " + e);
        }
    }

    @Override
    public void v_OutOfStock() {
        assertEquals(inqty, medical.getInqty(), 0.1);
        assertEquals(outqty, medical.getOutqty(), 0.1);
        assertEquals(stock, medical.getTotalQuantity(), 0.1);
        assertEquals(0, medical.getTotalQuantity(), 0.1);
    }

    @Override
    public void e_chargeToAbove() {
        int quantity = 8;
        try {
            _charge(quantity);
            inqty += quantity;
            stock += quantity;
        } catch (Exception e) {
            System.out.println("e_chargeToAbove: Ocorreu exceção " + e);
        }
    }

    @Override
    public void e_discharge() {
        int quantity = 3;
        try {
            _discharge(quantity);
            outqty += quantity;
            stock -= quantity;
        } catch (Exception e) {
            System.out.println("e_discharge: Ocorreu exceção " + e);
        }
    }

    @Override
    public void v_AboveCriticalLevel() {
        assertEquals(inqty, medical.getInqty(), 0.1);
        assertEquals(outqty, medical.getOutqty(), 0.1);
        assertEquals(stock, medical.getTotalQuantity(), 0.1);
        assertTrue(medical.getTotalQuantity() > criticalLevel);
    }

    @Test
    public void testTest() {
        v_OutOfStock();
        e_charge();
        v_BellowCriticalLevel();
        e_charge();
        v_BellowCriticalLevel();
        e_charge();
        v_AboveCriticalLevel();
        e_dischargeToEmpty();
        v_OutOfStock();
    }
    

    // @Test
    // public void runSmokeTest() {
    //     new TestBuilder()
    //             .addContext(new GraphWalkerTest().setNextElement(new Vertex().setName("v_OutOfStock").build()),
    //                     MODEL_PATH,
    //                     // new AStarPath(new ReachedVertex("v_FullQueue")))
    //                     new RandomPath(new VertexCoverage(100)))
    //             .execute();
    // }

    // @Test
    // public void runFunctionalTest() {
    //     new TestBuilder()
    //             .addContext(new GraphWalkerTest().setNextElement(new Vertex().setName("v_OutOfStock").build()),
    //                     MODEL_PATH,
    //                     new RandomPath(new EdgeCoverage(100)))
    //             .execute();
    // }

    // @Test
    // public void runStabilityTest() {
    //     new TestBuilder()
    //             .addContext(new GraphWalkerTest().setNextElement(new Vertex().setName("v_OutOfStock").build()),
    //                     MODEL_PATH,
    //                     new RandomPath(new TimeDuration(120, TimeUnit.SECONDS)))
    //             .execute();
    // }
}
