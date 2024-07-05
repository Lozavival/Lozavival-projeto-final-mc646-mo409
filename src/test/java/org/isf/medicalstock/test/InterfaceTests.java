package org.isf.medicalstock.test;

import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.GregorianCalendar;

import org.isf.medicals.model.Medical;
import org.isf.medicalstock.model.Lot;
import org.isf.medicalstock.model.Movement;
import org.isf.supplier.model.Supplier;
import org.isf.utils.exception.OHServiceException;
import org.isf.ward.model.Ward;
import org.isf.ward.test.TestWardType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class InterfaceTests extends TestsBase {

    @Parameter(0)
    public boolean medicalOutOfStock;

    @Parameter(1)
    public boolean medicalInDb;

    @Parameter(2)
    public boolean useMedical;

    @Parameter(3)
    public GregorianCalendar lotPrepDate;

    @Parameter(4)
    public GregorianCalendar lotExpDate;

    @Parameter(5)
    public String lotNo;

    @Parameter(6)
    public BigDecimal lotUnitCost;

    @Parameter(7)
    public int lotQuantity;

    @Parameter(8)
    public boolean lotMedicalEqualsMedical;

    @Parameter(9)
    public boolean useLot;

    @Parameter(10)
    public boolean movementType;

    @Parameter(11)
    public TestWardType wardType;

    @Parameter(12)
    public boolean useWard;

    @Parameter(13)
    public boolean useSupplier;

    @Parameter(14)
    public GregorianCalendar movementDate;

    @Parameter(15)
    public GregorianCalendar lastMovementDate;

    @Parameter(16)
    public int quantity;

    @Parameter(17)
    public String refNo;

    @Parameter(18)
    public boolean validTestCase;

    /* 
    * Args order:
    *      - (boolean) medical out of stock
    *      - (boolean) medical in db
    *      - (boolean) usar medical (true) ou null (false)
    *      - (GregorianCalendar) lot prep date
    *      - (GregorianCalendar) lot exp date
    *      - (String) lot no
    *      - (BigDecimal) lot unit cost
    *      - (int) lot quantity
    *      - (boolean) lot medical igual ao medical
    *      - (boolean) usar lot (true) ou null (false)
    *      - (boolean) movement type: true = charging, false = discharging
    *      - (TestWardType) wardType
    *      - (boolean) usar ward (true) ou null (false)
    *      - (boolean) usar supplier (true) ou null (false)
    *      - (GregorianCalendar) movement date
    *      - (GregorianCalendar) last movement date
    *      - (int) quantity
    *      - (String) ref no
    *      - (boolean) valid test case
    */
    @Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
            {false, true, true, currentDate, currentDate, "a", new BigDecimal(0.1), 1, true, true, true, TestWardType.INTERNAL_MEDICINE, true, true, currentDate, currentDate, 1, "refNo", true},
            {false, true, true, currentDate, currentDate, "a", new BigDecimal(0.1), 1, false, true, true, TestWardType.INTERNAL_MEDICINE, true, true, currentDate, currentDate, 1, "refNo", false},
        });
	}

    @Test
    public void movementTest() throws Exception {
        Medical medical = null;
        if (useMedical)
		    medical = _setupTestMedical(medicalOutOfStock);
        
        Lot lot = null;
        if (useLot && lotMedicalEqualsMedical)
		    lot = _setupTestLot(medical, lotPrepDate, lotExpDate, lotNo, lotUnitCost, lotQuantity);
        else if (useLot) {
            Medical medical2 = _setupTestMedical(medicalOutOfStock);
            lot = _setupTestLot(medical2, lotPrepDate, lotExpDate, lotNo, lotUnitCost, lotQuantity);
        }

        Ward ward = null;
        if (useWard)
            ward = _setupTestWard(wardType);
        Supplier supplier = null;
        if (useSupplier)
            supplier = _setupTestSupplier();
        
        Movement lastMovement = _setupTestMovement(medical, movementType, ward, supplier, lot, lastMovementDate, quantity, refNo);
		Movement movement = _setupTestMovement(medical, movementType, ward, supplier, lot, movementDate, quantity, refNo);
		ArrayList<Movement> movements = new ArrayList<>(2);
		movements.add(lastMovement);
		movements.add(movement);

        if (useMedical && !medicalInDb) {
            medicalsIoOperationRepository.delete(medical.getCode());
            medicalsIoOperationRepository.flush();
        }

        if (movementType) { // charging
            if (validTestCase) // valid test case, should return true
                assertTrue(movStockInsertingManager.newMultipleChargingMovements(movements, refNo));
            else // invalid test case, should throw OHServiceException
                assertThrows(OHServiceException.class, () -> movStockInsertingManager.newMultipleChargingMovements(movements, refNo));
        } else { // discharging
            if (validTestCase)
                assertTrue(movStockInsertingManager.newMultipleDischargingMovements(movements, refNo));
            else
                assertThrows(OHServiceException.class, () -> movStockInsertingManager.newMultipleDischargingMovements(movements, refNo));
        }
    }
}
