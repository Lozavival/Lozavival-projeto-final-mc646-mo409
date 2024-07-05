/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2021 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
 *
 * Open Hospital is a free and open source software for healthcare data management.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * https://www.gnu.org/licenses/gpl-3.0-standalone.html
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.isf.ward.test;

import static org.assertj.core.api.Assertions.assertThat;

import org.isf.utils.exception.OHException;
import org.isf.ward.model.Ward;

public class TestWard {
	// Codes and description for wards
	private String internalMedicineCode = "IM";
	private String maternityCode = "M";
	private String nurseryCode = "N";
	private String surgeryCode = "S";
	private String internalMedicineDescription = "INTERNAL MEDICINE";
	private String maternityDescription = "MATERNITY";
	private String nurseryDescription = "NURSERY";
	private String surgeryDescription = "SURGERY";

	// Default data
	private String code = "Z";
	private String description = "TestDescription";
	private String telephone = "TestTelephone";
	private String fax = "TestFac";
	private String email = "TestEmail@gmail.com";
	private Integer beds = 100;
	private Integer nurs = 101;
	private Integer docs = 102;
	private boolean isPharmacy = true;
	private boolean isFemale = true;
	private boolean isMale = false;

	public Ward setup(TestWardType wardType) throws OHException {
		Ward ward;

		switch (wardType) {
			case INTERNAL_MEDICINE:
				ward = new Ward(internalMedicineCode, internalMedicineDescription, telephone, fax, email, beds, nurs, docs, isMale, isFemale);
				break;
			case MATERNITY:
				ward = new Ward(maternityCode, maternityDescription, telephone, fax, email, beds, nurs, docs, isMale, isFemale);
				break;
			case NURSERY:
				ward = new Ward(nurseryCode, nurseryDescription, telephone, fax, email, beds, nurs, docs, isMale, isFemale);
				break;
			case SURGERY:
				ward = new Ward(surgeryCode, surgeryDescription, telephone, fax, email, beds, nurs, docs, isMale, isFemale);
				break;
			default:
				throw new OHException("Invalid ward type: " + wardType);
		}
		return ward;
	}

	public void _setParameters(Ward ward, boolean maternity) {
		ward.setCode(code);
		if (maternity) {
			ward.setCode(maternityCode);
		}
		ward.setBeds(beds);
		ward.setDescription(description);
		ward.setDocs(docs);
		ward.setEmail(email);
		ward.setFax(fax);
		ward.setFemale(isFemale);
		ward.setMale(isMale);
		ward.setNurs(nurs);
		ward.setPharmacy(isPharmacy);
		ward.setTelephone(telephone);
	}

	public void check(Ward ward, TestWardType wardType) {
		if (wardType == TestWardType.INTERNAL_MEDICINE) {
			assertThat(ward.getCode()).isEqualTo(internalMedicineCode);
			assertThat(ward.getDescription()).isEqualTo(internalMedicineDescription);
		} else if (wardType == TestWardType.MATERNITY) {
			assertThat(ward.getCode()).isEqualTo(maternityCode);
			assertThat(ward.getDescription()).isEqualTo(maternityDescription);
		} else if (wardType == TestWardType.NURSERY) {
			assertThat(ward.getCode()).isEqualTo(nurseryCode);
			assertThat(ward.getDescription()).isEqualTo(nurseryDescription);
		} else if (wardType == TestWardType.SURGERY) {
			assertThat(ward.getCode()).isEqualTo(surgeryCode);
			assertThat(ward.getDescription()).isEqualTo(surgeryDescription);
		}
		assertThat(ward.getBeds()).isEqualTo(beds);
		assertThat(ward.getDocs()).isEqualTo(docs);
		assertThat(ward.getEmail()).isEqualTo(email);
		assertThat(ward.getFax()).isEqualTo(fax);
		assertThat(ward.isFemale()).isEqualTo(isFemale);
		assertThat(ward.isMale()).isEqualTo(isMale);
		assertThat(ward.getNurs()).isEqualTo(nurs);
		assertThat(ward.isPharmacy()).isEqualTo(isPharmacy);
		assertThat(ward.getTelephone()).isEqualTo(telephone);
	}
}
